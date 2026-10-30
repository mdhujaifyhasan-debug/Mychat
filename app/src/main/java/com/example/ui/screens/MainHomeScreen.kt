package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*
import com.example.viewmodel.PulseViewModel
import com.example.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHomeScreen(
    initialTab: Int = 0,
    viewModel: PulseViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    var showSearchDialog by remember { mutableStateOf(false) }
    val conversations by viewModel.conversations.collectAsState()
    val callRecords by viewModel.callRecords.collectAsState()
    val incomingOffer by viewModel.incomingCallOffer.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val totalUnread = conversations.sumOf { it.unreadCount }
    val missedCalls = callRecords.count { it.isMissed }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(EmeraldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Shield,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "mychat",
                            color = DarkTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.postPhotoStatus("Quick camera snap from Pulse 📸")
                            viewModel.switchTab(1)
                        }
                    ) {
                        Icon(Icons.Rounded.CameraAlt, contentDescription = "Camera", tint = DarkTextPrimary)
                    }
                    IconButton(
                        onClick = { showSearchDialog = true },
                        modifier = Modifier.testTag("global_search_button")
                    ) {
                        Icon(Icons.Rounded.Search, contentDescription = "Search", tint = DarkTextPrimary)
                    }
                    IconButton(
                        onClick = { viewModel.switchTab(4) }
                    ) {
                        UserAvatar(
                            name = currentUser.name,
                            colorHex = currentUser.avatarColorHex,
                            size = 32.dp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurfaceElevated
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurfaceElevated,
                contentColor = DarkTextPrimary,
                tonalElevation = 8.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Tab 0: Chats
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (totalUnread > 0) {
                                    Badge(containerColor = EmeraldPrimary, contentColor = Color.Black) {
                                        Text("$totalUnread", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (selectedTab == 0) Icons.Rounded.Chat else Icons.Rounded.ChatBubbleOutline,
                                contentDescription = "Chats"
                            )
                        }
                    },
                    label = { Text("Chats", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldPrimary,
                        unselectedIconColor = DarkTextSecondary,
                        unselectedTextColor = DarkTextSecondary
                    ),
                    modifier = Modifier.testTag("tab_chats")
                )

                // Tab 1: Updates
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Rounded.MotionPhotosOn else Icons.Rounded.MotionPhotosOff,
                            contentDescription = "Updates"
                        )
                    },
                    label = { Text("Updates", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldPrimary,
                        unselectedIconColor = DarkTextSecondary,
                        unselectedTextColor = DarkTextSecondary
                    ),
                    modifier = Modifier.testTag("tab_updates")
                )

                // Tab 2: Communities
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 2) Icons.Rounded.Groups else Icons.Rounded.GroupWork,
                            contentDescription = "Communities"
                        )
                    },
                    label = { Text("Communities", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldPrimary,
                        unselectedIconColor = DarkTextSecondary,
                        unselectedTextColor = DarkTextSecondary
                    ),
                    modifier = Modifier.testTag("tab_communities")
                )

                // Tab 3: Calls
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (missedCalls > 0) {
                                    Badge(containerColor = CallMissedRed, contentColor = Color.White) {
                                        Text("$missedCalls", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (selectedTab == 3) Icons.Rounded.Call else Icons.Rounded.Phone,
                                contentDescription = "Calls"
                            )
                        }
                    },
                    label = { Text("Calls", fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldPrimary,
                        unselectedIconColor = DarkTextSecondary,
                        unselectedTextColor = DarkTextSecondary
                    ),
                    modifier = Modifier.testTag("tab_calls")
                )

                // Tab 4: Settings
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 4) Icons.Rounded.Settings else Icons.Rounded.SettingsSuggest,
                            contentDescription = "Settings"
                        )
                    },
                    label = { Text("Settings", fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = EmeraldPrimary,
                        indicatorColor = EmeraldPrimary,
                        unselectedIconColor = DarkTextSecondary,
                        unselectedTextColor = DarkTextSecondary
                    ),
                    modifier = Modifier.testTag("tab_settings")
                )
            }
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> ChatsTab(viewModel = viewModel)
                1 -> UpdatesTab(viewModel = viewModel)
                2 -> CommunitiesTab(viewModel = viewModel)
                3 -> CallsTab(viewModel = viewModel)
                4 -> SettingsTab(viewModel = viewModel)
            }

            // Incoming Call Alert Banner
            incomingOffer?.let { offer ->
                Surface(
                    color = DarkSurfaceElevated,
                    tonalElevation = 10.dp,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopCenter)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UserAvatar(name = offer.peerName, colorHex = offer.peerAvatarHex, size = 44.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(offer.peerName, color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                if (offer.isVideo) "Incoming Video Call..." else "Incoming Voice Call...",
                                color = EmeraldPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Decline
                        IconButton(
                            onClick = { viewModel.rejectIncomingCall() },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(DangerRed)
                        ) {
                            Icon(Icons.Rounded.CallEnd, contentDescription = "Decline", tint = Color.White)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Accept
                        IconButton(
                            onClick = { viewModel.acceptIncomingCall() },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary)
                        ) {
                            Icon(Icons.Rounded.Call, contentDescription = "Accept", tint = Color.Black)
                        }
                    }
                }
            }
        }
    }

    if (showSearchDialog) {
        SearchAllDialog(
            viewModel = viewModel,
            onDismiss = { showSearchDialog = false }
        )
    }
}
