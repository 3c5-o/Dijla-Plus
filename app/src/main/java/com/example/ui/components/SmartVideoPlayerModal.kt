package com.example.ui.components

import android.content.Context
import android.media.AudioManager
import android.media.audiofx.LoudnessEnhancer
import android.net.Uri
import android.view.TextureView
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.extractor.ts.DefaultTsPayloadReaderFactory
import androidx.media3.extractor.ts.TsExtractor
import com.example.model.*
import com.example.network.LocalStreamProxyServer
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import java.util.Locale
import java.util.concurrent.TimeUnit

private enum class AspectRatioFit(val labelAr: String) {
    FIT("تلقائي"),
    FILL("ملء الشاشة"),
    SIXTEEN_NINE("16:9 سينمائي")
}

private enum class MultiScreenMode(val count: Int, val labelAr: String) {
    SINGLE(1, "شاشة كاملة"),
    DUAL(2, "شاشتان 1x2"),
    QUAD(4, "4 شاشات 2x2")
}

private data class StreamCandidate(
    val label: String,
    val url: String,
    val forceMimeType: String?
)

private data class AudioTrackOption(
    val groupIndex: Int,
    val trackIndex: Int,
    val label: String
)

@OptIn(UnstableApi::class)
@Composable
fun SmartVideoPlayerModal(
    media: ActivePlayerMedia,
    allChannels: List<LiveChannel>,
    pingLatencyMs: Int = 24,
    savedPositionMs: Long = 0L,
    onSavePlaybackPosition: (String, Long) -> Unit = { _, _ -> },
    onClearPlaybackPosition: (String) -> Unit = {},
    onSelectChannel: (LiveChannel) -> Unit,
    onPlayNextEpisode: ((VodSeries, SeriesSeason, SeriesEpisode) -> Unit)? = null,
    onServerFailover: () -> Unit = {},
    onClose: () -> Unit
) {
    val context = LocalContext.current

    var currentMedia by remember(media) { mutableStateOf(media) }
    val isLiveStream = currentMedia is ActivePlayerMedia.ChannelMedia || currentMedia is ActivePlayerMedia.MatchMedia

    val mediaKey = remember(currentMedia) {
        when (val m = currentMedia) {
            is ActivePlayerMedia.MovieMedia -> m.movie.id
            is ActivePlayerMedia.EpisodeMedia -> m.episode.id
            is ActivePlayerMedia.ChannelMedia -> m.channel.id
            is ActivePlayerMedia.MatchMedia -> m.match.id
        }
    }

    val mediaTitle = when (val m = currentMedia) {
        is ActivePlayerMedia.ChannelMedia -> m.channel.name
        is ActivePlayerMedia.MatchMedia -> "${m.match.homeTeam} (${m.match.linkedChannelName})"
        is ActivePlayerMedia.MovieMedia -> "${m.movie.title} (${m.movie.year})"
        is ActivePlayerMedia.EpisodeMedia -> "${m.series.title} - ${m.season.title} - الحلقة ${m.episode.episodeNumber}"
    }

    val mediaSubtitle = when (val m = currentMedia) {
        is ActivePlayerMedia.ChannelMedia -> "#${m.channel.channelNumber} • ${m.channel.category} • ${m.channel.qualityBadge}"
        is ActivePlayerMedia.MatchMedia -> "${m.match.tournament} • ${m.match.commentator}"
        is ActivePlayerMedia.MovieMedia -> "${m.movie.category} • ${m.movie.qualityBadge}"
        is ActivePlayerMedia.EpisodeMedia -> m.episode.title
    }

    // Next Episode Resolver for Series
    val nextEpisodeTriple: Triple<VodSeries, SeriesSeason, SeriesEpisode>? = remember(currentMedia) {
        val epMedia = currentMedia as? ActivePlayerMedia.EpisodeMedia ?: return@remember null
        val eps = epMedia.season.episodes
        val idx = eps.indexOfFirst { it.id == epMedia.episode.id }
        if (idx >= 0 && idx + 1 < eps.size) {
            Triple(epMedia.series, epMedia.season, eps[idx + 1])
        } else null
    }

    val rawSources: List<StreamSource> = remember(currentMedia) {
        when (val m = currentMedia) {
            is ActivePlayerMedia.ChannelMedia -> m.channel.streamSources
            is ActivePlayerMedia.MatchMedia -> {
                val chSources = m.channel?.streamSources.orEmpty()
                if (chSources.isNotEmpty()) chSources
                else listOf(
                    StreamSource(
                        id = "match_primary",
                        label = "سيرفر دجلة بلس الرياضي VIP",
                        url = m.match.directStreamUrl,
                        quality = "FHD"
                    )
                )
            }
            is ActivePlayerMedia.MovieMedia -> buildVodFallbackSources(m.movie.streamUrl, "سيرفر دجلة بلس VOD")
            is ActivePlayerMedia.EpisodeMedia -> buildVodFallbackSources(m.episode.streamUrl, "سيرفر دجلة بلس الحلقات")
        }
    }

    // Build ordered StreamCandidates: Direct MP4/HLS/MPEG-TS -> Format Fallbacks -> LocalStreamProxyServer
    val candidates: List<StreamCandidate> = remember(rawSources, isLiveStream) {
        buildStreamCandidates(rawSources, isLiveStream)
    }

    var selectedCandidateIndex by remember(candidates) { mutableIntStateOf(0) }
    val currentCandidate = candidates.getOrNull(selectedCandidateIndex)

    var showControls by remember { mutableStateOf(true) }
    var showChannelDrawer by remember { mutableStateOf(false) }
    var showNumberPadDrawer by remember { mutableStateOf(false) }
    var numberPadDigits by remember { mutableStateOf("") }
    var isPlaying by remember { mutableStateOf(true) }
    var isBuffering by remember { mutableStateOf(true) }
    var isMuted by remember { mutableStateOf(false) }
    var isAudioBoostEnabled by remember { mutableStateOf(true) }
    var audioStatusToast by remember { mutableStateOf("") }
    var audioTracks by remember { mutableStateOf<List<AudioTrackOption>>(emptyList()) }
    var selectedAudioTrackIndex by remember { mutableIntStateOf(0) }
    var hasStreamError by remember { mutableStateOf(false) }
    var errorDetail by remember { mutableStateOf("") }
    var aspectFit by remember { mutableStateOf(AspectRatioFit.FIT) }
    var videoResolutionText by remember { mutableStateOf("FHD 1080p") }

    // Multi-Screen 2x2 State for Live TV
    var multiScreenMode by remember { mutableStateOf(MultiScreenMode.SINGLE) }
    var activeMultiCellIndex by remember { mutableIntStateOf(0) }
    val multiScreenChannels = remember(allChannels, currentMedia) {
        val primaryCh = (currentMedia as? ActivePlayerMedia.ChannelMedia)?.channel
            ?: (currentMedia as? ActivePlayerMedia.MatchMedia)?.channel
            ?: allChannels.firstOrNull()
        val others = allChannels.filter { it.id != primaryCh?.id }.take(3)
        listOfNotNull(primaryCh) + others
    }
    var quadCells by remember(multiScreenChannels) {
        mutableStateOf(multiScreenChannels.take(4))
    }

    // VOD Progress & Auto-Resume State
    var currentPosMs by remember(currentMedia) { mutableLongStateOf(0L) }
    var durationMs by remember(currentMedia) { mutableLongStateOf(0L) }
    var showResumeBanner by remember(currentMedia, savedPositionMs) {
        mutableStateOf(!isLiveStream && savedPositionMs > 5000L)
    }

    var retryNonce by remember { mutableIntStateOf(0) }
    var loudnessEnhancer by remember { mutableStateOf<LoudnessEnhancer?>(null) }

    // Helper function to unlock system + player audio and apply LoudnessEnhancer gain boost
    fun forceActivateAndBoostAudio(player: ExoPlayer, boost: Boolean) {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            if (audioManager != null) {
                @Suppress("DEPRECATION")
                audioManager.requestAudioFocus(
                    null,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN
                )
                val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val curVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                if (curVol == 0 && maxVol > 0) {
                    audioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        (maxVol * 0.75f).toInt().coerceAtLeast(1),
                        0
                    )
                }
            }
        } catch (_: Exception) {
        }

        isMuted = false
        player.volume = 1.0f

        // Enable audio track renderer and prefer Arabic/English/default audio tracks
        try {
            player.trackSelectionParameters = player.trackSelectionParameters
                .buildUpon()
                .setTrackTypeDisabled(C.TRACK_TYPE_AUDIO, false)
                .setPreferredAudioLanguages("ar", "ara", "en", "eng")
                .build()
        } catch (_: Exception) {
        }

        // Attach LoudnessEnhancer for MPEG-TS / AC3 / AAC low-volume streams
        try {
            val sessionId = player.audioSessionId
            if (sessionId != C.AUDIO_SESSION_ID_UNSET && sessionId > 0) {
                if (loudnessEnhancer == null) {
                    loudnessEnhancer = LoudnessEnhancer(sessionId)
                }
                loudnessEnhancer?.setTargetGain(if (boost) 700 else 0)
                loudnessEnhancer?.enabled = boost
            }
        } catch (_: Exception) {
        }
    }

    // Anti-Freeze Smart Buffer + OkHttpDataSource + Tolerant TsExtractor
    val exoPlayer = remember {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .build()

        val okHttpFactory = OkHttpDataSource.Factory(okHttpClient)
            .setUserAgent("IBOProPlayer/3.9 (Android; Linux)")
            .setDefaultRequestProperties(
                mapOf(
                    "Accept" to "*/*",
                    "Connection" to "keep-alive"
                )
            )

        val dataSourceFactory = DefaultDataSource.Factory(context, okHttpFactory)

        val extractorsFactory = DefaultExtractorsFactory()
            .setTsExtractorFlags(
                DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES or
                    DefaultTsPayloadReaderFactory.FLAG_DETECT_ACCESS_UNITS
            )
            .setTsExtractorTimestampSearchBytes(1500 * TsExtractor.TS_PACKET_SIZE)

        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory, extractorsFactory)

        val renderersFactory = DefaultRenderersFactory(context)
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
            .setEnableDecoderFallback(true)

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                /* minBufferMs = */ 10_000,
                /* maxBufferMs = */ 45_000,
                /* bufferForPlaybackMs = */ 1_500,
                /* bufferForPlaybackAfterRebufferMs = */ 3_000
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        ExoPlayer.Builder(context, renderersFactory)
            .setMediaSourceFactory(mediaSourceFactory)
            .setLoadControl(loadControl)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                    .build(),
                true
            )
            .setHandleAudioBecomingNoisy(true)
            .build()
            .apply {
                volume = 1.0f
                playWhenReady = true
            }
    }

    // Attach listener for buffering, resolution, audio track discovery, and automatic failover
    DisposableEffect(exoPlayer, candidates) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        isBuffering = true
                    }
                    Player.STATE_READY -> {
                        isBuffering = false
                        hasStreamError = false
                        durationMs = exoPlayer.duration.coerceAtLeast(0L)
                        forceActivateAndBoostAudio(exoPlayer, isAudioBoostEnabled)
                    }
                    Player.STATE_ENDED -> {
                        isBuffering = false
                        if (isLiveStream) {
                            exoPlayer.seekToDefaultPosition()
                            exoPlayer.prepare()
                        } else if (nextEpisodeTriple != null && onPlayNextEpisode != null) {
                            onPlayNextEpisode(
                                nextEpisodeTriple.first,
                                nextEpisodeTriple.second,
                                nextEpisodeTriple.third
                            )
                        }
                    }
                    Player.STATE_IDLE -> Unit
                }
            }

            override fun onAudioSessionIdChanged(audioSessionId: Int) {
                try {
                    loudnessEnhancer?.release()
                    loudnessEnhancer = null
                    if (audioSessionId != C.AUDIO_SESSION_ID_UNSET && audioSessionId > 0) {
                        loudnessEnhancer = LoudnessEnhancer(audioSessionId).apply {
                            setTargetGain(if (isAudioBoostEnabled) 700 else 0)
                            enabled = isAudioBoostEnabled
                        }
                    }
                } catch (_: Exception) {
                }
            }

            override fun onTracksChanged(tracks: Tracks) {
                val discovered = mutableListOf<AudioTrackOption>()
                tracks.groups.forEachIndexed { groupIdx, group ->
                    if (group.type == C.TRACK_TYPE_AUDIO) {
                        for (trackIdx in 0 until group.length) {
                            val format = group.getTrackFormat(trackIdx)
                            val lang = format.language?.uppercase(Locale.US) ?: "AR/DEFAULT"
                            val label = format.label ?: "مسار صوتي ${discovered.size + 1} ($lang)"
                            discovered.add(
                                AudioTrackOption(
                                    groupIndex = groupIdx,
                                    trackIndex = trackIdx,
                                    label = label
                                )
                            )
                        }
                    }
                }
                audioTracks = discovered
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
                if (playing) {
                    isBuffering = false
                    hasStreamError = false
                }
            }

            override fun onVideoSizeChanged(videoSize: VideoSize) {
                if (videoSize.width > 0 && videoSize.height > 0) {
                    videoResolutionText = "${videoSize.width}x${videoSize.height}"
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                if (selectedCandidateIndex < candidates.lastIndex) {
                    selectedCandidateIndex += 1
                } else {
                    isBuffering = false
                    hasStreamError = true
                    errorDetail = "تعذر تشغيل البث من المسار الحالي، يرجى الضغط على إعادة المحاولة أو التبديل للمسار البديل"
                }
            }
        }

        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    // Release ExoPlayer and LoudnessEnhancer cleanly when modal closes
    DisposableEffect(Unit) {
        onDispose {
            try {
                loudnessEnhancer?.release()
                loudnessEnhancer = null
            } catch (_: Exception) {
            }
            try {
                exoPlayer.stop()
                exoPlayer.clearVideoSurface()
                exoPlayer.release()
            } catch (_: Exception) {
            }
        }
    }

    // Clear audio status toast after 2.5 seconds
    LaunchedEffect(audioStatusToast) {
        if (audioStatusToast.isNotBlank()) {
            delay(2500L)
            audioStatusToast = ""
        }
    }

    // Load or switch stream candidate when candidate or retryNonce changes
    LaunchedEffect(currentCandidate, retryNonce) {
        val candidate = currentCandidate ?: return@LaunchedEffect
        if (candidate.url.isBlank()) {
            isBuffering = false
            hasStreamError = true
            errorDetail = "رابط البث غير متوفر حالياً"
            return@LaunchedEffect
        }

        isBuffering = true
        hasStreamError = false
        errorDetail = ""

        forceActivateAndBoostAudio(exoPlayer, isAudioBoostEnabled)

        val mediaItemBuilder = MediaItem.Builder()
            .setUri(Uri.parse(candidate.url))

        if (candidate.forceMimeType != null) {
            mediaItemBuilder.setMimeType(candidate.forceMimeType)
        }

        exoPlayer.stop()
        exoPlayer.setMediaItem(mediaItemBuilder.build())
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    // Fast automatic fallback watchdog: 4.5s for VOD/Episodes (.mp4 -> .m3u8 -> .ts), 12s for Live TV
    LaunchedEffect(currentCandidate, isBuffering, retryNonce, isLiveStream) {
        if (isBuffering && currentCandidate != null) {
            val timeoutMs = if (isLiveStream) 12_000L else 4_500L
            delay(timeoutMs)
            if (isBuffering && !exoPlayer.isPlaying) {
                if (selectedCandidateIndex < candidates.lastIndex) {
                    selectedCandidateIndex += 1
                } else {
                    hasStreamError = true
                    isBuffering = false
                    errorDetail = "انتهت مهلة الاستجابة من السيرفر، جرب المسار البديل"
                }
            }
        }
    }

    // Poll VOD progress & auto-save every 5 seconds for Resume Playback
    LaunchedEffect(exoPlayer, isLiveStream, mediaKey) {
        var saveTick = 0
        while (!isLiveStream) {
            currentPosMs = exoPlayer.currentPosition.coerceAtLeast(0L)
            val d = exoPlayer.duration
            if (d > 0L) durationMs = d
            saveTick++
            if (saveTick >= 10 && currentPosMs > 5000L) {
                saveTick = 0
                onSavePlaybackPosition(mediaKey, currentPosMs)
            }
            delay(500L)
        }
    }

    // Auto-hide OSD controls after 5 seconds when playing
    LaunchedEffect(showControls, isPlaying, showChannelDrawer, showNumberPadDrawer, hasStreamError) {
        if (showControls && isPlaying && !showChannelDrawer && !showNumberPadDrawer && !hasStreamError) {
            delay(5000L)
            showControls = false
        }
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    showControls = !showControls
                }
        ) {
            // =====================================================================
            // SINGLE SCREEN OR MULTI-SCREEN (1x2 / 2x2) VIEWPORT
            // =====================================================================
            if (!isLiveStream || multiScreenMode == MultiScreenMode.SINGLE) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val modifierForAspect = when (aspectFit) {
                        AspectRatioFit.FIT -> Modifier.fillMaxSize()
                        AspectRatioFit.FILL -> Modifier.fillMaxSize()
                        AspectRatioFit.SIXTEEN_NINE -> Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                    }

                    AndroidView(
                        factory = { ctx ->
                            TextureView(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                keepScreenOn = true
                                exoPlayer.setVideoTextureView(this)
                            }
                        },
                        update = { textureView ->
                            exoPlayer.setVideoTextureView(textureView)
                        },
                        modifier = modifierForAspect.testTag("exoplayer_texture_view")
                    )
                }
            } else {
                // Multi-Screen (1x2 or 2x2) Live Sports Split View
                val visibleCells = if (multiScreenMode == MultiScreenMode.DUAL) {
                    quadCells.take(2)
                } else {
                    quadCells.take(4)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 56.dp, bottom = 56.dp, start = 8.dp, end = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val rows = visibleCells.chunked(2)
                    rows.forEachIndexed { rowIdx, rowChannels ->
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowChannels.forEachIndexed { colIdx, cellChannel ->
                                val cellIndex = rowIdx * 2 + colIdx
                                val isActiveCell = cellIndex == activeMultiCellIndex

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(DijlahSurfaceCard)
                                        .border(
                                            width = if (isActiveCell) 2.dp else 1.dp,
                                            color = if (isActiveCell) DijlahGold else Color.White.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            activeMultiCellIndex = cellIndex
                                            currentMedia = ActivePlayerMedia.ChannelMedia(cellChannel)
                                            onSelectChannel(cellChannel)
                                        }
                                ) {
                                    if (isActiveCell) {
                                        AndroidView(
                                            factory = { ctx ->
                                                TextureView(ctx).apply {
                                                    layoutParams = ViewGroup.LayoutParams(
                                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                                        ViewGroup.LayoutParams.MATCH_PARENT
                                                    )
                                                    keepScreenOn = true
                                                    exoPlayer.setVideoTextureView(this)
                                                }
                                            },
                                            update = { textureView ->
                                                exoPlayer.setVideoTextureView(textureView)
                                            },
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Brush.verticalGradient(
                                                        listOf(DijlahSurfaceCard, DijlahBgDark)
                                                    )
                                                )
                                                .padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LiveTv,
                                                contentDescription = null,
                                                tint = DijlahGold,
                                                modifier = Modifier.size(32.dp)
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = cellChannel.name,
                                                color = DijlahTextPrimary,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 13.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "اضغط لتفعيل البث والصوت في هذه الشاشة",
                                                color = DijlahCyan,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .fillMaxWidth()
                                            .background(Color.Black.copy(alpha = 0.6f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${cellIndex + 1}. ${cellChannel.name}",
                                            color = if (isActiveCell) DijlahGold else DijlahTextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isActiveCell) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                                                contentDescription = null,
                                                tint = if (isActiveCell) DijlahEmerald else DijlahTextSecondary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = if (isActiveCell) "نشط" else "استعداد",
                                                color = if (isActiveCell) DijlahEmerald else DijlahTextSecondary,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Audio Status Feedback Banner
            if (audioStatusToast.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DijlahEmerald.copy(alpha = 0.95f),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 66.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null,
                            tint = Color(0xFF050B14),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = audioStatusToast,
                            color = Color(0xFF050B14),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // =====================================================================
            // AUTO-RESUME PROMPT BANNER (استكمال المشاهدة من حيث توقفت)
            // =====================================================================
            if (showResumeBanner && savedPositionMs > 5000L) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DijlahSurfaceCard.copy(alpha = 0.95f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, DijlahGold),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 68.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = DijlahGold
                        )
                        Text(
                            text = "هل تريد استكمال المشاهدة من ${formatDurationMs(savedPositionMs)}؟",
                            color = DijlahTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Button(
                            onClick = {
                                exoPlayer.seekTo(savedPositionMs)
                                showResumeBanner = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DijlahGold,
                                contentColor = Color(0xFF050B14)
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("استكمال المشاهدة", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = {
                                onClearPlaybackPosition(mediaKey)
                                showResumeBanner = false
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("من البداية", color = DijlahTextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }

            // =====================================================================
            // AUTO-NEXT EPISODE PROMPT (during last 20 seconds of a series episode)
            // =====================================================================
            val remainingMs = durationMs - currentPosMs
            if (!isLiveStream && nextEpisodeTriple != null && onPlayNextEpisode != null &&
                durationMs > 30_000L && remainingMs in 1_000L..20_000L
            ) {
                Surface(
                    onClick = {
                        onPlayNextEpisode(
                            nextEpisodeTriple.first,
                            nextEpisodeTriple.second,
                            nextEpisodeTriple.third
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    color = DijlahGold,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 24.dp, bottom = 92.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = null,
                            tint = Color(0xFF050B14)
                        )
                        Text(
                            text = "تشغيل الحلقة ${nextEpisodeTriple.third.episodeNumber} التالية (${remainingMs / 1000}s)",
                            color = Color(0xFF050B14),
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Buffering Indicator
            if (isBuffering && !hasStreamError) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xD9050B14),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.55f)),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(
                            color = DijlahGold,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "جاري الاتصال بسيرفر دجلة بلس VIP...",
                            color = DijlahTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "${currentCandidate?.label ?: "سيرفر دجلة بلس"} • Anti-Freeze Buffer",
                            color = DijlahGold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Error Recovery Overlay (100% Server Stealth — Zero exposed URLs)
            if (hasStreamError) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = DijlahBgSecondary.copy(alpha = 0.96f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DijlahCrimson.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .widthIn(max = 460.dp)
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WifiTetheringError,
                            contentDescription = null,
                            tint = DijlahGold,
                            modifier = Modifier.size(38.dp)
                        )
                        Text(
                            text = "تعذر تشغيل هذا المسار حالياً",
                            color = DijlahTextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = errorDetail.ifBlank { "يرجى تجربة المسار البديل أو إعادة المحاولة" },
                            color = DijlahTextSecondary,
                            fontSize = 12.sp
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    selectedCandidateIndex = 0
                                    retryNonce += 1
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DijlahGold,
                                    contentColor = Color(0xFF050B14)
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("إعادة المحاولة", fontWeight = FontWeight.ExtraBold)
                            }
                            if (candidates.size > 1) {
                                OutlinedButton(
                                    onClick = {
                                        selectedCandidateIndex = (selectedCandidateIndex + 1) % candidates.size
                                        retryNonce += 1
                                    },
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DijlahCyan),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("المسار البديل", color = DijlahCyan, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // OSD Overlay (Top Bar & Bottom Bar)
            if (showControls) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Top OSD Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.88f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            IconButton(
                                onClick = onClose,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.14f))
                                    .testTag("close_player_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "إغلاق المشغل",
                                    tint = Color.White
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = mediaTitle,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = mediaSubtitle,
                                    color = DijlahGold,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Top-Left OSD Tools
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Live Resolution + Ping Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isLiveStream) DijlahCyan.copy(alpha = 0.2f)
                                        else DijlahGold.copy(alpha = 0.2f)
                                    )
                                    .border(
                                        1.dp,
                                        if (isLiveStream) DijlahCyan else DijlahGold,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isLiveStream) "LIVE | $videoResolutionText | ${pingLatencyMs}ms"
                                    else "VOD | $videoResolutionText",
                                    color = if (isLiveStream) DijlahCyan else DijlahGold,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp
                                )
                            }

                            // Multi-Screen 2x2 Toggle (Live TV only)
                            if (isLiveStream && allChannels.size >= 2) {
                                OutlinedButton(
                                    onClick = {
                                        multiScreenMode = when (multiScreenMode) {
                                            MultiScreenMode.SINGLE -> MultiScreenMode.DUAL
                                            MultiScreenMode.DUAL -> MultiScreenMode.QUAD
                                            MultiScreenMode.QUAD -> MultiScreenMode.SINGLE
                                        }
                                    },
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (multiScreenMode != MultiScreenMode.SINGLE) DijlahGold else Color.White.copy(alpha = 0.35f)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .height(34.dp)
                                        .testTag("multiscreen_toggle_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.GridView,
                                        contentDescription = "شاشة متعددة",
                                        tint = DijlahGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = multiScreenMode.labelAr,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Digital Channel Number Pad Button (Live TV only)
                            if (isLiveStream) {
                                OutlinedButton(
                                    onClick = {
                                        showNumberPadDrawer = !showNumberPadDrawer
                                        showChannelDrawer = false
                                    },
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Dialpad,
                                        contentDescription = "طلب رقم القناة",
                                        tint = DijlahGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "أرقام",
                                        color = Color.White,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            // Aspect Ratio Switcher
                            OutlinedButton(
                                onClick = {
                                    aspectFit = when (aspectFit) {
                                        AspectRatioFit.FIT -> AspectRatioFit.FILL
                                        AspectRatioFit.FILL -> AspectRatioFit.SIXTEEN_NINE
                                        AspectRatioFit.SIXTEEN_NINE -> AspectRatioFit.FIT
                                    }
                                },
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AspectRatio,
                                    contentDescription = "نسبة العرض",
                                    tint = DijlahGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = aspectFit.labelAr,
                                    color = Color.White,
                                    fontSize = 10.sp
                                )
                            }

                            // Quick Channel List Drawer Button
                            if (allChannels.isNotEmpty()) {
                                Button(
                                    onClick = {
                                        showChannelDrawer = !showChannelDrawer
                                        showNumberPadDrawer = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DijlahGold,
                                        contentColor = Color(0xFF050B14)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .height(34.dp)
                                        .testTag("toggle_channel_drawer_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("القنوات", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Bottom OSD Controls & Stream Switcher
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.92f)
                                    )
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // VOD Seekbar for Movies & Series
                        if (!isLiveStream && durationMs > 0L) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = formatDurationMs(currentPosMs),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Slider(
                                    value = currentPosMs.toFloat().coerceIn(0f, durationMs.toFloat()),
                                    onValueChange = { newPos ->
                                        currentPosMs = newPos.toLong()
                                        exoPlayer.seekTo(currentPosMs)
                                    },
                                    valueRange = 0f..durationMs.toFloat().coerceAtLeast(1f),
                                    colors = SliderDefaults.colors(
                                        thumbColor = DijlahGold,
                                        activeTrackColor = DijlahGold,
                                        inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = formatDurationMs(durationMs),
                                    color = DijlahTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Playback Transport + Guaranteed Audio Boost Controls
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        if (exoPlayer.isPlaying) {
                                            exoPlayer.pause()
                                        } else {
                                            forceActivateAndBoostAudio(exoPlayer, isAudioBoostEnabled)
                                            exoPlayer.play()
                                        }
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(DijlahGold)
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "تشغيل/إيقاف",
                                        tint = Color(0xFF050B14)
                                    )
                                }

                                if (!isLiveStream) {
                                    IconButton(
                                        onClick = {
                                            exoPlayer.seekTo((exoPlayer.currentPosition - 10_000L).coerceAtLeast(0L))
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Replay10,
                                            contentDescription = "رجوع 10 ثواني",
                                            tint = Color.White
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            exoPlayer.seekTo((exoPlayer.currentPosition + 10_000L).coerceAtMost(durationMs))
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Forward10,
                                            contentDescription = "تقديم 10 ثواني",
                                            tint = Color.White
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        if (isMuted) {
                                            forceActivateAndBoostAudio(exoPlayer, isAudioBoostEnabled)
                                            audioStatusToast = "تم تشغيل وتنشيط الصوت 100%"
                                        } else {
                                            isMuted = true
                                            exoPlayer.volume = 0f
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "الصوت",
                                        tint = if (isMuted) DijlahCrimson else DijlahEmerald
                                    )
                                }

                                // Direct Audio Unlocker & Gain Booster Button (تنشيط / رفع الصوت)
                                Button(
                                    onClick = {
                                        isAudioBoostEnabled = true
                                        forceActivateAndBoostAudio(exoPlayer, true)
                                        audioStatusToast = "تم تنشيط ومضاعفة مستوى الصوت (Audio Boost)"
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DijlahEmerald.copy(alpha = 0.25f),
                                        contentColor = DijlahEmerald
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DijlahEmerald),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .height(34.dp)
                                        .testTag("activate_audio_boost_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "تنشيط / رفع الصوت",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                // Audio Track Switcher (مسار الصوت)
                                if (audioTracks.size > 1) {
                                    OutlinedButton(
                                        onClick = {
                                            val nextIdx = (selectedAudioTrackIndex + 1) % audioTracks.size
                                            selectedAudioTrackIndex = nextIdx
                                            val opt = audioTracks[nextIdx]
                                            try {
                                                val groups = exoPlayer.currentTracks.groups
                                                if (opt.groupIndex in groups.indices) {
                                                    val mediaTrackGroup = groups[opt.groupIndex].mediaTrackGroup
                                                    exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                                                        .buildUpon()
                                                        .setTrackTypeDisabled(C.TRACK_TYPE_AUDIO, false)
                                                        .setOverrideForType(
                                                            TrackSelectionOverride(mediaTrackGroup, opt.trackIndex)
                                                        )
                                                        .build()
                                                    audioStatusToast = "تم اختيار: ${opt.label}"
                                                }
                                            } catch (_: Exception) {
                                            }
                                        },
                                        border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Audiotrack,
                                            contentDescription = null,
                                            tint = DijlahGold,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "مسار الصوت (${selectedAudioTrackIndex + 1}/${audioTracks.size})",
                                            color = DijlahGold,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { retryNonce += 1 }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "إعادة تنشيط البث",
                                        tint = DijlahGold
                                    )
                                }
                            }

                            // Stream Format / Server Switcher Pills (100% Stealth labels)
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                items(candidates.size) { index ->
                                    val candidate = candidates[index]
                                    val selected = index == selectedCandidateIndex
                                    FilterChip(
                                        selected = selected,
                                        onClick = { selectedCandidateIndex = index },
                                        label = {
                                            Text(
                                                text = candidate.label,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = DijlahGold,
                                            selectedLabelColor = Color(0xFF050B14),
                                            containerColor = DijlahSurfaceCard.copy(alpha = 0.85f),
                                            labelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // On-Screen Digital Channel Number Zapping Pad
            if (showNumberPadDrawer && isLiveStream) {
                Surface(
                    color = DijlahBgSecondary.copy(alpha = 0.96f),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, DijlahGold),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 24.dp)
                        .width(240.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "طلب القناة بالرقم",
                                color = DijlahGold,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                            IconButton(
                                onClick = { showNumberPadDrawer = false },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = DijlahTextSecondary)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(DijlahSurfaceCard)
                                .border(1.dp, DijlahGold.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (numberPadDigits.isBlank()) "---" else "#$numberPadDigits",
                                color = DijlahCyan,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }

                        val rows = listOf(
                            listOf("1", "2", "3"),
                            listOf("4", "5", "6"),
                            listOf("7", "8", "9"),
                            listOf("مسح", "0", "انتقال")
                        )
                        rows.forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                row.forEach { key ->
                                    Button(
                                        onClick = {
                                            when (key) {
                                                "مسح" -> numberPadDigits = ""
                                                "انتقال" -> {
                                                    val targetNum = numberPadDigits.toIntOrNull()
                                                    if (targetNum != null) {
                                                        val found = allChannels.firstOrNull { it.channelNumber == targetNum }
                                                            ?: allChannels.getOrNull(targetNum - 1)
                                                        if (found != null) {
                                                            currentMedia = ActivePlayerMedia.ChannelMedia(found)
                                                            selectedCandidateIndex = 0
                                                            onSelectChannel(found)
                                                            showNumberPadDrawer = false
                                                            numberPadDigits = ""
                                                        }
                                                    }
                                                }
                                                else -> if (numberPadDigits.length < 4) {
                                                    numberPadDigits += key
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (key == "انتقال") DijlahGold else DijlahSurfaceCard,
                                            contentColor = if (key == "انتقال") Color(0xFF050B14) else DijlahTextPrimary
                                        ),
                                        contentPadding = PaddingValues(0.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(36.dp)
                                    ) {
                                        Text(key, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Quick Channel Zapping Drawer (Side Panel)
            if (showChannelDrawer) {
                Surface(
                    color = DijlahBgSecondary.copy(alpha = 0.96f),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .width(290.dp)
                        .border(1.dp, DijlahGold.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "التنقل السريع بين القنوات",
                                color = DijlahGold,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                            IconButton(onClick = { showChannelDrawer = false }) {
                                Icon(Icons.Default.Close, contentDescription = "إغلاق القائمة", tint = Color.White)
                            }
                        }
                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(allChannels, key = { it.id }) { ch ->
                                val isCurrent = (currentMedia as? ActivePlayerMedia.ChannelMedia)?.channel?.id == ch.id
                                Surface(
                                    onClick = {
                                        if (multiScreenMode != MultiScreenMode.SINGLE && activeMultiCellIndex in quadCells.indices) {
                                            val updatedQuad = quadCells.toMutableList()
                                            updatedQuad[activeMultiCellIndex] = ch
                                            quadCells = updatedQuad
                                        }
                                        currentMedia = ActivePlayerMedia.ChannelMedia(ch)
                                        selectedCandidateIndex = 0
                                        onSelectChannel(ch)
                                        showChannelDrawer = false
                                    },
                                    color = if (isCurrent) DijlahGold.copy(alpha = 0.22f) else DijlahSurfaceCard,
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = 1.dp,
                                        color = if (isCurrent) DijlahGold else Color.Transparent
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(ch.logoColorHex).copy(alpha = 0.25f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "#${ch.channelNumber}",
                                                color = DijlahGold,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 10.sp
                                            )
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = ch.name,
                                                color = DijlahTextPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = ch.category,
                                                color = DijlahTextSecondary,
                                                fontSize = 10.sp,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun buildVodFallbackSources(rawUrl: String, baseLabel: String): List<StreamSource> {
    val clean = rawUrl.trim()
    if (clean.isBlank()) return emptyList()

    val baseWithoutExt = when {
        clean.endsWith(".mp4", ignoreCase = true) -> clean.dropLast(4)
        clean.endsWith(".mkv", ignoreCase = true) -> clean.dropLast(4)
        clean.endsWith(".m3u8", ignoreCase = true) -> clean.dropLast(5)
        clean.endsWith(".ts", ignoreCase = true) -> clean.dropLast(3)
        clean.endsWith(".avi", ignoreCase = true) -> clean.dropLast(4)
        else -> clean
    }

    val list = mutableListOf<StreamSource>()
    val seen = mutableSetOf<String>()

    fun addSource(id: String, label: String, url: String, isBackup: Boolean) {
        if (seen.add(url)) {
            list.add(
                StreamSource(
                    id = id,
                    label = label,
                    url = url,
                    quality = "FHD",
                    isBackup = isBackup
                )
            )
        }
    }

    // 1. Original container extension first
    addSource("vod_primary", "$baseLabel 1 (مباشر)", clean, isBackup = false)
    // 2. Automatic format fallbacks (.mp4 -> .m3u8 -> .ts -> .mkv)
    addSource("vod_mp4", "$baseLabel (MP4)", "$baseWithoutExt.mp4", isBackup = true)
    addSource("vod_hls", "$baseLabel (HLS)", "$baseWithoutExt.m3u8", isBackup = true)
    addSource("vod_ts", "$baseLabel (MPEG-TS)", "$baseWithoutExt.ts", isBackup = true)
    addSource("vod_mkv", "$baseLabel (MKV)", "$baseWithoutExt.mkv", isBackup = true)

    return list
}

private fun buildStreamCandidates(
    rawSources: List<StreamSource>,
    isLiveStream: Boolean
): List<StreamCandidate> {
    val result = mutableListOf<StreamCandidate>()
    val seenUrls = mutableSetOf<String>()

    for (src in rawSources) {
        val url = src.url.trim()
        if (url.isBlank()) continue
        val lower = url.lowercase(Locale.US)

        if (isLiveStream) {
            if (lower.endsWith(".m3u8")) {
                if (seenUrls.add(url)) {
                    result.add(
                        StreamCandidate(
                            label = "سيرفر دجلة بلس 1 (HLS)",
                            url = url,
                            forceMimeType = MimeTypes.APPLICATION_M3U8
                        )
                    )
                }
                val tsUrl = url.dropLast(5) + ".ts"
                if (seenUrls.add(tsUrl)) {
                    result.add(
                        StreamCandidate(
                            label = "سيرفر دجلة بلس 2 (MPEG-TS)",
                            url = tsUrl,
                            forceMimeType = MimeTypes.VIDEO_MP2T
                        )
                    )
                }
            } else if (lower.endsWith(".ts")) {
                if (seenUrls.add(url)) {
                    result.add(
                        StreamCandidate(
                            label = "سيرفر دجلة بلس 2 (MPEG-TS)",
                            url = url,
                            forceMimeType = MimeTypes.VIDEO_MP2T
                        )
                    )
                }
                val m3u8Url = url.dropLast(3) + ".m3u8"
                if (seenUrls.add(m3u8Url)) {
                    result.add(
                        StreamCandidate(
                            label = "سيرفر دجلة بلس 1 (HLS)",
                            url = m3u8Url,
                            forceMimeType = MimeTypes.APPLICATION_M3U8
                        )
                    )
                }
            } else {
                if (seenUrls.add(url)) {
                    result.add(
                        StreamCandidate(
                            label = src.label,
                            url = url,
                            forceMimeType = null
                        )
                    )
                }
            }
        } else {
            val forceMime = when {
                lower.endsWith(".m3u8") -> MimeTypes.APPLICATION_M3U8
                lower.endsWith(".ts") -> MimeTypes.VIDEO_MP2T
                else -> null
            }
            if (seenUrls.add(url)) {
                result.add(
                    StreamCandidate(
                        label = src.label,
                        url = url,
                        forceMimeType = forceMime
                    )
                )
            }
        }
    }

    val firstRaw = rawSources.firstOrNull()?.url?.trim().orEmpty()
    if (firstRaw.startsWith("http")) {
        val proxyUrl = LocalStreamProxyServer.getProxyStreamUrl(firstRaw)
        if (seenUrls.add(proxyUrl)) {
            result.add(
                StreamCandidate(
                    label = "سيرفر دجلة بلس (نفق البروكسي المحلي)",
                    url = proxyUrl,
                    forceMimeType = if (firstRaw.endsWith(".ts", ignoreCase = true)) MimeTypes.VIDEO_MP2T else null
                )
            )
        }
    }

    return result
}

private fun formatDurationMs(ms: Long): String {
    val totalSeconds = (ms / 1000L).coerceAtLeast(0L)
    val hours = totalSeconds / 3600L
    val minutes = (totalSeconds % 3600L) / 60L
    val seconds = totalSeconds % 60L
    return if (hours > 0) {
        String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }
}
