package com.example.data

import com.example.domain.RecommendationEngine
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

class MockRecommendationService(
    private val engine: RecommendationEngine = RecommendationEngine()
) : RecommendationService {

    override suspend fun getSellingRecommendations(
        cropId: String,
        quantityQuintals: Int,
        qualityKey: String,
        location: String,
        readyTiming: String
    ): Result<List<SellingOpportunity>> {
        // Light realistic processing delay as requested in Section 2
        delay(350)
        return engine.calculateRecommendations(
            cropId = cropId,
            quantityQuintals = quantityQuintals,
            qualityKey = qualityKey,
            location = location,
            readyTiming = readyTiming
        )
    }
}

