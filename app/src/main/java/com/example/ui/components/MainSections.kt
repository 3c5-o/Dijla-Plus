package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
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

@Composable
fun CategoryFilterBar(
    categories: List<String>,
    selectedCategory: String,
    lockedCategories: List<String>,
    onSelectCategory: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            val isLocked = lockedCategories.contains(category)
            FilterChip(
                selected = isSelected,
                onClick = { onSelectCategory(category) },
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (isLocked) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "مقفل",
                                modifier = Modifier.size(13.dp),
                                tint = if (isSelected) Color(0xFF050B14) else DijlahGold
                            )
                        }
                        Text(
                            text = category,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = DijlahGold,
                    selectedLabelColor = Color(0xFF050B14),
                    containerColor = DijlahSurfaceCard,
                    labelColor = DijlahTextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = Color.White.copy(alpha = 0.1f),
                    selectedBorderColor = DijlahGold
                )
            )
        }
    }
}

// ============================================================================
// 1. HOME SCREEN — Landscape Bento Grid + 3-Second Auto-Rotating Ad Slider
// ============================================================================
@Composable
fun HomeBentoDashboardScreen(
    config: AppConfig,
    activePlaylist: SavedPlaylistAccount?,
    channelsCount: Int,
    moviesCount: Int,
    seriesCount: Int,
    favoritesCount: Int,
    isSyncing: Boolean,
    onSelectSection: (MainSection) -> Unit,
    onRefreshPlaylist: () -> Unit,
    onOpenServerStatus: () -> Unit,
    onExitApp: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Main 4-Column Landscape Bento Grid (RTL: Col 1 is Rightmost, Col 4 is Leftmost)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Column 1 (Rightmost in RTL): Tall Live TV Card (البث المباشر)
            BentoTallLiveTvCard(
                channelsCount = channelsCount,
                isSyncing = isSyncing,
                onClick = { onSelectSection(MainSection.LIVE_TV) },
                modifier = Modifier
                    .weight(1.12f)
                    .fillMaxHeight()
                    .testTag("bento_live_tv_card")
            )

            // Column 2 (Center-Right): Series (Top) + Matches Schedule / Favorites (Bottom)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BentoStandardFeatureCard(
                    title = "المسلسلات",
                    subtitle = if (seriesCount > 0) "$seriesCount مسلسل متاح" else "مكتبة المسلسلات",
                    badgeText = "SERIES",
                    icon = Icons.Default.VideoLibrary,
                    gradientColors = listOf(DijlahSurfaceCard, DijlahSurfaceElevated, DijlahWarmBronze),
                    accentColor = DijlahGold,
                    onClick = { onSelectSection(MainSection.SERIES) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("bento_series_card")
                )

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BentoCompactSubCard(
                        title = "المباريات",
                        subtitle = "بث حي",
                        icon = Icons.Default.SportsSoccer,
                        accentColor = DijlahCyan,
                        onClick = { onSelectSection(MainSection.MATCHES) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag("bento_matches_card")
                    )

                    BentoCompactSubCard(
                        title = "المفضلة",
                        subtitle = "$favoritesCount عنصر",
                        icon = Icons.Default.Favorite,
                        accentColor = DijlahCrimson,
                        onClick = { onSelectSection(MainSection.FAVORITES) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag("bento_favorites_card")
                    )
                }
            }

            // Column 3 (Center-Left): Movies (Top) + Change Playlist (Bottom)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BentoStandardFeatureCard(
                    title = "الأفلام",
                    subtitle = if (moviesCount > 0) "$moviesCount فيلم متاح" else "أحدث الأفلام العالمية",
                    badgeText = "VOD 4K",
                    icon = Icons.Default.Movie,
                    gradientColors = listOf(DijlahSurfaceCard, DijlahSurfaceElevated, DijlahWarmBronze),
                    accentColor = DijlahAmber,
                    onClick = { onSelectSection(MainSection.MOVIES) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("bento_movies_card")
                )

                BentoStandardFeatureCard(
                    title = "تغيير قوائم التشغيل",
                    subtitle = "أكواد دجلة بلس | Xtream | M3U",
                    badgeText = "VIP",
                    icon = Icons.AutoMirrored.Filled.PlaylistPlay,
                    gradientColors = listOf(DijlahSurfaceCard, DijlahSurfaceElevated, Color(0xFF0F2537)),
                    accentColor = DijlahCyan,
                    onClick = { onSelectSection(MainSection.PLAYLIST_MANAGER) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("bento_playlists_card")
                )
            }

            // Column 4 (Leftmost in RTL): 3-Second Auto-Rotating Ad Slider + 3 Square Action Buttons
            Column(
                modifier = Modifier
                    .weight(1.22f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 3-Second Auto-Rotating Promotional Slider
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

                // 3 Square Action Buttons (الإعدادات | تحديث | خروج)
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
                        title = if (isSyncing) "جاري..." else "تحديث",
                        icon = Icons.Default.Refresh,
                        iconTint = DijlahCyan,
                        onClick = onRefreshPlaylist,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag("bento_refresh_button")
                    )

                    BentoSquareActionTile(
                        title = "خروج",
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        iconTint = DijlahCrimson,
                        onClick = onExitApp,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag("bento_exit_button")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bottom Subscription & MAC Bar (100% Stealth: Never show raw host or username)
        Surface(
            color = DijlahSurfaceCard.copy(alpha = 0.92f),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.28f)),
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
                        text = "تاريخ الانتهاء : ${activePlaylist?.expDate ?: "غير محدود"}",
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
                        text = "سيرفر دجلة بلس VIP (مشفر)",
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
        border = androidx.compose.foundation.BorderStroke(1.2.dp, DijlahGold.copy(alpha = 0.5f)),
        modifier = modifier.testTag("bento_ad_slider_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            DijlahSurfaceCard,
                            DijlahSurfaceElevated,
                            DijlahWarmBronze
                        )
                    )
                )
        ) {
            if (activeSlide.imageUrl.startsWith("http")) {
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
                                    Color(0x99050B14),
                                    Color(0xE6050B14)
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

                    // Slide Indicator Dots
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
                            text = config.telegramHandle,
                            color = DijlahCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                    Text(
                        text = "${safeIndex + 1}/${slides.size}",
                        color = DijlahGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun BentoTallLiveTvCard(
    channelsCount: Int,
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
                    Brush.verticalGradient(
                        colors = listOf(
                            DijlahSurfaceCard,
                            DijlahSurfaceElevated,
                            DijlahWarmBronze
                        )
                    )
                )
                .padding(16.dp)
        ) {
            // Top Live Cyan Badge
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

            // Center Big Glowing Icon
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    DijlahGold.copy(alpha = 0.32f),
                                    Color(0xFF050B14).copy(alpha = 0.5f)
                                )
                            )
                        )
                        .border(2.dp, DijlahGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LiveTv,
                        contentDescription = "البث المباشر",
                        tint = DijlahGold,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Text(
                    text = "البث المباشر",
                    color = DijlahTextPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                )

                Text(
                    text = when {
                        isSyncing -> "جاري تحديث القنوات..."
                        channelsCount > 0 -> "$channelsCount قناة مباشرة FHD/4K"
                        else -> "اضغط لمشاهدة القنوات المباشرة"
                    },
                    color = DijlahGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Bottom Action Hint
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF050B14).copy(alpha = 0.55f))
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.GridView,
                    contentDescription = null,
                    tint = DijlahGold,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "مشاهدة فردية أو شاشة متعددة 2x2",
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
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.45f)),
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
                        .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
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
private fun BentoCompactSubCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = DijlahSurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            DijlahSurfaceCard,
                            DijlahSurfaceElevated.copy(alpha = 0.65f)
                        )
                    )
                )
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = DijlahTextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                maxLines = 1
            )
            Text(
                text = subtitle,
                color = accentColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
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
        color = DijlahSurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, iconTint.copy(alpha = 0.38f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            DijlahSurfaceCard,
                            DijlahSurfaceElevated.copy(alpha = 0.55f)
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
                    .background(iconTint.copy(alpha = 0.16f)),
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
// 2. MATCHES SCHEDULE SCREEN (جدول المباريات والقنوات الرياضية الحية)
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
                            text = "البث الرياضي المباشر والمباريات الحية",
                            color = DijlahTextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                    }
                    Text(
                        text = "اضغط على أي قناة أو مباراة لبدء البث المباشر فوراً",
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
                    Text("تحديث", color = DijlahGold)
                }
            }
        }

        if (filtered.isEmpty()) {
            item {
                EmptyStateCard(
                    message = if (isSyncing) "جاري تحميل القنوات الرياضية الحية..." else "لا توجد قنوات رياضية مطابقة للبحث حالياً"
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
                        listOf(DijlahSurfaceCard, DijlahSurfaceElevated.copy(alpha = 0.7f))
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
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(match.homeColorHex).copy(alpha = 0.25f))
                        .border(1.5.dp, DijlahGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = null,
                        tint = DijlahGold,
                        modifier = Modifier.size(22.dp)
                    )
                }
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
// 3. LIVE TV SCREEN (البث المباشر) + TV Number Zapping
// ============================================================================
@Composable
fun LiveTvSectionScreen(
    channels: List<LiveChannel>,
    favoriteIds: Set<String>,
    selectedCategory: String,
    lockedCategories: List<String>,
    searchQuery: String,
    isSyncing: Boolean,
    onSelectCategory: (String) -> Unit,
    onPlayChannel: (LiveChannel) -> Unit,
    onPlayChannelByNumber: (Int) -> Unit = {},
    onToggleFavorite: (String) -> Unit,
    onRefreshServer: () -> Unit
) {
    var showNumberZapDialog by remember { mutableStateOf(false) }
    var zapNumberInput by remember { mutableStateOf("") }

    val categories = remember(channels) {
        listOf("الكل") + channels.map { it.category }.distinct()
    }

    val filteredChannels = remember(channels, selectedCategory, searchQuery) {
        channels.filter { ch ->
            val matchesCat = selectedCategory == "الكل" || ch.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                ch.name.contains(searchQuery, ignoreCase = true) ||
                ch.category.contains(searchQuery, ignoreCase = true) ||
                ch.channelNumber.toString() == searchQuery.trim()
            matchesCat && matchesSearch
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.weight(1f)) {
                CategoryFilterBar(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    lockedCategories = lockedCategories,
                    onSelectCategory = onSelectCategory
                )
            }
            // Digital Channel Number Zapping Button
            OutlinedButton(
                onClick = { showNumberZapDialog = true },
                border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag("channel_number_zap_button")
            ) {
                Icon(Icons.Default.Dialpad, contentDescription = "طلب قناة بالرقم", tint = DijlahGold, modifier = Modifier.size(16.dp))
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
                        message = if (isSyncing) "جاري تحميل القنوات المباشرة من سيرفر دجلة بلس..." else "لا توجد قنوات مطابقة للبحث حالياً"
                    )
                    if (!isSyncing) {
                        Button(
                            onClick = onRefreshServer,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DijlahGold,
                                contentColor = Color(0xFF050B14)
                            )
                        ) {
                            Text("إعادة تحميل القنوات من السيرفر", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 185.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
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
                        label = { Text("أدخل رقم القناة (1 - ${channels.size})") },
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
        color = DijlahSurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.25f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("channel_card_${channel.id}")
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(DijlahSurfaceCard, DijlahSurfaceElevated.copy(alpha = 0.55f))
                    )
                )
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(channel.logoColorHex).copy(alpha = 0.22f))
                            .border(1.dp, DijlahGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
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
                                    Text(
                                        text = channel.logoText.take(4),
                                        color = DijlahGold,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp
                                    )
                                }
                            )
                        } else {
                            Text(
                                text = channel.logoText.take(4),
                                color = DijlahGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Channel Number Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF050B14))
                            .border(0.5.dp, DijlahGold.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "#${channel.channelNumber}",
                            color = DijlahGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
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
                            .background(DijlahCyan.copy(alpha = 0.16f))
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
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "مفضلة",
                            tint = if (isFavorite) DijlahCrimson else DijlahTextSecondary,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }

            Text(
                text = channel.name,
                color = DijlahTextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = channel.category,
                color = DijlahTextSecondary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ============================================================================
// 4. MOVIES VOD SCREEN (مكتبة الأفلام)
// ============================================================================
@Composable
fun MoviesVodScreen(
    movies: List<VodMovie>,
    favoriteIds: Set<String>,
    savedPlaybackPositions: Map<String, Long> = emptyMap(),
    selectedCategory: String,
    lockedCategories: List<String>,
    searchQuery: String,
    isSyncing: Boolean,
    onSelectCategory: (String) -> Unit,
    onPlayMovie: (VodMovie) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    val categories = remember(movies) {
        listOf("الكل") + movies.map { it.category }.distinct()
    }

    val filtered = remember(movies, selectedCategory, searchQuery) {
        movies.filter { movie ->
            val matchesCat = selectedCategory == "الكل" || movie.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                movie.title.contains(searchQuery, ignoreCase = true) ||
                movie.category.contains(searchQuery, ignoreCase = true)
            matchesCat && matchesSearch
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        CategoryFilterBar(
            categories = categories,
            selectedCategory = selectedCategory,
            lockedCategories = lockedCategories,
            onSelectCategory = onSelectCategory
        )

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                EmptyStateCard(
                    message = if (isSyncing) "جاري تحميل مكتبة الأفلام من السيرفر..." else "لا توجد أفلام مطابقة للبحث حالياً"
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 155.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered, key = { it.id }) { movie ->
                    VodPosterCard(
                        title = movie.title,
                        subtitle = "${movie.year} • ${movie.category}",
                        rating = movie.rating,
                        qualityBadge = movie.qualityBadge,
                        posterColorHex = movie.posterColorHex,
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
}

// ============================================================================
// 5. SERIES VOD SCREEN (مكتبة المسلسلات والحلقات)
// ============================================================================
@Composable
fun SeriesVodScreen(
    seriesList: List<VodSeries>,
    favoriteIds: Set<String>,
    selectedCategory: String,
    lockedCategories: List<String>,
    searchQuery: String,
    isSyncing: Boolean,
    onSelectCategory: (String) -> Unit,
    onOpenSeries: (VodSeries) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    val categories = remember(seriesList) {
        listOf("الكل") + seriesList.map { it.category }.distinct()
    }

    val filtered = remember(seriesList, selectedCategory, searchQuery) {
        seriesList.filter { s ->
            val matchesCat = selectedCategory == "الكل" || s.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                s.title.contains(searchQuery, ignoreCase = true) ||
                s.category.contains(searchQuery, ignoreCase = true)
            matchesCat && matchesSearch
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        CategoryFilterBar(
            categories = categories,
            selectedCategory = selectedCategory,
            lockedCategories = lockedCategories,
            onSelectCategory = onSelectCategory
        )

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                EmptyStateCard(
                    message = if (isSyncing) "جاري تحميل مكتبة المسلسلات من السيرفر..." else "لا توجد مسلسلات مطابقة للبحث حالياً"
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 155.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filtered, key = { it.id }) { series ->
                    VodPosterCard(
                        title = series.title,
                        subtitle = series.category,
                        rating = series.rating,
                        qualityBadge = series.qualityBadge,
                        posterColorHex = series.posterColorHex,
                        customPosterUri = series.customPosterUri,
                        isFavorite = favoriteIds.contains(series.id),
                        hasSavedProgress = false,
                        onClick = { onOpenSeries(series) },
                        onToggleFavorite = { onToggleFavorite(series.id) },
                        testTag = "series_card_${series.id}"
                    )
                }
            }
        }
    }
}

@Composable
fun VodPosterCard(
    title: String,
    subtitle: String,
    rating: String,
    qualityBadge: String,
    posterColorHex: Long,
    customPosterUri: String?,
    isFavorite: Boolean,
    hasSavedProgress: Boolean = false,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    testTag: String
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = DijlahSurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.25f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(posterColorHex),
                                DijlahBgDark
                            )
                        )
                    )
            ) {
                if (!customPosterUri.isNullOrBlank()) {
                    NetworkImage(
                        url = customPosterUri,
                        contentDescription = title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.22f),
                        modifier = Modifier
                            .size(52.dp)
                            .align(Alignment.Center)
                    )
                }

                // Quality Badge Top-Start
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.72f))
                        .border(0.5.dp, DijlahGold.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = qualityBadge,
                        color = DijlahGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 9.sp
                    )
                }

                // Favorite Button Top-End
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.55f))
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "مفضلة",
                        tint = if (isFavorite) DijlahCrimson else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Resume Indicator Bottom-Start if partially watched
                if (hasSavedProgress) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(DijlahCyan.copy(alpha = 0.9f))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = Color(0xFF050B14),
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = "استكمال",
                            color = Color(0xFF050B14),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 9.sp
                        )
                    }
                }

                // Rating Badge Bottom-End (Zero Emojis -> Material Star Icon)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = DijlahGold,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = rating,
                        color = DijlahGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    color = DijlahTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    color = DijlahTextSecondary,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

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
            border = androidx.compose.foundation.BorderStroke(1.5.dp, DijlahGold.copy(alpha = 0.45f)),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 560.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = series.title,
                            color = DijlahTextPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "${series.year} • ${series.category} •",
                                color = DijlahGold,
                                fontSize = 12.sp
                            )
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = DijlahGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = series.rating,
                                color = DijlahGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = DijlahTextSecondary)
                    }
                }

                Text(
                    text = series.description,
                    color = DijlahTextSecondary,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (isLoadingEpisodes) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
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
                    EmptyStateCard("لا توجد حلقات متاحة لهذا المسلسل على السيرفر حالياً")
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(series.seasons.size) { idx ->
                            val season = series.seasons[idx]
                            val selected = idx == selectedSeasonIndex
                            FilterChip(
                                selected = selected,
                                onClick = { selectedSeasonIndex = idx },
                                label = { Text(season.title) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DijlahGold,
                                    selectedLabelColor = Color(0xFF050B14)
                                )
                            )
                        }
                    }

                    if (currentSeason != null) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            items(currentSeason.episodes, key = { it.id }) { ep ->
                                val savedMs = savedPlaybackPositions[ep.id] ?: 0L
                                Surface(
                                    onClick = { onPlayEpisode(currentSeason, ep) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = DijlahSurfaceCard,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (savedMs > 5000L) DijlahCyan.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.08f)
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
                                            Text(
                                                text = "الحلقة ${ep.episodeNumber}: ${ep.title}",
                                                color = DijlahTextPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = "المدة: ${ep.duration}",
                                                    color = DijlahTextSecondary,
                                                    fontSize = 11.sp
                                                )
                                                if (savedMs > 5000L) {
                                                    Text(
                                                        text = "• استكمال المشاهدة متاح",
                                                        color = DijlahCyan,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }
                                        }
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "تشغيل الحلقة",
                                            tint = DijlahGold
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

// ============================================================================
// 6. FAVORITES SCREEN (قائمتي المفضلة)
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
                    text = "قائمتي المفضلة (${favoriteIds.size})",
                    color = DijlahTextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }
        }

        if (favChannels.isEmpty() && favMovies.isEmpty() && favSeries.isEmpty()) {
            item {
                EmptyStateCard(message = "قائمتك المفضلة فارغة حالياً. اضغط على أيقونة القلب لإضافة قنواتك وأفلامك المفضلة.")
            }
        }

        if (favChannels.isNotEmpty()) {
            item {
                Text(
                    text = "القنوات المفضلة (${favChannels.size})",
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
                    text = "الأفلام المفضلة (${favMovies.size})",
                    color = DijlahGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            items(favMovies, key = { it.id }) { movie ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    VodPosterCard(
                        title = movie.title,
                        subtitle = "${movie.year} • ${movie.category}",
                        rating = movie.rating,
                        qualityBadge = movie.qualityBadge,
                        posterColorHex = movie.posterColorHex,
                        customPosterUri = movie.customPosterUri,
                        isFavorite = true,
                        onClick = { onPlayMovie(movie) },
                        onToggleFavorite = { onToggleFavorite(movie.id) },
                        testTag = "fav_movie_${movie.id}"
                    )
                }
            }
        }

        if (favSeries.isNotEmpty()) {
            item {
                Text(
                    text = "المسلسلات المفضلة (${favSeries.size})",
                    color = DijlahGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            items(favSeries, key = { it.id }) { series ->
                VodPosterCard(
                    title = series.title,
                    subtitle = series.category,
                    rating = series.rating,
                    qualityBadge = series.qualityBadge,
                    posterColorHex = series.posterColorHex,
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
// 7. PLAYLIST MANAGER SCREEN (تغيير قوائم التشغيل: أكواد دجلة بلس / Xtream / M3U)
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
    onDeleteSavedPlaylist: (String) -> Unit
) {
    var selectedLoginTab by remember { mutableIntStateOf(0) } // 0: Code, 1: Xtream, 2: M3U
    var activationCodeInput by remember { mutableStateOf("") }

    // 100% Stealth: Never pre-fill foxbleu.org or ludovic/8333 in user-facing inputs
    var xtreamName by remember { mutableStateOf("اشتراك دجلة بلس VIP") }
    var xtreamHost by remember { mutableStateOf("") }
    var xtreamUser by remember { mutableStateOf("") }
    var xtreamPass by remember { mutableStateOf("") }

    var m3uName by remember { mutableStateOf("قائمة M3U خارجية") }
    var m3uUrl by remember { mutableStateOf("") }

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
                        text = "قوائم التشغيل المحفوظة",
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
            }
        }

        // Left Column: Add / Activate Playlist (Code / Xtream / M3U)
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
                    val tabs = listOf("كود تفعيل دجلة", "اشتراك VIP / Xtream", "رابط M3U")
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
                            Text(title, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
                        }
                    }
                }

                when (selectedLoginTab) {
                    0 -> {
                        Text(
                            text = "أدخل كود التفعيل الخاص باشتراك دجلة بلس VIP",
                            color = DijlahTextSecondary,
                            fontSize = 12.sp
                        )
                        OutlinedTextField(
                            value = activationCodeInput,
                            onValueChange = { activationCodeInput = it },
                            label = { Text("كود التفعيل السريع") },
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
                                text = if (isSyncing) "جاري التحقق والمزامنة..." else "تفعيل الكود وتشغيل القنوات",
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
                                    label = { Text("اسم القائمة") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            item {
                                OutlinedTextField(
                                    value = xtreamUser,
                                    onValueChange = { xtreamUser = it },
                                    label = { Text("اسم المستخدم (Username)") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            item {
                                OutlinedTextField(
                                    value = xtreamPass,
                                    onValueChange = { xtreamPass = it },
                                    label = { Text("كلمة المرور (Password)") },
                                    singleLine = true,
                                    visualTransformation = PasswordVisualTransformation(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            item {
                                OutlinedTextField(
                                    value = xtreamHost,
                                    onValueChange = { xtreamHost = it },
                                    label = { Text("رابط السيرفر (اتركه فارغاً لسيرفر دجلة بلس الرسمي)") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            item {
                                Button(
                                    onClick = {
                                        onLoginXtream(xtreamName, xtreamHost, xtreamUser, xtreamPass)
                                    },
                                    enabled = !isSyncing,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DijlahGold,
                                        contentColor = Color(0xFF050B14)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (isSyncing) "جاري الاتصال بالسيرفر..." else "تسجيل الدخول وتحميل القنوات",
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }

                    2 -> {
                        OutlinedTextField(
                            value = m3uName,
                            onValueChange = { m3uName = it },
                            label = { Text("اسم قائمة M3U") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = m3uUrl,
                            onValueChange = { m3uUrl = it },
                            label = { Text("رابط ملف M3U / M3U8") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = { onLoginM3u(m3uName, m3uUrl) },
                            enabled = !isSyncing,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DijlahGold,
                                contentColor = Color(0xFF050B14)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isSyncing) "جاري جلب ملف M3U..." else "إضافة وتحميل قائمة M3U",
                                fontWeight = FontWeight.ExtraBold
                            )
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
                // 100% Server Stealth: Never display raw serverHost or username
                Text(
                    text = "البوابة: سيرفر دجلة بلس VIP (مشفر بالكامل)",
                    color = DijlahGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "الصلاحية: ${account.expDate} • الحالة: ${account.status}",
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
// 8. SETTINGS SCREEN (الإعدادات الشاملة للمشغل والرقابة العائلية + 5-Tap MAC Secret)
// ============================================================================
@Composable
fun SettingsScreen(
    config: AppConfig,
    onUpdateConfig: (AppConfig) -> Unit,
    onOpenPlaylistManager: () -> Unit,
    onOpenServerStatus: () -> Unit,
    onMacAddressTapped: () -> Unit = {}
) {
    var parentalPin by remember(config) { mutableStateOf(config.defaultParentalPin) }
    var lockAdult by remember(config) { mutableStateOf(config.lockAdultContent) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = DijlahGold)
                Text(
                    text = "إعدادات دجلة بلس | Dijla Plus",
                    color = DijlahTextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }
        }

        item {
            Surface(
                color = DijlahSurfaceCard,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "تفعيل القفل العائلي التلقائي",
                                color = DijlahTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "قفل الباقات المشفرة والمحتوى العائلي برمز سري",
                                color = DijlahTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = lockAdult,
                            onCheckedChange = { lockAdult = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = DijlahGold)
                        )
                    }

                    OutlinedTextField(
                        value = parentalPin,
                        onValueChange = { parentalPin = it },
                        label = { Text("رمز القفل العائلي (Parental PIN)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                onUpdateConfig(
                                    config.copy(
                                        defaultParentalPin = parentalPin.ifBlank { "0000" },
                                        lockAdultContent = lockAdult
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DijlahGold,
                                contentColor = Color(0xFF050B14)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("حفظ الإعدادات", fontWeight = FontWeight.ExtraBold)
                        }

                        OutlinedButton(
                            onClick = onOpenPlaylistManager,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DijlahCyan),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("تغيير قوائم التشغيل", color = DijlahCyan, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onOpenServerStatus,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("فحص حالة السيرفر", color = DijlahGold, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Device MAC Info Row (5-Tap Secret Trigger to open Stealth Admin Panel)
        item {
            Surface(
                color = DijlahSurfaceCard,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMacAddressTapped() }
                    .testTag("settings_mac_secret_trigger")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Devices,
                            contentDescription = "بصمة الجهاز",
                            tint = DijlahCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "عنوان ماك للجهاز (Device MAC Address)",
                            color = DijlahTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = config.macAddress,
                        color = DijlahGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
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
