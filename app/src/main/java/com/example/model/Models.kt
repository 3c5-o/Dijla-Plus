package com.example.model

import kotlinx.serialization.Serializable

@Serializable
enum class MainSection(val titleAr: String) {
    HOME("الرئيسية"),
    MATCHES("جدول المباريات"),
    LIVE_TV("البث المباشر"),
    MOVIES("الأفلام"),
    SERIES("المسلسلات"),
    FAVORITES("قائمتي"),
    SETTINGS("الإعدادات"),
    PLAYLIST_MANAGER("تغيير قوائم التشغيل")
}

@Serializable
data class StreamSource(
    val id: String,
    val label: String,
    val url: String,
    val quality: String = "FHD",
    val isBackup: Boolean = false
)

@Serializable
data class LiveChannel(
    val id: String,
    val name: String,
    val category: String,
    val logoText: String,
    val logoColorHex: Long = 0xFFD4AF37,
    val streamIconUrl: String = "",
    val currentProgram: String = "بث مباشر",
    val qualityBadge: String = "FHD",
    val isEncrypted: Boolean = false,
    val isAdult: Boolean = false,
    val customPin: String = "",
    val streamSources: List<StreamSource> = emptyList(),
    val channelNumber: Int = 1
)

@Serializable
data class MatchItem(
    val id: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeShort: String,
    val awayShort: String,
    val homeColorHex: Long = 0xFF1E3E62,
    val awayColorHex: Long = 0xFF8C5A2B,
    val homeScore: Int = 0,
    val awayScore: Int = 0,
    val matchTime: String,
    val status: String, // "LIVE", "UPCOMING", "FINISHED"
    val tournament: String,
    val commentator: String,
    val linkedChannelId: String,
    val linkedChannelName: String,
    val directStreamUrl: String = ""
)

@Serializable
data class VodMovie(
    val id: String,
    val title: String,
    val year: Int,
    val rating: String,
    val category: String,
    val duration: String,
    val description: String,
    val qualityBadge: String = "4K UHD",
    val isAdult: Boolean = false,
    val posterColorHex: Long = 0xFF1E3E62,
    val customPosterUri: String? = null,
    val streamUrl: String,
    val streamId: String = "",
    val containerExtension: String = "mp4"
)

@Serializable
data class SeriesEpisode(
    val id: String,
    val episodeNumber: Int,
    val title: String,
    val duration: String,
    val streamUrl: String
)

@Serializable
data class SeriesSeason(
    val seasonNumber: Int,
    val title: String,
    val episodes: List<SeriesEpisode>
)

@Serializable
data class VodSeries(
    val id: String,
    val title: String,
    val year: Int,
    val rating: String,
    val category: String,
    val description: String,
    val qualityBadge: String = "1080p",
    val isAdult: Boolean = false,
    val posterColorHex: Long = 0xFF0B192C,
    val customPosterUri: String? = null,
    val seasons: List<SeriesSeason> = emptyList(),
    val seriesId: String = ""
)

@Serializable
data class XtreamServerPoolItem(
    val id: String,
    val label: String,
    val hostUrl: String,
    val username: String,
    val password: String,
    val isActive: Boolean = true,
    val isPrimary: Boolean = false
)

@Serializable
data class VipAccountEntry(
    val id: String,
    val username: String,
    val password: String,
    val subscriberName: String = "مشترك VIP",
    val expiryDate: String = "غير محدود",
    val maxConnections: Int = 1,
    val linkedServerId: String = "srv_foxbleu_primary",
    val macLockEnabled: Boolean = false,
    val boundMacAddress: String = "",
    val isActive: Boolean = true
)

@Serializable
data class ActivationCodeEntry(
    val code: String,
    val label: String = "كود تفعيل VIP",
    val expiryDate: String = "غير محدود",
    val linkedServerId: String = "srv_foxbleu_primary",
    val macLockEnabled: Boolean = false,
    val boundMacAddress: String = "",
    val isActive: Boolean = true
)

@Serializable
data class PromoBannerSlide(
    val id: String,
    val title: String,
    val subtitle: String,
    val badgeText: String = "حصرياً",
    val imageUrl: String = "",
    val targetAction: String = "LIVE_TV", // LIVE_TV, MOVIES, SERIES, MATCHES, TELEGRAM
    val accentHex: Long = 0xFFD4AF37
)

@Serializable
data class AppConfig(
    val appName: String = "دجلة بلس | Dijla Plus",
    val slogan: String = "البث المباشر والأفلام والمسلسلات بأعلى جودة",
    val isServerOnline: Boolean = true,
    val telegramHandle: String = "@Dijla_Plus",
    val telegramUrl: String = "https://t.me/Dijla_Plus",
    val telegramPopupEnabled: Boolean = true,
    val telegramPopupTitle: String = "قناة دجلة بلس الرسمية",
    val telegramPopupMessage: String = "اشترك في قناة دجلة بلس الرسمية على تلغرام للحصول على أحدث الأكواد والتحديثات ومتابعة المباريات العالمية.",
    val liveBroadcastBanner: String = "",
    val adBannerTitle: String = "دجلة بلس | Dijla Plus VIP",
    val adBannerSubtitle: String = "مشاهدة ممتعة لجميع القنوات الرياضية والترفيهية ومكتبة الأفلام والمسلسلات المحدثة يومياً",
    val adBannerBadge: String = "بث فائق السرعة",
    val adSliderIntervalMs: Long = 3000L,
    val promoBannerSlides: List<PromoBannerSlide> = listOf(
        PromoBannerSlide(
            id = "slide_1",
            title = "باقة الرياضة العالمية المباشرة",
            subtitle = "تابع أقوى الدوريات والبطولات الأوروبية والعربية بجودة FHD/4K بدون تقطيع",
            badgeText = "بث مباشر VIP",
            targetAction = "LIVE_TV",
            accentHex = 0xFFD4AF37
        ),
        PromoBannerSlide(
            id = "slide_2",
            title = "مكتبة الأفلام والمسلسلات الحصرية",
            subtitle = "أحدث الإنتاجات العربية والعالمية مع خاصية استكمال المشاهدة التلقائي",
            badgeText = "مكتبة VOD",
            targetAction = "MOVIES",
            accentHex = 0xFF00D2FF
        ),
        PromoBannerSlide(
            id = "slide_3",
            title = "قناة دجلة بلس الرسمية على تلغرام",
            subtitle = "انضم الآن للحصول على التحديثات الفورية وأكواد التفعيل والدعم الفني المباشر",
            badgeText = "@Dijla_Plus",
            targetAction = "TELEGRAM",
            accentHex = 0xFFF59E0B
        )
    ),
    val defaultParentalPin: String = "0000",
    val masterAdminPin: String = "2008",
    val secretSearchCode: String = "#*2008*#",
    val lockAdultContent: Boolean = true,
    val lockedCategories: List<String> = emptyList(),
    val cloudSyncUrl: String = "",
    val macAddress: String = "24:43:dd:c9:07:47",
    val validActivationCodes: List<String> = listOf("1", "D1", "B1"),
    val activationCodeEntries: List<ActivationCodeEntry> = listOf(
        ActivationCodeEntry(
            code = "1",
            label = "كود دجلة السريع (1)",
            expiryDate = "غير محدود",
            linkedServerId = "srv_foxbleu_primary",
            macLockEnabled = false
        ),
        ActivationCodeEntry(
            code = "D1",
            label = "كود باقة دجلة الذهبية (D1)",
            expiryDate = "غير محدود",
            linkedServerId = "srv_foxbleu_primary",
            macLockEnabled = false
        ),
        ActivationCodeEntry(
            code = "B1",
            label = "كود الباقة الملكية (B1)",
            expiryDate = "غير محدود",
            linkedServerId = "srv_foxbleu_primary",
            macLockEnabled = false
        )
    ),
    val vipAccounts: List<VipAccountEntry> = listOf(
        VipAccountEntry(
            id = "vip_default_1",
            username = "vip1",
            password = "2008",
            subscriberName = "مشترك دجلة بلس الملكي",
            expiryDate = "غير محدود",
            maxConnections = 1,
            linkedServerId = "srv_foxbleu_primary",
            macLockEnabled = false
        ),
        VipAccountEntry(
            id = "vip_ludovic",
            username = "ludovic",
            password = "8333",
            subscriberName = "الحساب الرئيسي VIP",
            expiryDate = "غير محدود",
            maxConnections = 1,
            linkedServerId = "srv_foxbleu_primary",
            macLockEnabled = false
        )
    ),
    val xtreamServersPool: List<XtreamServerPoolItem> = listOf(
        XtreamServerPoolItem(
            id = "srv_foxbleu_primary",
            label = "سيرفر دجلة بلس الرئيسي 1",
            hostUrl = "http://foxbleu.org:8789",
            username = "ludovic",
            password = "8333",
            isActive = true,
            isPrimary = true
        )
    )
)

@Serializable
data class SavedPlaylistAccount(
    val id: String,
    val playlistName: String,
    val loginType: String, // "XTREAM", "M3U", "CODE"
    val serverHost: String = "",
    val username: String = "",
    val password: String = "",
    val m3uUrl: String = "",
    val activationCode: String = "",
    val status: String = "Active",
    val expDate: String = "غير محدود",
    val maxConnections: String = "1",
    val activeConnections: String = "0",
    val isActive: Boolean = true
)

@Serializable
data class DijlahDatabaseSnapshot(
    val config: AppConfig,
    val channels: List<LiveChannel>,
    val matches: List<MatchItem>,
    val movies: List<VodMovie>,
    val seriesList: List<VodSeries>,
    val favoriteIds: List<String> = emptyList(),
    val savedPlaylists: List<SavedPlaylistAccount> = emptyList(),
    val savedPlaybackPositions: Map<String, Long> = emptyMap()
)

sealed class ActivePlayerMedia {
    data class ChannelMedia(val channel: LiveChannel) : ActivePlayerMedia()
    data class MatchMedia(val match: MatchItem, val channel: LiveChannel?) : ActivePlayerMedia()
    data class MovieMedia(val movie: VodMovie) : ActivePlayerMedia()
    data class EpisodeMedia(val series: VodSeries, val season: SeriesSeason, val episode: SeriesEpisode) : ActivePlayerMedia()
}
