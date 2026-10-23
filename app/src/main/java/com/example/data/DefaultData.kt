package com.example.data

import com.example.model.AppConfig
import com.example.model.SavedPlaylistAccount

object DijlahSeedData {
    const val PRIMARY_XTREAM_HOST = "http://foxbleu.org:8789"
    const val PRIMARY_XTREAM_USER = "ludovic"
    const val PRIMARY_XTREAM_PASS = "8333"

    val defaultConfig = AppConfig()

    val defaultPrimaryPlaylist = SavedPlaylistAccount(
        id = "primary_dijlah_vip",
        playlistName = "سيرفر دجلة بلس VIP",
        loginType = "XTREAM",
        serverHost = PRIMARY_XTREAM_HOST,
        username = PRIMARY_XTREAM_USER,
        password = PRIMARY_XTREAM_PASS,
        status = "Active",
        expDate = "غير محدود",
        maxConnections = "1",
        activeConnections = "0",
        isActive = true
    )
}
