package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfileEntity
import com.example.ui.theme.*

@Composable
fun ProfileDrawer(
    profile: UserProfileEntity,
    isOpen: Boolean,
    onClose: () -> Unit,
    onOpenSettings: () -> Unit,
    onViewProgress: () -> Unit,
    onViewAchievements: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Determine Rank border color matching gameplay rank
    val rankColor = when (profile.rank) {
        "Bronze" -> Color(0xFFCD7F32)
        "Silver" -> Color(0xFFC0C0C0)
        "Gold" -> EliteGold
        "Elite" -> CyberCyan
        "Apex" -> CyberPink
        "Mythic" -> CyberPurple
        else -> TextSecondary
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300, easing = EaseOutQuad)) + fadeIn(),
        exit = slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(250, easing = EaseInQuad)) + fadeOut(),
        modifier = modifier.fillMaxHeight()
    ) {
        // Overlay container to trap click events and prevent passing down to tabs
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.85f)
                .background(CyberNavy.copy(alpha = 0.98f))
                .border(1.dp, GlassTintBorder, RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
                .clickable(enabled = true, onClick = {}) // Intercept gestures
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Closer Control Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberBlack)
                            .border(1.dp, GlassTintBorder, RoundedCornerShape(8.dp))
                            .testTag("close_profile_panel")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = WarningRed)
                    }
                    
                    Text(
                        text = "OPERATIVE PROTOCOL",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )

                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberBlack)
                            .border(1.dp, GlassTintBorder, RoundedCornerShape(8.dp))
                            .testTag("open_settings_button")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = CyberCyan)
                    }
                }

                // Main Scrollable panel contents
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    
                    // SECTION 1: TOP SECTION (Large avatar with rank borders)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(CyberBlack)
                                .border(3.dp, rankColor, CircleShape)
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            ProceduralAvatar(
                                className = profile.characterClass,
                                rank = profile.rank,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // SECTION 2: USER INFO SECTION
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = profile.username.uppercase(),
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.testTag("drawer_username_lbl")
                        )
                        Text(
                            text = "SYSTEM ID: ${profile.userId}",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        
                        // Rank Badge, Level and XP elements
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(rankColor.copy(alpha = 0.15f))
                                    .border(1.dp, rankColor, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${profile.rank.uppercase()} IV",
                                    color = rankColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            
                            Text(
                                text = "LEVEL ${profile.level}",
                                color = EliteGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // XP Progress Bar
                        val xpPercent = if (profile.xpToNextLevel > 0) profile.xp.toFloat() / profile.xpToNextLevel.toFloat() else 0f
                        Spacer(modifier = Modifier.height(6.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(0.9f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("SYSTEM XP PROGRESS", color = TextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                Text("${profile.xp}/${profile.xpToNextLevel} XP", color = TextPrimary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            }
                            LinearProgressIndicator(
                                progress = { xpPercent.coerceIn(0f, 1f) },
                                color = EliteGold,
                                trackColor = GlassTintBorder,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )
                        }
                    }

                    HorizontalDivider(color = GlassTintBorder, thickness = 1.dp)

                    // SECTION 3: STATS GRID SECTION
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "SURVEILLANCE STATISTICS",
                            color = TextSecondary,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        // Stats Grid Layout
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                StatsBox(
                                    label = "RANK RATING (RR)",
                                    value = "${profile.rankRating}/100",
                                    subLabel = "RR to Promotion",
                                    valueColor = rankColor,
                                    modifier = Modifier.weight(1f)
                                )
                                StatsBox(
                                    label = "STREAK MULTIPLIER",
                                    value = "${profile.streak} DAYS",
                                    subLabel = "Focus continuity",
                                    valueColor = EliteGold,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                StatsBox(
                                    label = "TOTAL TRAINING XP",
                                    value = "${profile.goldCoins * 15 + profile.xp} XP",
                                    subLabel = "All time progression",
                                    valueColor = CyberCyan,
                                    modifier = Modifier.weight(1f)
                                )
                                StatsBox(
                                    label = "WEEKLY RATING",
                                    value = "${profile.weeklyPerformanceScore}/100",
                                    subLabel = "Habits accuracy",
                                    valueColor = CyberPurple,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            StatsBox(
                                label = "DAILY OBJECTIVES COMPLETION RATE",
                                value = "${(profile.completionRate * 100).toInt()}%",
                                subLabel = "Operations success ratio",
                                valueColor = Color.Green,
                                fullWidth = true
                            )
                        }
                    }

                    HorizontalDivider(color = GlassTintBorder, thickness = 1.dp)

                    // SECTION 4: QUICK ACTIONS SECTION
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "COMMAND INTEGRATIONS",
                            color = TextSecondary,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        // Edit Profile Button
                        DrawerActionButton(
                            label = "EDIT OPERATIVE SETTINGS",
                            icon = Icons.Default.Edit,
                            onClick = onOpenSettings,
                            color = CyberCyan
                        )

                        // View Progress Button
                        DrawerActionButton(
                            label = "SURVEILLANCE PROGRESS CHARTS",
                            icon = Icons.Default.TrendingUp,
                            onClick = onViewProgress,
                            color = EliteGold
                        )

                        // Achievements Button
                        DrawerActionButton(
                            label = "TACTICAL ACHIEVEMENT BADGES",
                            icon = Icons.Default.EmojiEvents,
                            onClick = onViewAchievements,
                            color = CyberPurple
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun StatsBox(
    label: String,
    value: String,
    subLabel: String,
    valueColor: Color,
    modifier: Modifier = Modifier,
    fullWidth: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CyberBlack.copy(alpha = 0.5f))
            .border(1.dp, GlassTintBorder, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 7.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = value,
                color = valueColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = subLabel,
                color = TextSecondary.copy(alpha = 0.7f),
                fontSize = 7.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun DrawerActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CyberBlack.copy(alpha = 0.4f))
            .border(1.dp, GlassTintBorder, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                color = TextPrimary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(14.dp)
        )
    }
}
