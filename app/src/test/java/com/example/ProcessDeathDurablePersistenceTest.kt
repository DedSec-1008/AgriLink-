package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.KisanSetuDatabaseProvider
import com.example.data.RoomAgriRepository
import com.example.data.local.database.KisanSetuDatabase
import com.example.model.LotStatus
import com.example.model.PaymentStatus
import com.example.model.TransactionStatus
import com.example.ui.AgriAppViewModel
import kotlinx.coroutines.flow.first
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
class ProcessDeathDurablePersistenceTest {

    @Test
    fun `farmer state survives process death and app cold restart via disk Room database`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbFile = "durable_farmer_test_${System.currentTimeMillis()}.db"

        // =========================================================================
        // PROCESS LIFECYCLE 1: Farmer opens app, creates lot, accepts offer, books transport
        // =========================================================================
        var diskDb = Room.databaseBuilder(context, KisanSetuDatabase::class.java, dbFile)
            .allowMainThreadQueries()
            .build()
        KisanSetuDatabase.prepopulateData(diskDb)

        var repo = RoomAgriRepository(diskDb)
        KisanSetuDatabaseProvider.setRepositoryForTesting(repo)
        var viewModel = AgriAppViewModel(repo)

        // 1. Farmer creates a new high-value produce lot
        val lot = repo.addLot(
            cropRes = R.string.crop_soybean,
            emoji = "🌱",
            quantity = 120,
            qualityRes = R.string.produce_quality_good,
            location = "Katol Village, Nagpur",
            readyTiming = "Immediate",
            expectedPricePerQ = 4900,
            estimatedNetPerQ = 4750,
            customLotId = "DURABLE-LOT-777",
            autoGenerateOffers = true
        )
        assertNotNull(lot)

        // 2. Farmer accepts an offer
        val offers = repo.getOffersForLot("DURABLE-LOT-777").first()
        assertTrue("Offers must have been generated and persisted in Room", offers.isNotEmpty())
        val offerToAccept = offers.first()
        val acceptResult = repo.acceptOffer(offerToAccept.id)
        assertTrue(acceptResult.isSuccess)
        val txn = acceptResult.getOrNull()!!
        assertEquals(TransactionStatus.OFFER_ACCEPTED, txn.status)

        // 3. Farmer books logistics transport
        val bookingResult = repo.bookTransport(txn.id, "transporter_mini_truck")
        assertTrue(bookingResult.isSuccess)

        // 4. Record a grievance
        val grievanceResult = repo.submitGrievance(txn.id, R.string.grievance_type_payment, "Awaiting buyer confirmation")
        assertTrue(grievanceResult.isSuccess)

        // =========================================================================
        // SIMULATE PROCESS DEATH:
        // The Android OS kills the process. All in-memory objects, ViewModels,
        // and database connections are completely destroyed.
        // =========================================================================
        diskDb.close()
        KisanSetuDatabaseProvider.reset()
        // Force garbage collection / nullification of all previous references
        @Suppress("UNUSED_VALUE")
        diskDb = null as KisanSetuDatabase? ?: diskDb

        // =========================================================================
        // PROCESS LIFECYCLE 2: App Cold Restart
        // The user reopens KisanSetu. The application builds a fresh Room instance from the disk file.
        // =========================================================================
        val restartedDiskDb = Room.databaseBuilder(context, KisanSetuDatabase::class.java, dbFile)
            .allowMainThreadQueries()
            .build()
        val restartedRepo = RoomAgriRepository(restartedDiskDb)
        KisanSetuDatabaseProvider.setRepositoryForTesting(restartedRepo)
        val restartedViewModel = AgriAppViewModel(restartedRepo)

        // Verify: Produce Lot is 100% intact with updated state
        val recoveredLot = restartedViewModel.repository.getLotById("DURABLE-LOT-777")
        assertNotNull("Produce lot must survive process death via disk Room DB", recoveredLot)
        assertEquals(120, recoveredLot?.quantityQuintals)
        assertEquals("Katol Village, Nagpur", recoveredLot?.location)
        assertEquals(LotStatus.OFFER_ACCEPTED, recoveredLot?.lotStatus)

        // Verify: Active Transaction is 100% intact with LOGISTICS_BOOKED status
        val recoveredTx = restartedViewModel.repository.getTransactionById(txn.id)
        assertNotNull("Transaction must survive process death", recoveredTx)
        assertEquals(TransactionStatus.LOGISTICS_BOOKED, recoveredTx?.status)
        assertEquals(PaymentStatus.PENDING, recoveredTx?.paymentStatus)
        assertEquals(120, recoveredTx?.quantityQuintals)

        // Verify: Transport booking is 100% intact
        val recoveredBooking = restartedViewModel.repository.getTransportBooking(txn.id)
        assertNotNull("Transport booking must survive process death", recoveredBooking)
        assertEquals("Mini Truck", recoveredBooking?.transporterName)

        // Verify: Grievance is 100% intact
        val recoveredGrievances = restartedViewModel.repository.getGrievancesForTransaction(txn.id)
        assertEquals(1, recoveredGrievances.size)
        assertEquals("Awaiting buyer confirmation", recoveredGrievances.first().details)

        // Cleanup test disk file
        restartedDiskDb.close()
        context.deleteDatabase(dbFile)
        KisanSetuDatabaseProvider.reset()
    }
}
