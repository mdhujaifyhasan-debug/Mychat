package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*
import com.example.viewmodel.PulseViewModel
import com.example.viewmodel.Screen

@Composable
fun SettingsTab(
    viewModel: PulseViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isTwoStepEnabled by viewModel.isTwoStepEnabled.collectAsState()
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsState()
    val readReceipts by viewModel.readReceiptsEnabled.collectAsState()
    val lastSeen by viewModel.lastSeenPrivacy.collectAsState()

    var showProfileEditDialog by remember { mutableStateOf(false) }
    var showQRProfileDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showStorageDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        // User Profile Header
        item {
            Surface(
                color = DarkSurfaceElevated,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showProfileEditDialog = true }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserAvatar(
                        name = currentUser.name,
                        colorHex = currentUser.avatarColorHex,
                        size = 58.dp
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentUser.name,
                            color = DarkTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = currentUser.statusText,
                            color = DarkTextSecondary,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${currentUser.phone} • @${currentUser.username}",
                            color = EmeraldPrimary,
                            fontSize = 11.sp
                        )
                    }
                    IconButton(onClick = { showQRProfileDialog = true }) {
                        Icon(
                            imageVector = Icons.Rounded.QrCode2,
                            contentDescription = "QR Code",
                            tint = EmeraldPrimary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Section: Core Systems
        item {
            SettingsCategoryHeader("SECURITY & ACCOUNT")
            SettingsItemRow(
                icon = Icons.Rounded.Key,
                iconTint = EmeraldPrimary,
                title = "Account & Passkeys",
                subtitle = "Security keys, change number, recovery"
            ) {
                viewModel.navigateTo(Screen.PrivacySecuritySettings)
            }
            SettingsItemRow(
                icon = Icons.Rounded.Devices,
                iconTint = CyanAccent,
                title = "Linked Devices",
                subtitle = "Active sessions across desktop and web"
            ) {
                viewModel.navigateTo(Screen.DeviceManagement)
            }
            SettingsItemRow(
                icon = Icons.Rounded.Lock,
                iconTint = SafetyGold,
                title = "Privacy & App Lock",
                subtitle = "Last seen, read receipts, biometric lock"
            ) {
                showPrivacyDialog = true
            }

            Spacer(modifier = Modifier.height(16.dp))
            SettingsCategoryHeader("PLATFORM & ENTERPRISE")

            SettingsItemRow(
                icon = Icons.Rounded.Storefront,
                iconTint = Color(0xFFF72585),
                title = "Business Suite",
                subtitle = "Product catalog, quick replies, away messages"
            ) {
                viewModel.navigateTo(Screen.BusinessTools)
            }
            SettingsItemRow(
                icon = Icons.Rounded.AdminPanelSettings,
                iconTint = EmeraldPrimary,
                title = "Admin Console",
                subtitle = "Platform metrics, feature flags, user management"
            ) {
                viewModel.navigateTo(Screen.AdminDashboard)
            }

            Spacer(modifier = Modifier.height(16.dp))
            SettingsCategoryHeader("CHATS & MEDIA")

            SettingsItemRow(
                icon = Icons.Rounded.Chat,
                iconTint = EmeraldLight,
                title = "Chats & Backup",
                subtitle = "Theme, chat wallpaper, encrypted cloud backup"
            ) {
                // Show info
            }
            SettingsItemRow(
                icon = Icons.Rounded.Notifications,
                iconTint = Color(0xFFFFB703),
                title = "Notifications & Sounds",
                subtitle = "Message, group and call alerts, vibration"
            ) {
                // Show info
            }
            SettingsItemRow(
                icon = Icons.Rounded.PieChart,
                iconTint = Color(0xFF4CC9F0),
                title = "Storage & Data",
                subtitle = "Network usage, auto-download, cache clear"
            ) {
                showStorageDialog = true
            }

            Spacer(modifier = Modifier.height(16.dp))
            SettingsCategoryHeader("ACCOUNT ACTIONS")

            SettingsItemRow(
                icon = Icons.Rounded.Logout,
                iconTint = DarkTextSecondary,
                title = "Log Out",
                subtitle = "Sign out from this Android device"
            ) {
                viewModel.logout()
            }
            SettingsItemRow(
                icon = Icons.Rounded.DeleteForever,
                iconTint = DangerRed,
                title = "Delete Account",
                subtitle = "Permanently remove your data and E2EE keys"
            ) {
                showDeleteAccountDialog = true
            }
        }
    }

    // Profile Edit Dialog
    if (showProfileEditDialog) {
        var name by remember { mutableStateOf(currentUser.name) }
        var username by remember { mutableStateOf(currentUser.username) }
        var status by remember { mutableStateOf(currentUser.statusText) }
        var phone by remember { mutableStateOf(currentUser.phone) }
        var email by remember { mutableStateOf(currentUser.email) }

        AlertDialog(
            onDismissRequest = { showProfileEditDialog = false },
            title = { Text("Edit Profile", color = DarkTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary, focusedTextColor = DarkTextPrimary, unfocusedTextColor = DarkTextPrimary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary, focusedTextColor = DarkTextPrimary, unfocusedTextColor = DarkTextPrimary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = status,
                        onValueChange = { status = it },
                        label = { Text("About Status") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary, focusedTextColor = DarkTextPrimary, unfocusedTextColor = DarkTextPrimary)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateProfile(name, username, status, phone, email)
                        showProfileEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Save", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfileEditDialog = false }) {
                    Text("Cancel", color = DarkTextSecondary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }

    // QR Profile Dialog
    if (showQRProfileDialog) {
        AlertDialog(
            onDismissRequest = { showQRProfileDialog = false },
            icon = { Icon(Icons.Rounded.QrCode2, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(48.dp)) },
            title = { Text("Your Pulse QR Code", color = DarkTextPrimary, textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.QrCode2, contentDescription = null, tint = Color.Black, modifier = Modifier.fillMaxSize())
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(currentUser.name, color = DarkTextPrimary, fontWeight = FontWeight.Bold)
                    Text("pulse.me/@${currentUser.username}", color = EmeraldPrimary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Friends can scan this code to start an end-to-end encrypted chat with you instantly.", color = DarkTextSecondary, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            },
            confirmButton = {
                TextButton(onClick = { showQRProfileDialog = false }) {
                    Text("Done", color = EmeraldPrimary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }

    // Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Controls", color = DarkTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Read Receipts", color = DarkTextPrimary, fontWeight = FontWeight.SemiBold)
                            Text("If turned off, you won't send or receive blue ticks", color = DarkTextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = readReceipts,
                            onCheckedChange = { viewModel.setReadReceipts(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("App Lock (Biometric / PIN)", color = DarkTextPrimary, fontWeight = FontWeight.SemiBold)
                            Text("Require fingerprint or passcode to open Pulse", color = DarkTextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = isBiometricEnabled,
                            onCheckedChange = { viewModel.toggleAppLock(it, "1234") },
                            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Two-Step Verification", color = DarkTextPrimary, fontWeight = FontWeight.SemiBold)
                            Text("PIN code required when registering on a new device", color = DarkTextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = isTwoStepEnabled,
                            onCheckedChange = { viewModel.toggleTwoStep(it, "4321") },
                            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Save", color = EmeraldPrimary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }

    // Storage Management Dialog
    if (showStorageDialog) {
        AlertDialog(
            onDismissRequest = { showStorageDialog = false },
            title = { Text("Storage & Cache", color = DarkTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Total Storage Used: 482 MB", color = DarkTextPrimary, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Media & Voice Notes: 310 MB\n• Documents: 92 MB\n• Cached Thumbnails: 80 MB", color = DarkTextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showStorageDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Rounded.CleaningServices, contentDescription = null, tint = EmeraldPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Clear Cache (80 MB)", color = DarkTextPrimary)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStorageDialog = false }) {
                    Text("Done", color = EmeraldPrimary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }

    // Delete Account Dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Delete Pulse Account?", color = DangerRed, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Deleting your account will:\n• Delete your account info and profile picture\n• Delete you from all Pulse groups and channels\n• Erase your cryptographic message history",
                    color = DarkTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountDialog = false
                        viewModel.logout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("Delete Account", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel", color = DarkTextSecondary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}

@Composable
fun SettingsCategoryHeader(title: String) {
    Text(
        text = title,
        color = DarkTextSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsItemRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = DarkTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(subtitle, color = DarkTextSecondary, fontSize = 11.sp)
        }
        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = DarkTextSecondary.copy(alpha = 0.6f))
    }
}
