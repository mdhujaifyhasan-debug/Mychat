package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Channel
import com.example.model.StatusStory
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*
import com.example.viewmodel.PulseViewModel
import com.example.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun UpdatesTab(
    viewModel: PulseViewModel,
    modifier: Modifier = Modifier
) {
    val stories by viewModel.stories.collectAsState()
    val channels by viewModel.channels.collectAsState()
    var showCreateStatusDialog by remember { mutableStateOf(false) }

    val myStory = stories.find { it.userId == "user_me" }
    val otherStories = stories.filterNot { it.userId == "user_me" }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Status Header
            item {
                Text(
                    text = "Status",
                    color = DarkTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 12.dp)
                )
            }

            // Status Horizontal Row
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // My Status Item
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                if (myStory != null) {
                                    viewModel.navigateTo(Screen.StatusViewer(myStory.id))
                                } else {
                                    showCreateStatusDialog = true
                                }
                            }
                        ) {
                            Box(modifier = Modifier.size(62.dp)) {
                                UserAvatar(
                                    name = "My Status",
                                    colorHex = 0xFF00A884,
                                    size = 60.dp
                                )
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.BottomEnd)
                                        .clip(CircleShape)
                                        .background(EmeraldPrimary)
                                        .border(2.dp, DarkBackground, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (myStory != null) Icons.Rounded.Visibility else Icons.Rounded.Add,
                                        contentDescription = "Add",
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "My Status",
                                color = DarkTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (myStory != null) "${myStory.viewerCount} views" else "Tap to add",
                                color = DarkTextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Friends' Stories
                    items(otherStories) { story ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                viewModel.markStoryViewed(story.id)
                                viewModel.navigateTo(Screen.StatusViewer(story.id))
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .border(
                                        width = 2.5.dp,
                                        color = if (story.isViewed) DarkDivider else EmeraldPrimary,
                                        shape = CircleShape
                                    )
                                    .padding(3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                UserAvatar(
                                    name = story.userName,
                                    colorHex = story.userAvatarHex,
                                    size = 54.dp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = story.userName.split(" ").firstOrNull() ?: "",
                                color = DarkTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(story.timestamp))
                            Text(
                                text = timeStr,
                                color = DarkTextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = DarkDivider, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Channels Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Channels",
                            color = DarkTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Stay updated on topics that matter to you",
                            color = DarkTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            items(channels) { channel ->
                ChannelFeedCard(
                    channel = channel,
                    onFollowToggle = { viewModel.toggleFollowChannel(channel.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // FAB to post status
        FloatingActionButton(
            onClick = { showCreateStatusDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp),
            containerColor = EmeraldPrimary,
            contentColor = Color.Black
        ) {
            Icon(Icons.Rounded.CameraAlt, contentDescription = "Add Status")
        }
    }

    if (showCreateStatusDialog) {
        CreateStatusDialog(
            onDismiss = { showCreateStatusDialog = false },
            onPostText = { text, colorHex ->
                showCreateStatusDialog = false
                viewModel.postTextStatus(text, colorHex)
            },
            onPostPhoto = { caption ->
                showCreateStatusDialog = false
                viewModel.postPhotoStatus(caption)
            }
        )
    }
}

@Composable
fun ChannelFeedCard(
    channel: Channel,
    onFollowToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Channel Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(name = channel.name, colorHex = channel.iconColorHex, size = 44.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = channel.name,
                            color = DarkTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        if (channel.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Rounded.Verified,
                                contentDescription = "Verified",
                                tint = CyanAccent,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = "${channel.followersCount / 1000}K followers • @${channel.username}",
                        color = DarkTextSecondary,
                        fontSize = 11.sp
                    )
                }
                Button(
                    onClick = onFollowToggle,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (channel.isFollowed) DarkSurfaceVariant else EmeraldPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = if (channel.isFollowed) "Following" else "Follow",
                        color = if (channel.isFollowed) DarkTextPrimary else Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = channel.description,
                color = DarkTextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            // Latest Post Preview if available
            channel.posts.firstOrNull()?.let { post ->
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = DarkSurfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = post.content,
                            color = DarkTextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                post.reactionsCount.forEach { (emoji, count) ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = DarkSurfaceElevated
                                    ) {
                                        Text(
                                            text = "$emoji $count",
                                            fontSize = 11.sp,
                                            color = DarkTextSecondary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "👁️ ${post.viewsCount}",
                                color = DarkTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateStatusDialog(
    onDismiss: () -> Unit,
    onPostText: (String, Long) -> Unit,
    onPostPhoto: (String) -> Unit
) {
    var statusText by remember { mutableStateOf("") }
    var selectedColorHex by remember { mutableStateOf(0xFF005C4B) }
    var isPhotoTab by remember { mutableStateOf(false) }

    val colorOptions = listOf(0xFF005C4B, 0xFF4361EE, 0xFF7209B7, 0xFFF72585, 0xFFFFB703)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isPhotoTab) "Share Photo Story" else "Share Text Story (24h)", color = DarkTextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterChip(
                        selected = !isPhotoTab,
                        onClick = { isPhotoTab = false },
                        label = { Text("Text Status") }
                    )
                    FilterChip(
                        selected = isPhotoTab,
                        onClick = { isPhotoTab = true },
                        label = { Text("Photo Status") }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (!isPhotoTab) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(selectedColorHex))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        OutlinedTextField(
                            value = statusText,
                            onValueChange = { statusText = it },
                            placeholder = { Text("Type a status...", color = Color.White.copy(alpha = 0.7f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Select Background Color:", color = DarkTextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        colorOptions.forEach { hex ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(hex))
                                    .clickable { selectedColorHex = hex }
                                    .border(
                                        width = if (selectedColorHex == hex) 2.dp else 0.dp,
                                        color = Color.White,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.CameraAlt, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(48.dp))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = statusText,
                            onValueChange = { statusText = it },
                            label = { Text("Add a caption...", color = DarkTextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary, focusedTextColor = DarkTextPrimary, unfocusedTextColor = DarkTextPrimary)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isPhotoTab) {
                        onPostPhoto(statusText)
                    } else if (statusText.isNotBlank()) {
                        onPostText(statusText, selectedColorHex)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Share", color = Color.Black)
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
