package com.example.data

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale

enum class AppLanguage(val code: String, val displayName: String, val shortDisplay: String) {
    ENGLISH("en", "English", "EN"),
    HINDI("hi", "हिन्दी", "हिं"),
    MARATHI("mr", "मराठी", "म")
}

class LocalizedContextWrapper(
    private val base: Context,
    private val configContext: Context
) : ContextWrapper(base) {
    override fun getResources(): Resources = configContext.resources
    override fun getAssets(): AssetManager = configContext.assets
    override fun getApplicationContext(): Context = base.applicationContext
    override fun createConfigurationContext(overrideConfiguration: Configuration): Context {
        return LocalizedContextWrapper(base, super.createConfigurationContext(overrideConfiguration))
    }
}

fun Context.createLocalizedContext(langCode: String): Context {
    val locale = Locale(langCode)
    Locale.setDefault(locale)
    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    config.setLayoutDirection(locale)
    val configContext = createConfigurationContext(config)
    return LocalizedContextWrapper(this, configContext)
}
