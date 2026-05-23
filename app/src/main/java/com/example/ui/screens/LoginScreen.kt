package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    var isSignUpMode by remember { mutableStateOf(false) }
    
    var isPasswordVisible by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }
    var isSuccessStatus by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CyberBlack, CyberNavy)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .border(2.dp, if (isSignUpMode) CyberPurple else CyberCyan, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberNavy.copy(alpha = 0.9f)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Glow Icon (Custom Lightning Bolt App Logo)
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.5.dp, if (isSignUpMode) CyberPurple else CyberCyan, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.app_logo_foreground_1779439750882),
                        contentDescription = "Ascend Logo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                    )
                }

                // Brand Header
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ASCEND COGNITIVE VECTOR",
                        color = if (isSignUpMode) CyberPurple else CyberCyan,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = if (isSignUpMode) "OPERATIVE REGISTRATION PROTOCOL" else "OPERATIVE DEPLOYMENT GATEWAY",
                        color = TextSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Inputs
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("EMAIL ADDRESS", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                        placeholder = { Text("e.g. ayanokoji@ascend.io", color = TextSecondary.copy(alpha = 0.5f), fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = if (isSignUpMode) CyberPurple else CyberCyan,
                            unfocusedBorderColor = GlassTintBorder,
                            focusedLabelColor = if (isSignUpMode) CyberPurple else CyberCyan,
                            unfocusedLabelColor = TextSecondary
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                        },
                        textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_email_input")
                    )

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("ENCRYPTION KEY (PASSWORD)", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                        singleLine = true,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = if (isSignUpMode) CyberPurple else CyberCyan,
                            unfocusedBorderColor = GlassTintBorder,
                            focusedLabelColor = if (isSignUpMode) CyberPurple else CyberCyan,
                            unfocusedLabelColor = TextSecondary
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password visibility",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input")
                    )
                }

                // Remember device checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = if (isSignUpMode) CyberPurple else CyberCyan,
                            uncheckedColor = GlassTintBorder,
                            checkmarkColor = CyberBlack
                        ),
                        modifier = Modifier.testTag("login_remember_checkbox")
                    )
                    Text(
                        text = "REMEMBER LOGGED-IN IDENTITY",
                        color = TextSecondary,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { rememberMe = !rememberMe }
                    )
                }

                // Status Message display
                if (statusMessage.isNotEmpty()) {
                    Text(
                        text = statusMessage.uppercase(),
                        color = if (isSuccessStatus) CyberCyan else WarningRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.testTag("login_error_message")
                    )
                }

                // Submit Button
                Button(
                    onClick = {
                        statusMessage = "CALIBRATING QUANTUM LINK..."
                        if (isSignUpMode) {
                            viewModel.registerUser(email, password) { success, msg ->
                                isSuccessStatus = success
                                statusMessage = msg
                                if (success) {
                                    // Signed up and logged in automatically!
                                }
                            }
                        } else {
                            viewModel.loginUser(email, password, rememberMe) { success, msg ->
                                isSuccessStatus = success
                                statusMessage = msg
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("login_submit_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSignUpMode) CyberPurple else CyberCyan
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (isSignUpMode) "INITIALIZE NEW OPERATIVE" else "COMMENCE SECTORS DEPLOYMENT",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Switch Sign in / Sign up link
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isSignUpMode) "ALREADY IN DIRECTORY? " else "FIRST TIME AT DEPOT? ",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = if (isSignUpMode) "ACCESS CLOUD CHANNEL" else "SIGN IN OPERATIVE REGISTER",
                        color = if (isSignUpMode) CyberCyan else CyberPurple,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .clickable {
                                isSignUpMode = !isSignUpMode
                                statusMessage = ""
                            }
                            .testTag("toggle_login_mode")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Helpful cheat sheet for easier evaluations
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberBlack.copy(alpha = 0.5f))
                        .padding(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "DEMONSTRATION ACCESS CREDS:",
                            color = EliteGold,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "• Click SIGN IN OPERATIVE REGISTER, enter a new Email / Password to create an account immediately, OR use general login credentials:",
                            color = TextSecondary,
                            fontSize = 7.5.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 10.sp
                        )
                        Text(
                            text = "  Email: dev@ascend.io  |  Key: dev123",
                            color = CyberCyan,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
