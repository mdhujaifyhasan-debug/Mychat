package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.PulseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessToolsScreen(
    viewModel: PulseViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.businessProfile.collectAsState()
    var showAddProductDialog by remember { mutableStateOf(false) }

    BackHandler {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Business Suite", fontWeight = FontWeight.Bold, color = DarkTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = DarkTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurfaceElevated)
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Business Profile Card
            item {
                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(EmeraldPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.Storefront, contentDescription = null, tint = Color.Black, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(profile.businessName, color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                                Text(profile.category, color = EmeraldPrimary, fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(profile.description, color = DarkTextSecondary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("📍 ${profile.address}", color = DarkTextSecondary, fontSize = 12.sp)
                        Text("🌐 ${profile.website}", color = CyanAccent, fontSize = 12.sp)
                        Text("🕒 ${profile.hours}", color = DarkTextSecondary, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Product Catalog
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("PRODUCT CATALOG", color = DarkTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { showAddProductDialog = true }) {
                        Icon(Icons.Rounded.Add, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Item", color = EmeraldPrimary, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            items(profile.catalog) { item ->
                Surface(
                    color = DarkSurfaceElevated,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(item.iconHex)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.ShoppingBag, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name, color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(item.price, color = EmeraldPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(item.description, color = DarkTextSecondary, fontSize = 11.sp)
                        }
                        IconButton(onClick = { viewModel.deleteProduct(item.id) }) {
                            Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = DangerRed)
                        }
                    }
                }
            }

            // Quick Replies
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text("QUICK REPLIES SHORTCUTS", color = DarkTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                profile.quickReplies.forEach { reply ->
                    Surface(
                        color = DarkSurfaceElevated,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(reply.shortcut, color = EmeraldPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(reply.text, color = DarkTextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Automated Messages
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text("AUTOMATED MESSAGING", color = DarkTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                Surface(color = DarkSurfaceElevated, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Greeting Message", color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(profile.greetingMessage, color = DarkTextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Away Message", color = DarkTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(profile.awayMessage, color = DarkTextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }
    }

    if (showAddProductDialog) {
        var prodName by remember { mutableStateOf("") }
        var prodPrice by remember { mutableStateOf("") }
        var prodDesc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddProductDialog = false },
            title = { Text("Add Catalog Item", color = DarkTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = prodName,
                        onValueChange = { prodName = it },
                        label = { Text("Item Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary, focusedTextColor = DarkTextPrimary, unfocusedTextColor = DarkTextPrimary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = prodPrice,
                        onValueChange = { prodPrice = it },
                        label = { Text("Price (e.g. $19.99)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary, focusedTextColor = DarkTextPrimary, unfocusedTextColor = DarkTextPrimary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = prodDesc,
                        onValueChange = { prodDesc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary, focusedTextColor = DarkTextPrimary, unfocusedTextColor = DarkTextPrimary)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (prodName.isNotBlank() && prodPrice.isNotBlank()) {
                            viewModel.addProduct(prodName, prodPrice, prodDesc)
                            showAddProductDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Add Item", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProductDialog = false }) {
                    Text("Cancel", color = DarkTextSecondary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}
