package com.example.data.neon

import android.content.Context
import com.example.model.Amenity
import com.example.model.BookingRequest
import com.example.model.BookingStatus
import com.example.model.Institution
import com.example.model.LandlordVerificationRecord
import com.example.model.ListingApprovalRecord
import com.example.model.ListingStatus
import com.example.model.Property
import com.example.model.RoomType
import com.example.model.VerificationStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Neon Postgres Database Configuration & Connection Metadata
 */
object NeonConfig {
    const val DEFAULT_NEON_HOST = "ep-bedspace-zm.eu-central-1.aws.neon.tech"
    const val DATABASE_NAME = "bedspace_db"
    const val POOL_MODE = "transaction" // Neon serverless pooler
    const val SSL_MODE = "require"
    
    // Connection string template for Neon Serverless Postgres
    const val CONNECTION_STRING_TEMPLATE = 
        "postgresql://bedspace_owner:********@$DEFAULT_NEON_HOST/$DATABASE_NAME?sslmode=$SSL_MODE"

    /**
     * PostgreSQL DDL Schema for Neon backend synchronization
     */
    val SQL_SCHEMA_DDL = """
        -- BedSpaceZM Neon Postgres Database Schema
        
        CREATE TABLE IF NOT EXISTS institutions (
            id VARCHAR(50) PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            short_name VARCHAR(50) NOT NULL,
            city VARCHAR(100) NOT NULL
        );

        CREATE TABLE IF NOT EXISTS properties (
            id VARCHAR(50) PRIMARY KEY,
            title VARCHAR(255) NOT NULL,
            institution_id VARCHAR(50) REFERENCES institutions(id),
            distance_km NUMERIC(4,2) NOT NULL,
            price_monthly_zmw INT NOT NULL,
            room_type VARCHAR(50) NOT NULL,
            amenities TEXT[] NOT NULL,
            rating NUMERIC(3,2),
            review_count INT DEFAULT 0,
            landlord_name VARCHAR(100) NOT NULL,
            is_landlord_verified BOOLEAN DEFAULT FALSE,
            address TEXT NOT NULL,
            description TEXT NOT NULL,
            available_rooms INT NOT NULL,
            total_rooms INT NOT NULL,
            is_featured BOOLEAN DEFAULT FALSE,
            status VARCHAR(50) DEFAULT 'ACTIVE',
            gallery_images TEXT[] DEFAULT '{}',
            created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
        );

        CREATE TABLE IF NOT EXISTS landlord_verifications (
            id VARCHAR(50) PRIMARY KEY,
            landlord_name VARCHAR(100) NOT NULL,
            nrc_number VARCHAR(50) NOT NULL,
            phone VARCHAR(50) NOT NULL,
            property_name VARCHAR(255) NOT NULL,
            institution_area VARCHAR(100) NOT NULL,
            nrc_doc_url TEXT,
            ownership_doc_url TEXT,
            status VARCHAR(50) DEFAULT 'UNDER_REVIEW',
            rejection_reason TEXT,
            submitted_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
        );

        CREATE TABLE IF NOT EXISTS booking_requests (
            id VARCHAR(50) PRIMARY KEY,
            property_id VARCHAR(50) REFERENCES properties(id),
            student_name VARCHAR(100) NOT NULL,
            student_phone VARCHAR(50) NOT NULL,
            student_email VARCHAR(100) NOT NULL,
            room_type VARCHAR(50) NOT NULL,
            monthly_price_zmw INT NOT NULL,
            status VARCHAR(50) DEFAULT 'PENDING',
            landlord_name VARCHAR(100) NOT NULL,
            landlord_phone VARCHAR(50) NOT NULL,
            landlord_whatsapp VARCHAR(50) NOT NULL,
            message TEXT,
            created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
        );

        CREATE TABLE IF NOT EXISTS push_notifications (
            id VARCHAR(50) PRIMARY KEY,
            recipient_role VARCHAR(20) NOT NULL, -- 'STUDENT' or 'LANDLORD'
            title VARCHAR(255) NOT NULL,
            body TEXT NOT NULL,
            channel_id VARCHAR(50) NOT NULL,
            reference_id VARCHAR(50),
            is_read BOOLEAN DEFAULT FALSE,
            created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
        );
    """.trimIndent()
}

data class PushNotificationRecord(
    val id: String = UUID.randomUUID().toString(),
    val recipientRole: String, // "STUDENT", "LANDLORD", "ADMIN"
    val title: String,
    val body: String,
    val channel: String, // "BOOKING_CONFIRMATION", "INQUIRY", "VERIFICATION", "SYSTEM"
    val timestamp: String = SimpleDateFormat("HH:mm, dd MMM", Locale.getDefault()).format(Date()),
    val isRead: Boolean = false,
    val actionUrl: String? = null
)

data class LandlordUploadedDocument(
    val id: String = UUID.randomUUID().toString(),
    val type: DocumentType,
    val fileName: String,
    val fileSizeFormatted: String,
    val localUri: String? = null,
    val uploadProgress: Float = 1.0f,
    val isUploaded: Boolean = true,
    val uploadDate: String = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
)

enum class DocumentType(val label: String) {
    NRC_FRONT("NRC (Front Side)"),
    NRC_BACK("NRC (Back Side)"),
    TITLE_DEED("Title Deed / Proof of Ownership"),
    COUNCIL_RATES("Council Rates Receipt")
}

data class PropertyGalleryItem(
    val id: String = UUID.randomUUID().toString(),
    val uriString: String,
    val isCover: Boolean = false,
    val label: String = "Property Photo"
)
