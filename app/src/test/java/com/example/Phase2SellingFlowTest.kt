package com.example

import com.example.data.MockAgriRepository
import com.example.data.MockRecommendationService
import com.example.model.CropOption
import com.example.ui.screens.SellingStep
import com.example.ui.screens.SellingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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
class Phase2SellingFlowTest {

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
    fun testRecommendationServiceCalculations() = runTest {
        val service = MockRecommendationService()
        val result = service.getSellingRecommendations(
            cropId = "soybean",
            quantityQuintals = 50,
            qualityKey = "good",
            location = "Nagpur, Maharashtra",
            readyTiming = "Ready now"
        )

        assertTrue(result.isSuccess)
        val recs = result.getOrThrow()
        assertTrue(recs.isNotEmpty())

        val topOption = recs.first { it.isTopRecommendation }
        assertEquals(R.string.buyer_abc_foods, topOption.buyerNameRes)
        assertEquals(4850, topOption.quotedPricePerQ)
        assertEquals(4700, topOption.netRealizationPerQ)
        assertEquals(235000, topOption.estimatedTotalAmount)
        assertTrue(topOption.isVerifiedBuyer)
    }

    @Test
    fun testCompleteSellingJourneyAndLotCreation() = runTest {
        val repository = MockAgriRepository()
        val viewModel = SellingViewModel(repository)

        // Step 1: Crop
        assertEquals(SellingStep.CROP, viewModel.uiState.value.currentStep)
        val soybean = CropOption("soybean", R.string.crop_soybean, "🌱", 4850)
        viewModel.selectCrop(soybean)
        assertEquals(SellingStep.QUANTITY, viewModel.uiState.value.currentStep)

        // Step 2: Quantity
        viewModel.updateQuantity(50)
        assertEquals(50, viewModel.uiState.value.quantityQuintals)
        viewModel.goToStep(SellingStep.QUALITY)
        assertEquals(SellingStep.QUALITY, viewModel.uiState.value.currentStep)

        // Step 3: Quality
        viewModel.selectQuality("good", R.string.produce_quality_good)
        viewModel.goToStep(SellingStep.LOCATION)
        assertEquals(SellingStep.LOCATION, viewModel.uiState.value.currentStep)

        // Step 4: Location
        viewModel.selectLocation("Nagpur, Maharashtra", R.string.loc_nagpur)
        viewModel.goToStep(SellingStep.READY_DATE)
        assertEquals(SellingStep.READY_DATE, viewModel.uiState.value.currentStep)

        // Step 5: Ready Date
        viewModel.selectReadyTiming("Ready now", R.string.timing_ready_now)
        viewModel.goToStep(SellingStep.REVIEW)
        assertEquals(SellingStep.REVIEW, viewModel.uiState.value.currentStep)

        // Review state verified
        assertEquals("soybean", viewModel.uiState.value.selectedCrop.id)
        assertEquals(50, viewModel.uiState.value.quantityQuintals)
        assertEquals("good", viewModel.uiState.value.qualityKey)
        assertEquals("Nagpur, Maharashtra", viewModel.uiState.value.location)
        assertEquals("Ready now", viewModel.uiState.value.readyTiming)

        // Step 6 & 7: Start Analysis & Get Recommendations
        viewModel.startAnalysisAndFindOptions()
        advanceUntilIdle()

        assertEquals(SellingStep.RECOMMENDATIONS, viewModel.uiState.value.currentStep)
        val recommendations = viewModel.uiState.value.recommendations
        assertTrue(recommendations.isNotEmpty())

        val topOption = recommendations.first { it.isTopRecommendation }
        assertEquals(R.string.buyer_abc_foods, topOption.buyerNameRes)
        assertEquals(4850, topOption.quotedPricePerQ)
        assertEquals(4700, topOption.netRealizationPerQ)
        assertEquals(235000, topOption.estimatedTotalAmount)

        // Step 8: Select Top Pick to Sell
        viewModel.selectOpportunityToSell(topOption)
        assertEquals(SellingStep.CONFIRM_SELL, viewModel.uiState.value.currentStep)

        // Step 9: Confirm & Create Lot
        viewModel.confirmCreateLot()
        advanceUntilIdle()

        assertEquals(SellingStep.LOT_CREATED, viewModel.uiState.value.currentStep)
        val createdLot = viewModel.uiState.value.createdLot
        assertNotNull(createdLot)
        assertEquals(R.string.crop_soybean, createdLot!!.cropNameRes)
        assertEquals(50, createdLot.quantityQuintals)
        assertEquals(R.string.buyer_abc_foods, createdLot.buyerNameRes)

        // Verify lot appears in Repository's getMyLots Flow
        val allLots = repository.getMyLots().first()
        assertTrue(allLots.any { it.lotId == createdLot.lotId && it.buyerNameRes == R.string.buyer_abc_foods })
        assertEquals("Lot #AG-1024", createdLot.lotId)
        assertEquals(R.string.lot_status_waiting, createdLot.statusRes)

        // Test Duplicate Lot Protection (Section 18)
        val countBefore = allLots.size
        viewModel.confirmCreateLot()
        advanceUntilIdle()
        val countAfter = repository.getMyLots().first().size
        assertEquals(countBefore, countAfter)
    }

    @Test
    fun testBackNavigationPreservesState() {
        val repository = MockAgriRepository()
        val viewModel = SellingViewModel(repository)

        viewModel.selectCrop(CropOption("wheat", R.string.crop_wheat, "🌾", 2275))
        assertEquals(SellingStep.QUANTITY, viewModel.uiState.value.currentStep)

        viewModel.updateQuantity(85)
        viewModel.goToStep(SellingStep.QUALITY)
        assertEquals(SellingStep.QUALITY, viewModel.uiState.value.currentStep)

        // Press Back to Quantity
        val backSuccess = viewModel.goBack()
        assertTrue(backSuccess)
        assertEquals(SellingStep.QUANTITY, viewModel.uiState.value.currentStep)
        // Verify state was preserved!
        assertEquals(85, viewModel.uiState.value.quantityQuintals)
        assertEquals("wheat", viewModel.uiState.value.selectedCrop.id)
    }
}
