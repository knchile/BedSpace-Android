package com.example.ui.components

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.neon.NeonRepository
import com.example.data.neon.PushNotificationRecord
import com.example.ui.theme.Amber100
import com.example.ui.theme.Amber600
import com.example.ui.theme.Blue100
import com.example.ui.theme.Blue50
import com.example.ui.theme.Blue600
import com.example.ui.theme.Green100
import com.example.ui.theme.Green50
import com.example.ui.theme.Green600
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.White
import com.example.util.NotificationHelper

@Composable
fun NotificationCenterDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val notifications by NeonRepository.notifications.collectAsState()
    var hasPermission by remember { mutableStateOf(NotificationHelper.hasNotificationPermission(context)) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .clip(RoundedCornerShape(20.dp))
                    .testTag("notification_center_dialog"),
                color = Slate50,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Slate50)
                ) {
                    // Header
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Navy800
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Blue600),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Push Notification Center",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = White
                                        )
                                    )
                                    Text(
                                        text = "${notifications.size} alerts & inquiries",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.sp)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (notifications.isNotEmpty()) {
                                    IconButton(onClick = { NeonRepository.clearAllNotifications() }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Clear All", tint = Slate300)
                                    }
                                }
                                IconButton(onClick = onDismiss) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = White)
                                }
                            }
                        }
                    }

                    // Notification Permission Banner if not yet granted (Android 13+)
                    if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Amber100
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Enable notifications for instant booking updates",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Amber600, fontWeight = FontWeight.SemiBold),
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Navy800),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Allow", fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Quick Simulation Testing Bar
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Slate100,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "🧪 Instant Push Notification Dispatcher:",
                                style = MaterialTheme.typography.labelSmall.copy(color = Slate700, fontWeight = FontWeight.Bold)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        NotificationHelper.sendStudentBookingConfirmedNotification(
                                            context = context,
                                            propertyTitle = "Campus View Lodge (Room 3A)",
                                            landlordName = "Mr. Mwansa Tembo"
                                        )
                                        NeonRepository.addNotification(
                                            PushNotificationRecord(
                                                recipientRole = "STUDENT",
                                                title = "🎉 Booking Confirmed!",
                                                body = "Mr. Mwansa Tembo approved your booking for Campus View Lodge. Phone & WhatsApp unlocked.",
                                                channel = "BOOKING_CONFIRMATION"
                                            )
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("test_push_booking"),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Student Confirmed", fontSize = 10.sp, maxLines = 1)
                                }

                                OutlinedButton(
                                    onClick = {
                                        NotificationHelper.sendLandlordNewInquiryNotification(
                                            context = context,
                                            studentName = "Thabo Musonda",
                                            propertyTitle = "Olympia Student Haven",
                                            roomType = "Self-Contained Studio"
                                        )
                                        NeonRepository.addNotification(
                                            PushNotificationRecord(
                                                recipientRole = "LANDLORD",
                                                title = "📩 New Student Inquiry",
                                                body = "Thabo Musonda is inquiring about Studio at Olympia Student Haven.",
                                                channel = "INQUIRY"
                                            )
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("test_push_inquiry"),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Landlord Inquiry", fontSize = 10.sp, maxLines = 1)
                                }
                            }
                        }
                    }

                    // Notification History List
                    if (notifications.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = Slate400,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = "No notifications yet",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = Slate500, fontWeight = FontWeight.Medium)
                                )
                                Text(
                                    text = "You will receive push notifications when bookings change.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate400, fontSize = 12.sp)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(notifications, key = { it.id }) { notif ->
                                NotificationCard(
                                    item = notif,
                                    onClick = { NeonRepository.markNotificationAsRead(notif.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    item: PushNotificationRecord,
    onClick: () -> Unit
) {
    val isConfirmed = item.channel == "BOOKING_CONFIRMATION"
    val isInquiry = item.channel == "INQUIRY"
    val isVerif = item.channel == "VERIFICATION"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("notif_item_${item.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (item.isRead) Slate50 else White),
        border = androidx.compose.foundation.BorderStroke(
            width = if (item.isRead) 1.dp else 1.5.dp,
            color = if (item.isRead) Slate200 else when {
                isConfirmed -> Green600.copy(alpha = 0.5f)
                isInquiry -> Blue600.copy(alpha = 0.5f)
                else -> Slate300
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isConfirmed -> Green50
                            isInquiry -> Blue50
                            else -> Slate100
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        isConfirmed -> Icons.Default.CheckCircle
                        isInquiry -> Icons.Default.Email
                        else -> Icons.Default.Security
                    },
                    contentDescription = null,
                    tint = when {
                        isConfirmed -> Green600
                        isInquiry -> Blue600
                        else -> Slate700
                    },
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (item.isRead) FontWeight.SemiBold else FontWeight.Bold,
                            color = Navy900
                        )
                    )
                    Text(
                        text = item.timestamp,
                        style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 10.sp)
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = item.body,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (item.isRead) Slate500 else Slate800,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when (item.recipientRole) {
                                    "STUDENT" -> Blue100
                                    "LANDLORD" -> Green100
                                    else -> Slate200
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "To: ${item.recipientRole}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (item.recipientRole) {
                                    "STUDENT" -> Blue600
                                    "LANDLORD" -> Green600
                                    else -> Slate700
                                }
                            )
                        )
                    }

                    if (!item.isRead) {
                        Text(
                            text = "• Unread",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Blue600,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
