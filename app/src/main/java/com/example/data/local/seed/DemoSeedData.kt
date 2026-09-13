package com.example.data.local.seed

import com.example.R
import com.example.data.local.entity.BuyerEntity
import com.example.data.local.entity.FarmerProfileEntity
import com.example.data.local.entity.MarketDataFreshness
import com.example.data.local.entity.MarketPriceEntity
import com.example.data.local.entity.OfferEntity
import com.example.data.local.entity.ProduceLotEntity
import com.example.data.local.entity.SyncStatus
import com.example.data.local.entity.TransactionEntity
import com.example.model.LotStatus
import com.example.model.OfferStatus
import com.example.model.PaymentStatus
import com.example.model.TransactionStatus

/**
 * Demo Seed Data for KisanSetu Development & Local Offline Prepopulation.
 *
 * ARCHITECTURAL NOTICE:
 * This dataset provides the initial local baseline for offline testing and developer previews.
 * It is clearly separated from the production database schema and can be safely superseded
 * or refreshed by future remote synchronization (e.g. e-NAM, Agmarknet API, or backend auth).
 *
 * NO PRODUCTION CREDENTIALS OR REAL-TIME CLAIMS ARE MADE.
 */
object DemoSeedData {

    val primaryFarmer = FarmerProfileEntity(
        id = "primary_farmer",
        fullName = "Ramesh Patil",
        role = "Organic Soybean & Wheat Cultivator",
        village = "Katol",
        district = "Nagpur",
        state = "Maharashtra",
        fpoName = "Nagpur Krishi Vikas FPO",
        fpoMemberId = "FPO-NGP-2024-8841",
        helpline = "1800-180-1551",
        preferredLanguageCode = "en",
        syncStatus = SyncStatus.SYNCED
    )

    val initialLot = ProduceLotEntity(
        lotId = "Lot #AG-1024",
        cropNameRes = R.string.crop_soybean,
        iconEmoji = "🌱",
        quantityQuintals = 50,
        qualityRes = R.string.produce_quality_good,
        statusRes = R.string.lot_status_waiting,
        dateCreated = "Today",
        buyerNameRes = null,
        location = "Nagpur, Maharashtra",
        readyTiming = "Ready now",
        expectedPricePerQ = 4850,
        estimatedNetPerQ = 4700,
        photos = emptyList(),
        farmerId = "farmer_nagpur_01",
        lotStatus = LotStatus.PUBLISHED,
        syncStatus = SyncStatus.SYNCED
    )

    val initialOffers = listOf(
        OfferEntity(
            id = "offer_Lot #AG-1024_abc",
            lotId = "Lot #AG-1024",
            buyerId = "buyer_abc",
            buyerNameRes = R.string.buyer_abc_foods,
            isVerifiedBuyer = true,
            farmerRating = 4.7,
            pricePerQuintal = 4850,
            quantityQuintals = 50,
            quotedTotalAmount = 242500,
            transportExpensePerQ = 120,
            otherExpensePerQ = 30,
            estimatedNetAmount = 235000,
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
            ),
            syncStatus = SyncStatus.SYNCED
        ),
        OfferEntity(
            id = "offer_Lot #AG-1024_ngp",
            lotId = "Lot #AG-1024",
            buyerId = "buyer_nagpur_agro",
            buyerNameRes = R.string.buyer_nagpur_agro,
            isVerifiedBuyer = true,
            farmerRating = 4.3,
            pricePerQuintal = 4760,
            quantityQuintals = 50,
            quotedTotalAmount = 238000,
            transportExpensePerQ = 50,
            otherExpensePerQ = 30,
            estimatedNetAmount = 234000,
            paymentTermsRes = R.string.payment_within_4_days,
            deliveryRequirementsRes = R.string.buyer_delivery_center_or_farmgate,
            qualityRequirementsRes = R.string.buyer_quality_requirement,
            status = OfferStatus.PENDING,
            createdAt = "Today",
            expiresAt = "Valid for 24 hours",
            distanceKm = 18,
            reliabilityTextRes = R.string.reliability_regulated_market,
            isBestOffer = false,
            whyBetterReasons = emptyList(),
            syncStatus = SyncStatus.SYNCED
        ),
        OfferEntity(
            id = "offer_Lot #AG-1024_xyz",
            lotId = "Lot #AG-1024",
            buyerId = "buyer_xyz",
            buyerNameRes = R.string.buyer_xyz_traders,
            isVerifiedBuyer = true,
            farmerRating = 4.5,
            pricePerQuintal = 4900,
            quantityQuintals = 50,
            quotedTotalAmount = 245000,
            transportExpensePerQ = 250,
            otherExpensePerQ = 30,
            estimatedNetAmount = 231000,
            paymentTermsRes = R.string.payment_within_3_days,
            deliveryRequirementsRes = R.string.buyer_delivery_center_or_farmgate,
            qualityRequirementsRes = R.string.buyer_quality_requirement,
            status = OfferStatus.PENDING,
            createdAt = "Today",
            expiresAt = "Valid for 24 hours",
            distanceKm = 45,
            reliabilityTextRes = R.string.reliability_good,
            isBestOffer = false,
            whyBetterReasons = emptyList(),
            syncStatus = SyncStatus.SYNCED
        )
    )

    val initialTransaction = TransactionEntity(
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
        createdAt = "Today",
        syncStatus = SyncStatus.SYNCED
    )

    val buyers = listOf(
        BuyerEntity(
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
            averagePaymentDays = 2,
            distanceKm = 28,
            reliabilityTextRes = R.string.reliability_very_reliable,
            buysCommodities = listOf(R.string.crop_soybean, R.string.crop_wheat, R.string.crop_maize)
        ),
        BuyerEntity(
            id = "buyer_xyz",
            nameRes = R.string.buyer_xyz_traders,
            locationRes = R.string.loc_nagpur,
            isVerified = true,
            commodityRes = R.string.crop_soybean,
            currentDemandMinQ = 30,
            currentDemandMaxQ = 80,
            requiredQualityRes = R.string.produce_quality_good,
            quotedPricePerQ = 4900,
            paymentDaysDescriptionRes = R.string.payment_within_3_days,
            onTimePaymentPct = 92,
            completedTransactions = 84,
            farmerRating = 4.5,
            neededTimelineRes = R.string.timeline_this_week,
            disputeRatePct = 1.2,
            averagePaymentDays = 3,
            distanceKm = 45,
            reliabilityTextRes = R.string.reliability_good,
            buysCommodities = listOf(R.string.crop_soybean, R.string.crop_cotton)
        ),
        BuyerEntity(
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
            averagePaymentDays = 4,
            distanceKm = 18,
            reliabilityTextRes = R.string.reliability_regulated_market,
            buysCommodities = listOf(R.string.crop_soybean, R.string.crop_wheat, R.string.crop_chana)
        )
    )

    val marketPrices = listOf(
        MarketPriceEntity(
            id = "ngp_soy",
            cropNameRes = R.string.crop_soybean,
            marketNameRes = R.string.market_nagpur,
            pricePerQuintal = 4850,
            priceChangeTextRes = R.string.price_change_yesterday,
            isPositiveChange = true,
            dataFreshness = MarketDataFreshness.DEMO
        ),
        MarketPriceEntity(
            id = "katol_soy",
            cropNameRes = R.string.crop_soybean,
            marketNameRes = R.string.market_katol,
            pricePerQuintal = 4780,
            priceChangeTextRes = R.string.status_steady,
            isPositiveChange = true,
            dataFreshness = MarketDataFreshness.DEMO
        ),
        MarketPriceEntity(
            id = "hingna_soy",
            cropNameRes = R.string.crop_soybean,
            marketNameRes = R.string.market_hingna,
            pricePerQuintal = 4720,
            priceChangeTextRes = R.string.status_steady,
            isPositiveChange = false,
            dataFreshness = MarketDataFreshness.DEMO
        ),
        MarketPriceEntity(
            id = "amravati_soy",
            cropNameRes = R.string.crop_soybean,
            marketNameRes = R.string.market_amravati,
            pricePerQuintal = 4820,
            priceChangeTextRes = R.string.status_steady,
            isPositiveChange = true,
            dataFreshness = MarketDataFreshness.DEMO
        )
    )
}
