package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfileEntity
import com.example.ui.components.ProceduralAvatar
import com.example.ui.components.RadarChart
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel

@Composable
fun DashboardScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val profileState by viewModel.userProfile.collectAsState()
    val missions by viewModel.allMissions.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    val profile = profileState ?: UserProfileEntity()

    // Derived radar stats from profile & completed missions
    val completedCount = missions.count { it.isCompleted }
    val fitnessCompleted = missions.count { it.isCompleted && it.category == "Fitness" }
    val focusCompleted = missions.count { it.isCompleted && it.category == "Deep Focus" }
    val intellectCompleted = missions.count { it.isCompleted && (it.category == "Study" || it.category == "Coding") }
    val otherCompleted = completedCount - fitnessCompleted - focusCompleted - intellectCompleted

    val disciplineStat = (0.3f + (completedCount * 0.08f) + (profile.streak * 0.05f)).coerceAtMost(1.0f)
    val focusStat = (0.2f + (profile.totalFocusMinutes / 300f)).coerceAtMost(1.0f)
    val intellectualStat = (0.4f + (intellectCompleted * 0.12f)).coerceAtMost(1.0f)
    val strengthStat = (0.3f + (fitnessCompleted * 0.15f)).coerceAtMost(1.0f)
    val creativityStat = (0.35f + (otherCompleted * 0.10f)).coerceAtMost(1.0f)
    val consistencyStat = profile.momentum // synced directly to calculated daily momentum!

    if (isLandscape) {
        // Landscape Split Screen Mode (LEFT and RIGHT panels)
        Row(
            modifier = modifier
                .fillMaxSize()
                .background(CyberBlack)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // LEFT PANEL: Character Model Card
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CyberNavy.copy(alpha = 0.5f))
                    .border(1.dp, GlassTintBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "TACTICAL AVATAR HUD",
                        color = CyberCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "RANK: ${profile.rank.uppercase()} DIVISION ${profile.division}",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        ProceduralAvatar(
                            className = profile.characterClass,
                            rank = profile.rank,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Text(
                        text = "COSMETIC EQUIPPED: ${profile.equippedOutfit}",
                        color = CyberPurple,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "WEAPON: ${profile.equippedWeapon} | PET: ${profile.equippedPet}",
                        color = TextSecondary,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // RIGHT PANEL: Profile + Stats Radar + Metrics scrollable
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile header HUD
                ProfileHeaderSection(profile = profile)

                // Spider radar
                RadarGraphSection(
                    disciplineStat, focusStat, intellectualStat,
                    strengthStat, creativityStat, consistencyStat
                )

                // Quick live metrics
                QuickMetricsSection(profile = profile, completedCount = completedCount)

                // Daily biological integrity & routines
                DailyRoutineSection(viewModel = viewModel, profile = profile)
            }
        }
    } else {
        // Compact Vertical Mobile Mode
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(CyberBlack)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Section
            ProfileHeaderSection(profile = profile)

            // Holographic rotating character (visual focal point)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(CyberNavy.copy(alpha = 0.8f), CyberBlack.copy(alpha = 0.8f))
                        )
                    )
                    .border(1.dp, CyberCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                ProceduralAvatar(
                    className = profile.characterClass,
                    rank = profile.rank,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Overlay text labels
                Column(modifier = Modifier.align(Alignment.BottomStart)) {
                    Text(
                        text = "EQUIPPED TITLE",
                        color = CyberCyan.copy(alpha = 0.6f),
                        fontSize = 8.sp,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "« ${profile.equippedTitle} »",
                        color = EliteGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                
                Column(modifier = Modifier.align(Alignment.TopEnd), horizontalAlignment = Alignment.End) {
                    Text(
                        text = "ARCHETYPE",
                        color = CyberPurple,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = profile.characterClass.uppercase(),
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Radar Spider chart
            RadarGraphSection(
                disciplineStat, focusStat, intellectualStat,
                strengthStat, creativityStat, consistencyStat
            )

            // Quick live metrics
            QuickMetricsSection(profile = profile, completedCount = completedCount)

            // Daily biological integrity & routines
            DailyRoutineSection(viewModel = viewModel, profile = profile)
        }
    }
}

@Composable
fun ProfileHeaderSection(profile: UserProfileEntity) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CyberNavy)
            .border(1.dp, GlassTintBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Rank Icon procedural drawing
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF131A33))
                    .border(2.dp, getRankColor(profile.rank), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = getRankIcon(profile.rank),
                        contentDescription = "Rank Icon",
                        tint = getRankColor(profile.rank),
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = profile.rank.substring(0, 3).uppercase(),
                        color = getRankColor(profile.rank),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // XP and Level Progress Bars
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = profile.username,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.testTag("username_label")
                    )
                    Text(
                        text = "LEVEL ${profile.level}",
                        color = EliteGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                // Level XP Progress bar
                val progress = if (profile.xpToNextLevel > 0) profile.xp.toFloat() / profile.xpToNextLevel else 0f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = CyberPurple,
                    trackColor = Color(0xFF1B1B30)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "XP: ${profile.xp} / ${profile.xpToNextLevel}",
                        color = TextSecondary,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${(progress * 100).toInt()}% SECURED",
                        color = CyberPurple,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Rank Rating bar (RR 0-100)
                val rrProgress = profile.rankRating.toFloat() / 100f
                LinearProgressIndicator(
                    progress = { rrProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = CyberCyan,
                    trackColor = Color(0xFF132A30)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "RANK RATING: ${profile.rankRating} / 100 RR",
                        color = CyberCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = if (profile.rankRating > 80) "PROMOTION RISK" else "SECURE",
                        color = if (profile.rankRating > 80) CyberPink else CyberCyan.copy(alpha = 0.6f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun RadarGraphSection(
    discipline: Float,
    focus: Float,
    intellect: Float,
    strength: Float,
    creativity: Float,
    consistency: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CyberNavy)
            .border(1.dp, GlassTintBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "BIOMETRIC COGNITIVE STATS",
                color = CyberCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxSize()) {
                RadarChart(
                    discipline = discipline,
                    focus = focus,
                    intelligence = intellect,
                    strength = strength,
                    creativity = creativity,
                    consistency = consistency,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun QuickMetricsSection(profile: UserProfileEntity, completedCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CyberNavy)
            .border(1.dp, GlassTintBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "ACTIVE TELEMETRY BADGES",
                color = CyberCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            GridLayout(columns = 2, spacing = 12.dp) {
                MetricItem(
                    title = "DAILY STREAK",
                    value = "${profile.streak} DAYS",
                    icon = Icons.Default.Whatshot,
                    accentColor = CyberPink,
                    desc = "Consecutive active entries"
                )
                MetricItem(
                    title = "XP LEVELING METRIC",
                    value = "${completedCount} RUNS",
                    icon = Icons.Default.CheckCircle,
                    accentColor = CyberCyan,
                    desc = "Successful contracts"
                )
                MetricItem(
                    title = "TOTAL FOCUS HOURS",
                    value = String.format("%.1f HRS", profile.totalFocusMinutes / 60f),
                    icon = Icons.Default.HourglassEmpty,
                    accentColor = EliteGold,
                    desc = "Secured focusing runs"
                )
                MetricItem(
                    title = "ACTIVE BURNOUT RISK",
                    value = String.format("%.0f%%", profile.burnout * 100),
                    icon = Icons.Default.Warning,
                    accentColor = if (profile.burnout > 0.6f) WarningRed else TextSecondary,
                    desc = "Fatigue decay penalty"
                )
                MetricItem(
                    title = "MOMENTUM RATE",
                    value = String.format("%.0f%%", profile.momentum * 100),
                    icon = Icons.Default.Speed,
                    accentColor = CyberPurple,
                    desc = "Operations speed buffer"
                )
                MetricItem(
                    title = "GOLD RESERVES",
                    value = "${profile.goldCoins} CC",
                    icon = Icons.Default.MonetizationOn,
                    accentColor = EliteGold,
                    desc = "Currency for crates & gear"
                )
            }
        }
    }
}

@Composable
fun MetricItem(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    desc: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F152B))
            .border(1.dp, GlassTintBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    color = TextSecondary,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = value,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = desc,
                    color = TextSecondary.copy(alpha = 0.6f),
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

// Simple flexible Grid helper since compose doesn't standardly expose an adaptive FlowGrid in core columns
@Composable
fun GridLayout(
    columns: Int,
    spacing: androidx.compose.ui.unit.Dp,
    content: @Composable () -> Unit
) {
    // Collect child layouts and put in custom grids
    LayoutGroup(columns = columns, spacing = spacing, content = content)
}

@Composable
fun LayoutGroup(columns: Int, spacing: androidx.compose.ui.unit.Dp, content: @Composable () -> Unit) {
    androidx.compose.ui.layout.Layout(
        content = content
    ) { measurables, constraints ->
        val itemWidth = maxOf(0, ((constraints.maxWidth - (spacing.toPx() * (columns - 1))) / columns).toInt())
        val itemConstraints = constraints.copy(
            minWidth = itemWidth.coerceAtMost(constraints.maxWidth),
            maxWidth = itemWidth.coerceAtMost(constraints.maxWidth)
        )
        
        val placeables = measurables.map { it.measure(itemConstraints) }
        val numRows = (placeables.size + columns - 1) / columns
        
        val rowHeights = IntArray(numRows) { 0 }
        placeables.forEachIndexed { index, placeable ->
            val row = index / columns
            rowHeights[row] = maxOf(rowHeights[row], placeable.height)
        }
        
        val totalHeight = maxOf(0, rowHeights.sum() + (spacing.toPx() * maxOf(0, numRows - 1)).toInt())
        
        layout(constraints.maxWidth, totalHeight) {
            var yOffset = 0
            for (row in 0 until numRows) {
                var xOffset = 0
                for (col in 0 until columns) {
                    val index = row * columns + col
                    if (index < placeables.size) {
                        placeables[index].placeRelative(xOffset, yOffset)
                        xOffset += itemWidth + spacing.toPx().toInt()
                    }
                }
                val rowHeight = rowHeights.getOrNull(row) ?: 0
                yOffset += rowHeight + spacing.toPx().toInt()
            }
        }
    }
}

@Composable
fun DailyRoutineSection(
    viewModel: GameViewModel,
    profile: UserProfileEntity
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CyberNavy)
            .border(1.dp, GlassTintBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header with title and streak
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DAILY BIOLOGICAL PROTOCOLS",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Mandatory Physical Discipline Vectors",
                        color = TextSecondary,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                
                // Physical streak representation
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(EliteGold.copy(alpha = 0.12f))
                        .border(1.dp, EliteGold.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Whatshot,
                        contentDescription = "Streak",
                        tint = EliteGold,
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = "${profile.dailyRoutineStreakCount}D STREAK",
                        color = EliteGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Divider
            HorizontalDivider(color = GlassTintBorder, thickness = 1.dp)

            // The 4 mandatory daily routine items
            val routines = listOf(
                Triple("pushups", "100 PUSH-UPS", "Chest, shoulders, triceps • Rewards: +10 RR (+20 XP) • Penalty: -15 RR"),
                Triple("situps", "100 SIT-UPS", "Core abdominal strength • Rewards: +10 RR (+20 XP) • Penalty: -15 RR"),
                Triple("squats", "100 SQUATS", "Legs and endurance • Rewards: +10 RR (+20 XP) • Penalty: -15 RR"),
                Triple("run", "10 KM RUN", "Cardiovascular stamina • Rewards: +25 RR (+50 XP) • Penalty: -30 RR")
            )

            for (routine in routines) {
                val type = routine.first
                val name = routine.second
                val desc = routine.third
                val status = when (type) {
                    "pushups" -> profile.dailyPushUps
                    "situps" -> profile.dailySitUps
                    "squats" -> profile.dailySquats
                    "run" -> profile.dailyRun
                    else -> "PENDING"
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (status == "COMPLETED") CyberCyan.copy(alpha = 0.05f) else if (status == "FAILED") WarningRed.copy(alpha = 0.05f) else Color(0xFF0C1123))
                        .border(1.dp, if (status == "COMPLETED") CyberCyan.copy(alpha = 0.3f) else if (status == "FAILED") WarningRed.copy(alpha = 0.3f) else GlassTintBorder, RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = name,
                            color = if (status == "COMPLETED") CyberCyan else if (status == "FAILED") WarningRed else TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = desc,
                            color = TextSecondary,
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (status == "PENDING") {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = { viewModel.completeDailyRoutineTask(type) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CyberCyan.copy(alpha = 0.15f))
                                    .border(1.dp, CyberCyan, RoundedCornerShape(4.dp))
                                    .testTag("routine_success_${type}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Success",
                                    tint = CyberCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            IconButton(
                                onClick = { viewModel.failDailyRoutineTask(type) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(WarningRed.copy(alpha = 0.15f))
                                    .border(1.dp, WarningRed.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                    .testTag("routine_fail_${type}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Fail/Skip",
                                    tint = WarningRed,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (status == "COMPLETED") CyberCyan.copy(alpha = 0.15f) else WarningRed.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = if (status == "COMPLETED") Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = status,
                                tint = if (status == "COMPLETED") CyberCyan else WarningRed,
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                text = status,
                                color = if (status == "COMPLETED") CyberCyan else WarningRed,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // CLOSEOUT ACTION BUTTON
            val completedCount = listOf(profile.dailyPushUps, profile.dailySitUps, profile.dailySquats, profile.dailyRun).count { it == "COMPLETED" }
            Button(
                onClick = { viewModel.closeoutDailyCycle() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (completedCount == 4) CyberCyan else if (completedCount == 0) WarningRed.copy(alpha = 0.15f) else CyberPurple
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .border(
                        1.dp,
                        if (completedCount == 4) CyberCyan else if (completedCount == 0) WarningRed.copy(alpha = 0.5f) else CyberPurple,
                        RoundedCornerShape(8.dp)
                    )
                    .testTag("closeout_dial_cycle_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (completedCount == 4) Color.Black else Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "CLOSEOUT DAILY DISCIPLINE CYCLE ($completedCount/4 READY)",
                    color = if (completedCount == 4) Color.Black else Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Text(
                text = "Closeout finalizes diurnal results. Pending routines become skipped (-15/-30 RR). 4/4 gains +40 RR bonus + Streak bonus. 0/4 skips trigger heavy decay penalty (-50 RR).",
                color = TextSecondary.copy(alpha = 0.7f),
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 11.sp
            )
        }
    }
}

// Helpers for Rank drawings
fun getRankIcon(rank: String): ImageVector {
    return when (rank) {
        "Recruit" -> Icons.Default.RadioButtonUnchecked
        "Bronze" -> Icons.Default.Shield
        "Silver" -> Icons.Default.MilitaryTech
        "Gold" -> Icons.Default.EmojiEvents
        "Elite" -> Icons.Default.Star
        "Apex" -> Icons.Default.Thunderstorm
        "Mythic" -> Icons.Default.AutoAwesome
        else -> Icons.Default.Person
    }
}

fun getRankColor(rank: String): Color {
    return when (rank) {
        "Recruit" -> TextSecondary
        "Bronze" -> Color(0xFFCD7F32)
        "Silver" -> Color(0xFFC0C0C0)
        "Gold" -> EliteGold
        "Elite" -> CyberPurple
        "Apex" -> CyberCyan
        "Mythic" -> CyberPink
        else -> CyberCyan
    }
}
