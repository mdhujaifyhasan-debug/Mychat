package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Conversation
import com.example.model.MessageType
import com.example.ui.components.MessageStatusIndicator
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*
import com.example.viewmodel.PulseViewModel
import com.example.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatsTab(
    viewModel: PulseViewModel,
    modifier: Modifier = Modifier
) {
    val conversations by viewModel.conversations.collectAsState()
    val filterIndex by viewModel.chatFilterIndex.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showNewChatDialog by remember { mutableStateOf(false) }
    var selectedConversationForOptions by remember { mutableStateOf<Conversation?>(null) }

    val filteredList = remember(conversations, filterIndex, searchQuery) {
        conversations.filter { conv ->
            val matchesFilter = when (filterIndex) {
                1 -> conv.unreadCount > 0
                2 -> conv.isPinned
                3 -> conv.isGroup
                else -> !conv.isArchived
            }
            val matchesSearch = searchQuery.isBlank() ||
                    conv.title.contains(searchQuery, ignoreCase = true) ||
                    (conv.lastMessage?.content?.contains(searchQuery, ignoreCase = true) == true)
            matchesFilter && matchesSearch
        }.sortedWith(compareByDescending<Conversation> { it.isPinned }.thenByDescending { it.lastMessage?.timestamp ?: 0L })
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Filter Chips Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf("All", "Unread", "Favorites", "Groups")
                items(filters.size) { idx ->
                    val isSelected = filterIndex == idx
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setChatFilter(idx) },
                        label = {
                            Text(
                                text = filters[idx],
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.Black else DarkTextPrimary
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            containerColor = DarkSurfaceVariant
                        ),
                        border = null,
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            // Archived Shortcut
            val archivedCount = conversations.count { it.isArchived }
            if (archivedCount > 0 && filterIndex == 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setChatFilter(4) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Archive,
                        contentDescription = "Archived",
                        tint = DarkTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Archived",
                        color = DarkTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "$archivedCount",
                        color = EmeraldPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                HorizontalDivider(color = DarkDivider, thickness = 0.5.dp)
            }

            // Chat List
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Rounded.ChatBubbleOutline,
                            contentDescription = null,
                            tint = DarkTextSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No conversations found",
                            color = DarkTextSecondary,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { showNewChatDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Text("Start a New Chat", color = Color.Black)
                        }
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredList, key = { it.id }) { conv ->
                        ConversationItem(
                            conversation = conv,
                            onClick = { viewModel.openChat(conv.id) },
                            onLongClick = { selectedConversationForOptions = conv }
                        )
                        HorizontalDivider(
                            color = DarkDivider.copy(alpha = 0.4f),
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(start = 72.dp)
                        )
                    }
                }
            }
        }

        // Floating Action Button for New Chat / Group
        FloatingActionButton(
            onClick = { showNewChatDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
                .testTag("new_chat_fab"),
            containerColor = EmeraldPrimary,
            contentColor = Color.Black,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Rounded.Message, contentDescription = "New Chat")
        }
    }

    // New Chat / Contact Dialog
    if (showNewChatDialog) {
        NewChatBottomSheet(
            viewModel = viewModel,
            onDismiss = { showNewChatDialog = false },
            onSelectContact = { contact ->
                showNewChatDialog = false
                // Check if existing conv or create new
                val existing = conversations.find { it.participantIds.contains(contact.id) && !it.isGroup }
                if (existing != null) {
                    viewModel.openChat(existing.id)
                } else {
                    val id = "conv_${contact.id}"
                    viewModel.sendMessage(id, "Hello ${contact.name}! 👋")
                    viewModel.openChat(id)
                }
            },
            onCreateGroupClick = {
                showNewChatDialog = false
                viewModel.navigateTo(Screen.CreateGroup)
            }
        )
    }

    // Long Press Options Dialog
    selectedConversationForOptions?.let { conv ->
        AlertDialog(
            onDismissRequest = { selectedConversationForOptions = null },
            title = { Text(conv.title, color = DarkTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.togglePinConversation(conv.id)
                                selectedConversationForOptions = null
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(if (conv.isPinned) Icons.Rounded.PushPin else Icons.Rounded.PushPin, contentDescription = null, tint = EmeraldPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(if (conv.isPinned) "Unpin Chat" else "Pin to Top", color = DarkTextPrimary)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.toggleMuteConversation(conv.id)
                                selectedConversationForOptions = null
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(if (conv.isMuted) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeOff, contentDescription = null, tint = DarkTextSecondary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(if (conv.isMuted) "Unmute Notifications" else "Mute Notifications", color = DarkTextPrimary)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.toggleArchiveConversation(conv.id)
                                selectedConversationForOptions = null
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Archive, contentDescription = null, tint = DarkTextSecondary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(if (conv.isArchived) "Unarchive" else "Archive Chat", color = DarkTextPrimary)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.toggleLockConversation(conv.id)
                                selectedConversationForOptions = null
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Lock, contentDescription = null, tint = SafetyGold)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(if (conv.isLocked) "Unlock Chat" else "Lock Chat with Fingerprint", color = DarkTextPrimary)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedConversationForOptions = null }) {
                    Text("Close", color = EmeraldPrimary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}

@Composable
fun ConversationItem(
    conversation: Conversation,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(
            name = conversation.title,
            colorHex = conversation.avatarColorHex,
            size = 52.dp,
            isOnline = false,
            showOnlineBadge = false
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.title,
                        color = DarkTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (conversation.isLocked) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = "Locked",
                            tint = SafetyGold,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                conversation.lastMessage?.let { lastMsg ->
                    val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(lastMsg.timestamp))
                    Text(
                        text = timeStr,
                        color = if (conversation.unreadCount > 0) EmeraldPrimary else DarkTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    conversation.lastMessage?.let { lastMsg ->
                        if (lastMsg.senderId == "user_me") {
                            MessageStatusIndicator(lastMsg.status)
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        val snippet = when (lastMsg.type) {
                            MessageType.AUDIO -> "🎙️ Voice message (${lastMsg.audioDurationSec}s)"
                            MessageType.IMAGE -> "📷 Photo"
                            MessageType.DOCUMENT -> "📄 ${lastMsg.fileName ?: "Document"}"
                            MessageType.LOCATION -> "📍 Location"
                            MessageType.POLL -> "📊 Poll: ${lastMsg.pollQuestion}"
                            else -> lastMsg.content
                        }

                        Text(
                            text = snippet,
                            color = DarkTextSecondary,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } ?: Text(
                        text = "No messages yet",
                        color = DarkTextSecondary,
                        fontSize = 14.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (conversation.isMuted) {
                        Icon(
                            imageVector = Icons.Rounded.VolumeOff,
                            contentDescription = "Muted",
                            tint = DarkTextSecondary,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(end = 4.dp)
                        )
                    }
                    if (conversation.isPinned) {
                        Icon(
                            imageVector = Icons.Rounded.PushPin,
                            contentDescription = "Pinned",
                            tint = DarkTextSecondary,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(end = 4.dp)
                        )
                    }
                    if (conversation.unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${conversation.unreadCount}",
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewChatBottomSheet(
    viewModel: PulseViewModel,
    onDismiss: () -> Unit,
    onSelectContact: (com.example.model.User) -> Unit,
    onCreateGroupClick: () -> Unit
) {
    val contacts by viewModel.contacts.collectAsState()
    var searchContactText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceElevated
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Select Contact",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkTextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchContactText,
                onValueChange = { searchContactText = it },
                placeholder = { Text("Search name or phone number", color = DarkTextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = DarkTextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldPrimary,
                    unfocusedBorderColor = DarkDivider,
                    focusedTextColor = DarkTextPrimary,
                    unfocusedTextColor = DarkTextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Actions: New Group, New Community
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCreateGroupClick() }
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(EmeraldPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.GroupAdd, contentDescription = null, tint = Color.Black)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text("New Group", color = DarkTextPrimary, fontWeight = FontWeight.Bold)
                    Text("Up to 1,024 members with custom admins", color = DarkTextSecondary, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = DarkDivider, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Contacts on Pulse", color = DarkTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            val filtered = contacts.filter {
                it.name.contains(searchContactText, ignoreCase = true) ||
                        it.phone.contains(searchContactText)
            }

            LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp)) {
                items(filtered) { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectContact(user) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UserAvatar(
                            name = user.name,
                            colorHex = user.avatarColorHex,
                            size = 42.dp,
                            isOnline = user.isOnline,
                            showOnlineBadge = true
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(user.name, color = DarkTextPrimary, fontWeight = FontWeight.SemiBold)
                            Text(user.statusText, color = DarkTextSecondary, fontSize = 12.sp, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}
