package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*
import com.example.viewmodel.PulseViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: PulseViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.adminStats.collectAsState()
    val contacts by viewModel.contacts.collectAsState()

    BackHandler {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.AdminPanelSettings, contentDescription = null, tint = EmeraldPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pulse Admin Console", fontWeight = FontWeight.Bold, color = DarkTextPrimary)
                    }
                },
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
            // Live Metric Cards Grid
            item {
                Text("REAL-TIME PLATFORM METRICS", color = DarkTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminMetricCard(title = "Total Users", value = "${stats.totalUsers}", icon = Icons.Rounded.People, color = EmeraldPrimary, modifier = Modifier.weight(1f))
                    AdminMetricCard(title = "Online Now", value = "${stats.onlineUsers}", icon = Icons.Rounded.Wifi, color = CyanAccent, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AdminMetricCard(title = "Messages Sent", value = "${stats.totalMessages / 1000}K", icon = Icons.Rounded.Chat, color = Color(0xFFF72585), modifier = Modifier.weight(1f))
                    AdminMetricCard(title = "Encrypted Calls", value = "${stats.totalCalls / 1000}K", icon = Icons.Rounded.Call, color = Color(0xFFFFB703), modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // System Maintenance Mode Toggle
            item {
                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Maintenance Mode", color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                if (stats.isMaintenanceMode) "Active: Non-admin logins blocked" else "Normal operation: All services online",
                                color = if (stats.isMaintenanceMode) DangerRed else EmeraldPrimary,
                                fontSize = 12.sp
                            )
                        }
                        Switch(
                            checked = stats.isMaintenanceMode,
                            onCheckedChange = { viewModel.toggleMaintenanceMode(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = DangerRed)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Feature Flags Management
            item {
                Text("GLOBAL FEATURE SWITCHES", color = DarkTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        stats.featureFlags.forEach { (flagKey, isEnabled) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = flagKey.replace("_", " ").uppercase(),
                                    color = DarkTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Switch(
                                    checked = isEnabled,
                                    onCheckedChange = { viewModel.toggleFeatureFlag(flagKey) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // User Moderation & Ban / Unban
            item {
                Text("USER MODERATION & ACCESS CONTROL", color = DarkTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(contacts) { contact ->
                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UserAvatar(name = contact.name, colorHex = contact.avatarColorHex, size = 38.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(contact.name, color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(contact.phone, color = DarkTextSecondary, fontSize = 11.sp)
                        }
                        if (contact.isBlocked) {
                            Button(
                                onClick = { viewModel.unbanUser(contact.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text("Unban", color = Color.Black, fontSize = 12.sp)
                            }
                        } else {
                            OutlinedButton(
                                onClick = { viewModel.banUser(contact.id) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text("Suspend", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Audit Logs
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text("SECURITY AUDIT LOGS", color = DarkTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                stats.auditLogs.forEach { log ->
                    Surface(
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(log.action, color = EmeraldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                val dateStr = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(log.timestamp))
                                Text(dateStr, color = DarkTextSecondary, fontSize = 10.sp)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(log.details, color = DarkTextPrimary, fontSize = 12.sp)
                            Text("By: ${log.adminName}", color = DarkTextSecondary, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, color = DarkTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(title, color = DarkTextSecondary, fontSize = 12.sp)
        }
    }
}
