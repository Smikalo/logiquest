package ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import util.AppMonitor
import util.ActiveAppInfo
import util.InputMonitor
import util.LogitechService

@Composable
fun MainWindow(onClose: () -> Unit) {
    // 1. START LOGITECH SERVICE
    LaunchedEffect(Unit) {
        LogitechService.start()
    }

    // 2. LISTEN TO LOGITECH EVENTS
    val logiAction by LogitechService.lastAction.collectAsState()

    // State for the active app
    val appInfo = produceState<ActiveAppInfo>(initialValue = ActiveAppInfo("SCANNING...", null)) {
        AppMonitor.activeAppFlow().collect { value = it }
    }

    // State for Typing (Defense Mode)
    val isTyping by produceState(initialValue = false) {
        InputMonitor.typingStateFlow().collect { value = it }
    }

    // 3. COMBINE LOGIC: Defend if Typing OR if Logitech Action is Shield
    val isDefending = isTyping || logiAction == "action.hero_shield"

    // Animations (FASTER!)
    val infiniteTransition = rememberInfiniteTransition()

    // Monster floating (600ms loop)
    val bobbing by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Player breathing (800ms loop)
    val breathing by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val defenseScale by animateFloatAsState(
        targetValue = if (isDefending) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val opponentName = appInfo.value.title.take(12).ifBlank { "DESKTOP" }

    // Colors
    val bgColor = Color(0xFFF8F8D8)
    val uiColor = Color(0xFF303030)
    val healthGreen = Color(0xFF50A040)
    val healthYellow = Color(0xFFF0C030)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(4.dp, uiColor, RoundedCornerShape(8.dp))
    ) {
        // --- BATTLE SCENE ---
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            // 1. OPPONENT INFO
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 12.dp, top = 12.dp)
                    .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                    .border(2.dp, uiColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Column {
                    Text(opponentName.uppercase(), fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Text(":L50", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("HP:", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(2.dp))
                        Box(Modifier.width(80.dp).height(6.dp).background(Color.Gray).border(1.dp, uiColor)) {
                            Box(Modifier.fillMaxWidth(0.8f).fillMaxHeight().background(healthGreen))
                        }
                    }
                }
            }

            // 2. OPPONENT SPRITE (With Funny Face)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 20.dp, top = 12.dp + bobbing.dp)
                    .size(80.dp)
            ) {
                if (appInfo.value.icon != null) {
                    // Real Icon + Face Overlay
                    Box(Modifier.fillMaxSize()) {
                        Image(
                            bitmap = appInfo.value.icon!!,
                            contentDescription = "App Icon",
                            modifier = Modifier.fillMaxSize()
                        )
                        MonsterFace(seed = opponentName)
                    }
                } else {
                    // Fallback Monster (has face built-in)
                    ProceduralAppMonster(opponentName)
                }
            }

            // 3. PLAYER SPRITE
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 30.dp, bottom = 0.dp)
                    .size(120.dp)
                    .scale(scaleX = 1f, scaleY = breathing * defenseScale)
            ) {
                SwordFighter(isDefending = isDefending)
            }

            // 4. PLAYER INFO
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 12.dp)
                    .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                    .border(2.dp, uiColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Column {
                    Text("DEVELOPER", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Text(":L99", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("HP:", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(2.dp))
                        Box(Modifier.width(80.dp).height(6.dp).background(Color.Gray).border(1.dp, uiColor)) {
                            Box(Modifier.fillMaxWidth(0.4f).fillMaxHeight().background(healthYellow))
                        }
                    }
                    Text("42/100", fontSize = 10.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.align(Alignment.End))
                }
            }
        }

        // --- MENU AREA ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(uiColor)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight()
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .border(2.dp, Color(0xFF606060), RoundedCornerShape(4.dp))
                    .padding(12.dp)
            ) {
                val statusText = when {
                    logiAction == "action.hero_shield" -> "Logitech Shield\nACTIVATED!"
                    isTyping -> "Player used\nTYPING DEFENSE!"
                    logiAction == "action.hero_attack" -> "Player used\nLOGITECH SMASH!"
                    else -> "Wild $opponentName\nappeared!"
                }

                Text(
                    text = statusText,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(4.dp))

            Column(
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxHeight()
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .border(2.dp, Color(0xFF606060), RoundedCornerShape(4.dp))
            ) {
                Row(Modifier.weight(1f).fillMaxWidth()) {
                    MenuButton("FIGHT", Modifier.weight(1f))
                    MenuButton("BAG", Modifier.weight(1f))
                }
                Row(Modifier.weight(1f).fillMaxWidth()) {
                    MenuButton("PKMN", Modifier.weight(1f))
                    MenuButton("RUN", Modifier.weight(1f), onClick = onClose)
                }
            }
        }
    }
}

@Composable
fun MonsterFace(seed: String) {
    val hash = seed.hashCode()
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Angry Eyes (White)
        drawCircle(Color.White, radius = w * 0.15f, center = Offset(w * 0.3f, h * 0.4f))
        drawCircle(Color.White, radius = w * 0.15f, center = Offset(w * 0.7f, h * 0.4f))

        // Pupils (Black)
        drawCircle(Color.Black, radius = w * 0.05f, center = Offset(w * 0.3f, h * 0.4f))
        drawCircle(Color.Black, radius = w * 0.05f, center = Offset(w * 0.7f, h * 0.4f))

        // Angry Eyebrows
        val strokeW = w * 0.05f
        drawLine(Color.Black, Offset(w * 0.15f, h * 0.25f), Offset(w * 0.4f, h * 0.35f), strokeWidth = strokeW, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        drawLine(Color.Black, Offset(w * 0.85f, h * 0.25f), Offset(w * 0.6f, h * 0.35f), strokeWidth = strokeW, cap = androidx.compose.ui.graphics.StrokeCap.Round)

        // Zigzag Mouth
        val path = Path().apply {
            moveTo(w * 0.3f, h * 0.65f)
            lineTo(w * 0.4f, h * 0.7f)
            lineTo(w * 0.5f, h * 0.65f)
            lineTo(w * 0.6f, h * 0.7f)
            lineTo(w * 0.7f, h * 0.65f)
        }
        drawPath(path, Color.Black, style = Stroke(width = strokeW))
    }
}

@Composable
fun SwordFighter(isDefending: Boolean) {
    val color = Color(0xFF202020)
    val swordAngle by animateFloatAsState(targetValue = if (isDefending) -45f else 0f)
    val armOffset by animateFloatAsState(targetValue = if (isDefending) 10f else 0f)

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        // Body
        drawRect(color = color, topLeft = Offset(w * 0.3f, h * 0.5f), size = Size(w * 0.4f, h * 0.5f))
        // Head
        drawCircle(color = color, center = Offset(w * 0.5f, h * 0.4f + (if(isDefending) 5f else 0f)), radius = w * 0.15f)
        // Arm
        val armPath = Path().apply {
            moveTo(w * 0.7f, h * 0.55f)
            lineTo(w * 0.9f - armOffset, h * 0.45f - armOffset)
        }
        drawPath(path = armPath, color = color, style = Stroke(width = 12f))
        // Sword
        rotate(degrees = swordAngle, pivot = Offset(w * 0.9f - armOffset, h * 0.45f - armOffset)) {
            val swordPath = Path().apply {
                moveTo(w * 0.9f - armOffset, h * 0.45f - armOffset)
                lineTo(w * 0.95f - armOffset, h * 0.5f - armOffset)
                moveTo(w * 0.9f - armOffset, h * 0.45f - armOffset)
                lineTo(w * 1.1f - armOffset, h * 0.1f - armOffset)
            }
            drawPath(path = swordPath, color = Color(0xFFE0E0E0), style = Stroke(width = 6f))
            if (isDefending) {
                drawCircle(Color(0x8844AAFF), center = Offset(w * 0.9f - armOffset, h * 0.45f - armOffset), radius = 30f, style = Stroke(width = 4f))
            }
        }
    }
}

@Composable
fun ProceduralAppMonster(appName: String) {
    val textMeasurer = rememberTextMeasurer()
    val initial = appName.firstOrNull()?.uppercase() ?: "?"
    val hash = appName.hashCode()
    val color1 = Color((hash * 12345).toInt() or 0xFF000000.toInt())
    val color2 = Color((hash * 67890).toInt() or 0xFF000000.toInt())

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Shadow
        drawOval(color = Color.Black.copy(alpha = 0.3f), topLeft = Offset(0f, size.height * 0.8f), size = Size(size.width, size.height * 0.2f))

        val iconSize = size.width * 0.8f
        val offset = (size.width - iconSize) / 2

        // Body
        drawRoundRect(brush = Brush.linearGradient(listOf(color1, color2)), topLeft = Offset(offset, 0f), size = Size(iconSize, iconSize), cornerRadius = CornerRadius(16f, 16f))
        drawRoundRect(color = Color.White, topLeft = Offset(offset, 0f), size = Size(iconSize, iconSize), cornerRadius = CornerRadius(16f, 16f), style = Stroke(width = 4f))

        // Letter
        val textLayoutResult = textMeasurer.measure(text = initial, style = TextStyle(fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = Color.White.copy(alpha = 0.3f), fontFamily = FontFamily.Monospace))
        drawText(textLayoutResult, topLeft = Offset(x = (size.width - textLayoutResult.size.width) / 2, y = (iconSize - textLayoutResult.size.height) / 2))
    }

    // Overlay Face
    MonsterFace(seed = appName)
}

@Composable
fun MenuButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier.fillMaxSize().border(1.dp, Color(0xFFD0D0D0)).clickable(interactionSource = interactionSource, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
    }
}