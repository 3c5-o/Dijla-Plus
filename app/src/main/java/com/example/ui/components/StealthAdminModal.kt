package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class AdminTab(val titleAr: String, val icon: ImageVector) {
    XTREAM_POOL("1. مخزن السيرفرات", Icons.Default.Dns),
    VIP_ACCOUNTS("2. حسابات VIP والاشتراك العائلي", Icons.Default.VerifiedUser),
    ACTIVATION_CODES("3. أكواد التفعيل المجانية (D1)", Icons.Default.Key),
    AD_SLIDER("4. السلايدر الإعلاني", Icons.Default.ViewCarousel),
    TELEGRAM_BROADCAST("5. التلغرام والإشعارات", Icons.AutoMirrored.Filled.Send),
    SECURITY_BACKUP("6. الأمان والنسخ الاحتياطي", Icons.Default.Security)
}

private data class DurationPreset(
    val labelAr: String,
    val durationMs: Long
)

private val VIP_DURATION_PRESETS = listOf(
    DurationPreset("تجريبي 1 ساعة", 3_600_000L),
    DurationPreset("شهري (30 يوم)", 30L * 24L * 3_600_000L),
    DurationPreset("3 أشهر (90 يوم)", 90L * 24L * 3_600_000L),
    DurationPreset("6 أشهر (180 يوم)", 180L * 24L * 3_600_000L),
    DurationPreset("سنة كاملة (366 يوم)", 366L * 24L * 3_600_000L)
)

private val DEVICE_LIMIT_OPTIONS = listOf(
    1 to "1 جهاز",
    2 to "2 هاتف/شاشة",
    3 to "3 أجهزة",
    4 to "4 أجهزة",
    5 to "5 أجهزة",
    0 to "غير محدود"
)

@Composable
fun StealthAdminModal(
    config: AppConfig,
    channels: List<LiveChannel>,
    movies: List<VodMovie>,
    seriesList: List<VodSeries>,
    onUpdateConfig: (AppConfig) -> Unit,
    onAddOrUpdateServerPoolItem: (XtreamServerPoolItem) -> Unit,
    onDeleteServerPoolItem: (String) -> Unit,
    onSetPrimaryServerPoolItem: (String) -> Unit,
    onAddOrUpdateVipAccount: (VipAccountEntry) -> Unit,
    onDeleteVipAccount: (String) -> Unit,
    onResetVipAccountMac: (String) -> Unit,
    onAddOrUpdateActivationCode: (ActivationCodeEntry) -> Unit,
    onDeleteActivationCode: (String) -> Unit,
    onResetActivationCodeMac: (String) -> Unit,
    onAddOrUpdatePromoSlide: (PromoBannerSlide) -> Unit,
    onDeletePromoSlide: (String) -> Unit,
    onToggleLockedCategory: (String) -> Unit,
    onExportJson: () -> String,
    onImportJson: (String) -> Boolean,
    onSyncCloudUrl: (String) -> Unit,
    onResetFactory: () -> Unit,
    onPostToast: (String) -> Unit,
    onClose: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(AdminTab.VIP_ACCOUNTS) }

    val allDistinctCategories = remember(channels, movies, seriesList) {
        (channels.map { it.category } + movies.map { it.category } + seriesList.map { it.category })
            .filter { it.isNotBlank() }
            .distinct()
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = DijlahBgDark,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Admin Top Header
                Surface(
                    color = DijlahBgSecondary,
                    tonalElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = DijlahGold,
                                    modifier = Modifier.size(26.dp)
                                )
                                Column {
                                    Text(
                                        text = "لوحة التحكم السرية الشاملة (2008) — دجلة بلس | Dijla Plus",
                                        color = DijlahGold,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "إدارة السيرفرات، حسابات VIP والاشتراك العائلي، الأكواد المجانية (D1)، وتصفير الـ MAC",
                                        color = DijlahTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Button(
                                onClick = onClose,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DijlahCrimson,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("close_admin_modal_button")
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("إغلاق اللوحة", fontWeight = FontWeight.Bold)
                            }
                        }

                        // 6 Admin Tabs Row
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(AdminTab.entries) { tab ->
                                val selected = tab == selectedTab
                                FilterChip(
                                    selected = selected,
                                    onClick = { selectedTab = tab },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = null,
                                            tint = if (selected) Color(0xFF050B14) else DijlahGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tab.titleAr,
                                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                                            fontSize = 12.sp
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = DijlahGold,
                                        selectedLabelColor = Color(0xFF050B14),
                                        containerColor = DijlahSurfaceCard,
                                        labelColor = DijlahTextPrimary
                                    )
                                )
                            }
                        }
                    }
                }

                // Tab Content Body
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    when (selectedTab) {
                        AdminTab.XTREAM_POOL -> AdminXtreamPoolTab(
                            config = config,
                            onAddOrUpdateServer = onAddOrUpdateServerPoolItem,
                            onDeleteServer = onDeleteServerPoolItem,
                            onSetPrimaryServer = onSetPrimaryServerPoolItem
                        )
                        AdminTab.VIP_ACCOUNTS -> AdminVipAccountsTab(
                            config = config,
                            onAddOrUpdateVipAccount = onAddOrUpdateVipAccount,
                            onDeleteVipAccount = onDeleteVipAccount,
                            onResetVipAccountMac = onResetVipAccountMac
                        )
                        AdminTab.ACTIVATION_CODES -> AdminActivationCodesTab(
                            config = config,
                            onAddOrUpdateCode = onAddOrUpdateActivationCode,
                            onDeleteCode = onDeleteActivationCode,
                            onResetCodeMac = onResetActivationCodeMac
                        )
                        AdminTab.AD_SLIDER -> AdminAdSliderTab(
                            config = config,
                            onUpdateConfig = onUpdateConfig,
                            onAddOrUpdateSlide = onAddOrUpdatePromoSlide,
                            onDeleteSlide = onDeletePromoSlide
                        )
                        AdminTab.TELEGRAM_BROADCAST -> AdminTelegramAndBroadcastTab(
                            config = config,
                            onUpdateConfig = onUpdateConfig
                        )
                        AdminTab.SECURITY_BACKUP -> AdminSecurityAndBackupTab(
                            config = config,
                            allCategories = allDistinctCategories,
                            onUpdateConfig = onUpdateConfig,
                            onToggleLockedCategory = onToggleLockedCategory,
                            onExportJson = onExportJson,
                            onImportJson = onImportJson,
                            onSyncCloudUrl = onSyncCloudUrl,
                            onResetFactory = onResetFactory,
                            onPostToast = onPostToast
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// TAB 1: مخزن سيرفرات الاكستريم (Xtream Servers Pool)
// ============================================================================
@Composable
private fun AdminXtreamPoolTab(
    config: AppConfig,
    onAddOrUpdateServer: (XtreamServerPoolItem) -> Unit,
    onDeleteServer: (String) -> Unit,
    onSetPrimaryServer: (String) -> Unit
) {
    var srvLabel by remember { mutableStateOf("") }
    var srvHost by remember { mutableStateOf("http://foxbleu.org:8789") }
    var srvUser by remember { mutableStateOf("ludovic") }
    var srvPass by remember { mutableStateOf("8333") }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1.15f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "سيرفرات الاكستريم المحفوظة (مخفية تماماً عن المستخدمين)",
                    color = DijlahGold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }
            items(config.xtreamServersPool, key = { it.id }) { srv ->
                Surface(
                    color = if (srv.isPrimary) DijlahSurfaceElevated else DijlahSurfaceCard,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (srv.isPrimary) DijlahGold else Color.White.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = srv.label,
                                color = DijlahTextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                            if (srv.isPrimary) {
                                Box(
                                    modifier = Modifier
                                        .background(DijlahGold, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "السيرفر الرئيسي النشط",
                                        color = Color(0xFF050B14),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Host: ${srv.hostUrl}",
                            color = DijlahCyan,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "User: ${srv.username} • Pass: ${srv.password}",
                            color = DijlahTextSecondary,
                            fontSize = 11.sp
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { onSetPrimaryServer(srv.id) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DijlahGold,
                                    contentColor = Color(0xFF050B14)
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("تعيين كرئيسي ومزامنة", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = {
                                    srvLabel = srv.label
                                    srvHost = srv.hostUrl
                                    srvUser = srv.username
                                    srvPass = srv.password
                                },
                                border = androidx.compose.foundation.BorderStroke(1.dp, DijlahCyan)
                            ) {
                                Text("تعديل", color = DijlahCyan, fontSize = 11.sp)
                            }
                            if (config.xtreamServersPool.size > 1) {
                                IconButton(onClick = { onDeleteServer(srv.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = DijlahCrimson)
                                }
                            }
                        }
                    }
                }
            }
        }

        Surface(
            color = DijlahSurfaceCard,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.35f)),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "إضافة أو تحديث سيرفر Xtream في المخزن",
                        color = DijlahGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
                item {
                    OutlinedTextField(
                        value = srvLabel,
                        onValueChange = { srvLabel = it },
                        label = { Text("اسم السيرفر (مثال: سيرفر دجلة الاحتياطي 2)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = srvHost,
                        onValueChange = { srvHost = it },
                        label = { Text("رابط الهوست والبورت (Host:Port)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = srvUser,
                        onValueChange = { srvUser = it },
                        label = { Text("اسم المستخدم (Username)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = srvPass,
                        onValueChange = { srvPass = it },
                        label = { Text("كلمة المرور (Password)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Button(
                        onClick = {
                            if (srvHost.isNotBlank() && srvUser.isNotBlank() && srvPass.isNotBlank()) {
                                val existing = config.xtreamServersPool.firstOrNull {
                                    it.hostUrl.equals(srvHost.trim(), ignoreCase = true) &&
                                        it.username.equals(srvUser.trim(), ignoreCase = true)
                                }
                                onAddOrUpdateServer(
                                    XtreamServerPoolItem(
                                        id = existing?.id ?: "srv_${System.currentTimeMillis()}",
                                        label = srvLabel.ifBlank { "سيرفر دجلة بلس VIP" },
                                        hostUrl = srvHost.trim(),
                                        username = srvUser.trim(),
                                        password = srvPass.trim(),
                                        isActive = true,
                                        isPrimary = existing?.isPrimary ?: false
                                    )
                                )
                                srvLabel = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DijlahGold,
                            contentColor = Color(0xFF050B14)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("حفظ السيرفر في المخزن", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

// ============================================================================
// TAB 2: إدارة حسابات المشتركين VIP + تحديد المدة + الاشتراك العائلي + تصفير MAC
// ============================================================================
@Composable
private fun AdminVipAccountsTab(
    config: AppConfig,
    onAddOrUpdateVipAccount: (VipAccountEntry) -> Unit,
    onDeleteVipAccount: (String) -> Unit,
    onResetVipAccountMac: (String) -> Unit
) {
    val primarySrv = config.xtreamServersPool.firstOrNull { it.isPrimary } ?: config.xtreamServersPool.firstOrNull()
    var subName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var customHost by remember(primarySrv) { mutableStateOf(primarySrv?.hostUrl ?: "http://foxbleu.org:8789") }
    var customSrvUser by remember(primarySrv) { mutableStateOf(primarySrv?.username ?: "ludovic") }
    var customSrvPass by remember(primarySrv) { mutableStateOf(primarySrv?.password ?: "8333") }
    var selectedPresetIndex by remember { mutableIntStateOf(1) } // Default: شهري (30 يوم)
    var selectedMaxDevices by remember { mutableIntStateOf(1) }  // Default: 1 جهاز
    var manualMacAddress by remember { mutableStateOf("") }
    var macLock by remember { mutableStateOf(true) }
    var selectedServerId by remember(config.xtreamServersPool) {
        mutableStateOf(config.xtreamServersPool.firstOrNull()?.id ?: "srv_foxbleu_primary")
    }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Right Column: VIP Accounts List
        LazyColumn(
            modifier = Modifier.weight(1.18f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "حسابات المشتركين VIP (${config.vipAccounts.size})",
                    color = DijlahGold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }
            items(config.vipAccounts, key = { it.id }) { vip ->
                val isExpired = vip.expiresAtEpochMs > 0L && System.currentTimeMillis() > vip.expiresAtEpochMs
                val registeredCount = vip.registeredMacs.size + if (vip.boundMacAddress.isNotBlank() && !vip.registeredMacs.contains(vip.boundMacAddress)) 1 else 0
                val maxLabel = if (vip.maxDevices <= 0) "غير محدود" else "${vip.maxDevices} أجهزة"

                Surface(
                    color = DijlahSurfaceCard,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isExpired) DijlahCrimson else DijlahGold.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${vip.subscriberName} (${vip.username})",
                                color = DijlahTextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isExpired) DijlahCrimson else DijlahGold.copy(alpha = 0.2f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isExpired) "منتهي الصلاحية" else "${vip.durationLabel} • ينتهي: ${vip.expiryDate}",
                                    color = if (isExpired) Color.White else DijlahGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Text(
                            text = "كلمة المرور: ${vip.password} • الاشتراك العائلي: $registeredCount / $maxLabel",
                            color = DijlahTextSecondary,
                            fontSize = 11.sp
                        )

                        if (vip.manualMacAddress.isNotBlank()) {
                            Text(
                                text = "مقفول يدوياً على MAC: ${vip.manualMacAddress}",
                                color = DijlahGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        val macsSummary = (vip.registeredMacs + listOfNotNull(vip.boundMacAddress.takeIf { it.isNotBlank() }))
                            .distinct()
                            .joinToString(" ، ")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (macsSummary.isNotBlank()) {
                                    "الأجهزة المسجلة: $macsSummary"
                                } else {
                                    "الأجهزة المسجلة: بانتظار أول دخول"
                                },
                                color = DijlahCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = { onResetVipAccountMac(vip.id) },
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DijlahCyan),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(Icons.Default.PhonelinkErase, contentDescription = null, tint = DijlahCyan, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تصفير الـ MAC", color = DijlahCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                IconButton(
                                    onClick = { onDeleteVipAccount(vip.id) },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = DijlahCrimson, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Left Column: Create / Update VIP Account Form with Duration + Family Device Selector
        Surface(
            color = DijlahSurfaceCard,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.35f)),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            LazyColumn(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "إنشاء حساب مشترك VIP جديد (تحديد المدة + عدد الأجهزة)",
                        color = DijlahGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }
                item {
                    OutlinedTextField(
                        value = subName,
                        onValueChange = { subName = it },
                        label = { Text("اسم المشترك (مثال: أحمد العراقي)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("اسم المستخدم للمشترك") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("كلمة المرور للمشترك") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Duration Selector Pills (تجريبي 1 ساعة / شهري 30 يوم / 3 أشهر / 6 أشهر / سنة كاملة 366 يوم)
                item {
                    Text(
                        text = "تحديد مدة الاشتراك:",
                        color = DijlahTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(VIP_DURATION_PRESETS.size) { idx ->
                            val preset = VIP_DURATION_PRESETS[idx]
                            val selected = idx == selectedPresetIndex
                            FilterChip(
                                selected = selected,
                                onClick = { selectedPresetIndex = idx },
                                label = {
                                    Text(
                                        text = preset.labelAr,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DijlahGold,
                                    selectedLabelColor = Color(0xFF050B14),
                                    containerColor = DijlahBgSecondary,
                                    labelColor = DijlahTextPrimary
                                )
                            )
                        }
                    }
                }

                // Family Subscription Device Limit Selector (1, 2, 3, 4, 5, غير محدود)
                item {
                    Text(
                        text = "الاشتراك العائلي (عدد الأجهزة المسموح بها بنفس الوقت):",
                        color = DijlahTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(DEVICE_LIMIT_OPTIONS) { (limitVal, labelAr) ->
                            val selected = selectedMaxDevices == limitVal
                            FilterChip(
                                selected = selected,
                                onClick = { selectedMaxDevices = limitVal },
                                label = {
                                    Text(
                                        text = labelAr,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DijlahCyan,
                                    selectedLabelColor = Color(0xFF050B14),
                                    containerColor = DijlahBgSecondary,
                                    labelColor = DijlahTextPrimary
                                )
                            )
                        }
                    }
                }

                // Optional Manual MAC Lock
                item {
                    OutlinedTextField(
                        value = manualMacAddress,
                        onValueChange = { manualMacAddress = it },
                        label = { Text("ربط بعنوان MAC يدوي لجهاز محدد (اختياري)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Xtream Server Binding Fields
                item {
                    Text(
                        text = "بيانات سيرفر الاكستريم المرتبط بهذا الحساب:",
                        color = DijlahGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    OutlinedTextField(
                        value = customHost,
                        onValueChange = { customHost = it },
                        label = { Text("الهوست والبورت (Host:Port)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = customSrvUser,
                            onValueChange = { customSrvUser = it },
                            label = { Text("يوزر السيرفر") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = customSrvPass,
                            onValueChange = { customSrvPass = it },
                            label = { Text("باسورد السيرفر") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Button(
                        onClick = {
                            if (username.isNotBlank() && password.isNotBlank()) {
                                val preset = VIP_DURATION_PRESETS[selectedPresetIndex]
                                val expiresAt = System.currentTimeMillis() + preset.durationMs
                                val dateFmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
                                val formattedExpiry = dateFmt.format(Date(expiresAt))

                                onAddOrUpdateVipAccount(
                                    VipAccountEntry(
                                        id = "vip_${System.currentTimeMillis()}",
                                        username = username.trim(),
                                        password = password.trim(),
                                        subscriberName = subName.trim().ifBlank { "مشترك VIP" },
                                        expiryDate = formattedExpiry,
                                        durationLabel = preset.labelAr,
                                        expiresAtEpochMs = expiresAt,
                                        linkedServerId = selectedServerId,
                                        customServerHost = customHost.trim(),
                                        customServerUser = customSrvUser.trim(),
                                        customServerPass = customSrvPass.trim(),
                                        macLockEnabled = macLock,
                                        manualMacAddress = manualMacAddress.trim(),
                                        maxDevices = selectedMaxDevices
                                    )
                                )
                                subName = ""
                                username = ""
                                password = ""
                                manualMacAddress = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DijlahGold,
                            contentColor = Color(0xFF050B14)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("إضافة وحفظ حساب VIP", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

// ============================================================================
// TAB 3: إدارة أكواد التفعيل السريعة المجانية (D1, 1, B1 + Duration + Multi-Device + MAC Reset)
// ============================================================================
@Composable
private fun AdminActivationCodesTab(
    config: AppConfig,
    onAddOrUpdateCode: (ActivationCodeEntry) -> Unit,
    onDeleteCode: (String) -> Unit,
    onResetCodeMac: (String) -> Unit
) {
    val primarySrv = config.xtreamServersPool.firstOrNull { it.isPrimary } ?: config.xtreamServersPool.firstOrNull()
    var codeInput by remember { mutableStateOf("") }
    var codeLabel by remember { mutableStateOf("") }
    var customHost by remember(primarySrv) { mutableStateOf(primarySrv?.hostUrl ?: "http://foxbleu.org:8789") }
    var customSrvUser by remember(primarySrv) { mutableStateOf(primarySrv?.username ?: "ludovic") }
    var customSrvPass by remember(primarySrv) { mutableStateOf(primarySrv?.password ?: "8333") }
    var selectedPresetIndex by remember { mutableIntStateOf(4) } // Default: سنة كاملة (366 يوم)
    var selectedMaxDevices by remember { mutableIntStateOf(0) }  // Default: غير محدود للكود العام
    var macLock by remember { mutableStateOf(false) }
    var selectedServerId by remember(config.xtreamServersPool) {
        mutableStateOf(config.xtreamServersPool.firstOrNull()?.id ?: "srv_foxbleu_primary")
    }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Right Column: Existing Activation Codes
        LazyColumn(
            modifier = Modifier.weight(1.18f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "أكواد التفعيل الفعالة (${config.activationCodeEntries.size})",
                    color = DijlahGold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }
            items(config.activationCodeEntries, key = { it.code }) { entry ->
                val maxLabel = if (entry.maxDevices <= 0) "غير محدود" else "${entry.maxDevices} أجهزة"
                val registeredCount = entry.registeredMacs.size + if (entry.boundMacAddress.isNotBlank() && !entry.registeredMacs.contains(entry.boundMacAddress)) 1 else 0

                Surface(
                    color = DijlahSurfaceCard,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.35f)),
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
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(DijlahGold, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = entry.code,
                                        color = Color(0xFF050B14),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp
                                    )
                                }
                                Text(
                                    text = entry.label,
                                    color = DijlahTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "المدة: ${entry.durationLabel} • الانتهاء: ${entry.expiryDate}",
                                color = DijlahTextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "حد الأجهزة: $registeredCount / $maxLabel",
                                color = if (entry.macLockEnabled) DijlahCyan else DijlahEmerald,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedButton(
                                onClick = { onResetCodeMac(entry.code) },
                                border = androidx.compose.foundation.BorderStroke(1.dp, DijlahCyan),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("تصفير MAC", color = DijlahCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { onDeleteCode(entry.code) }) {
                                Icon(Icons.Default.Delete, contentDescription = "حذف الكود", tint = DijlahCrimson)
                            }
                        }
                    }
                }
            }
        }

        // Left Column: Create / Edit Activation Code
        Surface(
            color = DijlahSurfaceCard,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.35f)),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            LazyColumn(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "إضافة أو تعديل كود تفعيل سريع (مثال: D1)",
                        color = DijlahGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }
                item {
                    OutlinedTextField(
                        value = codeInput,
                        onValueChange = { codeInput = it },
                        label = { Text("الكود (مثال: D1 أو 1 أو B1 أو VIP2026)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = codeLabel,
                        onValueChange = { codeLabel = it },
                        label = { Text("وصف الكود (مثال: كود دجلة بلس الرسمي)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Duration Selector
                item {
                    Text(
                        text = "مدة صلاحية الكود:",
                        color = DijlahTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(VIP_DURATION_PRESETS.size) { idx ->
                            val preset = VIP_DURATION_PRESETS[idx]
                            val selected = idx == selectedPresetIndex
                            FilterChip(
                                selected = selected,
                                onClick = { selectedPresetIndex = idx },
                                label = {
                                    Text(
                                        text = preset.labelAr,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DijlahGold,
                                    selectedLabelColor = Color(0xFF050B14),
                                    containerColor = DijlahBgSecondary,
                                    labelColor = DijlahTextPrimary
                                )
                            )
                        }
                    }
                }

                // Device Limit Selector
                item {
                    Text(
                        text = "عدد الأجهزة المسموح لها بتفعيل الكود:",
                        color = DijlahTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(DEVICE_LIMIT_OPTIONS) { (limitVal, labelAr) ->
                            val selected = selectedMaxDevices == limitVal
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    selectedMaxDevices = limitVal
                                    macLock = limitVal > 0
                                },
                                label = {
                                    Text(
                                        text = labelAr,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DijlahCyan,
                                    selectedLabelColor = Color(0xFF050B14),
                                    containerColor = DijlahBgSecondary,
                                    labelColor = DijlahTextPrimary
                                )
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = customHost,
                        onValueChange = { customHost = it },
                        label = { Text("هوست السيرفر المرتبط بالكود") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = customSrvUser,
                            onValueChange = { customSrvUser = it },
                            label = { Text("يوزر السيرفر") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = customSrvPass,
                            onValueChange = { customSrvPass = it },
                            label = { Text("باسورد السيرفر") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Button(
                        onClick = {
                            if (codeInput.isNotBlank()) {
                                val preset = VIP_DURATION_PRESETS[selectedPresetIndex]
                                val expiresAt = System.currentTimeMillis() + preset.durationMs
                                val dateFmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
                                val formattedExpiry = dateFmt.format(Date(expiresAt))

                                onAddOrUpdateCode(
                                    ActivationCodeEntry(
                                        code = codeInput.trim().uppercase(),
                                        label = codeLabel.trim().ifBlank { "كود دجلة بلس VIP" },
                                        expiryDate = formattedExpiry,
                                        durationLabel = preset.labelAr,
                                        expiresAtEpochMs = expiresAt,
                                        linkedServerId = selectedServerId,
                                        customServerHost = customHost.trim(),
                                        customServerUser = customSrvUser.trim(),
                                        customServerPass = customSrvPass.trim(),
                                        macLockEnabled = selectedMaxDevices > 0,
                                        maxDevices = selectedMaxDevices
                                    )
                                )
                                codeInput = ""
                                codeLabel = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DijlahGold,
                            contentColor = Color(0xFF050B14)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("حفظ وتفعيل الكود", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

// ============================================================================
// TAB 4: إدارة السلايدر الإعلاني المتحرك (3-Second Auto-Rotating Ad Slider)
// ============================================================================
@Composable
private fun AdminAdSliderTab(
    config: AppConfig,
    onUpdateConfig: (AppConfig) -> Unit,
    onAddOrUpdateSlide: (PromoBannerSlide) -> Unit,
    onDeleteSlide: (String) -> Unit
) {
    var slideTitle by remember { mutableStateOf("") }
    var slideSubtitle by remember { mutableStateOf("") }
    var slideBadge by remember { mutableStateOf("حصرياً VIP") }
    var slideImageUrl by remember { mutableStateOf("") }
    var slideTarget by remember { mutableStateOf("LIVE_TV") }
    var intervalSeconds by remember(config.adSliderIntervalMs) {
        mutableStateOf((config.adSliderIntervalMs / 1000L).toString())
    }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1.15f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "شرائح السلايدر الإعلاني (${config.promoBannerSlides.size})",
                        color = DijlahGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = intervalSeconds,
                            onValueChange = { intervalSeconds = it.filter { c -> c.isDigit() } },
                            label = { Text("ثواني التبديل") },
                            singleLine = true,
                            modifier = Modifier.width(105.dp)
                        )
                        Button(
                            onClick = {
                                val sec = intervalSeconds.toLongOrNull()?.coerceIn(2L, 15L) ?: 3L
                                onUpdateConfig(config.copy(adSliderIntervalMs = sec * 1000L))
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DijlahGold,
                                contentColor = Color(0xFF050B14)
                            )
                        ) {
                            Text("حفظ", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }

            items(config.promoBannerSlides, key = { it.id }) { slide ->
                Surface(
                    color = DijlahSurfaceCard,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(DijlahGold, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = slide.badgeText,
                                        color = Color(0xFF050B14),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 9.sp
                                    )
                                }
                                Text(
                                    text = slide.title,
                                    color = DijlahTextPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = slide.subtitle,
                                color = DijlahTextSecondary,
                                fontSize = 11.sp,
                                maxLines = 2
                            )
                            Text(
                                text = "الوجهة عند الضغط: ${slide.targetAction}",
                                color = DijlahCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row {
                            IconButton(
                                onClick = {
                                    slideTitle = slide.title
                                    slideSubtitle = slide.subtitle
                                    slideBadge = slide.badgeText
                                    slideImageUrl = slide.imageUrl
                                    slideTarget = slide.targetAction
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = DijlahGold)
                            }
                            if (config.promoBannerSlides.size > 1) {
                                IconButton(onClick = { onDeleteSlide(slide.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = DijlahCrimson)
                                }
                            }
                        }
                    }
                }
            }
        }

        Surface(
            color = DijlahSurfaceCard,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.35f)),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "إضافة شريحة إعلانية جديدة للسلايدر",
                        color = DijlahGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
                item {
                    OutlinedTextField(
                        value = slideTitle,
                        onValueChange = { slideTitle = it },
                        label = { Text("عنوان البنر الإعلاني") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = slideSubtitle,
                        onValueChange = { slideSubtitle = it },
                        label = { Text("الوصف المختصر") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = slideBadge,
                            onValueChange = { slideBadge = it },
                            label = { Text("الشارة (Badge)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = slideTarget,
                            onValueChange = { slideTarget = it },
                            label = { Text("القسم (LIVE_TV / MOVIES / TELEGRAM)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = slideImageUrl,
                        onValueChange = { slideImageUrl = it },
                        label = { Text("رابط صورة الخلفية (اختياري https://...)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Button(
                        onClick = {
                            if (slideTitle.isNotBlank()) {
                                onAddOrUpdateSlide(
                                    PromoBannerSlide(
                                        id = "slide_${System.currentTimeMillis()}",
                                        title = slideTitle.trim(),
                                        subtitle = slideSubtitle.trim().ifBlank { "مشاهدة ممتعة عبر دجلة بلس VIP" },
                                        badgeText = slideBadge.trim().ifBlank { "VIP" },
                                        imageUrl = slideImageUrl.trim(),
                                        targetAction = slideTarget.trim().ifBlank { "LIVE_TV" }
                                    )
                                )
                                slideTitle = ""
                                slideSubtitle = ""
                                slideImageUrl = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DijlahGold,
                            contentColor = Color(0xFF050B14)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("إضافة الشريحة للسلايدر", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

// ============================================================================
// TAB 5: إدارة قناة التلغرام والنافذة المنبثقة والإشعارات الحية
// ============================================================================
@Composable
private fun AdminTelegramAndBroadcastTab(
    config: AppConfig,
    onUpdateConfig: (AppConfig) -> Unit
) {
    var telegramHandle by remember(config) { mutableStateOf(config.telegramHandle) }
    var telegramUrl by remember(config) { mutableStateOf(config.telegramUrl) }
    var popupEnabled by remember(config) { mutableStateOf(config.telegramPopupEnabled) }
    var popupTitle by remember(config) { mutableStateOf(config.telegramPopupTitle) }
    var popupMessage by remember(config) { mutableStateOf(config.telegramPopupMessage) }
    var liveBroadcastBanner by remember(config) { mutableStateOf(config.liveBroadcastBanner) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Surface(
                color = DijlahSurfaceCard,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "إعدادات نافذة اشتراك التلغرام عند الدخول + شريط التنبيهات الفوري",
                        color = DijlahGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "إظهار نافذة التلغرام المنبثقة عند الدخول",
                            color = DijlahTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Switch(
                            checked = popupEnabled,
                            onCheckedChange = { popupEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = DijlahGold)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = telegramHandle,
                            onValueChange = { telegramHandle = it },
                            label = { Text("معرف القناة (@Dijla_Plus)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = telegramUrl,
                            onValueChange = { telegramUrl = it },
                            label = { Text("رابط قناة التلغرام (https://t.me/Dijla_Plus)") },
                            singleLine = true,
                            modifier = Modifier.weight(1.4f)
                        )
                    }

                    OutlinedTextField(
                        value = popupTitle,
                        onValueChange = { popupTitle = it },
                        label = { Text("عنوان النافذة المنبثقة") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = popupMessage,
                        onValueChange = { popupMessage = it },
                        label = { Text("رسالة الدعوة للاشتراك في التلغرام") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = liveBroadcastBanner,
                        onValueChange = { liveBroadcastBanner = it },
                        label = { Text("شريط تنبيه الإدارة الحي أعلى التطبيق (اتركه فارغاً للإخفاء)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            onUpdateConfig(
                                config.copy(
                                    telegramHandle = telegramHandle.trim().ifBlank { "@Dijla_Plus" },
                                    telegramUrl = telegramUrl.trim().ifBlank { "https://t.me/Dijla_Plus" },
                                    telegramPopupEnabled = popupEnabled,
                                    telegramPopupTitle = popupTitle.trim().ifBlank { "قناة دجلة بلس الرسمية" },
                                    telegramPopupMessage = popupMessage.trim(),
                                    liveBroadcastBanner = liveBroadcastBanner.trim()
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DijlahGold,
                            contentColor = Color(0xFF050B14)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("حفظ إعدادات التلغرام والإشعارات الحية", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

// ============================================================================
// TAB 6: الأمان والرقابة العائلية والمزامنة السحابية (Security & Cloud Sync)
// ============================================================================
@Composable
private fun AdminSecurityAndBackupTab(
    config: AppConfig,
    allCategories: List<String>,
    onUpdateConfig: (AppConfig) -> Unit,
    onToggleLockedCategory: (String) -> Unit,
    onExportJson: () -> String,
    onImportJson: (String) -> Boolean,
    onSyncCloudUrl: (String) -> Unit,
    onResetFactory: () -> Unit,
    onPostToast: (String) -> Unit
) {
    val context = LocalContext.current
    var appName by remember(config) { mutableStateOf(config.appName) }
    var masterPin by remember(config) { mutableStateOf(config.masterAdminPin) }
    var secretSearch by remember(config) { mutableStateOf(config.secretSearchCode) }
    var parentalPin by remember(config) { mutableStateOf(config.defaultParentalPin) }
    var macAddr by remember(config) { mutableStateOf(config.macAddress) }
    var cloudUrl by remember(config) { mutableStateOf(config.cloudSyncUrl) }
    var jsonBackupText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Surface(
                    color = DijlahSurfaceCard,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "إعدادات الأمان ورموز الدخول السرية",
                            color = DijlahGold,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                        OutlinedTextField(
                            value = appName,
                            onValueChange = { appName = it },
                            label = { Text("اسم التطبيق الرسمي") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = masterPin,
                                onValueChange = { masterPin = it },
                                label = { Text("رمز المسؤول (الافتراضي 2008)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = parentalPin,
                                onValueChange = { parentalPin = it },
                                label = { Text("رمز القفل العائلي") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = secretSearch,
                                onValueChange = { secretSearch = it },
                                label = { Text("كود البحث السري (#*2008*#)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = macAddr,
                                onValueChange = { macAddr = it },
                                label = { Text("عنوان MAC") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Button(
                            onClick = {
                                onUpdateConfig(
                                    config.copy(
                                        appName = appName.ifBlank { "دجلة بلس | Dijla Plus" },
                                        masterAdminPin = masterPin.ifBlank { "2008" },
                                        secretSearchCode = secretSearch.ifBlank { "#*2008*#" },
                                        defaultParentalPin = parentalPin.ifBlank { "0000" },
                                        macAddress = macAddr.ifBlank { "24:43:dd:c9:07:47" }
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DijlahGold,
                                contentColor = Color(0xFF050B14)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("حفظ إعدادات الأمان", fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "قفل الباقات والأقسام برمز PIN (${config.lockedCategories.size} مقفلة)",
                    color = DijlahGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            items(allCategories) { cat ->
                val isLocked = config.lockedCategories.contains(cat)
                Surface(
                    onClick = { onToggleLockedCategory(cat) },
                    color = DijlahSurfaceCard,
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
                        Text(text = cat, color = DijlahTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Checkbox(
                            checked = isLocked,
                            onCheckedChange = { onToggleLockedCategory(cat) },
                            colors = CheckboxDefaults.colors(checkedColor = DijlahGold)
                        )
                    }
                }
            }
        }

        Surface(
            color = DijlahSurfaceCard,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            LazyColumn(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "المزامنة السحابية والنسخ الاحتياطي (JSON)",
                        color = DijlahGold,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
                item {
                    OutlinedTextField(
                        value = cloudUrl,
                        onValueChange = { cloudUrl = it },
                        label = { Text("رابط ملف JSON السحابي (https://...)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Button(
                        onClick = { onSyncCloudUrl(cloudUrl) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DijlahCyan,
                            contentColor = Color(0xFF050B14)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("مزامنة فورية من السحابة", fontWeight = FontWeight.ExtraBold)
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val exported = onExportJson()
                                jsonBackupText = exported
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                clipboard?.setPrimaryClip(ClipData.newPlainText("DijlaPlusBackup", exported))
                                onPostToast("تم نسخ قاعدة البيانات بالكامل إلى الحافظة")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DijlahGold,
                                contentColor = Color(0xFF050B14)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("تصدير JSON", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = {
                                if (jsonBackupText.isNotBlank()) {
                                    onImportJson(jsonBackupText)
                                }
                            },
                            border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("استيراد JSON", color = DijlahGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = jsonBackupText,
                        onValueChange = { jsonBackupText = it },
                        label = { Text("محتوى النسخة الاحتياطية JSON") },
                        maxLines = 5,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                    )
                }
                item {
                    OutlinedButton(
                        onClick = onResetFactory,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DijlahCrimson),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("إعادة ضبط المصنع ومزامنة السيرفر الرئيسي", color = DijlahCrimson, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
