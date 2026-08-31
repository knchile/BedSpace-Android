package com.example.data

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

object SampleData {

    val institutions = listOf(
        Institution("all", "All Institutions", "All", "Zambia"),
        Institution("unza", "University of Zambia (UNZA)", "UNZA", "Lusaka - Great East Rd"),
        Institution("cbu", "Copperbelt University (CBU)", "CBU", "Kitwe - Riverside"),
        Institution("evelyn", "Evelyn Hone College", "Evelyn Hone", "Lusaka - Church Rd"),
        Institution("apex", "Apex Medical University", "Apex", "Lusaka - Chalala"),
        Institution("cavendish", "Cavendish University", "Cavendish", "Lusaka - Villa Elizabetha"),
        Institution("zcas", "ZCAS University", "ZCAS", "Lusaka - Dedan Kimathi"),
        Institution("mulungushi", "Mulungushi University", "Mulungushi", "Kabwe / Lusaka")
    )

    val sampleProperties = listOf(
        Property(
            id = "prop_1",
            title = "Campus View Lodge",
            institution = "UNZA",
            distanceKm = 0.8,
            priceMonthlyKwacha = 1800,
            roomType = RoomType.SINGLE_ROOM,
            amenities = listOf(Amenity.WIFI, Amenity.WATER, Amenity.SECURITY, Amenity.BACKUP_POWER),
            rating = 4.8,
            reviewCount = 24,
            landlordName = "Mr. Mwansa Tembo",
            isLandlordVerified = true,
            address = "Plot 44, Kalingalinga near UNZA Goma Gate",
            description = "Quiet and modern single student room located 800m from UNZA main gate. Includes 24/7 borehole water, high-speed fibre Wi-Fi, solar inverter power backup, and walled electric fence.",
            availableRooms = 2,
            totalRooms = 6,
            isFeatured = true,
            status = ListingStatus.ACTIVE
        ),
        Property(
            id = "prop_2",
            title = "Olympia Student Haven",
            institution = "UNZA",
            distanceKm = 1.2,
            priceMonthlyKwacha = 2200,
            roomType = RoomType.STUDIO,
            amenities = listOf(Amenity.WIFI, Amenity.WATER, Amenity.SECURITY, Amenity.HOT_SHOWER, Amenity.STUDY_DESK),
            rating = 4.9,
            reviewCount = 18,
            landlordName = "Mrs. Chileshe Banda",
            isLandlordVerified = true,
            address = "Katima Mulilo Rd, Olympia Park, Lusaka",
            description = "Self-contained modern studio with private bathroom, study desk, 24hr security guard, and quiet study environment suitable for serious students.",
            availableRooms = 1,
            totalRooms = 4,
            isFeatured = true,
            status = ListingStatus.ACTIVE
        ),
        Property(
            id = "prop_3",
            title = "Riverside CBU Villa",
            institution = "CBU",
            distanceKm = 0.5,
            priceMonthlyKwacha = 1400,
            roomType = RoomType.SHARED_ROOM,
            amenities = listOf(Amenity.WIFI, Amenity.WATER, Amenity.SECURITY, Amenity.FURNISHED),
            rating = 4.7,
            reviewCount = 31,
            landlordName = "Eng. Patrick Lungu",
            isLandlordVerified = true,
            address = "Riverside Ext, Kitwe (5 mins walk to CBU Campus)",
            description = "Spacious shared student quarters. Fully furnished with study tables, beds, personal lockers, and reliable borehole water system.",
            availableRooms = 3,
            totalRooms = 8,
            isFeatured = true,
            status = ListingStatus.ACTIVE
        ),
        Property(
            id = "prop_4",
            title = "Hone Village Annex",
            institution = "Evelyn Hone",
            distanceKm = 0.6,
            priceMonthlyKwacha = 1200,
            roomType = RoomType.BEDSPACE,
            amenities = listOf(Amenity.WIFI, Amenity.WATER, Amenity.SECURITY, Amenity.KITCHEN),
            rating = 4.5,
            reviewCount = 15,
            landlordName = "Mrs. Gertrude Phiri",
            isLandlordVerified = true,
            address = "Near Church Road & Fairview, Lusaka",
            description = "Convenient 2-in-1 bedspace right near Evelyn Hone College and Town centre. Includes communal kitchen, high security, and water tank storage.",
            availableRooms = 2,
            totalRooms = 10,
            isFeatured = false,
            status = ListingStatus.ACTIVE
        ),
        Property(
            id = "prop_5",
            title = "Apex Medical Residency",
            institution = "Apex",
            distanceKm = 0.9,
            priceMonthlyKwacha = 2500,
            roomType = RoomType.STUDIO,
            amenities = listOf(Amenity.WIFI, Amenity.WATER, Amenity.SECURITY, Amenity.BACKUP_POWER, Amenity.HOT_SHOWER),
            rating = 4.9,
            reviewCount = 12,
            landlordName = "Dr. Kangwa Mulenga",
            isLandlordVerified = true,
            address = "Off Lilayi Road, Chalala, Lusaka",
            description = "Tailored for medical and healthcare students needing calm study atmosphere, 24/7 power backup for night study, and hot water showers.",
            availableRooms = 1,
            totalRooms = 5,
            isFeatured = true,
            status = ListingStatus.ACTIVE
        ),
        Property(
            id = "prop_6",
            title = "ZCAS Executive Student Suites",
            institution = "ZCAS",
            distanceKm = 0.4,
            priceMonthlyKwacha = 1950,
            roomType = RoomType.SINGLE_ROOM,
            amenities = listOf(Amenity.WIFI, Amenity.WATER, Amenity.SECURITY, Amenity.STUDY_DESK),
            rating = 4.6,
            reviewCount = 20,
            landlordName = "Mr. Bright Mwanza",
            isLandlordVerified = true,
            address = "Dedan Kimathi Road, Rhodes Park, Lusaka",
            description = "Prime accommodation walking distance to ZCAS University and UNZA Ridgeway. High-speed uncapped Wi-Fi and quiet library room.",
            availableRooms = 2,
            totalRooms = 6,
            isFeatured = false,
            status = ListingStatus.ACTIVE
        )
    )

    val sampleStudentBookingRequests = listOf(
        BookingRequest(
            id = "req_101",
            propertyId = "prop_1",
            propertyTitle = "Campus View Lodge (Single Room)",
            institution = "UNZA",
            roomType = RoomType.SINGLE_ROOM,
            monthlyPrice = 1800,
            studentName = "Thabo Musonda",
            studentPhone = "+260 97 712 3456",
            studentEmail = "thabo.musonda@student.unza.zm",
            requestDate = "28 Aug 2026",
            status = BookingStatus.CONFIRMED,
            landlordName = "Mr. Mwansa Tembo",
            landlordPhone = "+260 96 688 2244",
            landlordWhatsapp = "+260 96 688 2244",
            message = "Hi Mr. Tembo, I am a 3rd year Computer Science student looking to move in for Term 1."
        ),
        BookingRequest(
            id = "req_102",
            propertyId = "prop_2",
            propertyTitle = "Olympia Student Haven (Studio)",
            institution = "UNZA",
            roomType = RoomType.STUDIO,
            monthlyPrice = 2200,
            studentName = "Thabo Musonda",
            studentPhone = "+260 97 712 3456",
            studentEmail = "thabo.musonda@student.unza.zm",
            requestDate = "30 Aug 2026",
            status = BookingStatus.PENDING,
            landlordName = "Mrs. Chileshe Banda",
            landlordPhone = "+260 95 511 8899", // Protected
            landlordWhatsapp = "+260 95 511 8899", // Protected
            message = "Inquiring if the studio room is still open for the upcoming academic semester."
        ),
        BookingRequest(
            id = "req_103",
            propertyId = "prop_6",
            propertyTitle = "ZCAS Executive Student Suites",
            institution = "ZCAS",
            roomType = RoomType.SINGLE_ROOM,
            monthlyPrice = 1950,
            studentName = "Thabo Musonda",
            studentPhone = "+260 97 712 3456",
            studentEmail = "thabo.musonda@student.unza.zm",
            requestDate = "15 Aug 2026",
            status = BookingStatus.DECLINED,
            landlordName = "Mr. Bright Mwanza",
            landlordPhone = "+260 97 733 4455",
            landlordWhatsapp = "+260 97 733 4455",
            message = "Room was fully booked by returning senior students."
        )
    )

    val sampleLandlordIncomingRequests = listOf(
        BookingRequest(
            id = "l_req_201",
            propertyId = "prop_1",
            propertyTitle = "Campus View Lodge — Room 3A",
            institution = "UNZA",
            roomType = RoomType.SINGLE_ROOM,
            monthlyPrice = 1800,
            studentName = "Kondwani Banda",
            studentPhone = "+260 97 844 5566",
            studentEmail = "k.banda@unza.zm",
            requestDate = "Today, 14:30",
            status = BookingStatus.PENDING,
            landlordName = "Mr. Mwansa Tembo",
            landlordPhone = "+260 96 688 2244",
            landlordWhatsapp = "+260 96 688 2244",
            message = "Hello sir, I am a 2nd year Engineering student. I would love to view and book this single room."
        ),
        BookingRequest(
            id = "l_req_202",
            propertyId = "prop_1",
            propertyTitle = "Campus View Lodge — Room 1B",
            institution = "UNZA",
            roomType = RoomType.SINGLE_ROOM,
            monthlyPrice = 1800,
            studentName = "Natasha Mutale",
            studentPhone = "+260 96 122 3344",
            studentEmail = "nmutale@unza.zm",
            requestDate = "Yesterday, 18:15",
            status = BookingStatus.PENDING,
            landlordName = "Mr. Mwansa Tembo",
            landlordPhone = "+260 96 688 2244",
            landlordWhatsapp = "+260 96 688 2244",
            message = "Inquiring about booking for 2 semesters. Is the backup power reliable during load shedding?"
        ),
        BookingRequest(
            id = "l_req_203",
            propertyId = "prop_1",
            propertyTitle = "Campus View Lodge — Room 4",
            institution = "UNZA",
            roomType = RoomType.SINGLE_ROOM,
            monthlyPrice = 1800,
            studentName = "Thabo Musonda",
            studentPhone = "+260 97 712 3456",
            studentEmail = "thabo.musonda@student.unza.zm",
            requestDate = "28 Aug 2026",
            status = BookingStatus.CONFIRMED,
            landlordName = "Mr. Mwansa Tembo",
            landlordPhone = "+260 96 688 2244",
            landlordWhatsapp = "+260 96 688 2244",
            message = "Confirmed booking for Term 1 academic calendar."
        )
    )

    val sampleAdminVerificationQueue = listOf(
        LandlordVerificationRecord(
            id = "verif_301",
            landlordName = "Mr. Mwansa Tembo",
            nrcNumber = "319482/11/1",
            phone = "+260 96 688 2244",
            propertyName = "Campus View Lodge (6 Units)",
            institutionArea = "UNZA - Kalingalinga",
            submissionDate = "29 Aug 2026",
            status = VerificationStatus.UNDER_REVIEW,
            nrcDocumentName = "NRC_319482_Tembo_VerifiedScan.pdf",
            ownershipDocumentName = "Lusaka_City_Council_Rates_2026.pdf"
        ),
        LandlordVerificationRecord(
            id = "verif_302",
            landlordName = "Mrs. Beauty Zulu",
            nrcNumber = "184920/65/1",
            phone = "+260 97 339 9001",
            propertyName = "Chilenje South Student Flats",
            institutionArea = "Apex Medical University",
            submissionDate = "27 Aug 2026",
            status = VerificationStatus.VERIFIED,
            nrcDocumentName = "NRC_Zulu_Beauty_Certified.pdf",
            ownershipDocumentName = "Ministry_Lands_TitleDeed_4402.pdf"
        ),
        LandlordVerificationRecord(
            id = "verif_303",
            landlordName = "Kelvin Chilufya",
            nrcNumber = "490211/10/2",
            phone = "+260 95 440 1122",
            propertyName = "Riverside Kitwe Student Boarding",
            institutionArea = "CBU - Riverside",
            submissionDate = "24 Aug 2026",
            status = VerificationStatus.REJECTED,
            rejectionReason = "Ownership document was blurry and expired lease agreement provided instead of title deed or landlord authority letter.",
            nrcDocumentName = "NRC_Kelvin_Scan.jpg",
            ownershipDocumentName = "Tenancy_Agreement_2024.pdf"
        )
    )

    val sampleAdminListingApprovals = listOf(
        ListingApprovalRecord(
            id = "list_app_401",
            propertyTitle = "Sunrise Chalala Medical Quarters",
            landlordName = "Mrs. Beauty Zulu",
            institution = "Apex",
            priceMonthlyKwacha = 2300,
            roomType = RoomType.STUDIO,
            submittedDate = "30 Aug 2026",
            status = ListingStatus.PENDING_APPROVAL
        ),
        ListingApprovalRecord(
            id = "list_app_402",
            propertyTitle = "Goma Hill Student Residence",
            landlordName = "Mr. Mwansa Tembo",
            institution = "UNZA",
            priceMonthlyKwacha = 1750,
            roomType = RoomType.SINGLE_ROOM,
            submittedDate = "29 Aug 2026",
            status = ListingStatus.PENDING_APPROVAL
        ),
        ListingApprovalRecord(
            id = "list_app_403",
            propertyTitle = "Church Road Scholars Hub",
            landlordName = "Mrs. Gertrude Phiri",
            institution = "Evelyn Hone",
            priceMonthlyKwacha = 1350,
            roomType = RoomType.SHARED_ROOM,
            submittedDate = "28 Aug 2026",
            status = ListingStatus.ACTIVE
        )
    )
}
