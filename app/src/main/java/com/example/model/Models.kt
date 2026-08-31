package com.example.model

enum class RoomType(val label: String) {
    SINGLE_ROOM("Single Room"),
    SHARED_ROOM("Shared Room"),
    BEDSPACE("Bedspace (2-in-1)"),
    STUDIO("Self-Contained Studio"),
    ONE_BEDROOM("1 Bedroom Flat")
}

enum class Amenity(val label: String, val iconName: String) {
    WIFI("Wi-Fi", "wifi"),
    WATER("Borehole Water 24/7", "water"),
    SECURITY("24/7 Security & Guard", "security"),
    BACKUP_POWER("Solar / Inverter Backup", "power"),
    FURNISHED("Furnished (Bed & Wardrobe)", "bed"),
    KITCHEN("Shared Kitchen", "kitchen"),
    STUDY_DESK("Dedicated Study Area", "desk"),
    HOT_SHOWER("Geyser / Hot Water", "shower")
}

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    DECLINED,
    CANCELLED
}

enum class VerificationStatus {
    UNDER_REVIEW,
    VERIFIED,
    REJECTED
}

enum class ListingStatus {
    ACTIVE,
    PENDING_APPROVAL,
    RENTED,
    DRAFT
}

data class Institution(
    val id: String,
    val name: String,
    val shortName: String,
    val city: String
)

data class Property(
    val id: String,
    val title: String,
    val institution: String,
    val distanceKm: Double,
    val priceMonthlyKwacha: Int,
    val roomType: RoomType,
    val amenities: List<Amenity>,
    val rating: Double?,
    val reviewCount: Int,
    val landlordName: String,
    val isLandlordVerified: Boolean,
    val address: String,
    val description: String,
    val availableRooms: Int,
    val totalRooms: Int,
    val isFeatured: Boolean = false,
    val status: ListingStatus = ListingStatus.ACTIVE,
    val imageUrl: String = ""
)

data class BookingRequest(
    val id: String,
    val propertyId: String,
    val propertyTitle: String,
    val institution: String,
    val roomType: RoomType,
    val monthlyPrice: Int,
    val studentName: String,
    val studentPhone: String,
    val studentEmail: String,
    val requestDate: String,
    val status: BookingStatus,
    val landlordName: String,
    val landlordPhone: String,
    val landlordWhatsapp: String,
    val message: String
)

data class LandlordVerificationRecord(
    val id: String,
    val landlordName: String,
    val nrcNumber: String,
    val phone: String,
    val propertyName: String,
    val institutionArea: String,
    val submissionDate: String,
    val status: VerificationStatus,
    val rejectionReason: String? = null,
    val nrcDocumentName: String = "NRC_ID_Front_Back.pdf",
    val ownershipDocumentName: String = "Property_Title_Or_Council_Rates.pdf"
)

data class ListingApprovalRecord(
    val id: String,
    val propertyTitle: String,
    val landlordName: String,
    val institution: String,
    val priceMonthlyKwacha: Int,
    val roomType: RoomType,
    val submittedDate: String,
    val status: ListingStatus,
    val rejectionReason: String? = null
)

enum class AppDestination(val route: String, val title: String) {
    LANDING("/", "Find Rooms"),
    STUDENT("/student/dashboard", "Student Portal"),
    LANDLORD("/landlord/dashboard", "Landlord Portal"),
    ADMIN("/admin", "Admin Console")
}
