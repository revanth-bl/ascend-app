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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.BossEntity
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel

@Composable
fun BossesScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val bosses by viewModel.allBosses.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val activeBosses = bosses.filter { !it.isCompleted }
    val defeatedBosses = bosses.filter { it.isCompleted }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Command header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "COLOSSAL CRUCIBLE CONSOLE",
                    color = CyberCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "CAMPAIGN BOSSES",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = WarningRed),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("summon_boss_button")
            ) {
                Icon(imageVector = Icons.Default.FlashOn, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(4.dp))
                Text("SUMMON TARGET", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
        }

        // Informational HUD warning info
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(WarningRed.copy(alpha = 0.08f))
                .border(1.dp, WarningRed.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = WarningRed,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "INTELLIGENCE BRIEFING: Slaying Colossal Bosses requires consistency in daily operations. Completing standard tactical contracts deals chunk damage automatically. Failing operations can let the boss regenerate!",
                    color = TextSecondary,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 13.sp
                )
            }
        }

        TabRow(
            selectedTabIndex = if (activeBosses.isNotEmpty() || defeatedBosses.isEmpty()) 0 else 1,
            containerColor = Color.Transparent,
            contentColor = WarningRed,
            indicator = { tabPositions ->
                val index = if (activeBosses.isNotEmpty() || defeatedBosses.isEmpty()) 0 else 1
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                    color = WarningRed
                )
            },
            divider = { HorizontalDivider(color = GlassTintBorder) }
        ) {
            Tab(
                selected = activeBosses.isNotEmpty() || defeatedBosses.isEmpty(),
                onClick = { },
                text = {
                    Text(
                        "ACTIVE CAMPAIGNS (${activeBosses.size})",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
            Tab(
                selected = activeBosses.isEmpty() && defeatedBosses.isNotEmpty(),
                onClick = { },
                text = {
                    Text(
                        "DEFEATED (${defeatedBosses.size})",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }

        // List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (activeBosses.isNotEmpty()) {
                items(activeBosses, key = { it.id }) { boss ->
                    BossTacticalCard(boss = boss, isDefeated = false, onDelete = { viewModel.deleteBoss(boss) })
                }
            }
            if (defeatedBosses.isNotEmpty()) {
                items(defeatedBosses, key = { it.id }) { boss ->
                    BossTacticalCard(boss = boss, isDefeated = true, onDelete = { viewModel.deleteBoss(boss) })
                }
            }
        }
    }

    // Modal adding Bosas dialogue
    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            var bName by remember { mutableStateOf("") }
            var bDesc by remember { mutableStateOf("") }
            var bHP by remember { mutableStateOf(100f) }
            var bTitle by remember { mutableStateOf("Entropy Eradicator") }
            var bGear by remember { mutableStateOf("God Saber") }

            Card(
                colors = CardDefaults.cardColors(containerColor = CyberNavy),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, WarningRed, RoundedCornerShape(16.dp))
                    .padding(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "CONSTRUCT DEFIANT LETHAL TARGET",
                        color = WarningRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    OutlinedTextField(
                        value = bName,
                        onValueChange = { bName = it },
                        label = { Text("CODENAME (Boss Name, e.g. CS-310 Semester Exams)", fontFamily = FontFamily.Monospace, fontSize = 9.sp) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WarningRed, unfocusedBorderColor = GlassTintBorder, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = bDesc,
                        onValueChange = { bDesc = it },
                        label = { Text("BRIEF TARGET FOCUS SCOPE (Description)", fontFamily = FontFamily.Monospace, fontSize = 9.sp) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WarningRed, unfocusedBorderColor = GlassTintBorder, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // HP slider
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("TARGET HARDNESS MATRIX (HP Scale)", color = TextSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            Text("${bHP.toInt()} HP", color = WarningRed, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                        Slider(
                            value = bHP,
                            onValueChange = { bHP = it },
                            valueRange = 50f..300f,
                            steps = 10,
                            colors = SliderDefaults.colors(thumbColor = WarningRed, activeTrackColor = WarningRed, inactiveTrackColor = GlassTintBorder)
                        )
                    }

                    OutlinedTextField(
                        value = bTitle,
                        onValueChange = { bTitle = it },
                        label = { Text("DEATH REWARD TATE TITLE (Custom Title)", fontFamily = FontFamily.Monospace, fontSize = 9.sp) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WarningRed, unfocusedBorderColor = GlassTintBorder, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = bGear,
                        onValueChange = { bGear = it },
                        label = { Text("LEGENDARY EXCLUSIVES LOOT (Cosmetic Weapon)", fontFamily = FontFamily.Monospace, fontSize = 9.sp) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WarningRed, unfocusedBorderColor = GlassTintBorder, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = { showAddDialog = false },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("FORFEIT", fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                if (bName.isNotEmpty()) {
                                    viewModel.addNewBoss(bName, bDesc, bHP, bTitle, bGear)
                                    showAddDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WarningRed),
                            shape = RoundedCornerShape(8.dp),
                            enabled = bName.isNotEmpty(),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("DEPLOY TARGET", color = Color.Black, fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BossTacticalCard(
    boss: BossEntity,
    isDefeated: Boolean,
    onDelete: () -> Unit
) {
    val hpPercentage = if (boss.maxHealth > 0) boss.currentHealth / boss.maxHealth else 0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isDefeated) Color(0xFF0C1D18) else CyberNavy)
            .border(
                1.dp,
                if (isDefeated) CyberCyan.copy(alpha = 0.5f) else WarningRed.copy(alpha = 0.4f),
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = if (isDefeated) Icons.Default.CheckCircle else Icons.Default.Dangerous,
                        contentDescription = null,
                        tint = if (isDefeated) CyberCyan else WarningRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isDefeated) "TARGET TERMINATED (DEFEATED)" else "CAMPAIGN HOSTILE BOSS",
                        color = if (isDefeated) CyberCyan else WarningRed,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "Purge", tint = WarningRed.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                }
            }

            // Name
            Text(
                text = boss.name,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            // Description
            Text(
                text = boss.description,
                color = TextSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )

            if (!isDefeated) {
                // HP bar
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "COMPLETION SHIELD INTEGRITY (HP)",
                            color = TextSecondary,
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = String.format("%.0f / %.0f HP", boss.currentHealth, boss.maxHealth),
                            color = WarningRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    LinearProgressIndicator(
                        progress = { hpPercentage },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = WarningRed,
                        trackColor = Color(0xFF2E1218)
                    )
                }
            }

            // Loot prospects
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F152B))
                    .padding(10.dp)
            ) {
                Text(
                    text = "ACQUIRED REPUTATION PAYROLL:",
                    color = TextSecondary.copy(alpha = 0.6f),
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Text("Custom Title", color = TextSecondary, fontSize = 7.sp, fontFamily = FontFamily.Monospace)
                        Text("« ${boss.rewardTitle} »", color = EliteGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Column {
                        Text("Special Weapon", color = TextSecondary, fontSize = 7.sp, fontFamily = FontFamily.Monospace)
                        Text(boss.rewardItem, color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}
