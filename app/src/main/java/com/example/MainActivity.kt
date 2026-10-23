package com.example

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
    val config by viewModel.config.collectAsStateWithLifecycle()
    val channels by viewModel.channels.collectAsStateWithLifecycle()
    val liveCategories by viewModel.liveCategories.collectAsStateWithLifecycle()
    val matches by viewModel.matches.collectAsStateWithLifecycle()
    val movies by viewModel.movies.collectAsStateWithLifecycle()
    val vodCategories by viewModel.vodCategories.collectAsStateWithLifecycle()
    val seriesList by viewModel.seriesList.collectAsStateWithLifecycle()
    val seriesCategories by viewModel.seriesCategories.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val savedPlaylists by viewModel.savedPlaylists.collectAsStateWithLifecycle()
    val savedPlaybackPositions by viewModel.savedPlaybackPositions.collectAsStateWithLifecycle()
    val recentlyWatchedChannelIds by viewModel.recentlyWatchedChannelIds.collectAsStateWithLifecycle()
    val recentlyWatchedSeriesIds by viewModel.recentlyWatchedSeriesIds.collectAsStateWithLifecycle()
    val currentSection by viewModel.currentSection.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val activePlayerMedia by viewModel.activePlayerMedia.collectAsStateWithLifecycle()
    val showSplashScreen by viewModel.showSplashScreen.collectAsStateWithLifecycle()
    val isAuthenticatedSession by viewModel.isAuthenticatedSession.collectAsStateWithLifecycle()
    val loginErrorMessage by viewModel.loginErrorMessage.collectAsStateWithLifecycle()
    val pendingPlaylistForNaming by viewModel.pendingPlaylistForNaming.collectAsStateWithLifecycle()
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
    BackHandler(enabled = isAuthenticatedSession && currentSection != MainSection.HOME) {
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
            if (!showSplashScreen && isAuthenticatedSession) {
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
            }
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
            when {
                // Step 1: Startup Splash Screen (1.5s)
                showSplashScreen -> {
                    DijlaSplashScreen(
                        appName = config.appName
                    )
                }

                // Step 2: Login Screen (VIP Username/Password OR Free Activation Code D1)
                !isAuthenticatedSession -> {
                    DijlaLoginScreen(
                        macAddress = config.macAddress,
                        telegramHandle = config.telegramHandle,
                        isSyncing = isSyncingPlaylist,
                        errorMessage = loginErrorMessage,
                        onSubmitVipCredentials = viewModel::submitLoginVipCredentials,
                        onSubmitActivationCode = viewModel::submitLoginActivationCode,
                        onLogoTapped = viewModel::onLogoTapped,
                        onMacAddressTapped = viewModel::onMacAddressTapped,
                        onSecretCodeEntered = viewModel::openStealthPinPromptDirectly
                    )
                }

                // Step 3: Main Authenticated Application
                else -> {
                    when (currentSection) {
                        MainSection.HOME -> HomeBentoDashboardScreen(
                            config = config,
                            activePlaylist = activePlaylist,
                            isSyncing = isSyncingPlaylist,
                            onSelectSection = viewModel::selectSection,
                            onRefreshPlaylist = viewModel::refreshActivePlaylist,
                            onOpenServerStatus = viewModel::openServerStatusModal,
                            onLogoutAccount = viewModel::logoutToLoginScreen
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
                            serverLiveCategories = liveCategories,
                            hiddenCategories = config.hiddenLiveCategories,
                            recentlyWatchedIds = recentlyWatchedChannelIds,
                            favoriteIds = favoriteIds,
                            selectedCategory = selectedCategory,
                            lockedCategories = config.lockedCategories,
                            searchQuery = searchQuery,
                            isSyncing = isSyncingPlaylist,
                            onSelectCategory = viewModel::selectCategory,
                            onPlayChannel = viewModel::requestPlayChannel,
                            onPlayChannelByNumber = { num -> viewModel.playChannelByNumber(num) },
                            onToggleFavorite = viewModel::toggleFavorite,
                            onRefreshServer = viewModel::refreshActivePlaylist,
                            onBackToHome = { viewModel.selectSection(MainSection.HOME) }
                        )

                        MainSection.MOVIES -> MoviesVodScreen(
                            movies = movies,
                            serverVodCategories = vodCategories,
                            hiddenCategories = config.hiddenVodCategories,
                            favoriteIds = favoriteIds,
                            savedPlaybackPositions = savedPlaybackPositions,
                            selectedCategory = selectedCategory,
                            lockedCategories = config.lockedCategories,
                            searchQuery = searchQuery,
                            isSyncing = isSyncingPlaylist,
                            onSelectCategory = viewModel::selectCategory,
                            onPlayMovie = viewModel::requestPlayMovie,
                            onToggleFavorite = viewModel::toggleFavorite,
                            onBackToHome = { viewModel.selectSection(MainSection.HOME) }
                        )

                        MainSection.SERIES -> SeriesVodScreen(
                            seriesList = seriesList,
                            serverSeriesCategories = seriesCategories,
                            hiddenCategories = config.hiddenSeriesCategories,
                            recentlyWatchedSeriesIds = recentlyWatchedSeriesIds,
                            favoriteIds = favoriteIds,
                            selectedCategory = selectedCategory,
                            lockedCategories = config.lockedCategories,
                            searchQuery = searchQuery,
                            isSyncing = isSyncingPlaylist,
                            onSelectCategory = viewModel::selectCategory,
                            onOpenSeries = viewModel::openSeriesDetail,
                            onToggleFavorite = viewModel::toggleFavorite,
                            onBackToHome = { viewModel.selectSection(MainSection.HOME) }
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
                            onDeleteSavedPlaylist = viewModel::deleteSavedPlaylist,
                            onOpenLoginScreen = viewModel::logoutToLoginScreen
                        )

                        MainSection.SETTINGS -> SettingsScreen(
                            config = config,
                            liveCategories = liveCategories,
                            vodCategories = vodCategories,
                            seriesCategories = seriesCategories,
                            onUpdateConfig = viewModel::updateAppConfig,
                            onOpenPlaylistManager = { viewModel.selectSection(MainSection.PLAYLIST_MANAGER) },
                            onOpenLoginScreen = viewModel::logoutToLoginScreen,
                            onRefreshPlaylistsNow = viewModel::refreshActivePlaylist,
                            onClearChannelsHistory = viewModel::clearChannelsHistory,
                            onClearMoviesHistory = viewModel::clearMoviesHistory,
                            onClearSeriesHistory = viewModel::clearSeriesHistory,
                            onMacAddressTapped = viewModel::onMacAddressTapped,
                            onPostToast = viewModel::postToast
                        )
                    }
                }
            }
        }
    }

    // 0. Enter Playlist Name Modal (تسمية قائمة التشغيل — Screenshot 10)
    pendingPlaylistForNaming?.let { pending ->
        EnterPlaylistNameModal(
            defaultName = pending.playlistName,
            onSave = viewModel::confirmPlaylistNameAndEnterHome,
            onExit = viewModel::cancelPlaylistNamingModal
        )
    }

    // 1. Official Telegram Subscription Entry Popup (https://t.me/Dijla_Plus)
    if (isAuthenticatedSession && showTelegramPopup && config.telegramPopupEnabled) {
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

    // 3. Fullscreen Smart Video Player Modal (Media3 ExoPlayer + Guaranteed Audio Boost + Episode Fallback)
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

    // 4. Server Status Modal (100% Stealth — Zero Item Counts)
    if (showServerStatusModal) {
        XtreamServerStatusModal(
            activePlaylist = activePlaylist,
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
