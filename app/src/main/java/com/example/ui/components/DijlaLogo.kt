package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

/**
 * Bespoke 3D-style inline Vector/Canvas Dijla Plus Logo (<DijlaLogo />):
 * A glowing rounded royal shield (#050B14 to #0D253F) bordered in metallic gold (#D4AF37),
 * featuring a golden 3D Play emblem intertwined with two glowing turquoise Tigris river waves (#00D2FF)
 * and a golden crown/plus star on top.
 */
@Composable
fun DijlaLogo(
    size: Dp = 44.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            // Outer ambient glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x55D4AF37),
                        Color(0x3300D2FF),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.5f, h * 0.5f),
                    radius = w * 0.54f
                ),
                radius = w * 0.52f,
                center = Offset(w * 0.5f, h * 0.5f)
            )

            // Shield Path
            val shieldPath = Path().apply {
                moveTo(w * 0.50f, h * 0.06f)
                lineTo(w * 0.88f, h * 0.20f)
                lineTo(w * 0.88f, h * 0.52f)
                cubicTo(
                    w * 0.88f, h * 0.74f,
                    w * 0.71f, h * 0.88f,
                    w * 0.50f, h * 0.95f
                )
                cubicTo(
                    w * 0.29f, h * 0.88f,
                    w * 0.12f, h * 0.74f,
                    w * 0.12f, h * 0.52f
                )
                lineTo(w * 0.12f, h * 0.20f)
                close()
            }

            // Shield Fill (#050B14 to #0D253F)
            drawPath(
                path = shieldPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF050B14),
                        Color(0xFF0D253F),
                        Color(0xFF17324D)
                    )
                )
            )

            // Shield Metallic Gold Border (#D4AF37 -> #F59E0B)
            drawPath(
                path = shieldPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFDE047),
                        Color(0xFFD4AF37),
                        Color(0xFFB8860B),
                        Color(0xFFD4AF37)
                    )
                ),
                style = Stroke(width = w * 0.045f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Golden Crown + Plus Star at Top of Shield
            val crownPath = Path().apply {
                moveTo(w * 0.35f, h * 0.29f)
                lineTo(w * 0.39f, h * 0.19f)
                lineTo(w * 0.50f, h * 0.25f)
                lineTo(w * 0.61f, h * 0.19f)
                lineTo(w * 0.65f, h * 0.29f)
                close()
            }
            drawPath(
                path = crownPath,
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFFDE047), Color(0xFFD4AF37))
                )
            )

            // Plus Star above Crown
            val plusCx = w * 0.50f
            val plusCy = h * 0.145f
            val arm = w * 0.045f
            drawLine(
                color = Color(0xFFFDE047),
                start = Offset(plusCx - arm, plusCy),
                end = Offset(plusCx + arm, plusCy),
                strokeWidth = w * 0.028f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color(0xFFFDE047),
                start = Offset(plusCx, plusCy - arm),
                end = Offset(plusCx, plusCy + arm),
                strokeWidth = w * 0.028f,
                cap = StrokeCap.Round
            )

            // 3D Golden Play Triangle in Center
            val playShadowPath = Path().apply {
                moveTo(w * 0.38f, h * 0.34f)
                lineTo(w * 0.70f, h * 0.51f)
                lineTo(w * 0.38f, h * 0.68f)
                close()
            }
            drawPath(
                path = playShadowPath,
                color = Color(0xFF7C4D25)
            )

            val playMainPath = Path().apply {
                moveTo(w * 0.36f, h * 0.32f)
                lineTo(w * 0.68f, h * 0.49f)
                lineTo(w * 0.36f, h * 0.66f)
                close()
            }
            drawPath(
                path = playMainPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFDE047),
                        Color(0xFFD4AF37),
                        Color(0xFFF59E0B)
                    )
                )
            )

            // Two Glowing Turquoise Tigris River Waves (#00D2FF) Intertwined
            val wave1 = Path().apply {
                moveTo(w * 0.22f, h * 0.64f)
                cubicTo(
                    w * 0.34f, h * 0.56f,
                    w * 0.42f, h * 0.72f,
                    w * 0.52f, h * 0.63f
                )
                cubicTo(
                    w * 0.62f, h * 0.54f,
                    w * 0.70f, h * 0.70f,
                    w * 0.78f, h * 0.62f
                )
            }
            drawPath(
                path = wave1,
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF00D2FF), Color(0xFF38BDF8), Color(0xFF00D2FF))
                ),
                style = Stroke(width = w * 0.055f, cap = StrokeCap.Round)
            )

            val wave2 = Path().apply {
                moveTo(w * 0.27f, h * 0.74f)
                cubicTo(
                    w * 0.37f, h * 0.68f,
                    w * 0.45f, h * 0.80f,
                    w * 0.54f, h * 0.73f
                )
                cubicTo(
                    w * 0.63f, h * 0.66f,
                    w * 0.68f, h * 0.78f,
                    w * 0.74f, h * 0.72f
                )
            }
            drawPath(
                path = wave2,
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF38BDF8), Color(0xFF00D2FF))
                ),
                style = Stroke(width = w * 0.042f, cap = StrokeCap.Round)
            )
        }
    }
}

/**
 * Fallback Poster Card Content displaying the bespoke Dijla Plus Logo when a movie/series/channel
 * has no poster image or fails to load.
 */
@Composable
fun DijlaFallbackPoster(
    modifier: Modifier = Modifier,
    logoSize: Dp = 56.dp,
    showBrandLabel: Boolean = true
) {
    Box(
        modifier = modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0D1E33),
                        Color(0xFF17324D),
                        Color(0xFF7C4D25)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            DijlaLogo(size = logoSize)
            if (showBrandLabel) {
                Text(
                    text = "دجلة بلس",
                    color = DijlahGold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

/**
 * 1.5s Royal Splash Screen displaying DijlaLogo, title, and loading bar.
 */
@Composable
fun DijlaSplashScreen(
    appName: String = "دجلة بلس | Dijla Plus"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "splashGlow")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "splashProgress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0D253F),
                        Color(0xFF050A12)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            DijlaLogo(size = 112.dp)

            Text(
                text = appName.ifBlank { "دجلة بلس | Dijla Plus" },
                color = DijlahTextPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 26.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "البث المباشر والأفلام والمسلسلات بأعلى جودة",
                color = DijlahGold,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(240.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.12f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(DijlahCyan, DijlahGold, DijlahAmber)
                            )
                        )
                )
            }
        }
    }
}

/**
 * Login Screen matching Screenshots 6 & 8:
 * - Centered vertical rounded bronze-slate gradient card over a dark smoky background
 *   with the large دجلة بلس icon at the top edge.
 * - Header: "أهلاً بيكم في دجلة بلس" and "لاتنسوا متابعتنا على التلجرام @Dijla_Plus".
 * - Two Login Modes:
 *   1) Default Mode (VIP Username & Password): Two clean white input fields with dark icons on the right.
 *   2) Activation Code Mode ("الدخول بكود تفعيل"): Single white input field ("أدخل كود التفعيل (مثال: D1)").
 * - Dark "تسجيل الدخول" button + bottom device MAC Address.
 */
@Composable
fun DijlaLoginScreen(
    macAddress: String,
    telegramHandle: String = "@Dijla_Plus",
    isSyncing: Boolean = false,
    errorMessage: String? = null,
    onSubmitVipCredentials: (username: String, password: String) -> Unit,
    onSubmitActivationCode: (code: String) -> Unit,
    onLogoTapped: () -> Unit,
    onMacAddressTapped: () -> Unit,
    onSecretCodeEntered: () -> Unit
) {
    var isActivationCodeMode by remember { mutableStateOf(false) }
    var usernameInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var codeInput by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF10253E),
                        Color(0xFF07111E),
                        Color(0xFF050A12)
                    )
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = Color.Transparent,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, DijlahGold.copy(alpha = 0.65f)),
            modifier = Modifier
                .widthIn(min = 320.dp, max = 430.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0D1E33),
                                Color(0xFF17324D),
                                Color(0xFF7C4D25)
                            )
                        )
                    )
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Top Dijla Plus Logo (7-tap secret admin trigger)
                Box(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onLogoTapped() }
                        .testTag("login_logo_secret_trigger")
                ) {
                    DijlaLogo(size = 68.dp)
                }

                // Header
                Text(
                    text = "أهلاً بيكم في دجلة بلس",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "لاتنسوا متابعتنا على التلجرام ${telegramHandle.ifBlank { "@Dijla_Plus" }}",
                    color = DijlahGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(2.dp))

                if (!isActivationCodeMode) {
                    // Default Mode: Username & Password white fields with dark icons on the right
                    WhiteLoginInputField(
                        value = usernameInput,
                        onValueChange = {
                            usernameInput = it
                            if (it.trim() == "#*2008*#") {
                                usernameInput = ""
                                onSecretCodeEntered()
                            }
                        },
                        placeholder = "اسم المستخدم",
                        icon = Icons.Default.Person,
                        isPassword = false,
                        testTag = "login_username_input"
                    )

                    WhiteLoginInputField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        placeholder = "كلمة المرور",
                        icon = Icons.Default.Lock,
                        isPassword = true,
                        testTag = "login_password_input"
                    )
                } else {
                    // Activation Code Mode: Single white field
                    WhiteLoginInputField(
                        value = codeInput,
                        onValueChange = {
                            codeInput = it
                            if (it.trim() == "#*2008*#") {
                                codeInput = ""
                                onSecretCodeEntered()
                            }
                        },
                        placeholder = "أدخل كود التفعيل (مثال: D1)",
                        icon = Icons.Default.VpnKey,
                        isPassword = false,
                        testTag = "login_activation_code_input"
                    )
                }

                // Mode Switch Toggle ("الدخول بكود تفعيل")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { isActivationCodeMode = !isActivationCodeMode }
                        .padding(vertical = 4.dp)
                        .testTag("toggle_activation_code_mode"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Checkbox(
                        checked = isActivationCodeMode,
                        onCheckedChange = { isActivationCodeMode = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = DijlahGold,
                            uncheckedColor = Color.White.copy(alpha = 0.8f),
                            checkmarkColor = Color(0xFF050B14)
                        ),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "الدخول بكود تفعيل",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }

                // Error Message if any
                AnimatedVisibility(visible = !errorMessage.isNullOrBlank()) {
                    Surface(
                        color = DijlahCrimson.copy(alpha = 0.22f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DijlahCrimson),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage.orEmpty(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                // Dark Login Button ("تسجيل الدخول")
                Button(
                    onClick = {
                        if (isActivationCodeMode) {
                            onSubmitActivationCode(codeInput)
                        } else {
                            onSubmitVipCredentials(usernameInput, passwordInput)
                        }
                    },
                    enabled = !isSyncing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF050B14),
                        contentColor = DijlahGold
                    ),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("login_submit_button")
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            color = DijlahGold,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("جاري التحقق والدخول...", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Login,
                            contentDescription = null,
                            tint = DijlahGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تسجيل الدخول",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                    }
                }

                // Persistent Device MAC Address at bottom (5-tap secret trigger)
                Row(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onMacAddressTapped() }
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Devices,
                        contentDescription = null,
                        tint = DijlahCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "عنوان الماك : $macAddress",
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun WhiteLoginInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean,
    testTag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, DijlahGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0D1E33)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = placeholder,
                    tint = DijlahGold,
                    modifier = Modifier.size(16.dp)
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color(0xFF64748B),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color(0xFF050B14),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
                    keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
                    cursorBrush = SolidColor(Color(0xFF050B14)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(testTag)
                )
            }
        }
    }
}

/**
 * Enter Playlist Name Modal (Screenshot 10):
 * - Header: "تسمية قائمة التشغيل"
 * - Subtitle: "إذا لم تختر اسماً للقائمة، سيتم تسميتها تلقائياً باسم دجلة بلس"
 * - White input field with placeholder "مثال: اشتراك دجلة بلس VIP" and user SVG icon on the right.
 * - Two bottom buttons side-by-side: "حفظ" and "خروج".
 */
@Composable
fun EnterPlaylistNameModal(
    defaultName: String = "",
    onSave: (String) -> Unit,
    onExit: () -> Unit
) {
    var playlistNameInput by remember(defaultName) { mutableStateOf(defaultName) }

    Dialog(onDismissRequest = onExit) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color.Transparent,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, DijlahGold.copy(alpha = 0.7f)),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0D1E33),
                                Color(0xFF17324D),
                                Color(0xFF7C4D25)
                            )
                        )
                    )
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DijlaLogo(size = 54.dp)

                Text(
                    text = "تسمية قائمة التشغيل",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "إذا لم تختر اسماً للقائمة، سيتم تسميتها تلقائياً باسم دجلة بلس",
                    color = DijlahGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                WhiteLoginInputField(
                    value = playlistNameInput,
                    onValueChange = { playlistNameInput = it },
                    placeholder = "مثال: اشتراك دجلة بلس VIP",
                    icon = Icons.Default.Person,
                    isPassword = false,
                    testTag = "playlist_name_modal_input"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val chosenName = playlistNameInput.trim().ifBlank { "دجلة بلس" }
                            onSave(chosenName)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF050B14),
                            contentColor = DijlahGold
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DijlahGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("playlist_name_save_button")
                    ) {
                        Text("حفظ", fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }

                    OutlinedButton(
                        onClick = onExit,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xFF050B14).copy(alpha = 0.6f),
                            contentColor = Color.White
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("playlist_name_exit_button")
                    ) {
                        Text("خروج", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
