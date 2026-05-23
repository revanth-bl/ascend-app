package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FocusScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val isLive by viewModel.isFocusActive
    val elapsedSecs by viewModel.focusTimeElapsedSeconds
    val targetMins by viewModel.focusTargetMinutes
    val isCompleted by viewModel.isFocusCompleted

    val targetSeconds = targetMins * 60
    val progress = if (targetSeconds > 0) elapsedSecs.toFloat() / targetSeconds.toFloat() else 0f
    
    // Countdown state prior to entering match
    var isStartingUp by remember { mutableStateOf(false) }
    var startupCountdown by remember { mutableStateOf(3) }
    val scope = rememberCoroutineScope()

    // Sound toggle representational indicator
    var isAudioMuted by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isStartingUp) {
            // Cinematic startup sequence
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "SYNCHRONIZING RECEPTORS",
                    color = CyberPurple,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.animateContentSize()
                )
                Text(
                    text = "$startupCountdown",
                    color = CyberCyan,
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "LOCKING IN COMPILING CHANNELS.\nDO NOT ABORT SIGNAL.",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
                
                CircularProgressIndicator(
                    color = CyberCyan,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(36.dp)
                )
            }
        } else if (isLive) {
            // Live Ranked match is active HUD
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Focus dynamic descriptors
                val focusModifierText = when {
                    progress >= 0.9f -> "MVP PERFORMANCE"
                    progress >= 0.7f -> "OVERTIME FOCUS"
                    progress >= 0.4f -> "PERFECT FOCUS"
                    else -> "CLUTCH SESSION"
                }
                val modifierColor = when {
                    progress >= 0.9f -> CyberPink
                    progress >= 0.7f -> EliteGold
                    progress >= 0.4f -> CyberCyan
                    else -> CyberPurple
                }

                // Match Badge and Mute controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(WarningRed)
                        )
                        Text(
                            text = "LIVE RANKED MATCH",
                            color = WarningRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    IconButton(
                        onClick = { isAudioMuted = !isAudioMuted },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF0C1021))
                    ) {
                        Icon(
                            imageVector = if (isAudioMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = CyberCyan
                        )
                    }
                }

                // Subtitle session indicator
                Text(
                    text = focusModifierText,
                    color = modifierColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(modifierColor.copy(alpha = 0.12f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )

                // Large Animated circular countdown vector
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(240.dp)
                ) {
                    // Outer rotating cyber loop
                    val infiniteTransition = rememberInfiniteTransition(label = "timer_ring")
                    val ringAngle by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(12000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "ring_rot"
                    )

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeW = 8.dp.toPx()
                        // 1. Background faint ring
                        drawCircle(
                            color = Color(0xFF131A33),
                            radius = size.minDimension / 2.1f,
                            style = Stroke(width = strokeW)
                        )
                        // 2. Glowing animated progress arc
                        drawArc(
                            color = modifierColor,
                            startAngle = -90f,
                            sweepAngle = 360f * progress,
                            useCenter = false,
                            style = Stroke(width = strokeW, cap = StrokeCap.Round)
                        )
                    }

                    // Text counter inside
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val remainingSeconds = (targetSeconds - elapsedSecs).coerceAtLeast(0)
                        val mins = remainingSeconds / 60
                        val secs = remainingSeconds % 60
                        Text(
                            text = String.format("%02d:%02d", mins, secs),
                            color = TextPrimary,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "COMPLETING SEQUENCE",
                            color = TextSecondary,
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Distraction Blocker Status
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0E1428))
                        .border(1.dp, GlassTintBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield",
                            tint = CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "DISTRACTION FIREWALL ACTIVE",
                                color = CyberCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Notifications suppressed. Exiting the app breaks consistency rating & forfeits potential RR rewards.",
                                color = TextSecondary,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Abort actions row
                Button(
                    onClick = { viewModel.stopOrAbandonFocusSession() },
                    colors = ButtonDefaults.buttonColors(containerColor = WarningRed.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, WarningRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .testTag("abort_focus_button")
                ) {
                    Text(
                        "ABORT MATCH (-10 RR PENALTY)",
                        color = WarningRed,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else if (isCompleted) {
            // Completion Recap panel
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CyberNavy)
                    .border(2.dp, CyberCyan, RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(CyberCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = EliteGold,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Text(
                    text = "MATCH ACQUIRED: VICTORY!",
                    color = CyberCyan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                // Results metrics
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F152B))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "COMBAT EVALUATION SUMMARY",
                        color = TextSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    RecapRow(label = "Session Focus Duration", value = "$targetMins Mins")
                    RecapRow(label = "Experience Gained", value = "+${targetMins * 2} XP", color = CyberPurple)
                    RecapRow(label = "Rank Rating Multiplier", value = "+${(targetMins / 10).coerceAtLeast(1)} RR", color = CyberCyan)
                    RecapRow(label = "Credit Reserves Earned", value = "+${targetMins / 5} CC", color = EliteGold)
                    RecapRow(label = "Overall Grade", value = "S+ (TACTICAL ACE)", color = CyberPink)
                }

                Button(
                    onClick = { viewModel.isFocusCompleted.value = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "DISMISS REPORT",
                        color = Color.Black,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            // Idle Config Screen (Initiate match menu)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Header Display
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FilterCenterFocus,
                        contentDescription = "Focus",
                        tint = CyberPurple,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "RANKED FOCUS MODULE",
                        color = CyberCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Convert absolute silence into competitive MMR.",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )
                }

                // Config Card: Select target minutes
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberNavy),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GlassTintBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "SECURE TELEMETRY LOAD",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        // Duration Picker buttons
                        val options = listOf(15, 25, 45, 60, 90)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            options.forEach { mins ->
                                val selected = targetMins == mins
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) CyberPurple.copy(alpha = 0.15f) else Color(0xFF0F152B))
                                        .border(1.dp, if (selected) CyberPurple else GlassTintBorder, RoundedCornerShape(8.dp))
                                        .clickable { viewModel.focusTargetMinutes.value = mins }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${mins}M",
                                        color = if (selected) CyberCyan else TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        // Estimates
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F152B))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "PROJECTED PAYROLL:",
                                color = TextSecondary,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                RewardProjectionChip(label = "XP Gained", valText = "+${targetMins * 2}", color = CyberPurple)
                                RewardProjectionChip(label = "RR Secured", valText = "+${(targetMins / 10).coerceAtLeast(1)}", color = CyberCyan)
                                RewardProjectionChip(label = "Crates CC", valText = "+${targetMins / 5}", color = EliteGold)
                            }
                        }
                    }
                }

                // Large cinematic Action start button
                Button(
                    onClick = {
                        // Trigger startup delay countdown animation
                        isStartingUp = true
                        startupCountdown = 3
                        scope.launch {
                            delay(1000)
                            startupCountdown = 2
                            delay(1000)
                            startupCountdown = 1
                            delay(1000)
                            isStartingUp = false
                            viewModel.startFocusSession(targetMins)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("start_ranked_match_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LAUNCH RANKED MATCH",
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }

                // Distraction warning
                Text(
                    text = "WARNING: Distraction blocker firewall will lock your screen. Leaving matches early triggers RR rating decay.",
                    color = WarningRed.copy(alpha = 0.6f),
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    lineHeight = 12.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun RecapRow(label: String, value: String, color: Color = TextPrimary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        Text(text = value, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun RewardProjectionChip(label: String, valText: String, color: Color) {
    Column {
        Text(text = label, color = TextSecondary.copy(alpha = 0.6f), fontSize = 7.sp, fontFamily = FontFamily.Monospace)
        Text(text = valText, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}
