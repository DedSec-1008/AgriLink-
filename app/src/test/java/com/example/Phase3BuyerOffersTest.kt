package com.example

import com.example.data.MockAgriRepository
import com.example.model.OfferStatus
import com.example.model.TransactionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class Phase3BuyerOffersTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testBuyersDirectoryData() = runTest {
        val repository = MockAgriRepository()
        val buyers = repository.getBuyers().first()

        assertTrue(buyers.isNotEmpty())
        val abcFoods = repository.getBuyerById("buyer_abc")
        assertNotNull(abcFoods)
        assertEquals(R.string.buyer_abc_foods, abcFoods!!.nameRes)
        assertTrue(abcFoods.isVerified)
        assertEquals(4850, abcFoods.quotedPricePerQ)
        assertEquals(4.7, abcFoods.farmerRating, 0.01)
    }

    @Test
    fun testLotCreationGeneratesOffers() = runTest {
        val repository = MockAgriRepository()
        val lot = repository.addLot(
            cropRes = R.string.crop_soybean,
            emoji = "🌱",
            quantity = 50,
            qualityRes = R.string.produce_quality_good,
            buyerNameRes = R.string.buyer_abc_foods
        )

        val offers = repository.getOffersForLot(lot.lotId).first()
        assertTrue("Lot should receive offers upon creation", offers.isNotEmpty())
        assertEquals(3, offers.size)

        val abcOffer = offers.firstOrNull { it.buyerId == "buyer_abc" }
        assertNotNull(abcOffer)
        assertEquals(4850, abcOffer!!.pricePerQuintal)
        assertEquals(50, abcOffer.quantityQuintals)
        assertEquals(OfferStatus.PENDING, abcOffer.status)

        // Verify transparent calculation: 50 * 4850 = 242500, transport = 50*120=6000, handling = 50*30=1500 -> 235000
        assertEquals(242500, abcOffer.quotedTotalAmount)
        assertEquals(235000, abcOffer.estimatedNetAmount)
    }

    @Test
    fun testAcceptOfferWorkflow() = runTest {
        val repository = MockAgriRepository()
        val lot = repository.addLot(
            cropRes = R.string.crop_soybean,
            emoji = "🌱",
            quantity = 50,
            qualityRes = R.string.produce_quality_good,
            buyerNameRes = R.string.buyer_abc_foods
        )

        val offers = repository.getOffersForLot(lot.lotId).first()
        val targetOffer = offers.first { it.buyerId == "buyer_abc" }

        // Accept the offer
        val result = repository.acceptOffer(targetOffer.id)
        assertTrue(result.isSuccess)
        val transaction = result.getOrThrow()
        assertEquals(targetOffer.id, transaction.offerId)
        assertEquals(lot.lotId, transaction.lotId)
        assertEquals("buyer_abc", transaction.buyerId)
        assertEquals(4850, transaction.agreedPricePerQ)
        assertEquals(235000, transaction.estimatedNetAmount)
        assertEquals(TransactionStatus.OFFER_ACCEPTED, transaction.status)

        // Verify offer status is ACCEPTED
        val updatedOffers = repository.getOffersForLot(lot.lotId).first()
        val acceptedOffer = updatedOffers.first { it.id == targetOffer.id }
        assertEquals(OfferStatus.ACCEPTED, acceptedOffer.status)

        // Verify other offers for this lot are CANCELLED
        val otherOffers = updatedOffers.filter { it.id != targetOffer.id }
        assertTrue(otherOffers.all { it.status == OfferStatus.CANCELLED })

        // Verify lot status is updated to OFFER ACCEPTED
        val updatedLot = repository.getLotById(lot.lotId)
        assertNotNull(updatedLot)
        assertEquals(R.string.status_offer_accepted, updatedLot!!.statusRes)

        // Verify active transactions flow contains the transaction
        val activeTxList = repository.getActiveTransactions().first()
        assertTrue(activeTxList.any { it.id == transaction.id })
    }

    @Test
    fun testRejectOffer() = runTest {
        val repository = MockAgriRepository()
        val lot = repository.addLot(
            cropRes = R.string.crop_soybean,
            emoji = "🌱",
            quantity = 50,
            qualityRes = R.string.produce_quality_good,
            buyerNameRes = R.string.buyer_abc_foods
        )

        val offers = repository.getOffersForLot(lot.lotId).first()
        val offerToReject = offers.first { it.buyerId == "buyer_xyz" }

        repository.rejectOffer(offerToReject.id)

        val updatedOffer = repository.getOfferById(offerToReject.id)
        assertNotNull(updatedOffer)
        assertEquals(OfferStatus.REJECTED, updatedOffer!!.status)
    }
}
