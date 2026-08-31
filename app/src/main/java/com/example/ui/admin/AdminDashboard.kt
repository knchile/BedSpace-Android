package com.example.ui.admin

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.SampleData
import com.example.model.LandlordVerificationRecord
import com.example.model.ListingApprovalRecord
import com.example.model.ListingStatus
import com.example.model.VerificationStatus
import com.example.ui.components.ListingStatusBadge
import com.example.ui.components.StatCard
import com.example.ui.components.VerificationStatusBadge
import com.example.ui.theme.Amber200
import com.example.ui.theme.Amber50
import com.example.ui.theme.Amber700
import com.example.ui.theme.Blue100
import com.example.ui.theme.Blue50
import com.example.ui.theme.Blue600
import com.example.ui.theme.Green100
import com.example.ui.theme.Green50
import com.example.ui.theme.Green600
import com.example.ui.theme.Green700
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Red100
import com.example.ui.theme.Red50
import com.example.ui.theme.Red600
import com.example.ui.theme.Red700
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.White

import com.example.data.auth.AuthRepository
import com.example.data.payment.PaymentRepository
import com.example.model.UserRole
import com.example.model.UserStatus
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning

@Composable
fun AdminDashboard(
    modifier: Modifier = Modifier
) {
    val currentUser by AuthRepository.currentUser.collectAsState()
    val allUsers by AuthRepository.registeredUsers.collectAsState()
    val allTransactions by PaymentRepository.transactions.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var verificationQueue by remember { mutableStateOf(SampleData.sampleAdminVerificationQueue) }
    var listingApprovals by remember { mutableStateOf(SampleData.sampleAdminListingApprovals) }
    var selectedVerificationForReview by remember { mutableStateOf<LandlordVerificationRecord?>(null) }

    val pendingVerifCount = verificationQueue.count { it.status == VerificationStatus.UNDER_REVIEW }
    val pendingListingCount = listingApprovals.count { it.status == ListingStatus.PENDING_APPROVAL }
    val totalVolumeKwacha = allTransactions.sumOf { it.amountKwacha }

    val tabs = listOf(
        "Overview",
        "Verification Queue ($pendingVerifCount)",
        "Listing Approvals ($pendingListingCount)",
        "User Accounts (${allUsers.size})",
        "Payments & Escrow (K$totalVolumeKwacha)"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
    ) {
        // 1. ADMIN HEADER
        item {
            Card(
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Blue600),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "BedSpaceZM Operations Console",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = White
                                    )
                                )
                                Text(
                                    text = "Logged in: ${currentUser?.email ?: "knchile@gmail.com"} (${currentUser?.name ?: "Admin"})",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Green100
                                    )
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Green600.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Green600.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "SUPER ADMIN",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Green100,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. ADMIN OVERVIEW STATISTICS
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "Pending Verifications",
                        value = "$pendingVerifCount",
                        subtitle = "Landlord IDs in queue",
                        icon = Icons.Filled.VerifiedUser,
                        accentColor = Amber700,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Awaiting Review",
                        value = "$pendingListingCount",
                        subtitle = "New property listings",
                        icon = Icons.Filled.RateReview,
                        accentColor = Blue600,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "Active Listings",
                        value = "142",
                        subtitle = "Published near campuses",
                        icon = Icons.Filled.Apartment,
                        accentColor = Green600,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Booking Requests",
                        value = "58",
                        subtitle = "This week",
                        icon = Icons.Filled.HourglassTop,
                        accentColor = Navy800,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. ADMIN TABS
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = White,
                contentColor = Navy800,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Navy800
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTabIndex == index) Navy900 else Slate500
                                )
                            )
                        }
                    )
                }
            }
        }

        // TAB VIEWPORT
        when (selectedTabIndex) {
            0, 1 -> {
                // LANDLORD VERIFICATION QUEUE
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Landlord Verification Queue",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Navy900
                                    )
                                )
                                Text(
                                    text = "Review National Registration Cards (NRC) and ownership documents.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                                )
                            }
                        }
                    }
                }

                // Security Banner Notice
                item {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Blue50,
                        border = BorderStroke(1.dp, Blue100),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                tint = Blue600,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Security Protocol: NRC identity records are strictly protected and never exposed in client endpoints.",
                                style = MaterialTheme.typography.labelSmall.copy(color = Slate700)
                            )
                        }
                    }
                }

                items(verificationQueue, key = { it.id }) { record ->
                    AdminVerificationItemCard(
                        record = record,
                        onReview = { selectedVerificationForReview = record },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            2 -> {
                // LISTING APPROVAL QUEUE
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Listing Approval Queue",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Navy900
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Audit accommodation photos, distance, and pricing before publishing.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                        )
                    }
                }

                items(listingApprovals, key = { it.id }) { listing ->
                    AdminListingApprovalItemCard(
                        listing = listing,
                        onApprove = {
                            listingApprovals = listingApprovals.map {
                                if (it.id == listing.id) it.copy(status = ListingStatus.ACTIVE) else it
                            }
                        },
                        onReject = {
                            listingApprovals = listingApprovals.map {
                                if (it.id == listing.id) it.copy(status = ListingStatus.DRAFT) else it
                            }
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            3 -> {
                // USER REGISTRY TAB
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Platform User Registry & Isolation",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Navy900
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "All accounts registered on BedSpaceZM. Each role is securely scoped to their own data.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                items(allUsers, key = { it.id }) { user ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (user.status == UserStatus.BANNED) Red50.copy(alpha = 0.5f) else if (user.status == UserStatus.BLOCKED) Amber50.copy(alpha = 0.5f) else White
                        ),
                        border = BorderStroke(1.dp, if (user.status == UserStatus.BANNED) Red100 else if (user.status == UserStatus.BLOCKED) Amber200 else Slate200),
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
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = user.name,
                                        fontWeight = FontWeight.Bold,
                                        color = Navy900,
                                        fontSize = 14.sp
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (user.role == UserRole.ADMIN) Slate100 else if (user.role == UserRole.LANDLORD) Green50 else Blue50
                                    ) {
                                        Text(
                                            text = user.role.label,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            color = if (user.role == UserRole.LANDLORD) Green700 else if (user.role == UserRole.STUDENT) Blue600 else Navy900,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                // Status Badge
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = when (user.status) {
                                        UserStatus.ACTIVE -> Green50
                                        UserStatus.BLOCKED -> Amber50
                                        UserStatus.BANNED -> Red50
                                    },
                                    border = BorderStroke(
                                        1.dp,
                                        when (user.status) {
                                            UserStatus.ACTIVE -> Green600.copy(alpha = 0.3f)
                                            UserStatus.BLOCKED -> Amber700.copy(alpha = 0.3f)
                                            UserStatus.BANNED -> Red600.copy(alpha = 0.3f)
                                        }
                                    )
                                ) {
                                    Text(
                                        text = user.status.label.uppercase(),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 9.sp,
                                        color = when (user.status) {
                                            UserStatus.ACTIVE -> Green700
                                            UserStatus.BLOCKED -> Amber700
                                            UserStatus.BANNED -> Red600
                                        },
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${user.email} • ${user.phone}",
                                fontSize = 11.sp,
                                color = Slate600
                            )
                            if (user.institution != null) {
                                Text(
                                    text = "Campus: ${user.institution}",
                                    fontSize = 11.sp,
                                    color = Slate700
                                )
                            }
                            if (user.nrcNumber != null) {
                                Text(
                                    text = "NRC ID: ${user.nrcNumber}",
                                    fontSize = 11.sp,
                                    color = Slate700
                                )
                            }
                            if (user.socialProvider != null) {
                                Text(
                                    text = "Signed up via: ${user.socialProvider}",
                                    fontSize = 10.sp,
                                    color = Blue600,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            if (user.blockReason != null) {
                                Text(
                                    text = "Reason: ${user.blockReason}",
                                    fontSize = 10.sp,
                                    color = Red600,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Admin Actions for non-admin accounts
                            if (user.role != UserRole.ADMIN) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (user.status == UserStatus.ACTIVE) {
                                        OutlinedButton(
                                            onClick = {
                                                AuthRepository.blockUser(user.id, "Violation of BedSpaceZM security policies")
                                            },
                                            shape = RoundedCornerShape(6.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Amber700),
                                            border = BorderStroke(1.dp, Amber700),
                                            modifier = Modifier.weight(1f).height(32.dp)
                                        ) {
                                            Icon(Icons.Filled.Block, contentDescription = null, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Block User", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                AuthRepository.banUser(user.id, "Permanent ban for fraudulent activity")
                                            },
                                            shape = RoundedCornerShape(6.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Red600),
                                            border = BorderStroke(1.dp, Red600),
                                            modifier = Modifier.weight(1f).height(32.dp)
                                        ) {
                                            Icon(Icons.Filled.Warning, contentDescription = null, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Ban Account", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Button(
                                            onClick = {
                                                AuthRepository.unblockUser(user.id)
                                            },
                                            shape = RoundedCornerShape(6.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Green700),
                                            modifier = Modifier.weight(1f).height(32.dp)
                                        ) {
                                            Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(12.dp), tint = White)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Unblock / Restore", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = White)
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            AuthRepository.deleteUser(user.id)
                                        },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate600),
                                        border = BorderStroke(1.dp, Slate300),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Icon(Icons.Filled.DeleteForever, contentDescription = "Delete", modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            4 -> {
                // PAYMENTS & ESCROW LEDGER TAB
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Green50,
                            border = BorderStroke(1.dp, Green100),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Mobile Money Escrow Balance",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Green700
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Total Gross Transaction Volume: ZMW K$totalVolumeKwacha",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Navy900
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Instant settlements via Airtel Money, MTN MoMo, Zamtel Kwacha, and Visa/Mastercard.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate700)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Live Payment Gateway Transactions",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Navy900
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                if (allTransactions.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = White),
                            border = BorderStroke(1.dp, Slate200),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No Transactions Logged", fontWeight = FontWeight.Bold, color = Navy900)
                                Text("Payments made by students on mobile money will show here.", color = Slate500, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    items(allTransactions, key = { it.id }) { tx ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
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
                                    Text(
                                        text = "${tx.studentName} ➔ ${tx.landlordName}",
                                        fontWeight = FontWeight.Bold,
                                        color = Navy900,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "ZMW K${tx.amountKwacha}",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Green700,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Property: ${tx.propertyTitle} (${tx.paymentType.label})",
                                    fontSize = 11.sp,
                                    color = Slate700
                                )
                                Text(
                                    text = "Gateway: ${tx.provider.label} • Ref: ${tx.referenceCode} • ${tx.dateFormatted}",
                                    fontSize = 10.sp,
                                    color = Slate500
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Verification Review Modal
    selectedVerificationForReview?.let { record ->
        AdminVerificationReviewDialog(
            record = record,
            onDismiss = { selectedVerificationForReview = null },
            onApprove = {
                verificationQueue = verificationQueue.map {
                    if (it.id == record.id) it.copy(status = VerificationStatus.VERIFIED) else it
                }
                selectedVerificationForReview = null
            },
            onReject = { reason ->
                verificationQueue = verificationQueue.map {
                    if (it.id == record.id) it.copy(status = VerificationStatus.REJECTED, rejectionReason = reason) else it
                }
                selectedVerificationForReview = null
            }
        )
    }
}

// Admin Verification Card Item
@Composable
private fun AdminVerificationItemCard(
    record: LandlordVerificationRecord,
    onReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, Slate200),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                VerificationStatusBadge(status = record.status)
                Text(
                    text = "Submitted: ${record.submissionDate}",
                    style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = record.landlordName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Property: ${record.propertyName} · Area: ${record.institutionArea}",
                style = MaterialTheme.typography.bodySmall.copy(color = Slate700)
            )

            Text(
                text = "NRC No: ${record.nrcNumber} · Phone: ${record.phone}",
                style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
            )

            if (record.rejectionReason != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Red50,
                    border = BorderStroke(1.dp, Red100),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Admin Note: ${record.rejectionReason}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Red700),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Slate100)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "2 Docs: NRC + Council Rates",
                    style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
                )

                Button(
                    onClick = onReview,
                    colors = ButtonDefaults.buttonColors(containerColor = Navy800),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = "Review Documents",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = White)
                    )
                }
            }
        }
    }
}

// Admin Listing Approval Card Item
@Composable
private fun AdminListingApprovalItemCard(
    listing: ListingApprovalRecord,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, Slate200),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ListingStatusBadge(status = listing.status)
                Text(
                    text = "Submitted: ${listing.submittedDate}",
                    style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = listing.propertyTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Landlord: ${listing.landlordName} · Campus: ${listing.institution} · K${listing.priceMonthlyKwacha} / month (${listing.roomType.label})",
                style = MaterialTheme.typography.bodySmall.copy(color = Slate700)
            )

            if (listing.status == ListingStatus.PENDING_APPROVAL) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        border = BorderStroke(1.dp, Red600),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f).height(36.dp)
                    ) {
                        Text("Reject", style = MaterialTheme.typography.labelSmall.copy(color = Red600))
                    }
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = Green600),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f).height(36.dp)
                    ) {
                        Text("Approve Listing", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = White))
                    }
                }
            }
        }
    }
}

// Verification Review Full Dialog
@Composable
private fun AdminVerificationReviewDialog(
    record: LandlordVerificationRecord,
    onDismiss: () -> Unit,
    onApprove: () -> Unit,
    onReject: (String) -> Unit
) {
    var rejectReason by remember { mutableStateOf("") }
    var showRejectInput by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = White,
            border = BorderStroke(1.dp, Slate200),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Landlord Verification Review",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Landlord Info
                Text(text = "Landlord: ${record.landlordName}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                Text(text = "NRC Number: ${record.nrcNumber}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Phone: ${record.phone}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Property Name: ${record.propertyName}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Submission Date: ${record.submissionDate}", style = MaterialTheme.typography.bodySmall.copy(color = Slate500))

                Spacer(modifier = Modifier.height(16.dp))

                // Protected Document Preview 1: NRC
                Text(
                    text = "1. National Registration Card (NRC)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Navy900)
                )
                Spacer(modifier = Modifier.height(4.dp))
                AdminDocPreviewBox(
                    label = record.nrcDocumentName,
                    type = "Official Zambian NRC Identity Scan"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Protected Document Preview 2: Ownership
                Text(
                    text = "2. Property Ownership Document",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Navy900)
                )
                Spacer(modifier = Modifier.height(4.dp))
                AdminDocPreviewBox(
                    label = record.ownershipDocumentName,
                    type = "Ministry of Lands Title Deed / Council Rates Receipt"
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (showRejectInput) {
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        label = { Text("Reason for rejection / Required correction") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (rejectReason.isNotBlank()) {
                                onReject(rejectReason)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Red600),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirm Rejection with Note", color = White)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showRejectInput = true },
                            border = BorderStroke(1.dp, Red600),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Text("Reject Documents", color = Red600)
                        }

                        Button(
                            onClick = onApprove,
                            colors = ButtonDefaults.buttonColors(containerColor = Green600),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Approve Landlord", color = White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminDocPreviewBox(
    label: String,
    type: String
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Slate100,
        border = BorderStroke(1.dp, Slate300),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Description,
                contentDescription = null,
                tint = Navy800,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Navy900)
                )
                Text(
                    text = type,
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 11.sp)
                )
            }
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Green50,
                border = BorderStroke(1.dp, Green100)
            ) {
                Text(
                    text = "Encrypted",
                    style = MaterialTheme.typography.labelSmall.copy(color = Green700, fontSize = 10.sp),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

// Institutions Directory Section
@Composable
private fun AdminInstitutionsSection(modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, Slate200),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Partner Universities & Colleges",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            SampleData.institutions.filter { it.id != "all" }.forEach { inst ->
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Slate50,
                    border = BorderStroke(1.dp, Slate200),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.School,
                            contentDescription = null,
                            tint = Navy800,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = inst.name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Navy900)
                            )
                            Text(
                                text = inst.city,
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Audit Log Section
@Composable
private fun AdminAuditLogSection(modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, Slate200),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Platform Moderation Audit Log",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

            val logs = listOf(
                "30 Aug 17:42: Admin verified Landlord Beauty Zulu (NRC 184920/65/1)",
                "30 Aug 14:15: New property 'Campus View Lodge' submitted for UNZA area",
                "29 Aug 09:30: Landlord Kelvin Chilufya document rejected: expired lease agreement",
                "28 Aug 11:20: Student Thabo Musonda booking request confirmed for Room 4"
            )

            logs.forEach { log ->
                Text(
                    text = "• $log",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate700),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
