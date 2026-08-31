package com.example.data.payment

import android.content.Context
import com.example.data.neon.NeonRepository
import com.example.data.neon.PushNotificationRecord
import com.example.model.BookingStatus
import com.example.model.PaymentProvider
import com.example.model.PaymentStatus
import com.example.model.PaymentTransaction
import com.example.model.PaymentType
import com.example.model.Property
import com.example.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Zambian Mobile Money & Card Payment Gateway Manager
 * Handles Airtel Money (*778#), MTN MoMo (*115#), Zamtel Kwacha (*344#), and Card payments.
 */
object PaymentRepository {

    private val _transactions = MutableStateFlow<List<PaymentTransaction>>(
        listOf(
            PaymentTransaction(
                id = "tx_sample_1",
                bookingId = "req_1",
                propertyId = "prop_1",
                propertyTitle = "Campus View Lodge (UNZA Great East)",
                studentId = "usr_student_1",
                studentName = "Thabo Musonda",
                studentPhone = "+260 97 112 3344",
                landlordName = "Mr. Mwansa Tembo",
                amountKwacha = 200,
                paymentType = PaymentType.RESERVATION_FEE,
                provider = PaymentProvider.AIRTEL_MONEY,
                providerAccountNumber = "0971123344",
                referenceCode = "BSZM-AIR-882190",
                timestamp = System.currentTimeMillis() - 86400000L * 2,
                dateFormatted = "28 Aug 2026, 14:32",
                status = PaymentStatus.COMPLETED
            )
        )
    )
    val transactions: StateFlow<List<PaymentTransaction>> = _transactions.asStateFlow()

    fun processPayment(
        bookingId: String,
        property: Property,
        student: User,
        amountKwacha: Int,
        paymentType: PaymentType,
        provider: PaymentProvider,
        accountOrPhone: String,
        context: Context? = null
    ): PaymentTransaction {
        val now = System.currentTimeMillis()
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        val dateFormatted = sdf.format(Date(now))
        val prefix = when (provider) {
            PaymentProvider.AIRTEL_MONEY -> "AIR"
            PaymentProvider.MTN_MOMO -> "MTN"
            PaymentProvider.ZAMTEL_KWACHA -> "ZAM"
            PaymentProvider.VISA_MASTERCARD -> "CRD"
        }
        val refCode = "BSZM-$prefix-${(100000..999999).random()}"

        val tx = PaymentTransaction(
            id = "tx_${UUID.randomUUID().toString().take(8)}",
            bookingId = bookingId,
            propertyId = property.id,
            propertyTitle = property.title,
            studentId = student.id,
            studentName = student.name,
            studentPhone = student.phone,
            landlordName = property.landlordName,
            amountKwacha = amountKwacha,
            paymentType = paymentType,
            provider = provider,
            providerAccountNumber = accountOrPhone,
            referenceCode = refCode,
            timestamp = now,
            dateFormatted = dateFormatted,
            status = PaymentStatus.COMPLETED
        )

        // Save transaction record
        _transactions.value = listOf(tx) + _transactions.value

        // Auto-confirm booking in Neon repository
        NeonRepository.updateBookingStatus(
            requestId = bookingId,
            newStatus = BookingStatus.CONFIRMED,
            context = context,
            landlordName = property.landlordName
        )

        // Notify Landlord and Student
        NeonRepository.addNotification(
            PushNotificationRecord(
                recipientRole = "LANDLORD",
                title = "💰 Payment Received: K$amountKwacha",
                body = "${student.name} completed ${paymentType.label} via ${provider.label} (Ref: $refCode). Room reserved.",
                channel = "PAYMENT"
            )
        )

        NeonRepository.addNotification(
            PushNotificationRecord(
                recipientRole = "STUDENT",
                title = "🧾 Payment Confirmed (Ref: $refCode)",
                body = "Your payment of K$amountKwacha to ${property.landlordName} was successful. Landlord contacts are unlocked!",
                channel = "PAYMENT"
            )
        )

        return tx
    }

    fun getStudentTransactions(studentId: String, studentEmail: String): List<PaymentTransaction> {
        return _transactions.value.filter { 
            it.studentId == studentId || it.studentName.contains(studentId, ignoreCase = true) 
        }
    }

    fun getLandlordTransactions(landlordName: String): List<PaymentTransaction> {
        return _transactions.value.filter { 
            it.landlordName.equals(landlordName, ignoreCase = true) 
        }
    }
}
