package com.example.data

import com.example.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

// Custom Data Classes for Moshi instead of kotlinx.serialization to bypass Gradle plugin setups
data class GeminiPart(val text: String)
data class GeminiContent(val parts: List<GeminiPart>, val role: String = "user")
data class GeminiGenerateRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null
)

data class GeminiCandidate(val content: GeminiContent)
data class GeminiGenerateResponse(val candidates: List<GeminiCandidate>?)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiGenerateRequest
    ): GeminiGenerateResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    suspend fun generateCoachFeedback(
        prompt: String,
        history: List<ChatLogEntity>,
        userProfile: UserProfileEntity,
        tone: String
    ): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "ERROR: API Key is unconfigured. Please configure your GEMINI_API_KEY inside the Secrets Panel of Google AI Studio."
        }

        // Construct System Prompt based on selected Coach Persona
        val systemPrompt = """
            You are the ASCEND Tactical Mentor, an elite military mental coach, performance analyst, and esports strategist for real life. 
            Your tone is strictly: $tone.
            The user is known as Operative: '${userProfile.username}' of class '${userProfile.characterClass}'.
            Their current real-time stats: Rank: ${userProfile.rank} Division ${userProfile.division} (RR: ${userProfile.rankRating}/100), Level: ${userProfile.level}, Streak: ${userProfile.streak} days, Active Burnout: ${(userProfile.burnout * 100).toInt()}%, Core Momentum: ${(userProfile.momentum * 100).toInt()}%.
            
            Always keep your feedback extremely punchy, military-tactical, gamified, and direct. Use short lines (max 2-3 brief paragraphs). Avoid generic wellness speak. Address them in terms of contracts, MMR, operations, RR penalty hazards, and absolute discipline. Analyze their performance metrics critically.
        """.trimIndent()

        // Construct history contents
        val contents = mutableListOf<GeminiContent>()
        
        // Take last 8 history items to protect context token limits and latency
        val recentLogs = history.takeLast(8)
        for (log in recentLogs) {
            val roleName = if (log.sender == "PLAYER") "user" else "model"
            contents.add(GeminiContent(parts = listOf(GeminiPart(log.message)), role = roleName))
        }

        // Add the current item
        contents.add(GeminiContent(parts = listOf(GeminiPart(prompt)), role = "user"))

        val request = GeminiGenerateRequest(
            contents = contents,
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(systemPrompt)), role = "user")
        )

        return try {
            val response = service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "Coach is offline. Check connection telemetry or secure your API key."
        } catch (e: Exception) {
            e.printStackTrace()
            "SYSTEM RESPONSE ERROR: Could not establish secure proxy downlink to Coach AI. Reason: ${e.localizedMessage ?: "Unknown network interruption"}."
        }
    }
}
