package com.example.ui.landlord

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.neon.DocumentType
import com.example.data.neon.NeonRepository
import com.example.model.Amenity
import com.example.model.BookingRequest
import com.example.model.BookingStatus
import com.example.model.ListingStatus
import com.example.model.Property
import com.example.model.RoomType
import com.example.model.VerificationStatus
import com.example.ui.components.BookingStatusBadge
import com.example.ui.components.ListingStatusBadge
import com.example.ui.components.StatCard
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.Amber100
import com.example.ui.theme.Amber50
import com.example.ui.theme.Amber600
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
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.White

@Composable
fun LandlordDashboard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var verificationStatus by remember { mutableStateOf(VerificationStatus.VERIFIED) }
    var showAddPropertyDialog by remember { mutableStateOf(false) }

    // Neon StateFlow subscriptions
    val allProperties by NeonRepository.properties.collectAsState()
    val incomingRequests by NeonRepository.incomingRequests.collectAsState()
    val landlordDocs by NeonRepository.landlordDocuments.collectAsState()
    val galleryPhotos by NeonRepository.propertyGallery.collectAsState()

    val myProperties = allProperties.take(3)
    val activeCount = myProperties.count { it.status == ListingStatus.ACTIVE }
    val newRequestsCount = incomingRequests.count { it.status == BookingStatus.PENDING }
    val confirmedCount = incomingRequests.count { it.status == BookingStatus.CONFIRMED }

    val tabs = listOf(
        "My Properties (${myProperties.size})",
        "Booking Requests (${incomingRequests.size})",
        "Verification & KYC (${landlordDocs.size}/3)",
        "Gallery Photos (${galleryPhotos.size})"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
    ) {
        // 1. LANDLORD HEADER
        item {
            Card(
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                border = BorderStroke(1.dp, Slate200),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Mr. Mwansa Tembo",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Navy900
                                    )
                                )
                                if (verificationStatus == VerificationStatus.VERIFIED) {
                                    VerifiedBadge(label = "Verified Landlord", compact = true)
                                }
                            }
                            Text(
                                text = "Landlord Portal · Neon Postgres Connected",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                            )
                        }

                        Button(
                            onClick = { showAddPropertyDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Navy800),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("landlord_add_property_btn")
                        ) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Property", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3-State Verification Status Simulation Bar
                    VerificationStatusBanner(
                        status = verificationStatus,
                        onToggleSimulation = {
                            verificationStatus = when (verificationStatus) {
                                VerificationStatus.UNDER_REVIEW -> VerificationStatus.VERIFIED
                                VerificationStatus.VERIFIED -> VerificationStatus.REJECTED
                                VerificationStatus.REJECTED -> VerificationStatus.UNDER_REVIEW
                            }
                        }
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
                        title = "Active Listings",
                        value = "$activeCount",
                        subtitle = "${myProperties.size} total listed",
                        icon = Icons.Filled.Apartment,
                        accentColor = Blue600,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Pending Requests",
                        value = "$newRequestsCount",
                        subtitle = "Action required",
                        icon = Icons.Filled.HourglassTop,
                        accentColor = Amber600,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Confirmed Tenants",
                        value = "$confirmedCount",
                        subtitle = "Contacts unlocked",
                        icon = Icons.Filled.CheckCircle,
                        accentColor = Green700,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "KYC Status",
                        value = if (verificationStatus == VerificationStatus.VERIFIED) "Approved" else "In Review",
                        subtitle = "Neon database synced",
                        icon = Icons.Filled.Shield,
                        accentColor = Navy800,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. TABS
        item {
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
                // MY PROPERTIES TAB
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Managed Properties",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Navy900
                            )
                        )
                        Text(
                            text = "Syncing with Neon",
                            style = MaterialTheme.typography.labelSmall.copy(color = Green600, fontWeight = FontWeight.Bold)
                        )
                    }
                }

                items(myProperties, key = { it.id }) { property ->
                    LandlordPropertyItemCard(
                        property = property,
                        onToggleRented = {
                            val newStatus = if (property.status == ListingStatus.ACTIVE) ListingStatus.RENTED else ListingStatus.ACTIVE
                            NeonRepository.updatePropertyStatus(property.id, newStatus)
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            1 -> {
                // BOOKING REQUESTS TAB
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Incoming Booking & Inquiry Requests",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Navy900
                                )
                            )
                            Text(
                                text = "Confirming will trigger real student push notification & reveal phone numbers",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 11.sp)
                            )
                        }
                    }
                }

                items(incomingRequests, key = { it.id }) { request ->
                    LandlordRequestCard(
                        request = request,
                        onConfirm = {
                            NeonRepository.updateBookingStatus(request.id, BookingStatus.CONFIRMED, context)
                        },
                        onDecline = {
                            NeonRepository.updateBookingStatus(request.id, BookingStatus.DECLINED, context)
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            2 -> {
                // VERIFICATION & KYC TAB (Upload handlers for NRC & Ownership)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Blue50,
                            border = BorderStroke(1.dp, Blue100),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = Blue600,
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text(
                                        text = "Zambian Landlord KYC Verification",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Navy900
                                        )
                                    )
                                    Text(
                                        text = "Upload clear images of your Zambian NRC and proof of ownership. Data is encrypted and reviewed by BedSpaceZM Trust & Safety.",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Slate700, fontSize = 12.sp)
                                    )
                                }
                            }
                        }

                        // NRC Front Upload Card
                        DocumentUploadCard(
                            docType = DocumentType.NRC_FRONT,
                            document = landlordDocs.find { it.type == DocumentType.NRC_FRONT },
                            onUploadDocument = { type, fname, size, uri ->
                                NeonRepository.addLandlordDocument(type, fname, size, uri)
                            },
                            onDeleteDocument = { id -> NeonRepository.removeLandlordDocument(id) }
                        )

                        // NRC Back Upload Card
                        DocumentUploadCard(
                            docType = DocumentType.NRC_BACK,
                            document = landlordDocs.find { it.type == DocumentType.NRC_BACK },
                            onUploadDocument = { type, fname, size, uri ->
                                NeonRepository.addLandlordDocument(type, fname, size, uri)
                            },
                            onDeleteDocument = { id -> NeonRepository.removeLandlordDocument(id) }
                        )

                        // Title Deed / Council Rates Upload Card
                        DocumentUploadCard(
                            docType = DocumentType.TITLE_DEED,
                            document = landlordDocs.find { it.type == DocumentType.TITLE_DEED },
                            onUploadDocument = { type, fname, size, uri ->
                                NeonRepository.addLandlordDocument(type, fname, size, uri)
                            },
                            onDeleteDocument = { id -> NeonRepository.removeLandlordDocument(id) }
                        )
                    }
                }
            }

            3 -> {
                // GALLERY PHOTOS TAB
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        PropertyGalleryManager(
                            galleryItems = galleryPhotos,
                            onAddImage = { uri, label ->
                                NeonRepository.addPropertyGalleryItem(uri, label)
                            },
                            onRemoveImage = { id ->
                                NeonRepository.removePropertyGalleryItem(id)
                            },
                            onSetCover = { id ->
                                NeonRepository.setCoverPhoto(id)
                            }
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Add Property Modal Dialog
    if (showAddPropertyDialog) {
        AddPropertyDialog(
            onDismiss = { showAddPropertyDialog = false },
            onAddProperty = { newProp ->
                NeonRepository.addProperty(newProp)
                showAddPropertyDialog = false
            }
        )
    }
}

// 3-State Verification Status Banner
@Composable
private fun VerificationStatusBanner(
    status: VerificationStatus,
    onToggleSimulation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bannerData = when (status) {
        VerificationStatus.UNDER_REVIEW -> QuadBanner(
            bg = Amber50,
            border = Amber100,
            icon = Icons.Filled.HourglassTop,
            title = "Documents Under Admin Review",
            desc = "Your NRC and ownership papers are being verified by BedSpaceZM Trust & Safety (typically within 24 hours).",
            text = Amber700,
            action = "View Review Status"
        )
        VerificationStatus.VERIFIED -> QuadBanner(
            bg = Green50,
            border = Green100,
            icon = Icons.Filled.CheckCircle,
            title = "Verified Landlord Badge Active ✅",
            desc = "All properties display the official 'Verified Landlord' Trust badge. Student bookings enabled.",
            text = Green700,
            action = "Verified"
        )
        VerificationStatus.REJECTED -> QuadBanner(
            bg = Red50,
            border = Red100,
            icon = Icons.Filled.Error,
            title = "Verification Incomplete / Rejected",
            desc = "NRC copy was blurry or name did not match ownership deed. Please re-upload clear photos.",
            text = Red700,
            action = "Re-Upload"
        )
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bannerData.bg,
        border = BorderStroke(1.dp, bannerData.border),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = bannerData.icon,
                    contentDescription = null,
                    tint = bannerData.text,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = bannerData.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = bannerData.text
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = bannerData.desc,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = bannerData.text.copy(alpha = 0.85f),
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tap to test state transition:",
                    style = MaterialTheme.typography.labelSmall.copy(color = Slate500, fontSize = 10.sp)
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = White,
                    border = BorderStroke(1.dp, bannerData.border),
                    modifier = Modifier.clickable(onClick = onToggleSimulation)
                ) {
                    Text(
                        text = "Cycle State (${status.name})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = bannerData.text
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

private data class QuadBanner(
    val bg: Color,
    val border: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val desc: String,
    val text: Color,
    val action: String
)

// Landlord Property Item Card
@Composable
private fun LandlordPropertyItemCard(
    property: Property,
    onToggleRented: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
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
                ListingStatusBadge(status = property.status)
                Text(
                    text = "${property.distanceKm} km from ${property.institution}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Blue600,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = property.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${property.roomType.label} · K${property.priceMonthlyKwacha} / month · ${property.availableRooms}/${property.totalRooms} available",
                style = MaterialTheme.typography.bodySmall.copy(color = Slate700)
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Slate100)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onToggleRented,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = if (property.status == ListingStatus.ACTIVE) "Mark as Rented" else "Mark as Active",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { /* View / Edit */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Navy800),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Edit Details", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

// Landlord Booking Request Item Card with push notification trigger on confirm
@Composable
private fun LandlordRequestCard(
    request: BookingRequest,
    onConfirm: () -> Unit,
    onDecline: () -> Unit,
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Blue50),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = request.studentName.take(1),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Blue600
                        )
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = request.studentName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                    )
                    Text(
                        text = "Interested in: ${request.propertyTitle}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

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

            if (request.status == BookingStatus.PENDING) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDecline,
                        border = BorderStroke(1.dp, Red600),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = null, tint = Red600, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Decline", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Red600))
                    }

                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = Green600),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Confirm Booking", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = White))
                    }
                }
            } else if (request.status == BookingStatus.CONFIRMED) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Green50,
                    border = BorderStroke(1.dp, Green100),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = Green600,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Booking Confirmed — Push Sent & Contacts Shared",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Green700
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Student Phone: ${request.studentPhone} · Email: ${request.studentEmail}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Slate800,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
    }
}

// Add New Property Dialog Modal
@Composable
private fun AddPropertyDialog(
    onDismiss: () -> Unit,
    onAddProperty: (Property) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var institution by remember { mutableStateOf("UNZA") }
    var priceText by remember { mutableStateOf("1800") }
    var distanceText by remember { mutableStateOf("0.8") }
    var roomType by remember { mutableStateOf(RoomType.SINGLE_ROOM) }
    var address by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

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
                        text = "Add New Property",
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

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Property Title (e.g., Campus View Lodge)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Monthly Rent (Kwacha)") },
                    prefix = { Text("K ") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = distanceText,
                    onValueChange = { distanceText = it },
                    label = { Text("Distance from campus (km)") },
                    suffix = { Text(" km") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Physical Address / Area") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & House Rules") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val prop = Property(
                            id = "prop_${System.currentTimeMillis()}",
                            title = title.ifBlank { "New Student Residence" },
                            institution = institution,
                            distanceKm = distanceText.toDoubleOrNull() ?: 1.0,
                            priceMonthlyKwacha = priceText.toIntOrNull() ?: 1800,
                            roomType = roomType,
                            amenities = listOf(Amenity.WIFI, Amenity.WATER, Amenity.SECURITY, Amenity.BACKUP_POWER),
                            rating = null,
                            reviewCount = 0,
                            landlordName = "Mr. Mwansa Tembo",
                            isLandlordVerified = true,
                            address = address.ifBlank { "Near Campus Gate, Lusaka" },
                            description = description.ifBlank { "Brand new student room near campus." },
                            availableRooms = 3,
                            totalRooms = 6,
                            status = ListingStatus.ACTIVE
                        )
                        onAddProperty(prop)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Navy800),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Publish Property Listing to Neon", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = White))
                }
            }
        }
    }
}
