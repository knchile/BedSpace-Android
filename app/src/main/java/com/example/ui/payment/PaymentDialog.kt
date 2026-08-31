package com.example.ui.payment

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.auth.AuthRepository
import com.example.data.payment.LipilaPaymentClient
import com.example.data.payment.PaymentRepository
import com.example.model.PaymentProvider
import com.example.model.PaymentTransaction
import com.example.model.PaymentType
import com.example.model.Property
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PaymentDialog(
    property: Property,
    bookingId: String,
    onDismiss: () -> Unit,
    onPaymentSuccess: (PaymentTransaction) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentUser = AuthRepository.currentUser.value

    var paymentType by remember { mutableStateOf(PaymentType.RESERVATION_FEE) }
    var selectedProvider by remember { mutableStateOf(PaymentProvider.AIRTEL_MONEY) }
    var phoneOrAccount by remember { mutableStateOf(currentUser?.phone ?: "097") }
    var isProcessing by remember { mutableStateOf(false) }
    var processingStep by remember { mutableStateOf("") }
    var completedTransaction by remember { mutableStateOf<PaymentTransaction?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val amountToPay = if (paymentType == PaymentType.RESERVATION_FEE) 200 else property.priceMonthlyKwacha

    Dialog(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = White,
            border = BorderStroke(1.dp, Slate200),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Green50),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.AccountBalanceWallet, contentDescription = null, tint = Green700, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Lipila Payment Gateway",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Navy900
                                )
                            )
                            Text(
                                text = "Mobile Money (Airtel · MTN · Zamtel) & Card",
                                style = MaterialTheme.typography.labelSmall.copy(color = Slate500)
                            )
                        }
                    }
                    if (!isProcessing) {
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = Slate500)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (completedTransaction != null) {
                    // --- SUCCESS RECEIPT VIEW ---
                    val tx = completedTransaction!!
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Green100),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Green700, modifier = Modifier.size(36.dp))
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Payment Approved!",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Green700)
                        )
                        Text(
                            text = "Your booking is confirmed & landlord contacts unlocked.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate600),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Receipt Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Slate50),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Slate200),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Amount Paid", color = Slate500, fontSize = 12.sp)
                                    Text("ZMW K${tx.amountKwacha}", fontWeight = FontWeight.Bold, color = Navy900, fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Reference Code", color = Slate500, fontSize = 12.sp)
                                    Text(tx.referenceCode, fontWeight = FontWeight.Bold, color = Blue600, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Payment Method", color = Slate500, fontSize = 12.sp)
                                    Text(tx.provider.label, fontWeight = FontWeight.Medium, color = Slate800, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Property", color = Slate500, fontSize = 12.sp)
                                    Text(tx.propertyTitle.take(22) + "...", fontWeight = FontWeight.Medium, color = Slate800, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Landlord", color = Slate500, fontSize = 12.sp)
                                    Text(tx.landlordName, fontWeight = FontWeight.Medium, color = Slate800, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Date & Time", color = Slate500, fontSize = 12.sp)
                                    Text(tx.dateFormatted, fontWeight = FontWeight.Medium, color = Slate600, fontSize = 11.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { onPaymentSuccess(tx) },
                            colors = ButtonDefaults.buttonColors(containerColor = Navy800),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Text("Done & View Booking", fontWeight = FontWeight.Bold, color = White)
                        }
                    }

                } else if (isProcessing) {
                    // --- PROCESSING MODAL ---
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Blue600,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Authorizing Mobile Money Transaction...",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Navy900)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = processingStep,
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate600),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Blue50,
                            border = BorderStroke(1.dp, Blue100),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.PhoneAndroid, contentDescription = null, tint = Blue600, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Check your phone for the USSD prompt (${selectedProvider.ussdPrefix}) to enter your Mobile Money PIN.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Blue600, fontSize = 11.sp)
                                )
                            }
                        }
                    }

                } else {
                    // --- PAYMENT SELECTION FORM ---

                    // Property Summary Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Slate50),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Slate200),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = property.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Navy900)
                            )
                            Text(
                                text = "Landlord: ${property.landlordName} • ${property.institution}",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate600)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Select Payment Type
                    Text(
                        text = "Choose Payment Option:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Slate700)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (paymentType == PaymentType.RESERVATION_FEE) Blue50 else Slate50,
                            border = BorderStroke(1.dp, if (paymentType == PaymentType.RESERVATION_FEE) Blue600 else Slate200),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { paymentType = PaymentType.RESERVATION_FEE }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Reservation Deposit", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Navy900)
                                Text("ZMW K200", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Blue600)
                                Text("Holds the room", fontSize = 10.sp, color = Slate500)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (paymentType == PaymentType.FIRST_MONTH_RENT) Blue50 else Slate50,
                            border = BorderStroke(1.dp, if (paymentType == PaymentType.FIRST_MONTH_RENT) Blue600 else Slate200),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { paymentType = PaymentType.FIRST_MONTH_RENT }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Full 1st Month", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Navy900)
                                Text("ZMW K${property.priceMonthlyKwacha}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Green700)
                                Text("Secures tenancy", fontSize = 10.sp, color = Slate500)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Payment Provider Selector
                    Text(
                        text = "Select Payment Provider (Zambia):",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Slate700)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        PaymentProvider.values().forEach { provider ->
                            val isSelected = selectedProvider == provider
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Slate100 else White,
                                border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) Navy900 else Slate200),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedProvider = provider }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp, vertical = 10.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(Color(provider.brandColorHex))
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = provider.label,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = Navy900,
                                                fontSize = 13.sp
                                            )
                                            if (provider != PaymentProvider.VISA_MASTERCARD) {
                                                Text(
                                                    text = "Instant USSD Push • ${provider.ussdPrefix}",
                                                    color = Slate500,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }
                                    if (isSelected) {
                                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Navy900, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = phoneOrAccount,
                        onValueChange = { 
                            phoneOrAccount = it 
                            errorMessage = null
                        },
                        label = { 
                            Text(if (selectedProvider == PaymentProvider.VISA_MASTERCARD) "Card Number / Account" else "Mobile Money Number") 
                        },
                        placeholder = { Text(if (selectedProvider == PaymentProvider.VISA_MASTERCARD) "4000 1234 5678 9010" else "0977 123 456") },
                        leadingIcon = { 
                            Icon(
                                if (selectedProvider == PaymentProvider.VISA_MASTERCARD) Icons.Filled.CreditCard else Icons.Filled.PhoneAndroid,
                                contentDescription = null,
                                tint = Slate500
                            ) 
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("payment_account_input")
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = Red600,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Trust Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Green50,
                        border = BorderStroke(1.dp, Green100),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Shield, contentDescription = null, tint = Green700, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Protected Escrow: Funds are safeguarded until tenancy confirmation.",
                                color = Green700,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (phoneOrAccount.trim().length < 9) {
                                errorMessage = "Please enter a valid mobile number or card."
                                return@Button
                            }
                            val studentUser = currentUser ?: com.example.model.User(
                                id = "usr_guest",
                                name = "Student User",
                                email = "student@bedspace.zm",
                                password = "",
                                phone = phoneOrAccount,
                                role = com.example.model.UserRole.STUDENT,
                                institution = property.institution
                            )

                            isProcessing = true
                            processingStep = "Initiating Lipila API payment request..."

                            coroutineScope.launch {
                                val lipilaResult = if (selectedProvider == PaymentProvider.VISA_MASTERCARD) {
                                    LipilaPaymentClient.collectCardPayment(
                                        amountKwacha = amountToPay,
                                        cardNumber = phoneOrAccount,
                                        customerName = studentUser.name,
                                        customerEmail = studentUser.email,
                                        narration = "BedSpaceZM: ${property.title}"
                                    )
                                } else {
                                    processingStep = "Pushing ${selectedProvider.label} USSD prompt to ${LipilaPaymentClient.formatZambianPhone(phoneOrAccount)}..."
                                    LipilaPaymentClient.collectMobileMoney(
                                        amountKwacha = amountToPay,
                                        accountNumber = phoneOrAccount,
                                        provider = selectedProvider,
                                        customerName = studentUser.name,
                                        customerEmail = studentUser.email,
                                        narration = "BedSpaceZM: ${property.title}"
                                    )
                                }

                                if (!lipilaResult.isSuccess) {
                                    isProcessing = false
                                    errorMessage = "Lipila Gateway Error: ${lipilaResult.message}"
                                    return@launch
                                }

                                processingStep = "Lipila status: ${lipilaResult.status}. Awaiting mobile money confirmation..."
                                delay(1200)

                                val tx = PaymentRepository.processPayment(
                                    bookingId = bookingId,
                                    property = property,
                                    student = studentUser,
                                    amountKwacha = amountToPay,
                                    paymentType = paymentType,
                                    provider = selectedProvider,
                                    accountOrPhone = LipilaPaymentClient.formatZambianPhone(phoneOrAccount),
                                    customRefCode = lipilaResult.identifier ?: lipilaResult.transactionReference,
                                    context = context
                                )
                                isProcessing = false
                                completedTransaction = tx
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Navy800),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("payment_submit_button")
                    ) {
                        Text(
                            text = "Pay ZMW K$amountToPay via ${selectedProvider.label.split(" ").first()}",
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }
                }
            }
        }
    }
}
