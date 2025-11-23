package com.example.reddittrending.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reddittrending.data.RedditRepository
import com.example.reddittrending.model.RedditPost
import com.example.reddittrending.model.SortType
import com.example.reddittrending.model.TimeFilter
import kotlinx.coroutines.launch

/**
 * 主界面ViewModel
 */
class MainViewModel : ViewModel() {

    private val repository = RedditRepository()

    // 帖子列表
    private val _posts = MutableLiveData<List<RedditPost>>()
    val posts: LiveData<List<RedditPost>> = _posts

    // 加载状态
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // 错误信息
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // 当前设置
    private val _subreddits = MutableLiveData<List<String>>(emptyList())
    val subreddits: LiveData<List<String>> = _subreddits

    private var currentTimeFilter = TimeFilter.DAY
    private var currentSortType = SortType.HOT

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
     * 清除错误
     */
    fun clearError() {
        _error.value = null
    }
}
