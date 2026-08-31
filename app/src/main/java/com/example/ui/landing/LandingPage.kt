package com.example.ui.landing

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleData
import com.example.model.AppDestination
import com.example.model.Property
import com.example.model.RoomType
import com.example.ui.components.BedSpaceLogo
import com.example.ui.components.ListingCard
import com.example.ui.components.PropertyDetailDialog
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.Amber100
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
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.White

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.example.data.neon.NeonRepository
import com.example.model.BookingRequest
import com.example.model.BookingStatus

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LandingPage(
    onNavigate: (AppDestination) -> Unit,
    onOpenChatBot: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedInstitution by remember { mutableStateOf("All Institutions") }
    var selectedRoomType by remember { mutableStateOf<RoomType?>(null) }
    var maxPriceFilter by remember { mutableFloatStateOf(3000f) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedPropertyForDetail by remember { mutableStateOf<Property?>(null) }

    val properties by NeonRepository.properties.collectAsState()

    // Filter properties based on search panel criteria
    val filteredProperties = remember(properties, selectedInstitution, selectedRoomType, maxPriceFilter, searchQuery) {
        properties.filter { prop ->
            val matchesInstitution = selectedInstitution == "All Institutions" || prop.institution.contains(selectedInstitution, ignoreCase = true) || selectedInstitution.contains(prop.institution, ignoreCase = true)
            val matchesRoomType = selectedRoomType == null || prop.roomType == selectedRoomType
            val matchesPrice = prop.priceMonthlyKwacha <= maxPriceFilter
            val matchesQuery = searchQuery.isBlank() || prop.title.contains(searchQuery, ignoreCase = true) || prop.address.contains(searchQuery, ignoreCase = true)
            matchesInstitution && matchesRoomType && matchesPrice && matchesQuery
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
    ) {
        // 1. HERO SECTION
        item {
            HeroSection(
                selectedInstitution = selectedInstitution,
                onInstitutionChange = { selectedInstitution = it },
                selectedRoomType = selectedRoomType,
                onRoomTypeChange = { selectedRoomType = it },
                maxPrice = maxPriceFilter,
                onMaxPriceChange = { maxPriceFilter = it },
                onSearchClick = {
                    // Filter is already reactive
                },
                onNavigate = onNavigate
            )
        }

        // 2. FEATURED ACCOMMODATION LISTINGS
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "Find your next place to stay",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Browse verified accommodation near your campus.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Slate500
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Quick campus filter chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickFilters = listOf("All Institutions", "UNZA", "CBU", "Evelyn Hone", "Apex", "ZCAS")
                    quickFilters.forEach { inst ->
                        val isSelected = selectedInstitution == inst || (inst == "All Institutions" && selectedInstitution == "All Institutions")
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) Navy800 else White,
                            border = BorderStroke(1.dp, if (isSelected) Navy800 else Slate300),
                            modifier = Modifier.clickable { selectedInstitution = inst }
                        ) {
                            Text(
                                text = inst,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) White else Slate700
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Listing Cards Grid
        items(filteredProperties, key = { it.id }) { property ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                ListingCard(
                    property = property,
                    onViewAccommodation = { selectedPropertyForDetail = it }
                )
            }
        }

        if (filteredProperties.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    border = BorderStroke(1.dp, Slate200),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No accommodations match your filter",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try increasing the price slider or selecting All Institutions.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {
                                selectedInstitution = "All Institutions"
                                selectedRoomType = null
                                maxPriceFilter = 3000f
                            }
                        ) {
                            Text("Reset All Filters")
                        }
                    }
                }
            }
        }

        // 3. TRUST SECTION (Strategically Important)
        item {
            TrustSection(modifier = Modifier.padding(16.dp))
        }

        // 4. HOW IT WORKS SECTION
        item {
            HowItWorksSection(modifier = Modifier.padding(16.dp))
        }

        // 5. SCAM PREVENTION BANNER (Local Zambian student trust)
        item {
            ScamPreventionCard(modifier = Modifier.padding(16.dp))
        }

        // 6. LANDLORD CTA SECTION
        item {
            LandlordCtaSection(
                onListProperty = { onNavigate(AppDestination.LANDLORD) },
                onLearnVerification = { onNavigate(AppDestination.LANDLORD) },
                modifier = Modifier.padding(16.dp)
            )
        }

        // 7. FOOTER SECTION
        item {
            FooterSection(
                onNavigate = onNavigate,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Detail Dialog
    selectedPropertyForDetail?.let { property ->
        PropertyDetailDialog(
            property = property,
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

// 1. HERO SECTION COMPOSABLE
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HeroSection(
    selectedInstitution: String,
    onInstitutionChange: (String) -> Unit,
    selectedRoomType: RoomType?,
    onRoomTypeChange: (RoomType?) -> Unit,
    maxPrice: Float,
    onMaxPriceChange: (Float) -> Unit,
    onSearchClick: () -> Unit,
    onNavigate: (AppDestination) -> Unit
) {
    var expandedInstDropdown by remember { mutableStateOf(false) }
    var expandedRoomDropdown by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Navy900,
                        Navy800
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Pill Badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Blue600.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, Blue600.copy(alpha = 0.4f)),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = null,
                        tint = Green500Override,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "VERIFIED STUDENT ACCOMMODATION YOU CAN TRUST",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }

            // Headline
            Text(
                text = "Find a verified place to stay near campus.",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = White,
                    lineHeight = 34.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Supporting Copy
            Text(
                text = "Discover student accommodation near Zambia's universities and colleges. Every landlord is verified before their listings go live.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Slate300,
                    lineHeight = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Accommodation Search Panel (Card)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                border = BorderStroke(1.dp, Slate200),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_search_panel")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Search Accommodation",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Field 1: Institution
                    Text(
                        text = "1. Select Institution",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Slate700
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = expandedInstDropdown,
                        onExpandedChange = { expandedInstDropdown = !expandedInstDropdown }
                    ) {
                        OutlinedTextField(
                            value = selectedInstitution,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedInstDropdown) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.School,
                                    contentDescription = null,
                                    tint = Navy800,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Blue600,
                                unfocusedBorderColor = Slate300
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedInstDropdown,
                            onDismissRequest = { expandedInstDropdown = false },
                            modifier = Modifier.background(White)
                        ) {
                            SampleData.institutions.forEach { inst ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(inst.name, style = MaterialTheme.typography.bodyMedium)
                                            Text(inst.city, style = MaterialTheme.typography.labelSmall.copy(color = Slate500))
                                        }
                                    },
                                    onClick = {
                                        onInstitutionChange(inst.shortName)
                                        expandedInstDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Field 2: Room Type
                    Text(
                        text = "2. Room Type",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Slate700
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ExposedDropdownMenuBox(
                        expanded = expandedRoomDropdown,
                        onExpandedChange = { expandedRoomDropdown = !expandedRoomDropdown }
                    ) {
                        OutlinedTextField(
                            value = selectedRoomType?.label ?: "All Room Types (Single, Studio, Shared...)",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRoomDropdown) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Apartment,
                                    contentDescription = null,
                                    tint = Navy800,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Blue600,
                                unfocusedBorderColor = Slate300
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedRoomDropdown,
                            onDismissRequest = { expandedRoomDropdown = false },
                            modifier = Modifier.background(White)
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Room Types", style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    onRoomTypeChange(null)
                                    expandedRoomDropdown = false
                                }
                            )
                            RoomType.values().forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.label, style = MaterialTheme.typography.bodyMedium) },
                                    onClick = {
                                        onRoomTypeChange(type)
                                        expandedRoomDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Field 3: Price Range
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "3. Max Price Range",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Slate700
                            )
                        )
                        Text(
                            text = "Up to K${maxPrice.toInt()} / month",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Navy900
                            )
                        )
                    }
                    Slider(
                        value = maxPrice,
                        onValueChange = onMaxPriceChange,
                        valueRange = 800f..3500f,
                        steps = 26,
                        colors = SliderDefaults.colors(
                            thumbColor = Navy800,
                            activeTrackColor = Blue600,
                            inactiveTrackColor = Slate200
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Primary Search Button
                    Button(
                        onClick = onSearchClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Navy800),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("search_rooms_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                            tint = White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Search Rooms",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Value props below search
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Verified landlords  ·  Near campus  ·  Safer booking",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Slate300,
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// 2. TRUST SECTION COMPOSABLE
@Composable
private fun TrustSection(modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, Slate200),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Green50),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = null,
                        tint = Green600,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Accommodation you can trust.",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                    )
                    Text(
                        text = "BedSpaceZM eliminates student accommodation scams in Zambia.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card 1: Verified Landlords
            TrustCardItem(
                icon = Icons.Filled.Verified,
                title = "Verified landlords",
                description = "We verify landlord identity and ownership documentation before properties are listed."
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Card 2: Reviewed Listings
            TrustCardItem(
                icon = Icons.Filled.RateReview,
                title = "Reviewed listings",
                description = "Every property goes through a review process before becoming searchable."
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Card 3: Protected Contact Details
            TrustCardItem(
                icon = Icons.Filled.Lock,
                title = "Protected contact details",
                description = "Landlord contact details are only revealed after a booking request is confirmed."
            )
        }
    }
}

@Composable
private fun TrustCardItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Slate50,
        border = BorderStroke(1.dp, Slate200),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Navy800),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Slate700,
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}

// 3. HOW IT WORKS COMPOSABLE
@Composable
private fun HowItWorksSection(modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, Slate200),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Finding accommodation is simple.",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
            )
            Text(
                text = "Four secure steps to your next room.",
                style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
            )

            Spacer(modifier = Modifier.height(16.dp))

            StepTimelineItem(step = "1", title = "Search", description = "Filter by your campus, preferred room type, and monthly budget.")
            StepTimelineItem(step = "2", title = "Send a request", description = "Submit a booking inquiry directly to the verified landlord.")
            StepTimelineItem(step = "3", title = "Get confirmed", description = "Landlord reviews your student profile and confirms availability.")
            StepTimelineItem(step = "4", title = "Connect with landlord", description = "Official phone and WhatsApp contacts unlock for viewing and move-in.", isLast = true)
        }
    }
}

@Composable
private fun StepTimelineItem(
    step: String,
    title: String,
    description: String,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Navy800),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = step,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                )
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(28.dp)
                        .background(Slate200)
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f).padding(bottom = if (isLast) 0.dp else 12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Slate600
                )
            )
        }
    }
}

private val Slate600 = Color(0xFF475569)

// 4. SCAM PREVENTION BANNER
@Composable
private fun ScamPreventionCard(modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Amber50,
        border = BorderStroke(1.dp, Amber100),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Filled.Policy,
                contentDescription = "Scam alert",
                tint = Amber700,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Scam Prevention Tip for Students",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Amber700
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Never send Airtel Money or MTN MoMo upfront payments to unknown individuals on social media. BedSpaceZM protects you by verifying property ownership before listings are published.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF78350F),
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

// 5. LANDLORD CTA COMPOSABLE
@Composable
private fun LandlordCtaSection(
    onListProperty: () -> Unit,
    onLearnVerification: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Navy800),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Have student accommodation?",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "List your property on BedSpaceZM and reach students looking for verified accommodation near campus.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Slate300,
                    lineHeight = 20.sp
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onListProperty,
                colors = ButtonDefaults.buttonColors(containerColor = Blue600),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "List Your Property",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onLearnVerification,
                border = BorderStroke(1.dp, Slate400),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Learn About Verification",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = White
                    )
                )
            }
        }
    }
}

// 6. FOOTER COMPOSABLE
@Composable
private fun FooterSection(
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Navy900,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            BedSpaceLogo(isDark = true, subtitle = "Verified student accommodation in Zambia.")

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Slate700)
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Marketplace Links",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Slate400,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            val links = listOf(
                "Find Accommodation" to { onNavigate(AppDestination.LANDING) },
                "Student Portal" to { onNavigate(AppDestination.STUDENT) },
                "Landlord Dashboard" to { onNavigate(AppDestination.LANDLORD) },
                "Admin Verification Console" to { onNavigate(AppDestination.ADMIN) },
                "Scam Prevention Guide" to {},
                "Privacy Policy & Terms" to {}
            )

            links.forEach { (label, action) ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate300),
                    modifier = Modifier
                        .clickable(onClick = action)
                        .padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "© 2026 BedSpaceZM. All rights reserved. Built for Zambia's higher education community.",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Slate500,
                    fontSize = 10.sp
                )
            )
        }
    }
}

private val Green500Override = Color(0xFF22C55E)
