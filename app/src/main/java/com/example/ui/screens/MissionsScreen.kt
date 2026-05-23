package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.MissionEntity
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MissionsScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val missions by viewModel.allMissions.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val activeMissions = missions.filter { !it.isCompleted }
    val completedMissions = missions.filter { it.isCompleted }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Mission Command HUD
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TACTICAL OPERATIONS ROOM",
                    color = CyberCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "ACTIVE OPERATIONS: ${activeMissions.size}",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Scan surprise quest!
                IconButton(
                    onClick = { viewModel.scanIntelSideQuest() },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = CyberPurple.copy(alpha = 0.2f)),
                    modifier = Modifier.border(1.dp, CyberPurple.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Radar,
                        contentDescription = "Scan Intel",
                        tint = CyberPurple
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("add_mission_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Operation",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "NEW CONTRACT",
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Subtitle command help of scan intelligence
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CyberNavy)
                .clickable { viewModel.scanIntelSideQuest() }
                .border(0.5.dp, CyberPurple.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Radar,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "ANTENNA LINK STABLE: Click here to scan satellite orbits for unexpected Side Quests.",
                color = TextSecondary,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(1f)
            )
        }

        // Horizontal Tabs for Completed or Active
        var selectedTabIndex by remember { mutableStateOf(0) }
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = CyberCyan,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = CyberCyan
                )
            },
            divider = { HorizontalDivider(color = GlassTintBorder) }
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = {
                    Text(
                        "ACTIVE (${activeMissions.size})",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = {
                    Text(
                        "COMPLETED (${completedMissions.size})",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }

        // Main operations lists
        if (selectedTabIndex == 0) {
            if (activeMissions.isEmpty()) {
                EmptyStateHUD(
                    icon = Icons.Default.SportsEsports,
                    title = "ZERO ACTIVE CONFLICTS",
                    desc = "Your radar is clean, Operative. Tap 'NEW CONTRACT' or 'SCAN INTEL' to lock in self-improvement targets and climb the RR ladder."
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(activeMissions, key = { it.id }) { m ->
                        MissionContractRow(
                            mission = m,
                            onComplete = { viewModel.completeMission(m.id) },
                            onFail = { viewModel.failMission(m.id) }
                        )
                    }
                }
            }
        } else {
            if (completedMissions.isEmpty()) {
                EmptyStateHUD(
                    icon = Icons.Default.History,
                    title = "ARCHIVES EMPTY",
                    desc = "Zero historical telemetry on completed operations. Resolve custom contracts to populate logs and earn reputation score."
                )
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    Button(
                        onClick = { viewModel.clearCompletedMissions() },
                        colors = ButtonDefaults.buttonColors(containerColor = WarningRed.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.End)
                            .border(1.dp, WarningRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = WarningRed, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PURGE HISTORIC ARCHIVES", color = WarningRed, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(completedMissions, key = { it.id }) { m ->
                            CompletedContractRow(mission = m, onDelete = { viewModel.deleteMission(m) })
                        }
                    }
                }
            }
        }
    }

    // Modal Contract Initiation Dialog
    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            var inputTitle by remember { mutableStateOf("") }
            var selectedCategory by remember { mutableStateOf("Deep Focus") }
            var selectedDifficulty by remember { mutableStateOf("Medium") }
            var inputDuration by remember { mutableStateOf(30) }
            var contractType by remember { mutableStateOf("Standard") } // Standard, Side, Special

            val categories = listOf("Deep Focus", "Study", "Coding", "Fitness", "Work", "Creativity", "Business", "Discipline", "Mental Health")
            val difficulties = listOf("Easy", "Medium", "Hard", "Legendary")

            Card(
                colors = CardDefaults.cardColors(containerColor = CyberNavy),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberCyan, RoundedCornerShape(16.dp))
                    .padding(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "COMPILE TACTICAL CONTRACT",
                        color = CyberCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    // Objective Title input field
                    OutlinedTextField(
                        value = inputTitle,
                        onValueChange = { inputTitle = it },
                        label = { Text("OPERATION IDENTIFIER (Title)", fontFamily = FontFamily.Monospace, fontSize = 9.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = GlassTintBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("operation_title_input")
                    )

                    // Contract Classification (Type Picker)
                    Column {
                        Text("CONTRACT TYPE (Classification)", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            val types = listOf("Standard" to "DAILY MISSION", "Side" to "SIDE QUEST", "Special" to "SPECIAL QUEST")
                            for (item in types) {
                                val typeVal = item.first
                                val typeLabel = item.second
                                val selected = contractType == typeVal
                                val accentColor = when (typeVal) {
                                    "Side" -> CyberCyan
                                    "Special" -> CyberPink
                                    else -> CyberPurple
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) accentColor.copy(alpha = 0.15f) else Color(0xFF0F152B))
                                        .border(1.dp, if (selected) accentColor else GlassTintBorder, RoundedCornerShape(8.dp))
                                        .clickable { contractType = typeVal }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = typeLabel,
                                        color = if (selected) accentColor else TextSecondary,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }

                    // Tactical rules info text
                    val typeDesc = when (contractType) {
                        "Side" -> "• SIDE QUESTS are optional • Give +20 to +50 RR • 0 Failure Penalty • Bestow bonus streak/momentum multipliers on success."
                        "Special" -> "• SPECIAL QUESTS represent high difficulty milestones • Give +100 to +500 RR • 0 Failure Penalty • Accelerate rank standing massively."
                        else -> "• STANDARD DAILY TASKS represent routine self-improvement checklist • Yield normal RR gains • Misses incur automatic penalty (-8 to -36 RR based on tier)."
                    }
                    Text(
                        text = typeDesc,
                        color = when (contractType) {
                            "Side" -> CyberCyan.copy(alpha = 0.8f)
                            "Special" -> CyberPink.copy(alpha = 0.8f)
                            else -> TextSecondary.copy(alpha = 0.8f)
                        },
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 11.sp
                    )

                    // Objective Category Picker
                    Column {
                        Text("MISSION COGNITIVE VECTOR (Category)", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        HorizontalScrollRow {
                            categories.forEach { cat ->
                                val selected = selectedCategory == cat
                                FilterChip(
                                    selected = selected,
                                    onClick = { selectedCategory = cat },
                                    label = { Text(cat, fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyberPurple,
                                        selectedLabelColor = Color.White,
                                        containerColor = Color(0xFF0C1021),
                                        labelColor = TextSecondary
                                    ),
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                        }
                    }

                    // Difficulty Level Selection
                    Column {
                        Text("COMBAT TIER (Difficulty)", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            difficulties.forEach { diff ->
                                val selected = selectedDifficulty == diff
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) getDifficultyColor(diff).copy(alpha = 0.15f) else Color(0xFF0F152B))
                                        .border(1.dp, if (selected) getDifficultyColor(diff) else GlassTintBorder, RoundedCornerShape(8.dp))
                                        .clickable { selectedDifficulty = diff }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = diff,
                                        color = if (selected) getDifficultyColor(diff) else TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }

                    // Estimated Duration slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("CONTRACT CYCLE (Duration)", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            Text("$inputDuration MINS", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                        Slider(
                            value = inputDuration.toFloat(),
                            onValueChange = { inputDuration = it.toInt() },
                            valueRange = 5f..180f,
                            steps = 35,
                            colors = SliderDefaults.colors(
                                thumbColor = CyberCyan,
                                activeTrackColor = CyberCyan,
                                inactiveTrackColor = GlassTintBorder
                            )
                        )
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showAddDialog = false },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("ABORT", fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                if (inputTitle.isNotEmpty()) {
                                    viewModel.addNewMission(
                                        title = inputTitle,
                                        category = selectedCategory,
                                        difficulty = selectedDifficulty,
                                        durationMinutes = inputDuration,
                                        isSide = contractType == "Side",
                                        isSpecial = contractType == "Special"
                                    )
                                    showAddDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(8.dp),
                            enabled = inputTitle.isNotEmpty(),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("operation_submit_button")
                        ) {
                            Text("DEPLOY", color = Color.Black, fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HorizontalScrollRow(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 4.dp)
    ) {
        content()
    }
}

@Composable
fun MissionContractRow(
    mission: MissionEntity,
    onComplete: () -> Unit,
    onFail: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (mission.isSideQuest) Color(0xFF1B0C25) else CyberNavy)
            .border(
                1.dp,
                if (mission.isSideQuest) CyberPurple.copy(alpha = 0.5f) else GlassTintBorder,
                RoundedCornerShape(12.dp)
            )
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header: Category & Rarity Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = getCategoryIcon(mission.category),
                        contentDescription = null,
                        tint = getDifficultyColor(mission.difficulty),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = mission.category.uppercase(),
                        color = TextSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    if (mission.isSideQuest) {
                        Text(
                            text = "SIDE QUEST",
                            color = CyberCyan,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyberCyan.copy(alpha = 0.15f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                // Rarity tag
                Text(
                    text = mission.rarity.uppercase(),
                    color = getRarityColor(mission.rarity),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(getRarityColor(mission.rarity).copy(alpha = 0.1f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            // Mission Title
            Text(
                text = mission.title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.testTag("mission_title_${mission.id}")
            )

            // Duration & Rewards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LabelMetric(icon = Icons.Default.Timer, value = "${mission.durationMinutes}m")
                    LabelMetric(icon = Icons.Default.Leaderboard, value = "+${mission.xpReward} XP", color = CyberPurple)
                    LabelMetric(icon = Icons.Default.TrendingUp, value = "+${mission.rrReward} RR", color = CyberCyan)
                }

                Text(
                    text = "FAIL: -${mission.failurePenalty} RR",
                    color = WarningRed.copy(alpha = 0.7f),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Quick Operations Actions Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onComplete,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .border(1.dp, CyberCyan, RoundedCornerShape(6.dp))
                        .testTag("complete_mission_button_${mission.id}")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("REPORT SUCCESS", color = CyberCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }

                OutlinedButton(
                    onClick = onFail,
                    border = BorderStroke(1.dp, WarningRed.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = WarningRed, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("FAIL CONTRACT", color = WarningRed, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
fun CompletedContractRow(mission: MissionEntity, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0F152B))
            .border(1.dp, GlassTintBorder, RoundedCornerShape(10.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(18.dp)
            )
            Column {
                Text(
                    text = mission.title,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "CATEGORY: ${mission.category} | REWARD: +${mission.xpReward} XP",
                    color = TextSecondary.copy(alpha = 0.5f),
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = WarningRed.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun LabelMetric(icon: ImageVector, value: String, color: Color = TextSecondary) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
        Text(text = value, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun EmptyStateHUD(
    icon: ImageVector,
    title: String,
    desc: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CyberNavy)
            .border(1.dp, GlassTintBorder, RoundedCornerShape(16.dp))
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberCyan.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(24.dp))
            }
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = desc,
                color = TextSecondary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 15.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

fun getDifficultyColor(diff: String): Color {
    return when (diff) {
        "Easy" -> CyberCyan
        "Medium" -> EliteGold
        "Hard" -> CyberPurple
        "Legendary" -> CyberPink
        else -> CyberCyan
    }
}

fun getRarityColor(rarity: String): Color {
    return when (rarity) {
        "Common" -> TextSecondary
        "Rare" -> Color(0xFF3399FF)
        "Epic" -> CyberPurple
        "Legendary" -> EliteGold
        "Mythic" -> CyberPink
        else -> CyberCyan
    }
}

fun getCategoryIcon(cat: String): ImageVector {
    return when (cat) {
        "Fitness" -> Icons.Default.FitnessCenter
        "Study" -> Icons.Default.MenuBook
        "Coding" -> Icons.Default.LaptopMac
        "Mental Health" -> Icons.Default.SelfImprovement
        "Deep Focus" -> Icons.Default.FilterCenterFocus
        "Creativity" -> Icons.Default.Brush
        "Business" -> Icons.Default.TrendingUp
        "Discipline" -> Icons.Default.VerifiedUser
        else -> Icons.Default.Assignment
    }
}
