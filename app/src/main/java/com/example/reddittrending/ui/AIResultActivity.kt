package com.example.reddittrending.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.reddittrending.databinding.ActivityAiResultBinding
import io.noties.markwon.Markwon

/**
 * AI分析结果展示页面
 */
class AIResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAiResultBinding
    private lateinit var markwon: Markwon

    companion object {
        private const val EXTRA_CONTENT = "extra_content"

        fun start(context: Context, content: String) {
            val intent = Intent(context, AIResultActivity::class.java).apply {
                putExtra(EXTRA_CONTENT, content)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAiResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupMarkdown()
        displayContent()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "AI分析报告"
    }

    private fun setupMarkdown() {
        markwon = Markwon.create(this)
    }

    private fun displayContent() {
        val content = intent.getStringExtra(EXTRA_CONTENT) ?: "暂无分析内容"
        markwon.setMarkdown(binding.tvContent, content)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
