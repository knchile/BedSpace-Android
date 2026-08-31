package com.example.data.neon

import android.content.Context
import com.example.data.SampleData
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
import com.example.util.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Neon Data Repository
 * Manages properties, booking requests, KYC documents, and push notifications
 * with active StateFlows.
 */
object NeonRepository {

    private val _properties = MutableStateFlow<List<Property>>(SampleData.sampleProperties)
    val properties: StateFlow<List<Property>> = _properties.asStateFlow()

    private val _studentRequests = MutableStateFlow<List<BookingRequest>>(SampleData.sampleStudentBookingRequests)
    val studentRequests: StateFlow<List<BookingRequest>> = _studentRequests.asStateFlow()
    val studentBookings: StateFlow<List<BookingRequest>> = _studentRequests

    private val _incomingRequests = MutableStateFlow<List<BookingRequest>>(SampleData.sampleLandlordIncomingRequests)
    val incomingRequests: StateFlow<List<BookingRequest>> = _incomingRequests.asStateFlow()
    val landlordInquiries: StateFlow<List<BookingRequest>> = _incomingRequests

    private val _adminVerifications = MutableStateFlow<List<LandlordVerificationRecord>>(SampleData.sampleAdminVerificationQueue)
    val adminVerifications: StateFlow<List<LandlordVerificationRecord>> = _adminVerifications.asStateFlow()

    private val _adminListingApprovals = MutableStateFlow<List<ListingApprovalRecord>>(SampleData.sampleAdminListingApprovals)
    val adminListingApprovals: StateFlow<List<ListingApprovalRecord>> = _adminListingApprovals.asStateFlow()

    private val _notifications = MutableStateFlow<List<PushNotificationRecord>>(listOf(
        PushNotificationRecord(
            recipientRole = "STUDENT",
            title = "🎉 Booking Confirmed!",
            body = "Your booking request for Campus View Lodge (Room 3A) was confirmed by Mr. Mwansa Tembo. Contact details are now unlocked!",
            channel = "BOOKING_CONFIRMATION"
        ),
        PushNotificationRecord(
            recipientRole = "LANDLORD",
            title = "📩 New Booking Inquiry",
            body = "Kondwani Banda sent a booking inquiry for Campus View Lodge (Single Room). Review in your dashboard.",
            channel = "INQUIRY"
        ),
        PushNotificationRecord(
            recipientRole = "LANDLORD",
            title = "🛡️ Verification Notice",
            body = "Your NRC and Lusaka City Council rates documents are currently being processed by BedSpace moderators.",
            channel = "VERIFICATION"
        )
    ))
    val notifications: StateFlow<List<PushNotificationRecord>> = _notifications.asStateFlow()

    private val _landlordDocuments = MutableStateFlow<List<LandlordUploadedDocument>>(listOf(
        LandlordUploadedDocument(
            type = DocumentType.NRC_FRONT,
            fileName = "NRC_Front_Mwansa_Tembo.jpg",
            fileSizeFormatted = "1.8 MB",
            isUploaded = true
        ),
        LandlordUploadedDocument(
            type = DocumentType.NRC_BACK,
            fileName = "NRC_Back_Mwansa_Tembo.jpg",
            fileSizeFormatted = "1.6 MB",
            isUploaded = true
        ),
        LandlordUploadedDocument(
            type = DocumentType.TITLE_DEED,
            fileName = "Lusaka_City_Council_Rates_2026.pdf",
            fileSizeFormatted = "3.2 MB",
            isUploaded = true
        )
    ))
    val landlordDocuments: StateFlow<List<LandlordUploadedDocument>> = _landlordDocuments.asStateFlow()

    private val _propertyGallery = MutableStateFlow<List<PropertyGalleryItem>>(listOf(
        PropertyGalleryItem(uriString = "res_sample_1", isCover = true, label = "Main Bedroom & Study Desk"),
        PropertyGalleryItem(uriString = "res_sample_2", isCover = false, label = "Private Ensuite Bathroom"),
        PropertyGalleryItem(uriString = "res_sample_3", isCover = false, label = "Shared Kitchen & Yard")
    ))
    val propertyGallery: StateFlow<List<PropertyGalleryItem>> = _propertyGallery.asStateFlow()

    private val _backendSyncStatus = MutableStateFlow("Connected to Neon Serverless Postgres")
    val backendSyncStatus: StateFlow<String> = _backendSyncStatus.asStateFlow()

    // --- Actions ---

    fun addStudentRequest(request: BookingRequest, context: Context? = null) {
        _studentRequests.value = listOf(request) + _studentRequests.value
        _incomingRequests.value = listOf(request) + _incomingRequests.value

        // Dispatch in-app notification record
        val notif = PushNotificationRecord(
            recipientRole = "LANDLORD",
            title = "📩 New Student Booking Request",
            body = "${request.studentName} sent a booking request for ${request.propertyTitle} (K${request.monthlyPrice}/mo).",
            channel = "INQUIRY"
        )
        addNotification(notif)

        // Dispatch real Android Push Notification if context is available
        context?.let { ctx: Context ->
            NotificationHelper.sendLandlordNewInquiryNotification(
                context = ctx,
                studentName = request.studentName,
                propertyTitle = request.propertyTitle,
                roomType = request.roomType.label
            )
        }
    }

    fun updateBookingStatus(
        requestId: String,
        newStatus: BookingStatus,
        context: Context? = null,
        landlordName: String = "Mr. Mwansa Tembo"
    ) {
        _incomingRequests.value = _incomingRequests.value.map { req ->
            if (req.id == requestId) req.copy(status = newStatus) else req
        }

        _studentRequests.value = _studentRequests.value.map { req ->
            if (req.id == requestId) req.copy(status = newStatus) else req
        }

        val targetBooking = _studentRequests.value.find { it.id == requestId }
        if (newStatus == BookingStatus.CONFIRMED && targetBooking != null) {
            val notif = PushNotificationRecord(
                recipientRole = "STUDENT",
                title = "🎉 Booking Confirmed by Landlord!",
                body = "Great news, ${targetBooking.studentName}! Your booking for ${targetBooking.propertyTitle} was accepted. $landlordName's phone (${targetBooking.landlordPhone}) is now unlocked.",
                channel = "BOOKING_CONFIRMATION"
            )
            addNotification(notif)

            context?.let { ctx: Context ->
                NotificationHelper.sendStudentBookingConfirmedNotification(
                    context = ctx,
                    propertyTitle = targetBooking.propertyTitle,
                    landlordName = landlordName
                )
            }
        } else if (newStatus == BookingStatus.DECLINED && targetBooking != null) {
            val notif = PushNotificationRecord(
                recipientRole = "STUDENT",
                title = "Booking Request Update",
                body = "Your request for ${targetBooking.propertyTitle} was declined as rooms are occupied.",
                channel = "BOOKING_CONFIRMATION"
            )
            addNotification(notif)
        }
    }

    fun updatePropertyStatus(propertyId: String, newStatus: ListingStatus) {
        _properties.value = _properties.value.map { prop ->
            if (prop.id == propertyId) prop.copy(status = newStatus) else prop
        }
    }

    fun addProperty(property: Property) {
        _properties.value = listOf(property) + _properties.value
        addNotification(
            PushNotificationRecord(
                recipientRole = "LANDLORD",
                title = "🏡 Property Listing Created",
                body = "Listing '${property.title}' published successfully to BedSpace Neon database.",
                channel = "SYSTEM"
            )
        )
    }

    fun addLandlordDocument(
        type: DocumentType,
        fileName: String,
        fileSizeFormatted: String,
        localUri: String? = null
    ) {
        val newDoc = LandlordUploadedDocument(
            type = type,
            fileName = fileName,
            fileSizeFormatted = fileSizeFormatted,
            localUri = localUri,
            isUploaded = true
        )
        _landlordDocuments.value = _landlordDocuments.value.filterNot { it.type == type } + newDoc

        addNotification(
            PushNotificationRecord(
                recipientRole = "ADMIN",
                title = "📄 New KYC Document Uploaded",
                body = "Landlord uploaded ${type.label}: $fileName. Queued for verification review.",
                channel = "VERIFICATION"
            )
        )
    }

    fun removeLandlordDocument(docId: String) {
        _landlordDocuments.value = _landlordDocuments.value.filterNot { it.id == docId }
    }

    fun addPropertyGalleryItem(uriString: String, label: String = "Property Photo") {
        val newItem = PropertyGalleryItem(
            uriString = uriString,
            isCover = _propertyGallery.value.isEmpty(),
            label = label
        )
        _propertyGallery.value = _propertyGallery.value + newItem
    }

    fun removePropertyGalleryItem(itemId: String) {
        val current = _propertyGallery.value.filterNot { it.id == itemId }
        if (current.isNotEmpty() && current.none { it.isCover }) {
            _propertyGallery.value = listOf(current[0].copy(isCover = true)) + current.drop(1)
        } else {
            _propertyGallery.value = current
        }
    }

    fun setCoverPhoto(itemId: String) {
        _propertyGallery.value = _propertyGallery.value.map { item ->
            item.copy(isCover = item.id == itemId)
        }
    }

    fun addNotification(notification: PushNotificationRecord) {
        _notifications.value = listOf(notification) + _notifications.value
    }

    fun markNotificationAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun clearAllNotifications() {
        _notifications.value = emptyList()
    }
}
