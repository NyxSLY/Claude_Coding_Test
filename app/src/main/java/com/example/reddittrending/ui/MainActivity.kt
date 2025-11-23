package com.example.reddittrending.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reddittrending.R
import com.example.reddittrending.databinding.ActivityMainBinding
import com.example.reddittrending.model.RedditPost
import com.example.reddittrending.model.SortType
import com.example.reddittrending.model.TimeFilter
import com.google.android.material.chip.Chip

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        // 设置时间范围下拉菜单
        val timeFilters = TimeFilter.values().map { it.displayName }
        val timeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, timeFilters)
        binding.spinnerTimeFilter.setAdapter(timeAdapter)
        binding.spinnerTimeFilter.setText(TimeFilter.DAY.displayName, false)
        binding.spinnerTimeFilter.setOnItemClickListener { _, _, position, _ ->
            viewModel.setTimeFilter(TimeFilter.values()[position])
        }

        // 设置排序方式下拉菜单
        val sortTypes = SortType.values().map { it.displayName }
        val sortAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, sortTypes)
        binding.spinnerSortType.setAdapter(sortAdapter)
        binding.spinnerSortType.setText(SortType.HOT.displayName, false)
        binding.spinnerSortType.setOnItemClickListener { _, _, position, _ ->
            viewModel.setSortType(SortType.values()[position])
        }

        // 设置subreddit输入
        binding.etSubreddit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addSubreddit()
                true
            } else {
                false
            }
        }

        binding.btnAddSubreddit.setOnClickListener {
            addSubreddit()
        }

        // 设置搜索按钮
        binding.btnSearch.setOnClickListener {
            viewModel.loadTrendingPosts()
        }

        // 设置RecyclerView
        postAdapter = PostAdapter { post ->
            openPost(post)
        }
        binding.recyclerPosts.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = postAdapter
        }

        // 设置下拉刷新
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadTrendingPosts()
        }

        // 添加一些默认的热门subreddits作为建议
        setupSuggestionChips()
    }

    private fun setupSuggestionChips() {
        val suggestions = listOf("technology", "programming", "worldnews", "science", "android")
        suggestions.forEach { suggestion ->
            val chip = Chip(this).apply {
                text = "r/$suggestion"
                isClickable = true
                setOnClickListener {
                    viewModel.addSubreddit(suggestion)
                }
            }
            binding.chipGroupSuggestions.addView(chip)
        }
    }

    private fun addSubreddit() {
        val subreddit = binding.etSubreddit.text.toString().trim()
        if (subreddit.isNotEmpty()) {
            viewModel.addSubreddit(subreddit)
            binding.etSubreddit.text?.clear()
        }
    }

    private fun setupObservers() {
        // 观察subreddits变化
        viewModel.subreddits.observe(this) { subreddits ->
            updateSubredditChips(subreddits)
        }

        // 观察帖子列表
        viewModel.posts.observe(this) { posts ->
            postAdapter.submitList(posts)
            binding.tvResultCount.text = "找到 ${posts.size} 个热门帖子"
            binding.tvResultCount.visibility = View.VISIBLE
        }

        // 观察加载状态
        viewModel.isLoading.observe(this) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
            binding.btnSearch.isEnabled = !isLoading
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // 观察错误信息
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    private fun updateSubredditChips(subreddits: List<String>) {
        binding.chipGroupSelected.removeAllViews()
        subreddits.forEach { subreddit ->
            val chip = Chip(this).apply {
                text = "r/$subreddit"
                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    viewModel.removeSubreddit(subreddit)
                }
            }
            binding.chipGroupSelected.addView(chip)
        }
        binding.tvSelectedLabel.visibility = if (subreddits.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun openPost(post: RedditPost) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.getFullUrl()))
        startActivity(intent)
    }
}
