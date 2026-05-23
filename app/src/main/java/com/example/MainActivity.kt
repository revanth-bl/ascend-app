package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.components.*
import com.example.data.UserProfileEntity
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Opt-in to Edge-To-Edge drawing safe-areas
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                MainHudShell()
            }
        }
    }
}

// 7 HUD Navigation tabs
enum class HudTab(val label: String, val icon: ImageVector, val tag: String) {
    RECON("RECON", Icons.Default.Radar, "recon_tab"),
    OPERATIONS("OPERATIONS", Icons.Default.Assignment, "operations_tab"),
    FOCUS("FOCUS", Icons.Default.FilterCenterFocus, "focus_tab"),
    CAMPAIGNS("CAMPAIGNS", Icons.Default.FlashOn, "campaigns_tab"),
    UPGRADES("UPGRADES", Icons.Default.AutoAwesome, "upgrades_tab"),
    COACH("COACH", Icons.Default.SmartToy, "coach_tab"),
    GRID_PASS("GRID PASS", Icons.Default.SportsEsports, "grid_pass_tab")
}

@Composable
fun MainHudShell() {
    val gameViewModel: GameViewModel = viewModel()
    val profileState by gameViewModel.userProfile.collectAsState(initial = null)

    // Check if the database profile is still seeding/loading on first start
    if (profileState == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberBlack),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CircularProgressIndicator(color = CyberCyan)
                Text(
                    "SEEDING MATRIX FIELDS...",
                    color = CyberCyan,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        return
    }

    val profile = profileState!!

    // If profile is NOT logged in yet, prompt email and password controls
    if (!profile.isLoggedIn) {
        LoginScreen(viewModel = gameViewModel, modifier = Modifier.fillMaxSize())
        return
    }

    // Main application navigation parameters
    var currentTab by remember { mutableStateOf(HudTab.RECON) }
    var isProfileDrawerOpen by remember { mutableStateOf(false) }
    var showSettingsScreen by remember { mutableStateOf(false) }

    // Navigation and achievement popups
    var showAchievementsDialog by remember { mutableStateOf(false) }
    var showProgressDialog by remember { mutableStateOf(false) }

    // Core layout container modifier
    val rootModifier = Modifier
        .fillMaxSize()
        .background(CyberBlack)

    Box(modifier = rootModifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                // High fidelity Cyber Header Bar providing instant drawer shortcut accessibility support
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = CyberNavy.copy(alpha = 0.85f),
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.app_logo_foreground_1779439750882),
                                contentDescription = "App Logo",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            )
                            Column {
                                Text(
                                    text = "ASCEND // CORE_LINK",
                                    color = CyberCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "SECTOR: ${currentTab.label}",
                                    color = TextSecondary,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Compact accessibility header button to toggle the left-swiped profile panel
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberBlack)
                                .border(1.dp, GlassTintBorder, RoundedCornerShape(8.dp))
                                .clickable { isProfileDrawerOpen = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("top_bar_profile_trigger")
                        ) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Profile Panel", tint = CyberCyan, modifier = Modifier.size(16.dp))
                            Text(
                                text = profile.username.uppercase(),
                                color = TextPrimary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            },
            bottomBar = {
                // High fidelity Cyber Floating Bottom Navigation Row complying with edge-to-edge safe area navigation bar insets
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, GlassTintBorder, RoundedCornerShape(14.dp)),
                    color = CyberNavy.copy(alpha = 0.95f),
                    tonalElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HudTab.values().forEach { tab ->
                            val active = currentTab == tab
                            val activeColor = if (active) CyberCyan else TextSecondary.copy(alpha = 0.7f)
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { currentTab = tab }
                                    .padding(vertical = 8.dp)
                                    .testTag(tab.tag),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.label,
                                        tint = activeColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = tab.label,
                                        color = activeColor,
                                        fontSize = 7.5.sp,
                                        fontWeight = if (active) FontWeight.Black else FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        letterSpacing = 0.2.sp
                                    )
                                }
                            }
                        }
                    }
                }
            },
            contentWindowInsets = WindowInsets.safeDrawing // Prevent camera notch or status clock crop
        ) { innerPadding ->
            // Render current selected cyberpunk HUD pane with crossfade animator transitions
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(CyberBlack)
            ) {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(150)) togetherWith fadeOut(animationSpec = tween(150))
                    },
                    label = "hud_screen_switch"
                ) { targetTab ->
                    when (targetTab) {
                        HudTab.RECON -> DashboardScreen(viewModel = gameViewModel, modifier = Modifier.fillMaxSize())
                        HudTab.OPERATIONS -> MissionsScreen(viewModel = gameViewModel, modifier = Modifier.fillMaxSize())
                        HudTab.FOCUS -> FocusScreen(viewModel = gameViewModel, modifier = Modifier.fillMaxSize())
                        HudTab.CAMPAIGNS -> BossesScreen(viewModel = gameViewModel, modifier = Modifier.fillMaxSize())
                        HudTab.UPGRADES -> SkillsScreen(viewModel = gameViewModel, modifier = Modifier.fillMaxSize())
                        HudTab.COACH -> CoachScreen(viewModel = gameViewModel, modifier = Modifier.fillMaxSize())
                        HudTab.GRID_PASS -> MultiplayerScreen(viewModel = gameViewModel, modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }

        // LEFT SWIPE / SIDE PROFILE DRAWER PANEL (aligned to right, sliding left)
        if (isProfileDrawerOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable { isProfileDrawerOpen = false }
            ) {
                ProfileDrawer(
                    profile = profile,
                    isOpen = isProfileDrawerOpen,
                    onClose = { isProfileDrawerOpen = false },
                    onOpenSettings = {
                        isProfileDrawerOpen = false
                        showSettingsScreen = true
                    },
                    onViewProgress = { showProgressDialog = true },
                    onViewAchievements = { showAchievementsDialog = true },
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }

        // SYSTEM PROFILE CONFIG OVERLAY (SETTINGS SCREEN)
        if (showSettingsScreen) {
            SettingsScreen(
                viewModel = gameViewModel,
                profile = profile,
                onBack = { showSettingsScreen = false },
                modifier = Modifier.fillMaxSize()
            )
        }

        // TACTICAL ACHIEVEMENTS DIALOG
        if (showAchievementsDialog) {
            AlertDialog(
                onDismissRequest = { showAchievementsDialog = false },
                containerColor = CyberNavy,
                modifier = Modifier.border(1.dp, CyberPurple, RoundedCornerShape(16.dp)),
                title = {
                    Text("TACTICAL ACHIEVEMENT BADGES", color = CyberPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Earn cyber accolades through consistent focused habits:", color = TextSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        val achievements = listOf(
                            Triple("Habit Initiate", "Complete 5 daily routine items.", "reward_unlocked"),
                            Triple("Iron Spine", "Maintain a 10-day routines streak safely.", "reward_pending"),
                            Triple("Hyperfocus Pulse", "Reach Level 5 via operations.", "reward_unlocked"),
                            Triple("Boss Decimator", "Defeat 2 Campaign boss battle targets.", "reward_pending")
                        )
                        achievements.forEach { (title, desc, status) ->
                            val earned = status == "reward_unlocked"
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF070B16))
                                    .border(1.dp, if (earned) CyberPurple.copy(alpha = 0.5f) else GlassTintBorder, RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(title.uppercase(), color = if (earned) CyberPurple else TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    Text(desc, color = TextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                }
                                Icon(
                                    imageVector = if (earned) Icons.Default.EmojiEvents else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (earned) CyberPurple else TextSecondary.copy(alpha = 0.5f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAchievementsDialog = false }) {
                        Text("SECURE RETURN", color = CyberCyan, fontFamily = FontFamily.Monospace, fontSize = 9.sp)
                    }
                }
            )
        }

        // SURVEILLANCE PROGRESS CHARTS DIALOG
        if (showProgressDialog) {
            AlertDialog(
                onDismissRequest = { showProgressDialog = false },
                containerColor = CyberNavy,
                modifier = Modifier.border(1.dp, EliteGold, RoundedCornerShape(16.dp)),
                title = {
                    Text("TACTICAL WEEKLY BIOLOGY VECTORS", color = EliteGold, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Chronological habit scores of preceding cycles:", color = TextSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        
                        // Graph plotting layout representation
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberBlack)
                                .border(1.dp, GlassTintBorder, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                val weeklyPoints = listOf(45, 60, 55, 75, 80, 90, profile.weeklyPerformanceScore)
                                val days = listOf("M", "T", "W", "T", "F", "S", "S")
                                weeklyPoints.forEachIndexed { idx, pt ->
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("$pt", color = EliteGold, fontSize = 7.sp, fontFamily = FontFamily.Monospace)
                                        Box(
                                            modifier = Modifier
                                                .width(12.dp)
                                                .height((pt.toFloat() / 100f * 60f).dp)
                                                .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                                                .background(androidx.compose.ui.graphics.Brush.verticalGradient(listOf(EliteGold, CyberCyan)))
                                        )
                                        Text(days[idx], color = TextSecondary, fontSize = 7.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }
                        
                        Text("Your continuous feedback loops show an accuracy level of ${(profile.completionRate * 100).toInt()}% over past training runs.", color = TextPrimary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showProgressDialog = false }) {
                        Text("SECURE RETURN", color = CyberCyan, fontFamily = FontFamily.Monospace, fontSize = 9.sp)
                    }
                }
            )
        }
    }
}
