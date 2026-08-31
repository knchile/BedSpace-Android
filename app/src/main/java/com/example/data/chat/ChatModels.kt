package com.example.data.chat

import com.example.data.neon.NeonRepository
import com.example.model.Property
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: String = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date()),
    val suggestedProperties: List<Property> = emptyList(),
    val quickReplies: List<String> = emptyList(),
    val isSafetyAlert: Boolean = false
)

enum class MessageSender {
    USER,
    BOT,
    SYSTEM
}

object ChatKnowledgeBase {

    val initialGreetings = listOf(
        "Muli bwanji! 👋 I'm **BedSpace AI Advisor**.",
        "I can help you find verified boarding houses near **UNZA, CBU, Evelyn Hone, Apex, Cavendish, and ZCAS**, check prices in Kwacha (ZMW), explain scam prevention, or assist landlords with NRC verification.",
        "What accommodation are you looking for today?"
    ).joinToString("\n\n")

    val quickQuestions = listOf(
        "Rooms near UNZA under K2,000",
        "How does Contact Protection work?",
        "Avoid mobile money rental scams",
        "Boarding houses near CBU Kitwe",
        "Landlord NRC verification steps"
    )

    fun getLocalResponse(query: String): ChatMessage {
        val lower = query.lowercase()
        val allProps = NeonRepository.properties.value

        return when {
            lower.contains("unza") || lower.contains("great east") || lower.contains("kalingalinga") -> {
                val unzaProps = allProps.filter { it.institution.equals("UNZA", ignoreCase = true) }
                ChatMessage(
                    sender = MessageSender.BOT,
                    text = "Here are verified student accommodation options within walking distance of **UNZA (Great East Road Campus)**. All listed properties feature verified landlords, borehole water, and security guards.",
                    suggestedProperties = unzaProps,
                    quickReplies = listOf("Filter under K2,000", "Rooms with Solar Backup", "How to Book a Viewing")
                )
            }
            lower.contains("cbu") || lower.contains("kitwe") || lower.contains("riverside") -> {
                val cbuProps = allProps.filter { it.institution.equals("CBU", ignoreCase = true) }
                ChatMessage(
                    sender = MessageSender.BOT,
                    text = "Here are boarding options near **Copperbelt University (CBU Riverside Campus)** in Kitwe. Walking distance is under 10 minutes to campus lectures.",
                    suggestedProperties = cbuProps,
                    quickReplies = listOf("Shared vs Single Rooms", "Book CBU Room", "CBU Landlord Contacts")
                )
            }
            lower.contains("scam") || lower.contains("safety") || lower.contains("airtel") || lower.contains("momo") || lower.contains("deposit") -> {
                ChatMessage(
                    sender = MessageSender.BOT,
                    text = "🛡️ **BedSpace Anti-Scam Rules for Zambian Students:**\n\n" +
                            "1. **Never send Airtel Money, MTN MoMo, or Zamtel deposits** to unverified phone numbers posted on Facebook or WhatsApp groups.\n" +
                            "2. **Use BedSpace Protected Contact**: Landlord phone numbers are only unlocked once your booking inquiry is confirmed.\n" +
                            "3. **Physical Inspection First**: Always inspect the room, water pressure, and electrical backup before handing over termly rent.\n" +
                            "4. **Verified Badge**: Only transact with landlords displaying the green **Verified Landlord** badge.",
                    isSafetyAlert = true,
                    quickReplies = listOf("Find Verified Rooms", "Report Suspicious Listing", "How Contact Protection Works")
                )
            }
            lower.contains("protect") || lower.contains("contact") || lower.contains("phone") || lower.contains("whatsapp") -> {
                ChatMessage(
                    sender = MessageSender.BOT,
                    text = "🔒 **How Contact Protection Works:**\n\n" +
                            "To eliminate middleman broker fees and fake landlord deposits, BedSpace conceals personal phone and WhatsApp numbers until a student sends a booking request and the verified landlord accepts it.\n\n" +
                            "Once confirmed, both student and landlord get direct 1-tap WhatsApp and calling access.",
                    quickReplies = listOf("Browse Listings", "Submit a Booking Request", "Landlord FAQ")
                )
            }
            lower.contains("landlord") || lower.contains("nrc") || lower.contains("verify") || lower.contains("council") -> {
                ChatMessage(
                    sender = MessageSender.BOT,
                    text = "📋 **Landlord Verification Requirements:**\n\n" +
                            "To get the Green Verified Shield on BedSpaceZM, landlords must provide:\n" +
                            "• Valid Zambian **NRC (Front & Back)**\n" +
                            "• **Proof of Property Ownership** (Title deed or recent Lusaka/Kitwe City Council Rates receipt)\n" +
                            "• Physical inspection by a BedSpace campus campus rep.\n\n" +
                            "Verification takes 12–24 hours.",
                    quickReplies = listOf("Upload NRC in Portal", "List New Property", "Contact Admin")
                )
            }
            lower.contains("price") || lower.contains("cost") || lower.contains("cheap") || lower.contains("zmw") || lower.contains("kwacha") -> {
                val cheapProps = allProps.sortedBy { it.priceMonthlyKwacha }.take(3)
                ChatMessage(
                    sender = MessageSender.BOT,
                    text = "💰 Student accommodation rates in Zambia generally range from **K1,200/mo (Bedspaces)** to **K2,500/mo (Self-Contained Studios)**. Here are our most affordable verified student options right now:",
                    suggestedProperties = cheapProps,
                    quickReplies = listOf("Rooms under K1,500", "UNZA Rooms", "Evelyn Hone Rooms")
                )
            }
            else -> {
                ChatMessage(
                    sender = MessageSender.BOT,
                    text = "I'm here to help you navigate student housing in Zambia! You can search by university (UNZA, CBU, Evelyn Hone, Apex, Cavendish, ZCAS), check average prices, learn about scam safety, or submit a room inquiry.",
                    quickReplies = listOf("Rooms near UNZA", "Scam Safety Tips", "How to Book", "Cheapest Rooms")
                )
            }
        }
    }
}
