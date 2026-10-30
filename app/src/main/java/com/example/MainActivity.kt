package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.PulseViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                PulseAppRoot()
            }
        }
    }
}

@Composable
fun PulseAppRoot(
    viewModel: PulseViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        if (!isAuthenticated) {
            AuthScreen(viewModel = viewModel)
        } else {
            when (val screen = currentScreen) {
                is Screen.Main -> MainHomeScreen(initialTab = screen.tabIndex, viewModel = viewModel)
                is Screen.ChatDetail -> ChatDetailScreen(conversationId = screen.conversationId, viewModel = viewModel)
                is Screen.ActiveCall -> ActiveCallScreen(viewModel = viewModel)
                is Screen.StatusViewer -> StatusViewerScreen(storyId = screen.initialStoryId, viewModel = viewModel)
                is Screen.CreateGroup -> CreateGroupScreen(viewModel = viewModel)
                is Screen.AdminDashboard -> AdminDashboardScreen(viewModel = viewModel)
                is Screen.BusinessTools -> BusinessToolsScreen(viewModel = viewModel)
                is Screen.DeviceManagement -> DeviceManagementScreen(viewModel = viewModel)
                is Screen.PrivacySecuritySettings -> SettingsTab(viewModel = viewModel)
                is Screen.Auth -> AuthScreen(viewModel = viewModel)
                else -> MainHomeScreen(initialTab = 0, viewModel = viewModel)
            }
        }
    }
}
