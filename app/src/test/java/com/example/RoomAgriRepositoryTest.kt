package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.RoomAgriRepository
import com.example.data.local.database.KisanSetuDatabase
import com.example.model.LotStatus
import com.example.model.OfferStatus
import com.example.model.PaymentStatus
import com.example.model.TransactionStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoomAgriRepositoryTest {

    private lateinit var database: KisanSetuDatabase
    private lateinit var repository: RoomAgriRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = KisanSetuDatabase.buildInMemory(context)
        runBlocking {
            KisanSetuDatabase.prepopulateData(database)
        }
        repository = RoomAgriRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `addLot persists lot into Room and emits via getMyLots Flow`() = runBlocking {
        val initialLots = repository.getMyLots().first()
        val initialCount = initialLots.size

        val newLot = repository.addLot(
            cropRes = R.string.crop_wheat,
            emoji = "🌾",
            quantity = 60,
            qualityRes = R.string.produce_quality_good,
            location = "Saoner, Nagpur",
            readyTiming = "Within 2 days",
            expectedPricePerQ = 2450,
            estimatedNetPerQ = 2380,
            customLotId = "LOT-WHEAT-2026-TEST"
        )
        assertNotNull(newLot)

        val updatedLots = repository.getMyLots().first()
        assertEquals(initialCount + 1, updatedLots.size)
        val fetched = repository.getLotById("LOT-WHEAT-2026-TEST")
        assertNotNull(fetched)
        assertEquals(60, fetched?.quantityQuintals)
        assertEquals("Saoner, Nagpur", fetched?.location)
    }

    @Test
    fun `publishLot creates published lot in Room with correct ID formatting`() = runBlocking {
        val published = repository.publishLot(
            cropRes = R.string.crop_soybean,
            emoji = "🌱",
            quantity = 100,
            qualityRes = R.string.produce_quality_good,
            buyerNameRes = null,
            location = "Katol",
            readyTiming = "Ready now",
            expectedPricePerQ = 4850,
            estimatedNetPerQ = 4700,
            photos = emptyList()
        )
        assertTrue(published.lotId.startsWith("LOT-AGL-2026-"))
        assertEquals(LotStatus.PUBLISHED, published.lotStatus)

        val retrieved = repository.getLotById(published.lotId)
        assertNotNull(retrieved)
        assertEquals(100, retrieved?.quantityQuintals)
    }

    @Test
    fun `acceptOffer marks offer accepted, cancels other offers, and creates transaction in Room`() = runBlocking {
        val lotId = "Lot #AG-1024"
        val offers = repository.getOffersForLot(lotId).first()
        assertTrue(offers.isNotEmpty())

        val offerToAccept = offers.first()
        val result = repository.acceptOffer(offerToAccept.id)
        assertTrue(result.isSuccess)

        val txn = result.getOrNull()
        assertNotNull(txn)
        assertEquals(TransactionStatus.OFFER_ACCEPTED, txn?.status)
        assertEquals(PaymentStatus.PENDING, txn?.paymentStatus)

        // Verify accepted offer status
        val updatedOffer = repository.getOfferById(offerToAccept.id)
        assertEquals(OfferStatus.ACCEPTED, updatedOffer?.status)

        // Verify other offers for this lot were cancelled
        val remainingOffers = repository.getOffersForLot(lotId).first()
        val otherOffers = remainingOffers.filter { it.id != offerToAccept.id }
        assertTrue(otherOffers.isNotEmpty())
        otherOffers.forEach {
            assertEquals(OfferStatus.CANCELLED, it.status)
        }

        // Verify corresponding lot status updated
        val lot = repository.getLotById(lotId)
        assertEquals(LotStatus.OFFER_ACCEPTED, lot?.lotStatus)

        // Verify transaction is in getActiveTransactions Flow
        val activeTxns = repository.getActiveTransactions().first()
        assertTrue(activeTxns.any { it.id == txn?.id })
    }

    @Test
    fun `transaction state transitions validate lifecycle strictly`() = runBlocking {
        val lotId = "Lot #AG-1024"
        val offers = repository.getOffersForLot(lotId).first()
        val txn = repository.acceptOffer(offers.first().id).getOrNull()!!

        // Valid: OFFER_ACCEPTED -> LOGISTICS_BOOKED
        val res1 = repository.updateTransactionStatus(txn.id, TransactionStatus.LOGISTICS_BOOKED)
        assertTrue(res1.isSuccess)
        assertEquals(TransactionStatus.LOGISTICS_BOOKED, res1.getOrNull()?.status)

        // Invalid: Cannot jump from LOGISTICS_BOOKED directly to DELIVERED (must dispatch first)
        val invalidRes = repository.updateTransactionStatus(txn.id, TransactionStatus.DELIVERED)
        assertFalse(invalidRes.isSuccess)

        // Valid: LOGISTICS_BOOKED -> DISPATCHED
        val res2 = repository.confirmProducePickup(txn.id)
        assertTrue(res2.isSuccess)
        assertEquals(TransactionStatus.DISPATCHED, res2.getOrNull()?.status)

        // Valid: DISPATCHED -> DELIVERED
        val res3 = repository.confirmProduceDelivery(txn.id)
        assertTrue(res3.isSuccess)
        assertEquals(TransactionStatus.DELIVERED, res3.getOrNull()?.status)

        // Valid: DELIVERED -> PAYMENT_INITIATED
        val res4 = repository.initiatePayment(txn.id)
        assertTrue(res4.isSuccess)
        assertEquals(PaymentStatus.INITIATED, res4.getOrNull()?.status)

        // Valid: PAYMENT_INITIATED -> PAYMENT_RECEIVED
        val res5 = repository.recordPaymentReceived(txn.id)
        assertTrue(res5.isSuccess)
        assertEquals(PaymentStatus.RECEIVED, res5.getOrNull()?.status)

        // Valid: Rate buyer advances to COMPLETED
        val rateRes = repository.rateBuyer(txn.id, rating = 5, feedback = "Fast settlement")
        assertTrue(rateRes.isSuccess)

        val completedTx = repository.getTransactionById(txn.id)
        assertEquals(TransactionStatus.COMPLETED, completedTx?.status)
        assertEquals(5, completedTx?.buyerRating)
        assertEquals("Fast settlement", completedTx?.buyerFeedback)
    }

    @Test
    fun `bookTransport persists booking and recalculates net realization in Room`() = runBlocking {
        val lotId = "Lot #AG-1024"
        val offers = repository.getOffersForLot(lotId).first()
        val txn = repository.acceptOffer(offers.first().id).getOrNull()!!

        val bookingResult = repository.bookTransport(txn.id, "transporter_truck")
        assertTrue(bookingResult.isSuccess)

        val booking = bookingResult.getOrNull()
        assertNotNull(booking)
        assertEquals("Truck", booking?.transporterName)

        val persistedBooking = repository.getTransportBooking(txn.id)
        assertNotNull(persistedBooking)
        assertEquals(booking?.totalCost, persistedBooking?.totalCost)

        val updatedTx = repository.getTransactionById(txn.id)
        assertEquals(TransactionStatus.LOGISTICS_BOOKED, updatedTx?.status)
        assertNotNull(updatedTx?.transporterBooking)
        assertEquals(booking?.totalCost, updatedTx?.transportDeduction)
    }

    @Test
    fun `submitGrievance and getGrievancesForTransaction persist issues in Room`() = runBlocking {
        val grievanceRes = repository.submitGrievance(
            transactionId = "AG-TXN-1024",
            issueTypeRes = R.string.grievance_type_payment,
            details = "Payment not credited to bank after 48h SLA"
        )
        assertTrue(grievanceRes.isSuccess)

        val grievances = repository.getGrievancesForTransaction("AG-TXN-1024")
        assertEquals(1, grievances.size)
        assertEquals("Payment not credited to bank after 48h SLA", grievances.first().details)
    }
}
