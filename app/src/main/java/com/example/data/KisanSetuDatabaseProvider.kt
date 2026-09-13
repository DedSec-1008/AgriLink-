package com.example.data

import android.content.Context
import com.example.data.local.database.KisanSetuDatabase

/**
 * Clean dependency provider for KisanSetu persistence layer.
 * Avoids heavy DI frameworks (Hilt) while guaranteeing testability and thread safety.
 */
object KisanSetuDatabaseProvider {

    @Volatile
    private var repositoryInstance: AgriRepository? = null

    /**
     * Retrieves the active repository. If initialized with context, returns the RoomAgriRepository.
     * Otherwise provides a safe fallback for isolated no-arg unit tests.
     */
    fun getRepository(context: Context? = null): AgriRepository {
        return repositoryInstance ?: synchronized(this) {
            repositoryInstance ?: run {
                if (context != null) {
                    val db = KisanSetuDatabase.getInstance(context)
                    RoomAgriRepository(db).also { repositoryInstance = it }
                } else {
                    MockAgriRepository().also { repositoryInstance = it }
                }
            }
        }
    }

    /**
     * Explicitly initializes the Room-backed repository with Application Context.
     */
    fun initialize(context: Context): AgriRepository {
        return synchronized(this) {
            val db = KisanSetuDatabase.getInstance(context)
            RoomAgriRepository(db).also { repositoryInstance = it }
        }
    }

    /**
     * Injects a custom repository (such as RoomAgriRepository with in-memory database) for testing.
     */
    fun setRepositoryForTesting(repository: AgriRepository?) {
        synchronized(this) {
            repositoryInstance = repository
        }
    }

    /**
     * Resets the provider instance.
     */
    fun reset() {
        synchronized(this) {
            repositoryInstance = null
        }
    }
}
