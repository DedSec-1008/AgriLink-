package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.converters.AgriTypeConverters
import com.example.data.local.database.KisanSetuDatabase
import com.example.data.local.entity.BuyerEntity
import com.example.data.local.entity.FarmerProfileEntity
import com.example.data.local.entity.GrievanceEntity
import com.example.data.local.entity.LogisticsBookingEntity
import com.example.data.local.entity.MarketDataFreshness
import com.example.data.local.entity.MarketPriceEntity
import com.example.data.local.entity.OfferEntity
import com.example.data.local.entity.PaymentRecordEntity
import com.example.data.local.entity.ProduceLotEntity
import com.example.data.local.entity.SyncStatus
import com.example.data.local.entity.TransactionEntity
import com.example.model.DestinationType
import com.example.model.LotStatus
import com.example.model.OfferStatus
import com.example.model.PaymentStatus
import com.example.model.TransactionStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoomDatabaseBasicTest {

    private lateinit var database: KisanSetuDatabase
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = KisanSetuDatabase.buildInMemory(context)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `test TypeConverters serialization and deserialization`() {
        val converters = AgriTypeConverters()

        // List<Int>
        val intList = listOf(101, 202, 303)
        val intString = converters.fromIntList(intList)
        val recoveredInts = converters.toIntList(intString)
        assertEquals(intList, recoveredInts)
        assertTrue(converters.toIntList("").isEmpty())

        // List<String>
        val stringList = listOf("img_url_1", "img_url_2")
        val stringData = converters.fromStringList(stringList)
        val recoveredStrings = converters.toStringList(stringData)
        assertEquals(stringList, recoveredStrings)

        // Enums
        assertEquals(LotStatus.PUBLISHED, converters.toLotStatus(converters.fromLotStatus(LotStatus.PUBLISHED)))
        assertEquals(OfferStatus.ACCEPTED, converters.toOfferStatus(converters.fromOfferStatus(OfferStatus.ACCEPTED)))
        assertEquals(TransactionStatus.DISPATCHED, converters.toTransactionStatus(converters.fromTransactionStatus(TransactionStatus.DISPATCHED)))
        assertEquals(PaymentStatus.RECEIVED, converters.toPaymentStatus(converters.fromPaymentStatus(PaymentStatus.RECEIVED)))
        assertEquals(SyncStatus.PENDING_UPLOAD, converters.toSyncStatus(converters.fromSyncStatus(SyncStatus.PENDING_UPLOAD)))
        assertEquals(MarketDataFreshness.LIVE, converters.toMarketFreshness(converters.fromMarketFreshness(MarketDataFreshness.LIVE)))
        assertEquals(DestinationType.BUYER, converters.toDestinationType(converters.fromDestinationType(DestinationType.BUYER)))
    }

    @Test
    fun `test FarmerProfileDao insert read and update`() = runBlocking {
        val profile = FarmerProfileEntity(
            id = "primary_farmer",
            fullName = "Tukaram Shinde",
            village = "Katol",
            district = "Nagpur",
            fpoName = "Katol Agri Producer Co."
        )
        database.farmerProfileDao().insertOrUpdateProfile(profile)

        val retrieved = database.farmerProfileDao().getProfile("primary_farmer")
        assertNotNull(retrieved)
        assertEquals("Tukaram Shinde", retrieved?.fullName)
        assertEquals("Katol", retrieved?.village)

        // Update profile
        val updated = retrieved!!.copy(village = "Hingna")
        database.farmerProfileDao().updateProfile(updated)

        val retrievedUpdated = database.farmerProfileDao().getProfile("primary_farmer")
        assertEquals("Hingna", retrievedUpdated?.village)
    }

    @Test
    fun `test ProduceLotDao CRUD and Flow reactivity`() = runBlocking {
        val lot = ProduceLotEntity(
            lotId = "LOT-TEST-001",
            cropNameRes = R.string.crop_soybean,
            iconEmoji = "🌱",
            quantityQuintals = 75,
            qualityRes = R.string.produce_quality_good,
            statusRes = R.string.lot_status_waiting,
            dateCreated = "Today",
            location = "Nagpur",
            expectedPricePerQ = 4800,
            estimatedNetPerQ = 4650
        )
        database.produceLotDao().insertLot(lot)

        val count = database.produceLotDao().countLots()
        assertEquals(1, count)

        val byId = database.produceLotDao().getLotById("LOT-TEST-001")
        assertNotNull(byId)
        assertEquals(75, byId?.quantityQuintals)

        // Test Flow
        val lotsList = database.produceLotDao().getAllLotsFlow().first()
        assertEquals(1, lotsList.size)
        assertEquals("LOT-TEST-001", lotsList[0].lotId)

        // Test status update
        database.produceLotDao().updateLotStatus(
            lotId = "LOT-TEST-001",
            statusRes = R.string.status_offer_accepted,
            lotStatus = LotStatus.OFFER_ACCEPTED,
            buyerNameRes = R.string.buyer_abc_foods
        )
        val updated = database.produceLotDao().getLotById("LOT-TEST-001")
        assertEquals(LotStatus.OFFER_ACCEPTED, updated?.lotStatus)
        assertEquals(R.string.buyer_abc_foods, updated?.buyerNameRes)

        // Delete
        database.produceLotDao().deleteLotById("LOT-TEST-001")
        assertEquals(0, database.produceLotDao().countLots())
    }

    @Test
    fun `test OfferDao operations and cancelOtherOffersForLot`() = runBlocking {
        val offer1 = OfferEntity(
            id = "offer_lot1_buyer1",
            lotId = "LOT-1",
            buyerId = "buyer_abc",
            buyerNameRes = R.string.buyer_abc_foods,
            isVerifiedBuyer = true,
            farmerRating = 4.8,
            pricePerQuintal = 4850,
            quantityQuintals = 50,
            quotedTotalAmount = 242500,
            transportExpensePerQ = 100,
            otherExpensePerQ = 20,
            estimatedNetAmount = 236500,
            paymentTermsRes = R.string.payment_within_2_days,
            deliveryRequirementsRes = R.string.buyer_delivery_center_or_farmgate,
            qualityRequirementsRes = R.string.buyer_quality_requirement,
            reliabilityTextRes = R.string.reliability_very_reliable
        )
        val offer2 = offer1.copy(id = "offer_lot1_buyer2", buyerId = "buyer_xyz", estimatedNetAmount = 230000)

        database.offerDao().insertOffers(listOf(offer1, offer2))
        assertEquals(2, database.offerDao().countOffers())

        // Accept offer1 and cancel others for LOT-1
        database.offerDao().updateOfferStatus("offer_lot1_buyer1", OfferStatus.ACCEPTED)
        database.offerDao().cancelOtherOffersForLot("LOT-1", "offer_lot1_buyer1")

        val accepted = database.offerDao().getOfferById("offer_lot1_buyer1")
        val cancelled = database.offerDao().getOfferById("offer_lot1_buyer2")

        assertEquals(OfferStatus.ACCEPTED, accepted?.status)
        assertEquals(OfferStatus.CANCELLED, cancelled?.status)
    }

    @Test
    fun `test Transaction and Logistics booking persistence`() = runBlocking {
        val tx = TransactionEntity(
            id = "TX-TEST-001",
            lotId = "LOT-1",
            offerId = "OFFER-1",
            buyerId = "buyer_abc",
            buyerNameRes = R.string.buyer_abc_foods,
            cropNameRes = R.string.crop_soybean,
            iconEmoji = "🌱",
            quantityQuintals = 50,
            agreedPricePerQ = 4850,
            grossProduceValue = 242500,
            transportDeduction = 6000,
            otherDeductions = 1500,
            estimatedNetAmount = 235000,
            status = TransactionStatus.OFFER_ACCEPTED,
            paymentStatus = PaymentStatus.PENDING
        )
        database.transactionDao().insertTransaction(tx)

        val booking = LogisticsBookingEntity(
            bookingId = "BK-001",
            transactionId = "TX-TEST-001",
            transporterName = "Kisan Express Logistics",
            vehicleTypeRes = R.string.vehicle_truck,
            pickupLocation = "Katol",
            deliveryLocation = "Nagpur Mandi",
            pickupTime = "Today, 3 PM",
            totalCost = 5500
        )
        database.logisticsBookingDao().insertBooking(booking)

        val fetchedBooking = database.logisticsBookingDao().getBookingForTransaction("TX-TEST-001")
        assertNotNull(fetchedBooking)
        assertEquals("Kisan Express Logistics", fetchedBooking?.transporterName)
        assertEquals(5500, fetchedBooking?.totalCost)
    }

    @Test
    fun `test PaymentRecord and Grievance persistence`() = runBlocking {
        val payment = PaymentRecordEntity(
            id = "PAY-001",
            transactionId = "TX-001",
            buyerNameRes = R.string.buyer_abc_foods,
            cropNameRes = R.string.crop_soybean,
            quantityQuintals = 50,
            agreedPricePerQ = 4850,
            grossProduceValue = 242500,
            transportCost = 6000,
            otherCosts = 1500,
            estimatedNetAmount = 235000,
            status = PaymentStatus.INITIATED
        )
        database.paymentRecordDao().insertPayment(payment)

        val fetchedPayment = database.paymentRecordDao().getPaymentForTransaction("TX-001")
        assertNotNull(fetchedPayment)
        assertEquals(PaymentStatus.INITIATED, fetchedPayment?.status)

        // Grievance
        val grievance = GrievanceEntity(
            id = "GRV-001",
            transactionId = "TX-001",
            issueTypeRes = R.string.grievance_type_payment,
            details = "Payment delayed past 48 hours SLA"
        )
        database.grievanceDao().insertGrievance(grievance)

        val grievances = database.grievanceDao().getGrievancesForTransaction("TX-001")
        assertEquals(1, grievances.size)
        assertEquals("Payment delayed past 48 hours SLA", grievances[0].details)
    }

    @Test
    fun `test Buyers and MarketPrices query and prepopulation`() = runBlocking {
        KisanSetuDatabase.prepopulateData(database)

        assertTrue(database.buyerDao().countBuyers() > 0)
        assertTrue(database.marketPriceDao().countPrices() > 0)
        assertTrue(database.produceLotDao().countLots() > 0)
        assertTrue(database.offerDao().countOffers() > 0)
        assertTrue(database.transactionDao().countTransactions() > 0)

        val prices = database.marketPriceDao().getAllPrices()
        assertTrue(prices.isNotEmpty())
        assertEquals(MarketDataFreshness.DEMO, prices.first().dataFreshness)
    }
}
