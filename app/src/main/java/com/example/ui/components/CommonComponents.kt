package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MessageStatus
import com.example.model.PollOption
import com.example.ui.theme.*

@Composable
fun UserAvatar(
    name: String,
    colorHex: Long,
    size: Dp = 48.dp,
    isOnline: Boolean = false,
    showOnlineBadge: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.size(size)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(colorHex),
                            Color(colorHex).copy(alpha = 0.8f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            val initials = name.split(" ")
                .filter { it.isNotBlank() }
                .take(2)
                .map { it.first().uppercase() }
                .joinToString("")
                .ifEmpty { "P" }

            Text(
                text = initials,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.4f).sp
            )
        }

        if (showOnlineBadge && isOnline) {
            Box(
                modifier = Modifier
                    .size(size * 0.3f)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Color(0xFF00A884))
                    .padding(2.dp)
            )
        }
    }
}

@Composable
fun MessageStatusIndicator(status: MessageStatus, modifier: Modifier = Modifier) {
    when (status) {
        MessageStatus.SENDING -> Icon(
            imageVector = Icons.Rounded.AccessTime,
            contentDescription = "Sending",
            tint = DarkTextSecondary,
            modifier = modifier.size(14.dp)
        )
        MessageStatus.SENT -> Icon(
            imageVector = Icons.Rounded.Check,
            contentDescription = "Sent",
            tint = DarkTextSecondary,
            modifier = modifier.size(14.dp)
        )
        MessageStatus.DELIVERED -> Icon(
            imageVector = Icons.Rounded.DoneAll,
            contentDescription = "Delivered",
            tint = DarkTextSecondary,
            modifier = modifier.size(15.dp)
        )
        MessageStatus.READ -> Icon(
            imageVector = Icons.Rounded.DoneAll,
            contentDescription = "Read",
            tint = ReadTickBlue,
            modifier = modifier.size(15.dp)
        )
    }
}

@Composable
fun E2EESecurityBadge(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = DarkSurfaceVariant.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Lock,
                contentDescription = "E2EE",
                tint = SafetyGold,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "End-to-end encrypted. No one outside this chat can read or listen.",
                color = DarkTextSecondary,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun AudioWaveformPlayer(
    durationSec: Int,
    isPlaying: Boolean,
    progress: Float,
    speed: Float,
    onPlayPause: () -> Unit,
    onSpeedClick: () -> Unit,
    isOutgoing: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPlayPause,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isOutgoing) EmeraldPrimary else TealAccent)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Simulated waveform bars
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val heights = listOf(8, 14, 22, 12, 18, 24, 16, 20, 10, 22, 14, 18, 24, 12, 16, 20, 8, 14, 18, 12)
                heights.forEachIndexed { idx, barH ->
                    val barThreshold = idx.toFloat() / heights.size
                    val isPast = progress >= barThreshold
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(barH.dp)
                            .clip(RoundedCornerShape(1.5.dp))
                            .background(
                                if (isPast) EmeraldPrimary else DarkTextSecondary.copy(alpha = 0.4f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val elapsed = (durationSec * progress).toInt()
                val currentText = "%02d:%02d".format(elapsed / 60, elapsed % 60)
                val totalText = "%02d:%02d".format(durationSec / 60, durationSec % 60)
                Text(
                    text = if (isPlaying) currentText else totalText,
                    fontSize = 11.sp,
                    color = DarkTextSecondary
                )
                Text(
                    text = "🎙️ Voice",
                    fontSize = 11.sp,
                    color = DarkTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Playback speed pill
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DarkSurfaceVariant,
            modifier = Modifier.clickable { onSpeedClick() }
        ) {
            Text(
                text = "${speed}x",
                color = DarkTextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun PollCardView(
    question: String,
    options: List<PollOption>,
    currentUserId: String,
    onVote: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalVotes = options.sumOf { it.voteCount }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        color = DarkSurfaceElevated
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Poll,
                    contentDescription = "Poll",
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = question,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = DarkTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            options.forEach { option ->
                val hasVoted = option.voterIds.contains(currentUserId)
                val percent = if (totalVotes > 0) (option.voteCount.toFloat() / totalVotes) else 0f

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onVote(option.id) },
                    color = if (hasVoted) EmeraldPrimary.copy(alpha = 0.15f) else DarkSurfaceVariant,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (hasVoted) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (hasVoted) EmeraldPrimary else DarkTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = option.text,
                                    fontSize = 14.sp,
                                    color = DarkTextPrimary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "${option.voteCount} (${(percent * 100).toInt()}%)",
                                fontSize = 12.sp,
                                color = DarkTextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { percent },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (hasVoted) EmeraldPrimary else TealAccent,
                            trackColor = DarkDivider
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$totalVotes votes • Select to vote or change vote",
                fontSize = 11.sp,
                color = DarkTextSecondary
            )
        }
    }
}
