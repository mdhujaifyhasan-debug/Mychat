package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Conversation
import com.example.model.Message
import com.example.model.MessageType
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.PulseViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    conversationId: String,
    viewModel: PulseViewModel,
    modifier: Modifier = Modifier
) {
    val conversations by viewModel.conversations.collectAsState()
    val allMessages by viewModel.messages.collectAsState()
    val conversation = conversations.find { it.id == conversationId } ?: Conversation(
        id = conversationId,
        title = "Chat",
        participantIds = listOf("user_me")
    )
    val messages = allMessages[conversationId] ?: emptyList()

    val playingAudioId by viewModel.playingAudioMessageId.collectAsState()
    val audioProgress by viewModel.audioProgress.collectAsState()
    val audioSpeed by viewModel.audioPlaybackSpeed.collectAsState()
    val isRecordingVoice by viewModel.isRecordingVoice.collectAsState()
    val recordDurationSec by viewModel.recordDurationSec.collectAsState()
    val replyingTo by viewModel.replyingToMessage.collectAsState()
    val editingMsg by viewModel.editingMessage.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var isViewOnceSelected by remember { mutableStateOf(false) }
    var showAttachmentSheet by remember { mutableStateOf(false) }
    var showPollCreator by remember { mutableStateOf(false) }
    var selectedMessageForMenu by remember { mutableStateOf<Message?>(null) }
    var translationMessageText by remember { mutableStateOf<String?>(null) }
    var showWallpaperDialog by remember { mutableStateOf(false) }
    var showChatInfoDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    // Sync input text when editing message
    LaunchedEffect(editingMsg) {
        if (editingMsg != null) {
            inputText = editingMsg!!.content
        }
    }

    // Auto scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    BackHandler {
        viewModel.cancelReplyingOrEditing()
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showChatInfoDialog = true }
                    ) {
                        UserAvatar(
                            name = conversation.title,
                            colorHex = conversation.avatarColorHex,
                            size = 38.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = conversation.title,
                                color = DarkTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (conversation.isGroup) "${conversation.participantIds.size} members" else "Online • E2E Encrypted",
                                color = EmeraldPrimary,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.cancelReplyingOrEditing()
                        viewModel.navigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = DarkTextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.startCall(
                            peerId = conversation.participantIds.firstOrNull { it != "user_me" } ?: "peer",
                            peerName = conversation.title,
                            peerAvatarHex = conversation.avatarColorHex,
                            isVideo = true
                        )
                    }) {
                        Icon(Icons.Rounded.Videocam, contentDescription = "Video Call", tint = DarkTextPrimary)
                    }
                    IconButton(onClick = {
                        viewModel.startCall(
                            peerId = conversation.participantIds.firstOrNull { it != "user_me" } ?: "peer",
                            peerName = conversation.title,
                            peerAvatarHex = conversation.avatarColorHex,
                            isVideo = false
                        )
                    }) {
                        Icon(Icons.Rounded.Call, contentDescription = "Voice Call", tint = DarkTextPrimary)
                    }
                    IconButton(onClick = { showChatInfoDialog = true }) {
                        Icon(Icons.Rounded.MoreVert, contentDescription = "Chat Options", tint = DarkTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurfaceElevated
                )
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Pinned Message Banner if available
            val pinnedMessage = messages.findLast { it.isPinned }
            if (pinnedMessage != null) {
                Surface(
                    color = DarkSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.PushPin, contentDescription = "Pinned", tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Pinned Message", fontSize = 11.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                            Text(pinnedMessage.content, fontSize = 12.sp, color = DarkTextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        IconButton(
                            onClick = { viewModel.togglePinMessage(conversationId, pinnedMessage.id) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Rounded.Close, contentDescription = "Unpin", tint = DarkTextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Message Stream List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                state = listState
            ) {
                item {
                    E2EESecurityBadge()
                }

                items(messages, key = { it.id }) { msg ->
                    val isMe = msg.senderId == "user_me"
                    ChatBubble(
                        message = msg,
                        isMe = isMe,
                        isPlayingAudio = playingAudioId == msg.id,
                        audioProgress = if (playingAudioId == msg.id) audioProgress else 0f,
                        audioSpeed = audioSpeed,
                        onAudioPlayPause = { viewModel.togglePlayAudio(msg.id, msg.audioDurationSec) },
                        onAudioSpeedClick = { viewModel.cyclePlaybackSpeed() },
                        onLongClick = { selectedMessageForMenu = msg },
                        onVotePoll = { optionId -> viewModel.votePoll(conversationId, msg.id, optionId) },
                        onOpenViewOnce = { viewModel.openViewOnce(conversationId, msg.id) }
                    )
                }
            }

            // Reply or Edit Preview Bar
            AnimatedVisibility(visible = replyingTo != null || editingMsg != null) {
                Surface(
                    color = DarkSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(36.dp)
                                .background(EmeraldPrimary, RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (editingMsg != null) "Editing Message" else "Replying to ${replyingTo?.senderName}",
                                fontSize = 12.sp,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = editingMsg?.content ?: replyingTo?.content ?: "",
                                fontSize = 12.sp,
                                color = DarkTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(onClick = {
                            viewModel.cancelReplyingOrEditing()
                            inputText = ""
                        }) {
                            Icon(Icons.Rounded.Close, contentDescription = "Cancel", tint = DarkTextSecondary)
                        }
                    }
                }
            }

            // Voice Recording Bar or Standard Input Row
            if (isRecordingVoice) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurfaceElevated)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(DangerRed)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Recording 0:%02d".format(recordDurationSec),
                        color = DangerRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { viewModel.cancelVoiceRecording() }) {
                        Text("Cancel", color = DarkTextSecondary)
                    }
                    IconButton(
                        onClick = { viewModel.sendVoiceRecording(conversationId) },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary)
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = "Send Voice", tint = Color.Black)
                    }
                }
            } else {
                // Regular Input Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurfaceElevated)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showAttachmentSheet = true }) {
                        Icon(Icons.Rounded.AttachFile, contentDescription = "Attachment", tint = DarkTextSecondary)
                    }

                    // View Once Toggle Icon
                    IconButton(
                        onClick = { isViewOnceSelected = !isViewOnceSelected },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isViewOnceSelected) EmeraldPrimary else DarkSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "1",
                                color = if (isViewOnceSelected) Color.Black else DarkTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Message", color = DarkTextSecondary) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant,
                            focusedTextColor = DarkTextPrimary,
                            unfocusedTextColor = DarkTextPrimary
                        ),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 4
                    )

                    if (inputText.isNotBlank()) {
                        IconButton(
                            onClick = {
                                viewModel.sendTextMessage(
                                    conversationId = conversationId,
                                    text = inputText.trim(),
                                    isViewOnce = isViewOnceSelected
                                )
                                inputText = ""
                                isViewOnceSelected = false
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary)
                                .testTag("send_message_button")
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = "Send", tint = Color.Black)
                        }
                    } else {
                        IconButton(
                            onClick = { viewModel.startVoiceRecording() },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary)
                                .testTag("mic_button")
                        ) {
                            Icon(Icons.Rounded.Mic, contentDescription = "Record Voice", tint = Color.Black)
                        }
                    }
                }
            }
        }
    }

    // Attachment Sheet
    if (showAttachmentSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAttachmentSheet = false },
            containerColor = DarkSurfaceElevated
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text("Share Content", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkTextPrimary)
                Spacer(modifier = Modifier.height(16.dp))

                val items = listOf(
                    Triple(Icons.Rounded.CameraAlt, "Camera", 0xFFE040FB),
                    Triple(Icons.Rounded.Image, "Gallery", 0xFF7C4DFF),
                    Triple(Icons.Rounded.Description, "Document", 0xFF536DFE),
                    Triple(Icons.Rounded.LocationOn, "Location", 0xFF00E676),
                    Triple(Icons.Rounded.Person, "Contact", 0xFF00B0FF),
                    Triple(Icons.Rounded.Poll, "Poll", 0xFFFFD600)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    items.take(3).forEach { (icon, label, color) ->
                        AttachmentIcon(icon, label, color) {
                            showAttachmentSheet = false
                            when (label) {
                                "Camera" -> viewModel.sendMockPhoto(conversationId, "Photo taken via Camera 📸")
                                "Gallery" -> viewModel.sendMockPhoto(conversationId, "Shared image from Gallery 🖼️")
                                "Document" -> viewModel.sendMockDocument(conversationId, "Pulse_Whitepaper_2026.pdf")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    items.drop(3).forEach { (icon, label, color) ->
                        AttachmentIcon(icon, label, color) {
                            showAttachmentSheet = false
                            when (label) {
                                "Location" -> viewModel.sendMockLocation(conversationId, "Pulse HQ, Market St, SF")
                                "Contact" -> viewModel.sendMockContact(conversationId, "Liam Vance", "+1 555-0188")
                                "Poll" -> showPollCreator = true
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Poll Creator Dialog
    if (showPollCreator) {
        CreatePollDialog(
            onDismiss = { showPollCreator = false },
            onCreate = { question, options ->
                showPollCreator = false
                viewModel.createPoll(conversationId, question, options)
            }
        )
    }

    // Message Long Press Action Sheet
    selectedMessageForMenu?.let { msg ->
        ModalBottomSheet(
            onDismissRequest = { selectedMessageForMenu = null },
            containerColor = DarkSurfaceElevated
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Reaction Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val emojis = listOf("❤️", "👍", "😂", "🔥", "😢", "😮")
                    emojis.forEach { emoji ->
                        Text(
                            text = emoji,
                            fontSize = 28.sp,
                            modifier = Modifier
                                .clickable {
                                    viewModel.addReaction(conversationId, msg.id, emoji)
                                    selectedMessageForMenu = null
                                }
                                .padding(4.dp)
                        )
                    }
                }

                HorizontalDivider(color = DarkDivider, thickness = 0.5.dp)

                // Actions
                ListItem(
                    headlineContent = { Text("Reply", color = DarkTextPrimary) },
                    leadingContent = { Icon(Icons.Rounded.Reply, contentDescription = null, tint = DarkTextPrimary) },
                    modifier = Modifier.clickable {
                        viewModel.setReplyingTo(msg)
                        selectedMessageForMenu = null
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )

                ListItem(
                    headlineContent = { Text("Copy", color = DarkTextPrimary) },
                    leadingContent = { Icon(Icons.Rounded.ContentCopy, contentDescription = null, tint = DarkTextPrimary) },
                    modifier = Modifier.clickable {
                        clipboardManager.setText(AnnotatedString(msg.content))
                        selectedMessageForMenu = null
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )

                ListItem(
                    headlineContent = { Text(if (msg.isPinned) "Unpin Message" else "Pin Message", color = DarkTextPrimary) },
                    leadingContent = { Icon(Icons.Rounded.PushPin, contentDescription = null, tint = EmeraldPrimary) },
                    modifier = Modifier.clickable {
                        viewModel.togglePinMessage(conversationId, msg.id)
                        selectedMessageForMenu = null
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )

                ListItem(
                    headlineContent = { Text(if (msg.isStarred) "Unstar" else "Star Message", color = DarkTextPrimary) },
                    leadingContent = { Icon(Icons.Rounded.Star, contentDescription = null, tint = SafetyGold) },
                    modifier = Modifier.clickable {
                        viewModel.toggleStarMessage(conversationId, msg.id)
                        selectedMessageForMenu = null
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )

                ListItem(
                    headlineContent = { Text("Translate Message", color = DarkTextPrimary) },
                    leadingContent = { Icon(Icons.Rounded.Translate, contentDescription = null, tint = CyanAccent) },
                    modifier = Modifier.clickable {
                        // Simulated translation to Bangla & Spanish
                        translationMessageText = "Bangla: এই মেসেজটি পালস এনক্রিপশনের মাধ্যমে সুরক্ষিত।\nSpanish: Este mensaje está protegido por el cifrado de Pulse."
                        selectedMessageForMenu = null
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )

                if (msg.senderId == "user_me") {
                    ListItem(
                        headlineContent = { Text("Edit Message", color = DarkTextPrimary) },
                        leadingContent = { Icon(Icons.Rounded.Edit, contentDescription = null, tint = EmeraldPrimary) },
                        modifier = Modifier.clickable {
                            viewModel.setEditing(msg)
                            selectedMessageForMenu = null
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )

                    ListItem(
                        headlineContent = { Text("Delete for Everyone", color = DangerRed) },
                        leadingContent = { Icon(Icons.Rounded.DeleteForever, contentDescription = null, tint = DangerRed) },
                        modifier = Modifier.clickable {
                            viewModel.deleteMessage(conversationId, msg.id, forEveryone = true)
                            selectedMessageForMenu = null
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }

                ListItem(
                    headlineContent = { Text("Delete for Me", color = DarkTextSecondary) },
                    leadingContent = { Icon(Icons.Rounded.Delete, contentDescription = null, tint = DarkTextSecondary) },
                    modifier = Modifier.clickable {
                        viewModel.deleteMessage(conversationId, msg.id, forEveryone = false)
                        selectedMessageForMenu = null
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
    }

    // Translation Result Dialog
    translationMessageText?.let { trText ->
        AlertDialog(
            onDismissRequest = { translationMessageText = null },
            icon = { Icon(Icons.Rounded.Translate, contentDescription = null, tint = CyanAccent) },
            title = { Text("Instant Message Translation", color = DarkTextPrimary) },
            text = { Text(trText, color = DarkTextSecondary, fontSize = 14.sp) },
            confirmButton = {
                TextButton(onClick = { translationMessageText = null }) {
                    Text("OK", color = EmeraldPrimary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }

    // Chat Info Dialog
    if (showChatInfoDialog) {
        AlertDialog(
            onDismissRequest = { showChatInfoDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    UserAvatar(name = conversation.title, colorHex = conversation.avatarColorHex, size = 40.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(conversation.title, color = DarkTextPrimary)
                }
            },
            text = {
                Column {
                    Text("Encryption: Signal Protocol E2EE (256-bit)", color = EmeraldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Disappearing Messages: Off (Can toggle 24h / 7d / 90d)", color = DarkTextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Custom Wallpaper: Dark Emerald Default", color = DarkTextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.toggleLockConversation(conversationId)
                            showChatInfoDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Rounded.Lock, contentDescription = null, tint = SafetyGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (conversation.isLocked) "Remove Chat Lock" else "Lock Chat with Biometrics", color = DarkTextPrimary)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showChatInfoDialog = false }) {
                    Text("Close", color = EmeraldPrimary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}

@Composable
fun AttachmentIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, colorHex: Long, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(Color(colorHex)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, color = DarkTextPrimary, fontSize = 12.sp)
    }
}

@Composable
fun ChatBubble(
    message: Message,
    isMe: Boolean,
    isPlayingAudio: Boolean,
    audioProgress: Float,
    audioSpeed: Float,
    onAudioPlayPause: () -> Unit,
    onAudioSpeedClick: () -> Unit,
    onLongClick: () -> Unit,
    onVotePoll: (String) -> Unit,
    onOpenViewOnce: () -> Unit
) {
    val bubbleColor = if (isMe) DarkOutgoingBubble else DarkIncomingBubble
    val align = if (isMe) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalAlignment = align
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            color = bubbleColor,
            modifier = Modifier
                .widthIn(min = 60.dp, max = 320.dp)
                .clickable { onLongClick() }
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                // Reply Quote Preview
                if (message.replyToContent != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Row(modifier = Modifier.padding(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(28.dp)
                                    .background(EmeraldPrimary, RoundedCornerShape(1.dp))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = message.replyToContent,
                                fontSize = 11.sp,
                                color = DarkTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Sender Name in Groups if incoming
                if (!isMe) {
                    Text(
                        text = message.senderName,
                        color = EmeraldPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // View Once Media
                if (message.isViewOnce) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenViewOnce() }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (message.isViewOnceOpened) DarkSurfaceVariant else EmeraldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("1", color = if (message.isViewOnceOpened) DarkTextSecondary else Color.Black, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (message.isViewOnceOpened) "Photo Opened" else "Photo (View Once)",
                            color = if (message.isViewOnceOpened) DarkTextSecondary else DarkTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    // Content by Type
                    when (message.type) {
                        MessageType.TEXT -> {
                            Text(
                                text = message.content,
                                color = DarkTextPrimary,
                                fontSize = 15.sp,
                                lineHeight = 20.sp
                            )
                        }
                        MessageType.AUDIO -> {
                            AudioWaveformPlayer(
                                durationSec = message.audioDurationSec,
                                isPlaying = isPlayingAudio,
                                progress = audioProgress,
                                speed = audioSpeed,
                                onPlayPause = onAudioPlayPause,
                                onSpeedClick = onAudioSpeedClick,
                                isOutgoing = isMe
                            )
                        }
                        MessageType.IMAGE -> {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(DarkSurfaceElevated),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Rounded.Image, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(48.dp))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Encrypted Media Preview", color = DarkTextSecondary, fontSize = 12.sp)
                                    }
                                }
                                if (!message.mediaCaption.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(message.mediaCaption, color = DarkTextPrimary, fontSize = 14.sp)
                                }
                            }
                        }
                        MessageType.DOCUMENT -> {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Rounded.PictureAsPdf, contentDescription = null, tint = DangerRed, modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(message.fileName ?: "Document.pdf", color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(message.fileSize ?: "2.1 MB", color = DarkTextSecondary, fontSize = 11.sp)
                                    }
                                    Icon(Icons.Rounded.Download, contentDescription = null, tint = EmeraldPrimary)
                                }
                            }
                        }
                        MessageType.LOCATION -> {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = DangerRed, modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Live Location Pin", color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(message.locationLabel ?: "Shared Coordinates", color = DarkTextSecondary, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                        MessageType.CONTACT -> {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Rounded.AccountCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(message.contactName ?: "Contact", color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(message.contactPhone ?: "", color = DarkTextSecondary, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                        MessageType.POLL -> {
                            PollCardView(
                                question = message.pollQuestion ?: "Poll",
                                options = message.pollOptions,
                                currentUserId = "user_me",
                                onVote = onVotePoll
                            )
                        }
                        else -> {
                            Text(message.content, color = DarkTextPrimary)
                        }
                    }
                }

                // Bubble Footer: Time, Edited label, Pin, Star, Ticks
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (message.isEdited) {
                        Text("Edited • ", fontSize = 10.sp, color = DarkTextSecondary)
                    }
                    if (message.isPinned) {
                        Icon(Icons.Rounded.PushPin, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                    if (message.isStarred) {
                        Icon(Icons.Rounded.Star, contentDescription = null, tint = SafetyGold, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                    }

                    val timeFormatted = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp))
                    Text(
                        text = timeFormatted,
                        fontSize = 10.sp,
                        color = DarkTextSecondary
                    )

                    if (isMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        MessageStatusIndicator(message.status)
                    }
                }
            }
        }

        // Reactions floating below the bubble
        if (message.reactions.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurfaceElevated,
                tonalElevation = 2.dp,
                modifier = Modifier
                    .offset(y = (-6).dp)
                    .padding(horizontal = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val uniqueReactions = message.reactions.values.distinct()
                    uniqueReactions.forEach { em ->
                        Text(em, fontSize = 12.sp)
                    }
                    if (message.reactions.size > 1) {
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("${message.reactions.size}", fontSize = 10.sp, color = DarkTextSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CreatePollDialog(
    onDismiss: () -> Unit,
    onCreate: (String, List<String>) -> Unit
) {
    var question by remember { mutableStateOf("") }
    var opt1 by remember { mutableStateOf("") }
    var opt2 by remember { mutableStateOf("") }
    var opt3 by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Poll", color = DarkTextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text("Ask a question", color = DarkTextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary, focusedTextColor = DarkTextPrimary, unfocusedTextColor = DarkTextPrimary)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = opt1,
                    onValueChange = { opt1 = it },
                    label = { Text("Option 1", color = DarkTextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary, focusedTextColor = DarkTextPrimary, unfocusedTextColor = DarkTextPrimary)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = opt2,
                    onValueChange = { opt2 = it },
                    label = { Text("Option 2", color = DarkTextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary, focusedTextColor = DarkTextPrimary, unfocusedTextColor = DarkTextPrimary)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = opt3,
                    onValueChange = { opt3 = it },
                    label = { Text("Option 3 (Optional)", color = DarkTextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary, focusedTextColor = DarkTextPrimary, unfocusedTextColor = DarkTextPrimary)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (question.isNotBlank() && opt1.isNotBlank() && opt2.isNotBlank()) {
                        val opts = listOfNotNull(opt1, opt2, opt3.takeIf { it.isNotBlank() })
                        onCreate(question, opts)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Create", color = Color.Black)
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
