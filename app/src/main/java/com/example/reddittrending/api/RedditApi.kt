package com.example.reddittrending.api

import com.example.reddittrending.model.RedditResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Reddit API接口定义
 * 使用Reddit的公开JSON API（无需认证）
 */
interface RedditApi {

    /**
     * 获取subreddit的热门帖子
     */
    @GET("r/{subreddit}/hot.json")
    suspend fun getHotPosts(
        @Path("subreddit") subreddit: String,
        @Query("limit") limit: Int = 25,
        @Query("after") after: String? = null
    ): RedditResponse

    /**
     * 获取subreddit的最新帖子
     */
    @GET("r/{subreddit}/new.json")
    suspend fun getNewPosts(
        @Path("subreddit") subreddit: String,
        @Query("limit") limit: Int = 25,
        @Query("after") after: String? = null
    ): RedditResponse

    /**
     * 获取subreddit的最高分帖子（支持时间范围筛选）
     */
    @GET("r/{subreddit}/top.json")
    suspend fun getTopPosts(
        @Path("subreddit") subreddit: String,
        @Query("t") timeFilter: String = "day",
        @Query("limit") limit: Int = 25,
        @Query("after") after: String? = null
    ): RedditResponse

    /**
     * 获取subreddit的上升帖子
     */
    @GET("r/{subreddit}/rising.json")
    suspend fun getRisingPosts(
        @Path("subreddit") subreddit: String,
        @Query("limit") limit: Int = 25,
        @Query("after") after: String? = null
    ): RedditResponse

    /**
     * 搜索多个subreddits的帖子
     */
    @GET("r/{subreddits}/top.json")
    suspend fun getMultiSubredditTopPosts(
        @Path("subreddits") subreddits: String, // 格式: sub1+sub2+sub3
        @Query("t") timeFilter: String = "day",
        @Query("limit") limit: Int = 50,
        @Query("after") after: String? = null
    ): RedditResponse

    /**
     * 获取多个subreddits的热门帖子
     */
    @GET("r/{subreddits}/hot.json")
    suspend fun getMultiSubredditHotPosts(
        @Path("subreddits") subreddits: String,
        @Query("limit") limit: Int = 50,
        @Query("after") after: String? = null
    ): RedditResponse

    /**
     * 获取帖子详情和评论
     * 返回数组：[0]是帖子信息，[1]是评论
     */
    @GET("r/{subreddit}/comments/{postId}.json")
    suspend fun getPostComments(
        @Path("subreddit") subreddit: String,
        @Path("postId") postId: String,
        @Query("limit") limit: Int = 50,
        @Query("depth") depth: Int = 3,
        @Query("sort") sort: String = "top"
    ): List<RedditResponse>
}
