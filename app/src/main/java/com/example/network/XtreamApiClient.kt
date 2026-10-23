package com.example.network

import com.example.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

data class XtreamSyncResult(
    val success: Boolean,
    val accountStatus: String = "Active",
    val expDate: String = "غير محدود",
    val maxConnections: String = "1",
    val activeConnections: String = "0",
    val liveCategories: List<String> = emptyList(),
    val vodCategories: List<String> = emptyList(),
    val seriesCategories: List<String> = emptyList(),
    val channels: List<LiveChannel> = emptyList(),
    val movies: List<VodMovie> = emptyList(),
    val seriesList: List<VodSeries> = emptyList(),
    val errorMessage: String? = null
)

object XtreamApiClient {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(18, TimeUnit.SECONDS)
        .readTimeout(35, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    private fun normalizeHost(hostUrl: String): String {
        val trimmed = hostUrl.trim().trimEnd('/')
        return if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            "http://$trimmed"
        } else {
            trimmed
        }
    }

    private fun JsonObject.str(key: String, default: String = ""): String {
        val el = this[key] ?: return default
        return (el as? JsonPrimitive)?.contentOrNull?.takeIf { it != "null" } ?: default
    }

    private fun formatEpochSeconds(raw: String): String {
        val sec = raw.toLongOrNull() ?: return "غير محدود"
        if (sec <= 0L) return "غير محدود"
        return try {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(sec * 1000L))
        } catch (_: Exception) {
            "غير محدود"
        }
    }

    private fun isDecorativeSeparator(name: String): Boolean {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return true
        if (trimmed.contains("●✬") || trimmed.contains("✬●") || trimmed.contains("-•")) return true
        if (trimmed.startsWith("###") || trimmed.startsWith("===") || trimmed.startsWith("***") || trimmed.startsWith("---")) return true
        if (trimmed.endsWith("###") || trimmed.endsWith("===") || trimmed.endsWith("***") || trimmed.endsWith("---")) return true
        return false
    }

    private suspend fun fetchJsonText(url: String): String? = withContext(Dispatchers.IO) {
        val encodedTarget = try {
            URLEncoder.encode(url, "UTF-8")
        } catch (_: Exception) {
            ""
        }
        val candidates = buildList {
            add(url)
            if (encodedTarget.isNotBlank()) {
                add("https://corsproxy.io/?$encodedTarget")
                add("https://api.allorigins.win/raw?url=$encodedTarget")
            }
        }
        for (candidate in candidates) {
            try {
                val req = Request.Builder()
                    .url(candidate)
                    .header("User-Agent", "IBOProPlayer/3.9 (Android; Linux)")
                    .header("Accept", "application/json, */*")
                    .get()
                    .build()
                okHttpClient.newCall(req).execute().use { resp ->
                    if (resp.isSuccessful) {
                        val body = resp.body?.string()?.trim().orEmpty()
                        if (body.startsWith("{") || body.startsWith("[")) {
                            return@withContext body
                        }
                    }
                }
            } catch (_: Exception) {
            }
        }
        null
    }

    suspend fun syncFullXtreamServer(
        hostUrl: String,
        username: String,
        password: String
    ): XtreamSyncResult = withContext(Dispatchers.IO) {
        try {
            val baseHost = normalizeHost(hostUrl)
            val user = username.trim()
            val pass = password.trim()
            val uEnc = URLEncoder.encode(user, "UTF-8")
            val pEnc = URLEncoder.encode(pass, "UTF-8")
            val baseApi = "$baseHost/player_api.php?username=$uEnc&password=$pEnc"

            coroutineScope {
                val authDef = async { fetchJsonText(baseApi) }
                val liveCatDef = async { fetchJsonText("$baseApi&action=get_live_categories") }
                val vodCatDef = async { fetchJsonText("$baseApi&action=get_vod_categories") }
                val serCatDef = async { fetchJsonText("$baseApi&action=get_series_categories") }
                val liveStreamsDef = async { fetchJsonText("$baseApi&action=get_live_streams") }
                val vodStreamsDef = async { fetchJsonText("$baseApi&action=get_vod_streams") }
                val seriesDef = async { fetchJsonText("$baseApi&action=get_series") }

                val authJson = authDef.await()
                var status = "Active"
                var expDate = "غير محدود"
                var maxCons = "1"
                var activeCons = "0"

                if (!authJson.isNullOrBlank()) {
                    try {
                        val root = json.parseToJsonElement(authJson).jsonObject
                        val userInfo = root["user_info"]?.jsonObject
                        if (userInfo != null) {
                            val authVal = userInfo.str("auth", "1")
                            if (authVal == "0") {
                                return@coroutineScope XtreamSyncResult(
                                    success = false,
                                    errorMessage = "بيانات اشتراك السيرفر غير صحيحة أو منتهية الصلاحية"
                                )
                            }
                            status = userInfo.str("status", "Active")
                            expDate = formatEpochSeconds(userInfo.str("exp_date", ""))
                            maxCons = userInfo.str("max_connections", "1")
                            activeCons = userInfo.str("active_cons", "0")
                        }
                    } catch (_: Exception) {
                    }
                }

                val liveCatMap = parseCategoryMap(liveCatDef.await())
                val vodCatMap = parseCategoryMap(vodCatDef.await())
                val serCatMap = parseCategoryMap(serCatDef.await())

                val channels = parseLiveStreams(
                    rawJson = liveStreamsDef.await(),
                    baseHost = baseHost,
                    user = user,
                    pass = pass,
                    categoryMap = liveCatMap
                )

                val movies = parseVodStreams(
                    rawJson = vodStreamsDef.await(),
                    baseHost = baseHost,
                    user = user,
                    pass = pass,
                    categoryMap = vodCatMap
                )

                val seriesList = parseSeriesList(
                    rawJson = seriesDef.await(),
                    categoryMap = serCatMap
                )

                val allLiveCats = (liveCatMap.values + channels.map { it.category })
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .distinct()

                val allVodCats = (vodCatMap.values + movies.map { it.category })
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .distinct()

                val allSeriesCats = (serCatMap.values + seriesList.map { it.category })
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .distinct()

                if (channels.isEmpty() && movies.isEmpty() && seriesList.isEmpty()) {
                    XtreamSyncResult(
                        success = false,
                        errorMessage = "تعذر جلب القنوات من السيرفر حالياً، تأكد من اتصال الإنترنت"
                    )
                } else {
                    XtreamSyncResult(
                        success = true,
                        accountStatus = status,
                        expDate = expDate,
                        maxConnections = maxCons,
                        activeConnections = activeCons,
                        liveCategories = allLiveCats,
                        vodCategories = allVodCats,
                        seriesCategories = allSeriesCats,
                        channels = channels,
                        movies = movies,
                        seriesList = seriesList
                    )
                }
            }
        } catch (e: Exception) {
            XtreamSyncResult(
                success = false,
                errorMessage = "خطأ أثناء الاتصال بالسيرفر: ${e.localizedMessage ?: "يرجى المحاولة مجدداً"}"
            )
        }
    }

    private fun parseCategoryMap(rawJson: String?): LinkedHashMap<String, String> {
        val map = LinkedHashMap<String, String>()
        if (rawJson.isNullOrBlank()) return map
        try {
            val arr = json.parseToJsonElement(rawJson) as? JsonArray ?: return map
            for (el in arr) {
                val obj = el as? JsonObject ?: continue
                val id = obj.str("category_id")
                val name = obj.str("category_name").trim()
                if (id.isNotBlank() && name.isNotBlank()) {
                    map[id] = name
                }
            }
        } catch (_: Exception) {
        }
        return map
    }

    private fun parseLiveStreams(
        rawJson: String?,
        baseHost: String,
        user: String,
        pass: String,
        categoryMap: Map<String, String>
    ): List<LiveChannel> {
        if (rawJson.isNullOrBlank()) return emptyList()
        val result = mutableListOf<LiveChannel>()
        val palette = listOf(0xFFD4AF37L, 0xFF00D2FFL, 0xFFF59E0BL, 0xFF10B981L, 0xFF1E3E62L, 0xFF8C5A2BL)
        try {
            val arr = json.parseToJsonElement(rawJson) as? JsonArray ?: return emptyList()
            var numCounter = 1
            for (el in arr) {
                val obj = el as? JsonObject ?: continue
                val streamId = obj.str("stream_id").ifBlank { obj.str("id") }
                if (streamId.isBlank()) continue
                val name = obj.str("name", "Channel $streamId").trim()
                if (isDecorativeSeparator(name)) continue
                val catId = obj.str("category_id")
                val category = categoryMap[catId] ?: "قنوات عامة"
                val icon = obj.str("stream_icon")
                val num = obj.str("num").toIntOrNull() ?: numCounter
                numCounter++

                val qualityBadge = when {
                    name.contains("4K", ignoreCase = true) || name.contains("UHD", ignoreCase = true) -> "4K"
                    name.contains("FHD", ignoreCase = true) || name.contains("1080", ignoreCase = true) -> "FHD"
                    name.contains("HD", ignoreCase = true) -> "HD"
                    else -> "FHD"
                }

                val isAdult = name.contains("+18") || name.contains("Adult", ignoreCase = true) ||
                    category.contains("+18") || category.contains("Adult", ignoreCase = true)

                val m3u8Url = "$baseHost/live/$user/$pass/$streamId.m3u8"
                val tsUrl = "$baseHost/live/$user/$pass/$streamId.ts"

                val sources = listOf(
                    StreamSource(
                        id = "${streamId}_hls",
                        label = "سيرفر دجلة بلس 1 (HLS)",
                        url = m3u8Url,
                        quality = qualityBadge,
                        isBackup = false
                    ),
                    StreamSource(
                        id = "${streamId}_ts",
                        label = "سيرفر دجلة بلس 2 (MPEG-TS)",
                        url = tsUrl,
                        quality = qualityBadge,
                        isBackup = true
                    )
                )

                val logoShort = name
                    .replace(Regex("[^A-Za-z0-9\\u0600-\\u06FF ]"), " ")
                    .trim()
                    .split(Regex("\\s+"))
                    .take(2)
                    .joinToString(" ")
                    .take(8)
                    .ifBlank { "TV" }

                result.add(
                    LiveChannel(
                        id = "live_$streamId",
                        name = name,
                        category = category,
                        logoText = logoShort,
                        logoColorHex = palette[result.size % palette.size],
                        streamIconUrl = icon,
                        currentProgram = "بث حي مباشر - $category",
                        qualityBadge = qualityBadge,
                        isEncrypted = false,
                        isAdult = isAdult,
                        streamSources = sources,
                        channelNumber = num
                    )
                )
            }
        } catch (_: Exception) {
        }
        return result
    }

    private fun parseVodStreams(
        rawJson: String?,
        baseHost: String,
        user: String,
        pass: String,
        categoryMap: Map<String, String>
    ): List<VodMovie> {
        if (rawJson.isNullOrBlank()) return emptyList()
        val result = mutableListOf<VodMovie>()
        val palette = listOf(0xFF0D1E33L, 0xFF17324DL, 0xFF7C4D25L, 0xFFD4AF37L)
        try {
            val arr = json.parseToJsonElement(rawJson) as? JsonArray ?: return emptyList()
            for (el in arr) {
                val obj = el as? JsonObject ?: continue
                val streamId = obj.str("stream_id").ifBlank { obj.str("id") }
                if (streamId.isBlank()) continue
                val name = obj.str("name", "Movie $streamId").trim()
                if (isDecorativeSeparator(name)) continue
                val catId = obj.str("category_id")
                val category = categoryMap[catId] ?: "أفلام متنوعة"
                val poster = obj.str("stream_icon").ifBlank { obj.str("cover") }.takeIf { it.startsWith("http") }
                val rating = obj.str("rating", "8.5").takeIf { it.isNotBlank() && it != "0" } ?: "8.5"
                val rawExt = obj.str("container_extension", "mp4").trim().removePrefix(".")
                val cleanExt = rawExt.ifBlank { "mp4" }
                val addedTs = obj.str("added").toLongOrNull() ?: 0L
                val yearMatch = Regex("(19\\d{2}|20\\d{2})").find(name)?.value?.toIntOrNull() ?: 2024
                val isAdult = name.contains("+18") || category.contains("+18") || category.contains("Adult", ignoreCase = true)
                val movieUrl = "$baseHost/movie/$user/$pass/$streamId.$cleanExt"

                result.add(
                    VodMovie(
                        id = "vod_$streamId",
                        title = name,
                        year = yearMatch,
                        rating = rating.take(3),
                        category = category,
                        duration = "فيلم كامل",
                        description = "مشاهدة فيلم $name ضمن باقة $category بجودة عالية على دجلة بلس.",
                        qualityBadge = if (name.contains("4K", ignoreCase = true)) "4K UHD" else "FHD 1080p",
                        isAdult = isAdult,
                        posterColorHex = palette[result.size % palette.size],
                        customPosterUri = poster,
                        streamUrl = movieUrl,
                        streamId = streamId,
                        containerExtension = cleanExt,
                        addedTimestamp = addedTs
                    )
                )
            }
        } catch (_: Exception) {
        }
        return result
    }

    private fun parseSeriesList(
        rawJson: String?,
        categoryMap: Map<String, String>
    ): List<VodSeries> {
        if (rawJson.isNullOrBlank()) return emptyList()
        val result = mutableListOf<VodSeries>()
        val palette = listOf(0xFF0D1E33L, 0xFF17324DL, 0xFF7C4D25L)
        try {
            val arr = json.parseToJsonElement(rawJson) as? JsonArray ?: return emptyList()
            for (el in arr) {
                val obj = el as? JsonObject ?: continue
                val seriesId = obj.str("series_id").ifBlank { obj.str("id") }
                if (seriesId.isBlank()) continue
                val name = obj.str("name", "Series $seriesId").trim()
                if (isDecorativeSeparator(name)) continue
                val catId = obj.str("category_id")
                val category = categoryMap[catId] ?: "مسلسلات متنوعة"
                val cover = obj.str("cover").ifBlank { obj.str("stream_icon") }.takeIf { it.startsWith("http") }
                val plot = obj.str("plot").ifBlank { "تابع حلقات مسلسل $name بجودة عالية عبر مكتبة دجلة بلس." }
                val rating = obj.str("rating", "8.7").takeIf { it.isNotBlank() && it != "0" } ?: "8.7"
                val lastMod = obj.str("last_modified").toLongOrNull() ?: 0L
                val year = obj.str("releaseDate").take(4).toIntOrNull()
                    ?: Regex("(19\\d{2}|20\\d{2})").find(name)?.value?.toIntOrNull()
                    ?: 2024
                val isAdult = name.contains("+18") || category.contains("+18") || category.contains("Adult", ignoreCase = true)

                result.add(
                    VodSeries(
                        id = "ser_$seriesId",
                        title = name,
                        year = year,
                        rating = rating.take(3),
                        category = category,
                        description = plot,
                        qualityBadge = "FHD 1080p",
                        isAdult = isAdult,
                        posterColorHex = palette[result.size % palette.size],
                        customPosterUri = cover,
                        seasons = emptyList(),
                        seriesId = seriesId,
                        lastModifiedTimestamp = lastMod
                    )
                )
            }
        } catch (_: Exception) {
        }
        return result
    }

    /**
     * ROOT-CAUSE FIX FOR SERIES EPISODE PLAYBACK:
     * 1) Handles `data.episodes` whether it is a JsonObject keyed by season number ("1": [...], "2": [...])
     *    OR a JsonArray (nested arrays per season OR flat list of episode objects).
     * 2) Extracts the true episode ID via `episode.id || episode.episode_id || episode.stream_id`.
     * 3) Cleans `container_extension` (`replace(/^\./, '')`, default `mp4`).
     * 4) Builds `${server.host}/series/${server.username}/${server.password}/${epId}.${cleanExt}`.
     */
    suspend fun fetchSeriesInfo(
        hostUrl: String,
        username: String,
        password: String,
        seriesId: String
    ): List<SeriesSeason> = withContext(Dispatchers.IO) {
        if (seriesId.isBlank()) return@withContext emptyList()
        val baseHost = normalizeHost(hostUrl)
        val user = username.trim()
        val pass = password.trim()
        val uEnc = URLEncoder.encode(user, "UTF-8")
        val pEnc = URLEncoder.encode(pass, "UTF-8")
        val sEnc = URLEncoder.encode(seriesId.trim(), "UTF-8")
        val url = "$baseHost/player_api.php?username=$uEnc&password=$pEnc&action=get_series_info&series_id=$sEnc"

        val rawJson = fetchJsonText(url) ?: return@withContext emptyList()
        val seasonsMap = linkedMapOf<Int, MutableList<SeriesEpisode>>()

        fun parseSingleEpisodeObj(epObj: JsonObject, defaultSeasonNum: Int, fallbackEpIndex: Int): Pair<Int, SeriesEpisode>? {
            val infoObj = epObj["info"] as? JsonObject
            val epId = epObj.str("id")
                .ifBlank { epObj.str("episode_id") }
                .ifBlank { epObj.str("stream_id") }
                .ifBlank { infoObj?.str("id").orEmpty() }
                .trim()
            if (epId.isBlank()) return null

            val seasonNum = epObj.str("season").toIntOrNull()
                ?: infoObj?.str("season")?.toIntOrNull()
                ?: defaultSeasonNum

            val epNum = epObj.str("episode_num").toIntOrNull()
                ?: infoObj?.str("episode_num")?.toIntOrNull()
                ?: fallbackEpIndex

            val epTitle = epObj.str("title")
                .ifBlank { infoObj?.str("name").orEmpty() }
                .ifBlank { "الحلقة $epNum" }

            val rawExt = epObj.str("container_extension")
                .ifBlank { infoObj?.str("container_extension").orEmpty() }
                .ifBlank { "mp4" }
            val cleanExt = rawExt.trim().removePrefix(".").ifBlank { "mp4" }

            val duration = infoObj?.str("duration")?.takeIf { it.isNotBlank() }
                ?: epObj.str("duration").takeIf { it.isNotBlank() }
                ?: "حلقة كاملة"

            val primaryUrl = "$baseHost/series/$user/$pass/$epId.$cleanExt"

            val episode = SeriesEpisode(
                id = "ep_${seasonNum}_${epId}",
                rawEpisodeId = epId,
                episodeNumber = epNum,
                seasonNumber = seasonNum,
                title = epTitle,
                duration = duration,
                containerExtension = cleanExt,
                streamUrl = primaryUrl
            )
            return seasonNum to episode
        }

        try {
            val rootElement = json.parseToJsonElement(rawJson)
            val root = rootElement as? JsonObject ?: return@withContext emptyList()
            val episodesElement = root["episodes"] ?: return@withContext emptyList()

            when (episodesElement) {
                is JsonObject -> {
                    for ((seasonKey, epArrayEl) in episodesElement.entries) {
                        val defaultSeasonNum = seasonKey.trim().toIntOrNull() ?: (seasonsMap.size + 1)
                        when (epArrayEl) {
                            is JsonArray -> {
                                for ((idx, epEl) in epArrayEl.withIndex()) {
                                    val epObj = epEl as? JsonObject ?: continue
                                    val parsed = parseSingleEpisodeObj(epObj, defaultSeasonNum, idx + 1) ?: continue
                                    seasonsMap.getOrPut(parsed.first) { mutableListOf() }.add(parsed.second)
                                }
                            }
                            is JsonObject -> {
                                // Sometimes episodes inside a season object are keyed by episode number
                                var idx = 1
                                for ((_, innerEl) in epArrayEl.entries) {
                                    val epObj = innerEl as? JsonObject ?: continue
                                    val parsed = parseSingleEpisodeObj(epObj, defaultSeasonNum, idx++) ?: continue
                                    seasonsMap.getOrPut(parsed.first) { mutableListOf() }.add(parsed.second)
                                }
                            }
                            else -> Unit
                        }
                    }
                }
                is JsonArray -> {
                    for ((outerIdx, itemEl) in episodesElement.withIndex()) {
                        when (itemEl) {
                            is JsonArray -> {
                                val seasonNum = outerIdx + 1
                                for ((idx, epEl) in itemEl.withIndex()) {
                                    val epObj = epEl as? JsonObject ?: continue
                                    val parsed = parseSingleEpisodeObj(epObj, seasonNum, idx + 1) ?: continue
                                    seasonsMap.getOrPut(parsed.first) { mutableListOf() }.add(parsed.second)
                                }
                            }
                            is JsonObject -> {
                                val parsed = parseSingleEpisodeObj(itemEl, 1, outerIdx + 1) ?: continue
                                seasonsMap.getOrPut(parsed.first) { mutableListOf() }.add(parsed.second)
                            }
                            else -> Unit
                        }
                    }
                }
                else -> Unit
            }
        } catch (_: Exception) {
        }

        seasonsMap.entries
            .sortedBy { it.key }
            .map { (seasonNum, eps) ->
                SeriesSeason(
                    seasonNumber = seasonNum,
                    title = "الموسم $seasonNum",
                    episodes = eps.sortedBy { it.episodeNumber }
                )
            }
    }

    suspend fun parseM3uPlaylist(m3uUrl: String): List<LiveChannel> = withContext(Dispatchers.IO) {
        try {
            val req = Request.Builder()
                .url(m3uUrl.trim())
                .header("User-Agent", "IBOProPlayer/3.9 (Android; Linux)")
                .get()
                .build()
            val rawText = okHttpClient.newCall(req).execute().use { resp ->
                if (resp.isSuccessful) resp.body?.string().orEmpty() else ""
            }
            if (rawText.isBlank()) return@withContext emptyList()

            val channels = mutableListOf<LiveChannel>()
            val lines = rawText.lines()
            var pendingName = ""
            var pendingGroup = "قنوات M3U"
            var pendingLogo = ""
            var counter = 1

            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.isEmpty()) continue
                if (trimmed.startsWith("#EXTINF:", ignoreCase = true)) {
                    pendingName = trimmed.substringAfterLast(",", "Channel $counter").trim()
                    pendingGroup = Regex("group-title=\"([^\"]+)\"").find(trimmed)?.groupValues?.getOrNull(1)
                        ?.ifBlank { "قنوات M3U" } ?: "قنوات M3U"
                    pendingLogo = Regex("tvg-logo=\"([^\"]+)\"").find(trimmed)?.groupValues?.getOrNull(1).orEmpty()
                } else if (!trimmed.startsWith("#") && (trimmed.startsWith("http://") || trimmed.startsWith("https://"))) {
                    val name = pendingName.ifBlank { "Channel $counter" }
                    if (!isDecorativeSeparator(name)) {
                        channels.add(
                            LiveChannel(
                                id = "m3u_$counter",
                                name = name,
                                category = pendingGroup,
                                logoText = name.take(4).uppercase(),
                                streamIconUrl = pendingLogo,
                                currentProgram = "بث مباشر - $pendingGroup",
                                qualityBadge = "FHD",
                                streamSources = listOf(
                                    StreamSource(
                                        id = "m3u_src_$counter",
                                        label = "سيرفر دجلة بلس المباشر",
                                        url = trimmed,
                                        quality = "FHD"
                                    )
                                ),
                                channelNumber = counter
                            )
                        )
                        counter++
                    }
                    pendingName = ""
                }
            }
            channels
        } catch (_: Exception) {
            emptyList()
        }
    }
}
