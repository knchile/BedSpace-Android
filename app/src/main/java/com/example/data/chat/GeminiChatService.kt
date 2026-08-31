package com.example.data.chat

import com.example.BuildConfig
import com.example.data.neon.NeonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiChatService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun getAssistantResponse(prompt: String, conversationHistory: List<ChatMessage>): ChatMessage = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        // If no valid API key or placeholder key, use local high-accuracy accommodation knowledge base
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY" || apiKey.contains("PLACEHOLDER", ignoreCase = true)) {
            return@withContext ChatKnowledgeBase.getLocalResponse(prompt)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val systemInstruction = """
                You are BedSpace AI, an intelligent, friendly, and protective student accommodation advisor in Zambia.
                BedSpaceZM connects students to verified boarding houses near Zambian campuses: UNZA (Great East Rd, Kalingalinga, Marshlands), CBU (Kitwe, Riverside), Evelyn Hone College (Lusaka Church Rd), Apex Medical University (Chalala), Cavendish University (Villa Elizabetha), ZCAS University (Dedan Kimathi Rd), and Mulungushi University.
                
                Key principles:
                - Currency is Zambian Kwacha (ZMW or K).
                - Strongly advise students NEVER to send unverified Airtel Money, MTN MoMo, or Zamtel deposits before physical viewing.
                - Explain that BedSpace protects contact details until verified landlords confirm booking requests to eliminate broker scams.
                - Landlords must provide NRC and City Council Rates/Title Deed to get verified.
                - Keep answers concise, clear, and student-focused with bullet points.
            """.trimIndent()

            val contentsArray = JSONArray()
            // Add previous recent messages
            conversationHistory.takeLast(6).forEach { msg ->
                val role = if (msg.sender == MessageSender.USER) "user" else "model"
                val partObj = JSONObject().put("text", msg.text)
                val contentObj = JSONObject()
                    .put("role", role)
                    .put("parts", JSONArray().put(partObj))
                contentsArray.put(contentObj)
            }

            // Current prompt
            val currentPartObj = JSONObject().put("text", prompt)
            val currentContentObj = JSONObject()
                .put("role", "user")
                .put("parts", JSONArray().put(currentPartObj))
            contentsArray.put(currentContentObj)

            val jsonBody = JSONObject().apply {
                put("contents", contentsArray)
                put("systemInstruction", JSONObject().put("parts", JSONArray().put(JSONObject().put("text", systemInstruction))))
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("maxOutputTokens", 800)
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBodyStr = response.body?.string() ?: ""
                val responseJson = JSONObject(responseBodyStr)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val text = parts?.optJSONObject(0)?.optString("text") ?: ""

                    // Check if prompt matches campus properties to attach
                    val lower = prompt.lowercase()
                    val suggested = if (lower.contains("unza")) {
                        NeonRepository.properties.value.filter { it.institution.equals("UNZA", ignoreCase = true) }
                    } else if (lower.contains("cbu")) {
                        NeonRepository.properties.value.filter { it.institution.equals("CBU", ignoreCase = true) }
                    } else if (lower.contains("cheap") || lower.contains("price")) {
                        NeonRepository.properties.value.sortedBy { it.priceMonthlyKwacha }.take(2)
                    } else emptyList()

                    return@withContext ChatMessage(
                        sender = MessageSender.BOT,
                        text = text.ifBlank { "I'm here to help you find safe, verified student accommodation across Zambia!" },
                        suggestedProperties = suggested,
                        quickReplies = listOf("Find UNZA Rooms", "Safety & Scam Protection", "Landlord Verification Info")
                    )
                }
            }
            // Fallback to local response on non-200
            ChatKnowledgeBase.getLocalResponse(prompt)
        } catch (e: Exception) {
            ChatKnowledgeBase.getLocalResponse(prompt)
        }
    }
}
