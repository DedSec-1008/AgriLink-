package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.database.KisanSetuDatabase
import com.example.data.local.entity.ProduceLotEntity
import com.example.data.local.migration.DatabaseMigrations.MIGRATION_1_2
import com.example.model.LotStatus
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DatabaseMigrationTest {

    @Test
    fun `migration MIGRATION_1_2 alters produce_lots schema and preserves existing data`() {
        runBlocking {
            val context = ApplicationProvider.getApplicationContext<Context>()

        // 1. Build a database instance and insert existing data under schema version 1
        val testDbName = "migration_test_${System.currentTimeMillis()}.db"
        val db = Room.databaseBuilder(context, KisanSetuDatabase::class.java, testDbName)
            .allowMainThreadQueries()
            .build()

        val initialLot = ProduceLotEntity(
            lotId = "MIG-LOT-101",
            cropNameRes = R.string.crop_soybean,
            iconEmoji = "🌱",
            quantityQuintals = 85,
            qualityRes = R.string.produce_quality_good,
            statusRes = R.string.lot_status_published,
            dateCreated = "Yesterday",
            location = "Nagpur",
            expectedPricePerQ = 4850,
            estimatedNetPerQ = 4700,
            lotStatus = LotStatus.PUBLISHED
        )
        db.produceLotDao().insertLot(initialLot)
        assertEquals(1, db.produceLotDao().countLots())

        // 2. Simulate running migration MIGRATION_1_2 directly on the database
        val openHelper = db.openHelper.writableDatabase
        MIGRATION_1_2.migrate(openHelper)

        // 3. Verify column syncRetryCount was added to produce_lots table
        val cursor = openHelper.query("PRAGMA table_info(produce_lots)")
        var foundSyncRetryColumn = false
        while (cursor.moveToNext()) {
            val colName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            if (colName == "syncRetryCount") {
                foundSyncRetryColumn = true
                break
            }
        }
        cursor.close()
        assertTrue("syncRetryCount column must exist after MIGRATION_1_2", foundSyncRetryColumn)

        // 4. Verify existing record survived the migration intact (no destructive migration)
        val migratedLot = db.produceLotDao().getLotById("MIG-LOT-101")
        assertNotNull("Existing lot must not be lost during migration", migratedLot)
        assertEquals(85, migratedLot?.quantityQuintals)
        assertEquals("MIG-LOT-101", migratedLot?.lotId)

        db.close()
        context.deleteDatabase(testDbName)
        }
    }
}
