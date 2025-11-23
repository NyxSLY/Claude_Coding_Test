package com.example.reddittrending.data

import com.example.reddittrending.api.RetrofitClient
import com.example.reddittrending.model.RedditPost
import com.example.reddittrending.model.SortType
import com.example.reddittrending.model.TimeFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

/**
 * Reddit数据仓库
 * 负责从API获取数据并进行处理
 */
class RedditRepository {

    private val api = RetrofitClient.redditApi

    /**
     * 获取多个subreddits的热门帖子
     * @param subreddits subreddit名称列表
     * @param timeFilter 时间范围
     * @param sortType 排序方式
     * @param limit 每个subreddit获取的帖子数量
     */
    suspend fun getTrendingPosts(
        subreddits: List<String>,
        timeFilter: TimeFilter,
        sortType: SortType,
        limit: Int = 25
    ): Result<List<RedditPost>> = withContext(Dispatchers.IO) {
        try {
            if (subreddits.isEmpty()) {
                return@withContext Result.failure(IllegalArgumentException("请至少输入一个subreddit"))
            }

            // 合并所有subreddits用+连接
            val combinedSubreddits = subreddits.joinToString("+") { it.trim() }

            val response = when (sortType) {
                SortType.HOT -> api.getMultiSubredditHotPosts(
                    subreddits = combinedSubreddits,
                    limit = limit * subreddits.size
                )
                SortType.TOP -> api.getMultiSubredditTopPosts(
                    subreddits = combinedSubreddits,
                    timeFilter = timeFilter.value,
                    limit = limit * subreddits.size
                )
                SortType.NEW -> api.getNewPosts(
                    subreddit = combinedSubreddits,
                    limit = limit * subreddits.size
                )
                SortType.RISING -> api.getRisingPosts(
                    subreddit = combinedSubreddits,
                    limit = limit * subreddits.size
                )
            }

            val posts = response.data.children
                .map { it.data }
                .filter { !it.isStickied } // 过滤置顶帖
                .filter { filterByTime(it, timeFilter) } // 按时间过滤

            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 分别获取每个subreddit的帖子然后合并（备用方法）
     */
    suspend fun getTrendingPostsSeparately(
        subreddits: List<String>,
        timeFilter: TimeFilter,
        sortType: SortType,
        limit: Int = 25
    ): Result<List<RedditPost>> = withContext(Dispatchers.IO) {
        try {
            if (subreddits.isEmpty()) {
                return@withContext Result.failure(IllegalArgumentException("请至少输入一个subreddit"))
            }

            val allPosts = subreddits.map { subreddit ->
                async {
                    try {
                        val response = when (sortType) {
                            SortType.HOT -> api.getHotPosts(subreddit.trim(), limit)
                            SortType.TOP -> api.getTopPosts(subreddit.trim(), timeFilter.value, limit)
                            SortType.NEW -> api.getNewPosts(subreddit.trim(), limit)
                            SortType.RISING -> api.getRisingPosts(subreddit.trim(), limit)
                        }
                        response.data.children.map { it.data }
                    } catch (e: Exception) {
                        emptyList()
                    }
                }
            }.awaitAll().flatten()

            val filteredPosts = allPosts
                .filter { !it.isStickied }
                .filter { filterByTime(it, timeFilter) }
                .sortedByDescending { it.score }

            Result.success(filteredPosts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 根据时间范围过滤帖子
     */
    private fun filterByTime(post: RedditPost, timeFilter: TimeFilter): Boolean {
        val now = System.currentTimeMillis() / 1000
        val postAge = now - post.createdUtc

        return when (timeFilter) {
            TimeFilter.HOUR -> postAge <= 3600
            TimeFilter.DAY -> postAge <= 86400
            TimeFilter.WEEK -> postAge <= 604800
            TimeFilter.MONTH -> postAge <= 2592000
            TimeFilter.YEAR -> postAge <= 31536000
            TimeFilter.ALL -> true
        }
    }
}
