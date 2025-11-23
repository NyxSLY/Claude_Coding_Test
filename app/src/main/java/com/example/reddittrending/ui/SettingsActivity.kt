package com.example.reddittrending.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reddittrending.ai.LLMProvider
import com.example.reddittrending.data.SettingsManager
import com.example.reddittrending.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingsManager = SettingsManager(this)

        setupToolbar()
        setupUI()
        loadSettings()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "AI设置"
    }

    private fun setupUI() {
        // 设置LLM提供商下拉菜单
        val providers = LLMProvider.values().map { it.displayName }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, providers)
        binding.spinnerProvider.setAdapter(adapter)

        binding.spinnerProvider.setOnItemClickListener { _, _, position, _ ->
            val provider = LLMProvider.values()[position]
            updateUIForProvider(provider)
        }

        // AI开关
        binding.switchAiEnabled.setOnCheckedChangeListener { _, isChecked ->
            binding.layoutApiSettings.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        // 保存按钮
        binding.btnSave.setOnClickListener {
            saveSettings()
        }

        // 测试按钮
        binding.btnTest.setOnClickListener {
            testConnection()
        }
    }

    private fun loadSettings() {
        // 加载AI开关状态
        binding.switchAiEnabled.isChecked = settingsManager.isAIEnabled()
        binding.layoutApiSettings.visibility =
            if (settingsManager.isAIEnabled()) View.VISIBLE else View.GONE

        // 加载LLM提供商
        val provider = settingsManager.getLLMProvider()
        binding.spinnerProvider.setText(provider.displayName, false)
        updateUIForProvider(provider)

        // 加载API密钥
        binding.etApiKey.setText(settingsManager.getApiKey(provider))

        // 加载自定义模型
        binding.etCustomModel.setText(settingsManager.getCustomModel() ?: "")
    }

    private fun updateUIForProvider(provider: LLMProvider) {
        // 更新提示文本
        binding.tilApiKey.hint = when (provider) {
            LLMProvider.OPENAI -> "OpenAI API Key (sk-...)"
            LLMProvider.CLAUDE -> "Anthropic API Key"
            LLMProvider.GEMINI -> "Google API Key"
            LLMProvider.DEEPSEEK -> "DeepSeek API Key"
        }

        // 更新默认模型提示
        binding.etCustomModel.hint = "默认: ${provider.modelName}"

        // 加载该提供商的API密钥
        binding.etApiKey.setText(settingsManager.getApiKey(provider))

        // 显示获取API密钥的帮助链接
        binding.tvApiHelp.text = when (provider) {
            LLMProvider.OPENAI -> "获取密钥: platform.openai.com/api-keys"
            LLMProvider.CLAUDE -> "获取密钥: console.anthropic.com"
            LLMProvider.GEMINI -> "获取密钥: aistudio.google.com/apikey"
            LLMProvider.DEEPSEEK -> "获取密钥: platform.deepseek.com"
        }
    }

    private fun saveSettings() {
        val providerIndex = LLMProvider.values()
            .indexOfFirst { it.displayName == binding.spinnerProvider.text.toString() }
        val provider = if (providerIndex >= 0) LLMProvider.values()[providerIndex] else LLMProvider.OPENAI

        val apiKey = binding.etApiKey.text.toString().trim()
        val customModel = binding.etCustomModel.text.toString().trim().ifBlank { null }
        val aiEnabled = binding.switchAiEnabled.isChecked

        // 保存设置
        settingsManager.setAIEnabled(aiEnabled)
        settingsManager.saveLLMProvider(provider)
        settingsManager.saveApiKey(provider, apiKey)
        settingsManager.saveCustomModel(customModel)

        Toast.makeText(this, "设置已保存", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun testConnection() {
        val apiKey = binding.etApiKey.text.toString().trim()
        if (apiKey.isBlank()) {
            Toast.makeText(this, "请先输入API密钥", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnTest.isEnabled = false
        binding.btnTest.text = "测试中..."

        // 简单验证API密钥格式
        val providerIndex = LLMProvider.values()
            .indexOfFirst { it.displayName == binding.spinnerProvider.text.toString() }
        val provider = if (providerIndex >= 0) LLMProvider.values()[providerIndex] else LLMProvider.OPENAI

        val isValidFormat = when (provider) {
            LLMProvider.OPENAI -> apiKey.startsWith("sk-")
            LLMProvider.CLAUDE -> apiKey.startsWith("sk-ant-")
            LLMProvider.GEMINI -> apiKey.length > 20
            LLMProvider.DEEPSEEK -> apiKey.startsWith("sk-")
        }

        binding.btnTest.postDelayed({
            binding.btnTest.isEnabled = true
            binding.btnTest.text = "测试连接"

            if (isValidFormat) {
                Toast.makeText(this, "API密钥格式正确！实际连接将在使用时验证。", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "API密钥格式可能不正确，请检查", Toast.LENGTH_LONG).show()
            }
        }, 1000)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
