package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatLogEntity
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel
import kotlinx.coroutines.launch

@Composable
fun CoachScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val logs by viewModel.chatLogs.collectAsState()
    val isGenerating by viewModel.isCoachGenerating
    var userText by viewModel.coachInput
    var selectedTone by viewModel.coachTone

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val toneOptions = listOf("Tactical", "Harsh", "Motivational", "Calm", "Military", "Anime")

    // Automatically scroll to the bottom of the log when new items arrive
    LaunchedEffect(logs.size, isGenerating) {
        if (logs.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(logs.size - 1)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // AI Mentors custom config banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CyberNavy)
                .border(1.dp, GlassTintBorder, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(CyberCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "ANALYST: BROADCAST DOWNLINK",
                                color = CyberCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "TACTICAL COACH ACTIVE",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Model Badge
                    Text(
                        text = "GEMINI-FLASH",
                        color = CyberPurple,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberPurple.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Coach Persona voice selections
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "RECALIBRATE VOICE DYNAMIC (Tone selector)",
                        color = TextSecondary,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        toneOptions.forEach { t ->
                            val active = selectedTone == t
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (active) CyberCyan.copy(alpha = 0.15f) else Color(0xFF0F152B))
                                    .border(1.dp, if (active) CyberCyan else GlassTintBorder, RoundedCornerShape(6.dp))
                                    .clickable { viewModel.setTacticalCoachTone(t) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = t.uppercase(),
                                    color = if (active) CyberCyan else TextSecondary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // Logs Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF060913))
                .border(0.5.dp, GlassTintBorder, RoundedCornerShape(12.dp))
                .padding(8.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(logs) { log ->
                    ChatBubble(log = log)
                }

                if (isGenerating) {
                    item {
                        CoachGeneratingIndicator()
                    }
                }
            }
        }

        // Tappable input panel
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = userText,
                onValueChange = { userText = it },
                placeholder = { Text("Enter tactical query...", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = TextSecondary.copy(alpha = 0.6f)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = GlassTintBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = CyberNavy,
                    unfocusedContainerColor = CyberNavy
                ),
                shape = RoundedCornerShape(10.dp),
                maxLines = 2,
                modifier = Modifier
                    .weight(1f)
                    .testTag("coach_chat_input")
            )

            Button(
                onClick = { viewModel.sendPlayerChatMessage() },
                enabled = userText.isNotEmpty() && !isGenerating,
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, disabledContainerColor = Color(0xFF0F152B)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .size(54.dp)
                    .testTag("send_chat_button"),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (userText.isNotEmpty() && !isGenerating) Color.Black else TextSecondary.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ChatBubble(log: ChatLogEntity) {
    val isCoach = log.sender == "COACH"
    
    val bubbleColor = if (isCoach) CyberNavy else Color(0xFF161F38)
    val alignment = if (isCoach) Alignment.CenterStart else Alignment.CenterEnd
    val borderStrokeColor = if (isCoach) CyberPurple.copy(alpha = 0.3f) else CyberCyan.copy(alpha = 0.2f)

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Column(
            horizontalAlignment = if (isCoach) Alignment.Start else Alignment.End,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            // Sender meta
            Text(
                text = if (isCoach) "TACTICAL COACH AI" else "OPERATIVE (YOU)",
                color = if (isCoach) CyberPurple else CyberCyan,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(start = 6.dp, end = 6.dp, bottom = 2.dp)
            )

            // Core bubble texts
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 10.dp,
                            topEnd = 10.dp,
                            bottomStart = if (isCoach) 2.dp else 10.dp,
                            bottomEnd = if (isCoach) 10.dp else 2.dp
                        )
                    )
                    .background(bubbleColor)
                    .border(
                        1.dp,
                        borderStrokeColor,
                        RoundedCornerShape(
                            topStart = 10.dp,
                            topEnd = 10.dp,
                            bottomStart = if (isCoach) 2.dp else 10.dp,
                            bottomEnd = if (isCoach) 10.dp else 2.dp
                        )
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = log.message,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun CoachGeneratingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "coach_loader")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(modifier = Modifier.fillMaxWidth(0.8f)) {
            Text(
                text = "COACH AI RETRIEVING DOWNLINK...",
                color = CyberPurple.copy(alpha = alphaAnim),
                fontSize = 8.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(start = 6.dp, end = 6.dp, bottom = 2.dp)
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberNavy)
                    .border(1.dp, CyberPurple.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(
                    color = CyberPurple,
                    strokeWidth = 1.5.dp,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Analyzing biometric logs and performance rating...",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
