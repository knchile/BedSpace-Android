package com.example.ui.faq

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FAQItem
import com.example.ui.theme.Blue100
import com.example.ui.theme.Blue50
import com.example.ui.theme.Blue600
import com.example.ui.theme.Green700
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.White

val sampleFaqList = listOf(
    FAQItem(
        category = "Anti-Scam & Trust",
        question = "How does BedSpaceZM prevent boarding house rental scams?",
        answer = "Every landlord on BedSpaceZM must submit their Zambian National Registration Card (NRC) and proof of property ownership (such as Lusaka/Kitwe City Council rate receipts or Title Deeds). Our admin team physically cross-checks these records before approving any listing."
    ),
    FAQItem(
        category = "Anti-Scam & Trust",
        question = "Why can't I see the landlord's phone number immediately?",
        answer = "We protect student privacy and shield landlords from unsolicited broker harassment. Once you send an inquiry or complete a reservation deposit, the verified landlord's direct phone number and WhatsApp line are unlocked instantly."
    ),
    FAQItem(
        category = "Payments & Deposits",
        question = "Which payment methods are supported on BedSpaceZM?",
        answer = "We support all major Zambian payment channels: Airtel Money (*778#), MTN Mobile Money (*115#), Zamtel Kwacha (*344#), and Visa/Mastercard Debit/Credit cards. Payments generate an instant digital receipt with a traceable reference code."
    ),
    FAQItem(
        category = "Payments & Deposits",
        question = "What is the K200 Reservation Deposit?",
        answer = "The K200 Reservation Deposit is an optional commitment fee that holds the room for 48 hours while you finalize viewing or move-in arrangements with the landlord. The fee is credited toward your first month's rent."
    ),
    FAQItem(
        category = "Landlords & Listings",
        question = "How do landlords list properties on BedSpaceZM?",
        answer = "Landlords can register for a Landlord Account, upload their NRC ID and City Council documents in the KYC Verification portal, and add property details (distance from campus, price, amenities, and photos). Once verified by Admin, the listing goes live to thousands of students."
    ),
    FAQItem(
        category = "Universities & Coverage",
        question = "Which Zambian universities and colleges are currently covered?",
        answer = "BedSpaceZM covers boarding houses and hostels near the University of Zambia (UNZA Great East & Ridgeway), Copperbelt University (CBU Riverside & Jambo Drive), Mulungushi University (Kabwe & Chalo), Evelyn Hone College (Lusaka CBD), Apex Medical University, and Eden University."
    ),
    FAQItem(
        category = "Tenancy & Rules",
        question = "What if the room looks different from the listing photos?",
        answer = "Our student escrow protection allows you to report any discrepancies within 24 hours of inspection. If the accommodation fails our verification standard, you are eligible for an immediate refund of your reservation deposit."
    )
)

@Composable
fun FaqScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All Categories") }
    var expandedIndex by remember { mutableIntStateOf(-1) }

    val categories = listOf("All Categories", "Anti-Scam & Trust", "Payments & Deposits", "Landlords & Listings", "Universities & Coverage")

    val filteredFaqs = remember(searchQuery, selectedCategory) {
        sampleFaqList.filter { faq ->
            val matchesCategory = selectedCategory == "All Categories" || faq.category == selectedCategory
            val matchesQuery = searchQuery.isBlank() || faq.question.contains(searchQuery, ignoreCase = true) || faq.answer.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Navy900)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Frequently Asked Questions",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Navy900)
                    )
                    Text(
                        text = "Everything you need to know about BedSpaceZM",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search questions, payments, verification...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = Slate500) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Category Pills
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.take(3).forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) Navy900 else White,
                        border = BorderStroke(1.dp, if (isSelected) Navy900 else Slate200),
                        modifier = Modifier.clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) White else Slate700,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // FAQ Items List
        items(filteredFaqs) { faq ->
            val index = sampleFaqList.indexOf(faq)
            val isExpanded = expandedIndex == index

            Card(
                colors = CardDefaults.cardColors(containerColor = White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, if (isExpanded) Blue600 else Slate200),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        expandedIndex = if (isExpanded) -1 else index
                    }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Blue50),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.HelpOutline, contentDescription = null, tint = Blue600, modifier = Modifier.size(14.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = faq.question,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Navy900, fontSize = 13.sp)
                            )
                        }
                        Icon(
                            imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = null,
                            tint = Slate500
                        )
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Column {
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = Slate100)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = faq.answer,
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate700, lineHeight = 18.sp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Slate50,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    text = "Category: ${faq.category}",
                                    color = Slate500,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
