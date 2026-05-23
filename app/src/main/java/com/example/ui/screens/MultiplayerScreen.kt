package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.CosmeticEntity
import com.example.data.UserProfileEntity
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MultiplayerScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val cosmetics by viewModel.allCosmetics.collectAsState()
    val profileState by viewModel.userProfile.collectAsState()
    val lastLooted by viewModel.lastItemLooted
    val showLoot by viewModel.showLootDialog

    val profile = profileState ?: UserProfileEntity()

    var activeTab by remember { mutableStateOf(0) } // 0: Customization/Store, 1: Guild/Battle Pass

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Coins Balance & Store Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "MULTIPLAYER OPERATIONS CENTER",
                    color = CyberCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = if (activeTab == 0) "CUSTOMIZATION & GACHA" else "GUILDS & PROGRESSION",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }

            // CC Wallet balance
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF131A33))
                    .border(1.dp, GlassTintBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = null,
                    tint = EliteGold,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${profile.goldCoins} CC",
                    color = EliteGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.testTag("currency_balance_label")
                )
            }
        }

        // Sub tab navigation
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = Color.Transparent,
            contentColor = CyberCyan,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = CyberCyan
                )
            },
            divider = { HorizontalDivider(color = GlassTintBorder) }
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = {
                    Text(
                        "CUSTOMIZATION STATION",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = {
                    Text(
                        "SEASONAL GUILDS PASS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }

        if (activeTab == 0) {
            // Store / customize tab
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Buy Loot Box Banner card block
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            androidx.compose.ui.graphics.Brush.horizontalGradient(
                                colors = listOf(CyberPurple.copy(alpha = 0.3f), Color(0xFF1B0C25))
                            )
                        )
                        .border(1.dp, CyberPurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "TACTICAL COFFEE CRATE (Loot crate)",
                                color = CyberPink,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Roll mystery cosmetic gear! Unlocks legendary swords, robotic pets, visual titles or cyber auras.",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 14.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = { viewModel.purchaseLootCrate() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            enabled = profile.goldCoins >= 80,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .border(1.dp, CyberCyan, RoundedCornerShape(8.dp))
                                .testTag("purchase_loot_crate")
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("ROLL CRATE", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color.Black, modifier = Modifier.size(10.dp))
                                    Text("80 CC", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }

                // Grid list of visual items
                Text(
                    text = "INVENTORY CUSTOMIZATION MODULES",
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Box(modifier = Modifier.weight(1f)) {
                    val scroll = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scroll),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (item in cosmetics) {
                            CosmeticInventoryItem(
                                cosmetic = item,
                                pCoins = profile.goldCoins,
                                onBuy = { viewModel.buyCosmetic(item.id) },
                                onEquip = { viewModel.equipCosmetic(item.id, item.category) }
                            )
                        }
                    }
                }
            }
        } else {
            // Guild and battle pass tab progress
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Battle Pass Eclipse Division Details
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberNavy)
                        .border(1.dp, GlassTintBorder, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "ECLIPSE DIVISION SEASON 1",
                                    color = CyberCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "PASSIVE BATTLE PASS PROGRESS",
                                    color = TextSecondary,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = "ENDS IN 29D",
                                color = WarningRed,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Progress representation (battle pass level)
                        val completedJobs = profile.streak * 10f
                        val passProgress = (completedJobs / 100f).coerceIn(0.15f, 0.95f)

                        LinearProgressIndicator(
                            progress = { passProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = CyberPurple,
                            trackColor = Color(0xFF0C1021)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tier ${(completedJobs/20).toInt() + 1} of 100",
                                color = TextSecondary,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Next cosmetic reward: Level ${((completedJobs/20).toInt() + 2) * 5}",
                                color = CyberCyan,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Competitive Guild Squad standby
                Text(
                    text = "ACTIVE PVP SQUAD LEADERBOARD (Tactical Teams)",
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberNavy)
                        .border(1.dp, GlassTintBorder, RoundedCornerShape(12.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    val fakeTeammates = listOf(
                        LeaderboardTeammate("1", "Operative_Z9", "APEX", 84, CyberCyan, true),
                        LeaderboardTeammate("2", "DeepFocus_God", "Mythic", 55, CyberPink, false),
                        LeaderboardTeammate("3", profile.username + " (YOU)", profile.rank, profile.rankRating, EliteGold, false),
                        LeaderboardTeammate("4", "GymBerserker_X", "Gold", 92, EliteGold, false),
                        LeaderboardTeammate("5", "SlackerDetox_99", "Bronze", 21, Color(0xFFCD7F32), false)
                    )

                    for (tm in fakeTeammates) {
                        LeaderboardRow(teammate = tm)
                    }
                }

                // Class archetype selections HUD
                Text(
                    text = "RECOMPILE CLASS ARCHETYPE BASE",
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberNavy)
                        .border(1.dp, GlassTintBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val classDescriptions = listOf(
                        Pair("Warrior", "Passive: completes Fitness contracts deals 2X damage to real-life Bosses! Theme is Neon Pink."),
                        Pair("Scholar", "Passive: complete Study and Coding operations rewards 20% additional gold coins! Theme is Cyber Cyan."),
                        Pair("Strategist", "Passive: allows one-time daily costless contract reroll! Theme is Gold."),
                        Pair("Creator", "Passive: gains double XP from focus meditation blocks! Theme is Purple."),
                        Pair("Monk", "Passive: reduces active burnout rate by 50%! Theme is Indigo.")
                    )

                    classDescriptions.forEach { item ->
                        val selected = profile.characterClass == item.first
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) CyberCyan.copy(alpha = 0.08f) else Color(0xFF0F152B))
                                .border(1.dp, if (selected) CyberCyan else GlassTintBorder, RoundedCornerShape(8.dp))
                                .clickable { viewModel.selectClass(item.first) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RadioButton(
                                selected = selected,
                                onClick = { viewModel.selectClass(item.first) },
                                colors = RadioButtonDefaults.colors(selectedColor = CyberCyan, unselectedColor = TextSecondary)
                            )
                            Column {
                                Text(
                                    text = item.first.uppercase(),
                                    color = if (selected) CyberCyan else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = item.second,
                                    color = TextSecondary,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog showing gacha item unlock details
    if (showLoot) {
        Dialog(onDismissRequest = { viewModel.showLootDialog.value = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberNavy),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, CyberPink, RoundedCornerShape(16.dp))
                    .padding(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "CYBER TACTICAL CRATE DETONATION",
                        color = CyberPink,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    if (lastLooted != null) {
                        val item = lastLooted!!
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(getRarityColor(item.rarity).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getCategoryTypeIcon(item.category),
                                contentDescription = null,
                                tint = getRarityColor(item.rarity),
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Text(
                            text = "UNLOCKED RARITY ${item.rarity.uppercase()}",
                            color = getRarityColor(item.rarity),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )

                        Text(
                            text = item.name,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = item.description,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        // All unlocked or refund
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = EliteGold,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "ALL COSMETICS PRE-UNLOCKED",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Refunded credit balances. Gained 40 CC reserves & 100 Character XP bonus.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        onClick = { viewModel.showLootDialog.value = false },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPink),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("SECURE GEAR", color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CosmeticInventoryItem(
    cosmetic: CosmeticEntity,
    pCoins: Int,
    onBuy: () -> Unit,
    onEquip: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (cosmetic.isEquipped) CyberCyan.copy(alpha = 0.05f) else Color(0xFF0F152B))
            .border(
                1.dp,
                if (cosmetic.isEquipped) CyberCyan else GlassTintBorder,
                RoundedCornerShape(10.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(getRarityColor(cosmetic.rarity).copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryTypeIcon(cosmetic.category),
                        contentDescription = null,
                        tint = getRarityColor(cosmetic.rarity),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = cosmetic.name,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = cosmetic.rarity.uppercase(),
                            color = getRarityColor(cosmetic.rarity),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(getRarityColor(cosmetic.rarity).copy(alpha = 0.12f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "SLOT: ${cosmetic.category.uppercase()} | ${cosmetic.description}",
                        color = TextSecondary,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Action
            if (cosmetic.isEquipped) {
                Text(
                    text = "EQUIPPED",
                    color = CyberCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
            } else if (cosmetic.isUnlocked) {
                Button(
                    onClick = onEquip,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .border(1.dp, CyberCyan, RoundedCornerShape(6.dp))
                        .testTag("equip_button_${cosmetic.id}")
                ) {
                    Text("EQUIP", color = CyberCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            } else {
                Button(
                    onClick = onBuy,
                    colors = ButtonDefaults.buttonColors(containerColor = EliteGold),
                    enabled = pCoins >= cosmetic.coinCost,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.testTag("buy_button_${cosmetic.id}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = Color.Black, modifier = Modifier.size(11.dp))
                        Text("${cosmetic.coinCost} CC", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardRow(teammate: LeaderboardTeammate) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(if (teammate.isPlayerLeader) CyberCyan.copy(alpha = 0.05f) else Color.Transparent)
            .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "${teammate.rankPosition}.",
                color = TextSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = teammate.name,
                color = if (teammate.isPlayerLeader) CyberCyan else TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = teammate.rankTier.uppercase(),
                color = teammate.rankColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "${teammate.rr} RR",
                color = TextSecondary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

data class LeaderboardTeammate(
    val rankPosition: String,
    val name: String,
    val rankTier: String,
    val rr: Int,
    val rankColor: Color,
    val isPlayerLeader: Boolean
)

fun getCategoryTypeIcon(category: String): ImageVector {
    return when (category) {
        "outfit" -> Icons.Default.CardMembership
        "weapon" -> Icons.Default.Security
        "pet" -> Icons.Default.SmartToy
        "background" -> Icons.Default.Photo
        "title" -> Icons.Default.Badge
        else -> Icons.Default.Check
    }
}
