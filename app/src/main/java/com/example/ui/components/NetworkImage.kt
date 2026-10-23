package com.example.ui.components

import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.example.network.LocalStreamProxyServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.io.File

private object BitmapMemoryCache {
    val cache = object : LruCache<String, ImageBitmap>(120) {}
}

@Composable
fun NetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    fallbackContent: @Composable () -> Unit = {}
) {
    val cleanUrl = url.trim()
    var bitmap by remember(cleanUrl) {
        mutableStateOf(
            if (cleanUrl.isNotEmpty()) BitmapMemoryCache.cache.get(cleanUrl) else null
        )
    }

    LaunchedEffect(cleanUrl) {
        if (cleanUrl.isEmpty() || bitmap != null) return@LaunchedEffect
        val loaded: ImageBitmap? = withContext(Dispatchers.IO) {
            try {
                if (cleanUrl.startsWith("/")) {
                    val file = File(cleanUrl)
                    if (file.exists()) {
                        BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
                    } else null
                } else if (cleanUrl.startsWith("http://") || cleanUrl.startsWith("https://")) {
                    val req = Request.Builder()
                        .url(cleanUrl)
                        .header("User-Agent", "IBOProPlayer/3.9 (Android)")
                        .get()
                        .build()
                    LocalStreamProxyServer.streamHttpClient.newCall(req).execute().use { resp ->
                        if (resp.isSuccessful) {
                            val bytes = resp.body?.bytes()
                            if (bytes != null && bytes.isNotEmpty()) {
                                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                            } else null
                        } else null
                    }
                } else null
            } catch (_: Exception) {
                null
            }
        }
        if (loaded != null) {
            BitmapMemoryCache.cache.put(cleanUrl, loaded)
            bitmap = loaded
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            fallbackContent()
        }
    }
}
