package com.example.reddittrending.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.reddittrending.ai.AIAnalysisResult
import com.example.reddittrending.ai.AIAnalysisService
import com.example.reddittrending.ai.PostWithComments
import com.example.reddittrending.data.RedditRepository
import com.example.reddittrending.data.SettingsManager
import com.example.reddittrending.model.RedditPost
import com.example.reddittrending.model.SortType
import com.example.reddittrending.model.TimeFilter
import kotlinx.coroutines.launch

/**
 * 主界面ViewModel
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RedditRepository()
    private val aiService = AIAnalysisService()
    private val settingsManager = SettingsManager(application)

    // 帖子列表
    private val _posts = MutableLiveData<List<RedditPost>>()
    val posts: LiveData<List<RedditPost>> = _posts

    // 加载状态
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // AI分析状态
    private val _isAnalyzing = MutableLiveData<Boolean>()
    val isAnalyzing: LiveData<Boolean> = _isAnalyzing

    // 错误信息
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // AI分析结果
    private val _aiAnalysis = MutableLiveData<AIAnalysisResult?>()
    val aiAnalysis: LiveData<AIAnalysisResult?> = _aiAnalysis

    // 当前设置
    private val _subreddits = MutableLiveData<List<String>>(emptyList())
    val subreddits: LiveData<List<String>> = _subreddits

    private var currentTimeFilter = TimeFilter.DAY
    private var currentSortType = SortType.HOT

    // 缓存帖子详情
    private var cachedPostsWithComments: List<PostWithComments> = emptyList()

    /**
     * 添加subreddit
     */
    fun addSubreddit(subreddit: String) {
        val current = _subreddits.value?.toMutableList() ?: mutableListOf()
        val cleanName = subreddit.trim().removePrefix("r/").removePrefix("/r/")
        if (cleanName.isNotEmpty() && !current.contains(cleanName)) {
            current.add(cleanName)
            _subreddits.value = current
        }
    }

    /**
     * 移除subreddit
     */
    fun removeSubreddit(subreddit: String) {
        val current = _subreddits.value?.toMutableList() ?: mutableListOf()
        current.remove(subreddit)
        _subreddits.value = current
    }

    /**
     * 设置时间范围
     */
    fun setTimeFilter(timeFilter: TimeFilter) {
        currentTimeFilter = timeFilter
    }

    /**
     * 设置排序方式
     */
    fun setSortType(sortType: SortType) {
        currentSortType = sortType
    }

    /**
     * 获取当前时间范围
     */
    fun getTimeFilter(): TimeFilter = currentTimeFilter

    /**
     * 获取当前排序方式
     */
    fun getSortType(): SortType = currentSortType

    /**
     * 是否启用AI分析
     */
    fun isAIEnabled(): Boolean = settingsManager.isAIEnabled() && settingsManager.isConfigured()

    /**
     * 加载热门帖子
     */
    fun loadTrendingPosts() {
        val subs = _subreddits.value
        if (subs.isNullOrEmpty()) {
            _error.value = "请先添加至少一个subreddit"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _aiAnalysis.value = null

            val result = repository.getTrendingPosts(
                subreddits = subs,
                timeFilter = currentTimeFilter,
                sortType = currentSortType,
                limit = 25
            )

            result.fold(
                onSuccess = { posts ->
                    _posts.value = posts
                    if (posts.isEmpty()) {
                        _error.value = "没有找到帖子，请尝试更换时间范围或subreddit"
                    } else if (isAIEnabled()) {
                        // 自动进行AI分析
                        performAIAnalysis(posts)
                    }
                },
                onFailure = { exception ->
                    _error.value = "加载失败: ${exception.message}"
                }
            )

            _isLoading.value = false
        }
    }

    /**
     * 执行AI分析
     */
    fun performAIAnalysis(posts: List<RedditPost>? = null) {
        val postsToAnalyze = posts ?: _posts.value
        if (postsToAnalyze.isNullOrEmpty()) {
            _error.value = "没有帖子可供分析"
            return
        }

        val config = settingsManager.getLLMConfig()
        if (config == null) {
            _error.value = "请先在设置中配置AI API密钥"
            return
        }

        viewModelScope.launch {
            _isAnalyzing.value = true

            try {
                // 获取热门帖子的评论
                val postsWithComments = repository.getPostsWithComments(
                    posts = postsToAnalyze,
                    maxPosts = 10
                )
                cachedPostsWithComments = postsWithComments

                // 调用AI分析
                val result = aiService.analyzeAndSummarize(
                    posts = postsToAnalyze,
                    postsWithComments = postsWithComments,
                    config = config
                )

                result.fold(
                    onSuccess = { analysis ->
                        _aiAnalysis.value = analysis
                    },
                    onFailure = { exception ->
                        _error.value = "AI分析失败: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                _error.value = "AI分析出错: ${e.message}"
            }

            _isAnalyzing.value = false
        }
    }

    /**
     * 清除错误
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * 清除AI分析结果
     */
    fun clearAIAnalysis() {
        _aiAnalysis.value = null
    }
}
