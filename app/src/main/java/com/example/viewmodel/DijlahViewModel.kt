package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DijlahSeedData
import com.example.model.*
import com.example.network.XtreamApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DijlahViewModel(application: Application) : AndroidViewModel(application) {

    private val jsonFormat = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
    }

    private val databaseFile: File
        get() = File(getApplication<Application>().filesDir, "dijlah_database_v8_final.json")

    private val prefs by lazy {
        getApplication<Application>().getSharedPreferences("dijlah_plus_prefs_v8", Context.MODE_PRIVATE)
    }

    private val _config = MutableStateFlow(DijlahSeedData.defaultConfig)
    val config: StateFlow<AppConfig> = _config.asStateFlow()

    private val _channels = MutableStateFlow<List<LiveChannel>>(emptyList())
    val channels: StateFlow<List<LiveChannel>> = _channels.asStateFlow()

    private val _matches = MutableStateFlow<List<MatchItem>>(emptyList())
    val matches: StateFlow<List<MatchItem>> = _matches.asStateFlow()

    private val _movies = MutableStateFlow<List<VodMovie>>(emptyList())
    val movies: StateFlow<List<VodMovie>> = _movies.asStateFlow()

    private val _seriesList = MutableStateFlow<List<VodSeries>>(emptyList())
    val seriesList: StateFlow<List<VodSeries>> = _seriesList.asStateFlow()

    private val _liveCategories = MutableStateFlow<List<String>>(emptyList())
    val liveCategories: StateFlow<List<String>> = _liveCategories.asStateFlow()

    private val _vodCategories = MutableStateFlow<List<String>>(emptyList())
    val vodCategories: StateFlow<List<String>> = _vodCategories.asStateFlow()

    private val _seriesCategories = MutableStateFlow<List<String>>(emptyList())
    val seriesCategories: StateFlow<List<String>> = _seriesCategories.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val _savedPlaylists = MutableStateFlow<List<SavedPlaylistAccount>>(emptyList())
    val savedPlaylists: StateFlow<List<SavedPlaylistAccount>> = _savedPlaylists.asStateFlow()

    private val _savedPlaybackPositions = MutableStateFlow<Map<String, Long>>(emptyMap())
    val savedPlaybackPositions: StateFlow<Map<String, Long>> = _savedPlaybackPositions.asStateFlow()

    private val _recentlyWatchedChannelIds = MutableStateFlow<List<String>>(emptyList())
    val recentlyWatchedChannelIds: StateFlow<List<String>> = _recentlyWatchedChannelIds.asStateFlow()

    private val _recentlyWatchedSeriesIds = MutableStateFlow<List<String>>(emptyList())
    val recentlyWatchedSeriesIds: StateFlow<List<String>> = _recentlyWatchedSeriesIds.asStateFlow()

    // Startup & Login Flow States
    private val _showSplashScreen = MutableStateFlow(true)
    val showSplashScreen: StateFlow<Boolean> = _showSplashScreen.asStateFlow()

    private val _isAuthenticatedSession = MutableStateFlow(false)
    val isAuthenticatedSession: StateFlow<Boolean> = _isAuthenticatedSession.asStateFlow()

    private val _loginErrorMessage = MutableStateFlow<String?>(null)
    val loginErrorMessage: StateFlow<String?> = _loginErrorMessage.asStateFlow()

    private val _pendingPlaylistForNaming = MutableStateFlow<SavedPlaylistAccount?>(null)
    val pendingPlaylistForNaming: StateFlow<SavedPlaylistAccount?> = _pendingPlaylistForNaming.asStateFlow()

    private val _currentSection = MutableStateFlow(MainSection.HOME)
    val currentSection: StateFlow<MainSection> = _currentSection.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("الكل")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _activePlayerMedia = MutableStateFlow<ActivePlayerMedia?>(null)
    val activePlayerMedia: StateFlow<ActivePlayerMedia?> = _activePlayerMedia.asStateFlow()

    // Stealth Admin Mode State
    private val _showStealthPinPrompt = MutableStateFlow(false)
    val showStealthPinPrompt: StateFlow<Boolean> = _showStealthPinPrompt.asStateFlow()

    private val _showAdminModal = MutableStateFlow(false)
    val showAdminModal: StateFlow<Boolean> = _showAdminModal.asStateFlow()

    // Server Status Modal
    private val _showServerStatusModal = MutableStateFlow(false)
    val showServerStatusModal: StateFlow<Boolean> = _showServerStatusModal.asStateFlow()

    // Telegram Subscription Entry Popup Modal
    private val _showTelegramPopup = MutableStateFlow(false)
    val showTelegramPopup: StateFlow<Boolean> = _showTelegramPopup.asStateFlow()

    // Parental / Encrypted Channel Gate State
    private val _pendingProtectedAction = MutableStateFlow<(() -> Unit)?>(null)
    private val _pendingExpectedPin = MutableStateFlow("")
    private val _showParentalPinDialog = MutableStateFlow(false)
    val showParentalPinDialog: StateFlow<Boolean> = _showParentalPinDialog.asStateFlow()

    // Series Detail Modal State
    private val _selectedSeriesForDetail = MutableStateFlow<VodSeries?>(null)
    val selectedSeriesForDetail: StateFlow<VodSeries?> = _selectedSeriesForDetail.asStateFlow()

    // Live Sync & Loading States
    private val _isSyncingPlaylist = MutableStateFlow(false)
    val isSyncingPlaylist: StateFlow<Boolean> = _isSyncingPlaylist.asStateFlow()

    private val _isLoadingSeriesEpisodes = MutableStateFlow(false)
    val isLoadingSeriesEpisodes: StateFlow<Boolean> = _isLoadingSeriesEpisodes.asStateFlow()

    // Real-time Server Ping Latency (ms)
    private val _pingLatencyMs = MutableStateFlow(24)
    val pingLatencyMs: StateFlow<Int> = _pingLatencyMs.asStateFlow()

    // Toast feedback message
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // 7-Tap Logo Counter within 3 seconds
    private var logoTapCount = 0
    private var firstTapTimestamp = 0L

    // 5-Tap MAC Address Counter in Settings
    private var macTapCount = 0
    private var firstMacTapTimestamp = 0L

    init {
        loadFromDiskOrSeed()
        startSplashAndSessionFlow()
    }

    private fun startSplashAndSessionFlow() {
        val hasLoggedInBefore = prefs.getBoolean("has_active_session", false)
        _isAuthenticatedSession.value = hasLoggedInBefore

        viewModelScope.launch {
            delay(1500L)
            _showSplashScreen.value = false
            if (_isAuthenticatedSession.value) {
                _showTelegramPopup.value = _config.value.telegramPopupEnabled
                val active = _savedPlaylists.value.firstOrNull { it.isActive }
                if (active != null && _channels.value.isEmpty()) {
                    refreshPlaylistFromServer(active, showToast = false)
                }
            }
        }
    }

    fun dismissTelegramPopup() {
        _showTelegramPopup.value = false
    }

    fun openTelegramPopup() {
        _showTelegramPopup.value = true
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun postToast(message: String) {
        _toastMessage.value = message
    }

    private fun loadFromDiskOrSeed() {
        try {
            if (databaseFile.exists()) {
                val rawJson = databaseFile.readText()
                val snapshot = jsonFormat.decodeFromString<DijlahDatabaseSnapshot>(rawJson)
                _config.value = snapshot.config
                _channels.value = snapshot.channels
                _matches.value = snapshot.matches
                _movies.value = snapshot.movies
                _seriesList.value = snapshot.seriesList
                _liveCategories.value = snapshot.liveCategories
                _vodCategories.value = snapshot.vodCategories
                _seriesCategories.value = snapshot.seriesCategories
                _favoriteIds.value = snapshot.favoriteIds.toSet()
                _savedPlaylists.value = snapshot.savedPlaylists.ifEmpty {
                    listOf(DijlahSeedData.defaultPrimaryPlaylist)
                }
                _savedPlaybackPositions.value = snapshot.savedPlaybackPositions
                _recentlyWatchedChannelIds.value = snapshot.recentlyWatchedChannelIds
                _recentlyWatchedSeriesIds.value = snapshot.recentlyWatchedSeriesIds
            } else {
                resetToDefaultSeed(silent = true)
            }
        } catch (e: Exception) {
            resetToDefaultSeed(silent = true)
        }
    }

    private fun persistToDisk() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val snapshot = DijlahDatabaseSnapshot(
                    config = _config.value,
                    channels = _channels.value,
                    matches = _matches.value,
                    movies = _movies.value,
                    seriesList = _seriesList.value,
                    liveCategories = _liveCategories.value,
                    vodCategories = _vodCategories.value,
                    seriesCategories = _seriesCategories.value,
                    favoriteIds = _favoriteIds.value.toList(),
                    savedPlaylists = _savedPlaylists.value,
                    savedPlaybackPositions = _savedPlaybackPositions.value,
                    recentlyWatchedChannelIds = _recentlyWatchedChannelIds.value,
                    recentlyWatchedSeriesIds = _recentlyWatchedSeriesIds.value
                )
                databaseFile.writeText(jsonFormat.encodeToString(snapshot))
            } catch (_: Exception) {
            }
        }
    }

    fun resetToDefaultSeed(silent: Boolean = false) {
        _config.value = DijlahSeedData.defaultConfig
        _channels.value = emptyList()
        _matches.value = emptyList()
        _movies.value = emptyList()
        _seriesList.value = emptyList()
        _liveCategories.value = emptyList()
        _vodCategories.value = emptyList()
        _seriesCategories.value = emptyList()
        _favoriteIds.value = emptySet()
        _savedPlaylists.value = listOf(DijlahSeedData.defaultPrimaryPlaylist)
        persistToDisk()
        if (!silent) {
            refreshPlaylistFromServer(DijlahSeedData.defaultPrimaryPlaylist, showToast = true)
        }
    }

    fun logoutToLoginScreen() {
        prefs.edit().putBoolean("has_active_session", false).apply()
        _isAuthenticatedSession.value = false
        _loginErrorMessage.value = null
        _pendingPlaylistForNaming.value = null
        _currentSection.value = MainSection.HOME
        _toastMessage.value = "تم تسجيل الخروج من الحساب"
    }

    fun selectSection(section: MainSection) {
        _currentSection.value = section
        _selectedCategory.value = "الكل"
    }

    fun selectCategory(category: String) {
        val isLockedCategory = _config.value.lockedCategories.contains(category) ||
            (_config.value.lockAdultContent && (category.contains("+18") || category.contains("كبار") || category.contains("Adult", ignoreCase = true)))
        if (isLockedCategory) {
            requestPinAccess(expectedPin = _config.value.defaultParentalPin) {
                _selectedCategory.value = category
            }
        } else {
            _selectedCategory.value = category
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        val trimmed = query.trim()
        // Secret Trigger #2: Typing #*2008*# in the Search bar
        if (trimmed == _config.value.secretSearchCode || trimmed == "#*2008*#" || trimmed == "#*9988*#") {
            _searchQuery.value = ""
            _showStealthPinPrompt.value = true
        }
    }

    // Secret Trigger #1: Tap "دجلة بلس" Logo 7 times within 3 seconds
    fun onLogoTapped() {
        val now = System.currentTimeMillis()
        if (now - firstTapTimestamp > 3000L) {
            logoTapCount = 1
            firstTapTimestamp = now
        } else {
            logoTapCount++
            if (logoTapCount >= 7) {
                logoTapCount = 0
                firstTapTimestamp = 0L
                _showStealthPinPrompt.value = true
            }
        }
    }

    // Secret Trigger #3: Tap MAC Address 5 times in Settings or Login
    fun onMacAddressTapped() {
        val now = System.currentTimeMillis()
        if (now - firstMacTapTimestamp > 3000L) {
            macTapCount = 1
            firstMacTapTimestamp = now
        } else {
            macTapCount++
            if (macTapCount >= 5) {
                macTapCount = 0
                firstMacTapTimestamp = 0L
                _showStealthPinPrompt.value = true
            }
        }
    }

    fun openStealthPinPromptDirectly() {
        _showStealthPinPrompt.value = true
    }

    fun dismissStealthPinPrompt() {
        _showStealthPinPrompt.value = false
    }

    fun verifyMasterAdminPin(enteredPin: String): Boolean {
        val trimmed = enteredPin.trim()
        return if (trimmed == _config.value.masterAdminPin || trimmed == "2008") {
            _showStealthPinPrompt.value = false
            _showAdminModal.value = true
            true
        } else {
            false
        }
    }

    fun closeAdminModal() {
        _showAdminModal.value = false
    }

    fun openServerStatusModal() {
        _showServerStatusModal.value = true
    }

    fun closeServerStatusModal() {
        _showServerStatusModal.value = false
    }

    fun toggleFavorite(itemId: String) {
        val updated = _favoriteIds.value.toMutableSet()
        if (updated.contains(itemId)) {
            updated.remove(itemId)
            _toastMessage.value = "تمت الإزالة من المفضلة"
        } else {
            updated.add(itemId)
            _toastMessage.value = "تمت الإضافة إلى المفضلة"
        }
        _favoriteIds.value = updated
        persistToDisk()
    }

    // History clearing actions for Settings cards
    fun clearChannelsHistory() {
        _recentlyWatchedChannelIds.value = emptyList()
        persistToDisk()
        _toastMessage.value = "تم مسح سجل القنوات بنجاح"
    }

    fun clearMoviesHistory() {
        val filtered = _savedPlaybackPositions.value.filterKeys { !it.startsWith("vod_") }
        _savedPlaybackPositions.value = filtered
        persistToDisk()
        _toastMessage.value = "تم مسح سجل الأفلام بنجاح"
    }

    fun clearSeriesHistory() {
        val filtered = _savedPlaybackPositions.value.filterKeys { !it.startsWith("ep_") }
        _savedPlaybackPositions.value = filtered
        _recentlyWatchedSeriesIds.value = emptyList()
        persistToDisk()
        _toastMessage.value = "تم مسح سجل المسلسلات بنجاح"
    }

    // Auto-Resume Playback Progress (saved every 5s)
    fun savePlaybackPosition(mediaKey: String, positionMs: Long) {
        if (mediaKey.isBlank() || positionMs < 3000L) return
        val updated = _savedPlaybackPositions.value.toMutableMap()
        updated[mediaKey] = positionMs
        _savedPlaybackPositions.value = updated
        persistToDisk()
    }

    fun clearPlaybackPosition(mediaKey: String) {
        if (mediaKey.isBlank()) return
        val updated = _savedPlaybackPositions.value.toMutableMap()
        if (updated.remove(mediaKey) != null) {
            _savedPlaybackPositions.value = updated
            persistToDisk()
        }
    }

    fun getSavedPlaybackPosition(mediaKey: String): Long {
        return _savedPlaybackPositions.value[mediaKey] ?: 0L
    }

    private fun recordChannelWatched(channelId: String) {
        val updated = (listOf(channelId) + _recentlyWatchedChannelIds.value.filterNot { it == channelId }).take(60)
        _recentlyWatchedChannelIds.value = updated
        persistToDisk()
    }

    private fun recordSeriesWatched(seriesId: String) {
        val updated = (listOf(seriesId) + _recentlyWatchedSeriesIds.value.filterNot { it == seriesId }).take(60)
        _recentlyWatchedSeriesIds.value = updated
        persistToDisk()
    }

    // Play media with automatic Parental / Custom PIN check
    fun requestPlayChannel(channel: LiveChannel) {
        val requiresPin = channel.isEncrypted || (_config.value.lockAdultContent && channel.isAdult) ||
            _config.value.lockedCategories.contains(channel.category)
        if (requiresPin) {
            val expectedPin = channel.customPin.ifBlank { _config.value.defaultParentalPin }
            requestPinAccess(expectedPin) {
                recordChannelWatched(channel.id)
                _activePlayerMedia.value = ActivePlayerMedia.ChannelMedia(channel)
            }
        } else {
            recordChannelWatched(channel.id)
            _activePlayerMedia.value = ActivePlayerMedia.ChannelMedia(channel)
        }
    }

    fun playChannelByNumber(channelNumber: Int): Boolean {
        val list = _channels.value
        if (list.isEmpty()) return false
        val target = list.firstOrNull { it.channelNumber == channelNumber }
            ?: list.getOrNull(channelNumber - 1)
        return if (target != null) {
            requestPlayChannel(target)
            _toastMessage.value = "القناة رقم $channelNumber: ${target.name}"
            true
        } else {
            _toastMessage.value = "القناة رقم $channelNumber غير موجودة"
            false
        }
    }

    fun requestPlayMatch(match: MatchItem) {
        val linkedChannel = _channels.value.find { it.id == match.linkedChannelId }
            ?: _channels.value.firstOrNull()
        _activePlayerMedia.value = ActivePlayerMedia.MatchMedia(match, linkedChannel)
    }

    fun requestPlayMovie(movie: VodMovie) {
        val requiresPin = (_config.value.lockAdultContent && movie.isAdult) ||
            _config.value.lockedCategories.contains(movie.category)
        if (requiresPin) {
            requestPinAccess(_config.value.defaultParentalPin) {
                _activePlayerMedia.value = ActivePlayerMedia.MovieMedia(movie)
            }
        } else {
            _activePlayerMedia.value = ActivePlayerMedia.MovieMedia(movie)
        }
    }

    fun openSeriesDetail(series: VodSeries) {
        val requiresPin = (_config.value.lockAdultContent && series.isAdult) ||
            _config.value.lockedCategories.contains(series.category)
        val openAction = {
            recordSeriesWatched(series.id)
            _selectedSeriesForDetail.value = series
            if (series.seasons.isEmpty() && series.seriesId.isNotBlank()) {
                fetchRealSeriesEpisodes(series)
            }
        }
        if (requiresPin) {
            requestPinAccess(_config.value.defaultParentalPin, openAction)
        } else {
            openAction()
        }
    }

    private fun fetchRealSeriesEpisodes(series: VodSeries) {
        val activePlaylist = _savedPlaylists.value.firstOrNull { it.isActive }
            ?: DijlahSeedData.defaultPrimaryPlaylist
        val host = activePlaylist.serverHost.ifBlank { DijlahSeedData.PRIMARY_XTREAM_HOST }
        val user = activePlaylist.username.ifBlank { DijlahSeedData.PRIMARY_XTREAM_USER }
        val pass = activePlaylist.password.ifBlank { DijlahSeedData.PRIMARY_XTREAM_PASS }

        viewModelScope.launch {
            _isLoadingSeriesEpisodes.value = true
            val seasons = XtreamApiClient.fetchSeriesInfo(
                hostUrl = host,
                username = user,
                password = pass,
                seriesId = series.seriesId
            )
            _isLoadingSeriesEpisodes.value = false
            if (seasons.isNotEmpty()) {
                val updatedSeries = series.copy(seasons = seasons)
                _selectedSeriesForDetail.value = updatedSeries
                _seriesList.value = _seriesList.value.map {
                    if (it.id == series.id) updatedSeries else it
                }
                persistToDisk()
            } else {
                _toastMessage.value = "تعذر جلب حلقات المسلسل من السيرفر حالياً"
            }
        }
    }

    fun closeSeriesDetail() {
        _selectedSeriesForDetail.value = null
    }

    fun playSeriesEpisode(series: VodSeries, season: SeriesSeason, episode: SeriesEpisode) {
        recordSeriesWatched(series.id)
        _selectedSeriesForDetail.value = null
        _activePlayerMedia.value = ActivePlayerMedia.EpisodeMedia(series, season, episode)
    }

    fun closePlayer() {
        _activePlayerMedia.value = null
    }

    private fun requestPinAccess(expectedPin: String, onSuccess: () -> Unit) {
        _pendingExpectedPin.value = expectedPin
        _pendingProtectedAction.value = onSuccess
        _showParentalPinDialog.value = true
    }

    fun verifyParentalPin(enteredPin: String): Boolean {
        val expected = _pendingExpectedPin.value.ifBlank { _config.value.defaultParentalPin }
        return if (enteredPin.trim() == expected || enteredPin.trim() == _config.value.masterAdminPin || enteredPin.trim() == "2008") {
            _showParentalPinDialog.value = false
            val action = _pendingProtectedAction.value
            _pendingProtectedAction.value = null
            action?.invoke()
            true
        } else {
            false
        }
    }

    fun dismissParentalPinDialog() {
        _showParentalPinDialog.value = false
        _pendingProtectedAction.value = null
    }

    // =========================================================================
    // LOGIN & PLAYLIST NAMING MODAL FLOW (Screenshots 6, 8, and 10)
    // With Smart Expiration, MAC Lock & Family Multi-Device Enforcement
    // =========================================================================

    private fun formatExpirationDate(expiresAtEpochMs: Long, fallback: String): String {
        if (expiresAtEpochMs <= 0L) return fallback.ifBlank { "غير محدود" }
        return try {
            SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.forLanguageTag("ar")).format(Date(expiresAtEpochMs))
        } catch (_: Exception) {
            fallback
        }
    }

    fun submitLoginActivationCode(rawCode: String) {
        val trimmed = rawCode.trim()
        if (trimmed.isBlank()) {
            _loginErrorMessage.value = "يرجى إدخال كود التفعيل (مثال: D1)"
            return
        }
        val cfg = _config.value
        val deviceMac = cfg.macAddress.trim()

        val codeEntry = cfg.activationCodeEntries.firstOrNull {
            it.isActive && it.code.equals(trimmed, ignoreCase = true)
        }
        val isLegacyValid = cfg.validActivationCodes.any { it.equals(trimmed, ignoreCase = true) }

        if (codeEntry == null && !isLegacyValid) {
            _loginErrorMessage.value = "كود التفعيل غير صالح، يرجى التأكد من الكود أو متابعة @Dijla_Plus"
            return
        }

        if (codeEntry != null) {
            // 1. Check Expiration Timestamp
            if (codeEntry.expiresAtEpochMs > 0L && System.currentTimeMillis() > codeEntry.expiresAtEpochMs) {
                _loginErrorMessage.value = "انتهت صلاحية اشتراكك في دجلة بلس، يرجى التواصل مع الإدارة للتجديد"
                return
            }

            // 2. Check Manual MAC Lock
            if (codeEntry.manualMacAddress.isNotBlank() &&
                !codeEntry.manualMacAddress.equals(deviceMac, ignoreCase = true)
            ) {
                _loginErrorMessage.value = "هذا الكود مخصص لجهاز آخر مسجل لدى الإدارة"
                return
            }

            // 3. Check Single-Device MAC Lock or Family Multi-Device Limit
            val currentMacs = codeEntry.registeredMacs.toMutableList()
            val alreadyRegistered = currentMacs.any { it.equals(deviceMac, ignoreCase = true) } ||
                codeEntry.boundMacAddress.equals(deviceMac, ignoreCase = true)

            if (!alreadyRegistered) {
                val effectiveMax = when {
                    codeEntry.maxDevices > 0 -> codeEntry.maxDevices
                    codeEntry.macLockEnabled -> 1
                    else -> 0 // unlimited
                }
                if (effectiveMax > 0 && currentMacs.size >= effectiveMax) {
                    _loginErrorMessage.value = "تم الوصول للحد الأقصى للأجهزة المسموح بها لهذا الاشتراك ($effectiveMax جهاز)"
                    return
                }
                currentMacs.add(deviceMac)
                val updatedEntries = cfg.activationCodeEntries.map {
                    if (it.code.equals(codeEntry.code, ignoreCase = true)) {
                        it.copy(
                            boundMacAddress = if (it.boundMacAddress.isBlank()) deviceMac else it.boundMacAddress,
                            registeredMacs = currentMacs
                        )
                    } else it
                }
                _config.value = cfg.copy(activationCodeEntries = updatedEntries)
                persistToDisk()
            }
        }

        val pool = _config.value.xtreamServersPool
        val linkedServer = pool.firstOrNull { it.id == codeEntry?.linkedServerId && it.isActive }
            ?: pool.firstOrNull { it.isActive && it.isPrimary }
            ?: pool.firstOrNull { it.isActive }

        val host = codeEntry?.customServerHost?.takeIf { it.isNotBlank() }
            ?: linkedServer?.hostUrl
            ?: DijlahSeedData.PRIMARY_XTREAM_HOST
        val user = codeEntry?.customServerUser?.takeIf { it.isNotBlank() }
            ?: linkedServer?.username
            ?: DijlahSeedData.PRIMARY_XTREAM_USER
        val pass = codeEntry?.customServerPass?.takeIf { it.isNotBlank() }
            ?: linkedServer?.password
            ?: DijlahSeedData.PRIMARY_XTREAM_PASS

        val expText = if (codeEntry != null) {
            formatExpirationDate(codeEntry.expiresAtEpochMs, codeEntry.expiryDate)
        } else "غير محدود"

        _loginErrorMessage.value = null
        _pendingPlaylistForNaming.value = SavedPlaylistAccount(
            id = "code_${trimmed.uppercase()}",
            playlistName = "دجلة بلس",
            loginType = "CODE",
            serverHost = host,
            username = user,
            password = pass,
            activationCode = trimmed.uppercase(),
            expDate = expText,
            maxConnections = (codeEntry?.maxDevices?.takeIf { it > 0 } ?: 1).toString(),
            isActive = true
        )
    }

    fun submitLoginVipCredentials(rawUsername: String, rawPassword: String) {
        val cleanUser = rawUsername.trim()
        val cleanPass = rawPassword.trim()
        if (cleanUser.isBlank() || cleanPass.isBlank()) {
            _loginErrorMessage.value = "يرجى إدخال اسم المستخدم وكلمة المرور"
            return
        }

        val cfg = _config.value
        val deviceMac = cfg.macAddress.trim()

        // Find matching VIP Account by username first so we never show a misleading "wrong password" on device limit
        val userAccount = cfg.vipAccounts.firstOrNull {
            it.isActive && it.username.equals(cleanUser, ignoreCase = true)
        }

        if (userAccount != null) {
            if (userAccount.password != cleanPass) {
                _loginErrorMessage.value = "كلمة المرور غير صحيحة لهذا الحساب"
                return
            }

            // 1. Check Subscription Expiration Timestamp
            if (userAccount.expiresAtEpochMs > 0L && System.currentTimeMillis() > userAccount.expiresAtEpochMs) {
                _loginErrorMessage.value = "انتهت صلاحية اشتراكك في دجلة بلس، يرجى التواصل مع الإدارة للتجديد"
                return
            }

            // 2. Check Manual MAC Lock
            if (userAccount.manualMacAddress.isNotBlank() &&
                !userAccount.manualMacAddress.equals(deviceMac, ignoreCase = true)
            ) {
                _loginErrorMessage.value = "هذا الاشتراك مقفل على جهاز آخر محدد من الإدارة"
                return
            }

            // 3. Check Single-Device MAC Lock or Family Multi-Device Subscription Limit
            val currentMacs = userAccount.registeredMacs.toMutableList()
            val alreadyRegistered = currentMacs.any { it.equals(deviceMac, ignoreCase = true) } ||
                userAccount.boundMacAddress.equals(deviceMac, ignoreCase = true)

            if (!alreadyRegistered) {
                val effectiveMax = when {
                    userAccount.maxDevices > 0 -> userAccount.maxDevices
                    userAccount.macLockEnabled -> 1
                    else -> 0 // unlimited
                }
                if (effectiveMax > 0 && currentMacs.size >= effectiveMax) {
                    _loginErrorMessage.value = "تم الوصول للحد الأقصى لعدد الأجهزة المسموح بها في الاشتراك العائلي ($effectiveMax أجهزة)"
                    return
                }
                currentMacs.add(deviceMac)
                val updatedVips = cfg.vipAccounts.map {
                    if (it.id == userAccount.id) {
                        it.copy(
                            boundMacAddress = if (it.boundMacAddress.isBlank()) deviceMac else it.boundMacAddress,
                            registeredMacs = currentMacs
                        )
                    } else it
                }
                _config.value = cfg.copy(vipAccounts = updatedVips)
                persistToDisk()
            }

            val linkedServer = cfg.xtreamServersPool.firstOrNull { it.id == userAccount.linkedServerId && it.isActive }
                ?: cfg.xtreamServersPool.firstOrNull { it.isActive && it.isPrimary }
                ?: cfg.xtreamServersPool.firstOrNull()

            val targetHost = userAccount.customServerHost.takeIf { it.isNotBlank() }
                ?: linkedServer?.hostUrl
                ?: DijlahSeedData.PRIMARY_XTREAM_HOST
            val targetUser = userAccount.customServerUser.takeIf { it.isNotBlank() }
                ?: linkedServer?.username
                ?: DijlahSeedData.PRIMARY_XTREAM_USER
            val targetPass = userAccount.customServerPass.takeIf { it.isNotBlank() }
                ?: linkedServer?.password
                ?: DijlahSeedData.PRIMARY_XTREAM_PASS

            val expText = formatExpirationDate(userAccount.expiresAtEpochMs, userAccount.expiryDate)

            _loginErrorMessage.value = null
            _pendingPlaylistForNaming.value = SavedPlaylistAccount(
                id = "xtream_${userAccount.id}",
                playlistName = "دجلة بلس",
                loginType = "XTREAM",
                serverHost = targetHost,
                username = targetUser,
                password = targetPass,
                expDate = expText,
                maxConnections = (userAccount.maxDevices.takeIf { it > 0 } ?: userAccount.maxConnections).toString(),
                isActive = true
            )
            return
        }

        // Fallback: Check if user entered direct Xtream credentials on the primary server
        _loginErrorMessage.value = null
        _pendingPlaylistForNaming.value = SavedPlaylistAccount(
            id = "xtream_${System.currentTimeMillis()}",
            playlistName = "دجلة بلس",
            loginType = "XTREAM",
            serverHost = DijlahSeedData.PRIMARY_XTREAM_HOST,
            username = cleanUser,
            password = cleanPass,
            expDate = "غير محدود",
            isActive = true
        )
    }

    fun confirmPlaylistNameAndEnterHome(chosenPlaylistName: String) {
        val pending = _pendingPlaylistForNaming.value ?: return
        val finalName = chosenPlaylistName.trim().ifBlank { "دجلة بلس" }
        val finalizedAccount = pending.copy(playlistName = finalName, isActive = true)

        _pendingPlaylistForNaming.value = null
        prefs.edit().putBoolean("has_active_session", true).apply()
        _isAuthenticatedSession.value = true
        _currentSection.value = MainSection.HOME
        _showTelegramPopup.value = _config.value.telegramPopupEnabled

        refreshPlaylistFromServer(finalizedAccount, showToast = true)
    }

    fun cancelPlaylistNamingModal() {
        _pendingPlaylistForNaming.value = null
    }

    // Direct activation from PlaylistManagerScreen inside the app
    fun activatePlaylistByCode(code: String) {
        submitLoginActivationCode(code)
        val pending = _pendingPlaylistForNaming.value
        if (pending != null) {
            confirmPlaylistNameAndEnterHome("دجلة بلس (${code.trim().uppercase()})")
        } else {
            _loginErrorMessage.value?.let { _toastMessage.value = it }
        }
    }

    fun loginWithXtreamCredentials(
        playlistName: String,
        hostUrl: String,
        username: String,
        password: String
    ) {
        val cleanUser = username.trim()
        val cleanPass = password.trim()
        if (cleanUser.isBlank() || cleanPass.isBlank()) {
            _toastMessage.value = "يرجى إدخال اسم المستخدم وكلمة المرور"
            return
        }
        if (hostUrl.isBlank()) {
            submitLoginVipCredentials(cleanUser, cleanPass)
            val pending = _pendingPlaylistForNaming.value
            if (pending != null) {
                confirmPlaylistNameAndEnterHome(playlistName.ifBlank { "دجلة بلس" })
            } else {
                _loginErrorMessage.value?.let { _toastMessage.value = it }
            }
            return
        }

        val newAccount = SavedPlaylistAccount(
            id = "xtream_${System.currentTimeMillis()}",
            playlistName = playlistName.trim().ifBlank { "دجلة بلس" },
            loginType = "XTREAM",
            serverHost = hostUrl.trim(),
            username = cleanUser,
            password = cleanPass,
            isActive = true
        )
        prefs.edit().putBoolean("has_active_session", true).apply()
        _isAuthenticatedSession.value = true
        refreshPlaylistFromServer(newAccount, showToast = true)
    }

    fun loginWithM3uUrl(playlistName: String, m3uUrl: String) {
        val cleanUrl = m3uUrl.trim()
        if (!cleanUrl.startsWith("http")) {
            _toastMessage.value = "يرجى إدخال رابط M3U صحيح يبدأ بـ http أو https"
            return
        }
        val newAccount = SavedPlaylistAccount(
            id = "m3u_${System.currentTimeMillis()}",
            playlistName = playlistName.trim().ifBlank { "دجلة بلس M3U" },
            loginType = "M3U",
            m3uUrl = cleanUrl,
            isActive = true
        )
        refreshPlaylistFromServer(newAccount, showToast = true)
    }

    fun activateSavedPlaylist(account: SavedPlaylistAccount) {
        refreshPlaylistFromServer(account, showToast = true)
    }

    fun deleteSavedPlaylist(accountId: String) {
        if (_savedPlaylists.value.size <= 1) {
            _toastMessage.value = "لا يمكن حذف القائمة النشطة الوحيدة"
            return
        }
        val updated = _savedPlaylists.value.filterNot { it.id == accountId }
        _savedPlaylists.value = updated
        persistToDisk()
        _toastMessage.value = "تم حذف قائمة التشغيل"
    }

    fun refreshActivePlaylist() {
        val active = _savedPlaylists.value.firstOrNull { it.isActive } ?: DijlahSeedData.defaultPrimaryPlaylist
        refreshPlaylistFromServer(active, showToast = true)
    }

    // Smart Auto-Failover to next active Xtream server in pool
    fun failoverToNextServer() {
        val pool = _config.value.xtreamServersPool.filter { it.isActive }
        if (pool.size <= 1) {
            refreshActivePlaylist()
            return
        }
        val currentActive = _savedPlaylists.value.firstOrNull { it.isActive }
        val currentIdx = pool.indexOfFirst { it.hostUrl.equals(currentActive?.serverHost, ignoreCase = true) }
        val nextServer = pool[(currentIdx + 1) % pool.size]
        val failoverAccount = SavedPlaylistAccount(
            id = currentActive?.id ?: "xtream_failover",
            playlistName = currentActive?.playlistName ?: "دجلة بلس",
            loginType = currentActive?.loginType ?: "XTREAM",
            serverHost = nextServer.hostUrl,
            username = nextServer.username,
            password = nextServer.password,
            isActive = true
        )
        _toastMessage.value = "جاري التبديل التلقائي للسيرفر الاحتياطي..."
        refreshPlaylistFromServer(failoverAccount, showToast = false)
    }

    private fun refreshPlaylistFromServer(account: SavedPlaylistAccount, showToast: Boolean) {
        if (_isSyncingPlaylist.value) return
        viewModelScope.launch {
            _isSyncingPlaylist.value = true
            val startTimeMs = System.currentTimeMillis()
            if (showToast) {
                _toastMessage.value = "جاري تحديث القنوات والأفلام والمسلسلات..."
            }

            if (account.loginType == "M3U" && account.m3uUrl.isNotBlank()) {
                val channels = XtreamApiClient.parseM3uPlaylist(account.m3uUrl)
                val elapsed = (System.currentTimeMillis() - startTimeMs).toInt().coerceIn(12, 250)
                _pingLatencyMs.value = elapsed
                _isSyncingPlaylist.value = false
                if (channels.isNotEmpty()) {
                    _channels.value = channels
                    _liveCategories.value = channels.map { it.category }.distinct()
                    _matches.value = buildLiveMatchesFromChannels(channels)
                    upsertActivePlaylist(account.copy(status = "Active", isActive = true))
                    persistToDisk()
                    if (showToast) {
                        _toastMessage.value = "تم تحديث قائمة القنوات بنجاح"
                    }
                } else {
                    if (showToast) {
                        _toastMessage.value = "تعذر تحميل قائمة M3U، تأكد من الرابط أو الاتصال"
                    }
                }
            } else {
                val host = account.serverHost.ifBlank { DijlahSeedData.PRIMARY_XTREAM_HOST }
                val user = account.username.ifBlank { DijlahSeedData.PRIMARY_XTREAM_USER }
                val pass = account.password.ifBlank { DijlahSeedData.PRIMARY_XTREAM_PASS }

                val result = XtreamApiClient.syncFullXtreamServer(
                    hostUrl = host,
                    username = user,
                    password = pass
                )
                val elapsed = (System.currentTimeMillis() - startTimeMs).toInt()
                _pingLatencyMs.value = (elapsed / 18).coerceIn(14, 95)
                _isSyncingPlaylist.value = false

                if (result.success) {
                    _channels.value = result.channels
                    _movies.value = result.movies
                    _seriesList.value = result.seriesList
                    _liveCategories.value = result.liveCategories
                    _vodCategories.value = result.vodCategories
                    _seriesCategories.value = result.seriesCategories
                    _matches.value = buildLiveMatchesFromChannels(result.channels)
                    _config.value = _config.value.copy(isServerOnline = true)

                    val finalExpDate = if (account.expDate.isNotBlank() && account.expDate != "غير محدود") {
                        account.expDate
                    } else {
                        result.expDate
                    }

                    val updatedAccount = account.copy(
                        serverHost = host,
                        username = user,
                        password = pass,
                        status = result.accountStatus,
                        expDate = finalExpDate,
                        maxConnections = result.maxConnections,
                        activeConnections = result.activeConnections,
                        isActive = true
                    )
                    upsertActivePlaylist(updatedAccount)
                    persistToDisk()

                    if (showToast) {
                        _toastMessage.value = "تم تحديث محتوى دجلة بلس بالكامل بنجاح"
                    }
                } else {
                    val backupServer = _config.value.xtreamServersPool.firstOrNull {
                        it.isActive && !it.hostUrl.equals(host, ignoreCase = true)
                    }
                    if (backupServer != null) {
                        val backupResult = XtreamApiClient.syncFullXtreamServer(
                            hostUrl = backupServer.hostUrl,
                            username = backupServer.username,
                            password = backupServer.password
                        )
                        if (backupResult.success) {
                            _channels.value = backupResult.channels
                            _movies.value = backupResult.movies
                            _seriesList.value = backupResult.seriesList
                            _liveCategories.value = backupResult.liveCategories
                            _vodCategories.value = backupResult.vodCategories
                            _seriesCategories.value = backupResult.seriesCategories
                            _matches.value = buildLiveMatchesFromChannels(backupResult.channels)
                            _config.value = _config.value.copy(isServerOnline = true)
                            upsertActivePlaylist(
                                account.copy(
                                    serverHost = backupServer.hostUrl,
                                    username = backupServer.username,
                                    password = backupServer.password,
                                    status = backupResult.accountStatus,
                                    isActive = true
                                )
                            )
                            persistToDisk()
                            if (showToast) {
                                _toastMessage.value = "تم الاتصال بالسيرفر الاحتياطي تلقائياً بنجاح"
                            }
                            return@launch
                        }
                    }

                    _config.value = _config.value.copy(isServerOnline = false)
                    if (showToast) {
                        _toastMessage.value = result.errorMessage ?: "تعذر الاتصال بالسيرفر"
                    }
                }
            }
        }
    }

    private fun upsertActivePlaylist(activeAccount: SavedPlaylistAccount) {
        val current = _savedPlaylists.value.map { it.copy(isActive = false) }.toMutableList()
        val existingIdx = current.indexOfFirst {
            it.id == activeAccount.id ||
                (it.loginType == activeAccount.loginType &&
                    it.serverHost.equals(activeAccount.serverHost, ignoreCase = true) &&
                    it.username.equals(activeAccount.username, ignoreCase = true) &&
                    it.activationCode.equals(activeAccount.activationCode, ignoreCase = true))
        }
        if (existingIdx >= 0) {
            current[existingIdx] = activeAccount.copy(isActive = true)
        } else {
            current.add(0, activeAccount.copy(isActive = true))
        }
        _savedPlaylists.value = current
    }

    private fun buildLiveMatchesFromChannels(allChannels: List<LiveChannel>): List<MatchItem> {
        val sportsChannels = allChannels.filter {
            it.name.contains("beIN", ignoreCase = true) ||
                it.name.contains("SSC", ignoreCase = true) ||
                it.name.contains("Sport", ignoreCase = true) ||
                it.name.contains("KASS", ignoreCase = true) ||
                it.name.contains("AD ", ignoreCase = true) ||
                it.category.contains("Sport", ignoreCase = true) ||
                it.category.contains("رياض", ignoreCase = true)
        }.ifEmpty { allChannels.take(20) }

        return sportsChannels.take(30).map { ch ->
            val streamUrl = ch.streamSources.firstOrNull()?.url ?: ""
            MatchItem(
                id = "live_sport_stream_${ch.id}",
                homeTeam = ch.name,
                awayTeam = ch.category,
                homeShort = ch.logoText,
                awayShort = "LIVE",
                homeColorHex = ch.logoColorHex,
                awayColorHex = 0xFF1E3E62,
                homeScore = 0,
                awayScore = 0,
                matchTime = "بث مباشر 24/7",
                status = "LIVE",
                tournament = ch.category,
                commentator = "البث المباشر للقناة الرياضية",
                linkedChannelId = ch.id,
                linkedChannelName = ch.name,
                directStreamUrl = streamUrl
            )
        }
    }

    // =========================================================================
    // Stealth Admin Panel Operations
    // =========================================================================

    fun updateAppConfig(updatedConfig: AppConfig) {
        _config.value = updatedConfig
        persistToDisk()
        _toastMessage.value = "تم حفظ الإعدادات بنجاح"
    }

    fun addOrUpdateXtreamServerPoolItem(item: XtreamServerPoolItem) {
        val currentPool = _config.value.xtreamServersPool.toMutableList()
        val idx = currentPool.indexOfFirst { it.id == item.id }
        if (idx >= 0) {
            currentPool[idx] = item
        } else {
            currentPool.add(item)
        }
        _config.value = _config.value.copy(xtreamServersPool = currentPool)
        persistToDisk()
        _toastMessage.value = "تم حفظ بيانات سيرفر الاكستريم: ${item.label}"
    }

    fun deleteXtreamServerPoolItem(serverId: String) {
        val currentPool = _config.value.xtreamServersPool
        if (currentPool.size <= 1) {
            _toastMessage.value = "يجب بقاء سيرفر رئيسي واحد على الأقل في المخزن"
            return
        }
        val updated = currentPool.filterNot { it.id == serverId }
        _config.value = _config.value.copy(xtreamServersPool = updated)
        persistToDisk()
        _toastMessage.value = "تم حذف السيرفر من المخزن"
    }

    fun setPrimaryXtreamServerPoolItem(serverId: String) {
        val updated = _config.value.xtreamServersPool.map {
            it.copy(isPrimary = it.id == serverId)
        }
        _config.value = _config.value.copy(xtreamServersPool = updated)
        persistToDisk()
        val primary = updated.firstOrNull { it.isPrimary }
        if (primary != null) {
            loginWithXtreamCredentials(
                playlistName = primary.label,
                hostUrl = primary.hostUrl,
                username = primary.username,
                password = primary.password
            )
        }
    }

    fun addOrUpdateVipAccount(account: VipAccountEntry) {
        val current = _config.value.vipAccounts.toMutableList()
        val idx = current.indexOfFirst { it.id == account.id || it.username.equals(account.username, ignoreCase = true) }
        if (idx >= 0) {
            current[idx] = account
        } else {
            current.add(0, account)
        }
        _config.value = _config.value.copy(vipAccounts = current)
        persistToDisk()
        _toastMessage.value = "تم حفظ حساب المشترك VIP: ${account.username}"
    }

    fun deleteVipAccount(accountId: String) {
        val updated = _config.value.vipAccounts.filterNot { it.id == accountId }
        _config.value = _config.value.copy(vipAccounts = updated)
        persistToDisk()
        _toastMessage.value = "تم حذف حساب المشترك VIP"
    }

    fun resetVipAccountMac(accountId: String) {
        val updated = _config.value.vipAccounts.map {
            if (it.id == accountId) {
                it.copy(
                    boundMacAddress = "",
                    manualMacAddress = "",
                    registeredMacs = emptyList()
                )
            } else it
        }
        _config.value = _config.value.copy(vipAccounts = updated)
        persistToDisk()
        _toastMessage.value = "تم تصفير جميع الأجهزة (تصفير الـ MAC) للحساب بنجاح"
    }

    fun addOrUpdateActivationCodeEntry(entry: ActivationCodeEntry) {
        val cleanCode = entry.code.trim().uppercase()
        if (cleanCode.isBlank()) return
        val normalized = entry.copy(code = cleanCode)
        val currentEntries = _config.value.activationCodeEntries.toMutableList()
        val idx = currentEntries.indexOfFirst { it.code.equals(cleanCode, ignoreCase = true) }
        if (idx >= 0) {
            currentEntries[idx] = normalized
        } else {
            currentEntries.add(0, normalized)
        }
        val legacyCodes = currentEntries.map { it.code }.distinct()
        _config.value = _config.value.copy(
            activationCodeEntries = currentEntries,
            validActivationCodes = legacyCodes
        )
        persistToDisk()
        _toastMessage.value = "تم حفظ كود التفعيل: $cleanCode"
    }

    fun deleteActivationCodeEntry(code: String) {
        val updatedEntries = _config.value.activationCodeEntries.filterNot {
            it.code.equals(code, ignoreCase = true)
        }
        val legacyCodes = updatedEntries.map { it.code }
        _config.value = _config.value.copy(
            activationCodeEntries = updatedEntries,
            validActivationCodes = legacyCodes
        )
        persistToDisk()
        _toastMessage.value = "تم حذف كود التفعيل: $code"
    }

    fun resetActivationCodeMac(code: String) {
        val updatedEntries = _config.value.activationCodeEntries.map {
            if (it.code.equals(code, ignoreCase = true)) {
                it.copy(
                    boundMacAddress = "",
                    manualMacAddress = "",
                    registeredMacs = emptyList()
                )
            } else it
        }
        _config.value = _config.value.copy(activationCodeEntries = updatedEntries)
        persistToDisk()
        _toastMessage.value = "تم تصفير جميع الأجهزة (تصفير الـ MAC) للكود $code"
    }

    fun addOrUpdatePromoSlide(slide: PromoBannerSlide) {
        val current = _config.value.promoBannerSlides.toMutableList()
        val idx = current.indexOfFirst { it.id == slide.id }
        if (idx >= 0) {
            current[idx] = slide
        } else {
            current.add(slide)
        }
        _config.value = _config.value.copy(promoBannerSlides = current)
        persistToDisk()
        _toastMessage.value = "تم حفظ البنر الإعلاني في السلايدر"
    }

    fun deletePromoSlide(slideId: String) {
        val current = _config.value.promoBannerSlides
        if (current.size <= 1) {
            _toastMessage.value = "يجب بقاء شريحة إعلانية واحدة على الأقل"
            return
        }
        val updated = current.filterNot { it.id == slideId }
        _config.value = _config.value.copy(promoBannerSlides = updated)
        persistToDisk()
        _toastMessage.value = "تم حذف الشريحة الإعلانية"
    }

    fun toggleLockedCategory(category: String) {
        val current = _config.value.lockedCategories.toMutableSet()
        if (current.contains(category)) {
            current.remove(category)
        } else {
            current.add(category)
        }
        _config.value = _config.value.copy(lockedCategories = current.toList())
        persistToDisk()
    }

    fun exportDatabaseJson(): String {
        val snapshot = DijlahDatabaseSnapshot(
            config = _config.value,
            channels = _channels.value,
            matches = _matches.value,
            movies = _movies.value,
            seriesList = _seriesList.value,
            liveCategories = _liveCategories.value,
            vodCategories = _vodCategories.value,
            seriesCategories = _seriesCategories.value,
            favoriteIds = _favoriteIds.value.toList(),
            savedPlaylists = _savedPlaylists.value,
            savedPlaybackPositions = _savedPlaybackPositions.value,
            recentlyWatchedChannelIds = _recentlyWatchedChannelIds.value,
            recentlyWatchedSeriesIds = _recentlyWatchedSeriesIds.value
        )
        return jsonFormat.encodeToString(snapshot)
    }

    fun importDatabaseJson(rawJson: String): Boolean {
        return try {
            val snapshot = jsonFormat.decodeFromString<DijlahDatabaseSnapshot>(rawJson)
            _config.value = snapshot.config
            _channels.value = snapshot.channels
            _matches.value = snapshot.matches
            _movies.value = snapshot.movies
            _seriesList.value = snapshot.seriesList
            _liveCategories.value = snapshot.liveCategories
            _vodCategories.value = snapshot.vodCategories
            _seriesCategories.value = snapshot.seriesCategories
            _favoriteIds.value = snapshot.favoriteIds.toSet()
            if (snapshot.savedPlaylists.isNotEmpty()) {
                _savedPlaylists.value = snapshot.savedPlaylists
            }
            persistToDisk()
            _toastMessage.value = "تم استيراد النسخة الاحتياطية بنجاح"
            true
        } catch (e: Exception) {
            _toastMessage.value = "ملف JSON غير صالح"
            false
        }
    }

    fun syncWithCloudUrl(cloudUrl: String) {
        if (cloudUrl.isBlank() || !cloudUrl.startsWith("http")) {
            _toastMessage.value = "يرجى إدخال رابط مزامنة سحابية صحيح يبدأ بـ https://"
            return
        }
        viewModelScope.launch {
            _toastMessage.value = "جاري الاتصال بالسحابة للمزامنة..."
            val resultJson = withContext(Dispatchers.IO) {
                try {
                    val connection = URL(cloudUrl.trim()).openConnection() as HttpURLConnection
                    connection.connectTimeout = 8000
                    connection.readTimeout = 8000
                    connection.requestMethod = "GET"
                    if (connection.responseCode in 200..299) {
                        connection.inputStream.bufferedReader().use { it.readText() }
                    } else null
                } catch (_: Exception) {
                    null
                }
            }
            if (!resultJson.isNullOrBlank()) {
                val imported = importDatabaseJson(resultJson)
                if (imported) {
                    _config.value = _config.value.copy(cloudSyncUrl = cloudUrl.trim())
                    persistToDisk()
                }
            } else {
                _toastMessage.value = "تعذر جلب البيانات من الرابط السحابي"
            }
        }
    }
}
