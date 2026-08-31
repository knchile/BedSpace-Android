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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.AuthRepository
import com.example.data.neon.NeonRepository
import com.example.data.payment.PaymentRepository
import com.example.model.BookingRequest
import com.example.model.BookingStatus
import com.example.model.Property
import com.example.ui.components.BookingStatusBadge
import com.example.ui.components.ListingCard
import com.example.ui.components.PropertyDetailDialog
import com.example.ui.components.ProtectedContactPanel
import com.example.ui.components.StatCard
import com.example.ui.payment.PaymentDialog
import com.example.ui.theme.Blue100
import com.example.ui.theme.Blue50
import com.example.ui.theme.Blue600
import com.example.ui.theme.Green100
import com.example.ui.theme.Green50
import com.example.ui.theme.Green700
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.White

@Composable
fun StudentDashboard(
    onOpenChatBot: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by AuthRepository.currentUser.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedPropertyForDetail by remember { mutableStateOf<Property?>(null) }
    var selectedPropertyForPayment by remember { mutableStateOf<Pair<Property, String>?>(null) }

    // Neon & Payment StateFlows
    val properties by NeonRepository.properties.collectAsState()
    val allStudentRequests by NeonRepository.studentRequests.collectAsState()
    val allTransactions by PaymentRepository.transactions.collectAsState()

    // Filter requests strictly to the logged-in student (account isolation)
    val userEmail = currentUser?.email?.lowercase() ?: ""
    val userName = currentUser?.name?.lowercase() ?: ""
    val studentRequests = remember(allStudentRequests, userEmail, userName) {
        if (currentUser == null) emptyList()
        else allStudentRequests.filter { req ->
            req.studentEmail.lowercase() == userEmail || req.studentName.lowercase() == userName
        }
    }

    val studentTransactions = remember(allTransactions, currentUser) {
        if (currentUser == null) emptyList()
        else allTransactions.filter { 
            it.studentId == currentUser!!.id || it.studentName.equals(currentUser!!.name, ignoreCase = true) 
        }
    }

    val pendingRequestsCount = studentRequests.count { it.status == BookingStatus.PENDING }
    val confirmedCount = studentRequests.count { it.status == BookingStatus.CONFIRMED }

    val tabs = listOf("Overview", "Recommended", "My Bookings (${studentRequests.size})", "Payments (${studentTransactions.size})", "Profile")

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
                                text = "Welcome, ${currentUser?.name ?: "Student"} 👋",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Navy900
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Your personal student housing & payment portal.",
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
                                        text = (currentUser?.name?.take(2)?.uppercase()) ?: "ST",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = currentUser?.institution ?: "Student",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Navy900
                                        )
                                    )
                                    Text(
                                        text = "Active Account",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Green700,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. SUMMARY METRIC STATS ROW
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "Pending",
                    value = "$pendingRequestsCount",
                    icon = Icons.Filled.HourglassTop,
                    accentColor = Blue600,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Confirmed",
                    value = "$confirmedCount",
                    icon = Icons.Filled.CheckCircle,
                    accentColor = Green700,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Payments",
                    value = "${studentTransactions.size}",
                    icon = Icons.Filled.AccountBalanceWallet,
                    accentColor = Blue600,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. TABS HEADER
        item {
            Spacer(modifier = Modifier.height(14.dp))
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = White,
                contentColor = Blue600,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Blue600
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(10.dp))
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
                // OVERVIEW TAB
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

                if (studentRequests.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = White),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Slate200),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No Active Bookings Yet", fontWeight = FontWeight.Bold, color = Navy900)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Browse verified rooms below and send an inquiry or reservation request.",
                                    color = Slate500,
                                    fontSize = 12.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(studentRequests.take(2), key = { it.id }) { request ->
                        StudentRequestCard(
                            request = request,
                            onPay = {
                                val prop = properties.find { it.id == request.propertyId } ?: properties.first()
                                selectedPropertyForPayment = Pair(prop, request.id)
                            },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Recommended Accommodation",
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
                        text = "Verified Boarding Houses Near Campus",
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
                        text = "Your Isolated Booking Requests",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Only you can see these requests. Landlord phone numbers unlock automatically upon booking confirmation or payment.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate500),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                if (studentRequests.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = White),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Slate200),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No requests sent yet", fontWeight = FontWeight.Bold, color = Navy900)
                                Text("Select an accommodation from the Recommended tab to send a booking request.", color = Slate500, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    items(studentRequests, key = { it.id }) { request ->
                        StudentRequestCard(
                            request = request,
                            onPay = {
                                val prop = properties.find { it.id == request.propertyId } ?: properties.first()
                                selectedPropertyForPayment = Pair(prop, request.id)
                            },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            3 -> {
                // PAYMENTS TAB
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Mobile Money Payment History",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Official digital receipts for Airtel Money, MTN MoMo, and Card transactions.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate500),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                if (studentTransactions.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = White),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Slate200),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.Receipt, contentDescription = null, tint = Slate500, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No Mobile Money Payments Yet", fontWeight = FontWeight.Bold, color = Navy900)
                                Text("When you pay a K200 reservation deposit or monthly rent, receipts appear here.", color = Slate500, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    items(studentTransactions, key = { it.id }) { tx ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = White),
                            border = BorderStroke(1.dp, Slate200),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Green50,
                                        border = BorderStroke(1.dp, Green100)
                                    ) {
                                        Text(
                                            text = tx.status.label,
                                            color = Green700,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Text(
                                        text = tx.dateFormatted,
                                        style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = tx.propertyTitle,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Navy900)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Amount: ZMW K${tx.amountKwacha} • Paid via ${tx.provider.label}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate700)
                                )
                                Text(
                                    text = "Ref: ${tx.referenceCode} • Landlord: ${tx.landlordName}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
                                )
                            }
                        }
                    }
                }
            }

            4 -> {
                // PROFILE TAB
                item {
                    StudentProfileCard(
                        user = currentUser,
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
                    studentName = currentUser?.name ?: "Student User",
                    studentPhone = currentUser?.phone ?: "+260 97 000 0000",
                    studentEmail = currentUser?.email ?: "student@bedspace.zm",
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

    // Mobile Money Payment Dialog
    if (selectedPropertyForPayment != null) {
        val (prop, bookingId) = selectedPropertyForPayment!!
        PaymentDialog(
            property = prop,
            bookingId = bookingId,
            onDismiss = { selectedPropertyForPayment = null },
            onPaymentSuccess = { _ ->
                selectedPropertyForPayment = null
            }
        )
    }
}

// Student Request Item Card
@Composable
private fun StudentRequestCard(
    request: BookingRequest,
    onPay: () -> Unit = {},
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
                text = "${request.roomType.label} · Landlord: ${request.landlordName} · K${request.monthlyPrice}/mo",
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

            Spacer(modifier = Modifier.height(10.dp))

            // Mobile Money Payment Action Button
            Button(
                onClick = onPay,
                colors = ButtonDefaults.buttonColors(containerColor = if (request.status == BookingStatus.CONFIRMED) Green700 else Navy800),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (request.status == BookingStatus.CONFIRMED) "Pay First Month Rent (Airtel / MTN)" else "Pay K200 Reservation Deposit",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun StudentProfileCard(
    user: com.example.model.User?,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, Slate200),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Your Student Account",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Full Name: ${user?.name ?: "Student"}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Email: ${user?.email ?: "student@bedspace.zm"}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Institution: ${user?.institution ?: "University of Zambia (UNZA)"}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Phone: ${user?.phone ?: "+260 97 000 0000"}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Student ID: ${user?.studentId ?: "2026/088"}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Status: Verified Student", style = MaterialTheme.typography.bodyMedium, color = Green700, fontWeight = FontWeight.Bold)
        }
    }
}
