package com.example.ui.student

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.neon.NeonRepository
import com.example.model.BookingRequest
import com.example.model.BookingStatus
import com.example.model.Property
import com.example.ui.components.BookingStatusBadge
import com.example.ui.components.ListingCard
import com.example.ui.components.PropertyDetailDialog
import com.example.ui.components.ProtectedContactPanel
import com.example.ui.components.StatCard
import com.example.ui.theme.Blue100
import com.example.ui.theme.Blue50
import com.example.ui.theme.Blue600
import com.example.ui.theme.Green50
import com.example.ui.theme.Green700
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

@Composable
fun StudentDashboard(
    onOpenChatBot: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedPropertyForDetail by remember { mutableStateOf<Property?>(null) }

    // Neon StateFlow
    val properties by NeonRepository.properties.collectAsState()
    val studentRequests by NeonRepository.studentRequests.collectAsState()

    val savedCount = 4
    val pendingRequestsCount = studentRequests.count { it.status == BookingStatus.PENDING }
    val confirmedCount = studentRequests.count { it.status == BookingStatus.CONFIRMED }

    val tabs = listOf("Overview", "Recommended", "My Requests (${studentRequests.size})", "Profile")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
    ) {
        // 1. STUDENT DASHBOARD HEADER
        item {
            Card(
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                border = BorderStroke(1.dp, Slate200),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Good morning, Thabo 👋",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Navy900
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Find verified accommodation near your campus.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Slate500
                                )
                            )
                        }

                        // Student Avatar with Institution Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Blue50,
                            border = BorderStroke(1.dp, Blue100)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Blue600),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "TM",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "UNZA Student",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Navy900
                                        )
                                    )
                                    Text(
                                        text = "Great East Rd",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Slate500,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Inline Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = "Search by area, price (e.g. K1500), wifi...",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Slate400)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                tint = Slate400,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Slate50,
                            unfocusedContainerColor = Slate50,
                            focusedBorderColor = Blue600,
                            unfocusedBorderColor = Slate300
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("student_search_input")
                    )
                }
            }
        }

        // 2. STATS OVERVIEW CARDS
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Saved Places",
                        value = "$savedCount",
                        subtitle = "Quick viewing list",
                        icon = Icons.Filled.Bookmark,
                        accentColor = Blue600,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Active Requests",
                        value = "$pendingRequestsCount",
                        subtitle = "Awaiting landlord review",
                        icon = Icons.Filled.HourglassTop,
                        accentColor = Slate700,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Confirmed Bookings",
                        value = "$confirmedCount",
                        subtitle = "Phone numbers unlocked",
                        icon = Icons.Filled.CheckCircle,
                        accentColor = Green700,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Scam Protection",
                        value = "100%",
                        subtitle = "Verified landlords only",
                        icon = Icons.Filled.Lock,
                        accentColor = Blue600,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // AI Advisor Prompt Card
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Blue50,
                border = BorderStroke(1.dp, Blue100),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onOpenChatBot() }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Blue600),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.SmartToy, contentDescription = null, tint = White, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(
                                text = "Need Help Picking a Room?",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Navy900)
                            )
                            Text(
                                text = "Chat with BedSpace AI to compare boarding houses & prices",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate600, fontSize = 11.sp)
                            )
                        }
                    }
                    Button(
                        onClick = onOpenChatBot,
                        colors = ButtonDefaults.buttonColors(containerColor = Navy800),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Chat Now", fontSize = 11.sp)
                    }
                }
            }
        }

        // 3. TABS
        item {
            Spacer(modifier = Modifier.height(14.dp))
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = White,
                contentColor = Blue600,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Blue600
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTabIndex == index) Blue600 else Slate500
                                )
                            )
                        }
                    )
                }
            }
        }

        // 4. TAB CONTENTS
        when (selectedTabIndex) {
            0 -> {
                // OVERVIEW TAB: Recent Requests & Recommended
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Your Recent Inquiries & Bookings",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                items(studentRequests.take(2), key = { it.id }) { request ->
                    StudentRequestCard(
                        request = request,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Recommended for UNZA Students",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                items(properties.take(3), key = { it.id }) { prop ->
                    ListingCard(
                        property = prop,
                        onViewAccommodation = { selectedPropertyForDetail = it },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            1 -> {
                // RECOMMENDED TAB
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Verified Boarding Houses Near UNZA",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                items(properties, key = { it.id }) { prop ->
                    ListingCard(
                        property = prop,
                        onViewAccommodation = { selectedPropertyForDetail = it },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            2 -> {
                // MY REQUESTS TAB
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "All Booking Requests & Inquiries",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Phone numbers are revealed automatically when verified landlords confirm your booking.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate500),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                items(studentRequests, key = { it.id }) { request ->
                    StudentRequestCard(
                        request = request,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            3 -> {
                // PROFILE TAB
                item {
                    StudentProfileCard(
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Property Detail Modal
    if (selectedPropertyForDetail != null) {
        PropertyDetailDialog(
            property = selectedPropertyForDetail!!,
            onDismiss = { selectedPropertyForDetail = null },
            onRequestBooking = { prop, message ->
                val newReq = BookingRequest(
                    id = "req_${System.currentTimeMillis()}",
                    propertyId = prop.id,
                    propertyTitle = prop.title,
                    institution = prop.institution,
                    roomType = prop.roomType,
                    monthlyPrice = prop.priceMonthlyKwacha,
                    studentName = "Thabo Musonda",
                    studentPhone = "+260 97 112 3344",
                    studentEmail = "thabo.unza@gmail.com",
                    landlordName = prop.landlordName,
                    landlordPhone = "+260 96 688 2244",
                    landlordWhatsapp = "+260 96 688 2244",
                    status = BookingStatus.PENDING,
                    requestDate = "Today",
                    message = message
                )
                NeonRepository.addStudentRequest(newReq, context)
                selectedPropertyForDetail = null
            }
        )
    }
}

// Student Request Item Card
@Composable
private fun StudentRequestCard(
    request: BookingRequest,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, Slate200),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BookingStatusBadge(status = request.status)
                Text(
                    text = request.requestDate,
                    style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = request.propertyTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${request.roomType.label} · Landlord: ${request.landlordName}",
                style = MaterialTheme.typography.bodySmall.copy(color = Slate700)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Slate50,
                border = BorderStroke(1.dp, Slate200),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "\"${request.message}\"",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate700),
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            ProtectedContactPanel(
                landlordName = request.landlordName,
                phone = request.landlordPhone,
                whatsapp = request.landlordWhatsapp,
                isConfirmed = request.status == BookingStatus.CONFIRMED
            )
        }
    }
}

@Composable
private fun StudentProfileCard(modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, Slate200),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Student Profile",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Name: Thabo Musonda", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Institution: University of Zambia (UNZA)", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Phone: +260 97 112 3344", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Email: thabo.unza@gmail.com", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
