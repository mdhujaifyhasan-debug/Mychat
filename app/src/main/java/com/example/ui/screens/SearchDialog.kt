package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*
import com.example.viewmodel.PulseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAllDialog(
    viewModel: PulseViewModel,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val conversations by viewModel.conversations.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val channels by viewModel.channels.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceElevated
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search chats, contacts, channels, messages...", color = DarkTextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = EmeraldPrimary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldPrimary,
                    unfocusedBorderColor = DarkDivider,
                    focusedTextColor = DarkTextPrimary,
                    unfocusedTextColor = DarkTextPrimary
                ),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            val matchedChats = remember(query, conversations) {
                if (query.isBlank()) emptyList()
                else conversations.filter { it.title.contains(query, ignoreCase = true) }
            }

            val matchedContacts = remember(query, contacts) {
                if (query.isBlank()) emptyList()
                else contacts.filter { it.name.contains(query, ignoreCase = true) || it.phone.contains(query) }
            }

            val matchedChannels = remember(query, channels) {
                if (query.isBlank()) emptyList()
                else channels.filter { it.name.contains(query, ignoreCase = true) }
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (matchedChats.isNotEmpty()) {
                    item {
                        Text("CHATS & GROUPS", color = DarkTextSecondary, fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    items(matchedChats) { conv ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onDismiss()
                                    viewModel.openChat(conv.id)
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            UserAvatar(name = conv.title, colorHex = conv.avatarColorHex, size = 38.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(conv.title, color = DarkTextPrimary, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                                Text(conv.lastMessage?.content ?: "", color = DarkTextSecondary, fontSize = 12.sp, maxLines = 1)
                            }
                        }
                    }
                }

                if (matchedContacts.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("CONTACTS", color = DarkTextSecondary, fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    items(matchedContacts) { contact ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onDismiss()
                                    viewModel.openChat("conv_${contact.id}")
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            UserAvatar(name = contact.name, colorHex = contact.avatarColorHex, size = 38.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(contact.name, color = DarkTextPrimary, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                                Text(contact.phone, color = DarkTextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }

                if (matchedChannels.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("CHANNELS", color = DarkTextSecondary, fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    items(matchedChannels) { ch ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            UserAvatar(name = ch.name, colorHex = ch.iconColorHex, size = 38.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(ch.name, color = DarkTextPrimary, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                                Text("${ch.followersCount} followers", color = EmeraldPrimary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
