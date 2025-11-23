package com.example.reddittrending.ai

/**
 * 支持的LLM提供商
 */
enum class LLMProvider(
    val displayName: String,
    val baseUrl: String,
    val modelName: String
) {
    OPENAI(
        displayName = "ChatGPT (OpenAI)",
        baseUrl = "https://api.openai.com/v1/",
        modelName = "gpt-4o-mini"
    ),
    CLAUDE(
        displayName = "Claude (Anthropic)",
        baseUrl = "https://api.anthropic.com/v1/",
        modelName = "claude-3-5-sonnet-20241022"
    ),
    GEMINI(
        displayName = "Gemini (Google)",
        baseUrl = "https://generativelanguage.googleapis.com/v1beta/",
        modelName = "gemini-1.5-flash"
    ),
    DEEPSEEK(
        displayName = "DeepSeek",
        baseUrl = "https://api.deepseek.com/v1/",
        modelName = "deepseek-chat"
    );

    companion object {
        fun fromName(name: String): LLMProvider? {
            return values().find { it.name == name }
        }
    }
}

/**
 * LLM配置
 */
data class LLMConfig(
    val provider: LLMProvider,
    val apiKey: String,
    val customModel: String? = null
) {
    fun getModel(): String = customModel ?: provider.modelName
}

/**
 * AI分析结果
 */
data class AIAnalysisResult(
    val summary: String,           // 总结内容
    val hotTopics: List<HotTopic>, // 热门话题列表
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 热门话题
 */
data class HotTopic(
    val title: String,             // 话题标题
    val summary: String,           // 话题摘要
    val sentiment: String,         // 情感倾向: positive/negative/neutral
    val discussionPoints: List<String>, // 主要讨论点
    val originalPosts: List<PostReference> // 原帖引用
)

/**
 * 原帖引用
 */
data class PostReference(
    val title: String,
    val subreddit: String,
    val url: String,
    val score: Int,
    val numComments: Int
)

/**
 * Reddit帖子详情（含评论）
 */
data class PostWithComments(
    val id: String,
    val title: String,
    val selfText: String?,
    val author: String,
    val subreddit: String,
    val url: String,
    val score: Int,
    val numComments: Int,
    val comments: List<Comment>
)

/**
 * 评论
 */
data class Comment(
    val author: String,
    val body: String,
    val score: Int,
    val replies: List<Comment> = emptyList()
)
