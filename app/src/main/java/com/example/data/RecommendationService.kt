package com.example.data

import com.example.R
import com.example.model.SellingOpportunity
import kotlinx.coroutines.delay

interface RecommendationService {
    suspend fun getSellingRecommendations(
        cropId: String,
        quantityQuintals: Int,
        qualityKey: String,
        location: String,
        readyTiming: String
    ): Result<List<SellingOpportunity>>
}

class MockRecommendationService : RecommendationService {

    override suspend fun getSellingRecommendations(
        cropId: String,
        quantityQuintals: Int,
        qualityKey: String,
        location: String,
        readyTiming: String
    ): Result<List<SellingOpportunity>> {
        // Light realistic processing delay as requested in Section 11
        delay(600)

        // If quantity is somehow invalid, return empty list to exercise empty state
        if (quantityQuintals <= 0) {
            return Result.success(emptyList())
        }

        // Base price mapping
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

        // Quality adjustment factor
        val qualityMultiplier = when (qualityKey.lowercase()) {
            "good" -> 1.00
            "average" -> 0.95
            "poor" -> 0.88
            else -> 1.00
        }

        // For the primary demo scenario: Soybean + 50q + Good + Nagpur + Ready now:
        // Quoted price: 4,850, Transport: 120, Other: 30, Net: 4,700, Total: 2,35,000
        val isPrimaryDemo = cropId.equals("soybean", ignoreCase = true) &&
                qualityKey.equals("good", ignoreCase = true)

        val abcQuoted = if (isPrimaryDemo) 4850 else (basePrice * qualityMultiplier).toInt()
        val abcTransport = 120
        val abcOther = 30
        val abcNet = abcQuoted - abcTransport - abcOther
        val abcTotal = abcNet * quantityQuintals

        val mandiQuoted = if (isPrimaryDemo) 4780 else ((basePrice - 70) * qualityMultiplier).toInt()
        val mandiTransport = 140
        val mandiOther = 50
        val mandiNet = mandiQuoted - mandiTransport - mandiOther
        val mandiTotal = mandiNet * quantityQuintals

        val xyzQuoted = if (isPrimaryDemo) 4760 else ((basePrice - 90) * qualityMultiplier).toInt()
        val xyzTransport = 130
        val xyzOther = 60
        val xyzNet = xyzQuoted - xyzTransport - xyzOther
        val xyzTotal = xyzNet * quantityQuintals

        val recommendations = listOf(
            // Primary recommendation: ABC Foods
            SellingOpportunity(
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
                    R.string.reason_affordable_transport,
                    R.string.reason_suitable_qty,
                    R.string.reason_suitable_quality,
                    R.string.reason_reliable_buyer
                ),
                quantityMatch = true,
                qualityMatch = true,
                isVerifiedBuyer = true
            ),
            // Alternative 1: Nagpur Mandi (APMC)
            SellingOpportunity(
                id = "opp_nagpur_mandi",
                buyerNameRes = R.string.buyer_nagpur_mandi,
                buyerTypeRes = R.string.buyer_type_regulated_apmc,
                cropNameRes = cropNameRes,
                quantityQuintals = quantityQuintals,
                quotedPricePerQ = mandiQuoted,
                transportExpensePerQ = mandiTransport,
                otherExpensePerQ = mandiOther,
                handlingCostPerQ = 35,
                storageCostPerQ = 0,
                transactionCostPerQ = 15,
                netRealizationPerQ = mandiNet,
                estimatedTotalAmount = mandiTotal,
                statusTextRes = R.string.status_good_option,
                recommendationStrength = "GOOD",
                isTopRecommendation = false,
                distanceKm = 24,
                paymentReliabilityRes = R.string.payment_mandi_cycle,
                reasonsRes = listOf(
                    R.string.reason_regulated_market,
                    R.string.reason_payment_history,
                    R.string.reason_quick_unloading
                ),
                quantityMatch = true,
                qualityMatch = true,
                isVerifiedBuyer = true
            ),
            // Alternative 2: XYZ Traders
            SellingOpportunity(
                id = "opp_xyz_traders",
                buyerNameRes = R.string.buyer_xyz_traders,
                buyerTypeRes = R.string.buyer_type_local_trader,
                cropNameRes = cropNameRes,
                quantityQuintals = quantityQuintals,
                quotedPricePerQ = xyzQuoted,
                transportExpensePerQ = xyzTransport,
                otherExpensePerQ = xyzOther,
                handlingCostPerQ = 40,
                storageCostPerQ = 0,
                transactionCostPerQ = 20,
                netRealizationPerQ = xyzNet,
                estimatedTotalAmount = xyzTotal,
                statusTextRes = R.string.status_good_option,
                recommendationStrength = "GOOD",
                isTopRecommendation = false,
                distanceKm = 22,
                paymentReliabilityRes = R.string.payment_on_weighment,
                reasonsRes = listOf(
                    R.string.reason_affordable_transport,
                    R.string.reason_quick_unloading,
                    R.string.reason_suitable_qty
                ),
                quantityMatch = true,
                qualityMatch = true,
                isVerifiedBuyer = true
            )
        )

        return Result.success(recommendations)
    }
}
