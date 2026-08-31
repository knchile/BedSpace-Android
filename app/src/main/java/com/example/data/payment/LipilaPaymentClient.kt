package com.example.data.payment

import android.util.Log
import com.example.BuildConfig
import com.example.model.PaymentProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Lipila Payment Gateway Client (Zambia)
 * Live integration with Lipila REST API for Mobile Money (Airtel, MTN, Zamtel) and Card Collections.
 * Live Production Endpoint: https://blz.lipila.io/api/v1/collections/mobile-money
 */
object LipilaPaymentClient {

    private const val TAG = "LipilaPaymentClient"
    
    // Live API Key provided for BedSpaceZM merchant integration
    private const val DEFAULT_API_KEY = "lsk_019f41c4-269e-7529-ab2d-c3a3b099e76f"
    
    // Lipila Production & Sandbox URLs
    private const val PROD_BASE_URL = "https://blz.lipila.io"
    private const val SANDBOX_BASE_URL = "https://api.lipila.dev"

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(25, TimeUnit.SECONDS)
            .readTimeout(25, TimeUnit.SECONDS)
            .writeTimeout(25, TimeUnit.SECONDS)
            .build()
    }

    private fun getApiKey(): String {
        return try {
            val key = BuildConfig.LIPILA_API_KEY
            if (key.isNullOrBlank() || key == "MY_LIPILA_API_KEY") DEFAULT_API_KEY else key
        } catch (e: Throwable) {
            DEFAULT_API_KEY
        }
    }

    /**
     * Formats raw user input into standard Zambian MSISDN: 2609XXXXXXXX / 2607XXXXXXXX
     */
    fun formatZambianPhone(raw: String): String {
        val digits = raw.replace("[^0-9]".toRegex(), "")
        return when {
            digits.startsWith("260") && digits.length == 12 -> digits
            digits.startsWith("0") && digits.length == 10 -> "260" + digits.substring(1)
            digits.length == 9 && (digits.startsWith("9") || digits.startsWith("7")) -> "260$digits"
            else -> digits
        }
    }

    data class LipilaResponse(
        val isSuccess: Boolean,
        val transactionReference: String,
        val identifier: String? = null,
        val status: String,
        val message: String,
        val rawResponse: String? = null
    )

    /**
     * Collects Mobile Money payment via Lipila API.
     * Triggers USSD prompt to customer's mobile phone (Airtel *778#, MTN *115#, Zamtel *344#).
     */
    suspend fun collectMobileMoney(
        amountKwacha: Int,
        accountNumber: String,
        provider: PaymentProvider,
        customerName: String,
        customerEmail: String,
        narration: String,
        referenceId: String = "BSZM-${System.currentTimeMillis()}-${(100..999).random()}"
    ): LipilaResponse = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        val formattedPhone = formatZambianPhone(accountNumber)

        val names = customerName.trim().split(" ", limit = 2)
        val firstName = names.getOrElse(0) { "Student" }.ifBlank { "Student" }
        val lastName = names.getOrElse(1) { "Applicant" }.ifBlank { "User" }

        val jsonPayload = JSONObject().apply {
            put("amount", amountKwacha)
            put("currency", "ZMW")
            put("phoneNumber", formattedPhone)
            put("accountNumber", formattedPhone)
            put("narration", narration.take(100))
            put("referenceId", referenceId)
            put("firstName", firstName)
            put("lastName", lastName)
            put("email", customerEmail.ifBlank { "student@bedspace.zm" })
        }

        val requestBody = jsonPayload.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

        // Priority to production endpoint (where merchant live key is registered)
        val endpoints = listOf(
            "$PROD_BASE_URL/api/v1/collections/mobile-money",
            "$SANDBOX_BASE_URL/api/v1/collections/mobile-money"
        )

        var lastErrorMessage = "Unable to connect to Lipila Gateway"

        for (endpoint in endpoints) {
            try {
                Log.d(TAG, "Initiating Lipila collection on $endpoint with phone: $formattedPhone, ref: $referenceId")

                val request = Request.Builder()
                    .url(endpoint)
                    .addHeader("x-api-key", apiKey)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .post(requestBody)
                    .build()

                val response = httpClient.newCall(request).execute()
                val responseBodyStr = response.body?.string() ?: ""
                Log.d(TAG, "Lipila API response code: ${response.code}, body: $responseBodyStr")

                if (response.isSuccessful || response.code in 200..202) {
                    val resJson = try { JSONObject(responseBodyStr) } catch (_: Exception) { null }
                    val statusStr = resJson?.optString("status", "Pending") ?: "Pending"
                    val identifier = resJson?.optString("identifier", "")
                    val txId = resJson?.optString("referenceId", referenceId) ?: referenceId
                    val paymentType = resJson?.optString("paymentType", provider.label) ?: provider.label

                    return@withContext LipilaResponse(
                        isSuccess = true,
                        transactionReference = txId,
                        identifier = identifier,
                        status = statusStr,
                        message = "USSD Prompt sent to $formattedPhone ($paymentType). Please approve on your handset.",
                        rawResponse = responseBodyStr
                    )
                } else {
                    // Extract error detail from response
                    val errJson = try { JSONObject(responseBodyStr) } catch (_: Exception) { null }
                    val errMessage = errJson?.optString("message", "")
                    val errorsObj = errJson?.optJSONObject("errors")
                    val generalErrors = errorsObj?.optJSONArray("generalErrors")?.let { arr ->
                        (0 until arr.length()).map { arr.getString(it) }.joinToString("; ")
                    }

                    lastErrorMessage = when {
                        !generalErrors.isNullOrBlank() -> generalErrors
                        !errMessage.isNullOrBlank() -> errMessage
                        else -> "HTTP ${response.code}: $responseBodyStr"
                    }
                    Log.w(TAG, "Lipila error response: $lastErrorMessage")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Lipila endpoint $endpoint call failed: ${e.message}")
                lastErrorMessage = e.message ?: "Network timeout connecting to Lipila"
            }
        }

        // Return real failure so user is informed and not deceived by a fake confirmation
        return@withContext LipilaResponse(
            isSuccess = false,
            transactionReference = referenceId,
            status = "Failed",
            message = lastErrorMessage
        )
    }

    /**
     * Collects Card payment via Lipila API.
     */
    suspend fun collectCardPayment(
        amountKwacha: Int,
        cardNumber: String,
        customerName: String,
        customerEmail: String,
        narration: String,
        referenceId: String = "BSZM-CRD-${System.currentTimeMillis()}-${(100..999).random()}"
    ): LipilaResponse = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        val names = customerName.trim().split(" ", limit = 2)
        val firstName = names.getOrElse(0) { "Student" }.ifBlank { "Student" }
        val lastName = names.getOrElse(1) { "Applicant" }.ifBlank { "User" }

        val jsonPayload = JSONObject().apply {
            put("amount", amountKwacha)
            put("currency", "ZMW")
            put("accountNumber", cardNumber.replace(" ", ""))
            put("narration", narration.take(100))
            put("referenceId", referenceId)
            put("firstName", firstName)
            put("lastName", lastName)
            put("email", customerEmail.ifBlank { "student@bedspace.zm" })
        }

        val requestBody = jsonPayload.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

        try {
            val request = Request.Builder()
                .url("$PROD_BASE_URL/api/v1/collections/card")
                .addHeader("x-api-key", apiKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            val responseBodyStr = response.body?.string() ?: ""

            if (response.isSuccessful || response.code in 200..202) {
                val resJson = try { JSONObject(responseBodyStr) } catch (_: Exception) { null }
                val identifier = resJson?.optString("identifier", "")
                val redirectUrl = resJson?.optString("cardRedirectionUrl", "")

                return@withContext LipilaResponse(
                    isSuccess = true,
                    transactionReference = resJson?.optString("referenceId", referenceId) ?: referenceId,
                    identifier = identifier,
                    status = "Successful",
                    message = "Card payment processed via Lipila Gateway",
                    rawResponse = responseBodyStr
                )
            } else {
                return@withContext LipilaResponse(
                    isSuccess = false,
                    transactionReference = referenceId,
                    status = "Failed",
                    message = "Card payment failed: $responseBodyStr"
                )
            }
        } catch (e: Exception) {
            Log.w(TAG, "Lipila card collection error: ${e.message}")
            return@withContext LipilaResponse(
                isSuccess = false,
                transactionReference = referenceId,
                status = "Failed",
                message = e.message ?: "Card gateway network error"
            )
        }
    }
}

