package com.example

import com.example.data.MockAgriRepository
import com.example.model.PaymentStatus
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
class Phase4LogisticsPaymentTest {

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
    fun testAcceptOfferTransitionsToOfferAccepted() = runTest {
        val repository = MockAgriRepository()
        val lot = repository.addLot(
            cropRes = R.string.crop_soybean,
            emoji = "🌱",
            quantity = 50,
            qualityRes = R.string.produce_quality_good,
            buyerNameRes = R.string.buyer_abc_foods
        )

        val offers = repository.getOffersForLot(lot.lotId).first()
        assertTrue(offers.isNotEmpty())
        val selectedOffer = offers.first()

        val acceptResult = repository.acceptOffer(selectedOffer.id)
        assertTrue(acceptResult.isSuccess)
        val transaction = acceptResult.getOrNull()
        assertNotNull(transaction)
        assertEquals(TransactionStatus.OFFER_ACCEPTED, transaction!!.status)
        assertEquals(selectedOffer.id, transaction.offerId)
        assertEquals(lot.lotId, transaction.lotId)
        assertEquals(50, transaction.quantityQuintals)
        assertEquals(selectedOffer.pricePerQuintal, transaction.agreedPricePerQ)

        // Verify transaction is in active transactions flow
        val activeTxList = repository.getActiveTransactions().first()
        val found = activeTxList.find { it.id == transaction.id }
        assertNotNull("Transaction must appear in active transactions flow", found)
    }

    @Test
    fun testBookTransportFlow() = runTest {
        val repository = MockAgriRepository()
        val lot = repository.addLot(
            cropRes = R.string.crop_soybean,
            emoji = "🌱",
            quantity = 50,
            qualityRes = R.string.produce_quality_good,
            buyerNameRes = R.string.buyer_abc_foods
        )
        val offers = repository.getOffersForLot(lot.lotId).first()
        val tx = repository.acceptOffer(offers.first().id).getOrThrow()

        // Get transporter options
        val transporterOptions = repository.getTransporterOptions(tx.cropNameRes, tx.quantityQuintals)
        assertTrue(transporterOptions.isNotEmpty())
        val transporter = transporterOptions.first()

        // Book transport
        val bookingResult = repository.bookTransport(tx.id, transporter.id)
        assertTrue(bookingResult.isSuccess)
        val booking = bookingResult.getOrThrow()
        assertEquals(tx.id, booking.transactionId)
        assertEquals(transporter.name, booking.transporterName)

        // Verify transaction status updated to LOGISTICS_BOOKED
        val updatedTx = repository.getTransactionById(tx.id)
        assertNotNull(updatedTx)
        assertEquals(TransactionStatus.LOGISTICS_BOOKED, updatedTx!!.status)
    }

    @Test
    fun testProducePickupAndDeliveryFlow() = runTest {
        val repository = MockAgriRepository()
        val lot = repository.addLot(
            cropRes = R.string.crop_soybean,
            emoji = "🌱",
            quantity = 50,
            qualityRes = R.string.produce_quality_good,
            buyerNameRes = R.string.buyer_abc_foods
        )
        val offers = repository.getOffersForLot(lot.lotId).first()
        val tx = repository.acceptOffer(offers.first().id).getOrThrow()
        repository.bookTransport(tx.id, "trans_shree")

        // Confirm Produce Pickup
        val pickupResult = repository.confirmProducePickup(tx.id)
        assertTrue(pickupResult.isSuccess)
        val dispatchedTx = pickupResult.getOrThrow()
        assertEquals(TransactionStatus.DISPATCHED, dispatchedTx.status)

        // Confirm Delivery
        val deliveryResult = repository.confirmProduceDelivery(tx.id)
        assertTrue(deliveryResult.isSuccess)
        val deliveredTx = deliveryResult.getOrThrow()
        assertEquals(TransactionStatus.DELIVERED, deliveredTx.status)

        // Verify payment details generated with transparent breakdown
        val paymentDetails = repository.getPaymentDetails(tx.id)
        assertNotNull("Payment details must be initialized after delivery", paymentDetails)
        assertEquals(tx.grossProduceValue, paymentDetails!!.grossProduceValue)
        assertTrue(paymentDetails.transportCost > 0)
        assertEquals(paymentDetails.grossProduceValue - paymentDetails.transportCost - paymentDetails.otherCosts, paymentDetails.estimatedNetAmount)
    }

    @Test
    fun testPaymentSettlementAndSaleCompletion() = runTest {
        val repository = MockAgriRepository()
        val lot = repository.addLot(
            cropRes = R.string.crop_soybean,
            emoji = "🌱",
            quantity = 50,
            qualityRes = R.string.produce_quality_good,
            buyerNameRes = R.string.buyer_abc_foods
        )
        val offers = repository.getOffersForLot(lot.lotId).first()
        val tx = repository.acceptOffer(offers.first().id).getOrThrow()
        repository.bookTransport(tx.id, "trans_shree")
        repository.confirmProducePickup(tx.id)
        repository.confirmProduceDelivery(tx.id)

        // Initiate payment
        val initResult = repository.initiatePayment(tx.id)
        assertTrue(initResult.isSuccess)
        assertEquals(PaymentStatus.INITIATED, initResult.getOrThrow().status)

        // Confirm payment received
        val paymentReceivedResult = repository.recordPaymentReceived(tx.id)
        assertTrue(paymentReceivedResult.isSuccess)
        val payment = paymentReceivedResult.getOrThrow()
        assertEquals(PaymentStatus.RECEIVED, payment.status)

        // Verify transaction is marked as PAYMENT_RECEIVED
        val txAfterPayment = repository.getTransactionById(tx.id)
        assertNotNull(txAfterPayment)
        assertEquals(TransactionStatus.PAYMENT_RECEIVED, txAfterPayment!!.status)

        // Complete sale
        val completionResult = repository.updateTransactionStatus(tx.id, TransactionStatus.COMPLETED)
        assertTrue(completionResult.isSuccess)
        assertEquals(TransactionStatus.COMPLETED, completionResult.getOrThrow().status)
    }

    @Test
    fun testBuyerRatingAndGrievanceFlow() = runTest {
        val repository = MockAgriRepository()
        val lot = repository.addLot(
            cropRes = R.string.crop_soybean,
            emoji = "🌱",
            quantity = 50,
            qualityRes = R.string.produce_quality_good,
            buyerNameRes = R.string.buyer_abc_foods
        )
        val offers = repository.getOffersForLot(lot.lotId).first()
        val tx = repository.acceptOffer(offers.first().id).getOrThrow()

        // Submit Buyer Rating
        val ratingResult = repository.rateBuyer(tx.id, rating = 5, feedback = "Fast payment and honest weighment")
        assertTrue("Farmer should be able to submit buyer rating", ratingResult.isSuccess)

        // Submit Grievance
        val grievanceResult = repository.submitGrievance(
            transactionId = tx.id,
            issueTypeRes = R.string.grievance_type_payment,
            details = "Delay in bank transfer reconciliation"
        )
        assertTrue("Farmer should be able to submit grievance issue", grievanceResult.isSuccess)
        val grievances = repository.getGrievancesForTransaction(tx.id)
        assertEquals(1, grievances.size)
        assertEquals("Delay in bank transfer reconciliation", grievances.first().details)
    }
}
