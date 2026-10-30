package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.model.Community
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*
import com.example.viewmodel.PulseViewModel

@Composable
fun CommunitiesTab(
    viewModel: PulseViewModel,
    modifier: Modifier = Modifier
) {
    val communities by viewModel.communities.collectAsState()
    var showCreateCommunityDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            // New Community Starter Banner
            item {
                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCreateCommunityDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(EmeraldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Groups, contentDescription = null, tint = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "New Community",
                                color = DarkTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Bring multiple related groups together under one hub",
                                color = DarkTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Icon(Icons.Rounded.Add, contentDescription = null, tint = EmeraldPrimary)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Your Communities",
                    color = DarkTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(communities, key = { it.id }) { community ->
                CommunityCard(
                    community = community,
                    onOpenAnnouncement = {
                        viewModel.openChat(community.announcementGroupId)
                    }
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }

    if (showCreateCommunityDialog) {
        CreateCommunityDialog(
            onDismiss = { showCreateCommunityDialog = false }
        )
    }
}

@Composable
fun CommunityCard(
    community: Community,
    onOpenAnnouncement: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                UserAvatar(
                    name = community.name,
                    colorHex = community.iconColorHex,
                    size = 48.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = community.name,
                        color = DarkTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "${community.memberCount} members • ${community.subGroupIds.size + 1} groups",
                        color = DarkTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = community.description,
                color = DarkTextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = DarkDivider, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Announcement Channel Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenAnnouncement() }
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(EmeraldPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Campaign, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Announcements", color = DarkTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text("Official updates for all community members", color = DarkTextSecondary, fontSize = 11.sp)
                }
                Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = DarkTextSecondary)
            }
        }
    }
}

@Composable
fun CreateCommunityDialog(onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Community", color = DarkTextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Community Name", color = DarkTextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary, focusedTextColor = DarkTextPrimary, unfocusedTextColor = DarkTextPrimary)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Community Description & Rules", color = DarkTextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary, focusedTextColor = DarkTextPrimary, unfocusedTextColor = DarkTextPrimary)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Create Hub", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = DarkTextSecondary)
            }
        },
        containerColor = DarkSurfaceElevated
    )
}
