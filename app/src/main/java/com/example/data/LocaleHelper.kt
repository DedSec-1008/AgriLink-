package com.example.data

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

enum class AppLanguage(val code: String, val displayName: String, val shortDisplay: String) {
    ENGLISH("en", "English", "EN"),
    HINDI("hi", "हिन्दी", "हिं"),
    MARATHI("mr", "मराठी", "म")
}

fun Context.createLocalizedContext(langCode: String): Context {
    val locale = Locale(langCode)
    Locale.setDefault(locale)
    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    config.setLayoutDirection(locale)
    return createConfigurationContext(config)
}
