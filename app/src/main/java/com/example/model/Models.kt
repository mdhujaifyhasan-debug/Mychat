package com.example.model

enum class MessageStatus {
    SENDING, SENT, DELIVERED, READ
}

enum class MessageType {
    TEXT, IMAGE, VIDEO, AUDIO, DOCUMENT, LOCATION, CONTACT, POLL
}

enum class StoryType {
    TEXT, IMAGE, VOICE
}

data class User(
    val id: String,
    val name: String,
    val username: String,
    val phone: String,
    val email: String,
    val avatarUrl: String? = null,
    val avatarColorHex: Long = 0xFF00A884,
    val statusText: String = "Hey there! I am using Pulse.",
    val isOnline: Boolean = false,
    val lastSeen: String = "Just now",
    val isVerified: Boolean = false,
    val isBlocked: Boolean = false,
    val isBusiness: Boolean = false,
    val country: String = "United States",
    val countryCode: String = "+1"
)

data class PollOption(
    val id: String,
    val text: String,
    val voterIds: List<String> = emptyList()
) {
    val voteCount: Int get() = voterIds.size
}

data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.SENT,
    val type: MessageType = MessageType.TEXT,
    val mediaUrl: String? = null,
    val mediaCaption: String? = null,
    val fileName: String? = null,
    val fileSize: String? = null,
    val audioDurationSec: Int = 0,
    val isVoiceNote: Boolean = false,
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    val locationLabel: String? = null,
    val contactName: String? = null,
    val contactPhone: String? = null,
    val pollQuestion: String? = null,
    val pollOptions: List<PollOption> = emptyList(),
    val replyToMessageId: String? = null,
    val replyToContent: String? = null,
    val isEdited: Boolean = false,
    val isDeletedForEveryone: Boolean = false,
    val isStarred: Boolean = false,
    val isPinned: Boolean = false,
    val isViewOnce: Boolean = false,
    val isViewOnceOpened: Boolean = false,
    val disappearingInSeconds: Long? = null,
    val reactions: Map<String, String> = emptyMap() // userId -> emoji
)

data class Conversation(
    val id: String,
    val isGroup: Boolean = false,
    val title: String,
    val avatarUrl: String? = null,
    val avatarColorHex: Long = 0xFF00A884,
    val participantIds: List<String>,
    val lastMessage: Message? = null,
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isMuted: Boolean = false,
    val isLocked: Boolean = false,
    val customThemeHex: Long? = null,
    val wallpaperRes: String? = null,
    val disappearingDurationSec: Long? = null,
    val groupAdminIds: List<String> = emptyList(),
    val groupOwnerId: String? = null,
    val onlyAdminsCanMessage: Boolean = false,
    val groupDescription: String = "",
    val inviteCode: String = "pulse.me/join"
)

data class CallRecord(
    val id: String,
    val peerId: String,
    val peerName: String,
    val peerAvatarHex: Long,
    val isVideo: Boolean,
    val isIncoming: Boolean,
    val isMissed: Boolean,
    val timestamp: Long,
    val durationSeconds: Int
)

data class StatusStory(
    val id: String,
    val userId: String,
    val userName: String,
    val userAvatarHex: Long,
    val type: StoryType = StoryType.TEXT,
    val contentText: String? = null,
    val mediaUrl: String? = null,
    val backgroundColorHex: Long = 0xFF128C7E,
    val caption: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val viewerCount: Int = 0,
    val viewers: List<String> = emptyList(),
    val isViewed: Boolean = false
)

data class ChannelPost(
    val id: String,
    val channelId: String,
    val content: String,
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val viewsCount: Int = 120,
    val reactionsCount: Map<String, Int> = mapOf("❤️" to 45, "🔥" to 88, "👏" to 19)
)

data class Channel(
    val id: String,
    val name: String,
    val username: String,
    val description: String,
    val iconColorHex: Long = 0xFF00B4D8,
    val isVerified: Boolean = true,
    val isPublic: Boolean = true,
    val followersCount: Int = 14200,
    val isFollowed: Boolean = false,
    val posts: List<ChannelPost> = emptyList()
)

data class Community(
    val id: String,
    val name: String,
    val description: String,
    val iconColorHex: Long = 0xFF7209B7,
    val memberCount: Int = 340,
    val announcementGroupId: String,
    val subGroupIds: List<String> = emptyList(),
    val rules: List<String> = listOf("Be respectful", "No spam or unauthorized promotions", "Follow community guidelines")
)

data class ProductItem(
    val id: String,
    val name: String,
    val price: String,
    val description: String,
    val iconHex: Long = 0xFF4361EE
)

data class QuickReply(
    val id: String,
    val shortcut: String,
    val text: String
)

data class BusinessProfile(
    val isEnabled: Boolean = false,
    val businessName: String = "Pulse Studio Co.",
    val category: String = "Technology & Services",
    val description: String = "Next-gen communication systems and software solutions.",
    val address: String = "100 Innovation Way, Suite 400",
    val website: String = "https://pulse.app",
    val hours: String = "Mon - Fri: 9:00 AM - 6:00 PM",
    val greetingMessage: String = "Hello! Thanks for reaching out to Pulse Studio. How can we help you today?",
    val awayMessage: String = "We are currently away, but we will respond as soon as we return!",
    val quickReplies: List<QuickReply> = listOf(
        QuickReply("1", "/thanks", "Thank you for contacting Pulse! Let us know if you need anything else."),
        QuickReply("2", "/pricing", "Our standard packages start at $19/mo. Check our catalog for details."),
        QuickReply("3", "/hours", "Our support hours are Mon-Fri 9 AM to 6 PM EST.")
    ),
    val catalog: List<ProductItem> = listOf(
        ProductItem("p1", "Pulse Pro Cloud Sync", "$19.99/mo", "Encrypted multi-device sync, 100GB secure cloud drive.", 0xFF00A884),
        ProductItem("p2", "Pulse Business API", "$99/mo", "Automated broadcast channels, customer bot and webhooks.", 0xFF4361EE),
        ProductItem("p3", "Hardware Security Key", "$35.00", "FIDO2 / U2F physical key for two-step verification.", 0xFFF72585)
    )
)

data class AuditLogEntry(
    val id: String,
    val action: String,
    val adminName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val details: String
)

data class AdminStats(
    val totalUsers: Int = 18450,
    val activeUsers: Int = 12900,
    val onlineUsers: Int = 3410,
    val totalMessages: Int = 948200,
    val totalGroups: Int = 1420,
    val totalChannels: Int = 380,
    val totalCommunities: Int = 112,
    val totalCalls: Int = 54200,
    val isMaintenanceMode: Boolean = false,
    val featureFlags: Map<String, Boolean> = mapOf(
        "end_to_end_encryption" to true,
        "video_calling" to true,
        "voice_status" to true,
        "channels_broadcast" to true,
        "view_once_media" to true,
        "business_catalog" to true,
        "passkey_support" to true
    ),
    val auditLogs: List<AuditLogEntry> = listOf(
        AuditLogEntry("1", "POLICY_UPDATE", "SuperAdmin", System.currentTimeMillis() - 3600000, "Updated E2EE key rotation window to 30 days"),
        AuditLogEntry("2", "USER_UNBAN", "Admin_Sarah", System.currentTimeMillis() - 7200000, "Lifted temporary rate limit flag for user @alex_chen"),
        AuditLogEntry("3", "SYSTEM_CHECK", "AutomatedBot", System.currentTimeMillis() - 14400000, "All WebRTC signaling nodes healthy (0.01% packet loss)")
    )
)

data class ActiveCallState(
    val isInCall: Boolean = false,
    val isVideo: Boolean = false,
    val peerId: String = "",
    val peerName: String = "",
    val peerAvatarHex: Long = 0xFF00A884,
    val isMuted: Boolean = false,
    val isCameraOn: Boolean = true,
    val isSpeakerOn: Boolean = false,
    val durationSeconds: Int = 0,
    val isFrontCamera: Boolean = true,
    val isConnecting: Boolean = false
)

data class DeviceSession(
    val id: String,
    val deviceName: String,
    val osVersion: String,
    val location: String,
    val lastActive: String,
    val isCurrent: Boolean = false
)
