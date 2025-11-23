package com.example.reddittrending.ai

import retrofit2.http.*

/**
 * OpenAI API (也用于DeepSeek，格式兼容)
 */
interface OpenAIApi {
    @POST("chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: OpenAIRequest
    ): OpenAIResponse
}

/**
 * Claude API
 */
interface ClaudeApi {
    @POST("messages")
    suspend fun createMessage(
        @Header("x-api-key") apiKey: String,
        @Header("anthropic-version") version: String = "2023-06-01",
        @Body request: ClaudeRequest
    ): ClaudeResponse
}

/**
 * Gemini API
 */
interface GeminiApi {
    @POST("models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}
