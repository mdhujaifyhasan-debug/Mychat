package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.CallMade
import androidx.compose.material.icons.automirrored.rounded.CallMissed
import androidx.compose.material.icons.automirrored.rounded.CallReceived
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CallRecord
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*
import com.example.viewmodel.PulseViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CallsTab(
    viewModel: PulseViewModel,
    modifier: Modifier = Modifier
) {
    val callRecords by viewModel.callRecords.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    var showStartCallDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Create Call Link Card
            item {
                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { /* Link copied */ }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Link, contentDescription = null, tint = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Create call link",
                                color = DarkTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Share a link for your Pulse video or voice call",
                                color = DarkTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Quick Demo Bar to Simulate Incoming Call
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Calls",
                        color = DarkTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedButton(
                        onClick = { viewModel.simulateIncomingCall("Sophia Rivera", true) },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldPrimary)
                    ) {
                        Icon(Icons.Rounded.RingVolume, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simulate Incoming Call", fontSize = 11.sp)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Call Records List
            items(callRecords, key = { it.id }) { call ->
                CallRecordItem(
                    call = call,
                    onCallBack = { isVideo ->
                        viewModel.startCall(
                            peerId = call.peerId,
                            peerName = call.peerName,
                            peerAvatarHex = call.peerAvatarHex,
                            isVideo = isVideo
                        )
                    }
                )
                HorizontalDivider(color = DarkDivider, thickness = 0.5.dp, modifier = Modifier.padding(start = 72.dp))
            }
        }

        // FAB to start call
        FloatingActionButton(
            onClick = { showStartCallDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
                .testTag("new_call_fab"),
            containerColor = EmeraldPrimary,
            contentColor = Color.Black
        ) {
            Icon(Icons.Rounded.Call, contentDescription = "New Call")
        }
    }

    if (showStartCallDialog) {
        AlertDialog(
            onDismissRequest = { showStartCallDialog = false },
            title = { Text("Start a Call", color = DarkTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp)) {
                    items(contacts) { contact ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            UserAvatar(name = contact.name, colorHex = contact.avatarColorHex, size = 38.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(contact.name, color = DarkTextPrimary, fontWeight = FontWeight.SemiBold)
                                Text(contact.phone, color = DarkTextSecondary, fontSize = 11.sp)
                            }
                            IconButton(onClick = {
                                showStartCallDialog = false
                                viewModel.startCall(contact.id, contact.name, contact.avatarColorHex, false)
                            }) {
                                Icon(Icons.Rounded.Call, contentDescription = "Voice", tint = EmeraldPrimary)
                            }
                            IconButton(onClick = {
                                showStartCallDialog = false
                                viewModel.startCall(contact.id, contact.name, contact.avatarColorHex, true)
                            }) {
                                Icon(Icons.Rounded.Videocam, contentDescription = "Video", tint = CyanAccent)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStartCallDialog = false }) {
                    Text("Close", color = EmeraldPrimary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}

@Composable
fun CallRecordItem(
    call: CallRecord,
    onCallBack: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCallBack(call.isVideo) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(name = call.peerName, colorHex = call.peerAvatarHex, size = 46.dp)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = call.peerName,
                color = if (call.isMissed) CallMissedRed else DarkTextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                val icon = when {
                    call.isMissed -> Icons.AutoMirrored.Rounded.CallMissed
                    call.isIncoming -> Icons.AutoMirrored.Rounded.CallReceived
                    else -> Icons.AutoMirrored.Rounded.CallMade
                }
                val iconColor = if (call.isMissed) CallMissedRed else EmeraldPrimary
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                val timeStr = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(call.timestamp))
                val durationText = if (call.durationSeconds > 0) " (${call.durationSeconds / 60}m ${call.durationSeconds % 60}s)" else ""
                Text(
                    text = "$timeStr$durationText",
                    color = DarkTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        IconButton(onClick = { onCallBack(call.isVideo) }) {
            Icon(
                imageVector = if (call.isVideo) Icons.Rounded.Videocam else Icons.Rounded.Call,
                contentDescription = "Call",
                tint = EmeraldPrimary
            )
        }
    }
}
