package com.example.reddittrending

import com.example.reddittrending.model.RedditPost
import com.example.reddittrending.model.TimeFilter
import com.example.reddittrending.model.SortType
import com.example.reddittrending.model.ImageSource
import org.junit.Test
import org.junit.Assert.*

/**
 * 核心功能单元测试
 */
class RedditModelsTest {

    @Test
    fun `test RedditPost getFormattedScore with small number`() {
        val post = createTestPost(score = 500)
        assertEquals("500", post.getFormattedScore())
    }

    @Test
    fun `test RedditPost getFormattedScore with thousands`() {
        val post = createTestPost(score = 1500)
        assertEquals("1.5K", post.getFormattedScore())
    }

    @Test
    fun `test RedditPost getFormattedScore with millions`() {
        val post = createTestPost(score = 1500000)
        assertEquals("1.5M", post.getFormattedScore())
    }

    @Test
    fun `test RedditPost getFullUrl`() {
        val post = createTestPost(permalink = "/r/programming/comments/abc123/test_post/")
        assertEquals("https://www.reddit.com/r/programming/comments/abc123/test_post/", post.getFullUrl())
    }

    @Test
    fun `test RedditPost getTimeAgo recent`() {
        val now = System.currentTimeMillis() / 1000
        val post = createTestPost(createdUtc = now - 30) // 30秒前
        assertEquals("刚刚", post.getTimeAgo())
    }

    @Test
    fun `test RedditPost getTimeAgo minutes`() {
        val now = System.currentTimeMillis() / 1000
        val post = createTestPost(createdUtc = now - 300) // 5分钟前
        assertEquals("5分钟前", post.getTimeAgo())
    }

    @Test
    fun `test RedditPost getTimeAgo hours`() {
        val now = System.currentTimeMillis() / 1000
        val post = createTestPost(createdUtc = now - 7200) // 2小时前
        assertEquals("2小时前", post.getTimeAgo())
    }

    @Test
    fun `test RedditPost getTimeAgo days`() {
        val now = System.currentTimeMillis() / 1000
        val post = createTestPost(createdUtc = now - 172800) // 2天前
        assertEquals("2天前", post.getTimeAgo())
    }

    @Test
    fun `test ImageSource getDecodedUrl`() {
        val source = ImageSource(
            url = "https://example.com/image.jpg?width=100&amp;height=100",
            width = 100,
            height = 100
        )
        assertEquals("https://example.com/image.jpg?width=100&height=100", source.getDecodedUrl())
    }

    @Test
    fun `test TimeFilter values`() {
        assertEquals("hour", TimeFilter.HOUR.value)
        assertEquals("day", TimeFilter.DAY.value)
        assertEquals("week", TimeFilter.WEEK.value)
        assertEquals("month", TimeFilter.MONTH.value)
        assertEquals("year", TimeFilter.YEAR.value)
        assertEquals("all", TimeFilter.ALL.value)
    }

    @Test
    fun `test TimeFilter display names`() {
        assertEquals("1小时", TimeFilter.HOUR.displayName)
        assertEquals("24小时", TimeFilter.DAY.displayName)
        assertEquals("一周", TimeFilter.WEEK.displayName)
        assertEquals("一个月", TimeFilter.MONTH.displayName)
        assertEquals("一年", TimeFilter.YEAR.displayName)
        assertEquals("全部时间", TimeFilter.ALL.displayName)
    }

    @Test
    fun `test SortType values`() {
        assertEquals("hot", SortType.HOT.value)
        assertEquals("new", SortType.NEW.value)
        assertEquals("top", SortType.TOP.value)
        assertEquals("rising", SortType.RISING.value)
    }

    @Test
    fun `test SortType display names`() {
        assertEquals("热门", SortType.HOT.displayName)
        assertEquals("最新", SortType.NEW.displayName)
        assertEquals("最高分", SortType.TOP.displayName)
        assertEquals("上升中", SortType.RISING.displayName)
    }

    @Test
    fun `test subreddit name cleaning`() {
        // 模拟ViewModel中的清理逻辑
        val inputs = listOf("programming", "r/android", "/r/technology")
        val expected = listOf("programming", "android", "technology")

        inputs.forEachIndexed { index, input ->
            val cleaned = input.trim().removePrefix("r/").removePrefix("/r/")
            assertEquals(expected[index], cleaned)
        }
    }

    @Test
    fun `test multi-subreddit URL format`() {
        val subreddits = listOf("programming", "android", "technology")
        val combined = subreddits.joinToString("+") { it.trim() }
        assertEquals("programming+android+technology", combined)
    }

    private fun createTestPost(
        id: String = "test123",
        title: String = "Test Post",
        selfText: String? = null,
        author: String = "testuser",
        subreddit: String = "programming",
        subredditPrefixed: String = "r/programming",
        score: Int = 100,
        upvoteRatio: Float = 0.95f,
        numComments: Int = 50,
        createdUtc: Long = System.currentTimeMillis() / 1000,
        url: String = "https://example.com",
        permalink: String = "/r/programming/comments/test123/test_post/",
        thumbnail: String? = null,
        preview: com.example.reddittrending.model.Preview? = null,
        isSelf: Boolean = false,
        isNsfw: Boolean = false,
        isStickied: Boolean = false,
        flairText: String? = null
    ): RedditPost {
        return RedditPost(
            id = id,
            title = title,
            selfText = selfText,
            author = author,
            subreddit = subreddit,
            subredditPrefixed = subredditPrefixed,
            score = score,
            upvoteRatio = upvoteRatio,
            numComments = numComments,
            createdUtc = createdUtc,
            url = url,
            permalink = permalink,
            thumbnail = thumbnail,
            preview = preview,
            isSelf = isSelf,
            isNsfw = isNsfw,
            isStickied = isStickied,
            flairText = flairText
        )
    }
}
