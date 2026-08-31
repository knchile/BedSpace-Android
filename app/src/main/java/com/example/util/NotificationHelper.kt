package com.example.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {

    const val CHANNEL_BOOKINGS = "bedspace_channel_bookings"
    const val CHANNEL_INQUIRIES = "bedspace_channel_inquiries"
    const val CHANNEL_SAFETY = "bedspace_channel_safety"

    private var hasCreatedChannels = false

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val bookingsChannel = NotificationChannel(
                CHANNEL_BOOKINGS,
                "Booking Confirmations & Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Instant notifications when a landlord accepts or updates your room booking."
                enableVibration(true)
            }

            val inquiriesChannel = NotificationChannel(
                CHANNEL_INQUIRIES,
                "Landlord Inquiries & Requests",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for landlords when verified students submit accommodation inquiries."
                enableVibration(true)
            }

            val safetyChannel = NotificationChannel(
                CHANNEL_SAFETY,
                "Verification & Security Notices",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "KYC status updates, scam prevention advisories, and campus alerts."
            }

            notificationManager.createNotificationChannel(bookingsChannel)
            notificationManager.createNotificationChannel(inquiriesChannel)
            notificationManager.createNotificationChannel(safetyChannel)
            hasCreatedChannels = true
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun sendStudentBookingConfirmedNotification(
        context: Context,
        propertyTitle: String,
        landlordName: String
    ) {
        initNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_BOOKINGS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🎉 Booking Confirmed — Contact Unlocked!")
            .setContentText("Mr. $landlordName confirmed your request for $propertyTitle. Phone & WhatsApp are now accessible.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Great news! Your booking inquiry for $propertyTitle was confirmed by $landlordName. You can now tap to view verified phone numbers and WhatsApp buttons safely.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        if (hasNotificationPermission(context)) {
            try {
                NotificationManagerCompat.from(context).notify((System.currentTimeMillis() % 10000).toInt(), notification)
            } catch (e: SecurityException) {
                // permission revoked
            }
        }
    }

    fun sendLandlordNewInquiryNotification(
        context: Context,
        studentName: String,
        propertyTitle: String,
        roomType: String
    ) {
        initNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            102,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_INQUIRIES)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle("📩 New Student Booking Request")
            .setContentText("$studentName is inquiring about $roomType at $propertyTitle.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("A verified student ($studentName) has submitted a booking request for $roomType at $propertyTitle. Tap to review details and confirm.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        if (hasNotificationPermission(context)) {
            try {
                NotificationManagerCompat.from(context).notify((System.currentTimeMillis() % 10000).toInt(), notification)
            } catch (e: SecurityException) {
                // permission revoked
            }
        }
    }

    fun sendVerificationNotification(
        context: Context,
        title: String,
        message: String
    ) {
        initNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            103,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_SAFETY)
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        if (hasNotificationPermission(context)) {
            try {
                NotificationManagerCompat.from(context).notify((System.currentTimeMillis() % 10000).toInt(), notification)
            } catch (e: SecurityException) {
                // permission revoked
            }
        }
    }
}
