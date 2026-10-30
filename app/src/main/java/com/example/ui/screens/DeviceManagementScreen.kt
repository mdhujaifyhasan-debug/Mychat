package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.PulseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceManagementScreen(
    viewModel: PulseViewModel,
    modifier: Modifier = Modifier
) {
    val devices by viewModel.devices.collectAsState()

    BackHandler {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Linked Devices", fontWeight = FontWeight.Bold, color = DarkTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = DarkTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurfaceElevated)
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            item {
                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.LaptopMac, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Use Pulse on Web & Desktop", color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Link your computers or tablets securely using QR code or Passkey without sharing your master encryption keys.",
                            color = DarkTextSecondary,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { /* Link new device */ },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Icon(Icons.Rounded.QrCodeScanner, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Link a Device", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("ACTIVE SESSIONS", color = DarkTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(devices, key = { it.id }) { device ->
                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (device.deviceName.contains("Phone")) Icons.Rounded.PhoneAndroid else Icons.Rounded.Computer,
                            contentDescription = null,
                            tint = if (device.isCurrent) EmeraldPrimary else DarkTextSecondary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(device.deviceName, color = DarkTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                if (device.isCurrent) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("• Current", color = EmeraldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text("${device.osVersion} • ${device.location}", color = DarkTextSecondary, fontSize = 11.sp)
                            Text("Status: ${device.lastActive}", color = if (device.isCurrent) EmeraldPrimary else DarkTextSecondary, fontSize = 11.sp)
                        }
                        if (!device.isCurrent) {
                            TextButton(onClick = { viewModel.removeDevice(device.id) }) {
                                Text("Revoke", color = DangerRed, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
