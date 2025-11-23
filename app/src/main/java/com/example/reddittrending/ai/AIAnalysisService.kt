package com.example.reddittrending.ai

import com.example.reddittrending.model.RedditPost
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * AI分析服务
 * 支持多种LLM提供商进行Reddit帖子分析
 */
class AIAnalysisService {

    private val gson = Gson()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * 分析Reddit帖子并生成总结
     */
    suspend fun analyzeAndSummarize(
        posts: List<RedditPost>,
        postsWithComments: List<PostWithComments>,
        config: LLMConfig
    ): Result<AIAnalysisResult> = withContext(Dispatchers.IO) {
        try {
            val prompt = buildAnalysisPrompt(posts, postsWithComments)
            val response = callLLM(prompt, config)

            response.fold(
                onSuccess = { content ->
                    val result = parseAIResponse(content, posts)
                    Result.success(result)
                },
                onFailure = { e ->
                    Result.failure(e)
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 构建分析Prompt
     */
    private fun buildAnalysisPrompt(
        posts: List<RedditPost>,
        postsWithComments: List<PostWithComments>
    ): String {
        val postsContent = buildString {
            appendLine("以下是Reddit上的热门帖子及其讨论内容：")
            appendLine()

            postsWithComments.forEachIndexed { index, post ->
                appendLine("=" .repeat(50))
                appendLine("【帖子 ${index + 1}】")
                appendLine("标题: ${post.title}")
                appendLine("来源: r/${post.subreddit}")
                appendLine("热度: ${post.score}分, ${post.numComments}条评论")
                appendLine("链接: ${post.url}")
                appendLine()

                if (!post.selfText.isNullOrBlank()) {
                    appendLine("正文内容:")
                    appendLine(post.selfText.take(2000))
                    appendLine()
                }

                if (post.comments.isNotEmpty()) {
                    appendLine("热门评论:")
                    post.comments.take(10).forEach { comment ->
                        appendLine("  - [${comment.score}分] ${comment.author}: ${comment.body.take(500)}")
                        // 添加子评论
                        comment.replies.take(3).forEach { reply ->
                            appendLine("    └─ [${reply.score}分] ${reply.author}: ${reply.body.take(300)}")
                        }
                    }
                }
                appendLine()
            }

            // 补充没有评论详情的帖子
            val postsWithCommentsIds = postsWithComments.map { it.id }.toSet()
            posts.filter { it.id !in postsWithCommentsIds }.take(10).forEach { post ->
                appendLine("=" .repeat(50))
                appendLine("【补充帖子】")
                appendLine("标题: ${post.title}")
                appendLine("来源: r/${post.subreddit}")
                appendLine("热度: ${post.score}分, ${post.numComments}条评论")
                appendLine("链接: ${post.getFullUrl()}")
                if (!post.selfText.isNullOrBlank()) {
                    appendLine("正文: ${post.selfText.take(500)}")
                }
                appendLine()
            }
        }

        return """
$postsContent

---

请根据以上Reddit帖子和讨论内容，进行深度分析并生成总结报告。

要求：
1. **总体概述**：用2-3句话概括今天Reddit上讨论的主要方向和氛围

2. **热门话题分析**：识别出3-5个主要话题，对每个话题：
   - 给出简洁的话题标题
   - 总结该话题的核心讨论内容（不只是帖子标题，要分析评论中的观点）
   - 分析社区的主流观点和情感倾向（支持/反对/中立）
   - 列出2-3个关键讨论点
   - 注明相关原帖来源（subreddit和帖子标题）

3. **有趣观点**：挑选1-2个有见地的评论或独特观点

4. **争议话题**：如果有明显的争议性讨论，简要说明各方立场

请用中文回复，格式清晰，使用Markdown格式。在每个话题后附上相关原帖的链接。
""".trimIndent()
    }

    /**
     * 调用LLM API
     */
    private suspend fun callLLM(prompt: String, config: LLMConfig): Result<String> {
        return when (config.provider) {
            LLMProvider.OPENAI -> callOpenAI(prompt, config)
            LLMProvider.DEEPSEEK -> callDeepSeek(prompt, config)
            LLMProvider.CLAUDE -> callClaude(prompt, config)
            LLMProvider.GEMINI -> callGemini(prompt, config)
        }
    }

    private suspend fun callOpenAI(prompt: String, config: LLMConfig): Result<String> {
        return try {
            val api = createRetrofit(LLMProvider.OPENAI.baseUrl).create(OpenAIApi::class.java)
            val request = OpenAIRequest(
                model = config.getModel(),
                messages = listOf(
                    OpenAIMessage("system", "你是一个专业的内容分析师，擅长分析社交媒体讨论并提供有价值的总结。"),
                    OpenAIMessage("user", prompt)
                )
            )
            val response = api.chatCompletion("Bearer ${config.apiKey}", request)

            if (response.error != null) {
                Result.failure(Exception("OpenAI Error: ${response.error.message}"))
            } else {
                val content = response.choices?.firstOrNull()?.message?.content
                    ?: throw Exception("Empty response from OpenAI")
                Result.success(content)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun callDeepSeek(prompt: String, config: LLMConfig): Result<String> {
        return try {
            val api = createRetrofit(LLMProvider.DEEPSEEK.baseUrl).create(OpenAIApi::class.java)
            val request = OpenAIRequest(
                model = config.getModel(),
                messages = listOf(
                    OpenAIMessage("system", "你是一个专业的内容分析师，擅长分析社交媒体讨论并提供有价值的总结。"),
                    OpenAIMessage("user", prompt)
                )
            )
            val response = api.chatCompletion("Bearer ${config.apiKey}", request)

            if (response.error != null) {
                Result.failure(Exception("DeepSeek Error: ${response.error.message}"))
            } else {
                val content = response.choices?.firstOrNull()?.message?.content
                    ?: throw Exception("Empty response from DeepSeek")
                Result.success(content)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun callClaude(prompt: String, config: LLMConfig): Result<String> {
        return try {
            val api = createRetrofit(LLMProvider.CLAUDE.baseUrl).create(ClaudeApi::class.java)
            val request = ClaudeRequest(
                model = config.getModel(),
                messages = listOf(
                    ClaudeMessage("user", prompt)
                )
            )
            val response = api.createMessage(config.apiKey, request = request)

            if (response.error != null) {
                Result.failure(Exception("Claude Error: ${response.error.message}"))
            } else {
                val content = response.content?.firstOrNull { it.type == "text" }?.text
                    ?: throw Exception("Empty response from Claude")
                Result.success(content)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun callGemini(prompt: String, config: LLMConfig): Result<String> {
        return try {
            val api = createRetrofit(LLMProvider.GEMINI.baseUrl).create(GeminiApi::class.java)
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(prompt)))
                )
            )
            val response = api.generateContent(config.getModel(), config.apiKey, request)

            if (response.error != null) {
                Result.failure(Exception("Gemini Error: ${response.error.message}"))
            } else {
                val content = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: throw Exception("Empty response from Gemini")
                Result.success(content)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * 解析AI响应
     */
    private fun parseAIResponse(content: String, posts: List<RedditPost>): AIAnalysisResult {
        // 直接返回AI生成的Markdown内容
        // 同时提取原帖引用信息
        val postRefs = posts.map { post ->
            PostReference(
                title = post.title,
                subreddit = post.subreddit,
                url = post.getFullUrl(),
                score = post.score,
                numComments = post.numComments
            )
        }

        return AIAnalysisResult(
            summary = content,
            hotTopics = listOf(
                HotTopic(
                    title = "AI生成的完整分析",
                    summary = content,
                    sentiment = "neutral",
                    discussionPoints = emptyList(),
                    originalPosts = postRefs
                )
            )
        )
    }
}
