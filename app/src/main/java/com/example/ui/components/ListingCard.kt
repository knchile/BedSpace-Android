package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Property
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
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.White

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ListingCard(
    property: Property,
    onViewAccommodation: (Property) -> Unit,
    modifier: Modifier = Modifier,
    onSaveToggle: ((Property) -> Unit)? = null,
    isSaved: Boolean = false
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, Slate200),
        modifier = modifier
            .fillMaxWidth()
            .testTag("listing_card_${property.id}")
    ) {
        Column {
            // Property Image Container with Verified Badge Overlay
            PropertyImageContainer(
                title = property.title,
                institution = property.institution,
                badgeContent = {
                    if (property.isLandlordVerified) {
                        VerifiedBadge(label = "Verified Landlord", compact = true)
                    }
                }
            )

            // Card Body
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                // Institution & Distance Tag
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = Blue600,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${property.distanceKm} km from ${property.institution}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Blue600,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    // Rating if available
                    if (property.rating != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Slate100)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${property.rating}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Slate800
                                )
                            )
                            Text(
                                text = " (${property.reviewCount})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Slate500,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Title
                Text(
                    text = property.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Room Type & Availability
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.MeetingRoom,
                        contentDescription = null,
                        tint = Slate500,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = property.roomType.label,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Slate700,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = " · ${property.availableRooms} rooms open",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (property.availableRooms > 0) Green700 else Slate500,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Key Amenities Chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    property.amenities.take(3).forEach { amenity ->
                        AmenityChip(amenity = amenity)
                    }
                    if (property.amenities.size > 3) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Slate100,
                            border = BorderStroke(1.dp, Slate200)
                        ) {
                            Text(
                                text = "+${property.amenities.size - 3} more",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Slate500,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Slate200)
                Spacer(modifier = Modifier.height(12.dp))

                // Price and Action Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "K${property.priceMonthlyKwacha}",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Navy900
                                )
                            )
                            Text(
                                text = " / month",
                                modifier = Modifier.padding(bottom = 2.dp, start = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Slate500
                                )
                            )
                        }
                        Text(
                            text = "No deposit scams",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Green700,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Button(
                        onClick = { onViewAccommodation(property) },
                        colors = ButtonDefaults.buttonColors(containerColor = Navy800),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("view_accommodation_btn_${property.id}")
                    ) {
                        Text(
                            text = "View Accommodation",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                        )
                    }
                }
            }
        }
    }
}

// Property Details & Booking Request Modal
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PropertyDetailDialog(
    property: Property,
    onDismiss: () -> Unit,
    onRequestBooking: (property: Property, note: String) -> Unit
) {
    var studentNote by remember { mutableStateOf("") }
    var showSuccessRequest by remember { mutableStateOf(false) }

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
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with Image and Close button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Navy900)
                ) {
                    PropertyImageContainer(
                        title = property.title,
                        institution = property.institution,
                        height = 180.dp,
                        badgeContent = {
                            VerifiedBadge(label = "Verified Landlord")
                        }
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = White
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    if (showSuccessRequest) {
                        // Success Confirmation State
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Green50,
                            border = BorderStroke(1.dp, Green100),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Shield,
                                    contentDescription = null,
                                    tint = Green600,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Booking Request Sent!",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Green700
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "The verified landlord has been notified. Landlord contact details will unlock immediately once they confirm your request in accordance with BedSpaceZM trust policy.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate700),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = onDismiss,
                                    colors = ButtonDefaults.buttonColors(containerColor = Navy800),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Done")
                                }
                            }
                        }
                    } else {
                        // Main Detail Content
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "K${property.priceMonthlyKwacha} / month",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Navy900
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Blue50,
                                border = BorderStroke(1.dp, Blue600.copy(alpha = 0.2f))
                            ) {
                                Text(
                                    text = property.roomType.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Blue600,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = property.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Navy900
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = Slate500,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${property.distanceKm} km from ${property.institution} · ${property.address}",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Slate200)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Description
                        Text(
                            text = "About this Accommodation",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Navy900
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = property.description,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Slate700)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // All Amenities
                        Text(
                            text = "Included Amenities",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Navy900
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            property.amenities.forEach { amenity ->
                                AmenityChip(amenity = amenity)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Landlord Verification Card
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Slate100),
                            border = BorderStroke(1.dp, Slate200),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Navy800),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = property.landlordName.take(1),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = property.landlordName,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Navy900
                                        )
                                    )
                                    Text(
                                        text = "Identity & Property Ownership Verified by BedSpaceZM",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Green700)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Protected Contact Notice
                        ProtectedContactPanel(
                            landlordName = property.landlordName,
                            phone = "+260 96 XXX XXXX",
                            whatsapp = "+260 96 XXX XXXX",
                            isConfirmed = false
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Send Booking Request section
                        Text(
                            text = "Send Request to Landlord",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Navy900
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = studentNote,
                            onValueChange = { studentNote = it },
                            placeholder = {
                                Text(
                                    "E.g., Hi, I am a 2nd year student at ${property.institution} looking to move in next month...",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            maxLines = 4,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                onRequestBooking(property, studentNote)
                                showSuccessRequest = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Navy800),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("submit_booking_request_btn")
                        ) {
                            Text(
                                "Send Free Booking Request",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
