package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
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
import com.example.data.SkillEntity
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel

@Composable
fun SkillsScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val skills by viewModel.allSkills.collectAsState()
    val profileState by viewModel.userProfile.collectAsState()
    val profile = profileState ?: com.example.data.UserProfileEntity()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Command Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CyberNavy)
                .border(1.dp, GlassTintBorder, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DISCIPLINE SKILL CONFIGURATION",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "AVAILABLE PERK POINTS",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Balance of skill points
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberPurple.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = CyberPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "${profile.skillPoints} POINTS",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.testTag("perk_points_label")
                    )
                }
            }
        }

        Text(
            text = "TACTICAL CHASSIS UPGRADES (Passive Perks)",
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(top = 4.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(skills, key = { it.id }) { skill ->
                SkillNodeRow(
                    skill = skill,
                    canBuy = profile.skillPoints >= skill.costSkillPoints,
                    onUnlock = { viewModel.unlockSkillPerk(skill.id) }
                )
            }
        }
    }
}

@Composable
fun SkillNodeRow(
    skill: SkillEntity,
    canBuy: Boolean,
    onUnlock: () -> Unit
) {
    val isUnlocked = skill.isUnlocked

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isUnlocked) CyberPurple.copy(alpha = 0.08f) else CyberNavy)
            .border(
                1.dp,
                if (isUnlocked) CyberPurple else GlassTintBorder,
                RoundedCornerShape(12.dp)
            )
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Category Chip
                Text(
                    text = skill.category.uppercase(),
                    color = if (isUnlocked) CyberPurple else TextSecondary,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isUnlocked) CyberPurple.copy(alpha = 0.15f) else Color(0xFF0F152B))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Title
                Text(
                    text = skill.name,
                    color = if (isUnlocked) TextPrimary else TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                // Description
                Text(
                    text = skill.description,
                    color = if (isUnlocked) TextSecondary else TextSecondary.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Effect active details
                AnimatedVisibility(visible = isUnlocked) {
                    Text(
                        text = "★ EFFECT ACTIVE: ${skill.effectText}",
                        color = CyberCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Action unlock button
            if (isUnlocked) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberCyan.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LockOpen,
                        contentDescription = "Unlocked",
                        tint = CyberCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Button(
                    onClick = onUnlock,
                    enabled = canBuy,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberPurple,
                        disabledContainerColor = Color(0xFF0F152B)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier
                        .border(
                            1.dp,
                            if (canBuy) CyberPurple else GlassTintBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .testTag("unlock_skill_button_${skill.id}")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = if (canBuy) Color.White else TextSecondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${skill.costSkillPoints} LP",
                            color = if (canBuy) Color.White else TextSecondary.copy(alpha = 0.4f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
