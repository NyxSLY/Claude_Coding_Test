package com.example.reddittrending.ai

import com.google.gson.annotations.SerializedName

/**
 * OpenAI / DeepSeek API 请求格式（兼容格式）
 */
data class OpenAIRequest(
    @SerializedName("model") val model: String,
    @SerializedName("messages") val messages: List<OpenAIMessage>,
    @SerializedName("max_tokens") val maxTokens: Int = 4096,
    @SerializedName("temperature") val temperature: Float = 0.7f
)

data class OpenAIMessage(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String
)

data class OpenAIResponse(
    @SerializedName("choices") val choices: List<OpenAIChoice>?,
    @SerializedName("error") val error: OpenAIError?
)

data class OpenAIChoice(
    @SerializedName("message") val message: OpenAIMessage
)

data class OpenAIError(
    @SerializedName("message") val message: String
)

/**
 * Claude API 请求格式
 */
data class ClaudeRequest(
    @SerializedName("model") val model: String,
    @SerializedName("max_tokens") val maxTokens: Int = 4096,
    @SerializedName("messages") val messages: List<ClaudeMessage>
)

data class ClaudeMessage(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String
)

data class ClaudeResponse(
    @SerializedName("content") val content: List<ClaudeContent>?,
    @SerializedName("error") val error: ClaudeError?
)

data class ClaudeContent(
    @SerializedName("type") val type: String,
    @SerializedName("text") val text: String
)

data class ClaudeError(
    @SerializedName("message") val message: String
)

/**
 * Gemini API 请求格式
 */
data class GeminiRequest(
    @SerializedName("contents") val contents: List<GeminiContent>,
    @SerializedName("generationConfig") val generationConfig: GeminiGenerationConfig = GeminiGenerationConfig()
)

data class GeminiContent(
    @SerializedName("parts") val parts: List<GeminiPart>
)

data class GeminiPart(
    @SerializedName("text") val text: String
)

data class GeminiGenerationConfig(
    @SerializedName("temperature") val temperature: Float = 0.7f,
    @SerializedName("maxOutputTokens") val maxOutputTokens: Int = 4096
)

data class GeminiResponse(
    @SerializedName("candidates") val candidates: List<GeminiCandidate>?,
    @SerializedName("error") val error: GeminiError?
)

data class GeminiCandidate(
    @SerializedName("content") val content: GeminiContent
)

data class GeminiError(
    @SerializedName("message") val message: String
)
