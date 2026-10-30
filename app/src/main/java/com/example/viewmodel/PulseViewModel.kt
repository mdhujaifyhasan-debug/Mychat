package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PulseRepository
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen {
    data class Main(val tabIndex: Int = 0) : Screen()
    data class ChatDetail(val conversationId: String) : Screen()
    object ActiveCall : Screen()
    data class StatusViewer(val initialStoryId: String) : Screen()
    object CreateStatus : Screen()
    object CreateGroup : Screen()
    data class ChannelDetail(val channelId: String) : Screen()
    data class CommunityDetail(val communityId: String) : Screen()
    object BusinessTools : Screen()
    object AdminDashboard : Screen()
    object DeviceManagement : Screen()
    object PrivacySecuritySettings : Screen()
    object TwoStepSetup : Screen()
    object SearchAll : Screen()
    object Auth : Screen()
    object LockScreen : Screen()
}

class PulseViewModel(
    private val repository: PulseRepository = PulseRepository()
) : ViewModel() {

    // Current Screen Stack
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Main(0))
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val screenStack = mutableListOf<Screen>()

    // Repository Flows
    val currentUser = repository.currentUser
    val isAuthenticated = repository.isAuthenticated
    val isAppLocked = repository.isAppLocked
    val isTwoStepLocked = repository.isTwoStepLocked
    val contacts = repository.contacts
    val conversations = repository.conversations
    val messages = repository.messages
    val stories = repository.stories
    val channels = repository.channels
    val communities = repository.communities
    val callRecords = repository.callRecords
    val activeCall = repository.activeCall
    val businessProfile = repository.businessProfile
    val adminStats = repository.adminStats
    val devices = repository.devices
    val readReceiptsEnabled = repository.readReceiptsEnabled
    val lastSeenPrivacy = repository.lastSeenPrivacy
    val profilePhotoPrivacy = repository.profilePhotoPrivacy
    val chatWallpaperTheme = repository.chatWallpaperTheme
    val isTwoStepEnabled = repository.isTwoStepEnabled
    val isBiometricEnabled = repository.isBiometricEnabled

    // Chat Detail States
    private val _selectedConversationId = MutableStateFlow<String?>(null)
    val selectedConversationId: StateFlow<String?> = _selectedConversationId.asStateFlow()

    // Voice Note Player Simulation
    private val _playingAudioMessageId = MutableStateFlow<String?>(null)
    val playingAudioMessageId: StateFlow<String?> = _playingAudioMessageId.asStateFlow()

    private val _audioPlaybackSpeed = MutableStateFlow(1.0f) // 1.0, 1.5, 2.0
    val audioPlaybackSpeed: StateFlow<Float> = _audioPlaybackSpeed.asStateFlow()

    private val _audioProgress = MutableStateFlow(0f)
    val audioProgress: StateFlow<Float> = _audioProgress.asStateFlow()

    private var audioJob: Job? = null

    // Voice Note Recording Simulation
    private val _isRecordingVoice = MutableStateFlow(false)
    val isRecordingVoice: StateFlow<Boolean> = _isRecordingVoice.asStateFlow()

    private val _recordDurationSec = MutableStateFlow(0)
    val recordDurationSec: StateFlow<Int> = _recordDurationSec.asStateFlow()

    private var recordJob: Job? = null

    // Call Duration Timer Job
    private var callTimerJob: Job? = null

    // Incoming Call Banner Simulation
    private val _incomingCallOffer = MutableStateFlow<CallRecord?>(null)
    val incomingCallOffer: StateFlow<CallRecord?> = _incomingCallOffer.asStateFlow()

    // Active Message Action Sheet / Edit / Reply
    private val _replyingToMessage = MutableStateFlow<Message?>(null)
    val replyingToMessage: StateFlow<Message?> = _replyingToMessage.asStateFlow()

    private val _editingMessage = MutableStateFlow<Message?>(null)
    val editingMessage: StateFlow<Message?> = _editingMessage.asStateFlow()

    // Search Query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Chat Filter: 0: All, 1: Unread, 2: Favorites, 3: Groups
    private val _chatFilterIndex = MutableStateFlow(0)
    val chatFilterIndex: StateFlow<Int> = _chatFilterIndex.asStateFlow()

    init {
        // Monitor active call for timer
        viewModelScope.launch {
            activeCall.collect { call ->
                if (call.isInCall) {
                    if (callTimerJob == null || callTimerJob?.isActive == false) {
                        callTimerJob = launch {
                            while (activeCall.value.isInCall) {
                                delay(1000)
                                repository.tickCallDuration()
                            }
                        }
                    }
                } else {
                    callTimerJob?.cancel()
                    callTimerJob = null
                }
            }
        }
    }

    // Navigation
    fun navigateTo(screen: Screen) {
        screenStack.add(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (screenStack.isNotEmpty()) {
            _currentScreen.value = screenStack.removeAt(screenStack.size - 1)
            return true
        }
        if (_currentScreen.value !is Screen.Main) {
            _currentScreen.value = Screen.Main(0)
            return true
        }
        return false
    }

    fun switchTab(tabIndex: Int) {
        _currentScreen.value = Screen.Main(tabIndex)
    }

    fun openChat(conversationId: String) {
        _selectedConversationId.value = conversationId
        navigateTo(Screen.ChatDetail(conversationId))
    }

    // Voice Note Player
    fun togglePlayAudio(messageId: String, durationSec: Int) {
        if (_playingAudioMessageId.value == messageId) {
            // Stop
            audioJob?.cancel()
            _playingAudioMessageId.value = null
            _audioProgress.value = 0f
        } else {
            // Start
            audioJob?.cancel()
            _playingAudioMessageId.value = messageId
            _audioProgress.value = 0f
            val totalMs = (durationSec.coerceAtLeast(4) * 1000L).toFloat()
            audioJob = viewModelScope.launch {
                val stepMs = 100L
                var elapsed = 0L
                while (elapsed < totalMs && _playingAudioMessageId.value == messageId) {
                    delay(stepMs)
                    elapsed += (stepMs * _audioPlaybackSpeed.value).toLong()
                    _audioProgress.value = (elapsed / totalMs).coerceIn(0f, 1f)
                }
                _playingAudioMessageId.value = null
                _audioProgress.value = 0f
            }
        }
    }

    fun cyclePlaybackSpeed() {
        _audioPlaybackSpeed.value = when (_audioPlaybackSpeed.value) {
            1.0f -> 1.5f
            1.5f -> 2.0f
            else -> 1.0f
        }
    }

    // Voice Note Recorder
    fun startVoiceRecording() {
        _isRecordingVoice.value = true
        _recordDurationSec.value = 0
        recordJob?.cancel()
        recordJob = viewModelScope.launch {
            while (_isRecordingVoice.value) {
                delay(1000)
                _recordDurationSec.value += 1
            }
        }
    }

    fun cancelVoiceRecording() {
        recordJob?.cancel()
        _isRecordingVoice.value = false
        _recordDurationSec.value = 0
    }

    fun sendVoiceRecording(conversationId: String) {
        val duration = _recordDurationSec.value.coerceAtLeast(2)
        cancelVoiceRecording()
        repository.sendMessage(
            conversationId = conversationId,
            content = "Voice message ($duration s)",
            type = MessageType.AUDIO,
            isVoiceNote = true,
            audioDurationSec = duration
        )
    }

    // Send Regular Message
    fun sendTextMessage(conversationId: String, text: String, isViewOnce: Boolean = false) {
        val reply = _replyingToMessage.value
        val edit = _editingMessage.value
        if (edit != null) {
            repository.editMessage(conversationId, edit.id, text)
            _editingMessage.value = null
            return
        }
        repository.sendMessage(
            conversationId = conversationId,
            content = text,
            replyToMessageId = reply?.id,
            replyToContent = reply?.content?.take(40),
            isViewOnce = isViewOnce
        )
        _replyingToMessage.value = null
    }

    fun setReplyingTo(message: Message?) {
        _replyingToMessage.value = message
        _editingMessage.value = null
    }

    fun setEditing(message: Message?) {
        _editingMessage.value = message
        _replyingToMessage.value = null
    }

    fun cancelReplyingOrEditing() {
        _replyingToMessage.value = null
        _editingMessage.value = null
    }

    fun addReaction(conversationId: String, messageId: String, emoji: String) {
        repository.addReaction(conversationId, messageId, emoji)
    }

    fun votePoll(conversationId: String, messageId: String, optionId: String) {
        repository.votePoll(conversationId, messageId, optionId)
    }

    fun deleteMessage(conversationId: String, messageId: String, forEveryone: Boolean) {
        repository.deleteMessage(conversationId, messageId, forEveryone)
    }

    fun togglePinMessage(conversationId: String, messageId: String) {
        repository.togglePinMessage(conversationId, messageId)
    }

    fun toggleStarMessage(conversationId: String, messageId: String) {
        repository.toggleStarMessage(conversationId, messageId)
    }

    fun openViewOnce(conversationId: String, messageId: String) {
        repository.openViewOnce(conversationId, messageId)
    }

    // Attachments
    fun sendMockPhoto(conversationId: String, caption: String = "Photo attachment") {
        repository.sendMessage(
            conversationId = conversationId,
            content = caption,
            type = MessageType.IMAGE,
            mediaCaption = caption
        )
    }

    fun sendMockDocument(conversationId: String, name: String = "Project_Specs_2026.pdf") {
        repository.sendMessage(
            conversationId = conversationId,
            content = "Document: $name",
            type = MessageType.DOCUMENT,
            fileName = name,
            fileSize = "1.8 MB"
        )
    }

    fun sendMockLocation(conversationId: String, label: String = "Current Location") {
        repository.sendMessage(
            conversationId = conversationId,
            content = "Location: $label",
            type = MessageType.LOCATION,
            locationLat = 37.78825,
            locationLng = -122.4324,
            locationLabel = label
        )
    }

    fun sendMockContact(conversationId: String, name: String, phone: String) {
        repository.sendMessage(
            conversationId = conversationId,
            content = "Contact: $name",
            type = MessageType.CONTACT,
            contactName = name,
            contactPhone = phone
        )
    }

    fun createPoll(conversationId: String, question: String, options: List<String>) {
        val pollOptions = options.filter { it.isNotBlank() }.mapIndexed { idx, text ->
            PollOption("opt_$idx", text)
        }
        if (question.isNotBlank() && pollOptions.size >= 2) {
            repository.sendMessage(
                conversationId = conversationId,
                content = "Poll: $question",
                type = MessageType.POLL,
                pollQuestion = question,
                pollOptions = pollOptions
            )
        }
    }

    fun sendMessage(conversationId: String, text: String) {
        sendTextMessage(conversationId, text)
    }

    fun togglePinConversation(conversationId: String) = repository.togglePinConversation(conversationId)
    fun toggleArchiveConversation(conversationId: String) = repository.toggleArchiveConversation(conversationId)
    fun toggleMuteConversation(conversationId: String) = repository.toggleMuteConversation(conversationId)
    fun toggleLockConversation(conversationId: String) = repository.toggleLockConversation(conversationId)

    // Calls
    fun startCall(peerId: String, peerName: String, peerAvatarHex: Long, isVideo: Boolean) {
        repository.startCall(peerId, peerName, peerAvatarHex, isVideo)
        navigateTo(Screen.ActiveCall)
    }

    fun endCall() {
        repository.endCall()
        navigateBack()
    }

    fun toggleCallMute() = repository.toggleCallMute()
    fun toggleCallCamera() = repository.toggleCallCamera()
    fun toggleCallSpeaker() = repository.toggleCallSpeaker()
    fun flipCamera() = repository.flipCamera()

    fun simulateIncomingCall(peerName: String = "Sophia Rivera", isVideo: Boolean = true) {
        _incomingCallOffer.value = CallRecord(
            id = "incoming_${System.currentTimeMillis()}",
            peerId = "u1",
            peerName = peerName,
            peerAvatarHex = 0xFF4361EE,
            isVideo = isVideo,
            isIncoming = true,
            isMissed = false,
            timestamp = System.currentTimeMillis(),
            durationSeconds = 0
        )
    }

    fun acceptIncomingCall() {
        val offer = _incomingCallOffer.value ?: return
        _incomingCallOffer.value = null
        startCall(offer.peerId, offer.peerName, offer.peerAvatarHex, offer.isVideo)
    }

    fun rejectIncomingCall() {
        val offer = _incomingCallOffer.value ?: return
        repository.endCall()
        _incomingCallOffer.value = null
    }

    // Status
    fun postTextStatus(text: String, bgHex: Long) {
        repository.postStory(StoryType.TEXT, text = text, bgHex = bgHex)
    }

    fun postPhotoStatus(caption: String) {
        repository.postStory(StoryType.IMAGE, caption = caption, bgHex = 0xFF128C7E)
    }

    fun markStoryViewed(storyId: String) {
        repository.markStoryViewed(storyId)
    }

    // Channels
    fun toggleFollowChannel(channelId: String) = repository.toggleFollowChannel(channelId)
    fun postToChannel(channelId: String, content: String) = repository.postToChannel(channelId, content)

    // Groups
    fun createGroup(name: String, desc: String, members: List<String>) {
        val id = repository.createGroup(name, desc, members)
        openChat(id)
    }

    // Business
    fun updateBusinessProfile(
        name: String, category: String, desc: String,
        address: String, website: String, hours: String,
        greeting: String, away: String
    ) {
        repository.updateBusinessProfile(name, category, desc, address, website, hours, greeting, away)
    }

    fun addProduct(name: String, price: String, desc: String) = repository.addProduct(name, price, desc)
    fun deleteProduct(id: String) = repository.deleteProduct(id)

    // Admin
    fun toggleMaintenanceMode(enabled: Boolean) = repository.toggleMaintenanceMode(enabled)
    fun toggleFeatureFlag(flag: String) = repository.toggleFeatureFlag(flag)
    fun banUser(userId: String) = repository.banUser(userId)
    fun unbanUser(userId: String) = repository.unbanUser(userId)

    // Search & Filters
    fun setSearchQuery(q: String) {
        _searchQuery.value = q
    }

    fun setChatFilter(index: Int) {
        _chatFilterIndex.value = index
    }

    // Auth
    fun loginWithPhone(phone: String, otp: String): Boolean = repository.loginWithPhone(phone, otp)
    fun loginWithEmail(email: String, pass: String): Boolean = repository.loginWithEmail(email, pass)
    fun loginWithGoogle() = repository.loginWithGoogle()
    fun logout() {
        repository.logout()
        _currentScreen.value = Screen.Auth
    }

    fun unlockApp(pin: String): Boolean = repository.unlockApp(pin)
    fun unlockTwoStep(pin: String): Boolean = repository.unlockTwoStep(pin)
    fun toggleAppLock(enabled: Boolean, pin: String) = repository.toggleAppLock(enabled, pin)
    fun toggleTwoStep(enabled: Boolean, pin: String) = repository.toggleTwoStepVerification(enabled, pin)
    fun removeDevice(id: String) = repository.removeDevice(id)
    fun updateProfile(name: String, username: String, statusText: String, phone: String, email: String) =
        repository.updateProfile(name, username, statusText, phone, email)

    fun setReadReceipts(enabled: Boolean) = repository.setReadReceipts(enabled)
    fun setLastSeenPrivacy(option: String) = repository.setLastSeenPrivacy(option)
    fun setProfilePhotoPrivacy(option: String) = repository.setProfilePhotoPrivacy(option)
    fun setChatWallpaperTheme(theme: String) = repository.setChatWallpaperTheme(theme)
}
