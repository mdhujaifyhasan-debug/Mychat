package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.PulseViewModel
import com.example.viewmodel.Screen

@Composable
fun AuthScreen(
    viewModel: PulseViewModel,
    modifier: Modifier = Modifier
) {
    var authMode by remember { mutableStateOf(0) } // 0: Phone + OTP, 1: Email + Password, 2: Forgot Password
    var phoneInput by remember { mutableStateOf("+1 555-0199") }
    var otpInput by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }

    var emailInput by remember { mutableStateOf("alex@pulse.im") }
    var passwordInput by remember { mutableStateOf("••••••••") }
    var isNewRegistration by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf("Alex Mercer") }
    var usernameInput by remember { mutableStateOf("alex_pulse") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Branding Icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(EmeraldPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Shield,
                    contentDescription = "Pulse Shield",
                    tint = Color.Black,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "mychat",
                color = DarkTextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Real-time Encrypted Communication",
                color = EmeraldPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Auth Tabs: Phone vs Email
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceElevated)
                    .padding(4.dp)
            ) {
                TabButton(
                    title = "Phone + OTP",
                    isSelected = authMode == 0,
                    modifier = Modifier.weight(1f)
                ) {
                    authMode = 0
                    errorMessage = null
                }
                TabButton(
                    title = "Email",
                    isSelected = authMode == 1,
                    modifier = Modifier.weight(1f)
                ) {
                    authMode = 1
                    errorMessage = null
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (authMode) {
                0 -> {
                    // Phone & OTP Flow
                    if (!otpSent) {
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            label = { Text("Phone Number (with Country Code)") },
                            modifier = Modifier.fillMaxWidth().testTag("phone_input"),
                            leadingIcon = { Icon(Icons.Rounded.Phone, contentDescription = null, tint = EmeraldPrimary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldPrimary,
                                focusedTextColor = DarkTextPrimary,
                                unfocusedTextColor = DarkTextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (phoneInput.length >= 8) {
                                    otpSent = true
                                    otpInput = "123456" // Auto-fill demo OTP
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("send_otp_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Send 6-Digit OTP", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text(
                            text = "Enter OTP sent to $phoneInput\n(Demo code: 123456)",
                            color = DarkTextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = otpInput,
                            onValueChange = { otpInput = it },
                            label = { Text("6-Digit Verification Code") },
                            modifier = Modifier.fillMaxWidth().testTag("otp_input"),
                            leadingIcon = { Icon(Icons.Rounded.LockClock, contentDescription = null, tint = EmeraldPrimary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldPrimary,
                                focusedTextColor = DarkTextPrimary,
                                unfocusedTextColor = DarkTextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (viewModel.loginWithPhone(phoneInput, otpInput)) {
                                    viewModel.navigateTo(Screen.Main(0))
                                } else {
                                    errorMessage = "Invalid OTP. Use 123456 for demo."
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("verify_otp_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Verify & Continue", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        TextButton(onClick = { otpSent = false }) {
                            Text("Change phone number", color = DarkTextSecondary, fontSize = 12.sp)
                        }
                    }
                }
                1 -> {
                    // Email & Password Flow
                    if (isNewRegistration) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary, focusedTextColor = DarkTextPrimary, unfocusedTextColor = DarkTextPrimary),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth().testTag("email_input"),
                        leadingIcon = { Icon(Icons.Rounded.Email, contentDescription = null, tint = EmeraldPrimary) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary, focusedTextColor = DarkTextPrimary, unfocusedTextColor = DarkTextPrimary),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth().testTag("password_input"),
                        leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null, tint = EmeraldPrimary) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary, focusedTextColor = DarkTextPrimary, unfocusedTextColor = DarkTextPrimary),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (viewModel.loginWithEmail(emailInput, passwordInput)) {
                                viewModel.navigateTo(Screen.Main(0))
                            } else {
                                errorMessage = "Please enter valid email & password"
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp).testTag("login_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isNewRegistration) "Create Encrypted Account" else "Sign In", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    TextButton(onClick = { isNewRegistration = !isNewRegistration }) {
                        Text(
                            text = if (isNewRegistration) "Already have an account? Sign In" else "New to Pulse? Register now",
                            color = EmeraldPrimary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = DangerRed, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = DarkDivider, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Google One-Tap Sign In
            OutlinedButton(
                onClick = {
                    viewModel.loginWithGoogle()
                    viewModel.navigateTo(Screen.Main(0))
                },
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("google_login_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkTextPrimary)
            ) {
                Icon(Icons.Rounded.AccountCircle, contentDescription = null, tint = EmeraldPrimary)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Continue with Google", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun TabButton(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        color = if (isSelected) EmeraldPrimary else Color.Transparent,
        shape = RoundedCornerShape(10.dp)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                color = if (isSelected) Color.Black else DarkTextSecondary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp
            )
        }
    }
}
