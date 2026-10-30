package com.example

import com.example.data.PulseRepository
import com.example.model.MessageType
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PulseRepositoryTest {

    private lateinit var repository: PulseRepository

    @Before
    fun setup() {
        repository = PulseRepository()
    }

    @Test
    fun testSendMessageAppendsCorrectly() {
        val convId = "conv_sophia"
        val initialCount = repository.messages.value[convId]?.size ?: 0

        repository.sendMessage(
            conversationId = convId,
            content = "Testing automated messaging flow",
            type = MessageType.TEXT
        )

        val updatedMessages = repository.messages.value[convId]
        assertNotNull(updatedMessages)
        assertEquals(initialCount + 1, updatedMessages!!.size)
        assertEquals("Testing automated messaging flow", updatedMessages.last().content)
    }

    @Test
    fun testReactionToggle() {
        val convId = "conv_sophia"
        val firstMsgId = repository.messages.value[convId]!!.first().id

        repository.addReaction(convId, firstMsgId, "🔥")
        val msgWithReaction = repository.messages.value[convId]!!.find { it.id == firstMsgId }
        assertNotNull(msgWithReaction)
        assertEquals("🔥", msgWithReaction!!.reactions["user_me"])

        // Toggling same emoji removes it
        repository.addReaction(convId, firstMsgId, "🔥")
        val msgToggled = repository.messages.value[convId]!!.find { it.id == firstMsgId }
        assertNull(msgToggled!!.reactions["user_me"])
    }

    @Test
    fun testCallLifecycle() {
        repository.startCall("u1", "Sophia Rivera", 0xFF4361EE, true)
        val activeCall = repository.activeCall.value
        assertTrue(activeCall.isInCall)
        assertTrue(activeCall.isVideo)
        assertEquals("Sophia Rivera", activeCall.peerName)

        repository.tickCallDuration()
        assertEquals(1, repository.activeCall.value.durationSeconds)

        repository.endCall()
        assertFalse(repository.activeCall.value.isInCall)
        val lastRecord = repository.callRecords.value.first()
        assertEquals("Sophia Rivera", lastRecord.peerName)
    }

    @Test
    fun testAdminMaintenanceModeToggle() {
        assertFalse(repository.adminStats.value.isMaintenanceMode)
        repository.toggleMaintenanceMode(true)
        assertTrue(repository.adminStats.value.isMaintenanceMode)
    }
}
