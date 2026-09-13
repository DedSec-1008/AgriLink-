package com.example.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration policy and explicit definitions for KisanSetu Room Database.
 *
 * CRITICAL PRODUCTION POLICY:
 * Destructive migrations (fallbackToDestructiveMigration) are strictly forbidden
 * to prevent silent farmer data loss in rural production environments.
 */
object DatabaseMigrations {

    const val CURRENT_DATABASE_VERSION = 1
    const val DATABASE_NAME = "kisansetu_database"

    /**
     * All registered manual migrations.
     * When schema version increments to 2 in future phases, explicit ALTER TABLE scripts
     * are added here to preserve local user data.
     */
    val ALL_MIGRATIONS: Array<Migration> = arrayOf(
        // Future migrations will be declared here, e.g. MIGRATION_1_2
    )

    /**
     * Example forward migration definition prepared for schema expansion.
     * Demonstrates safe non-destructive migration capability for unit testing.
     */
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Adds a sync retry counter column to produce lots safely without dropping existing lots
            db.execSQL("ALTER TABLE produce_lots ADD COLUMN syncRetryCount INTEGER NOT NULL DEFAULT 0")
        }
    }
}
