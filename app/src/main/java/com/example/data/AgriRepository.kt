package com.example.data

import com.example.R
import com.example.model.AgriTransaction
import com.example.model.Buyer
import com.example.model.CropOption
import com.example.model.HelpCategory
import com.example.model.MarketPriceInfo
import com.example.model.Offer
import com.example.model.OfferStatus
import com.example.model.ProduceItem
import com.example.model.ProduceLot
import com.example.model.SellingOpportunity
import com.example.model.TransactionStatus
import com.example.model.GrievanceIssue
import com.example.model.PaymentDetails
import com.example.model.PaymentStatus
import com.example.model.TransportBooking
import com.example.model.TransporterOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

interface TransactionRepository {
    fun getActiveTransactions(): Flow<List<AgriTransaction>>
    fun getTransactionById(id: String): AgriTransaction?
    suspend fun updateTransactionStatus(transactionId: String, newStatus: TransactionStatus): Result<AgriTransaction>
    suspend fun rateBuyer(transactionId: String, rating: Int, feedback: String?): Result<Unit>
    suspend fun submitGrievance(transactionId: String, issueTypeRes: Int, details: String): Result<GrievanceIssue>
    fun getGrievancesForTransaction(transactionId: String): List<GrievanceIssue>
}

interface LogisticsRepository {
    fun getTransporterOptions(cropRes: Int, quantity: Int): List<TransporterOption>
    suspend fun bookTransport(transactionId: String, transporterId: String): Result<TransportBooking>
    fun getTransportBooking(transactionId: String): TransportBooking?
    suspend fun confirmProducePickup(transactionId: String): Result<AgriTransaction>
    suspend fun confirmProduceDelivery(transactionId: String): Result<AgriTransaction>
}

interface PaymentRepository {
    fun getPaymentDetails(transactionId: String): PaymentDetails?
    suspend fun initiatePayment(transactionId: String): Result<PaymentDetails>
    suspend fun recordPaymentReceived(transactionId: String): Result<PaymentDetails>
    suspend fun flagPaymentDelayed(transactionId: String): Result<PaymentDetails>
}

interface AgriRepository : TransactionRepository, LogisticsRepository, PaymentRepository {
    val recommendationService: RecommendationService
    fun getCurrentProduce(): ProduceItem
    fun getTodaysPrimaryPrice(): MarketPriceInfo
    fun getBestSellingOpportunity(): SellingOpportunity
    fun getMarketPrices(): List<MarketPriceInfo>
    fun getAvailableCrops(): List<CropOption>
    fun getMyLots(): Flow<List<ProduceLot>>
    fun getHelpCategories(): List<HelpCategory>
    suspend fun addLot(cropRes: Int, emoji: String, quantity: Int, qualityRes: Int, buyerNameRes: Int? = null): ProduceLot
    
    // Phase 3 & 4: Buyers, Offers & Transactions
    fun getBuyers(): Flow<List<Buyer>>
    fun getBuyerById(id: String): Buyer?
    fun getOffersForLot(lotId: String): Flow<List<Offer>>
    fun getOfferById(offerId: String): Offer?
    suspend fun acceptOffer(offerId: String): Result<AgriTransaction>
    suspend fun rejectOffer(offerId: String): Result<Unit>
    fun getLotById(lotId: String): ProduceLot?
}

class MockAgriRepository(
    override val recommendationService: RecommendationService = MockRecommendationService()
) : AgriRepository {

    private val currentProduce = ProduceItem(
        cropNameRes = R.string.crop_soybean,
        iconEmoji = "🌱",
        quantityQuintals = 50,
        qualityRes = R.string.produce_quality_good
    )

    private val primaryPrice = MarketPriceInfo(
        id = "ngp_soy",
        cropNameRes = R.string.crop_soybean,
        marketNameRes = R.string.market_nagpur,
        pricePerQuintal = 4850,
        priceChangeTextRes = R.string.price_change_yesterday,
        isPositiveChange = true
    )

    private val bestOpportunity = SellingOpportunity(
        buyerNameRes = R.string.buyer_abc_foods,
        cropNameRes = R.string.crop_soybean,
        quantityQuintals = 50,
        quotedPricePerQ = 4850,
        transportExpensePerQ = 120,
        otherExpensePerQ = 30,
        netRealizationPerQ = 4700,
        estimatedTotalAmount = 235000,
        statusTextRes = R.string.status_strong_rec,
        reasonsRes = listOf(
            R.string.reason_verified_buyer,
            R.string.reason_good_price,
            R.string.reason_affordable_transport,
            R.string.reason_suitable_qty
        )
    )

    private val nearbyMarkets = listOf(
        MarketPriceInfo("1", R.string.crop_soybean, R.string.market_nagpur, 4850, R.string.price_change_yesterday, true),
        MarketPriceInfo("2", R.string.crop_soybean, R.string.market_katol, 4780, R.string.status_steady, true),
        MarketPriceInfo("3", R.string.crop_soybean, R.string.market_hingna, 4720, R.string.status_steady, false),
        MarketPriceInfo("4", R.string.crop_soybean, R.string.market_amravati, 4820, R.string.status_steady, true)
    )

    private val availableCrops = listOf(
        CropOption("soybean", R.string.crop_soybean, "🌱", 4850),
        CropOption("wheat", R.string.crop_wheat, "🌾", 2400),
        CropOption("rice", R.string.crop_rice, "🌾", 2800),
        CropOption("maize", R.string.crop_maize, "🌽", 2150),
        CropOption("other", R.string.crop_other, "🥜", 3500)
    )

    private val buyersList = listOf(
        Buyer(
            id = "buyer_abc",
            nameRes = R.string.buyer_abc_foods,
            locationRes = R.string.loc_nagpur,
            isVerified = true,
            commodityRes = R.string.crop_soybean,
            currentDemandMinQ = 50,
            currentDemandMaxQ = 100,
            requiredQualityRes = R.string.produce_quality_good,
            quotedPricePerQ = 4850,
            paymentDaysDescriptionRes = R.string.payment_within_2_days,
            onTimePaymentPct = 96,
            completedTransactions = 128,
            farmerRating = 4.7,
            neededTimelineRes = R.string.timeline_this_week,
            disputeRatePct = 0.8,
            averagePaymentDays = 2
        ),
        Buyer(
            id = "buyer_xyz",
            nameRes = R.string.buyer_xyz_traders,
            locationRes = R.string.loc_nagpur,
            isVerified = true,
            commodityRes = R.string.crop_soybean,
            currentDemandMinQ = 30,
            currentDemandMaxQ = 80,
            requiredQualityRes = R.string.produce_quality_good,
            quotedPricePerQ = 4800,
            paymentDaysDescriptionRes = R.string.payment_within_3_days,
            onTimePaymentPct = 92,
            completedTransactions = 84,
            farmerRating = 4.5,
            neededTimelineRes = R.string.timeline_this_week,
            disputeRatePct = 1.2,
            averagePaymentDays = 3
        ),
        Buyer(
            id = "buyer_nagpur_agro",
            nameRes = R.string.buyer_nagpur_agro,
            locationRes = R.string.loc_nagpur,
            isVerified = true,
            commodityRes = R.string.crop_soybean,
            currentDemandMinQ = 20,
            currentDemandMaxQ = 60,
            requiredQualityRes = R.string.produce_quality_good,
            quotedPricePerQ = 4760,
            paymentDaysDescriptionRes = R.string.payment_within_4_days,
            onTimePaymentPct = 89,
            completedTransactions = 52,
            farmerRating = 4.3,
            neededTimelineRes = R.string.timeline_this_week,
            disputeRatePct = 1.5,
            averagePaymentDays = 4
        )
    )

    private val buyersFlow = MutableStateFlow(buyersList)
    private val lotsFlow = MutableStateFlow<List<ProduceLot>>(emptyList())
    private val offersFlow = MutableStateFlow<List<Offer>>(emptyList())
    private val transactionsFlow = MutableStateFlow<List<AgriTransaction>>(emptyList())
    private val bookingsMap = mutableMapOf<String, TransportBooking>()
    private val grievancesList = mutableListOf<GrievanceIssue>()
    private var nextLotSeq = 1025

    private val defaultTransaction = AgriTransaction(
        id = "AG-TXN-1024",
        lotId = "Lot #AG-1024",
        offerId = "offer_Lot #AG-1024_abc",
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
        paymentStatus = PaymentStatus.PENDING,
        createdAt = "Today"
    )

    init {
        val initialLot = ProduceLot(
            lotId = "Lot #AG-1024",
            cropNameRes = R.string.crop_soybean,
            iconEmoji = "🌱",
            quantityQuintals = 50,
            qualityRes = R.string.produce_quality_good,
            statusRes = R.string.lot_status_waiting,
            dateCreated = "Today",
            location = "Nagpur, Maharashtra",
            readyTiming = "Ready now",
            expectedPricePerQ = 4850,
            estimatedNetPerQ = 4700
        )
        lotsFlow.value = listOf(initialLot)
        offersFlow.value = createOffersForLot("Lot #AG-1024", 50)
        transactionsFlow.value = listOf(defaultTransaction)
    }

    private val helpCategories = listOf(
        HelpCategory("selling", R.string.cat_selling, "🌾"),
        HelpCategory("price", R.string.cat_price, "💰"),
        HelpCategory("buyer", R.string.cat_buyer, "🛒"),
        HelpCategory("lot", R.string.cat_lot, "📦"),
        HelpCategory("transport", R.string.cat_transport, "🚚"),
        HelpCategory("payment", R.string.cat_payment, "💳"),
        HelpCategory("verification", R.string.cat_verification, "✓"),
        HelpCategory("using_app", R.string.cat_using_app, "📱")
    )

    override fun getCurrentProduce(): ProduceItem = currentProduce
    override fun getTodaysPrimaryPrice(): MarketPriceInfo = primaryPrice
    override fun getBestSellingOpportunity(): SellingOpportunity = bestOpportunity
    override fun getMarketPrices(): List<MarketPriceInfo> = nearbyMarkets
    override fun getAvailableCrops(): List<CropOption> = availableCrops
    override fun getMyLots(): Flow<List<ProduceLot>> = lotsFlow.asStateFlow()
    override fun getHelpCategories(): List<HelpCategory> = helpCategories

    override suspend fun addLot(
        cropRes: Int,
        emoji: String,
        quantity: Int,
        qualityRes: Int,
        buyerNameRes: Int?
    ): ProduceLot {
        val lotId = "Lot #AG-$nextLotSeq"
        nextLotSeq++
        val newLot = ProduceLot(
            lotId = lotId,
            cropNameRes = cropRes,
            iconEmoji = emoji,
            quantityQuintals = quantity,
            qualityRes = qualityRes,
            statusRes = R.string.lot_status_waiting,
            dateCreated = "Just now",
            buyerNameRes = buyerNameRes,
            location = "Nagpur, Maharashtra",
            readyTiming = "Ready now",
            expectedPricePerQ = 4850,
            estimatedNetPerQ = 4700
        )
        lotsFlow.value = listOf(newLot) + lotsFlow.value

        // Automatically generate realistic mock offers for this lot
        val initialOffers = createOffersForLot(lotId, quantity)
        offersFlow.value = offersFlow.value + initialOffers

        return newLot
    }

    private fun createOffersForLot(lotId: String, quantity: Int): List<Offer> {
        val abcGross = 4850 * quantity
        val abcTransportTotal = 120 * quantity // ₹6,000 for 50q
        val abcOtherTotal = 30 * quantity       // ₹1,500 for 50q
        val abcNet = abcGross - abcTransportTotal - abcOtherTotal // ₹2,35,000 for 50q

        val xyzGross = 4800 * quantity
        val xyzTransportTotal = 140 * quantity
        val xyzOtherTotal = 30 * quantity
        val xyzNet = xyzGross - xyzTransportTotal - xyzOtherTotal

        val ngpGross = 4760 * quantity
        val ngpTransportTotal = 150 * quantity
        val ngpOtherTotal = 30 * quantity
        val ngpNet = ngpGross - ngpTransportTotal - ngpOtherTotal

        return listOf(
            Offer(
                id = "offer_${lotId}_abc",
                lotId = lotId,
                buyerId = "buyer_abc",
                buyerNameRes = R.string.buyer_abc_foods,
                isVerifiedBuyer = true,
                farmerRating = 4.7,
                pricePerQuintal = 4850,
                quantityQuintals = quantity,
                quotedTotalAmount = abcGross,
                transportExpensePerQ = 120,
                otherExpensePerQ = 30,
                estimatedNetAmount = abcNet,
                paymentTermsRes = R.string.payment_within_2_days,
                deliveryRequirementsRes = R.string.buyer_delivery_center_or_farmgate,
                qualityRequirementsRes = R.string.buyer_quality_requirement,
                status = OfferStatus.PENDING,
                createdAt = "Today",
                expiresAt = "Valid for 24 hours"
            ),
            Offer(
                id = "offer_${lotId}_xyz",
                lotId = lotId,
                buyerId = "buyer_xyz",
                buyerNameRes = R.string.buyer_xyz_traders,
                isVerifiedBuyer = true,
                farmerRating = 4.5,
                pricePerQuintal = 4800,
                quantityQuintals = quantity,
                quotedTotalAmount = xyzGross,
                transportExpensePerQ = 140,
                otherExpensePerQ = 30,
                estimatedNetAmount = xyzNet,
                paymentTermsRes = R.string.payment_within_3_days,
                deliveryRequirementsRes = R.string.buyer_delivery_center_or_farmgate,
                qualityRequirementsRes = R.string.buyer_quality_requirement,
                status = OfferStatus.PENDING,
                createdAt = "Today",
                expiresAt = "Valid for 24 hours"
            ),
            Offer(
                id = "offer_${lotId}_ngp",
                lotId = lotId,
                buyerId = "buyer_nagpur_agro",
                buyerNameRes = R.string.buyer_nagpur_agro,
                isVerifiedBuyer = true,
                farmerRating = 4.3,
                pricePerQuintal = 4760,
                quantityQuintals = quantity,
                quotedTotalAmount = ngpGross,
                transportExpensePerQ = 150,
                otherExpensePerQ = 30,
                estimatedNetAmount = ngpNet,
                paymentTermsRes = R.string.payment_within_4_days,
                deliveryRequirementsRes = R.string.buyer_delivery_center_or_farmgate,
                qualityRequirementsRes = R.string.buyer_quality_requirement,
                status = OfferStatus.PENDING,
                createdAt = "Today",
                expiresAt = "Valid for 24 hours"
            )
        )
    }

    override fun getBuyers(): Flow<List<Buyer>> = buyersFlow.asStateFlow()

    override fun getBuyerById(id: String): Buyer? {
        return buyersList.firstOrNull { it.id == id }
    }

    override fun getOffersForLot(lotId: String): Flow<List<Offer>> {
        return offersFlow.map { allOffers ->
            val forLot = allOffers.filter { it.lotId == lotId }
            if (forLot.isEmpty()) {
                createOffersForLot(lotId, 50)
            } else {
                forLot
            }
        }
    }

    override fun getOfferById(offerId: String): Offer? {
        val found = offersFlow.value.firstOrNull { it.id == offerId }
        if (found != null) return found
        val fallbackOffers = createOffersForLot("Lot #AG-1024", 50)
        return fallbackOffers.firstOrNull { it.id == offerId }
    }

    override suspend fun acceptOffer(offerId: String): Result<AgriTransaction> {
        val currentOffers = offersFlow.value.toMutableList()
        val offerIndex = currentOffers.indexOfFirst { it.id == offerId }
        val targetOffer = if (offerIndex != -1) {
            currentOffers[offerIndex]
        } else {
            getOfferById(offerId) ?: return Result.failure(IllegalArgumentException("Offer not found"))
        }

        // 1. Update this offer to ACCEPTED
        val acceptedOffer = targetOffer.copy(status = OfferStatus.ACCEPTED)
        if (offerIndex != -1) {
            currentOffers[offerIndex] = acceptedOffer
            // 2. Mark other offers for this lot as CANCELLED
            for (i in currentOffers.indices) {
                if (currentOffers[i].lotId == targetOffer.lotId && currentOffers[i].id != targetOffer.id) {
                    currentOffers[i] = currentOffers[i].copy(status = OfferStatus.CANCELLED)
                }
            }
            offersFlow.value = currentOffers
        } else {
            offersFlow.value = offersFlow.value + acceptedOffer
        }

        // 3. Update the lot status in lotsFlow
        val currentLots = lotsFlow.value.toMutableList()
        val lotIndex = currentLots.indexOfFirst { it.lotId == targetOffer.lotId }
        val associatedCrop = if (lotIndex != -1) currentLots[lotIndex].cropNameRes else R.string.crop_soybean
        val associatedEmoji = if (lotIndex != -1) currentLots[lotIndex].iconEmoji else "🌱"

        if (lotIndex != -1) {
            currentLots[lotIndex] = currentLots[lotIndex].copy(
                statusRes = R.string.status_offer_accepted,
                buyerNameRes = targetOffer.buyerNameRes
            )
            lotsFlow.value = currentLots
        }

        // 4. Create and persist Transaction in transactionsFlow
        val txnId = if (targetOffer.lotId == "Lot #AG-1024") "AG-TXN-1024" else "AG-TXN-${targetOffer.lotId.replace("Lot #AG-", "")}"
        val transaction = AgriTransaction(
            id = txnId,
            lotId = targetOffer.lotId,
            offerId = targetOffer.id,
            buyerId = targetOffer.buyerId,
            buyerNameRes = targetOffer.buyerNameRes,
            cropNameRes = associatedCrop,
            iconEmoji = associatedEmoji,
            quantityQuintals = targetOffer.quantityQuintals,
            agreedPricePerQ = targetOffer.pricePerQuintal,
            grossProduceValue = targetOffer.quantityQuintals * targetOffer.pricePerQuintal,
            transportDeduction = targetOffer.quantityQuintals * 120,
            otherDeductions = targetOffer.quantityQuintals * 30,
            estimatedNetAmount = targetOffer.estimatedNetAmount,
            status = TransactionStatus.OFFER_ACCEPTED,
            paymentStatus = PaymentStatus.PENDING,
            createdAt = "Today"
        )

        transactionsFlow.value = listOf(transaction) + transactionsFlow.value.filter { it.lotId != targetOffer.lotId }

        return Result.success(transaction)
    }

    override suspend fun rejectOffer(offerId: String): Result<Unit> {
        val currentOffers = offersFlow.value.toMutableList()
        val offerIndex = currentOffers.indexOfFirst { it.id == offerId }
        if (offerIndex != -1) {
            currentOffers[offerIndex] = currentOffers[offerIndex].copy(status = OfferStatus.REJECTED)
            offersFlow.value = currentOffers
        }
        return Result.success(Unit)
    }

    override fun getActiveTransactions(): Flow<List<AgriTransaction>> = transactionsFlow.asStateFlow()

    override fun getTransactionById(id: String): AgriTransaction? {
        val found = transactionsFlow.value.firstOrNull { it.id == id }
        if (found != null) return found
        if (id == "AG-TXN-1024" || id.startsWith("TX-1024") || id == "default_txn") {
            return defaultTransaction
        }
        return null
    }

    override fun getLotById(lotId: String): ProduceLot? {
        return lotsFlow.value.firstOrNull { it.lotId == lotId }
    }

    // --- Phase 4: State Machine, Logistics, Payment & Grievances ---

    private fun validateTransition(current: TransactionStatus, next: TransactionStatus): Boolean {
        return when (current) {
            TransactionStatus.OFFER_ACCEPTED ->
                next == TransactionStatus.LOGISTICS_BOOKED || next == TransactionStatus.CANCELLED
            TransactionStatus.LOGISTICS_BOOKED ->
                next == TransactionStatus.DISPATCHED || next == TransactionStatus.CANCELLED
            TransactionStatus.DISPATCHED ->
                next == TransactionStatus.DELIVERED || next == TransactionStatus.DISPUTED
            TransactionStatus.DELIVERED ->
                next == TransactionStatus.PAYMENT_INITIATED || next == TransactionStatus.DISPUTED
            TransactionStatus.PAYMENT_INITIATED ->
                next == TransactionStatus.PAYMENT_RECEIVED || next == TransactionStatus.PAYMENT_DELAYED || next == TransactionStatus.DISPUTED
            TransactionStatus.PAYMENT_DELAYED ->
                next == TransactionStatus.PAYMENT_RECEIVED || next == TransactionStatus.DISPUTED
            TransactionStatus.PAYMENT_RECEIVED ->
                next == TransactionStatus.COMPLETED
            TransactionStatus.COMPLETED -> false
            TransactionStatus.CANCELLED -> false
            TransactionStatus.DISPUTED ->
                next == TransactionStatus.COMPLETED || next == TransactionStatus.CANCELLED
        }
    }

    override suspend fun updateTransactionStatus(transactionId: String, newStatus: TransactionStatus): Result<AgriTransaction> {
        val tx = getTransactionById(transactionId)
            ?: return Result.failure(IllegalArgumentException("Transaction not found"))

        if (!validateTransition(tx.status, newStatus)) {
            return Result.failure(IllegalStateException("Invalid state transition from ${tx.status} to $newStatus"))
        }

        val updatedPaymentStatus = when (newStatus) {
            TransactionStatus.PAYMENT_INITIATED -> PaymentStatus.INITIATED
            TransactionStatus.PAYMENT_RECEIVED, TransactionStatus.COMPLETED -> PaymentStatus.RECEIVED
            TransactionStatus.PAYMENT_DELAYED -> PaymentStatus.DELAYED
            else -> tx.paymentStatus
        }

        val updatedTx = tx.copy(status = newStatus, paymentStatus = updatedPaymentStatus)
        val list = transactionsFlow.value.toMutableList()
        val idx = list.indexOfFirst { it.id == transactionId }
        if (idx != -1) {
            list[idx] = updatedTx
            transactionsFlow.value = list
        } else {
            transactionsFlow.value = listOf(updatedTx) + list
        }

        // Synchronize corresponding ProduceLot status in My Lots
        val newLotStatusRes = when (newStatus) {
            TransactionStatus.OFFER_ACCEPTED -> R.string.tx_status_offer_accepted
            TransactionStatus.LOGISTICS_BOOKED -> R.string.tx_status_logistics_booked
            TransactionStatus.DISPATCHED -> R.string.tx_status_dispatched
            TransactionStatus.DELIVERED -> R.string.tx_status_delivered
            TransactionStatus.PAYMENT_INITIATED -> R.string.tx_status_payment_initiated
            TransactionStatus.PAYMENT_RECEIVED, TransactionStatus.COMPLETED -> R.string.tx_status_completed
            TransactionStatus.PAYMENT_DELAYED -> R.string.tx_status_payment_delayed
            TransactionStatus.CANCELLED -> R.string.tx_status_cancelled
            TransactionStatus.DISPUTED -> R.string.tx_status_disputed
        }
        val lots = lotsFlow.value.toMutableList()
        val lotIdx = lots.indexOfFirst { it.lotId == tx.lotId }
        if (lotIdx != -1) {
            lots[lotIdx] = lots[lotIdx].copy(statusRes = newLotStatusRes)
            lotsFlow.value = lots
        }

        return Result.success(updatedTx)
    }

    override fun getTransporterOptions(cropRes: Int, quantity: Int): List<TransporterOption> {
        return listOf(
            TransporterOption(
                id = "transporter_shree_agro",
                name = "Shree Agro Transport",
                vehicleTypeRes = R.string.vehicle_small_medium,
                isVerified = true,
                rating = 4.6,
                estimatedPickupTimeRes = R.string.estimated_pickup_time,
                distanceKm = 42,
                totalCost = 6000,
                costPerQuintal = 120,
                pickupLocationRes = R.string.loc_nagpur,
                deliveryLocation = "ABC Foods"
            )
        )
    }

    override suspend fun bookTransport(transactionId: String, transporterId: String): Result<TransportBooking> {
        val tx = getTransactionById(transactionId)
            ?: return Result.failure(IllegalArgumentException("Transaction not found"))

        val booking = TransportBooking(
            bookingId = "TR-BK-1024",
            transactionId = transactionId,
            transporterName = "Shree Agro Transport",
            vehicleTypeRes = R.string.vehicle_small_medium,
            pickupLocation = "Nagpur, Maharashtra",
            deliveryLocation = "ABC Foods",
            pickupTime = "Tomorrow, 8:00 AM",
            distanceKm = 42,
            totalCost = 6000,
            costPerQ = 120,
            isConfirmed = true
        )

        // Progress transaction state if at OFFER_ACCEPTED
        if (tx.status == TransactionStatus.OFFER_ACCEPTED) {
            val statusResult = updateTransactionStatus(transactionId, TransactionStatus.LOGISTICS_BOOKED)
            if (statusResult.isFailure) {
                return Result.failure(statusResult.exceptionOrNull() ?: Exception("Cannot book logistics"))
            }
        }

        bookingsMap[transactionId] = booking
        val list = transactionsFlow.value.toMutableList()
        val idx = list.indexOfFirst { it.id == transactionId }
        if (idx != -1) {
            list[idx] = list[idx].copy(transporterBooking = booking)
            transactionsFlow.value = list
        }

        return Result.success(booking)
    }

    override fun getTransportBooking(transactionId: String): TransportBooking? {
        return bookingsMap[transactionId] ?: getTransactionById(transactionId)?.transporterBooking
    }

    override suspend fun confirmProducePickup(transactionId: String): Result<AgriTransaction> {
        return updateTransactionStatus(transactionId, TransactionStatus.DISPATCHED)
    }

    override suspend fun confirmProduceDelivery(transactionId: String): Result<AgriTransaction> {
        return updateTransactionStatus(transactionId, TransactionStatus.DELIVERED)
    }

    override fun getPaymentDetails(transactionId: String): PaymentDetails? {
        val tx = getTransactionById(transactionId) ?: return null
        return PaymentDetails(
            transactionId = tx.id,
            buyerNameRes = tx.buyerNameRes,
            cropNameRes = tx.cropNameRes,
            quantityQuintals = tx.quantityQuintals,
            agreedPricePerQ = tx.agreedPricePerQ,
            grossProduceValue = tx.grossProduceValue,
            transportCost = tx.transportDeduction,
            otherCosts = tx.otherDeductions,
            estimatedNetAmount = tx.estimatedNetAmount,
            status = tx.paymentStatus,
            paymentDate = "Today",
            paymentMethod = "Direct Bank Transfer (RTGS/NEFT)"
        )
    }

    override suspend fun initiatePayment(transactionId: String): Result<PaymentDetails> {
        val tx = getTransactionById(transactionId)
            ?: return Result.failure(IllegalArgumentException("Transaction not found"))
        if (tx.status == TransactionStatus.DELIVERED) {
            updateTransactionStatus(transactionId, TransactionStatus.PAYMENT_INITIATED)
        }
        return getPaymentDetails(transactionId)?.let { Result.success(it) }
            ?: Result.failure(IllegalStateException("Unable to get payment details"))
    }

    override suspend fun recordPaymentReceived(transactionId: String): Result<PaymentDetails> {
        val updateRes = updateTransactionStatus(transactionId, TransactionStatus.PAYMENT_RECEIVED)
        if (updateRes.isFailure) {
            return Result.failure(updateRes.exceptionOrNull() ?: Exception("Cannot mark payment received"))
        }
        return getPaymentDetails(transactionId)?.let { Result.success(it) }
            ?: Result.failure(IllegalStateException("Payment details not found"))
    }

    override suspend fun flagPaymentDelayed(transactionId: String): Result<PaymentDetails> {
        val updateRes = updateTransactionStatus(transactionId, TransactionStatus.PAYMENT_DELAYED)
        if (updateRes.isFailure) {
            return Result.failure(updateRes.exceptionOrNull() ?: Exception("Cannot flag payment delayed"))
        }
        return getPaymentDetails(transactionId)?.let { Result.success(it) }
            ?: Result.failure(IllegalStateException("Payment details not found"))
    }

    override suspend fun rateBuyer(transactionId: String, rating: Int, feedback: String?): Result<Unit> {
        val list = transactionsFlow.value.toMutableList()
        val idx = list.indexOfFirst { it.id == transactionId }
        if (idx != -1) {
            val updated = list[idx].copy(buyerRating = rating, buyerFeedback = feedback)
            list[idx] = updated
            transactionsFlow.value = list
            if (updated.status == TransactionStatus.PAYMENT_RECEIVED) {
                updateTransactionStatus(transactionId, TransactionStatus.COMPLETED)
            }
        }
        return Result.success(Unit)
    }

    override suspend fun submitGrievance(transactionId: String, issueTypeRes: Int, details: String): Result<GrievanceIssue> {
        val issue = GrievanceIssue(
            id = "GRV-${System.currentTimeMillis().toString().takeLast(4)}",
            transactionId = transactionId,
            issueTypeRes = issueTypeRes,
            details = details,
            createdAt = "Today"
        )
        grievancesList.add(issue)
        return Result.success(issue)
    }

    override fun getGrievancesForTransaction(transactionId: String): List<GrievanceIssue> {
        return grievancesList.filter { it.transactionId == transactionId }
    }
}
