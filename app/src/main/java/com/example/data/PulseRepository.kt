package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PulseRepository {

    // Current User
    private val _currentUser = MutableStateFlow(
        User(
            id = "user_me",
            name = "Alex Mercer",
            username = "alex_pulse",
            phone = "+1 555 019 2831",
            email = "alex.mercer@pulse.im",
            avatarColorHex = 0xFF00A884,
            statusText = "Building secure communication for everyone 🚀",
            isOnline = true,
            isVerified = true,
            isBusiness = false,
            country = "United States",
            countryCode = "+1"
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // Auth State
    private val _isAuthenticated = MutableStateFlow(true)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _isTwoStepLocked = MutableStateFlow(false)
    val isTwoStepLocked: StateFlow<Boolean> = _isTwoStepLocked.asStateFlow()

    private val _isAppLocked = MutableStateFlow(false)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    private val _appPin = MutableStateFlow("1234")
    val appPin: StateFlow<String> = _appPin.asStateFlow()

    private val _twoStepPin = MutableStateFlow("4321")
    val twoStepPin: StateFlow<String> = _twoStepPin.asStateFlow()

    private val _isTwoStepEnabled = MutableStateFlow(true)
    val isTwoStepEnabled: StateFlow<Boolean> = _isTwoStepEnabled.asStateFlow()

    private val _isBiometricEnabled = MutableStateFlow(true)
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()

    // Devices
    private val _devices = MutableStateFlow(
        listOf(
            DeviceSession("d1", "Pixel 8 Pro (This Phone)", "Android 15", "San Francisco, USA", "Active now", isCurrent = true),
            DeviceSession("d2", "Pulse Web Desktop", "Chrome on macOS", "San Francisco, USA", "Active 2h ago"),
            DeviceSession("d3", "Pulse Tablet Pro", "Android 14", "New York, USA", "Active yesterday")
        )
    )
    val devices: StateFlow<List<DeviceSession>> = _devices.asStateFlow()

    // Contacts
    private val _contacts = MutableStateFlow(
        listOf(
            User("u1", "Sophia Rivera", "sophia_r", "+1 555 014 9821", "sophia@example.com", avatarColorHex = 0xFF4361EE, statusText = "Coffee & Code ☕", isOnline = true),
            User("u2", "Liam Vance", "liam_v", "+1 555 018 7632", "liam@example.com", avatarColorHex = 0xFF7209B7, statusText = "At the gym 🏋️", isOnline = false, lastSeen = "15m ago"),
            User("u3", "Emma Watson", "emma_w", "+1 555 019 3321", "emma@example.com", avatarColorHex = 0xFFF72585, statusText = "Busy in meetings 📊", isOnline = true),
            User("u4", "Noah Patel", "noah_p", "+1 555 011 4455", "noah@example.com", avatarColorHex = 0xFF4CC9F0, statusText = "Available", isOnline = false, lastSeen = "1h ago"),
            User("u5", "Olivia Chen", "olivia_c", "+1 555 016 8899", "olivia@example.com", avatarColorHex = 0xFFFFB703, statusText = "Working remotely 🌐", isOnline = true),
            User("u6", "James Wilson", "james_w", "+1 555 017 1122", "james@example.com", avatarColorHex = 0xFF06D6A0, statusText = "Gaming 🎮", isOnline = false, lastSeen = "Yesterday")
        )
    )
    val contacts: StateFlow<List<User>> = _contacts.asStateFlow()

    // Conversations
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    // Messages per conversation: Map<conversationId, List<Message>>
    private val _messages = MutableStateFlow<Map<String, List<Message>>>(emptyMap())
    val messages: StateFlow<Map<String, List<Message>>> = _messages.asStateFlow()

    // Status Stories
    private val _stories = MutableStateFlow<List<StatusStory>>(emptyList())
    val stories: StateFlow<List<StatusStory>> = _stories.asStateFlow()

    // Channels
    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

    // Communities
    private val _communities = MutableStateFlow<List<Community>>(emptyList())
    val communities: StateFlow<List<Community>> = _communities.asStateFlow()

    // Calls
    private val _callRecords = MutableStateFlow<List<CallRecord>>(emptyList())
    val callRecords: StateFlow<List<CallRecord>> = _callRecords.asStateFlow()

    // Active Call
    private val _activeCall = MutableStateFlow(ActiveCallState())
    val activeCall: StateFlow<ActiveCallState> = _activeCall.asStateFlow()

    // Business Profile
    private val _businessProfile = MutableStateFlow(BusinessProfile())
    val businessProfile: StateFlow<BusinessProfile> = _businessProfile.asStateFlow()

    // Admin Stats
    private val _adminStats = MutableStateFlow(AdminStats())
    val adminStats: StateFlow<AdminStats> = _adminStats.asStateFlow()

    // Privacy & Settings toggles
    private val _readReceiptsEnabled = MutableStateFlow(true)
    val readReceiptsEnabled: StateFlow<Boolean> = _readReceiptsEnabled.asStateFlow()

    private val _lastSeenPrivacy = MutableStateFlow("Everyone") // Everyone, My Contacts, Nobody
    val lastSeenPrivacy: StateFlow<String> = _lastSeenPrivacy.asStateFlow()

    private val _profilePhotoPrivacy = MutableStateFlow("Everyone")
    val profilePhotoPrivacy: StateFlow<String> = _profilePhotoPrivacy.asStateFlow()

    private val _chatWallpaperTheme = MutableStateFlow("Default Emerald")
    val chatWallpaperTheme: StateFlow<String> = _chatWallpaperTheme.asStateFlow()

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        // Initial Messages for Sophia
        val c1Id = "conv_sophia"
        val now = System.currentTimeMillis()
        val c1Messages = listOf(
            Message(
                id = "m1",
                conversationId = c1Id,
                senderId = "u1",
                senderName = "Sophia Rivera",
                content = "Hey Alex! Have you reviewed the end-to-end encryption specs for Pulse?",
                timestamp = now - 7200000,
                status = MessageStatus.READ
            ),
            Message(
                id = "m2",
                conversationId = c1Id,
                senderId = "user_me",
                senderName = "Alex Mercer",
                content = "Yes, verified! Double Ratchet with curve25519 is working smoothly on all clients.",
                timestamp = now - 6800000,
                status = MessageStatus.READ,
                reactions = mapOf("u1" to "🔥")
            ),
            Message(
                id = "m3",
                conversationId = c1Id,
                senderId = "u1",
                senderName = "Sophia Rivera",
                content = "Voice note update from design team 🎙️",
                timestamp = now - 3600000,
                status = MessageStatus.READ,
                type = MessageType.AUDIO,
                isVoiceNote = true,
                audioDurationSec = 14
            ),
            Message(
                id = "m4",
                conversationId = c1Id,
                senderId = "user_me",
                senderName = "Alex Mercer",
                content = "Awesome audio quality! Here is the latest preview of the new dashboard mockup:",
                timestamp = now - 1800000,
                status = MessageStatus.READ,
                type = MessageType.IMAGE,
                mediaCaption = "Pulse Dark Theme with Material 3 tokens"
            ),
            Message(
                id = "m5",
                conversationId = c1Id,
                senderId = "u1",
                senderName = "Sophia Rivera",
                content = "Loving the emerald accents! Let's do a quick video call around 4 PM.",
                timestamp = now - 600000,
                status = MessageStatus.READ,
                reactions = mapOf("user_me" to "❤️")
            )
        )

        // Initial Messages for Liam
        val c2Id = "conv_liam"
        val c2Messages = listOf(
            Message(
                id = "m20",
                conversationId = c2Id,
                senderId = "u2",
                senderName = "Liam Vance",
                content = "Here is the architectural overview document for the WebRTC TURN cluster.",
                timestamp = now - 86400000,
                status = MessageStatus.READ,
                type = MessageType.DOCUMENT,
                fileName = "Pulse_Architecture_v2.4.pdf",
                fileSize = "2.4 MB"
            ),
            Message(
                id = "m21",
                conversationId = c2Id,
                senderId = "user_me",
                senderName = "Alex Mercer",
                content = "Thanks Liam! Sending you the data center location pin.",
                timestamp = now - 43200000,
                status = MessageStatus.READ,
                type = MessageType.LOCATION,
                locationLat = 37.7749,
                locationLng = -122.4194,
                locationLabel = "Pulse Supercluster DC-1, San Francisco"
            ),
            Message(
                id = "m22",
                conversationId = c2Id,
                senderId = "u2",
                senderName = "Liam Vance",
                content = "Sounds good! See you there.",
                timestamp = now - 1200000,
                status = MessageStatus.DELIVERED
            )
        )

        // Initial Messages for Group: Android Core Architecture
        val cGroupId = "conv_group_android"
        val cGroupMessages = listOf(
            Message(
                id = "mg1",
                conversationId = cGroupId,
                senderId = "u1",
                senderName = "Sophia Rivera",
                content = "Welcome everyone to Pulse Core Architecture! Let's vote on the audio codec for ultra-low latency.",
                timestamp = now - 14400000,
                status = MessageStatus.READ,
                type = MessageType.POLL,
                pollQuestion = "Which audio codec should we default to on slow 3G/2G connections?",
                pollOptions = listOf(
                    PollOption("p1", "Opus Variable Bitrate (6-24kbps)", listOf("u1", "u2", "user_me")),
                    PollOption("p2", "AMR-WB (Adaptive Multi-Rate)", listOf("u3")),
                    PollOption("p3", "AAC-LD Low Delay", listOf("u5"))
                )
            ),
            Message(
                id = "mg2",
                conversationId = cGroupId,
                senderId = "u3",
                senderName = "Emma Watson",
                content = "Opus is definitely the standard for WebRTC voice frames.",
                timestamp = now - 7200000,
                status = MessageStatus.READ,
                replyToMessageId = "mg1",
                replyToContent = "Which audio codec should we default to..."
            ),
            Message(
                id = "mg3",
                conversationId = cGroupId,
                senderId = "u5",
                senderName = "Olivia Chen",
                content = "Agreed! Pinned this decision for the next sprint.",
                timestamp = now - 900000,
                status = MessageStatus.READ,
                isPinned = true
            )
        )

        // Seed Map
        _messages.value = mapOf(
            c1Id to c1Messages,
            c2Id to c2Messages,
            cGroupId to cGroupMessages
        )

        // Seed Conversations
        _conversations.value = listOf(
            Conversation(
                id = c1Id,
                isGroup = false,
                title = "Sophia Rivera",
                avatarColorHex = 0xFF4361EE,
                participantIds = listOf("user_me", "u1"),
                lastMessage = c1Messages.last(),
                unreadCount = 0,
                isPinned = true
            ),
            Conversation(
                id = cGroupId,
                isGroup = true,
                title = "Pulse Core Architecture ⚡",
                avatarColorHex = 0xFF00A884,
                participantIds = listOf("user_me", "u1", "u2", "u3", "u5"),
                lastMessage = cGroupMessages.last(),
                unreadCount = 2,
                isPinned = true,
                groupAdminIds = listOf("user_me", "u1"),
                groupOwnerId = "user_me",
                groupDescription = "Official design and engineering discussions for Pulse Messenger."
            ),
            Conversation(
                id = c2Id,
                isGroup = false,
                title = "Liam Vance",
                avatarColorHex = 0xFF7209B7,
                participantIds = listOf("user_me", "u2"),
                lastMessage = c2Messages.last(),
                unreadCount = 0
            ),
            Conversation(
                id = "conv_emma",
                isGroup = false,
                title = "Emma Watson",
                avatarColorHex = 0xFFF72585,
                participantIds = listOf("user_me", "u3"),
                lastMessage = Message(
                    id = "me1",
                    conversationId = "conv_emma",
                    senderId = "u3",
                    senderName = "Emma Watson",
                    content = "Don't forget to push the PR today! 💻",
                    timestamp = now - 18000000,
                    status = MessageStatus.READ
                ),
                unreadCount = 1
            ),
            Conversation(
                id = "conv_olivia",
                isGroup = false,
                title = "Olivia Chen",
                avatarColorHex = 0xFFFFB703,
                participantIds = listOf("user_me", "u5"),
                lastMessage = Message(
                    id = "mo1",
                    conversationId = "conv_olivia",
                    senderId = "u5",
                    senderName = "Olivia Chen",
                    content = "Let's sync up after standup.",
                    timestamp = now - 28000000,
                    status = MessageStatus.READ
                ),
                unreadCount = 0
            )
        )

        // Seed Status Stories
        _stories.value = listOf(
            StatusStory(
                id = "s_me",
                userId = "user_me",
                userName = "My Status",
                userAvatarHex = 0xFF00A884,
                type = StoryType.TEXT,
                contentText = "Building the future of privacy on Android 🛡️🚀",
                backgroundColorHex = 0xFF005C4B,
                timestamp = now - 7200000,
                viewerCount = 18,
                viewers = listOf("Sophia Rivera", "Liam Vance", "Emma Watson", "Olivia Chen", "Noah Patel")
            ),
            StatusStory(
                id = "s_u1",
                userId = "u1",
                userName = "Sophia Rivera",
                userAvatarHex = 0xFF4361EE,
                type = StoryType.TEXT,
                contentText = "Golden hour vibes after an intense launch week! 🌅✨",
                backgroundColorHex = 0xFFF72585,
                timestamp = now - 14400000,
                isViewed = false
            ),
            StatusStory(
                id = "s_u2",
                userId = "u2",
                userName = "Liam Vance",
                userAvatarHex = 0xFF7209B7,
                type = StoryType.TEXT,
                contentText = "Server cluster load: 0.12ms ping worldwide! Zero downtime. ⚡",
                backgroundColorHex = 0xFF4361EE,
                timestamp = now - 21600000,
                isViewed = true
            ),
            StatusStory(
                id = "s_u5",
                userId = "u5",
                userName = "Olivia Chen",
                userAvatarHex = 0xFFFFB703,
                type = StoryType.VOICE,
                contentText = "Voice memo from Tokyo conference 🗼🎙️",
                backgroundColorHex = 0xFF06D6A0,
                timestamp = now - 36000000,
                isViewed = false
            )
        )

        // Seed Channels
        _channels.value = listOf(
            Channel(
                id = "ch_official",
                name = "Pulse Messenger News",
                username = "pulsenews",
                description = "Official announcements, new features, security updates, and releases.",
                iconColorHex = 0xFF00A884,
                isVerified = true,
                followersCount = 428000,
                isFollowed = true,
                posts = listOf(
                    ChannelPost(
                        id = "cp1",
                        channelId = "ch_official",
                        content = "🎉 Welcome to Pulse v2.4! We are excited to introduce 24h Status updates, Communities, End-to-End Encrypted Voice/Video calls, and custom Business Catalogs.",
                        timestamp = now - 86400000,
                        viewsCount = 142000,
                        reactionsCount = mapOf("❤️" to 12400, "🔥" to 8900, "👏" to 4300)
                    ),
                    ChannelPost(
                        id = "cp2",
                        channelId = "ch_official",
                        content = "🔒 Privacy Deep Dive: How Pulse verifies safety numbers without transmitting your private keys to central servers.",
                        timestamp = now - 28800000,
                        viewsCount = 98000,
                        reactionsCount = mapOf("🛡️" to 7100, "💡" to 3400)
                    )
                )
            ),
            Channel(
                id = "ch_android",
                name = "Android Developers Community",
                username = "androiddev",
                description = "Daily news, tips, Kotlin 2.0 tricks, and Jetpack Compose best practices.",
                iconColorHex = 0xFF3DDC84,
                isVerified = true,
                followersCount = 189000,
                isFollowed = true,
                posts = listOf(
                    ChannelPost(
                        id = "cp3",
                        channelId = "ch_android",
                        content = "Compose Multiplatform is gaining rapid adoption across mobile and desktop. What is your favorite feature in Compose 2026?",
                        timestamp = now - 18000000,
                        viewsCount = 45000,
                        reactionsCount = mapOf("🚀" to 3200, "👍" to 2100)
                    )
                )
            ),
            Channel(
                id = "ch_tech",
                name = "Tech Wire & AI Frontiers",
                username = "techwire",
                description = "Breaking technology innovations, cryptography, and distributed systems.",
                iconColorHex = 0xFF7209B7,
                isVerified = false,
                followersCount = 76000,
                isFollowed = false,
                posts = listOf(
                    ChannelPost(
                        id = "cp4",
                        channelId = "ch_tech",
                        content = "Decentralized messaging protocols are showing 300% growth in active nodes this quarter.",
                        timestamp = now - 43200000,
                        viewsCount = 22000,
                        reactionsCount = mapOf("🧠" to 1400)
                    )
                )
            )
        )

        // Seed Communities
        _communities.value = listOf(
            Community(
                id = "comm_1",
                name = "Global Android Builders",
                description = "A thriving hub of mobile engineers, UI architects, and open source contributors.",
                iconColorHex = 0xFF00A884,
                memberCount = 1420,
                announcementGroupId = cGroupId,
                subGroupIds = listOf(cGroupId, "conv_emma"),
                rules = listOf(
                    "Be respectful and supportive of fellow developers.",
                    "No promotional spam or unsolicited direct messages.",
                    "Keep code discussions modern (Kotlin, Jetpack Compose, Coroutines)."
                )
            ),
            Community(
                id = "comm_2",
                name = "Pulse Open Security Collective",
                description = "Cryptographers and security researchers reviewing zero-trust protocols.",
                iconColorHex = 0xFF4361EE,
                memberCount = 890,
                announcementGroupId = cGroupId,
                subGroupIds = listOf(c2Id),
                rules = listOf(
                    "Responsible disclosure for all vulnerability research.",
                    "Verify cryptographic claims with source code."
                )
            )
        )

        // Seed Call Records
        _callRecords.value = listOf(
            CallRecord(
                id = "call_1",
                peerId = "u1",
                peerName = "Sophia Rivera",
                peerAvatarHex = 0xFF4361EE,
                isVideo = true,
                isIncoming = true,
                isMissed = false,
                timestamp = now - 3600000,
                durationSeconds = 745
            ),
            CallRecord(
                id = "call_2",
                peerId = "u2",
                peerName = "Liam Vance",
                peerAvatarHex = 0xFF7209B7,
                isVideo = false,
                isIncoming = true,
                isMissed = true,
                timestamp = now - 18000000,
                durationSeconds = 0
            ),
            CallRecord(
                id = "call_3",
                peerId = "u3",
                peerName = "Emma Watson",
                peerAvatarHex = 0xFFF72585,
                isVideo = false,
                isIncoming = false,
                isMissed = false,
                timestamp = now - 43200000,
                durationSeconds = 312
            ),
            CallRecord(
                id = "call_4",
                peerId = "u5",
                peerName = "Olivia Chen",
                peerAvatarHex = 0xFFFFB703,
                isVideo = true,
                isIncoming = false,
                isMissed = false,
                timestamp = now - 86400000,
                durationSeconds = 1205
            )
        )
    }

    // ---------------- AUTH OPERATIONS ----------------
    fun loginWithPhone(phone: String, otp: String): Boolean {
        if (otp == "123456" || otp.length == 6) {
            _isAuthenticated.value = true
            return true
        }
        return false
    }

    fun loginWithEmail(email: String, pass: String): Boolean {
        if (email.contains("@") && pass.isNotEmpty()) {
            _isAuthenticated.value = true
            return true
        }
        return false
    }

    fun loginWithGoogle() {
        _isAuthenticated.value = true
    }

    fun logout() {
        _isAuthenticated.value = false
    }

    fun unlockTwoStep(pin: String): Boolean {
        if (pin == _twoStepPin.value) {
            _isTwoStepLocked.value = false
            return true
        }
        return false
    }

    fun unlockApp(pin: String): Boolean {
        if (pin == _appPin.value) {
            _isAppLocked.value = false
            return true
        }
        return false
    }

    fun toggleAppLock(enabled: Boolean, newPin: String = "1234") {
        _isBiometricEnabled.value = enabled
        _appPin.value = newPin
    }

    fun toggleTwoStepVerification(enabled: Boolean, pin: String = "4321") {
        _isTwoStepEnabled.value = enabled
        _twoStepPin.value = pin
    }

    fun updateProfile(name: String, username: String, statusText: String, phone: String, email: String) {
        _currentUser.update {
            it.copy(
                name = name,
                username = username,
                statusText = statusText,
                phone = phone,
                email = email
            )
        }
    }

    fun removeDevice(deviceId: String) {
        _devices.update { list -> list.filterNot { it.id == deviceId } }
    }

    // ---------------- MESSAGING OPERATIONS ----------------
    fun sendMessage(
        conversationId: String,
        content: String,
        type: MessageType = MessageType.TEXT,
        mediaUrl: String? = null,
        mediaCaption: String? = null,
        fileName: String? = null,
        fileSize: String? = null,
        audioDurationSec: Int = 0,
        isVoiceNote: Boolean = false,
        locationLat: Double? = null,
        locationLng: Double? = null,
        locationLabel: String? = null,
        contactName: String? = null,
        contactPhone: String? = null,
        pollQuestion: String? = null,
        pollOptions: List<PollOption> = emptyList(),
        replyToMessageId: String? = null,
        replyToContent: String? = null,
        isViewOnce: Boolean = false
    ) {
        val user = _currentUser.value
        val newMsg = Message(
            id = "msg_${System.currentTimeMillis()}_${(100..999).random()}",
            conversationId = conversationId,
            senderId = user.id,
            senderName = user.name,
            content = content,
            timestamp = System.currentTimeMillis(),
            status = MessageStatus.READ,
            type = type,
            mediaUrl = mediaUrl,
            mediaCaption = mediaCaption,
            fileName = fileName,
            fileSize = fileSize,
            audioDurationSec = audioDurationSec,
            isVoiceNote = isVoiceNote,
            locationLat = locationLat,
            locationLng = locationLng,
            locationLabel = locationLabel,
            contactName = contactName,
            contactPhone = contactPhone,
            pollQuestion = pollQuestion,
            pollOptions = pollOptions,
            replyToMessageId = replyToMessageId,
            replyToContent = replyToContent,
            isViewOnce = isViewOnce
        )

        // Append message
        _messages.update { currentMap ->
            val list = currentMap[conversationId]?.toMutableList() ?: mutableListOf()
            list.add(newMsg)
            currentMap + (conversationId to list)
        }

        // Update conversation last message & timestamp
        _conversations.update { convList ->
            convList.map { conv ->
                if (conv.id == conversationId) {
                    conv.copy(lastMessage = newMsg)
                } else conv
            }
        }
    }

    fun addReaction(conversationId: String, messageId: String, emoji: String) {
        val user = _currentUser.value
        _messages.update { currentMap ->
            val list = currentMap[conversationId]?.map { msg ->
                if (msg.id == messageId) {
                    val updatedReactions = msg.reactions.toMutableMap()
                    if (updatedReactions[user.id] == emoji) {
                        updatedReactions.remove(user.id)
                    } else {
                        updatedReactions[user.id] = emoji
                    }
                    msg.copy(reactions = updatedReactions)
                } else msg
            } ?: emptyList()
            currentMap + (conversationId to list)
        }
    }

    fun votePoll(conversationId: String, messageId: String, optionId: String) {
        val userId = _currentUser.value.id
        _messages.update { currentMap ->
            val list = currentMap[conversationId]?.map { msg ->
                if (msg.id == messageId && msg.type == MessageType.POLL) {
                    val updatedOptions = msg.pollOptions.map { opt ->
                        if (opt.id == optionId) {
                            val voters = opt.voterIds.toMutableList()
                            if (voters.contains(userId)) voters.remove(userId) else voters.add(userId)
                            opt.copy(voterIds = voters)
                        } else {
                            // If single choice poll, remove user from other options
                            opt.copy(voterIds = opt.voterIds.filterNot { it == userId })
                        }
                    }
                    msg.copy(pollOptions = updatedOptions)
                } else msg
            } ?: emptyList()
            currentMap + (conversationId to list)
        }
    }

    fun editMessage(conversationId: String, messageId: String, newContent: String) {
        _messages.update { currentMap ->
            val list = currentMap[conversationId]?.map { msg ->
                if (msg.id == messageId) {
                    msg.copy(content = newContent, isEdited = true)
                } else msg
            } ?: emptyList()
            currentMap + (conversationId to list)
        }
    }

    fun deleteMessage(conversationId: String, messageId: String, forEveryone: Boolean) {
        _messages.update { currentMap ->
            val list = currentMap[conversationId]?.mapNotNull { msg ->
                if (msg.id == messageId) {
                    if (forEveryone) {
                        msg.copy(
                            content = "🚫 This message was deleted",
                            isDeletedForEveryone = true,
                            type = MessageType.TEXT
                        )
                    } else {
                        null // Delete locally for me
                    }
                } else msg
            } ?: emptyList()
            currentMap + (conversationId to list)
        }
    }

    fun togglePinMessage(conversationId: String, messageId: String) {
        _messages.update { currentMap ->
            val list = currentMap[conversationId]?.map { msg ->
                if (msg.id == messageId) msg.copy(isPinned = !msg.isPinned) else msg
            } ?: emptyList()
            currentMap + (conversationId to list)
        }
    }

    fun toggleStarMessage(conversationId: String, messageId: String) {
        _messages.update { currentMap ->
            val list = currentMap[conversationId]?.map { msg ->
                if (msg.id == messageId) msg.copy(isStarred = !msg.isStarred) else msg
            } ?: emptyList()
            currentMap + (conversationId to list)
        }
    }

    fun openViewOnce(conversationId: String, messageId: String) {
        _messages.update { currentMap ->
            val list = currentMap[conversationId]?.map { msg ->
                if (msg.id == messageId && msg.isViewOnce) {
                    msg.copy(isViewOnceOpened = true)
                } else msg
            } ?: emptyList()
            currentMap + (conversationId to list)
        }
    }

    // ---------------- CONVERSATION SETTINGS ----------------
    fun togglePinConversation(conversationId: String) {
        _conversations.update { list ->
            list.map { if (it.id == conversationId) it.copy(isPinned = !it.isPinned) else it }
        }
    }

    fun toggleArchiveConversation(conversationId: String) {
        _conversations.update { list ->
            list.map { if (it.id == conversationId) it.copy(isArchived = !it.isArchived) else it }
        }
    }

    fun toggleMuteConversation(conversationId: String) {
        _conversations.update { list ->
            list.map { if (it.id == conversationId) it.copy(isMuted = !it.isMuted) else it }
        }
    }

    fun toggleLockConversation(conversationId: String) {
        _conversations.update { list ->
            list.map { if (it.id == conversationId) it.copy(isLocked = !it.isLocked) else it }
        }
    }

    fun createGroup(title: String, description: String, memberIds: List<String>): String {
        val id = "conv_grp_${System.currentTimeMillis()}"
        val user = _currentUser.value
        val allParticipants = (listOf(user.id) + memberIds).distinct()
        val newConv = Conversation(
            id = id,
            isGroup = true,
            title = title,
            avatarColorHex = 0xFF00A884,
            participantIds = allParticipants,
            groupAdminIds = listOf(user.id),
            groupOwnerId = user.id,
            groupDescription = description,
            lastMessage = Message(
                id = "init_$id",
                conversationId = id,
                senderId = user.id,
                senderName = user.name,
                content = "Group created: $title",
                status = MessageStatus.READ
            )
        )
        _conversations.update { listOf(newConv) + it }
        _messages.update { it + (id to listOf(newConv.lastMessage!!)) }
        return id
    }

    // ---------------- STATUS STORIES ----------------
    fun postStory(type: StoryType, text: String? = null, bgHex: Long = 0xFF005C4B, caption: String? = null) {
        val user = _currentUser.value
        val story = StatusStory(
            id = "story_${System.currentTimeMillis()}",
            userId = user.id,
            userName = "My Status",
            userAvatarHex = user.avatarColorHex,
            type = type,
            contentText = text,
            backgroundColorHex = bgHex,
            caption = caption,
            timestamp = System.currentTimeMillis()
        )
        _stories.update { listOf(story) + it.filterNot { s -> s.userId == user.id } }
    }

    fun markStoryViewed(storyId: String) {
        _stories.update { list ->
            list.map { if (it.id == storyId) it.copy(isViewed = true) else it }
        }
    }

    // ---------------- CHANNELS ----------------
    fun toggleFollowChannel(channelId: String) {
        _channels.update { list ->
            list.map { ch ->
                if (ch.id == channelId) {
                    val newFollowed = !ch.isFollowed
                    ch.copy(
                        isFollowed = newFollowed,
                        followersCount = if (newFollowed) ch.followersCount + 1 else ch.followersCount - 1
                    )
                } else ch
            }
        }
    }

    fun postToChannel(channelId: String, content: String) {
        val post = ChannelPost(
            id = "cp_${System.currentTimeMillis()}",
            channelId = channelId,
            content = content,
            timestamp = System.currentTimeMillis(),
            viewsCount = 1
        )
        _channels.update { list ->
            list.map { ch ->
                if (ch.id == channelId) ch.copy(posts = listOf(post) + ch.posts) else ch
            }
        }
    }

    // ---------------- CALLS ----------------
    fun startCall(peerId: String, peerName: String, peerAvatarHex: Long, isVideo: Boolean) {
        _activeCall.value = ActiveCallState(
            isInCall = true,
            isVideo = isVideo,
            peerId = peerId,
            peerName = peerName,
            peerAvatarHex = peerAvatarHex,
            isMuted = false,
            isCameraOn = isVideo,
            isSpeakerOn = isVideo,
            durationSeconds = 0,
            isFrontCamera = true,
            isConnecting = true
        )
    }

    fun endCall() {
        val call = _activeCall.value
        if (call.isInCall) {
            val record = CallRecord(
                id = "call_${System.currentTimeMillis()}",
                peerId = call.peerId,
                peerName = call.peerName,
                peerAvatarHex = call.peerAvatarHex,
                isVideo = call.isVideo,
                isIncoming = false,
                isMissed = false,
                timestamp = System.currentTimeMillis(),
                durationSeconds = call.durationSeconds
            )
            _callRecords.update { listOf(record) + it }
            _activeCall.value = ActiveCallState()
        }
    }

    fun toggleCallMute() {
        _activeCall.update { it.copy(isMuted = !it.isMuted) }
    }

    fun toggleCallCamera() {
        _activeCall.update { it.copy(isCameraOn = !it.isCameraOn) }
    }

    fun toggleCallSpeaker() {
        _activeCall.update { it.copy(isSpeakerOn = !it.isSpeakerOn) }
    }

    fun flipCamera() {
        _activeCall.update { it.copy(isFrontCamera = !it.isFrontCamera) }
    }

    fun tickCallDuration() {
        if (_activeCall.value.isInCall) {
            _activeCall.update { it.copy(durationSeconds = it.durationSeconds + 1, isConnecting = false) }
        }
    }

    // ---------------- BUSINESS PROFILE ----------------
    fun updateBusinessProfile(
        name: String,
        category: String,
        description: String,
        address: String,
        website: String,
        hours: String,
        greeting: String,
        away: String
    ) {
        _businessProfile.update {
            it.copy(
                businessName = name,
                category = category,
                description = description,
                address = address,
                website = website,
                hours = hours,
                greetingMessage = greeting,
                awayMessage = away
            )
        }
    }

    fun addProduct(name: String, price: String, desc: String) {
        val product = ProductItem(
            id = "prod_${System.currentTimeMillis()}",
            name = name,
            price = price,
            description = desc,
            iconHex = 0xFF00A884
        )
        _businessProfile.update {
            it.copy(catalog = it.catalog + product)
        }
    }

    fun deleteProduct(productId: String) {
        _businessProfile.update {
            it.copy(catalog = it.catalog.filterNot { p -> p.id == productId })
        }
    }

    // ---------------- ADMIN PANEL ----------------
    fun toggleMaintenanceMode(enabled: Boolean) {
        _adminStats.update {
            it.copy(
                isMaintenanceMode = enabled,
                auditLogs = listOf(
                    AuditLogEntry(
                        id = "log_${System.currentTimeMillis()}",
                        action = if (enabled) "ENABLE_MAINTENANCE" else "DISABLE_MAINTENANCE",
                        adminName = _currentUser.value.name,
                        details = "Maintenance mode set to $enabled"
                    )
                ) + it.auditLogs
            )
        }
    }

    fun toggleFeatureFlag(flag: String) {
        _adminStats.update { stats ->
            val updated = stats.featureFlags.toMutableMap()
            updated[flag] = !(updated[flag] ?: true)
            stats.copy(
                featureFlags = updated,
                auditLogs = listOf(
                    AuditLogEntry(
                        id = "log_${System.currentTimeMillis()}",
                        action = "TOGGLE_FEATURE",
                        adminName = _currentUser.value.name,
                        details = "Toggled feature flag: $flag to ${updated[flag]}"
                    )
                ) + stats.auditLogs
            )
        }
    }

    fun banUser(userId: String) {
        _contacts.update { list ->
            list.map { if (it.id == userId) it.copy(isBlocked = true) else it }
        }
        _adminStats.update { stats ->
            stats.copy(
                auditLogs = listOf(
                    AuditLogEntry(
                        id = "log_${System.currentTimeMillis()}",
                        action = "BAN_USER",
                        adminName = _currentUser.value.name,
                        details = "Suspended access for user ID: $userId"
                    )
                ) + stats.auditLogs
            )
        }
    }

    fun unbanUser(userId: String) {
        _contacts.update { list ->
            list.map { if (it.id == userId) it.copy(isBlocked = false) else it }
        }
    }

    // ---------------- SETTINGS ----------------
    fun setReadReceipts(enabled: Boolean) {
        _readReceiptsEnabled.value = enabled
    }

    fun setLastSeenPrivacy(option: String) {
        _lastSeenPrivacy.value = option
    }

    fun setProfilePhotoPrivacy(option: String) {
        _profilePhotoPrivacy.value = option
    }

    fun setChatWallpaperTheme(theme: String) {
        _chatWallpaperTheme.value = theme
    }
}
