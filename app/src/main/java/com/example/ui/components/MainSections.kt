package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
import com.example.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Locale

private enum class VodSortOption(val labelAr: String) {
    ADDED("بحسب ما تم إضافته"),
    RATING("بحسب التقييم"),
    ALPHA("الأبجدية أ - ي")
}

// ============================================================================
// 1. HOME SCREEN — Landscape Bento Grid + 3-Second Auto-Rotating Ad Slider
// ============================================================================
@Composable
fun HomeBentoDashboardScreen(
    config: AppConfig,
    activePlaylist: SavedPlaylistAccount?,
    isSyncing: Boolean,
    onSelectSection: (MainSection) -> Unit,
    onRefreshPlaylist: () -> Unit,
    onOpenServerStatus: () -> Unit,
    onLogoutAccount: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Main 4-Column Landscape Bento Grid (Zero item counts, 100% Iraqi/Arabic phrasing)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Column 1: Tall Live TV Card (البث المباشر)
            BentoTallLiveTvCard(
                isSyncing = isSyncing,
                onClick = { onSelectSection(MainSection.LIVE_TV) },
                modifier = Modifier
                    .weight(1.12f)
                    .fillMaxHeight()
                    .testTag("bento_live_tv_card")
            )

            // Column 2: Movies (الأفلام) + Sports & Matches (الرياضة والمباريات)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BentoStandardFeatureCard(
                    title = "الأفلام",
                    subtitle = "مكتبة الأفلام العربية والعالمية",
                    badgeText = "VOD 4K",
                    icon = Icons.Default.Movie,
                    gradientColors = listOf(Color(0xFF0D1E33), Color(0xFF17324D), Color(0xFF7C4D25)),
                    accentColor = DijlahGold,
                    onClick = { onSelectSection(MainSection.MOVIES) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("bento_movies_card")
                )

                BentoStandardFeatureCard(
                    title = "الرياضة والمباريات",
                    subtitle = "القنوات الرياضية والدوريات المباشرة",
                    badgeText = "LIVE SPORT",
                    icon = Icons.Default.SportsSoccer,
                    gradientColors = listOf(Color(0xFF0D1E33), Color(0xFF17324D), Color(0xFF0F2B46)),
                    accentColor = DijlahCyan,
                    onClick = { onSelectSection(MainSection.MATCHES) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("bento_matches_card")
                )
            }

            // Column 3: Series (المسلسلات) + Change Account / Playlists (تغيير الحساب / القوائم)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BentoStandardFeatureCard(
                    title = "المسلسلات",
                    subtitle = "أحدث المسلسلات والحلقات الكاملة",
                    badgeText = "SERIES",
                    icon = Icons.Default.VideoLibrary,
                    gradientColors = listOf(Color(0xFF0D1E33), Color(0xFF17324D), Color(0xFF7C4D25)),
                    accentColor = DijlahAmber,
                    onClick = { onSelectSection(MainSection.SERIES) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("bento_series_card")
                )

                BentoStandardFeatureCard(
                    title = "تغيير الحساب / القوائم",
                    subtitle = "إدارة اشتراكات وأكواد دجلة بلس",
                    badgeText = "VIP",
                    icon = Icons.AutoMirrored.Filled.PlaylistPlay,
                    gradientColors = listOf(Color(0xFF0D1E33), Color(0xFF17324D), Color(0xFF7C4D25)),
                    accentColor = DijlahGold,
                    onClick = { onSelectSection(MainSection.PLAYLIST_MANAGER) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("bento_playlists_card")
                )
            }

            // Column 4: 3-Second Auto-Rotating Ad Slider + 3 Action Buttons (الإعدادات | تحديث المحتوى | تسجيل الخروج)
            Column(
                modifier = Modifier
                    .weight(1.22f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HomeAutoRotatingAdSlider(
                    config = config,
                    onSelectSection = onSelectSection,
                    onOpenTelegram = {
                        try {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(config.telegramUrl.ifBlank { "https://t.me/Dijla_Plus" }))
                            )
                        } catch (_: Exception) {
                        }
                    },
                    modifier = Modifier
                        .weight(1.28f)
                        .fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .weight(0.72f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BentoSquareActionTile(
                        title = "الإعدادات",
                        icon = Icons.Default.Settings,
                        iconTint = DijlahGold,
                        onClick = { onSelectSection(MainSection.SETTINGS) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag("bento_settings_button")
                    )

                    BentoSquareActionTile(
                        title = if (isSyncing) "جاري التحديث" else "تحديث المحتوى",
                        icon = Icons.Default.Refresh,
                        iconTint = DijlahCyan,
                        onClick = onRefreshPlaylist,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag("bento_refresh_button")
                    )

                    BentoSquareActionTile(
                        title = "تسجيل الخروج",
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        iconTint = DijlahCrimson,
                        onClick = onLogoutAccount,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag("bento_logout_button")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bottom Subscription & MAC Bar (100% Stealth: Never show raw host or username)
        Surface(
            color = Color(0xFF0D1E33).copy(alpha = 0.94f),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "الاشتراك الحالي ينتهي في: ${activePlaylist?.expDate ?: "غير محدود"}",
                        color = DijlahTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "|",
                        color = DijlahGold.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                    Text(
                        text = "عنوان ماك : ${config.macAddress}",
                        color = DijlahCyan,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.clickable { onOpenServerStatus() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (config.isServerOnline) DijlahEmerald else DijlahCrimson)
                    )
                    Text(
                        text = activePlaylist?.playlistName?.ifBlank { "دجلة بلس" } ?: "دجلة بلس",
                        color = DijlahGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeAutoRotatingAdSlider(
    config: AppConfig,
    onSelectSection: (MainSection) -> Unit,
    onOpenTelegram: () -> Unit,
    modifier: Modifier = Modifier
) {
    val slides = remember(config.promoBannerSlides, config.adBannerTitle, config.adBannerSubtitle) {
        config.promoBannerSlides.ifEmpty {
            listOf(
                PromoBannerSlide(
                    id = "fallback_1",
                    title = config.adBannerTitle,
                    subtitle = config.adBannerSubtitle,
                    badgeText = config.adBannerBadge,
                    targetAction = "LIVE_TV",
                    accentHex = 0xFFD4AF37
                )
            )
        }
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    val intervalMs = config.adSliderIntervalMs.coerceIn(2000L, 10000L)

    LaunchedEffect(slides.size, intervalMs) {
        if (slides.size > 1) {
            while (true) {
                delay(intervalMs)
                currentIndex = (currentIndex + 1) % slides.size
            }
        }
    }

    val safeIndex = currentIndex % slides.size
    val activeSlide = slides[safeIndex]

    Surface(
        onClick = {
            when (activeSlide.targetAction.uppercase()) {
                "LIVE_TV" -> onSelectSection(MainSection.LIVE_TV)
                "MOVIES" -> onSelectSection(MainSection.MOVIES)
                "SERIES" -> onSelectSection(MainSection.SERIES)
                "MATCHES" -> onSelectSection(MainSection.MATCHES)
                "TELEGRAM" -> onOpenTelegram()
                else -> onSelectSection(MainSection.LIVE_TV)
            }
        },
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, DijlahGold.copy(alpha = 0.55f)),
        modifier = modifier.testTag("bento_ad_slider_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0D1E33),
                            Color(0xFF17324D),
                            Color(0xFF7C4D25)
                        )
                    )
                )
        ) {
            if (activeSlide.imageUrl.startsWith("http") || activeSlide.imageUrl.startsWith("/")) {
                NetworkImage(
                    url = activeSlide.imageUrl,
                    contentDescription = activeSlide.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0x88050A12),
                                    Color(0xE6050A12)
                                )
                            )
                        )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DijlahGold)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = activeSlide.badgeText.ifBlank { "دجلة بلس VIP" },
                            color = Color(0xFF050B14),
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        slides.forEachIndexed { idx, _ ->
                            val isSelected = idx == safeIndex
                            Box(
                                modifier = Modifier
                                    .height(6.dp)
                                    .width(if (isSelected) 16.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) DijlahGold else Color.White.copy(alpha = 0.35f))
                                    .clickable { currentIndex = idx }
                            )
                        }
                    }
                }

                AnimatedContent(
                    targetState = activeSlide,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "promoSlideTransition"
                ) { slide ->
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = slide.title,
                            color = DijlahTextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = slide.subtitle,
                            color = DijlahTextSecondary,
                            fontSize = 10.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 14.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            tint = DijlahCyan,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "تابعونا على التلغرام ${config.telegramHandle}",
                            color = DijlahCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                    DijlaLogo(size = 20.dp)
                }
            }
        }
    }
}

@Composable
private fun BentoTallLiveTvCard(
    isSyncing: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, DijlahGold.copy(alpha = 0.65f)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0D1E33),
                            Color(0xFF17324D),
                            Color(0xFF7C4D25)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.align(Alignment.TopStart),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DijlahCyan.copy(alpha = 0.2f))
                        .border(1.dp, DijlahCyan, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(DijlahCyan)
                        )
                        Text(
                            text = "LIVE TV",
                            color = DijlahCyan,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DijlaLogo(size = 76.dp)

                Text(
                    text = "البث المباشر",
                    color = DijlahTextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                )

                Text(
                    text = if (isSyncing) "جاري تحديث الباقات..." else "مشاهدة جميع القنوات والباقات المباشرة",
                    color = DijlahGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF050A12).copy(alpha = 0.6f))
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LiveTv,
                    contentDescription = null,
                    tint = DijlahGold,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "بث فائق الجودة بدون تقطيع",
                    color = DijlahTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun BentoStandardFeatureCard(
    title: String,
    subtitle: String,
    badgeText: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradientColors))
                .padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(6.dp))
                    .background(accentColor.copy(alpha = 0.2f))
                    .border(0.5.dp, accentColor, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badgeText,
                    color = accentColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 9.sp
                )
            }

            Column(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.18f))
                        .border(1.dp, accentColor.copy(alpha = 0.55f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = title,
                    color = DijlahTextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = subtitle,
                    color = DijlahTextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun BentoSquareActionTile(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, iconTint.copy(alpha = 0.45f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF0D1E33),
                            Color(0xFF17324D),
                            Color(0xFF7C4D25)
                        )
                    )
                )
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.18f))
                    .border(1.dp, iconTint.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(19.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = DijlahTextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ============================================================================
// SHARED SIDEBAR FOR LIVE TV, MOVIES & SERIES (22% Width, Zero Item Counts)
// ============================================================================
@Composable
private fun MediaCategoriesSidebar(
    sectionTitle: String,
    topIcon: ImageVector,
    categories: List<String>,
    selectedCategory: String,
    lockedCategories: List<String>,
    sidebarSearchQuery: String,
    onSidebarSearchChanged: (String) -> Unit,
    onSelectCategory: (String) -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSidebarSearchInput by remember { mutableStateOf(false) }

    val displayedCategories = remember(categories, sidebarSearchQuery) {
        if (sidebarSearchQuery.isBlank()) categories
        else categories.filter { it.contains(sidebarSearchQuery, ignoreCase = true) }
    }

    Surface(
        color = Color(0xFF081322),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top Large Golden SVG/Vector Icon
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(DijlahGold.copy(alpha = 0.28f), Color(0xFF0D1E33))
                        )
                    )
                    .border(1.5.dp, DijlahGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = topIcon,
                    contentDescription = sectionTitle,
                    tint = DijlahGold,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Action Row: عودة & بحث
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    onClick = onBackToHome,
                    color = Color(0xFF0D1E33),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .testTag("sidebar_back_button")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "عودة",
                            tint = DijlahGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "عودة",
                            color = DijlahTextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    onClick = {
                        showSidebarSearchInput = !showSidebarSearchInput
                        if (!showSidebarSearchInput) onSidebarSearchChanged("")
                    },
                    color = if (showSidebarSearchInput) DijlahGold else Color(0xFF0D1E33),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .testTag("sidebar_search_button")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "بحث",
                            tint = if (showSidebarSearchInput) Color(0xFF050B14) else DijlahGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "بحث",
                            color = if (showSidebarSearchInput) Color(0xFF050B14) else DijlahTextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Instant Category Filter Input when "بحث" is toggled
            AnimatedVisibility(visible = showSidebarSearchInput) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0D1E33))
                        .border(1.dp, DijlahGold, RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (sidebarSearchQuery.isEmpty()) {
                        Text(
                            text = "تصفية الأقسام...",
                            color = DijlahTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    BasicTextField(
                        value = sidebarSearchQuery,
                        onValueChange = onSidebarSearchChanged,
                        singleLine = true,
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        cursorBrush = SolidColor(DijlahGold),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            HorizontalDivider(color = DijlahGold.copy(alpha = 0.2f))

            // Vertical Scrollable List of ALL Categories (Zero Counts / Numbers)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(displayedCategories, key = { it }) { catName ->
                    val isSelected = catName == selectedCategory
                    val isLocked = lockedCategories.contains(catName)

                    Surface(
                        onClick = { onSelectCategory(catName) },
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) DijlahGold else Color.White.copy(alpha = 0.08f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isSelected) {
                                        Brush.horizontalGradient(
                                            listOf(DijlahGold, DijlahAmber)
                                        )
                                    } else {
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF0D1E33), Color(0xFF132942))
                                        )
                                    }
                                )
                                .padding(horizontal = 10.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = catName,
                                color = if (isSelected) Color(0xFF050B14) else DijlahTextPrimary,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            if (isLocked) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "مقفل",
                                    tint = if (isSelected) Color(0xFF050B14) else DijlahGold,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 2. MATCHES SCHEDULE SCREEN (الرياضة والمباريات)
// ============================================================================
@Composable
fun MatchesScheduleScreen(
    matches: List<MatchItem>,
    searchQuery: String,
    isSyncing: Boolean,
    onPlayMatch: (MatchItem) -> Unit,
    onRefreshServer: () -> Unit
) {
    val filtered = remember(matches, searchQuery) {
        matches.filter {
            searchQuery.isBlank() ||
                it.homeTeam.contains(searchQuery, ignoreCase = true) ||
                it.awayTeam.contains(searchQuery, ignoreCase = true) ||
                it.tournament.contains(searchQuery, ignoreCase = true) ||
                it.linkedChannelName.contains(searchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsSoccer,
                            contentDescription = null,
                            tint = DijlahGold,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "الرياضة والمباريات المباشرة",
                            color = DijlahTextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                    }
                    Text(
                        text = "اضغط على أي قناة رياضية لبدء البث المباشر فوراً",
                        color = DijlahTextSecondary,
                        fontSize = 12.sp
                    )
                }
                OutlinedButton(
                    onClick = onRefreshServer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = DijlahGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تحديث المحتوى", color = DijlahGold)
                }
            }
        }

        if (filtered.isEmpty()) {
            item {
                EmptyStateCard(
                    message = if (isSyncing) "جاري تحميل القنوات الرياضية المباشرة..." else "لا توجد قنوات رياضية مطابقة للبحث حالياً"
                )
            }
        } else {
            items(filtered, key = { it.id }) { match ->
                MatchBroadcastCard(match = match, onWatchClick = { onPlayMatch(match) })
            }
        }
    }
}

@Composable
fun MatchBroadcastCard(
    match: MatchItem,
    onWatchClick: () -> Unit
) {
    Surface(
        onClick = onWatchClick,
        shape = RoundedCornerShape(16.dp),
        color = DijlahSurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("match_card_${match.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF0D1E33), Color(0xFF17324D), Color(0xFF7C4D25))
                    )
                )
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                DijlaLogo(size = 44.dp)
                Column {
                    Text(
                        text = match.homeTeam,
                        color = DijlahTextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(DijlahCyan)
                        )
                        Text(
                            text = "${match.tournament} • ${match.matchTime}",
                            color = DijlahCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Button(
                onClick = onWatchClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DijlahGold,
                    contentColor = Color(0xFF050B14)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("مشاهدة البث", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
            }
        }
    }
}

// ============================================================================
// 3. LIVE TV SCREEN (البث المباشر — Left Sidebar without numbers + Main Grid)
// ============================================================================
@Composable
fun LiveTvSectionScreen(
    channels: List<LiveChannel>,
    serverLiveCategories: List<String>,
    hiddenCategories: List<String>,
    recentlyWatchedIds: List<String>,
    favoriteIds: Set<String>,
    selectedCategory: String,
    lockedCategories: List<String>,
    searchQuery: String,
    isSyncing: Boolean,
    onSelectCategory: (String) -> Unit,
    onPlayChannel: (LiveChannel) -> Unit,
    onPlayChannelByNumber: (Int) -> Unit = {},
    onToggleFavorite: (String) -> Unit,
    onRefreshServer: () -> Unit,
    onBackToHome: () -> Unit = {}
) {
    var showNumberZapDialog by remember { mutableStateOf(false) }
    var zapNumberInput by remember { mutableStateOf("") }
    var sidebarFilter by remember { mutableStateOf("") }

    val categories = remember(channels, serverLiveCategories, hiddenCategories) {
        val rawCats = (serverLiveCategories + channels.map { it.category })
            .map { it.trim() }
            .filter { it.isNotBlank() && !hiddenCategories.contains(it) }
            .distinct()
        listOf("شوهدت مؤخراً", "الكل", "المفضلة") + rawCats
    }

    val filteredChannels = remember(channels, selectedCategory, searchQuery, sidebarFilter, favoriteIds, recentlyWatchedIds, hiddenCategories) {
        val baseList = when (selectedCategory) {
            "شوهدت مؤخراً" -> {
                val mapById = channels.associateBy { it.id }
                recentlyWatchedIds.mapNotNull { mapById[it] }
            }
            "المفضلة" -> channels.filter { favoriteIds.contains(it.id) }
            "الكل" -> channels.filter { !hiddenCategories.contains(it.category) }
            else -> channels.filter { it.category == selectedCategory }
        }

        val effectiveQuery = searchQuery.ifBlank { sidebarFilter }
        if (effectiveQuery.isBlank()) {
            baseList
        } else {
            baseList.filter { ch ->
                ch.name.contains(effectiveQuery, ignoreCase = true) ||
                    ch.category.contains(effectiveQuery, ignoreCase = true) ||
                    ch.channelNumber.toString() == effectiveQuery.trim()
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Right Main Content Area (78% width)
        Column(
            modifier = Modifier
                .weight(0.78f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top bar with Category Title (Zero numbers) + Number Zapping Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0D1E33))
                    .border(1.dp, DijlahGold.copy(alpha = 0.28f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LiveTv,
                        contentDescription = null,
                        tint = DijlahGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = selectedCategory,
                        color = DijlahTextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                }

                OutlinedButton(
                    onClick = { showNumberZapDialog = true },
                    border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(32.dp)
                        .testTag("channel_number_zap_button")
                ) {
                    Icon(Icons.Default.Dialpad, contentDescription = "رقم القناة", tint = DijlahGold, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("رقم القناة", color = DijlahGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }

            if (filteredChannels.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        EmptyStateCard(
                            message = if (isSyncing) "جاري تحميل القنوات المباشرة من سيرفر دجلة بلس..." else "لا توجد قنوات في هذا القسم حالياً"
                        )
                        if (!isSyncing) {
                            Button(
                                onClick = onRefreshServer,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DijlahGold,
                                    contentColor = Color(0xFF050B14)
                                )
                            ) {
                                Text("تحديث القنوات الآن", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredChannels, key = { it.id }) { channel ->
                        LiveChannelCard(
                            channel = channel,
                            isFavorite = favoriteIds.contains(channel.id),
                            onPlay = { onPlayChannel(channel) },
                            onToggleFavorite = { onToggleFavorite(channel.id) }
                        )
                    }
                }
            }
        }

        // Left Sidebar (22% width, Zero counts)
        MediaCategoriesSidebar(
            sectionTitle = "البث المباشر",
            topIcon = Icons.Default.LiveTv,
            categories = categories,
            selectedCategory = selectedCategory,
            lockedCategories = lockedCategories,
            sidebarSearchQuery = sidebarFilter,
            onSidebarSearchChanged = { sidebarFilter = it },
            onSelectCategory = onSelectCategory,
            onBackToHome = onBackToHome,
            modifier = Modifier
                .weight(0.22f)
                .fillMaxHeight()
        )
    }

    if (showNumberZapDialog) {
        Dialog(onDismissRequest = { showNumberZapDialog = false }) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = DijlahBgSecondary,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, DijlahGold),
                modifier = Modifier.widthIn(max = 360.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "الانتقال السريع برقم القناة",
                        color = DijlahTextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                    OutlinedTextField(
                        value = zapNumberInput,
                        onValueChange = { zapNumberInput = it.filter { ch -> ch.isDigit() }.take(5) },
                        label = { Text("أدخل رقم القناة") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DijlahGold,
                            focusedTextColor = DijlahTextPrimary,
                            unfocusedTextColor = DijlahTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showNumberZapDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("إلغاء", color = DijlahTextSecondary)
                        }
                        Button(
                            onClick = {
                                val num = zapNumberInput.toIntOrNull()
                                if (num != null) {
                                    showNumberZapDialog = false
                                    zapNumberInput = ""
                                    onPlayChannelByNumber(num)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DijlahGold,
                                contentColor = Color(0xFF050B14)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("تشغيل القناة", fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiveChannelCard(
    channel: LiveChannel,
    isFavorite: Boolean,
    onPlay: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Surface(
        onClick = onPlay,
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("channel_card_${channel.id}")
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF0D1E33), Color(0xFF17324D), Color(0xFF7C4D25))
                    )
                )
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF050B14))
                        .border(1.dp, DijlahGold.copy(alpha = 0.55f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (channel.streamIconUrl.startsWith("http")) {
                        NetworkImage(
                            url = channel.streamIconUrl,
                            contentDescription = channel.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp),
                            contentScale = ContentScale.Fit,
                            fallbackContent = {
                                DijlaLogo(size = 32.dp)
                            }
                        )
                    } else {
                        DijlaLogo(size = 32.dp)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (channel.isEncrypted || channel.isAdult) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "مشفر",
                            tint = DijlahGold,
                            modifier = Modifier
                                .size(15.dp)
                                .padding(end = 2.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DijlahCyan.copy(alpha = 0.18f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = channel.qualityBadge,
                            color = DijlahCyan,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 9.sp
                        )
                    }
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "مفضلة",
                            tint = if (isFavorite) DijlahCrimson else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Text(
                text = channel.name,
                color = DijlahTextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = channel.category,
                color = DijlahGold,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ============================================================================
// 4. MOVIES VOD SCREEN (الأفلام — Matching Screenshots 2 & 3)
// Left Sidebar (22%) without numbers + Right 5-Column Poster Grid (78%)
// ============================================================================
@Composable
fun MoviesVodScreen(
    movies: List<VodMovie>,
    serverVodCategories: List<String>,
    hiddenCategories: List<String>,
    favoriteIds: Set<String>,
    savedPlaybackPositions: Map<String, Long> = emptyMap(),
    selectedCategory: String,
    lockedCategories: List<String>,
    searchQuery: String,
    isSyncing: Boolean,
    onSelectCategory: (String) -> Unit,
    onPlayMovie: (VodMovie) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onBackToHome: () -> Unit = {}
) {
    var sortOption by remember { mutableStateOf(VodSortOption.ADDED) }
    var showSortDropdown by remember { mutableStateOf(false) }
    var sidebarFilter by remember { mutableStateOf("") }

    val categories = remember(movies, serverVodCategories, hiddenCategories) {
        val rawCats = (serverVodCategories + movies.map { it.category })
            .map { it.trim() }
            .filter { it.isNotBlank() && !hiddenCategories.contains(it) }
            .distinct()
        listOf("استئناف للمشاهدة", "الكل", "المفضلة") + rawCats
    }

    val filtered = remember(movies, selectedCategory, searchQuery, sidebarFilter, favoriteIds, savedPlaybackPositions, sortOption, hiddenCategories) {
        val baseList = when (selectedCategory) {
            "استئناف للمشاهدة" -> movies.filter { (savedPlaybackPositions[it.id] ?: 0L) > 5000L }
            "المفضلة" -> movies.filter { favoriteIds.contains(it.id) }
            "الكل" -> movies.filter { !hiddenCategories.contains(it.category) }
            else -> movies.filter { it.category == selectedCategory }
        }

        val effectiveQuery = searchQuery.ifBlank { sidebarFilter }
        val searched = if (effectiveQuery.isBlank()) {
            baseList
        } else {
            baseList.filter { movie ->
                movie.title.contains(effectiveQuery, ignoreCase = true) ||
                    movie.category.contains(effectiveQuery, ignoreCase = true)
            }
        }

        when (sortOption) {
            VodSortOption.ADDED -> searched.sortedByDescending { it.addedTimestamp }
            VodSortOption.RATING -> searched.sortedByDescending { it.rating.toDoubleOrNull() ?: 0.0 }
            VodSortOption.ALPHA -> searched.sortedBy { it.title }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Right Main Grid (78% width)
        Column(
            modifier = Modifier
                .weight(0.78f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top Bar: Current Category Title (Zero numbers) + Sort Dropdown ("بحسب ما تم إضافته")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0D1E33))
                    .border(1.dp, DijlahGold.copy(alpha = 0.28f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = null,
                        tint = DijlahGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = selectedCategory,
                        color = DijlahTextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                }

                Box {
                    OutlinedButton(
                        onClick = { showSortDropdown = true },
                        border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("movies_sort_dropdown_button")
                    ) {
                        Text(
                            text = sortOption.labelAr,
                            color = DijlahGold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = DijlahGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showSortDropdown,
                        onDismissRequest = { showSortDropdown = false },
                        modifier = Modifier.background(Color(0xFF0D1E33))
                    ) {
                        VodSortOption.entries.forEach { opt ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = opt.labelAr,
                                        color = if (opt == sortOption) DijlahGold else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                },
                                onClick = {
                                    sortOption = opt
                                    showSortDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateCard(
                        message = if (isSyncing) "جاري تحميل مكتبة الأفلام من السيرفر..." else "لا توجد أفلام في هذا القسم حالياً"
                    )
                }
            } else {
                // 5-Column Poster Grid matching Screenshots 2 & 3
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filtered, key = { it.id }) { movie ->
                        VodPosterCard(
                            title = movie.title,
                            rating = movie.rating,
                            qualityBadge = movie.qualityBadge,
                            customPosterUri = movie.customPosterUri,
                            isFavorite = favoriteIds.contains(movie.id),
                            hasSavedProgress = (savedPlaybackPositions[movie.id] ?: 0L) > 5000L,
                            onClick = { onPlayMovie(movie) },
                            onToggleFavorite = { onToggleFavorite(movie.id) },
                            testTag = "movie_card_${movie.id}"
                        )
                    }
                }
            }
        }

        // Left Sidebar (22% width, Zero counts)
        MediaCategoriesSidebar(
            sectionTitle = "الأفلام",
            topIcon = Icons.Default.Movie,
            categories = categories,
            selectedCategory = selectedCategory,
            lockedCategories = lockedCategories,
            sidebarSearchQuery = sidebarFilter,
            onSidebarSearchChanged = { sidebarFilter = it },
            onSelectCategory = onSelectCategory,
            onBackToHome = onBackToHome,
            modifier = Modifier
                .weight(0.22f)
                .fillMaxHeight()
        )
    }
}

// ============================================================================
// 5. SERIES VOD SCREEN (المسلسلات — Matching Screenshot 4)
// Left Sidebar (22%) without numbers + Right 5-Column Poster Grid (78%)
// ============================================================================
@Composable
fun SeriesVodScreen(
    seriesList: List<VodSeries>,
    serverSeriesCategories: List<String>,
    hiddenCategories: List<String>,
    recentlyWatchedSeriesIds: List<String>,
    favoriteIds: Set<String>,
    selectedCategory: String,
    lockedCategories: List<String>,
    searchQuery: String,
    isSyncing: Boolean,
    onSelectCategory: (String) -> Unit,
    onOpenSeries: (VodSeries) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onBackToHome: () -> Unit = {}
) {
    var sortOption by remember { mutableStateOf(VodSortOption.ADDED) }
    var showSortDropdown by remember { mutableStateOf(false) }
    var sidebarFilter by remember { mutableStateOf("") }

    val categories = remember(seriesList, serverSeriesCategories, hiddenCategories) {
        val rawCats = (serverSeriesCategories + seriesList.map { it.category })
            .map { it.trim() }
            .filter { it.isNotBlank() && !hiddenCategories.contains(it) }
            .distinct()
        listOf("شوهدت مؤخراً", "الكل", "المفضلة") + rawCats
    }

    val filtered = remember(seriesList, selectedCategory, searchQuery, sidebarFilter, favoriteIds, recentlyWatchedSeriesIds, sortOption, hiddenCategories) {
        val baseList = when (selectedCategory) {
            "شوهدت مؤخراً" -> {
                val mapById = seriesList.associateBy { it.id }
                recentlyWatchedSeriesIds.mapNotNull { mapById[it] }
            }
            "المفضلة" -> seriesList.filter { favoriteIds.contains(it.id) }
            "الكل" -> seriesList.filter { !hiddenCategories.contains(it.category) }
            else -> seriesList.filter { it.category == selectedCategory }
        }

        val effectiveQuery = searchQuery.ifBlank { sidebarFilter }
        val searched = if (effectiveQuery.isBlank()) {
            baseList
        } else {
            baseList.filter { s ->
                s.title.contains(effectiveQuery, ignoreCase = true) ||
                    s.category.contains(effectiveQuery, ignoreCase = true)
            }
        }

        when (sortOption) {
            VodSortOption.ADDED -> searched.sortedByDescending { it.lastModifiedTimestamp }
            VodSortOption.RATING -> searched.sortedByDescending { it.rating.toDoubleOrNull() ?: 0.0 }
            VodSortOption.ALPHA -> searched.sortedBy { it.title }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Right Main Grid (78% width)
        Column(
            modifier = Modifier
                .weight(0.78f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top Bar: Current Category Title (Zero numbers) + Sort Dropdown ("بحسب ما تم إضافته")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0D1E33))
                    .border(1.dp, DijlahGold.copy(alpha = 0.28f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VideoLibrary,
                        contentDescription = null,
                        tint = DijlahGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = selectedCategory,
                        color = DijlahTextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                }

                Box {
                    OutlinedButton(
                        onClick = { showSortDropdown = true },
                        border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("series_sort_dropdown_button")
                    ) {
                        Text(
                            text = sortOption.labelAr,
                            color = DijlahGold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = DijlahGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showSortDropdown,
                        onDismissRequest = { showSortDropdown = false },
                        modifier = Modifier.background(Color(0xFF0D1E33))
                    ) {
                        VodSortOption.entries.forEach { opt ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = opt.labelAr,
                                        color = if (opt == sortOption) DijlahGold else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                },
                                onClick = {
                                    sortOption = opt
                                    showSortDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateCard(
                        message = if (isSyncing) "جاري تحميل مكتبة المسلسلات من السيرفر..." else "لا توجد مسلسلات في هذا القسم حالياً"
                    )
                }
            } else {
                // 5-Column Poster Grid matching Screenshot 4
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filtered, key = { it.id }) { series ->
                        VodPosterCard(
                            title = series.title,
                            rating = series.rating,
                            qualityBadge = series.qualityBadge,
                            customPosterUri = series.customPosterUri,
                            isFavorite = favoriteIds.contains(series.id),
                            hasSavedProgress = recentlyWatchedSeriesIds.contains(series.id),
                            onClick = { onOpenSeries(series) },
                            onToggleFavorite = { onToggleFavorite(series.id) },
                            testTag = "series_card_${series.id}"
                        )
                    }
                }
            }
        }

        // Left Sidebar (22% width, Zero counts)
        MediaCategoriesSidebar(
            sectionTitle = "المسلسلات",
            topIcon = Icons.Default.VideoLibrary,
            categories = categories,
            selectedCategory = selectedCategory,
            lockedCategories = lockedCategories,
            sidebarSearchQuery = sidebarFilter,
            onSidebarSearchChanged = { sidebarFilter = it },
            onSelectCategory = onSelectCategory,
            onBackToHome = onBackToHome,
            modifier = Modifier
                .weight(0.22f)
                .fillMaxHeight()
        )
    }
}

/**
 * 5-Column Poster Card (aspect-ratio: 2/3) with automatic DijlaFallbackPoster
 * when `stream_icon` / `cover` is empty or fails to load, and a bronze-gold gradient bottom title strip.
 */
@Composable
fun VodPosterCard(
    title: String,
    rating: String,
    qualityBadge: String,
    customPosterUri: String?,
    isFavorite: Boolean,
    hasSavedProgress: Boolean = false,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    testTag: String
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF0D1E33),
        border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.38f)),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f / 3f)
            .testTag(testTag)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (!customPosterUri.isNullOrBlank()) {
                NetworkImage(
                    url = customPosterUri,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    fallbackContent = {
                        DijlaFallbackPoster(
                            modifier = Modifier.fillMaxSize(),
                            logoSize = 52.dp,
                            showBrandLabel = true
                        )
                    }
                )
            } else {
                DijlaFallbackPoster(
                    modifier = Modifier.fillMaxSize(),
                    logoSize = 52.dp,
                    showBrandLabel = true
                )
            }

            // Quality Badge Top-Start
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.72f))
                    .border(0.5.dp, DijlahGold.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = qualityBadge,
                    color = DijlahGold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 8.sp
                )
            }

            // Favorite Button Top-End
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.58f))
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "مفضلة",
                    tint = if (isFavorite) DijlahCrimson else Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }

            // Resume Indicator if partially watched
            if (hasSavedProgress) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(DijlahCyan.copy(alpha = 0.92f))
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "شوهد",
                        color = Color(0xFF050B14),
                        fontWeight = FontWeight.Black,
                        fontSize = 8.sp
                    )
                }
            }

            // Bronze-Gold Gradient Bottom Title Strip matching Screenshots 2–4
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xDD0D1E33),
                                Color(0xF57C4D25)
                            )
                        )
                    )
                    .padding(horizontal = 8.dp, vertical = 7.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ============================================================================
// SERIES SEASONS & EPISODES MODAL (Normalized Seasons Sidebar + S01E01 Episodes)
// ============================================================================
@Composable
fun SeriesDetailModal(
    series: VodSeries,
    isLoadingEpisodes: Boolean,
    savedPlaybackPositions: Map<String, Long> = emptyMap(),
    onPlayEpisode: (SeriesSeason, SeriesEpisode) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedSeasonIndex by remember(series.seasons) { mutableIntStateOf(0) }
    val currentSeason = series.seasons.getOrNull(selectedSeasonIndex)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = DijlahBgSecondary,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, DijlahGold.copy(alpha = 0.55f)),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 780.dp)
                .heightIn(min = 380.dp, max = 560.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF050A12), Color(0xFF0D1E33), Color(0xFF17324D))
                        )
                    )
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        DijlaLogo(size = 42.dp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = series.title,
                                color = DijlahTextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${series.category} • ${series.year}",
                                color = DijlahGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                    }
                }

                if (isLoadingEpisodes) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(color = DijlahGold)
                            Text(
                                text = "جاري جلب المواسم والحلقات من سيرفر دجلة بلس...",
                                color = DijlahTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                } else if (series.seasons.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyStateCard("لا توجد حلقات متاحة لهذا المسلسل على السيرفر حالياً")
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Left Sidebar: Seasons List (الموسم 1, الموسم 2...) without numbers
                        LazyColumn(
                            modifier = Modifier
                                .weight(0.28f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF081322))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(series.seasons.size) { idx ->
                                val season = series.seasons[idx]
                                val selected = idx == selectedSeasonIndex
                                Surface(
                                    onClick = { selectedSeasonIndex = idx },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selected) DijlahGold else Color(0xFF0D1E33),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (selected) DijlahGold else Color.White.copy(alpha = 0.1f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = season.title,
                                        color = if (selected) Color(0xFF050B14) else Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                                    )
                                }
                            }
                        }

                        // Right Panel: Episodes List (S01E01, S01E02...)
                        if (currentSeason != null) {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(0.72f)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(currentSeason.episodes, key = { it.id }) { ep ->
                                    val savedMs = savedPlaybackPositions[ep.id] ?: 0L
                                    val epCode = String.format(
                                        Locale.US,
                                        "S%02dE%02d",
                                        currentSeason.seasonNumber.coerceAtLeast(1),
                                        ep.episodeNumber.coerceAtLeast(1)
                                    )
                                    Surface(
                                        onClick = { onPlayEpisode(currentSeason, ep) },
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFF0D1E33),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (savedMs > 5000L) DijlahCyan else DijlahGold.copy(alpha = 0.35f)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("episode_item_${ep.id}")
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 14.dp, vertical = 10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(DijlahGold)
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Text(
                                                        text = epCode,
                                                        color = Color(0xFF050B14),
                                                        fontWeight = FontWeight.Black,
                                                        fontSize = 11.sp
                                                    )
                                                }
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = "الحلقة ${ep.episodeNumber} — ${ep.title}",
                                                        color = DijlahTextPrimary,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        fontSize = 13.sp,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        Text(
                                                            text = ep.duration,
                                                            color = DijlahTextSecondary,
                                                            fontSize = 10.sp
                                                        )
                                                        if (savedMs > 5000L) {
                                                            Text(
                                                                text = "• استئناف المشاهدة متاح",
                                                                color = DijlahCyan,
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 10.sp
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            Icon(
                                                imageVector = Icons.Default.PlayCircleFilled,
                                                contentDescription = "تشغيل الحلقة",
                                                tint = DijlahGold,
                                                modifier = Modifier.size(26.dp)
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

// ============================================================================
// 6. FAVORITES SCREEN (المفضلة — Zero counts)
// ============================================================================
@Composable
fun FavoritesScreen(
    channels: List<LiveChannel>,
    movies: List<VodMovie>,
    seriesList: List<VodSeries>,
    favoriteIds: Set<String>,
    onPlayChannel: (LiveChannel) -> Unit,
    onPlayMovie: (VodMovie) -> Unit,
    onOpenSeries: (VodSeries) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    val favChannels = channels.filter { favoriteIds.contains(it.id) }
    val favMovies = movies.filter { favoriteIds.contains(it.id) }
    val favSeries = seriesList.filter { favoriteIds.contains(it.id) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = DijlahCrimson)
                Text(
                    text = "المفضلة",
                    color = DijlahTextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }
        }

        if (favChannels.isEmpty() && favMovies.isEmpty() && favSeries.isEmpty()) {
            item {
                EmptyStateCard(message = "قائمة المفضلة فارغة حالياً. اضغط على أيقونة القلب لإضافة القنوات أو الأفلام أو المسلسلات.")
            }
        }

        if (favChannels.isNotEmpty()) {
            item {
                Text(
                    text = "القنوات المفضلة",
                    color = DijlahGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            items(favChannels, key = { it.id }) { ch ->
                LiveChannelCard(
                    channel = ch,
                    isFavorite = true,
                    onPlay = { onPlayChannel(ch) },
                    onToggleFavorite = { onToggleFavorite(ch.id) }
                )
            }
        }

        if (favMovies.isNotEmpty()) {
            item {
                Text(
                    text = "الأفلام المفضلة",
                    color = DijlahGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            items(favMovies, key = { it.id }) { movie ->
                VodPosterCard(
                    title = movie.title,
                    rating = movie.rating,
                    qualityBadge = movie.qualityBadge,
                    customPosterUri = movie.customPosterUri,
                    isFavorite = true,
                    onClick = { onPlayMovie(movie) },
                    onToggleFavorite = { onToggleFavorite(movie.id) },
                    testTag = "fav_movie_${movie.id}"
                )
            }
        }

        if (favSeries.isNotEmpty()) {
            item {
                Text(
                    text = "المسلسلات المفضلة",
                    color = DijlahGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            items(favSeries, key = { it.id }) { series ->
                VodPosterCard(
                    title = series.title,
                    rating = series.rating,
                    qualityBadge = series.qualityBadge,
                    customPosterUri = series.customPosterUri,
                    isFavorite = true,
                    onClick = { onOpenSeries(series) },
                    onToggleFavorite = { onToggleFavorite(series.id) },
                    testTag = "fav_series_${series.id}"
                )
            }
        }
    }
}

// ============================================================================
// 7. PLAYLIST MANAGER SCREEN (تغيير الحساب / القوائم)
// ============================================================================
@Composable
fun PlaylistManagerScreen(
    config: AppConfig,
    savedPlaylists: List<SavedPlaylistAccount>,
    isSyncing: Boolean,
    onActivateByCode: (String) -> Unit,
    onLoginXtream: (String, String, String, String) -> Unit,
    onLoginM3u: (String, String) -> Unit,
    onActivateSavedPlaylist: (SavedPlaylistAccount) -> Unit,
    onDeleteSavedPlaylist: (String) -> Unit,
    onOpenLoginScreen: () -> Unit = {}
) {
    var selectedLoginTab by remember { mutableIntStateOf(0) } // 0: Code, 1: VIP User/Pass
    var activationCodeInput by remember { mutableStateOf("") }
    var xtreamName by remember { mutableStateOf("دجلة بلس") }
    var xtreamUser by remember { mutableStateOf("") }
    var xtreamPass by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Right Column: Saved Playlists List (100% Server Stealth)
        Surface(
            color = DijlahSurfaceCard,
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.35f)),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الحسابات والقوائم المحفوظة",
                        color = DijlahGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "MAC: ${config.macAddress}",
                        color = DijlahCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(savedPlaylists, key = { it.id }) { item ->
                        SavedPlaylistCard(
                            account = item,
                            onActivate = { onActivateSavedPlaylist(item) },
                            onDelete = { onDeleteSavedPlaylist(item.id) }
                        )
                    }
                }

                OutlinedButton(
                    onClick = onOpenLoginScreen,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Login, contentDescription = null, tint = DijlahGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("فتح شاشة تسجيل الدخول الرئيسية", color = DijlahGold, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Left Column: Quick Add / Switch Account
        Surface(
            color = DijlahSurfaceCard,
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tabs = listOf("الدخول بكود تفعيل", "حساب مشترك VIP")
                    tabs.forEachIndexed { idx, title ->
                        val selected = selectedLoginTab == idx
                        Button(
                            onClick = { selectedLoginTab = idx },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) DijlahGold else DijlahBgSecondary,
                                contentColor = if (selected) Color(0xFF050B14) else DijlahTextSecondary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                        }
                    }
                }

                when (selectedLoginTab) {
                    0 -> {
                        Text(
                            text = "أدخل كود التفعيل (مثال: D1)",
                            color = DijlahTextSecondary,
                            fontSize = 12.sp
                        )
                        OutlinedTextField(
                            value = activationCodeInput,
                            onValueChange = { activationCodeInput = it },
                            label = { Text("كود التفعيل") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("activation_code_input")
                        )
                        Button(
                            onClick = { onActivateByCode(activationCodeInput) },
                            enabled = !isSyncing,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DijlahGold,
                                contentColor = Color(0xFF050B14)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("activate_code_submit_button")
                        ) {
                            Text(
                                text = if (isSyncing) "جاري التحقق..." else "تفعيل الكود وتشغيل القنوات",
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    1 -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            item {
                                OutlinedTextField(
                                    value = xtreamName,
                                    onValueChange = { xtreamName = it },
                                    label = { Text("تسمية قائمة التشغيل") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            item {
                                OutlinedTextField(
                                    value = xtreamUser,
                                    onValueChange = { xtreamUser = it },
                                    label = { Text("اسم المستخدم") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            item {
                                OutlinedTextField(
                                    value = xtreamPass,
                                    onValueChange = { xtreamPass = it },
                                    label = { Text("كلمة المرور") },
                                    singleLine = true,
                                    visualTransformation = PasswordVisualTransformation(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            item {
                                Button(
                                    onClick = {
                                        onLoginXtream(xtreamName, "", xtreamUser, xtreamPass)
                                    },
                                    enabled = !isSyncing,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DijlahGold,
                                        contentColor = Color(0xFF050B14)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (isSyncing) "جاري الاتصال..." else "تسجيل الدخول",
                                        fontWeight = FontWeight.ExtraBold
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

@Composable
private fun SavedPlaylistCard(
    account: SavedPlaylistAccount,
    onActivate: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        onClick = onActivate,
        color = if (account.isActive) DijlahSurfaceElevated else DijlahBgSecondary,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (account.isActive) DijlahGold else Color.White.copy(alpha = 0.08f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (account.isActive) DijlahEmerald else DijlahTextSecondary)
                    )
                    Text(
                        text = account.playlistName,
                        color = DijlahTextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "بوابة دجلة بلس VIP المشفرة",
                    color = DijlahGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "ينتهي في: ${account.expDate}",
                    color = DijlahEmerald,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (!account.isActive) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف القائمة",
                        tint = DijlahCrimson,
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(DijlahGold)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "نشط",
                        color = Color(0xFF050B14),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

// ============================================================================
// 8. SETTINGS SCREEN — All 20 Polished Iraqi/Arabic Settings Cards + 5-Tap MAC
// ============================================================================
private data class IraqiSettingItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun SettingsScreen(
    config: AppConfig,
    liveCategories: List<String> = emptyList(),
    vodCategories: List<String> = emptyList(),
    seriesCategories: List<String> = emptyList(),
    onUpdateConfig: (AppConfig) -> Unit,
    onOpenPlaylistManager: () -> Unit,
    onOpenLoginScreen: () -> Unit,
    onRefreshPlaylistsNow: () -> Unit,
    onClearChannelsHistory: () -> Unit,
    onClearMoviesHistory: () -> Unit,
    onClearSeriesHistory: () -> Unit,
    onMacAddressTapped: () -> Unit = {},
    onPostToast: (String) -> Unit = {}
) {
    var showParentalDialog by remember { mutableStateOf(false) }
    var hideModalType by remember { mutableStateOf<String?>(null) } // "LIVE", "VOD", "SERIES"

    val settingItems = remember(config) {
        listOf(
            IraqiSettingItem(
                id = "add_account",
                title = "إضافة حساب أو كود",
                subtitle = "الدخول بحساب VIP أو كود تفعيل (D1)",
                icon = Icons.Default.PersonAdd,
                onClick = onOpenLoginScreen
            ),
            IraqiSettingItem(
                id = "parental_lock",
                title = "القفل العائلي",
                subtitle = if (config.lockAdultContent) "مفعل (${config.defaultParentalPin})" else "غير مفعل",
                icon = Icons.Default.Lock,
                onClick = { showParentalDialog = true }
            ),
            IraqiSettingItem(
                id = "switch_accounts",
                title = "تبديل الحسابات",
                subtitle = "إدارة وتبديل قوائم التشغيل المحفوظة",
                icon = Icons.Default.SwitchAccount,
                onClick = onOpenPlaylistManager
            ),
            IraqiSettingItem(
                id = "language_settings",
                title = "إعدادات اللغة",
                subtitle = "العربية (النسخة العراقية الكاملة)",
                icon = Icons.Default.Language,
                onClick = { onPostToast("اللغة المعتمدة: العربية (نسخة دجلة بلس العراقية)") }
            ),
            IraqiSettingItem(
                id = "theme_style",
                title = "تغيير مظهر التطبيق",
                subtitle = config.appThemeStyle,
                icon = Icons.Default.Palette,
                onClick = {
                    val next = if (config.appThemeStyle.contains("الملكي")) "الثيم البرونزي والذهبي الفخم" else "الثيم الملكي الكحلي والذهبي"
                    onUpdateConfig(config.copy(appThemeStyle = next))
                }
            ),
            IraqiSettingItem(
                id = "hide_live_cats",
                title = "إخفاء باقات البث المباشر",
                subtitle = "اختيار الباقات المخفية من قائمة القنوات",
                icon = Icons.Default.VisibilityOff,
                onClick = { hideModalType = "LIVE" }
            ),
            IraqiSettingItem(
                id = "hide_vod_cats",
                title = "إخفاء أقسام الأفلام",
                subtitle = "اختيار الأقسام المخفية من مكتبة الأفلام",
                icon = Icons.Default.MovieFilter,
                onClick = { hideModalType = "VOD" }
            ),
            IraqiSettingItem(
                id = "hide_series_cats",
                title = "إخفاء أقسام المسلسلات",
                subtitle = "اختيار الأقسام المخفية من مكتبة المسلسلات",
                icon = Icons.Default.VideoSettings,
                onClick = { hideModalType = "SERIES" }
            ),
            IraqiSettingItem(
                id = "clear_channels_history",
                title = "مسح سجل القنوات",
                subtitle = "تنظيف قائمة القنوات التي شوهدت مؤخراً",
                icon = Icons.Default.HistoryToggleOff,
                onClick = onClearChannelsHistory
            ),
            IraqiSettingItem(
                id = "clear_movies_history",
                title = "مسح سجل الأفلام",
                subtitle = "مسح قائمة استئناف المشاهدة للأفلام",
                icon = Icons.Default.DeleteSweep,
                onClick = onClearMoviesHistory
            ),
            IraqiSettingItem(
                id = "clear_series_history",
                title = "مسح سجل المسلسلات",
                subtitle = "مسح سجل الحلقات والمسلسلات التي شوهدت مؤخراً",
                icon = Icons.Default.AutoDelete,
                onClick = onClearSeriesHistory
            ),
            IraqiSettingItem(
                id = "channel_sort",
                title = "ترتيب القنوات",
                subtitle = config.channelSortPref,
                icon = Icons.Default.SortByAlpha,
                onClick = {
                    val next = when (config.channelSortPref) {
                        "الترتيب الافتراضي" -> "الأبجدية أ - ي"
                        "الأبجدية أ - ي" -> "حسب رقم القناة"
                        else -> "الترتيب الافتراضي"
                    }
                    onUpdateConfig(config.copy(channelSortPref = next))
                }
            ),
            IraqiSettingItem(
                id = "live_stream_format",
                title = "صيغة البث المباشر",
                subtitle = config.streamFormatPref,
                icon = Icons.Default.HighQuality,
                onClick = {
                    val next = if (config.streamFormatPref.contains("HLS")) "MPEG-TS (.ts) مباشر" else "HLS (m3u8) تلقائي"
                    onUpdateConfig(config.copy(streamFormatPref = next))
                }
            ),
            IraqiSettingItem(
                id = "internal_player",
                title = "تغيير المشغل الداخلي",
                subtitle = config.internalPlayerEngine,
                icon = Icons.Default.SmartDisplay,
                onClick = {
                    val next = if (config.internalPlayerEngine.contains("Media3")) "مشغل دجلة بلس السريع (Hardware Decoder)" else "مشغل دجلة بلس الذكي (Media3 + Audio Boost)"
                    onUpdateConfig(config.copy(internalPlayerEngine = next))
                }
            ),
            IraqiSettingItem(
                id = "external_player",
                title = "مشغل خارجي",
                subtitle = if (config.externalPlayerEnabled) "مسموح عند الطلب" else "استخدام مشغل دجلة بلس المدمج",
                icon = Icons.Default.OpenInNew,
                onClick = {
                    onUpdateConfig(config.copy(externalPlayerEnabled = !config.externalPlayerEnabled))
                }
            ),
            IraqiSettingItem(
                id = "autoplay_toggle",
                title = "التشغيل التلقائي",
                subtitle = if (config.autoPlayEnabled) "مفعل للحلقات والقنوات" else "متوقف",
                icon = Icons.Default.PlayCircleOutline,
                onClick = {
                    onUpdateConfig(config.copy(autoPlayEnabled = !config.autoPlayEnabled))
                }
            ),
            IraqiSettingItem(
                id = "time_format",
                title = "تنسيق الوقت",
                subtitle = if (config.timeFormat24h) "نظام 24 ساعة" else "نظام 12 ساعة (ص / م)",
                icon = Icons.Default.AccessTime,
                onClick = {
                    onUpdateConfig(config.copy(timeFormat24h = !config.timeFormat24h))
                }
            ),
            IraqiSettingItem(
                id = "subtitle_settings",
                title = "إعدادات الترجمة",
                subtitle = config.subtitleSizePref,
                icon = Icons.Default.Subtitles,
                onClick = {
                    val next = when (config.subtitleSizePref) {
                        "كبير وواضح (ذهبي)" -> "متوسط (أبيض)"
                        "متوسط (أبيض)" -> "كبير جداً (شاشات التلفاز)"
                        else -> "كبير وواضح (ذهبي)"
                    }
                    onUpdateConfig(config.copy(subtitleSizePref = next))
                }
            ),
            IraqiSettingItem(
                id = "device_type",
                title = "نوع الجهاز (شاشة / موبايل)",
                subtitle = config.deviceTypeMode,
                icon = Icons.Default.Tv,
                onClick = {
                    val next = when (config.deviceTypeMode) {
                        "شاشة سمارت / موبايل (تلقائي)" -> "شاشة تلفاز Android TV"
                        "شاشة تلفاز Android TV" -> "هاتف / جهاز لوحي"
                        else -> "شاشة سمارت / موبايل (تلقائي)"
                    }
                    onUpdateConfig(config.copy(deviceTypeMode = next))
                }
            ),
            IraqiSettingItem(
                id = "refresh_now",
                title = "تحديث القوائم الآن",
                subtitle = "مزامنة جميع القنوات والأفلام والمسلسلات فوراً",
                icon = Icons.Default.Sync,
                onClick = onRefreshPlaylistsNow
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header + 5-Tap MAC Secret Trigger
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DijlaLogo(size = 34.dp)
                Text(
                    text = "إعدادات تطبيق دجلة بلس | Dijla Plus",
                    color = DijlahTextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp
                )
            }

            Surface(
                color = Color(0xFF0D1E33),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.45f)),
                modifier = Modifier
                    .clickable { onMacAddressTapped() }
                    .testTag("settings_mac_secret_trigger")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Devices,
                        contentDescription = null,
                        tint = DijlahCyan,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "عنوان ماك : ${config.macAddress}",
                        color = DijlahGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // 20 Iraqi/Arabic Settings Cards Grid (4 Columns)
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(settingItems, key = { it.id }) { item ->
                Surface(
                    onClick = item.onClick,
                    shape = RoundedCornerShape(14.dp),
                    color = Color.Transparent,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("setting_card_${item.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF0D1E33),
                                        Color(0xFF17324D),
                                        Color(0xFF7C4D25)
                                    )
                                )
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF050B14).copy(alpha = 0.65f))
                                .border(1.dp, DijlahGold.copy(alpha = 0.55f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = DijlahGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = item.subtitle,
                                color = DijlahGold,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }

    // Parental Lock Configuration Dialog
    if (showParentalDialog) {
        var pinInput by remember(config) { mutableStateOf(config.defaultParentalPin) }
        var lockEnabled by remember(config) { mutableStateOf(config.lockAdultContent) }

        Dialog(onDismissRequest = { showParentalDialog = false }) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = DijlahBgSecondary,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, DijlahGold),
                modifier = Modifier.widthIn(max = 400.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "إعدادات القفل العائلي",
                        color = DijlahGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("تفعيل القفل العائلي", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Switch(
                            checked = lockEnabled,
                            onCheckedChange = { lockEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = DijlahGold)
                        )
                    }
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { pinInput = it },
                        label = { Text("رمز القفل العائلي (PIN)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showParentalDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("إلغاء", color = DijlahTextSecondary)
                        }
                        Button(
                            onClick = {
                                onUpdateConfig(
                                    config.copy(
                                        defaultParentalPin = pinInput.ifBlank { "0000" },
                                        lockAdultContent = lockEnabled
                                    )
                                )
                                showParentalDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DijlahGold,
                                contentColor = Color(0xFF050B14)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("حفظ", fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }

    // Category Visibility Dialog (إخفاء باقات البث المباشر / الأفلام / المسلسلات)
    hideModalType?.let { type ->
        val modalTitle = when (type) {
            "LIVE" -> "إخفاء باقات البث المباشر"
            "VOD" -> "إخفاء أقسام الأفلام"
            else -> "إخفاء أقسام المسلسلات"
        }
        val allCats = when (type) {
            "LIVE" -> liveCategories
            "VOD" -> vodCategories
            else -> seriesCategories
        }
        val currentHidden = when (type) {
            "LIVE" -> config.hiddenLiveCategories
            "VOD" -> config.hiddenVodCategories
            else -> config.hiddenSeriesCategories
        }

        Dialog(onDismissRequest = { hideModalType = null }) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = DijlahBgSecondary,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, DijlahGold),
                modifier = Modifier
                    .widthIn(max = 460.dp)
                    .heightIn(max = 500.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = modalTitle,
                        color = DijlahGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                    if (allCats.isEmpty()) {
                        EmptyStateCard("لا توجد أقسام محملة حالياً")
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f, fill = false),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(allCats) { cat ->
                                val isHidden = currentHidden.contains(cat)
                                Surface(
                                    onClick = {
                                        val updated = currentHidden.toMutableSet()
                                        if (isHidden) updated.remove(cat) else updated.add(cat)
                                        val newCfg = when (type) {
                                            "LIVE" -> config.copy(hiddenLiveCategories = updated.toList())
                                            "VOD" -> config.copy(hiddenVodCategories = updated.toList())
                                            else -> config.copy(hiddenSeriesCategories = updated.toList())
                                        }
                                        onUpdateConfig(newCfg)
                                    },
                                    color = Color(0xFF0D1E33),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(cat, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Checkbox(
                                            checked = isHidden,
                                            onCheckedChange = null,
                                            colors = CheckboxDefaults.colors(checkedColor = DijlahCrimson)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Button(
                        onClick = { hideModalType = null },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DijlahGold,
                            contentColor = Color(0xFF050B14)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("إغلاق وحفظ", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateCard(message: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = DijlahSurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                color = DijlahTextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
