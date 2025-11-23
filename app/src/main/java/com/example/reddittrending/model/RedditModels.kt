package com.example.reddittrending.model

import com.google.gson.annotations.SerializedName

/**
 * Reddit API响应的数据模型
 */

data class RedditResponse(
    @SerializedName("data") val data: RedditData
)

data class RedditData(
    @SerializedName("children") val children: List<RedditChild>,
    @SerializedName("after") val after: String?,
    @SerializedName("before") val before: String?
)

data class RedditChild(
    @SerializedName("kind") val kind: String,
    @SerializedName("data") val data: RedditPost
)

data class RedditPost(
    @SerializedName("id") val id: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("selftext") val selfText: String? = null,
    @SerializedName("body") val body: String? = null, // 评论内容字段
    @SerializedName("author") val author: String = "[deleted]",
    @SerializedName("subreddit") val subreddit: String = "",
    @SerializedName("subreddit_name_prefixed") val subredditPrefixed: String = "",
    @SerializedName("score") val score: Int = 0,
    @SerializedName("upvote_ratio") val upvoteRatio: Float = 0f,
    @SerializedName("num_comments") val numComments: Int = 0,
    @SerializedName("created_utc") val createdUtc: Long = 0,
    @SerializedName("url") val url: String = "",
    @SerializedName("permalink") val permalink: String = "",
    @SerializedName("thumbnail") val thumbnail: String? = null,
    @SerializedName("preview") val preview: Preview? = null,
    @SerializedName("is_self") val isSelf: Boolean = false,
    @SerializedName("over_18") val isNsfw: Boolean = false,
    @SerializedName("stickied") val isStickied: Boolean = false,
    @SerializedName("link_flair_text") val flairText: String? = null
) {
    fun getFullUrl(): String = "https://www.reddit.com$permalink"

    fun getTimeAgo(): String {
        val now = System.currentTimeMillis() / 1000
        val diff = now - createdUtc
        return when {
            diff < 60 -> "刚刚"
            diff < 3600 -> "${diff / 60}分钟前"
            diff < 86400 -> "${diff / 3600}小时前"
            else -> "${diff / 86400}天前"
        }
    }

    fun getFormattedScore(): String {
        return when {
            score >= 1000000 -> String.format("%.1fM", score / 1000000.0)
            score >= 1000 -> String.format("%.1fK", score / 1000.0)
            else -> score.toString()
        }
    }
}

data class Preview(
    @SerializedName("images") val images: List<PreviewImage>?
)

data class PreviewImage(
    @SerializedName("source") val source: ImageSource?,
    @SerializedName("resolutions") val resolutions: List<ImageSource>?
)

data class ImageSource(
    @SerializedName("url") val url: String,
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int
) {
    fun getDecodedUrl(): String = url.replace("&amp;", "&")
}

/**
 * 时间范围枚举
 */
enum class TimeFilter(val value: String, val displayName: String) {
    HOUR("hour", "1小时"),
    DAY("day", "24小时"),
    WEEK("week", "一周"),
    MONTH("month", "一个月"),
    YEAR("year", "一年"),
    ALL("all", "全部时间")
}

/**
 * 排序方式枚举
 */
enum class SortType(val value: String, val displayName: String) {
    HOT("hot", "热门"),
    NEW("new", "最新"),
    TOP("top", "最高分"),
    RISING("rising", "上升中")
}
