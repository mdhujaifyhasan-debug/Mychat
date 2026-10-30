package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*
import com.example.viewmodel.PulseViewModel

@Composable
fun ActiveCallScreen(
    viewModel: PulseViewModel,
    modifier: Modifier = Modifier
) {
    val activeCall by viewModel.activeCall.collectAsState()

    BackHandler {
        viewModel.endCall()
    }

    val pulseScale by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F2027),
                        Color(0xFF203A43),
                        Color(0xFF2C5364)
                    )
                )
            )
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Lock, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "End-to-End Encrypted Call",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = activeCall.peerName,
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            val timeText = if (activeCall.isConnecting) {
                "Ringing..."
            } else {
                val mins = activeCall.durationSeconds / 60
                val secs = activeCall.durationSeconds % 60
                "%02d:%02d".format(mins, secs)
            }
            Text(
                text = timeText,
                color = EmeraldPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Center Content (Avatar with Pulsing rings or simulated Video Feed)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 120.dp),
            contentAlignment = Alignment.Center
        ) {
            if (activeCall.isVideo && activeCall.isCameraOn) {
                // Video Call Screen Simulation
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(380.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, EmeraldPrimary.copy(alpha = 0.4f), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        UserAvatar(
                            name = activeCall.peerName,
                            colorHex = activeCall.peerAvatarHex,
                            size = 110.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (activeCall.isFrontCamera) "Live HD Video Feed (Front Camera)" else "Live HD Video Feed (Back Camera)",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Opus Low Delay 48kHz • 1080p 60fps",
                            color = EmeraldPrimary,
                            fontSize = 11.sp
                        )
                    }

                    // Self Video Picture-in-Picture Preview in corner
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(14.dp)
                            .size(90.dp, 120.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceVariant,
                        tonalElevation = 6.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            UserAvatar(name = "Me", colorHex = 0xFF00A884, size = 48.dp)
                        }
                    }
                }
            } else {
                // Voice Call Avatar with Pulsing Outer Ring
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(EmeraldPrimary.copy(alpha = 0.15f))
                    )
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary.copy(alpha = 0.25f))
                    )
                    UserAvatar(
                        name = activeCall.peerName,
                        colorHex = activeCall.peerAvatarHex,
                        size = 100.dp
                    )
                }
            }
        }

        // Bottom Call Controls
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color.Black.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speaker Button
                IconButton(
                    onClick = { viewModel.toggleCallSpeaker() },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (activeCall.isSpeakerOn) Color.White else DarkSurfaceVariant)
                ) {
                    Icon(
                        imageVector = if (activeCall.isSpeakerOn) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeDown,
                        contentDescription = "Speaker",
                        tint = if (activeCall.isSpeakerOn) Color.Black else Color.White
                    )
                }

                // Video Toggle Button
                IconButton(
                    onClick = { viewModel.toggleCallCamera() },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (activeCall.isCameraOn) Color.White else DarkSurfaceVariant)
                ) {
                    Icon(
                        imageVector = if (activeCall.isCameraOn) Icons.Rounded.Videocam else Icons.Rounded.VideocamOff,
                        contentDescription = "Camera",
                        tint = if (activeCall.isCameraOn) Color.Black else Color.White
                    )
                }

                // Flip Camera
                if (activeCall.isVideo) {
                    IconButton(
                        onClick = { viewModel.flipCamera() },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Cameraswitch,
                            contentDescription = "Flip Camera",
                            tint = Color.White
                        )
                    }
                }

                // Mute Mic Button
                IconButton(
                    onClick = { viewModel.toggleCallMute() },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (activeCall.isMuted) DangerRed else Color.White)
                ) {
                    Icon(
                        imageVector = if (activeCall.isMuted) Icons.Rounded.MicOff else Icons.Rounded.Mic,
                        contentDescription = "Mute",
                        tint = if (activeCall.isMuted) Color.White else Color.Black
                    )
                }

                // End Call Button
                IconButton(
                    onClick = { viewModel.endCall() },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(DangerRed)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CallEnd,
                        contentDescription = "End Call",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
