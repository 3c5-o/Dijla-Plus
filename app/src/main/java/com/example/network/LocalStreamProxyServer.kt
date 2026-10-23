package com.example.network

import android.net.Uri
import okhttp3.ConnectionPool
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Local Loopback HTTP Stream Proxy (`http://127.0.0.1:<port>`)
 *
 * Solves all Xtream Codes playback issues without using legacy Android MediaPlayerNative:
 * 1. Follows Xtream single-use `302 Found` token redirects (`http://foxbleu.org:8789/live/...` -> edge IP)
 *    on every `.m3u8` manifest refresh so tokens never expire with HTTP 509.
 * 2. Resolves root-relative `/hls/<hash>/<id>_<seq>.ts` segment paths against the redirected edge server
 *    origin (`response.request.url`) instead of the main portal host.
 * 3. Converts browser `HEAD` probes into `GET` with `Range: bytes=0-1` so Xtream CDN proxies never
 *    return `502 Bad Gateway`.
 * 4. Injects `User-Agent: IPTVSmartersPro/3.9` and permissive CORS headers (`Access-Control-Allow-Origin: *`).
 */
object LocalStreamProxyServer {

    const val DEFAULT_USER_AGENT = "IPTVSmartersPro/3.9 (Android; BaghdadDijlahPlus)"

    val streamHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .connectionPool(ConnectionPool(16, 2, TimeUnit.MINUTES))
            .build()
    }

    private val executor = Executors.newCachedThreadPool()

    @Volatile
    private var serverSocket: ServerSocket? = null

    @Volatile
    private var boundPort: Int = 0

    @Synchronized
    fun ensureStarted(): Int {
        if (boundPort > 0 && serverSocket?.isClosed == false) {
            return boundPort
        }
        return try {
            val socket = ServerSocket(0, 64, InetAddress.getByName("127.0.0.1"))
            serverSocket = socket
            boundPort = socket.localPort
            executor.execute { acceptLoop(socket) }
            boundPort
        } catch (_: Exception) {
            0
        }
    }

    fun getBaseOriginUrl(): String {
        val port = ensureStarted()
        return if (port > 0) "http://127.0.0.1:$port/" else "http://localhost/"
    }

    fun buildProxiedHlsUrl(rawStreamUrl: String): String {
        val port = ensureStarted()
        if (port <= 0 || rawStreamUrl.isBlank()) return rawStreamUrl
        val encoded = URLEncoder.encode(rawStreamUrl.trim(), "UTF-8")
        return "http://127.0.0.1:$port/live.m3u8?url=$encoded"
    }

    fun buildProxiedTsUrl(rawStreamUrl: String): String {
        val port = ensureStarted()
        if (port <= 0 || rawStreamUrl.isBlank()) return rawStreamUrl
        val encoded = URLEncoder.encode(rawStreamUrl.trim(), "UTF-8")
        return "http://127.0.0.1:$port/raw.ts?url=$encoded"
    }

    fun buildProxiedVodUrl(rawStreamUrl: String): String {
        val port = ensureStarted()
        if (port <= 0 || rawStreamUrl.isBlank()) return rawStreamUrl
        val encoded = URLEncoder.encode(rawStreamUrl.trim(), "UTF-8")
        return "http://127.0.0.1:$port/vod?url=$encoded"
    }

    fun getProxyStreamUrl(rawStreamUrl: String): String {
        val clean = rawStreamUrl.trim()
        return when {
            clean.endsWith(".m3u8", ignoreCase = true) -> buildProxiedHlsUrl(clean)
            clean.endsWith(".ts", ignoreCase = true) -> buildProxiedTsUrl(clean)
            else -> buildProxiedVodUrl(clean)
        }
    }

    private fun acceptLoop(server: ServerSocket) {
        while (!server.isClosed) {
            try {
                val client = server.accept()
                executor.execute { handleClientSocket(client) }
            } catch (_: Exception) {
                if (server.isClosed) break
            }
        }
    }

    private fun handleClientSocket(socket: Socket) {
        socket.use { client ->
            try {
                client.soTimeout = 30_000
                val reader = BufferedReader(InputStreamReader(client.getInputStream(), Charsets.UTF_8))
                val requestLine = reader.readLine() ?: return
                val parts = requestLine.split(" ")
                if (parts.size < 2) return
                val method = parts[0].uppercase()
                val fullPath = parts[1]

                val requestHeaders = mutableMapOf<String, String>()
                while (true) {
                    val line = reader.readLine() ?: break
                    if (line.isEmpty()) break
                    val colon = line.indexOf(':')
                    if (colon > 0) {
                        val k = line.substring(0, colon).trim().lowercase()
                        val v = line.substring(colon + 1).trim()
                        requestHeaders[k] = v
                    }
                }

                val out = BufferedOutputStream(client.getOutputStream())

                if (method == "OPTIONS") {
                    writeCorsPreflight(out)
                    return
                }

                val targetUrl = extractQueryParam(fullPath, "url")
                if (targetUrl.isBlank()) {
                    writeSimpleResponse(out, 400, "text/plain", "Missing url parameter".toByteArray())
                    return
                }

                when {
                    fullPath.startsWith("/live.m3u8") -> {
                        handleHlsManifestRequest(out, targetUrl)
                    }
                    fullPath.startsWith("/seg.ts") || fullPath.startsWith("/raw.ts") -> {
                        handleBinaryStreamRequest(
                            out = out,
                            targetUrl = targetUrl,
                            method = method,
                            rangeHeader = requestHeaders["range"],
                            defaultContentType = "video/mp2t"
                        )
                    }
                    else -> {
                        handleBinaryStreamRequest(
                            out = out,
                            targetUrl = targetUrl,
                            method = method,
                            rangeHeader = requestHeaders["range"],
                            defaultContentType = "video/mp4"
                        )
                    }
                }
            } catch (_: Exception) {
                // Client disconnected or socket closed during zapping; ignore quietly
            }
        }
    }

    private fun handleHlsManifestRequest(out: BufferedOutputStream, targetUrl: String) {
        val req = Request.Builder()
            .url(targetUrl)
            .header("User-Agent", DEFAULT_USER_AGENT)
            .header("Accept", "*/*")
            .header("Connection", "keep-alive")
            .get()
            .build()

        streamHttpClient.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) {
                writeSimpleResponse(
                    out,
                    resp.code,
                    "text/plain",
                    "Upstream error ${resp.code}".toByteArray()
                )
                return
            }

            val finalHttpUrl = resp.request.url
            val bodyString = resp.body?.string().orEmpty()

            if (!bodyString.trimStart().startsWith("#EXTM3U")) {
                writeSimpleResponse(
                    out,
                    502,
                    "text/plain",
                    "Invalid M3U8 manifest".toByteArray()
                )
                return
            }

            val port = boundPort
            val rewrittenManifest = buildString {
                for (rawLine in bodyString.lineSequence()) {
                    val trimmed = rawLine.trim()
                    when {
                        trimmed.isEmpty() -> append("\n")
                        trimmed.startsWith("#") -> {
                            // Rewrite URI="..." attributes inside #EXT-X-KEY or #EXT-X-MAP if present
                            if (trimmed.contains("URI=\"")) {
                                val rewrittenTag = Regex("""URI="([^"]+)"""").replace(trimmed) { match ->
                                    val uriVal = match.groupValues[1]
                                    val resolved = finalHttpUrl.resolve(uriVal)?.toString() ?: uriVal
                                    val enc = URLEncoder.encode(resolved, "UTF-8")
                                    """URI="http://127.0.0.1:$port/seg.ts?url=$enc""""
                                }
                                append(rewrittenTag).append("\n")
                            } else {
                                append(trimmed).append("\n")
                            }
                        }
                        else -> {
                            val resolvedUrl = finalHttpUrl.resolve(trimmed)?.toString() ?: trimmed
                            val isSubPlaylist = resolvedUrl.substringBefore('?')
                                .endsWith(".m3u8", ignoreCase = true)
                            if (isSubPlaylist) {
                                val encoded = URLEncoder.encode(resolvedUrl, "UTF-8")
                                append("http://127.0.0.1:$port/live.m3u8?url=$encoded\n")
                            } else {
                                append("$resolvedUrl\n")
                            }
                        }
                    }
                }
            }

            val payload = rewrittenManifest.toByteArray(Charsets.UTF_8)
            writeSimpleResponse(
                out = out,
                statusCode = 200,
                contentType = "application/vnd.apple.mpegurl; charset=utf-8",
                body = payload
            )
        }
    }

    private fun handleBinaryStreamRequest(
        out: BufferedOutputStream,
        targetUrl: String,
        method: String,
        rangeHeader: String?,
        defaultContentType: String
    ) {
        val reqBuilder = Request.Builder()
            .url(targetUrl)
            .header("User-Agent", DEFAULT_USER_AGENT)
            .header("Accept", "*/*")
            .header("Connection", "keep-alive")
            .header("Icy-MetaData", "1")

        // Xtream CDN proxies return 502 on HEAD, so convert HEAD to a 2-byte GET probe
        if (method == "HEAD") {
            reqBuilder.get()
            reqBuilder.header("Range", rangeHeader ?: "bytes=0-1")
        } else {
            reqBuilder.get()
            if (!rangeHeader.isNullOrBlank()) {
                reqBuilder.header("Range", rangeHeader)
            }
        }

        streamHttpClient.newCall(reqBuilder.build()).execute().use { resp ->
            val statusCode = if (method == "HEAD" && resp.code == 206) 200 else resp.code
            val statusText = when (statusCode) {
                200 -> "OK"
                206 -> "Partial Content"
                else -> "Error"
            }
            val contentType = resp.header("Content-Type") ?: defaultContentType
            val contentLength = resp.header("Content-Length")
            val contentRange = resp.header("Content-Range")
            val acceptRanges = resp.header("Accept-Ranges") ?: "bytes"

            val headerBuilder = StringBuilder()
            headerBuilder.append("HTTP/1.1 $statusCode $statusText\r\n")
            headerBuilder.append("Content-Type: $contentType\r\n")
            headerBuilder.append("Access-Control-Allow-Origin: *\r\n")
            headerBuilder.append("Access-Control-Allow-Methods: GET, HEAD, OPTIONS\r\n")
            headerBuilder.append("Access-Control-Allow-Headers: *\r\n")
            headerBuilder.append("Accept-Ranges: $acceptRanges\r\n")
            headerBuilder.append("Cache-Control: no-cache\r\n")
            if (!contentRange.isNullOrBlank()) {
                headerBuilder.append("Content-Range: $contentRange\r\n")
            }
            if (!contentLength.isNullOrBlank() && method != "HEAD") {
                headerBuilder.append("Content-Length: $contentLength\r\n")
            }
            headerBuilder.append("Connection: close\r\n\r\n")

            out.write(headerBuilder.toString().toByteArray(Charsets.UTF_8))
            out.flush()

            if (method != "HEAD") {
                val bodyStream = resp.body?.byteStream() ?: return
                val buffer = ByteArray(32 * 1024)
                while (true) {
                    val read = bodyStream.read(buffer)
                    if (read <= 0) break
                    out.write(buffer, 0, read)
                }
                out.flush()
            }
        }
    }

    private fun writeCorsPreflight(out: BufferedOutputStream) {
        val headers = buildString {
            append("HTTP/1.1 204 No Content\r\n")
            append("Access-Control-Allow-Origin: *\r\n")
            append("Access-Control-Allow-Methods: GET, HEAD, OPTIONS\r\n")
            append("Access-Control-Allow-Headers: *\r\n")
            append("Access-Control-Max-Age: 86400\r\n")
            append("Connection: close\r\n\r\n")
        }
        out.write(headers.toByteArray(Charsets.UTF_8))
        out.flush()
    }

    private fun writeSimpleResponse(
        out: BufferedOutputStream,
        statusCode: Int,
        contentType: String,
        body: ByteArray
    ) {
        val statusText = if (statusCode == 200) "OK" else "Error"
        val headers = buildString {
            append("HTTP/1.1 $statusCode $statusText\r\n")
            append("Content-Type: $contentType\r\n")
            append("Content-Length: ${body.size}\r\n")
            append("Access-Control-Allow-Origin: *\r\n")
            append("Access-Control-Allow-Methods: GET, HEAD, OPTIONS\r\n")
            append("Access-Control-Allow-Headers: *\r\n")
            append("Cache-Control: no-cache, no-store, must-revalidate\r\n")
            append("Connection: close\r\n\r\n")
        }
        out.write(headers.toByteArray(Charsets.UTF_8))
        out.write(body)
        out.flush()
    }

    private fun extractQueryParam(fullPath: String, key: String): String {
        val qIndex = fullPath.indexOf('?')
        if (qIndex < 0 || qIndex + 1 >= fullPath.length) return ""
        val query = fullPath.substring(qIndex + 1)
        for (pair in query.split("&")) {
            val eq = pair.indexOf('=')
            if (eq > 0) {
                val k = pair.substring(0, eq)
                val v = pair.substring(eq + 1)
                if (k == key) {
                    return try {
                        URLDecoder.decode(v, "UTF-8")
                    } catch (_: Exception) {
                        v
                    }
                }
            }
        }
        return ""
    }
}
