package com.example.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.converters.AgriTypeConverters
import com.example.data.local.dao.BuyerDao
import com.example.data.local.dao.FarmerProfileDao
import com.example.data.local.dao.GrievanceDao
import com.example.data.local.dao.LogisticsBookingDao
import com.example.data.local.dao.MarketPriceDao
import com.example.data.local.dao.OfferDao
import com.example.data.local.dao.PaymentRecordDao
import com.example.data.local.dao.ProduceLotDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.entity.BuyerEntity
import com.example.data.local.entity.FarmerProfileEntity
import com.example.data.local.entity.GrievanceEntity
import com.example.data.local.entity.LogisticsBookingEntity
import com.example.data.local.entity.MarketPriceEntity
import com.example.data.local.entity.OfferEntity
import com.example.data.local.entity.PaymentRecordEntity
import com.example.data.local.entity.ProduceLotEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.migration.DatabaseMigrations
import com.example.data.local.seed.DemoSeedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Main Room Database for KisanSetu.
 * Acts as the single local source of truth for offline-first agricultural trade.
 */
@Database(
    entities = [
        FarmerProfileEntity::class,
        ProduceLotEntity::class,
        OfferEntity::class,
        TransactionEntity::class,
        LogisticsBookingEntity::class,
        PaymentRecordEntity::class,
        GrievanceEntity::class,
        BuyerEntity::class,
        MarketPriceEntity::class
    ],
    version = DatabaseMigrations.CURRENT_DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(AgriTypeConverters::class)
abstract class KisanSetuDatabase : RoomDatabase() {

    abstract fun farmerProfileDao(): FarmerProfileDao
    abstract fun produceLotDao(): ProduceLotDao
    abstract fun offerDao(): OfferDao
    abstract fun transactionDao(): TransactionDao
    abstract fun logisticsBookingDao(): LogisticsBookingDao
    abstract fun paymentRecordDao(): PaymentRecordDao
    abstract fun grievanceDao(): GrievanceDao
    abstract fun buyerDao(): BuyerDao
    abstract fun marketPriceDao(): MarketPriceDao

    companion object {
        @Volatile
        private var INSTANCE: KisanSetuDatabase? = null

        fun getInstance(context: Context): KisanSetuDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context.applicationContext).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(appContext: Context): KisanSetuDatabase {
            return Room.databaseBuilder(
                appContext,
                KisanSetuDatabase::class.java,
                DatabaseMigrations.DATABASE_NAME
            )
                .addMigrations(*DatabaseMigrations.ALL_MIGRATIONS)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Prepopulate default demo data on first creation
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getInstance(appContext)
                            prepopulateData(database)
                        }
                    }
                })
                .build()
        }

        /**
         * Creates an isolated in-memory database for local unit and Robolectric tests.
         */
        fun buildInMemory(context: Context): KisanSetuDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                KisanSetuDatabase::class.java
            )
                .allowMainThreadQueries()
                .build()
        }

        /**
         * Safely seeds initial data into the provided database instance.
         */
        suspend fun prepopulateData(database: KisanSetuDatabase) {
            database.farmerProfileDao().insertOrUpdateProfile(DemoSeedData.primaryFarmer)
            database.produceLotDao().insertLot(DemoSeedData.initialLot)
            database.offerDao().insertOffers(DemoSeedData.initialOffers)
            database.transactionDao().insertTransaction(DemoSeedData.initialTransaction)
            database.buyerDao().insertBuyers(DemoSeedData.buyers)
            database.marketPriceDao().insertPrices(DemoSeedData.marketPrices)
        }
    }
}
