package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.AgriRepository
import com.example.data.KisanSetuDatabaseProvider

/**
 * Retained Application-level ViewModel that survives Activity recreation
 * and device configuration changes (e.g. screen rotation).
 * Owns the [AgriRepository] to guarantee state persistence across Android lifecycles.
 */
class AgriAppViewModel(
    val repository: AgriRepository = KisanSetuDatabaseProvider.getRepository()
) : ViewModel() {

    companion object {
        fun Factory(context: Context): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = KisanSetuDatabaseProvider.getRepository(context)
                return AgriAppViewModel(repo) as T
            }
        }
    }
}
