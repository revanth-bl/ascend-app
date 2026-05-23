package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfileEntity
import com.example.ui.components.ProceduralAvatar
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: GameViewModel,
    profile: UserProfileEntity,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val clipboardManager = LocalClipboardManager.current

    // Local states for form editing
    var currentUsername by remember { mutableStateOf(profile.username) }
    var currentBio by remember { mutableStateOf(profile.bio) }
    var selectedAvatar by remember { mutableStateOf(profile.customAvatarUri) }
    var selectedBanner by remember { mutableStateOf(profile.bannerImage) }
    var activeTheme by remember { mutableStateOf(profile.appTheme) }
    var sfxEnabled by remember { mutableStateOf(profile.soundEffectsEnabled) }
    var animIntensity by remember { mutableStateOf(profile.animationIntensity) }
    var notifsEnabled by remember { mutableStateOf(profile.notificationsEnabled) }

    // Dialog flags
    var showAvatarDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showBannerDialog by remember { mutableStateOf(false) }
    var notificationSnackMessage by remember { mutableStateOf("") }
    var exportedTextData by remember { mutableStateOf("") }

    // Avatar Presets
    val avatars = listOf(
        Pair("avatar_1", "Cyber Vanguard"),
        Pair("avatar_2", "Tactical Slicer"),
        Pair("avatar_3", "Stealth Operative"),
        Pair("avatar_4", "Digital Maverick"),
        Pair("avatar_5", "Omega Sentinel")
    )

    // Banner presets
    val banners = listOf(
        "Slate Dark", "Cyber Navy", "Eclipse Red", "Cosmic Nebula", "Neon Green", "Twilight Purple"
    )

    // Save state on any profile edit change automatically
    LaunchedEffect(currentUsername, currentBio, selectedAvatar, selectedBanner, activeTheme, sfxEnabled, animIntensity, notifsEnabled) {
        viewModel.updateActiveProfileSettings(
            username = currentUsername,
            bio = currentBio,
            customAvatarUri = selectedAvatar,
            bannerImage = selectedBanner,
            appTheme = activeTheme,
            soundEffectsEnabled = sfxEnabled,
            animationIntensity = animIntensity,
            notificationsEnabled = notifsEnabled
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberNavy)
                    .statusBarsPadding()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberBlack)
                        .border(1.dp, GlassTintBorder, RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CyberCyan)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "SYSTEM CONFIGURATION",
                        color = CyberCyan,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Sectors Calibration & Identity Registry",
                        color = TextSecondary,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Banner & Profile Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, GlassTintBorder, RoundedCornerShape(16.dp))
                ) {
                    // Banner background simulated color
                    val bannerBrush = when (selectedBanner) {
                        "Eclipse Red" -> Brush.verticalGradient(listOf(Color(0xFF8B0000), CyberBlack))
                        "Cosmic Nebula" -> Brush.verticalGradient(listOf(Color(48, 25, 52), CyberBlack))
                        "Neon Green" -> Brush.verticalGradient(listOf(Color(0, 100, 80), CyberBlack))
                        "Twilight Purple" -> Brush.verticalGradient(listOf(Color(75, 0, 130), CyberBlack))
                        "Cyber Navy" -> Brush.verticalGradient(listOf(Color(12, 16, 33), CyberBlack))
                        else -> Brush.verticalGradient(listOf(SlateDark, CyberBlack))
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(bannerBrush)
                    )

                    // Overlay content
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Avatar View with level and rank frame
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(CyberBlack.copy(alpha = 0.5f))
                                .border(
                                    2.dp,
                                    when (profile.rank) {
                                        "Bronze" -> Color(0xFFCD7F32)
                                        "Silver" -> Color(0xFFC0C0C0)
                                        "Gold" -> EliteGold
                                        "Elite" -> CyberCyan
                                        "Apex" -> CyberPink
                                        "Mythic" -> CyberPurple
                                        else -> TextSecondary
                                    },
                                    CircleShape
                                )
                                .clickable { showAvatarDialog = true }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            ProceduralAvatar(
                                className = "Warrior",
                                rank = profile.rank,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Text details
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentUsername.uppercase(),
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit photo",
                                    tint = CyberCyan,
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clickable { showAvatarDialog = true }
                                )
                            }
                            Text(
                                text = "SYSTEM ID: ${profile.userId}",
                                color = TextSecondary,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "COGNITIVE VECTOR: ${profile.characterClass.uppercase()}",
                                color = CyberCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(GlassTintBorder)
                                    .clickable { showBannerDialog = true }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Palette, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(10.dp))
                                Text("CHANGE BANNER", color = TextPrimary, fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // 1. ACCOUNT SETTINS PANEL
                SettingsCard(title = "SECURE IDENTITY CREDENTIALS") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Username Edit
                        OutlinedTextField(
                            value = currentUsername,
                            onValueChange = { currentUsername = it },
                            label = { Text("OPERATIVE CODENAME", fontSize = 9.sp, fontFamily = FontFamily.Monospace) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = GlassTintBorder,
                                focusedLabelColor = CyberCyan,
                                unfocusedLabelColor = TextSecondary
                            ),
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_username_input")
                        )

                        // Bio Edit
                        OutlinedTextField(
                            value = currentBio,
                            onValueChange = { currentBio = it },
                            label = { Text("BIOLOGY DISCIPLINE MEMORANDUM (BIO)", fontSize = 9.sp, fontFamily = FontFamily.Monospace) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = GlassTintBorder,
                                focusedLabelColor = CyberCyan,
                                unfocusedLabelColor = TextSecondary
                            ),
                            leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .testTag("settings_bio_input")
                        )

                        // Immutable ID view
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF070B16))
                                .border(1.dp, GlassTintBorder, RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("ASSOCIATED LOGIN EMAIL", color = TextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                Text(profile.email.ifEmpty { "guest@ascend.io" }, color = TextPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                            Icon(Icons.Default.Verified, contentDescription = "Immutable", tint = CyberCyan, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                // 2. Preferences & Themes
                SettingsCard(title = "TRAINING HUB PREFERENCES") {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Theming variations
                        Column {
                            Text("INTERFACE CALIBRATION THEME", color = TextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                val themes = listOf("Dark Cyber", "Eclipse Red", "Neon Green", "Twilight Purple")
                                themes.forEach { t ->
                                    val selected = activeTheme == t
                                    val tc = when (t) {
                                        "Eclipse Red" -> WarningRed
                                        "Neon Green" -> CyberCyan
                                        "Twilight Purple" -> CyberPurple
                                        else -> CyberCyan
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (selected) tc.copy(alpha = 0.15f) else Color(0xFF0F152B))
                                            .border(1.dp, if (selected) tc else GlassTintBorder, RoundedCornerShape(6.dp))
                                            .clickable { activeTheme = t }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(t.uppercase(), color = if (selected) tc else TextSecondary, fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }

                        // Sound effects toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("AUDITORY FEEDBACK MATRIX", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                Text("Play synthesizers and clicks on tactical completions", color = TextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            }
                            Switch(
                                checked = sfxEnabled,
                                onCheckedChange = { sfxEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = CyberBlack,
                                    checkedTrackColor = CyberCyan,
                                    uncheckedThumbColor = TextSecondary,
                                    uncheckedTrackColor = GlassTintBorder
                                ),
                                modifier = Modifier.testTag("settings_sfx_toggle")
                            )
                        }

                        // Animation scale selector
                        Column {
                            Text("DYNAMICS REVEAL INTENSITY", color = TextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                val intensities = listOf("Low", "Medium", "High")
                                intensities.forEach { intensity ->
                                    val selected = animIntensity == intensity
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (selected) CyberCyan.copy(alpha = 0.15f) else Color(0xFF0F152B))
                                            .border(1.dp, if (selected) CyberCyan else GlassTintBorder, RoundedCornerShape(6.dp))
                                            .clickable { animIntensity = intensity }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(intensity.uppercase(), color = if (selected) CyberCyan else TextSecondary, fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Notification remiders
                SettingsCard(title = "TACTICAL INTERCEPT NOTIFICATIONS") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("MANDATORY STATUS PINGERS", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            Text("Remind routines, streaks and rating decay risks", color = TextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        }
                        Switch(
                            checked = notifsEnabled,
                            onCheckedChange = { notifsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CyberBlack,
                                checkedTrackColor = CyberCyan,
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = GlassTintBorder
                            ),
                            modifier = Modifier.testTag("settings_notifs_toggle")
                        )
                    }
                }

                // 4. Data storage tools: Backup, Notepad export and Reset
                SettingsCard(title = "HARDWARE TERMINAL STORAGE") {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Clipboard / Text Export
                        Button(
                            onClick = {
                                viewModel.exportDataToText { content ->
                                    exportedTextData = content
                                    showExportDialog = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F152B)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .testTag("settings_export_button")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("EXPORT STATS SUMMARY (EXCEL/NOTEPAD TEXT)", color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }

                        // Backup
                        Button(
                            onClick = {
                                viewModel.backupData()
                                notificationSnackMessage = "CLOUD BACKUP COMPILATION: STRESSED SNAPSHOTS IN SECURED ACCOUNT!"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F152B)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, GlassTintBorder, RoundedCornerShape(8.dp))
                                .testTag("settings_backup_button")
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("BACKUP SECTOR LOGS TO DATA MATRIX", color = TextPrimary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }

                        // Wipe progress reset
                        Button(
                            onClick = { showResetDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = WarningRed.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, WarningRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .testTag("settings_reset_button")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = WarningRed, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("CRITICAL SYSTEM RESET (WIPE RECORD)", color = WarningRed, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                // State message banner
                if (notificationSnackMessage.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberCyan.copy(alpha = 0.08f))
                            .border(1.dp, CyberCyan, RoundedCornerShape(8.dp))
                            .clickable { notificationSnackMessage = "" }
                            .padding(10.dp)
                    ) {
                        Text(
                            text = notificationSnackMessage,
                            color = CyberCyan,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // LOGOUT VECTOR (CRITICAL REQUIREMENT)
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("settings_logout_submit"),
                    colors = ButtonDefaults.buttonColors(containerColor = WarningRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TERMINATE SECURE LINK (LOGOUT)",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Text(
                    text = "ASCEND Tactical Console Beta v3.02. Connected via encrypted local sandbox SQLite vectors. All transmissions are subject to strict cyber habit surveillance.",
                    color = TextSecondary.copy(alpha = 0.5f),
                    fontSize = 7.5.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = 10.sp
                )
            }
        }
    }

    // GALLERY/CAMERA SELECTOR DIALOG
    if (showAvatarDialog) {
        AlertDialog(
            onDismissRequest = { showAvatarDialog = false },
            containerColor = CyberNavy,
            modifier = Modifier.border(1.dp, CyberCyan, RoundedCornerShape(16.dp)),
            title = {
                Text("CAMERA / PRESENTS REGISTER", color = CyberCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select a cyberpunk character preset or simulate a secure device media picker trigger below:", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)

                    // Avatar Presets List
                    avatars.forEach { (avId, avName) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedAvatar == avId) CyberCyan.copy(alpha = 0.1f) else Color.Transparent)
                                .clickable {
                                    selectedAvatar = avId
                                    showAvatarDialog = false
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CyberBlack),
                                contentAlignment = Alignment.Center
                            ) {
                                ProceduralAvatar(className = "Strategist", rank = "Apex", modifier = Modifier.fillMaxSize())
                            }
                            Text(avName, color = if (selectedAvatar == avId) CyberCyan else TextPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }

                    HorizontalDivider(color = GlassTintBorder, thickness = 1.dp)

                    // Gallery / Photopicker simulation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF070B16))
                            .clickable {
                                selectedAvatar = "avatar_3" // set mockup custom
                                notificationSnackMessage = "GALLERY RETRIEVAL SEQUENCER: Simulated custom device photo selected!"
                                showAvatarDialog = false
                            }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = EliteGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SIMULATE LAUNCH GALLERY PHOTO-PICKER", color = EliteGold, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF070B16))
                            .clickable {
                                selectedAvatar = "avatar_4"
                                notificationSnackMessage = "CAMERA RESOLVER OPTIC: Simulated snapshots capturing completed!"
                                showAvatarDialog = false
                            }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SIMULATE SECURE OPTIC CAMERA TRIGGER", color = CyberCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAvatarDialog = false }) {
                    Text("ABORT", color = WarningRed, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                }
            }
        )
    }

    // BANNER BACKGROUND DIALOG
    if (showBannerDialog) {
        AlertDialog(
            onDismissRequest = { showBannerDialog = false },
            containerColor = CyberNavy,
            modifier = Modifier.border(1.dp, CyberCyan, RoundedCornerShape(16.dp)),
            title = {
                Text("TACTICAL DRAWINGS COVERS", color = CyberCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Choose an active sector background cover for your identity layout profile:", color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(4.dp))
                    banners.forEach { banner ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedBanner == banner) CyberCyan.copy(alpha = 0.12f) else Color(0xFF070B16))
                                .border(1.dp, if (selectedBanner == banner) CyberCyan else GlassTintBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedBanner = banner
                                    showBannerDialog = false
                                }
                                .padding(12.dp)
                        ) {
                            Text(banner.uppercase(), color = if (selectedBanner == banner) CyberCyan else TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBannerDialog = false }) {
                    Text("ABORT", color = WarningRed, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                }
            }
        )
    }

    // STATS EXPORT DISPLAY DIALOG (NOTEPAD / EXCEL STYLE)
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            containerColor = CyberNavy,
            modifier = Modifier.border(2.dp, CyberCyan, RoundedCornerShape(16.dp)),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("NOTEPAD SUMMARY EXPORTER", color = CyberCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    IconButton(onClick = {
                        clipboardManager.setText(AnnotatedString(exportedTextData))
                        notificationSnackMessage = "DIRECTORY: COPIED SECURE HABITS SUMMARY TO USER CLIPBOARD!"
                        showExportDialog = false
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyberCyan, modifier = Modifier.size(16.dp))
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("The progress logs have been compiled successfully as plain text files (notepad config):", color = TextSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberBlack)
                            .border(1.dp, GlassTintBorder, RoundedCornerShape(8.dp))
                            .verticalScroll(rememberScrollState())
                            .padding(12.dp)
                    ) {
                        Text(
                            text = exportedTextData,
                            color = TextPrimary,
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 11.sp
                        )
                    }

                    // Exported status notify
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyberCyan.copy(alpha = 0.12f))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Locally saved to: internal_documents/ascend_notepad.txt",
                            color = CyberCyan,
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        clipboardManager.setText(AnnotatedString(exportedTextData))
                        notificationSnackMessage = "HABIT SUMMARY SUCCESSFULLY EXPORTED TO COPIED MEMORY CELL!"
                        showExportDialog = false
                    }) {
                        Text("COPY SUMMARY", color = CyberCyan, fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = { showExportDialog = false }) {
                        Text("CLOSE", color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                }
            }
        )
    }

    // PROGRESS RESET WARNING DIALOG
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = CyberNavy,
            modifier = Modifier.border(1.dp, WarningRed, RoundedCornerShape(16.dp)),
            title = {
                Text("CRITICAL RECOMPILATION", color = WarningRed, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            },
            text = {
                Text("BE WARNED: This protocol triggers complete terminal reset of all active habit sheets, level XP standings, gold balances and streak modifiers. This action is IRREVERSIBLE. Proceed with record override?", color = TextPrimary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        viewModel.resetProgress()
                        notificationSnackMessage = "SYSTEM COMMAND: HABIT SUMMARY STATISTICS ERUSED AND RESETTED TO RECRUIT DEFAULTS!"
                        showResetDialog = false
                    }) {
                        Text("YES, WIPE PROGRESS", color = WarningRed, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("ABORT", color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                }
            }
        )
    }

    // LOGOUT CONFIRMATION DIALOG
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = CyberNavy,
            modifier = Modifier.border(1.dp, WarningRed, RoundedCornerShape(16.dp)),
            title = {
                Text("TERMINATE SECURE SESSION?", color = WarningRed, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            },
            text = {
                Text("This will lock your profile sheets. The secure Habit surveillance telemetry will pause until you deploy encryption credentials again.", color = TextPrimary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                            viewModel.logoutUser()
                        },
                        modifier = Modifier.testTag("confirm_logout_action")
                    ) {
                        Text("CONFIRM DE-AUTHENTICATE", color = WarningRed, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("STAY ENGAGED", color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                }
            }
        )
    }
}

@Composable
fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GlassTintBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CyberNavy.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                color = CyberCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontFamily = FontFamily.Monospace
            )
            HorizontalDivider(color = GlassTintBorder, thickness = 1.dp)
            content()
        }
    }
}
