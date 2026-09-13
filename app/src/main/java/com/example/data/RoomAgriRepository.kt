package com.example.data

import com.example.R
import com.example.data.local.database.KisanSetuDatabase
import com.example.data.local.entity.GrievanceEntity
import com.example.data.local.entity.LogisticsBookingEntity
import com.example.data.local.entity.OfferEntity
import com.example.data.local.entity.PaymentRecordEntity
import com.example.data.local.entity.ProduceLotEntity
import com.example.data.local.entity.SyncStatus
import com.example.data.local.entity.TransactionEntity
import com.example.model.AgriTransaction
import com.example.model.Buyer
import com.example.model.CropOption
import com.example.model.GrievanceIssue
import com.example.model.HelpCategory
import com.example.model.LotStatus
import com.example.model.MarketPriceInfo
import com.example.model.Offer
import com.example.model.OfferStatus
import com.example.model.PaymentDetails
import com.example.model.PaymentStatus
import com.example.model.ProduceItem
import com.example.model.ProduceLot
import com.example.model.SellingOpportunity
import com.example.model.TransactionStatus
import com.example.model.TransportBooking
import com.example.model.TransporterOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Production-quality Room-backed implementation of AgriRepository.
 * Acts as the single local source of truth for offline-first agricultural trade.
 */
class RoomAgriRepository(
    val database: KisanSetuDatabase,
    override val recommendationService: RecommendationService = MockRecommendationService()
) : AgriRepository {

    private var nextLotSeq = 1025
    private var nextPublishedSeq = 1

    private val availableCrops = listOf(
        CropOption("soybean", R.string.crop_soybean, "🌱", 4850),
        CropOption("wheat", R.string.crop_wheat, "🌾", 2400),
        CropOption("rice", R.string.crop_rice, "🌾", 2800),
        CropOption("maize", R.string.crop_maize, "🌽", 2150),
        CropOption("other", R.string.crop_other, "🥜", 3500)
    )

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

    override fun getCurrentProduce(): ProduceItem = ProduceItem(
        cropNameRes = R.string.crop_soybean,
        iconEmoji = "🌱",
        quantityQuintals = 50,
        qualityRes = R.string.produce_quality_good
    )

    override fun getTodaysPrimaryPrice(): MarketPriceInfo {
        return runBlocking(Dispatchers.IO) {
            val prices = database.marketPriceDao().getAllPrices()
            prices.firstOrNull()?.toDomain() ?: MarketPriceInfo(
                id = "ngp_soy",
                cropNameRes = R.string.crop_soybean,
                marketNameRes = R.string.market_nagpur,
                pricePerQuintal = 4850,
                priceChangeTextRes = R.string.price_change_yesterday,
                isPositiveChange = true
            )
        }
    }

    override fun getBestSellingOpportunity(): SellingOpportunity = SellingOpportunity(
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

    override fun getMarketPrices(): List<MarketPriceInfo> {
        return runBlocking(Dispatchers.IO) {
            database.marketPriceDao().getAllPrices().map { it.toDomain() }
        }
    }

    override fun getAvailableCrops(): List<CropOption> = availableCrops

    override fun getMyLots(): Flow<List<ProduceLot>> {
        return database.produceLotDao().getAllLotsFlow().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getHelpCategories(): List<HelpCategory> = helpCategories

    override suspend fun addLot(
        cropRes: Int,
        emoji: String,
        quantity: Int,
        qualityRes: Int,
        buyerNameRes: Int?,
        location: String,
        readyTiming: String,
        expectedPricePerQ: Int,
        estimatedNetPerQ: Int,
        photos: List<String>,
        statusRes: Int,
        customLotId: String?,
        autoGenerateOffers: Boolean
    ): ProduceLot = withContext(Dispatchers.IO) {
        val lotId = customLotId ?: "Lot #AG-$nextLotSeq".also { nextLotSeq++ }
        val lotStatus = if (statusRes == R.string.lot_status_draft) LotStatus.DRAFT else LotStatus.PUBLISHED
        val lotEntity = ProduceLotEntity(
            lotId = lotId,
            cropNameRes = cropRes,
            iconEmoji = emoji,
            quantityQuintals = quantity,
            qualityRes = qualityRes,
            statusRes = statusRes,
            dateCreated = "Just now",
            buyerNameRes = buyerNameRes,
            location = location,
            readyTiming = readyTiming,
            expectedPricePerQ = expectedPricePerQ,
            estimatedNetPerQ = estimatedNetPerQ,
            photos = photos,
            farmerId = "farmer_nagpur_01",
            lotStatus = lotStatus,
            syncStatus = SyncStatus.PENDING_UPLOAD
        )
        database.produceLotDao().insertLot(lotEntity)

        if (autoGenerateOffers) {
            val offers = createOffersForLot(lotId, quantity)
            database.offerDao().insertOffers(offers.map { OfferEntity.fromDomain(it, SyncStatus.PENDING_UPLOAD) })
        }

        lotEntity.toDomain()
    }

    override suspend fun publishLot(
        cropRes: Int,
        emoji: String,
        quantity: Int,
        qualityRes: Int,
        buyerNameRes: Int?,
        location: String,
        readyTiming: String,
        expectedPricePerQ: Int,
        estimatedNetPerQ: Int,
        photos: List<String>
    ): ProduceLot {
        val lotId = "LOT-AGL-2026-${String.format(Locale.US, "%04d", nextPublishedSeq++)}"
        return addLot(
            cropRes = cropRes,
            emoji = emoji,
            quantity = quantity,
            qualityRes = qualityRes,
            buyerNameRes = buyerNameRes,
            location = location,
            readyTiming = readyTiming,
            expectedPricePerQ = expectedPricePerQ,
            estimatedNetPerQ = estimatedNetPerQ,
            photos = photos,
            statusRes = R.string.lot_status_published,
            customLotId = lotId,
            autoGenerateOffers = false
        )
    }

    private fun createOffersForLot(lotId: String, quantity: Int): List<Offer> {
        val abcGross = 4850 * quantity
        val abcTransportTotal = 120 * quantity
        val abcOtherTotal = 30 * quantity
        val abcNet = abcGross - abcTransportTotal - abcOtherTotal

        val xyzGross = 4900 * quantity
        val xyzTransportTotal = 250 * quantity
        val xyzOtherTotal = 30 * quantity
        val xyzNet = xyzGross - xyzTransportTotal - xyzOtherTotal

        val ngpGross = 4760 * quantity
        val ngpTransportTotal = 50 * quantity
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
                expiresAt = "Valid for 24 hours",
                distanceKm = 28,
                reliabilityTextRes = R.string.reliability_very_reliable,
                isBestOffer = true,
                whyBetterReasons = listOf(
                    R.string.reason_higher_net,
                    R.string.reason_verified_buyer_check,
                    R.string.reason_reliable_payment_check,
                    R.string.reason_suitable_quantity_check
                )
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
                transportExpensePerQ = 50,
                otherExpensePerQ = 30,
                estimatedNetAmount = ngpNet,
                paymentTermsRes = R.string.payment_within_4_days,
                deliveryRequirementsRes = R.string.buyer_delivery_center_or_farmgate,
                qualityRequirementsRes = R.string.buyer_quality_requirement,
                status = OfferStatus.PENDING,
                createdAt = "Today",
                expiresAt = "Valid for 24 hours",
                distanceKm = 18,
                reliabilityTextRes = R.string.reliability_regulated_market,
                isBestOffer = false,
                whyBetterReasons = emptyList()
            ),
            Offer(
                id = "offer_${lotId}_xyz",
                lotId = lotId,
                buyerId = "buyer_xyz",
                buyerNameRes = R.string.buyer_xyz_traders,
                isVerifiedBuyer = true,
                farmerRating = 4.5,
                pricePerQuintal = 4900,
                quantityQuintals = quantity,
                quotedTotalAmount = xyzGross,
                transportExpensePerQ = 250,
                otherExpensePerQ = 30,
                estimatedNetAmount = xyzNet,
                paymentTermsRes = R.string.payment_within_3_days,
                deliveryRequirementsRes = R.string.buyer_delivery_center_or_farmgate,
                qualityRequirementsRes = R.string.buyer_quality_requirement,
                status = OfferStatus.PENDING,
                createdAt = "Today",
                expiresAt = "Valid for 24 hours",
                distanceKm = 45,
                reliabilityTextRes = R.string.reliability_good,
                isBestOffer = false,
                whyBetterReasons = emptyList()
            )
        )
    }

    override fun getBuyers(): Flow<List<Buyer>> {
        return database.buyerDao().getAllBuyersFlow().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getMatchingBuyersForLot(lotId: String): Flow<List<Buyer>> {
        return getBuyers().map { allBuyers ->
            val lot = getLotById(lotId)
            if (lot != null) {
                allBuyers.filter { buyer ->
                    buyer.buysCommodities.contains(lot.cropNameRes) || buyer.commodityRes == lot.cropNameRes
                }
            } else {
                allBuyers
            }
        }
    }

    override fun getBuyerById(id: String): Buyer? {
        return runBlocking(Dispatchers.IO) {
            database.buyerDao().getBuyerById(id)?.toDomain()
        }
    }

    override fun getOffersForLot(lotId: String): Flow<List<Offer>> {
        return database.offerDao().getOffersForLotFlow(lotId).map { entities ->
            if (entities.isEmpty() && (lotId == "Lot #AG-1024" || lotId.startsWith("Lot #AG-"))) {
                val lot = getLotById(lotId)
                val qty = lot?.quantityQuintals ?: 50
                val newOffers = createOffersForLot(lotId, qty)
                withContext(Dispatchers.IO) {
                    database.offerDao().insertOffers(newOffers.map { OfferEntity.fromDomain(it) })
                }
                newOffers.sortedByDescending { it.estimatedNetAmount }
            } else {
                entities.map { it.toDomain() }.sortedByDescending { it.estimatedNetAmount }
            }
        }
    }

    override fun generateOffersForLot(lotId: String): List<Offer> {
        return runBlocking(Dispatchers.IO) {
            val existing = database.offerDao().getOffersForLot(lotId)
            if (existing.isNotEmpty()) return@runBlocking existing.map { it.toDomain() }
            val lot = database.produceLotDao().getLotById(lotId)?.toDomain()
            val qty = lot?.quantityQuintals ?: 50
            val newOffers = createOffersForLot(lotId, qty)
            database.offerDao().insertOffers(newOffers.map { OfferEntity.fromDomain(it) })
            newOffers
        }
    }

    override fun getOfferById(offerId: String): Offer? {
        return runBlocking(Dispatchers.IO) {
            database.offerDao().getOfferById(offerId)?.toDomain()
                ?: createOffersForLot("Lot #AG-1024", 50).firstOrNull { it.id == offerId }
        }
    }

    override suspend fun acceptOffer(offerId: String): Result<AgriTransaction> = withContext(Dispatchers.IO) {
        val targetOffer = database.offerDao().getOfferById(offerId)?.toDomain()
            ?: getOfferById(offerId)
            ?: return@withContext Result.failure(IllegalArgumentException("Offer not found"))

        // 1. Mark this offer as ACCEPTED and others as CANCELLED
        database.offerDao().updateOfferStatus(targetOffer.id, OfferStatus.ACCEPTED)
        database.offerDao().cancelOtherOffersForLot(targetOffer.lotId, targetOffer.id)

        // 2. Update the lot status in Room
        val lot = database.produceLotDao().getLotById(targetOffer.lotId)
        val associatedCrop = lot?.cropNameRes ?: R.string.crop_soybean
        val associatedEmoji = lot?.iconEmoji ?: "🌱"

        database.produceLotDao().updateLotStatus(
            lotId = targetOffer.lotId,
            statusRes = R.string.status_offer_accepted,
            lotStatus = LotStatus.OFFER_ACCEPTED,
            buyerNameRes = targetOffer.buyerNameRes
        )

        // 3. Create and persist Transaction
        val txnId = if (targetOffer.lotId == "Lot #AG-1024") "AG-TXN-1024"
        else "AG-TXN-${targetOffer.lotId.replace("Lot #AG-", "").replace("LOT-AGL-2026-", "")}"

        val txEntity = TransactionEntity(
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
            transportDeduction = targetOffer.quantityQuintals * targetOffer.transportExpensePerQ,
            otherDeductions = targetOffer.quantityQuintals * targetOffer.otherExpensePerQ,
            estimatedNetAmount = targetOffer.estimatedNetAmount,
            status = TransactionStatus.OFFER_ACCEPTED,
            paymentStatus = PaymentStatus.PENDING,
            createdAt = "Today",
            syncStatus = SyncStatus.PENDING_UPLOAD
        )
        database.transactionDao().insertTransaction(txEntity)

        Result.success(txEntity.toDomain())
    }

    override suspend fun rejectOffer(offerId: String): Result<Unit> = withContext(Dispatchers.IO) {
        database.offerDao().updateOfferStatus(offerId, OfferStatus.REJECTED)
        Result.success(Unit)
    }

    override fun getActiveTransactions(): Flow<List<AgriTransaction>> {
        return database.transactionDao().getAllTransactionsFlow().map { list ->
            list.map { tx ->
                val booking = database.logisticsBookingDao().getBookingForTransaction(tx.id)?.toDomain()
                tx.toDomain(booking)
            }
        }
    }

    override fun getTransactionById(id: String): AgriTransaction? {
        return runBlocking(Dispatchers.IO) {
            val tx = database.transactionDao().getTransactionById(id)
                ?: (if (id == "AG-TXN-1024" || id.startsWith("TX-1024") || id == "default_txn") {
                    database.transactionDao().getTransactionById("AG-TXN-1024")
                } else null)
            val booking = tx?.let { database.logisticsBookingDao().getBookingForTransaction(it.id)?.toDomain() }
            tx?.toDomain(booking)
        }
    }

    override fun getLotById(lotId: String): ProduceLot? {
        return runBlocking(Dispatchers.IO) {
            database.produceLotDao().getLotById(lotId)?.toDomain()
        }
    }

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

    override suspend fun updateTransactionStatus(
        transactionId: String,
        newStatus: TransactionStatus
    ): Result<AgriTransaction> = withContext(Dispatchers.IO) {
        val tx = database.transactionDao().getTransactionById(transactionId)
            ?: return@withContext Result.failure(IllegalArgumentException("Transaction not found"))

        if (!validateTransition(tx.status, newStatus)) {
            return@withContext Result.failure(IllegalStateException("Invalid state transition from ${tx.status} to $newStatus"))
        }

        val updatedPaymentStatus = when (newStatus) {
            TransactionStatus.PAYMENT_INITIATED -> PaymentStatus.INITIATED
            TransactionStatus.PAYMENT_RECEIVED, TransactionStatus.COMPLETED -> PaymentStatus.RECEIVED
            TransactionStatus.PAYMENT_DELAYED -> PaymentStatus.DELAYED
            else -> tx.paymentStatus
        }

        database.transactionDao().updateTransactionStatus(
            id = transactionId,
            status = newStatus,
            paymentStatus = updatedPaymentStatus
        )

        // Synchronize corresponding ProduceLot status in Room
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
        database.produceLotDao().updateLotStatus(
            lotId = tx.lotId,
            statusRes = newLotStatusRes,
            lotStatus = LotStatus.OFFER_ACCEPTED,
            buyerNameRes = tx.buyerNameRes
        )

        val updatedTx = database.transactionDao().getTransactionById(transactionId)!!
        val booking = database.logisticsBookingDao().getBookingForTransaction(transactionId)?.toDomain()
        Result.success(updatedTx.toDomain(booking))
    }

    override fun getTransporterOptions(cropRes: Int, quantity: Int): List<TransporterOption> {
        return listOf(
            TransporterOption(
                id = "transporter_truck",
                name = "Truck",
                vehicleTypeRes = R.string.vehicle_truck,
                isVerified = true,
                rating = 4.7,
                estimatedPickupTimeRes = R.string.delivery_today,
                distanceKm = 28,
                totalCost = 6000,
                costPerQuintal = 120,
                pickupLocationRes = R.string.loc_nagpur,
                deliveryLocation = "ABC Foods",
                capacityQuintals = 100,
                deliveryTimingRes = R.string.delivery_today
            ),
            TransporterOption(
                id = "transporter_mini_truck",
                name = "Mini Truck",
                vehicleTypeRes = R.string.vehicle_mini_truck,
                isVerified = true,
                rating = 4.5,
                estimatedPickupTimeRes = R.string.delivery_tomorrow,
                distanceKm = 28,
                totalCost = 5500,
                costPerQuintal = 110,
                pickupLocationRes = R.string.loc_nagpur,
                deliveryLocation = "ABC Foods",
                capacityQuintals = 50,
                deliveryTimingRes = R.string.delivery_tomorrow
            )
        )
    }

    override suspend fun bookTransport(
        transactionId: String,
        transporterId: String
    ): Result<TransportBooking> = withContext(Dispatchers.IO) {
        val tx = database.transactionDao().getTransactionById(transactionId)
            ?: return@withContext Result.failure(IllegalArgumentException("Transaction not found"))

        val options = getTransporterOptions(tx.cropNameRes, tx.quantityQuintals)
        val selected = options.find { it.id == transporterId }
            ?: (if (transporterId.contains("mini", ignoreCase = true)) options.getOrNull(1) else null)
            ?: options.first()

        val booking = TransportBooking(
            bookingId = "TR-BK-1024",
            transactionId = transactionId,
            transporterName = selected.name,
            vehicleTypeRes = selected.vehicleTypeRes,
            pickupLocation = "Your location",
            deliveryLocation = selected.deliveryLocation,
            pickupTime = "Today, 2:00 PM",
            distanceKm = selected.distanceKm,
            totalCost = selected.totalCost,
            costPerQ = selected.costPerQuintal,
            isConfirmed = true,
            capacityQuintals = selected.capacityQuintals,
            deliveryTimingRes = selected.deliveryTimingRes
        )

        database.logisticsBookingDao().insertBooking(
            LogisticsBookingEntity.fromDomain(booking, SyncStatus.PENDING_UPLOAD)
        )

        // Progress transaction state if at OFFER_ACCEPTED
        val targetStatus = if (tx.status == TransactionStatus.OFFER_ACCEPTED) {
            TransactionStatus.LOGISTICS_BOOKED
        } else {
            tx.status
        }

        val estimatedNet = tx.grossProduceValue - selected.totalCost - tx.otherDeductions
        database.transactionDao().updateTransportBookingDetails(
            id = transactionId,
            transportCost = selected.totalCost,
            estimatedNet = estimatedNet,
            status = targetStatus
        )

        Result.success(booking)
    }

    override fun getTransportBooking(transactionId: String): TransportBooking? {
        return runBlocking(Dispatchers.IO) {
            database.logisticsBookingDao().getBookingForTransaction(transactionId)?.toDomain()
        }
    }

    override suspend fun confirmProducePickup(transactionId: String): Result<AgriTransaction> {
        return updateTransactionStatus(transactionId, TransactionStatus.DISPATCHED)
    }

    override suspend fun confirmProduceDelivery(transactionId: String): Result<AgriTransaction> {
        return updateTransactionStatus(transactionId, TransactionStatus.DELIVERED)
    }

    override fun getPaymentDetails(transactionId: String): PaymentDetails? {
        return runBlocking(Dispatchers.IO) {
            val record = database.paymentRecordDao().getPaymentForTransaction(transactionId)
            if (record != null) {
                return@runBlocking record.toDomain()
            }
            val tx = database.transactionDao().getTransactionById(transactionId) ?: return@runBlocking null
            PaymentDetails(
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
    }

    override suspend fun initiatePayment(transactionId: String): Result<PaymentDetails> = withContext(Dispatchers.IO) {
        val tx = database.transactionDao().getTransactionById(transactionId)
            ?: return@withContext Result.failure(IllegalArgumentException("Transaction not found"))

        if (tx.status == TransactionStatus.DELIVERED) {
            updateTransactionStatus(transactionId, TransactionStatus.PAYMENT_INITIATED)
        }

        val details = getPaymentDetails(transactionId)
            ?: return@withContext Result.failure(IllegalStateException("Unable to get payment details"))

        database.paymentRecordDao().insertPayment(
            PaymentRecordEntity.fromDomain(details.copy(status = PaymentStatus.INITIATED), SyncStatus.PENDING_UPLOAD)
        )
        Result.success(details.copy(status = PaymentStatus.INITIATED))
    }

    override suspend fun recordPaymentReceived(transactionId: String): Result<PaymentDetails> = withContext(Dispatchers.IO) {
        val updateRes = updateTransactionStatus(transactionId, TransactionStatus.PAYMENT_RECEIVED)
        if (updateRes.isFailure) {
            return@withContext Result.failure(updateRes.exceptionOrNull() ?: Exception("Cannot mark payment received"))
        }
        val details = getPaymentDetails(transactionId)
            ?: return@withContext Result.failure(IllegalStateException("Payment details not found"))

        database.paymentRecordDao().insertPayment(
            PaymentRecordEntity.fromDomain(details.copy(status = PaymentStatus.RECEIVED), SyncStatus.PENDING_UPLOAD)
        )
        Result.success(details.copy(status = PaymentStatus.RECEIVED))
    }

    override suspend fun flagPaymentDelayed(transactionId: String): Result<PaymentDetails> = withContext(Dispatchers.IO) {
        val updateRes = updateTransactionStatus(transactionId, TransactionStatus.PAYMENT_DELAYED)
        if (updateRes.isFailure) {
            return@withContext Result.failure(updateRes.exceptionOrNull() ?: Exception("Cannot flag payment delayed"))
        }
        val details = getPaymentDetails(transactionId)
            ?: return@withContext Result.failure(IllegalStateException("Payment details not found"))

        database.paymentRecordDao().insertPayment(
            PaymentRecordEntity.fromDomain(details.copy(status = PaymentStatus.DELAYED), SyncStatus.PENDING_UPLOAD)
        )
        Result.success(details.copy(status = PaymentStatus.DELAYED))
    }

    override suspend fun rateBuyer(
        transactionId: String,
        rating: Int,
        feedback: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        database.transactionDao().updateBuyerRating(transactionId, rating, feedback)
        val tx = database.transactionDao().getTransactionById(transactionId)
        if (tx != null && tx.status == TransactionStatus.PAYMENT_RECEIVED) {
            updateTransactionStatus(transactionId, TransactionStatus.COMPLETED)
        }
        Result.success(Unit)
    }

    override suspend fun submitGrievance(
        transactionId: String,
        issueTypeRes: Int,
        details: String
    ): Result<GrievanceIssue> = withContext(Dispatchers.IO) {
        val issue = GrievanceIssue(
            id = "GRV-${System.currentTimeMillis().toString().takeLast(4)}",
            transactionId = transactionId,
            issueTypeRes = issueTypeRes,
            details = details,
            createdAt = "Today"
        )
        database.grievanceDao().insertGrievance(
            GrievanceEntity.fromDomain(issue, SyncStatus.PENDING_UPLOAD)
        )
        Result.success(issue)
    }

    override fun getGrievancesForTransaction(transactionId: String): List<GrievanceIssue> {
        return runBlocking(Dispatchers.IO) {
            database.grievanceDao().getGrievancesForTransaction(transactionId).map { it.toDomain() }
        }
    }
}
