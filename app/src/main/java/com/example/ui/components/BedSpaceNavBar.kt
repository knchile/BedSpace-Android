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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BedSpaceTopBar(
    currentDestination: AppDestination,
    onNavigate: (AppDestination) -> Unit,
    onOpenChatBot: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showPortalMenu by remember { mutableStateOf(false) }
    val notifications by NeonRepository.notifications.collectAsState()
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
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo
                BedSpaceLogo(
                    modifier = Modifier.clickable { onNavigate(AppDestination.LANDING) },
                    subtitle = "Verified Student Housing"
                )

                // Actions: AI Chatbot, Push Notifs, Portal Switcher
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
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = "AI Advisor",
                                tint = Blue600,
                                modifier = Modifier.size(15.dp)
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
                            modifier = Modifier.size(22.dp)
                        )
                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Red600),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$unreadCount",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    // Portal Switcher Pill Button
                    Box {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = when (currentDestination) {
                                AppDestination.LANDING -> Slate100
                                AppDestination.STUDENT -> Blue50
                                AppDestination.LANDLORD -> Green50
                                AppDestination.ADMIN -> Slate100
                            },
                            border = BorderStroke(
                                1.dp,
                                when (currentDestination) {
                                    AppDestination.LANDING -> Slate300
                                    AppDestination.STUDENT -> Blue100
                                    AppDestination.LANDLORD -> Green100
                                    AppDestination.ADMIN -> Slate300
                                }
                            ),
                            modifier = Modifier
                                .clickable { showPortalMenu = true }
                                .testTag("portal_switcher_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (currentDestination) {
                                        AppDestination.LANDING -> Icons.Filled.Language
                                        AppDestination.STUDENT -> Icons.Filled.School
                                        AppDestination.LANDLORD -> Icons.Filled.Apartment
                                        AppDestination.ADMIN -> Icons.Filled.AdminPanelSettings
                                    },
                                    contentDescription = null,
                                    tint = when (currentDestination) {
                                        AppDestination.LANDING -> Slate700
                                        AppDestination.STUDENT -> Blue600
                                        AppDestination.LANDLORD -> Green700
                                        AppDestination.ADMIN -> Navy800
                                    },
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when (currentDestination) {
                                        AppDestination.LANDING -> "Public"
                                        AppDestination.STUDENT -> "Student"
                                        AppDestination.LANDLORD -> "Landlord"
                                        AppDestination.ADMIN -> "Admin"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Navy900,
                                        fontSize = 11.sp
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = Slate500,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Dropdown menu to switch UX Reference Screens
                        DropdownMenu(
                            expanded = showPortalMenu,
                            onDismissRequest = { showPortalMenu = false },
                            modifier = Modifier.background(White)
                        ) {
                            PortalMenuItem(
                                title = "1. Public Landing Page (/)",
                                subtitle = "Public student accommodation search",
                                icon = Icons.Filled.Language,
                                isSelected = currentDestination == AppDestination.LANDING,
                                onClick = {
                                    onNavigate(AppDestination.LANDING)
                                    showPortalMenu = false
                                }
                            )
                            HorizontalDivider(color = Slate100)
                            PortalMenuItem(
                                title = "2. Student Dashboard (/student/dashboard)",
                                subtitle = "Saved accommodation & booking requests",
                                icon = Icons.Filled.School,
                                isSelected = currentDestination == AppDestination.STUDENT,
                                onClick = {
                                    onNavigate(AppDestination.STUDENT)
                                    showPortalMenu = false
                                }
                            )
                            HorizontalDivider(color = Slate100)
                            PortalMenuItem(
                                title = "3. Landlord Dashboard (/landlord/dashboard)",
                                subtitle = "Verification, listings & request approvals",
                                icon = Icons.Filled.Apartment,
                                isSelected = currentDestination == AppDestination.LANDLORD,
                                onClick = {
                                    onNavigate(AppDestination.LANDLORD)
                                    showPortalMenu = false
                                }
                            )
                            HorizontalDivider(color = Slate100)
                            PortalMenuItem(
                                title = "4. Admin Console (/admin)",
                                subtitle = "Verification queues & listing approvals",
                                icon = Icons.Filled.AdminPanelSettings,
                                isSelected = currentDestination == AppDestination.ADMIN,
                                onClick = {
                                    onNavigate(AppDestination.ADMIN)
                                    showPortalMenu = false
                                }
                            )
                        }
                    }
                }
            }

            // Quick Portal Tab Bar for fast one-tap navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate50)
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PortalTab(
                    title = "Landing",
                    icon = Icons.Filled.Language,
                    isSelected = currentDestination == AppDestination.LANDING,
                    onClick = { onNavigate(AppDestination.LANDING) },
                    modifier = Modifier.weight(1f)
                )
                PortalTab(
                    title = "Student",
                    icon = Icons.Filled.School,
                    isSelected = currentDestination == AppDestination.STUDENT,
                    onClick = { onNavigate(AppDestination.STUDENT) },
                    modifier = Modifier.weight(1f)
                )
                PortalTab(
                    title = "Landlord",
                    icon = Icons.Filled.Apartment,
                    isSelected = currentDestination == AppDestination.LANDLORD,
                    onClick = { onNavigate(AppDestination.LANDLORD) },
                    modifier = Modifier.weight(1f)
                )
                PortalTab(
                    title = "Admin",
                    icon = Icons.Filled.AdminPanelSettings,
                    isSelected = currentDestination == AppDestination.ADMIN,
                    onClick = { onNavigate(AppDestination.ADMIN) },
                    modifier = Modifier.weight(1f)
                )
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
