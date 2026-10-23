package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AppConfig
import com.example.model.MainSection
import com.example.model.SavedPlaylistAccount
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DijlahTopBar(
    config: AppConfig,
    currentSection: MainSection,
    searchQuery: String,
    isSyncing: Boolean,
    pingLatencyMs: Int = 24,
    onSearchQueryChanged: (String) -> Unit,
    onLogoTapped: () -> Unit,
    onSelectSection: (MainSection) -> Unit,
    onRefreshServer: () -> Unit,
    onOpenServerStatus: () -> Unit,
    onOpenTelegramPopup: () -> Unit = {}
) {
    val context = LocalContext.current
    var currentTimeText by remember { mutableStateOf("") }
    var currentDateText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val arabicLocale = Locale.forLanguageTag("ar")
        val timeFmt = SimpleDateFormat("hh:mm a", arabicLocale)
        val dateFmt = SimpleDateFormat("EEE d MMM yyyy", arabicLocale)
        while (true) {
            val now = Date()
            currentTimeText = timeFmt.format(now)
            currentDateText = dateFmt.format(now)
            delay(15_000L)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF050B14),
                        Color(0xEE081220)
                    )
                )
            )
    ) {
        // Optional Live Admin Broadcast Banner at very top
        if (config.liveBroadcastBanner.isNotBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF1E3E62),
                                Color(0xFF8C5A2B),
                                Color(0xFF0B192C)
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = "إشعار مباشر",
                    tint = DijlahGold,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "تنبيه الإدارة: ${config.liveBroadcastBanner}",
                    color = DijlahTextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Surface(
            color = Color.Transparent,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Right Side (in RTL): Brand Identity + Back Button (7-tap secret trigger on Logo)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (currentSection != MainSection.HOME) {
                        Surface(
                            onClick = { onSelectSection(MainSection.HOME) },
                            color = DijlahSurfaceCard,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.45f)),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("back_to_home_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "الرئيسية",
                                    tint = DijlahGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "الرئيسية",
                                    color = DijlahGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Brand Logo Box (7-Tap Secret Admin Trigger)
                    Row(
                        modifier = Modifier
                            .testTag("dijlah_logo_secret_trigger")
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onLogoTapped() },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(DijlahGold, DijlahAmber, DijlahWarmBronze)
                                    )
                                )
                                .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LiveTv,
                                contentDescription = "شعار دجلة بلس",
                                tint = Color(0xFF050B14),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    text = config.appName.ifBlank { "دجلة بلس | Dijla Plus" },
                                    color = DijlahTextPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    maxLines = 1
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(DijlahGold.copy(alpha = 0.18f))
                                        .border(0.5.dp, DijlahGold.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "VIP",
                                        color = DijlahGold,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                            Text(
                                text = if (currentSection == MainSection.HOME) config.slogan else currentSection.titleAr,
                                color = DijlahTextSecondary,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Center: Search Bar (supports secret #*2008*# code) + Quick Section Pills
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(DijlahSurfaceCard)
                            .border(
                                width = 1.dp,
                                color = if (searchQuery.isNotEmpty()) DijlahGold else Color.White.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "بحث",
                                tint = DijlahGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "ابحث عن قناة، مباراة، فيلم، أو مسلسل...",
                                        color = DijlahTextSecondary.copy(alpha = 0.7f),
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = onSearchQueryChanged,
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        color = DijlahTextPrimary,
                                        fontSize = 12.sp
                                    ),
                                    cursorBrush = SolidColor(DijlahGold),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("global_search_input")
                                )
                            }
                            if (searchQuery.isNotEmpty()) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "مسح البحث",
                                    tint = DijlahTextSecondary,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { onSearchQueryChanged("") }
                                )
                            }
                        }
                    }

                    // Quick Navigation Pills when inside sub-sections
                    if (currentSection != MainSection.HOME) {
                        val quickItems = listOf(
                            MainSection.LIVE_TV to "مباشر",
                            MainSection.MOVIES to "أفلام",
                            MainSection.SERIES to "مسلسلات",
                            MainSection.MATCHES to "مباريات",
                            MainSection.FAVORITES to "قائمتي"
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            quickItems.forEach { (sec, label) ->
                                val selected = currentSection == sec
                                val bg by animateColorAsState(
                                    if (selected) DijlahGold else DijlahSurfaceCard,
                                    label = "navPillBg"
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(bg)
                                        .border(
                                            1.dp,
                                            if (selected) DijlahGold else Color.White.copy(alpha = 0.08f),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { onSelectSection(sec) }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = label,
                                        color = if (selected) Color(0xFF050B14) else DijlahTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Left Side (in RTL): Telegram Badge + Server Status + Ping + Live Clock
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Official Telegram Channel Pill (@Dijla_Plus)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(DijlahCyan.copy(alpha = 0.14f))
                            .border(1.dp, DijlahCyan.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                            .clickable {
                                try {
                                    context.startActivity(
                                        Intent(Intent.ACTION_VIEW, Uri.parse(config.telegramUrl.ifBlank { "https://t.me/Dijla_Plus" }))
                                    )
                                } catch (_: Exception) {
                                    onOpenTelegramPopup()
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                            .testTag("topbar_telegram_badge"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "قناة التلغرام",
                            tint = DijlahCyan,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = config.telegramHandle.ifBlank { "@Dijla_Plus" },
                            color = DijlahCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Server Status + Live Ping Badge (100% Stealth)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (config.isServerOnline) DijlahEmerald.copy(alpha = 0.14f)
                                else DijlahCrimson.copy(alpha = 0.15f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (config.isServerOnline) DijlahEmerald.copy(alpha = 0.45f)
                                else DijlahCrimson.copy(alpha = 0.45f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onOpenServerStatus() }
                            .padding(horizontal = 9.dp, vertical = 5.dp)
                            .testTag("server_status_badge"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                color = DijlahGold,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(11.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (config.isServerOnline) DijlahEmerald else DijlahCrimson)
                            )
                        }
                        Text(
                            text = when {
                                isSyncing -> "مزامنة..."
                                config.isServerOnline -> "${pingLatencyMs}ms | VIP"
                                else -> "غير متصل"
                            },
                            color = if (config.isServerOnline) DijlahEmerald else DijlahCrimson,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Clock & Date
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = currentTimeText,
                            color = DijlahGold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = currentDateText,
                            color = DijlahTextSecondary,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }

        HorizontalDivider(
            color = DijlahGold.copy(alpha = 0.2f),
            thickness = 1.dp
        )
    }
}

@Composable
fun TelegramSubscriptionPopupModal(
    config: AppConfig,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = DijlahBgSecondary,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, DijlahGold.copy(alpha = 0.65f)),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 460.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                DijlahSurfaceCard,
                                DijlahSurfaceElevated.copy(alpha = 0.7f),
                                DijlahBgDark
                            )
                        )
                    )
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(DijlahCyan, DijlahSurfaceElevated)
                            )
                        )
                        .border(1.5.dp, DijlahGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "قناة دجلة بلس على تلغرام",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = config.telegramPopupTitle.ifBlank { "قناة دجلة بلس الرسمية" },
                    color = DijlahTextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DijlahGold.copy(alpha = 0.16f))
                        .border(1.dp, DijlahGold.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = config.telegramHandle.ifBlank { "@Dijla_Plus" },
                        color = DijlahGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = config.telegramPopupMessage.ifBlank {
                        "اشترك في قناة دجلة بلس الرسمية على تلغرام للحصول على أحدث الأكواد والتحديثات ومتابعة المباريات العالمية."
                    },
                    color = DijlahTextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 19.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("telegram_popup_cancel_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                    ) {
                        Text(
                            text = "إلغاء",
                            color = DijlahTextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            try {
                                val targetUrl = config.telegramUrl.ifBlank { "https://t.me/Dijla_Plus" }
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)))
                            } catch (_: Exception) {
                            }
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DijlahGold,
                            contentColor = Color(0xFF050B14)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .testTag("telegram_popup_subscribe_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "اشتراك في القناة",
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun XtreamServerStatusModal(
    activePlaylist: SavedPlaylistAccount?,
    channelsCount: Int,
    moviesCount: Int,
    seriesCount: Int,
    isSyncing: Boolean,
    pingLatencyMs: Int = 24,
    onRefresh: () -> Unit,
    onOpenPlaylistManager: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DijlahBgSecondary,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, DijlahGold.copy(alpha = 0.55f)),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = "حالة السيرفر",
                            tint = DijlahGold
                        )
                        Text(
                            text = "معلومات اتصال سيرفر دجلة بلس VIP",
                            color = DijlahTextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = DijlahTextSecondary)
                    }
                }

                // 100% Server Stealth: Never expose raw host, port, username, or password
                Surface(
                    color = DijlahSurfaceCard,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StatusRowItem("البوابة النشطة", "سيرفر دجلة بلس VIP", DijlahGold)
                        StatusRowItem("حالة الاشتراك", if (activePlaylist?.status == "Active") "نشط (Active)" else (activePlaylist?.status ?: "نشط"))
                        StatusRowItem("سرعة الاستجابة (Ping)", "${pingLatencyMs} ms (فائق السرعة)", DijlahCyan)
                        StatusRowItem("نظام الحماية والتشفير", "Stealth Stream Proxy مفعل", DijlahEmerald)
                        StatusRowItem("تاريخ الصلاحية", activePlaylist?.expDate ?: "غير محدود")
                        StatusRowItem("القنوات المباشرة المحملة", "$channelsCount قناة")
                        StatusRowItem("مكتبة الأفلام (VOD)", "$moviesCount فيلم")
                        StatusRowItem("مكتبة المسلسلات", "$seriesCount مسلسل")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onOpenPlaylistManager()
                        },
                        modifier = Modifier.weight(1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold)
                    ) {
                        Text("إدارة القوائم", color = DijlahGold, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onRefresh,
                        enabled = !isSyncing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DijlahGold,
                            contentColor = Color(0xFF050B14)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (isSyncing) "جاري التحديث..." else "تحديث الآن",
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusRowItem(label: String, value: String, valueColor: Color = DijlahTextPrimary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = DijlahTextSecondary, fontSize = 12.sp)
        Text(text = value, color = valueColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
fun StealthPinPromptDialog(
    onVerifyPin: (String) -> Boolean,
    onDismiss: () -> Unit
) {
    var pinInput by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DijlahBgSecondary,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, DijlahGold.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 400.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(DijlahGold.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "لوحة تحكم الإدارة",
                        tint = DijlahGold,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = "بوابة إدارة دجلة بلس | Dijla Plus",
                    color = DijlahTextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "أدخل الرمز السري للمسؤول لفتح لوحة التحكم الشاملة",
                    color = DijlahTextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = pinInput,
                    onValueChange = {
                        pinInput = it
                        hasError = false
                    },
                    label = { Text("الرمز السري للمسؤول") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    isError = hasError,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DijlahGold,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                        focusedTextColor = DijlahTextPrimary,
                        unfocusedTextColor = DijlahTextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("stealth_pin_input")
                )

                if (hasError) {
                    Text(
                        text = "الرمز السري غير صحيح",
                        color = DijlahCrimson,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                    ) {
                        Text("إلغاء", color = DijlahTextSecondary)
                    }
                    Button(
                        onClick = {
                            if (!onVerifyPin(pinInput)) {
                                hasError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DijlahGold,
                            contentColor = Color(0xFF050B14)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stealth_pin_confirm_button")
                    ) {
                        Text("دخول اللوحة", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

@Composable
fun ParentalPinGateDialog(
    onVerifyPin: (String) -> Boolean,
    onDismiss: () -> Unit
) {
    var pinInput by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DijlahBgSecondary,
            border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.45f)),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 380.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(DijlahGold.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "حماية عائلية",
                        tint = DijlahGold,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = "محتوى محمي برمز القفل العائلي",
                    color = DijlahTextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )

                Text(
                    text = "يرجى إدخال رمز القفل العائلي (PIN) للمتابعة",
                    color = DijlahTextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = pinInput,
                    onValueChange = {
                        pinInput = it
                        hasError = false
                    },
                    label = { Text("رمز PIN") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    isError = hasError,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DijlahGold,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedTextColor = DijlahTextPrimary,
                        unfocusedTextColor = DijlahTextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("parental_pin_input")
                )

                if (hasError) {
                    Text(
                        text = "رمز PIN غير صحيح",
                        color = DijlahCrimson,
                        fontSize = 12.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("إلغاء", color = DijlahTextSecondary)
                    }
                    Button(
                        onClick = {
                            if (!onVerifyPin(pinInput)) {
                                hasError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DijlahGold,
                            contentColor = Color(0xFF050B14)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("parental_pin_confirm_button")
                    ) {
                        Text("فتح القفل", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
