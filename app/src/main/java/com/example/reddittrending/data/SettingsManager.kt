package com.example.reddittrending.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.reddittrending.ai.LLMConfig
import com.example.reddittrending.ai.LLMProvider

/**
 * 设置管理器
 * 安全存储API密钥等敏感信息
 */
class SettingsManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val securePrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_settings",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val normalPrefs: SharedPreferences =
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LLM_PROVIDER = "llm_provider"
        private const val KEY_OPENAI_API_KEY = "openai_api_key"
        private const val KEY_CLAUDE_API_KEY = "claude_api_key"
        private const val KEY_GEMINI_API_KEY = "gemini_api_key"
        private const val KEY_DEEPSEEK_API_KEY = "deepseek_api_key"
        private const val KEY_CUSTOM_MODEL = "custom_model"
        private const val KEY_AI_ENABLED = "ai_enabled"
    }

    /**
     * 保存LLM提供商选择
     */
    fun saveLLMProvider(provider: LLMProvider) {
        normalPrefs.edit().putString(KEY_LLM_PROVIDER, provider.name).apply()
    }

    /**
     * 获取LLM提供商
     */
    fun getLLMProvider(): LLMProvider {
        val name = normalPrefs.getString(KEY_LLM_PROVIDER, LLMProvider.OPENAI.name)
        return LLMProvider.fromName(name ?: LLMProvider.OPENAI.name) ?: LLMProvider.OPENAI
    }

    /**
     * 保存API密钥
     */
    fun saveApiKey(provider: LLMProvider, apiKey: String) {
        val key = when (provider) {
            LLMProvider.OPENAI -> KEY_OPENAI_API_KEY
            LLMProvider.CLAUDE -> KEY_CLAUDE_API_KEY
            LLMProvider.GEMINI -> KEY_GEMINI_API_KEY
            LLMProvider.DEEPSEEK -> KEY_DEEPSEEK_API_KEY
        }
        securePrefs.edit().putString(key, apiKey).apply()
    }

    /**
     * 获取API密钥
     */
    fun getApiKey(provider: LLMProvider): String {
        val key = when (provider) {
            LLMProvider.OPENAI -> KEY_OPENAI_API_KEY
            LLMProvider.CLAUDE -> KEY_CLAUDE_API_KEY
            LLMProvider.GEMINI -> KEY_GEMINI_API_KEY
            LLMProvider.DEEPSEEK -> KEY_DEEPSEEK_API_KEY
        }
        return securePrefs.getString(key, "") ?: ""
    }

    /**
     * 保存自定义模型名称
     */
    fun saveCustomModel(model: String?) {
        normalPrefs.edit().putString(KEY_CUSTOM_MODEL, model).apply()
    }

    /**
     * 获取自定义模型名称
     */
    fun getCustomModel(): String? {
        return normalPrefs.getString(KEY_CUSTOM_MODEL, null)
    }

    /**
     * 设置是否启用AI分析
     */
    fun setAIEnabled(enabled: Boolean) {
        normalPrefs.edit().putBoolean(KEY_AI_ENABLED, enabled).apply()
    }

    /**
     * 获取是否启用AI分析
     */
    fun isAIEnabled(): Boolean {
        return normalPrefs.getBoolean(KEY_AI_ENABLED, false)
    }

    /**
     * 获取当前LLM配置
     */
    fun getLLMConfig(): LLMConfig? {
        val provider = getLLMProvider()
        val apiKey = getApiKey(provider)

        if (apiKey.isBlank()) return null

        return LLMConfig(
            provider = provider,
            apiKey = apiKey,
            customModel = getCustomModel()
        )
    }

    /**
     * 检查是否已配置API
     */
    fun isConfigured(): Boolean {
        val provider = getLLMProvider()
        return getApiKey(provider).isNotBlank()
    }
}
