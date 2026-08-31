package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.neon.NeonRepository
import com.example.model.AppDestination
import com.example.ui.theme.Blue100
import com.example.ui.theme.Blue50
import com.example.ui.theme.Blue600
import com.example.ui.theme.Green100
import com.example.ui.theme.Green50
import com.example.ui.theme.Green700
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Red600
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.White

import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import com.example.data.auth.AuthRepository
import com.example.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BedSpaceTopBar(
    currentDestination: AppDestination,
    onNavigate: (AppDestination) -> Unit,
    onOpenChatBot: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onOpenAuth: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showPortalMenu by remember { mutableStateOf(false) }
    var showUserMenu by remember { mutableStateOf(false) }
    val notifications by NeonRepository.notifications.collectAsState()
    val currentUser by AuthRepository.currentUser.collectAsState()
    val unreadCount = notifications.count { !it.isRead }

    Surface(
        color = White,
        border = BorderStroke(1.dp, Slate200),
        shadowElevation = 1.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo
                BedSpaceLogo(
                    modifier = Modifier.clickable { onNavigate(AppDestination.LANDING) },
                    subtitle = "Verified Student Housing"
                )

                // Actions: AI Chatbot, Push Notifs, User Profile / Auth Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Chatbot Trigger
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Blue50,
                        border = BorderStroke(1.dp, Blue100),
                        modifier = Modifier
                            .clickable { onOpenChatBot() }
                            .testTag("topbar_chatbot_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = "AI Advisor",
                                tint = Blue600,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "AI Help",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Blue600,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Notification Bell
                    Box(
                        modifier = Modifier
                            .clickable { onOpenNotifications() }
                            .testTag("topbar_notif_button")
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Navy800,
                            modifier = Modifier.size(20.dp)
                        )
                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(13.dp)
                                    .clip(CircleShape)
                                    .background(Red600),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$unreadCount",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = White,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    // User Profile / Auth Pill
                    Box {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (currentUser != null) {
                                when (currentUser!!.role) {
                                    UserRole.ADMIN -> Slate100
                                    UserRole.LANDLORD -> Green50
                                    UserRole.STUDENT -> Blue50
                                }
                            } else Slate100,
                            border = BorderStroke(1.dp, Slate300),
                            modifier = Modifier
                                .clickable {
                                    if (currentUser == null) onOpenAuth() else showUserMenu = true
                                }
                                .testTag("topbar_user_profile_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (currentUser?.role == UserRole.ADMIN) Icons.Filled.AdminPanelSettings else Icons.Filled.Person,
                                    contentDescription = null,
                                    tint = if (currentUser?.role == UserRole.LANDLORD) Green700 else if (currentUser?.role == UserRole.STUDENT) Blue600 else Navy800,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentUser?.name?.split(" ")?.first() ?: "Sign In",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Navy900,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // User Menu Dropdown
                        DropdownMenu(
                            expanded = showUserMenu,
                            onDismissRequest = { showUserMenu = false },
                            modifier = Modifier.background(White)
                        ) {
                            if (currentUser != null) {
                                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                                    Text(
                                        text = currentUser!!.name,
                                        fontWeight = FontWeight.Bold,
                                        color = Navy900,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${currentUser!!.email} • ${currentUser!!.role.label}",
                                        color = Slate500,
                                        fontSize = 10.sp
                                    )
                                }
                                HorizontalDivider(color = Slate100)
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Filled.Logout, contentDescription = null, tint = Red600, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Sign Out", color = Red600, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    },
                                    onClick = {
                                        AuthRepository.logout()
                                        showUserMenu = false
                                        onNavigate(AppDestination.LANDING)
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Filled.Person, contentDescription = null, tint = Blue600, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Switch User / Sign In", color = Navy900, fontSize = 12.sp)
                                        }
                                    },
                                    onClick = {
                                        showUserMenu = false
                                        onOpenAuth()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Quick Portal Tab Bar for fast role-isolated navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate50)
                    .padding(horizontal = 4.dp, vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                when (currentUser?.role) {
                    UserRole.STUDENT -> {
                        PortalTab(
                            title = "Find Rooms",
                            icon = Icons.Filled.Language,
                            isSelected = currentDestination == AppDestination.LANDING,
                            onClick = { onNavigate(AppDestination.LANDING) },
                            modifier = Modifier.weight(1.1f)
                        )
                        PortalTab(
                            title = "My Dashboard",
                            icon = Icons.Filled.School,
                            isSelected = currentDestination == AppDestination.STUDENT,
                            onClick = { onNavigate(AppDestination.STUDENT) },
                            modifier = Modifier.weight(1.1f)
                        )
                        PortalTab(
                            title = "Help & FAQs",
                            icon = Icons.Filled.HelpOutline,
                            isSelected = currentDestination == AppDestination.FAQS,
                            onClick = { onNavigate(AppDestination.FAQS) },
                            modifier = Modifier.weight(1f)
                        )
                        PortalTab(
                            title = "Privacy",
                            icon = Icons.Filled.Policy,
                            isSelected = currentDestination == AppDestination.PRIVACY_POLICY,
                            onClick = { onNavigate(AppDestination.PRIVACY_POLICY) },
                            modifier = Modifier.weight(0.9f)
                        )
                    }
                    UserRole.LANDLORD -> {
                        PortalTab(
                            title = "My Dashboard",
                            icon = Icons.Filled.Apartment,
                            isSelected = currentDestination == AppDestination.LANDLORD,
                            onClick = { onNavigate(AppDestination.LANDLORD) },
                            modifier = Modifier.weight(1.2f)
                        )
                        PortalTab(
                            title = "Browse Market",
                            icon = Icons.Filled.Language,
                            isSelected = currentDestination == AppDestination.LANDING,
                            onClick = { onNavigate(AppDestination.LANDING) },
                            modifier = Modifier.weight(1.1f)
                        )
                        PortalTab(
                            title = "Landlord FAQs",
                            icon = Icons.Filled.HelpOutline,
                            isSelected = currentDestination == AppDestination.FAQS,
                            onClick = { onNavigate(AppDestination.FAQS) },
                            modifier = Modifier.weight(1f)
                        )
                        PortalTab(
                            title = "Privacy",
                            icon = Icons.Filled.Policy,
                            isSelected = currentDestination == AppDestination.PRIVACY_POLICY,
                            onClick = { onNavigate(AppDestination.PRIVACY_POLICY) },
                            modifier = Modifier.weight(0.9f)
                        )
                    }
                    UserRole.ADMIN -> {
                        PortalTab(
                            title = "Admin Console",
                            icon = Icons.Filled.AdminPanelSettings,
                            isSelected = currentDestination == AppDestination.ADMIN,
                            onClick = { onNavigate(AppDestination.ADMIN) },
                            modifier = Modifier.weight(1.2f)
                        )
                        PortalTab(
                            title = "Market",
                            icon = Icons.Filled.Language,
                            isSelected = currentDestination == AppDestination.LANDING,
                            onClick = { onNavigate(AppDestination.LANDING) },
                            modifier = Modifier.weight(0.9f)
                        )
                        PortalTab(
                            title = "Student",
                            icon = Icons.Filled.School,
                            isSelected = currentDestination == AppDestination.STUDENT,
                            onClick = { onNavigate(AppDestination.STUDENT) },
                            modifier = Modifier.weight(0.9f)
                        )
                        PortalTab(
                            title = "Landlord",
                            icon = Icons.Filled.Apartment,
                            isSelected = currentDestination == AppDestination.LANDLORD,
                            onClick = { onNavigate(AppDestination.LANDLORD) },
                            modifier = Modifier.weight(0.9f)
                        )
                        PortalTab(
                            title = "FAQs",
                            icon = Icons.Filled.HelpOutline,
                            isSelected = currentDestination == AppDestination.FAQS,
                            onClick = { onNavigate(AppDestination.FAQS) },
                            modifier = Modifier.weight(0.8f)
                        )
                    }
                    null -> {
                        // GUEST (Not Logged In)
                        PortalTab(
                            title = "Find Rooms",
                            icon = Icons.Filled.Language,
                            isSelected = currentDestination == AppDestination.LANDING,
                            onClick = { onNavigate(AppDestination.LANDING) },
                            modifier = Modifier.weight(1.2f)
                        )
                        PortalTab(
                            title = "List Property",
                            icon = Icons.Filled.Apartment,
                            isSelected = false,
                            onClick = { onOpenAuth() },
                            modifier = Modifier.weight(1.1f)
                        )
                        PortalTab(
                            title = "Help & FAQs",
                            icon = Icons.Filled.HelpOutline,
                            isSelected = currentDestination == AppDestination.FAQS,
                            onClick = { onNavigate(AppDestination.FAQS) },
                            modifier = Modifier.weight(1f)
                        )
                        PortalTab(
                            title = "Privacy",
                            icon = Icons.Filled.Policy,
                            isSelected = currentDestination == AppDestination.PRIVACY_POLICY,
                            onClick = { onNavigate(AppDestination.PRIVACY_POLICY) },
                            modifier = Modifier.weight(0.9f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PortalTab(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (isSelected) Navy800 else Color.Transparent,
        modifier = modifier
            .padding(horizontal = 2.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 5.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) White else Slate500,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) White else Slate700,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
private fun PortalMenuItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Blue600 else Slate500,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = Navy900
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate500
                        )
                    )
                }
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Selected",
                        tint = Blue600,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        },
        onClick = onClick
    )
}
