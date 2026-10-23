package com.example

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.ActivePlayerMedia
import com.example.model.MainSection
import com.example.ui.components.*
import com.example.ui.theme.DijlahBgDark
import com.example.ui.theme.DijlahGold
import com.example.ui.theme.DijlahPlusTheme
import com.example.viewmodel.DijlahViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force mandatory Landscape orientation for Smart TV / Android Box Bento layout
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        enableEdgeToEdge()

        // Immersive full-screen mode
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            DijlahPlusTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    DijlahPlusApp()
                }
            }
        }
    }
}

@Composable
fun DijlahPlusApp(
    viewModel: DijlahViewModel = viewModel()
) {
    val context = LocalContext.current
    val config by viewModel.config.collectAsStateWithLifecycle()
    val channels by viewModel.channels.collectAsStateWithLifecycle()
    val matches by viewModel.matches.collectAsStateWithLifecycle()
    val movies by viewModel.movies.collectAsStateWithLifecycle()
    val seriesList by viewModel.seriesList.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val savedPlaylists by viewModel.savedPlaylists.collectAsStateWithLifecycle()
    val savedPlaybackPositions by viewModel.savedPlaybackPositions.collectAsStateWithLifecycle()
    val currentSection by viewModel.currentSection.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val activePlayerMedia by viewModel.activePlayerMedia.collectAsStateWithLifecycle()
    val showStealthPinPrompt by viewModel.showStealthPinPrompt.collectAsStateWithLifecycle()
    val showAdminModal by viewModel.showAdminModal.collectAsStateWithLifecycle()
    val showServerStatusModal by viewModel.showServerStatusModal.collectAsStateWithLifecycle()
    val showTelegramPopup by viewModel.showTelegramPopup.collectAsStateWithLifecycle()
    val showParentalPinDialog by viewModel.showParentalPinDialog.collectAsStateWithLifecycle()
    val selectedSeriesForDetail by viewModel.selectedSeriesForDetail.collectAsStateWithLifecycle()
    val isSyncingPlaylist by viewModel.isSyncingPlaylist.collectAsStateWithLifecycle()
    val isLoadingSeriesEpisodes by viewModel.isLoadingSeriesEpisodes.collectAsStateWithLifecycle()
    val pingLatencyMs by viewModel.pingLatencyMs.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val activePlaylist = remember(savedPlaylists) {
        savedPlaylists.firstOrNull { it.isActive } ?: savedPlaylists.firstOrNull()
    }

    // Handle system Back button inside sub-sections to return to Home Bento screen
    BackHandler(enabled = currentSection != MainSection.HOME) {
        viewModel.selectSection(MainSection.HOME)
    }

    LaunchedEffect(toastMessage) {
        val msg = toastMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Short)
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DijlahBgDark),
        containerColor = DijlahBgDark,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            DijlahTopBar(
                config = config,
                currentSection = currentSection,
                searchQuery = searchQuery,
                isSyncing = isSyncingPlaylist,
                pingLatencyMs = pingLatencyMs,
                onSearchQueryChanged = viewModel::onSearchQueryChanged,
                onLogoTapped = viewModel::onLogoTapped,
                onSelectSection = viewModel::selectSection,
                onRefreshServer = viewModel::refreshActivePlaylist,
                onOpenServerStatus = viewModel::openServerStatusModal,
                onOpenTelegramPopup = viewModel::openTelegramPopup
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = DijlahGold,
                    contentColor = Color(0xFF050B14)
                ) {
                    Text(
                        text = data.visuals.message,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DijlahBgDark)
        ) {
            when (currentSection) {
                MainSection.HOME -> HomeBentoDashboardScreen(
                    config = config,
                    activePlaylist = activePlaylist,
                    channelsCount = channels.size,
                    moviesCount = movies.size,
                    seriesCount = seriesList.size,
                    favoritesCount = favoriteIds.size,
                    isSyncing = isSyncingPlaylist,
                    onSelectSection = viewModel::selectSection,
                    onRefreshPlaylist = viewModel::refreshActivePlaylist,
                    onOpenServerStatus = viewModel::openServerStatusModal,
                    onExitApp = {
                        (context as? Activity)?.finish()
                    }
                )

                MainSection.MATCHES -> MatchesScheduleScreen(
                    matches = matches,
                    searchQuery = searchQuery,
                    isSyncing = isSyncingPlaylist,
                    onPlayMatch = viewModel::requestPlayMatch,
                    onRefreshServer = viewModel::refreshActivePlaylist
                )

                MainSection.LIVE_TV -> LiveTvSectionScreen(
                    channels = channels,
                    favoriteIds = favoriteIds,
                    selectedCategory = selectedCategory,
                    lockedCategories = config.lockedCategories,
                    searchQuery = searchQuery,
                    isSyncing = isSyncingPlaylist,
                    onSelectCategory = viewModel::selectCategory,
                    onPlayChannel = viewModel::requestPlayChannel,
                    onPlayChannelByNumber = { num -> viewModel.playChannelByNumber(num) },
                    onToggleFavorite = viewModel::toggleFavorite,
                    onRefreshServer = viewModel::refreshActivePlaylist
                )

                MainSection.MOVIES -> MoviesVodScreen(
                    movies = movies,
                    favoriteIds = favoriteIds,
                    savedPlaybackPositions = savedPlaybackPositions,
                    selectedCategory = selectedCategory,
                    lockedCategories = config.lockedCategories,
                    searchQuery = searchQuery,
                    isSyncing = isSyncingPlaylist,
                    onSelectCategory = viewModel::selectCategory,
                    onPlayMovie = viewModel::requestPlayMovie,
                    onToggleFavorite = viewModel::toggleFavorite
                )

                MainSection.SERIES -> SeriesVodScreen(
                    seriesList = seriesList,
                    favoriteIds = favoriteIds,
                    selectedCategory = selectedCategory,
                    lockedCategories = config.lockedCategories,
                    searchQuery = searchQuery,
                    isSyncing = isSyncingPlaylist,
                    onSelectCategory = viewModel::selectCategory,
                    onOpenSeries = viewModel::openSeriesDetail,
                    onToggleFavorite = viewModel::toggleFavorite
                )

                MainSection.FAVORITES -> FavoritesScreen(
                    channels = channels,
                    movies = movies,
                    seriesList = seriesList,
                    favoriteIds = favoriteIds,
                    onPlayChannel = viewModel::requestPlayChannel,
                    onPlayMovie = viewModel::requestPlayMovie,
                    onOpenSeries = viewModel::openSeriesDetail,
                    onToggleFavorite = viewModel::toggleFavorite
                )

                MainSection.PLAYLIST_MANAGER -> PlaylistManagerScreen(
                    config = config,
                    savedPlaylists = savedPlaylists,
                    isSyncing = isSyncingPlaylist,
                    onActivateByCode = viewModel::activatePlaylistByCode,
                    onLoginXtream = viewModel::loginWithXtreamCredentials,
                    onLoginM3u = viewModel::loginWithM3uUrl,
                    onActivateSavedPlaylist = viewModel::activateSavedPlaylist,
                    onDeleteSavedPlaylist = viewModel::deleteSavedPlaylist
                )

                MainSection.SETTINGS -> SettingsScreen(
                    config = config,
                    onUpdateConfig = viewModel::updateAppConfig,
                    onOpenPlaylistManager = { viewModel.selectSection(MainSection.PLAYLIST_MANAGER) },
                    onOpenServerStatus = viewModel::openServerStatusModal,
                    onMacAddressTapped = viewModel::onMacAddressTapped
                )
            }
        }
    }

    // 1. Official Telegram Subscription Entry Popup (https://t.me/Dijla_Plus)
    if (showTelegramPopup && config.telegramPopupEnabled) {
        TelegramSubscriptionPopupModal(
            config = config,
            onDismiss = viewModel::dismissTelegramPopup
        )
    }

    // 2. Series Seasons & Episodes Modal
    selectedSeriesForDetail?.let { series ->
        SeriesDetailModal(
            series = series,
            isLoadingEpisodes = isLoadingSeriesEpisodes,
            savedPlaybackPositions = savedPlaybackPositions,
            onPlayEpisode = { season, episode ->
                viewModel.playSeriesEpisode(series, season, episode)
            },
            onDismiss = viewModel::closeSeriesDetail
        )
    }

    // 3. Fullscreen Smart Video Player Modal (Media3 ExoPlayer + Multi-Screen 2x2 + Auto-Resume)
    activePlayerMedia?.let { media ->
        val mediaKey = when (media) {
            is ActivePlayerMedia.MovieMedia -> media.movie.id
            is ActivePlayerMedia.EpisodeMedia -> media.episode.id
            is ActivePlayerMedia.ChannelMedia -> media.channel.id
            is ActivePlayerMedia.MatchMedia -> media.match.id
        }
        val savedPos = savedPlaybackPositions[mediaKey] ?: 0L

        SmartVideoPlayerModal(
            media = media,
            allChannels = channels,
            pingLatencyMs = pingLatencyMs,
            savedPositionMs = savedPos,
            onSavePlaybackPosition = viewModel::savePlaybackPosition,
            onClearPlaybackPosition = viewModel::clearPlaybackPosition,
            onSelectChannel = viewModel::requestPlayChannel,
            onPlayNextEpisode = { s, season, nextEp ->
                viewModel.playSeriesEpisode(s, season, nextEp)
            },
            onServerFailover = viewModel::failoverToNextServer,
            onClose = viewModel::closePlayer
        )
    }

    // 4. Server Status Modal (100% Stealth)
    if (showServerStatusModal) {
        XtreamServerStatusModal(
            activePlaylist = activePlaylist,
            channelsCount = channels.size,
            moviesCount = movies.size,
            seriesCount = seriesList.size,
            isSyncing = isSyncingPlaylist,
            pingLatencyMs = pingLatencyMs,
            onRefresh = viewModel::refreshActivePlaylist,
            onOpenPlaylistManager = { viewModel.selectSection(MainSection.PLAYLIST_MANAGER) },
            onDismiss = viewModel::closeServerStatusModal
        )
    }

    // 5. Parental / Encrypted Channel PIN Gate
    if (showParentalPinDialog) {
        ParentalPinGateDialog(
            onVerifyPin = viewModel::verifyParentalPin,
            onDismiss = viewModel::dismissParentalPinDialog
        )
    }

    // 6. Stealth Admin PIN Prompt (7-Tap Logo / 5-Tap MAC / #*2008*#)
    if (showStealthPinPrompt) {
        StealthPinPromptDialog(
            onVerifyPin = viewModel::verifyMasterAdminPin,
            onDismiss = viewModel::dismissStealthPinPrompt
        )
    }

    // 7. Full Stealth Admin Dashboard Modal (6 Integrated Modules)
    if (showAdminModal) {
        StealthAdminModal(
            config = config,
            channels = channels,
            movies = movies,
            seriesList = seriesList,
            onUpdateConfig = viewModel::updateAppConfig,
            onAddOrUpdateServerPoolItem = viewModel::addOrUpdateXtreamServerPoolItem,
            onDeleteServerPoolItem = viewModel::deleteXtreamServerPoolItem,
            onSetPrimaryServerPoolItem = viewModel::setPrimaryXtreamServerPoolItem,
            onAddOrUpdateVipAccount = viewModel::addOrUpdateVipAccount,
            onDeleteVipAccount = viewModel::deleteVipAccount,
            onResetVipAccountMac = viewModel::resetVipAccountMac,
            onAddOrUpdateActivationCode = viewModel::addOrUpdateActivationCodeEntry,
            onDeleteActivationCode = viewModel::deleteActivationCodeEntry,
            onResetActivationCodeMac = viewModel::resetActivationCodeMac,
            onAddOrUpdatePromoSlide = viewModel::addOrUpdatePromoSlide,
            onDeletePromoSlide = viewModel::deletePromoSlide,
            onToggleLockedCategory = viewModel::toggleLockedCategory,
            onExportJson = viewModel::exportDatabaseJson,
            onImportJson = viewModel::importDatabaseJson,
            onSyncCloudUrl = viewModel::syncWithCloudUrl,
            onResetFactory = { viewModel.resetToDefaultSeed(silent = false) },
            onPostToast = viewModel::postToast,
            onClose = viewModel::closeAdminModal
        )
    }
}
