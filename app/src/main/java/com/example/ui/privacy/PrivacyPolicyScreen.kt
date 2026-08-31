package com.example.ui.privacy

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.White

@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                        text = "Privacy & Security Policy",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Navy900)
                    )
                    Text(
                        text = "Zambia Data Protection Act (2021) Compliant",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Overview Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Navy900),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Green700),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Shield, contentDescription = null, tint = White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Your Data is Protected", color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Zero-Spam & Shielded Contact Policy", color = Slate300, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "BedSpaceZM strictly enforces user confidentiality. Personal phone numbers, National Registration Cards (NRC), and Mobile Money credentials are never sold or publicly exposed to unverified third parties.",
                        color = Slate200,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Section 1: Information We Collect
        item {
            PolicySectionCard(
                icon = Icons.Filled.Policy,
                title = "1. Information We Collect",
                content = "• Students: Full name, university/college email, phone number, and institution enrollment details.\n• Landlords: Legal full name, National Registration Card (NRC) number, proof of property ownership (Council Rates / Title Deed), and direct WhatsApp contacts.\n• Financial Data: Mobile money transaction references (Airtel, MTN, Zamtel, Card). We NEVER store your secret PINs."
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Section 2: Protected Contact System
        item {
            PolicySectionCard(
                icon = Icons.Filled.Lock,
                title = "2. Protected Contact Architecture",
                content = "To eliminate student extortion and unverified broker scams in Lusaka and the Copperbelt, landlord phone numbers and exact room addresses remain encrypted and hidden until a booking or reservation request is confirmed.\nThis ensures only verified, active students can reach verified property managers directly."
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Section 3: Mobile Money & Financial Security
        item {
            PolicySectionCard(
                icon = Icons.Filled.PhoneAndroid,
                title = "3. Mobile Money Payment Security",
                content = "All booking deposits and rent payments made via Airtel Money (*778#), MTN Mobile Money (*115#), and Zamtel Kwacha (*344#) are processed through secure, bank-grade encrypted payment rails with USSD push verification.\nFunds are held in secure escrow until reservation terms are fulfilled."
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Section 4: Data Retention & User Rights
        item {
            PolicySectionCard(
                icon = Icons.Filled.Gavel,
                title = "4. Your Rights Under Zambian Law",
                content = "In accordance with the Data Protection Act No. 3 of 2021 of Zambia:\n• You have the right to request access to your stored personal records.\n• You may request complete erasure or account deletion at any time.\n• Landlord KYC documents are retained exclusively for anti-fraud validation and audit requirements."
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Contact / Inquiries
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate100),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Slate200),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Questions or Data Requests?", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Contact our Data Protection Officer at privacy@bedspace.zm or reach our Lusaka Support Desk at +260 97 000 0000.",
                        color = Slate600,
                        fontSize = 11.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PolicySectionCard(
    icon: ImageVector,
    title: String,
    content: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Slate200),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Blue50),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Blue600, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(title, fontWeight = FontWeight.Bold, color = Navy900, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = content,
                color = Slate700,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}
