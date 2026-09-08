package com.example.domain

import com.example.R
import com.example.model.DestinationType
import com.example.model.SellingOpportunity

/**
 * Domain-level Intelligent Selling Recommendation Engine.
 *
 * Answers the farmer's core question: "Where should I sell my produce?"
 *
 * Core Principles:
 * - Net realization is the primary metric:
 *   Net price = quoted price - transport cost - applicable mandi/market fees - other known selling costs
 *   Total expected realization = net price × quantity
 * - Explains why a suggestion is made (e.g. good price, verified buyer, low transport, quantity match)
 * - Supports both MARKET and BUYER destination types
 * - Highlights that a higher raw quoted price does not necessarily equal higher money in the farmer's pocket
 */
class RecommendationEngine {

    /**
     * Calculates explainable selling recommendations based on inputs:
     * @param cropId Crop identifier (e.g. "soybean", "wheat")
     * @param quantityQuintals Produce quantity in quintals
     * @param qualityKey Produce quality ("good", "average", "poor")
     * @param location Farmer's location
     * @param readyTiming Harvest readiness timing
     */
    fun calculateRecommendations(
        cropId: String,
        quantityQuintals: Int,
        qualityKey: String,
        location: String,
        readyTiming: String
    ): Result<List<SellingOpportunity>> {
        if (quantityQuintals <= 0) {
            return Result.failure(IllegalArgumentException("Quantity must be greater than 0"))
        }

        val basePrice = when (cropId.lowercase()) {
            "soybean" -> 4850
            "wheat" -> 2400
            "rice" -> 2800
            "maize" -> 2150
            else -> 3500
        }

        val cropNameRes = when (cropId.lowercase()) {
            "soybean" -> R.string.crop_soybean
            "wheat" -> R.string.crop_wheat
            "rice" -> R.string.crop_rice
            "maize" -> R.string.crop_maize
            else -> R.string.crop_other
        }

        val qualityMultiplier = when (qualityKey.lowercase()) {
            "good" -> 1.00
            "average" -> 0.95
            "poor" -> 0.88
            else -> 1.00
        }

        val isPrimaryDemo = cropId.equals("soybean", ignoreCase = true) &&
                (qualityKey.isEmpty() || qualityKey.equals("good", ignoreCase = true))

        // -------------------------------------------------------------
        // Option 1: ABC Foods (Verified Direct Buyer) - BEST RECOMMENDATION
        // Quoted: ₹4,850/q, Transport: ₹120, Other: ₹30 -> Expenses: ₹150/q
        // Net: ₹4,700/q -> Total: ₹2,35,000 for 50q
        // -------------------------------------------------------------
        val abcQuoted = if (isPrimaryDemo) 4850 else (basePrice * qualityMultiplier).toInt()
        val abcTransport = 120
        val abcOther = 30
        val abcNet = abcQuoted - abcTransport - abcOther
        val abcTotal = abcNet * quantityQuintals

        val abcOption = SellingOpportunity(
            id = "opp_abc_foods",
            buyerNameRes = R.string.buyer_abc_foods,
            buyerTypeRes = R.string.buyer_type_verified_direct,
            cropNameRes = cropNameRes,
            quantityQuintals = quantityQuintals,
            quotedPricePerQ = abcQuoted,
            transportExpensePerQ = abcTransport,
            otherExpensePerQ = abcOther,
            handlingCostPerQ = 20,
            storageCostPerQ = 0,
            transactionCostPerQ = 10,
            netRealizationPerQ = abcNet,
            estimatedTotalAmount = abcTotal,
            statusTextRes = R.string.status_strong_rec,
            recommendationStrength = "STRONG",
            isTopRecommendation = true,
            distanceKm = 18,
            paymentReliabilityRes = R.string.payment_same_day,
            reasonsRes = listOf(
                R.string.reason_good_price,
                R.string.reason_verified_buyer,
                R.string.reason_affordable_transport,
                R.string.reason_suitable_qty,
                R.string.reason_payment_history
            ),
            quantityMatch = true,
            qualityMatch = true,
            isVerifiedBuyer = true,
            destinationType = DestinationType.BUYER,
            matchQualityRes = R.string.status_strong_rec,
            paymentDaysEstimate = 2,
            completedTransactionsCount = 128
        )

        // -------------------------------------------------------------
        // Option 2: Katol Market (Regulated APMC Mandi)
        // Quoted: ₹4,720/q, 45 km, Expenses: ₹180 (Transport: ₹130, Mandi: ₹50)
        // Net: ₹4,540/q -> Total: ₹2,27,000 for 50q
        // -------------------------------------------------------------
        val katolQuoted = if (isPrimaryDemo) 4720 else ((basePrice - 130) * qualityMultiplier).toInt()
        val katolTransport = 130
        val katolOther = 50
        val katolNet = katolQuoted - katolTransport - katolOther
        val katolTotal = katolNet * quantityQuintals

        val katolOption = SellingOpportunity(
            id = "opp_katol_market",
            buyerNameRes = R.string.market_katol,
            buyerTypeRes = R.string.buyer_type_regulated_apmc,
            cropNameRes = cropNameRes,
            quantityQuintals = quantityQuintals,
            quotedPricePerQ = katolQuoted,
            transportExpensePerQ = katolTransport,
            otherExpensePerQ = katolOther,
            handlingCostPerQ = 30,
            storageCostPerQ = 0,
            transactionCostPerQ = 20,
            netRealizationPerQ = katolNet,
            estimatedTotalAmount = katolTotal,
            statusTextRes = R.string.status_good_option,
            recommendationStrength = "GOOD",
            isTopRecommendation = false,
            distanceKm = 45,
            paymentReliabilityRes = R.string.payment_mandi_cycle,
            reasonsRes = listOf(
                R.string.reason_good_price,
                R.string.reason_regulated_market,
                R.string.reason_suitable_qty
            ),
            quantityMatch = true,
            qualityMatch = true,
            isVerifiedBuyer = false,
            destinationType = DestinationType.MARKET,
            matchQualityRes = R.string.status_good_option,
            paymentDaysEstimate = 2,
            completedTransactionsCount = 210
        )

        // -------------------------------------------------------------
        // Option 3: Nagpur Market (Regulated APMC Mandi)
        // Quoted: ₹4,650/q, 18 km, Expenses: ₹150 (Transport: ₹110, Mandi: ₹40)
        // Net: ₹4,500/q -> Total: ₹2,25,000 for 50q
        // -------------------------------------------------------------
        val nagpurQuoted = if (isPrimaryDemo) 4650 else ((basePrice - 200) * qualityMultiplier).toInt()
        val nagpurTransport = 110
        val nagpurOther = 40
        val nagpurNet = nagpurQuoted - nagpurTransport - nagpurOther
        val nagpurTotal = nagpurNet * quantityQuintals

        val nagpurOption = SellingOpportunity(
            id = "opp_nagpur_market",
            buyerNameRes = R.string.buyer_nagpur_mandi,
            buyerTypeRes = R.string.buyer_type_regulated_apmc,
            cropNameRes = cropNameRes,
            quantityQuintals = quantityQuintals,
            quotedPricePerQ = nagpurQuoted,
            transportExpensePerQ = nagpurTransport,
            otherExpensePerQ = nagpurOther,
            handlingCostPerQ = 25,
            storageCostPerQ = 0,
            transactionCostPerQ = 15,
            netRealizationPerQ = nagpurNet,
            estimatedTotalAmount = nagpurTotal,
            statusTextRes = R.string.status_good_option,
            recommendationStrength = "GOOD",
            isTopRecommendation = false,
            distanceKm = 18,
            paymentReliabilityRes = R.string.payment_mandi_cycle,
            reasonsRes = listOf(
                R.string.reason_low_transport,
                R.string.reason_regulated_market,
                R.string.reason_quick_unloading
            ),
            quantityMatch = true,
            qualityMatch = true,
            isVerifiedBuyer = false,
            destinationType = DestinationType.MARKET,
            matchQualityRes = R.string.status_good_option,
            paymentDaysEstimate = 1,
            completedTransactionsCount = 350
        )

        // -------------------------------------------------------------
        // Option 4: Amravati Market (Regulated APMC Mandi)
        // High Quoted Raw Price: ₹4,780/q, 80 km distance -> Transport: ₹250, Mandi: ₹60 (Expenses: ₹310)
        // Net: ₹4,470/q (Lowest net realization due to travel costs!) -> Total: ₹2,23,500 for 50q
        // -------------------------------------------------------------
        val amravatiQuoted = if (isPrimaryDemo) 4780 else ((basePrice - 70) * qualityMultiplier).toInt()
        val amravatiTransport = 250
        val amravatiOther = 60
        val amravatiNet = amravatiQuoted - amravatiTransport - amravatiOther
        val amravatiTotal = amravatiNet * quantityQuintals

        val amravatiOption = SellingOpportunity(
            id = "opp_amravati_market",
            buyerNameRes = R.string.market_amravati,
            buyerTypeRes = R.string.buyer_type_regulated_apmc,
            cropNameRes = cropNameRes,
            quantityQuintals = quantityQuintals,
            quotedPricePerQ = amravatiQuoted,
            transportExpensePerQ = amravatiTransport,
            otherExpensePerQ = amravatiOther,
            handlingCostPerQ = 40,
            storageCostPerQ = 0,
            transactionCostPerQ = 20,
            netRealizationPerQ = amravatiNet,
            estimatedTotalAmount = amravatiTotal,
            statusTextRes = R.string.status_good_option,
            recommendationStrength = "MODERATE",
            isTopRecommendation = false,
            distanceKm = 80,
            paymentReliabilityRes = R.string.payment_mandi_cycle,
            reasonsRes = listOf(
                R.string.reason_good_price,
                R.string.reason_regulated_market
            ),
            quantityMatch = true,
            qualityMatch = true,
            isVerifiedBuyer = false,
            destinationType = DestinationType.MARKET,
            matchQualityRes = R.string.status_good_option,
            paymentDaysEstimate = 2,
            completedTransactionsCount = 420
        )

        val recommendations = listOf(abcOption, katolOption, nagpurOption, amravatiOption)
        return Result.success(recommendations)
    }
}
