package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.auth.AuthRepository
import com.example.model.AppDestination
import com.example.model.Property
import com.example.model.UserRole
import com.example.ui.admin.AdminDashboard
import com.example.ui.auth.AuthDialog
import com.example.ui.chat.ChatBotDialog
import com.example.ui.components.BedSpaceTopBar
import com.example.ui.components.NotificationCenterDialog
import com.example.ui.faq.FaqScreen
import com.example.ui.landlord.LandlordDashboard
import com.example.ui.landing.LandingPage
import com.example.ui.privacy.PrivacyPolicyScreen
import com.example.ui.student.StudentDashboard
import com.example.ui.theme.BedSpaceTheme
import com.example.util.NotificationHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Android notification channels for Bookings, Inquiries, and KYC
        NotificationHelper.initNotificationChannels(this)
        enableEdgeToEdge()
        setContent {
            BedSpaceTheme {
                BedSpaceApp()
            }
        }
    }
}

@Composable
fun BedSpaceApp() {
    val currentUser by AuthRepository.currentUser.collectAsState()
    var currentDestination by remember { mutableStateOf(AppDestination.LANDING) }
    var showChatBot by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }

    // If user logs out while on a role-restricted screen, return to Landing
    LaunchedEffect(currentUser) {
        if (currentUser == null && (currentDestination == AppDestination.STUDENT || currentDestination == AppDestination.LANDLORD || currentDestination == AppDestination.ADMIN)) {
            currentDestination = AppDestination.LANDING
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BedSpaceTopBar(
                currentDestination = currentDestination,
                onNavigate = { destination ->
                    // Validate authorization before navigating
                    when (destination) {
                        AppDestination.STUDENT -> {
                            if (currentUser == null) {
                                showAuthDialog = true
                            } else if (currentUser?.role == UserRole.STUDENT || currentUser?.role == UserRole.ADMIN) {
                                currentDestination = destination
                            } else {
                                showAuthDialog = true
                            }
                        }
                        AppDestination.LANDLORD -> {
                            if (currentUser == null) {
                                showAuthDialog = true
                            } else if (currentUser?.role == UserRole.LANDLORD || currentUser?.role == UserRole.ADMIN) {
                                currentDestination = destination
                            } else {
                                showAuthDialog = true
                            }
                        }
                        AppDestination.ADMIN -> {
                            if (currentUser?.role == UserRole.ADMIN) {
                                currentDestination = destination
                            } else {
                                showAuthDialog = true
                            }
                        }
                        else -> {
                            currentDestination = destination
                        }
                    }
                },
                onOpenChatBot = { showChatBot = true },
                onOpenNotifications = { showNotifications = true },
                onOpenAuth = { showAuthDialog = true }
            )
        }
    ) { innerPadding ->
        Crossfade(
            targetState = currentDestination,
            label = "ScreenTransition",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { destination ->
            when (destination) {
                AppDestination.LANDING -> LandingPage(
                    onNavigate = { newDest -> currentDestination = newDest },
                    onOpenChatBot = { showChatBot = true }
                )
                AppDestination.STUDENT -> {
                    if (currentUser != null && (currentUser?.role == UserRole.STUDENT || currentUser?.role == UserRole.ADMIN)) {
                        StudentDashboard(
                            onOpenChatBot = { showChatBot = true }
                        )
                    } else {
                        LandingPage(
                            onNavigate = { newDest -> currentDestination = newDest },
                            onOpenChatBot = { showChatBot = true }
                        )
                    }
                }
                AppDestination.LANDLORD -> {
                    if (currentUser != null && (currentUser?.role == UserRole.LANDLORD || currentUser?.role == UserRole.ADMIN)) {
                        LandlordDashboard()
                    } else {
                        LandingPage(
                            onNavigate = { newDest -> currentDestination = newDest },
                            onOpenChatBot = { showChatBot = true }
                        )
                    }
                }
                AppDestination.ADMIN -> {
                    if (currentUser?.role == UserRole.ADMIN) {
                        AdminDashboard()
                    } else {
                        LandingPage(
                            onNavigate = { newDest -> currentDestination = newDest },
                            onOpenChatBot = { showChatBot = true }
                        )
                    }
                }
                AppDestination.PRIVACY_POLICY -> PrivacyPolicyScreen(
                    onBack = { currentDestination = AppDestination.LANDING }
                )
                AppDestination.FAQS -> FaqScreen(
                    onBack = { currentDestination = AppDestination.LANDING }
                )
            }
        }
    }

    // Floating AI Chatbot Dialog
    if (showChatBot) {
        ChatBotDialog(
            onDismiss = { showChatBot = false },
            onSelectProperty = { _: Property ->
                currentDestination = AppDestination.STUDENT
                showChatBot = false
            }
        )
    }

    // Push Notification Center Dialog
    if (showNotifications) {
        NotificationCenterDialog(
            onDismiss = { showNotifications = false }
        )
    }

    // Authentication Dialog (Sign In / Register)
    if (showAuthDialog) {
        AuthDialog(
            onDismiss = { showAuthDialog = false },
            onAuthSuccess = { user ->
                showAuthDialog = false
                when (user.role) {
                    UserRole.ADMIN -> currentDestination = AppDestination.ADMIN
                    UserRole.LANDLORD -> currentDestination = AppDestination.LANDLORD
                    UserRole.STUDENT -> currentDestination = AppDestination.STUDENT
                }
            }
        )
    }
}
