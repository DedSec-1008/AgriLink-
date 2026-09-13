# ==============================================================================
# KisanSetu Production ProGuard / R8 Rules
# ==============================================================================

# Preserve line numbers and source file names for crash analytics
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# ------------------------------------------------------------------------------
# Kotlin Coroutines
# ------------------------------------------------------------------------------
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ------------------------------------------------------------------------------
# Android Lifecycle & ViewModels
# ------------------------------------------------------------------------------
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keep class androidx.lifecycle.ViewModelProvider$Factory { *; }

# ------------------------------------------------------------------------------
# Jetpack Compose
# ------------------------------------------------------------------------------
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }

# ------------------------------------------------------------------------------
# KisanSetu Domain Models & Data Layer
# ------------------------------------------------------------------------------
-keep class com.example.model.** { *; }
-keepclassmembers class com.example.model.** {
    <fields>;
    <methods>;
}
-keep class com.example.data.** { *; }
-keepclassmembers class com.example.data.** {
    <fields>;
    <methods>;
}

# ------------------------------------------------------------------------------
# Networking (Retrofit, OkHttp, Moshi)
# ------------------------------------------------------------------------------
-keepattributes EnclosingMethod
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# Moshi rules
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonQualifier interface *
-keepclassmembers class * {
    @com.squareup.moshi.FromJson *;
    @com.squareup.moshi.ToJson *;
}
-keep class com.example.data.remote.dto.** { *; }
-keepclassmembers class com.example.data.remote.dto.** {
    <fields>;
    <methods>;
}

# ------------------------------------------------------------------------------
# Android Architecture & System Components
# ------------------------------------------------------------------------------
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-dontwarn java.awt.**
-dontwarn javax.annotation.**

