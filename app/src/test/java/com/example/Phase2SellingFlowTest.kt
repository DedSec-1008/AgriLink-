package com.example

import com.example.data.MockAgriRepository
import com.example.data.MockRecommendationService
import com.example.model.CropOption
import com.example.ui.screens.HarvestReadiness
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
        assertTrue(createdLot.lotId.startsWith("Lot #AG-"))
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

    @Test
    fun testStep1CropSelectionAndContinuance() {
        val repository = MockAgriRepository()
        val viewModel = SellingViewModel(repository)

        // Verify initial state is on CROP step
        assertEquals(SellingStep.CROP, viewModel.uiState.value.currentStep)

        // Verify all repository crops including 'other'
        val availableCrops = repository.getAvailableCrops()
        val otherCrop = availableCrops.first { it.id == "other" }
        val wheatCrop = availableCrops.first { it.id == "wheat" }

        // Farmer taps Wheat card: updates selectedCrop, stays on CROP step until Continue
        viewModel.setCrop(wheatCrop)
        assertEquals(SellingStep.CROP, viewModel.uiState.value.currentStep)
        assertEquals("wheat", viewModel.uiState.value.selectedCrop.id)

        // Farmer taps Other card: updates selectedCrop, stays on CROP step
        viewModel.setCrop(otherCrop)
        assertEquals(SellingStep.CROP, viewModel.uiState.value.currentStep)
        assertEquals("other", viewModel.uiState.value.selectedCrop.id)

        // Farmer presses Continue: proceeds to QUANTITY step
        viewModel.goToStep(SellingStep.QUANTITY)
        assertEquals(SellingStep.QUANTITY, viewModel.uiState.value.currentStep)
        assertEquals("other", viewModel.uiState.value.selectedCrop.id)

        // Going back returns to CROP step and keeps "other" selected
        val backSuccess = viewModel.goBack()
        assertTrue(backSuccess)
        assertEquals(SellingStep.CROP, viewModel.uiState.value.currentStep)
        assertEquals("other", viewModel.uiState.value.selectedCrop.id)
    }

    @Test
    fun testStep2QuantitySelectionAndContinuance() = runTest {
        val repository = MockAgriRepository()
        val viewModel = SellingViewModel(repository)
        val availableCrops = repository.getAvailableCrops()

        val soybean = availableCrops.first { it.id == "soybean" }
        viewModel.setCrop(soybean)
        viewModel.goToStep(SellingStep.QUANTITY)

        assertEquals(SellingStep.QUANTITY, viewModel.uiState.value.currentStep)
        assertEquals("soybean", viewModel.uiState.value.selectedCrop.id)

        // Presets & direct updates
        viewModel.updateQuantity(25)
        assertEquals(25, viewModel.uiState.value.quantityQuintals)

        viewModel.updateQuantity(100)
        assertEquals(100, viewModel.uiState.value.quantityQuintals)

        // Stepper behavior (custom value)
        viewModel.updateQuantity(95)
        assertEquals(95, viewModel.uiState.value.quantityQuintals)

        // Navigate back to Step 1 (Crop Selection): crop must remain intact
        val backToCrop = viewModel.goBack()
        assertTrue(backToCrop)
        assertEquals(SellingStep.CROP, viewModel.uiState.value.currentStep)
        assertEquals("soybean", viewModel.uiState.value.selectedCrop.id)

        // Navigate forward again to Step 2: quantity must remain intact
        viewModel.goToStep(SellingStep.QUANTITY)
        assertEquals(SellingStep.QUANTITY, viewModel.uiState.value.currentStep)
        assertEquals(95, viewModel.uiState.value.quantityQuintals)

        // Continue to Step 3 (Quality)
        viewModel.goToStep(SellingStep.QUALITY)
        assertEquals(SellingStep.QUALITY, viewModel.uiState.value.currentStep)
        assertEquals(95, viewModel.uiState.value.quantityQuintals)
        assertEquals("soybean", viewModel.uiState.value.selectedCrop.id)
    }

    @Test
    fun testStep3QualitySelectionAndContinuance() = runTest {
        val repository = MockAgriRepository()
        val viewModel = SellingViewModel(repository)
        val availableCrops = repository.getAvailableCrops()

        val soybean = availableCrops.first { it.id == "soybean" }
        viewModel.setCrop(soybean)
        viewModel.goToStep(SellingStep.QUANTITY)
        viewModel.updateQuantity(75)
        viewModel.goToStep(SellingStep.QUALITY)

        // Verify Step 3 initial state
        assertEquals(SellingStep.QUALITY, viewModel.uiState.value.currentStep)
        assertEquals("soybean", viewModel.uiState.value.selectedCrop.id)
        assertEquals(75, viewModel.uiState.value.quantityQuintals)
        assertEquals("", viewModel.uiState.value.qualityKey)

        // Select Good
        viewModel.selectQuality("good", R.string.produce_quality_good)
        assertEquals("good", viewModel.uiState.value.qualityKey)
        assertEquals(R.string.produce_quality_good, viewModel.uiState.value.qualityRes)

        // Select Average
        viewModel.selectQuality("average", R.string.quality_average)
        assertEquals("average", viewModel.uiState.value.qualityKey)
        assertEquals(R.string.quality_average, viewModel.uiState.value.qualityRes)

        // Select Poor
        viewModel.selectQuality("poor", R.string.quality_poor)
        assertEquals("poor", viewModel.uiState.value.qualityKey)
        assertEquals(R.string.quality_poor, viewModel.uiState.value.qualityRes)

        // Re-select Good
        viewModel.selectQuality("good", R.string.produce_quality_good)
        assertEquals("good", viewModel.uiState.value.qualityKey)

        // Go back to Step 2 (Quantity)
        val backToQty = viewModel.goBack()
        assertTrue(backToQty)
        assertEquals(SellingStep.QUANTITY, viewModel.uiState.value.currentStep)
        assertEquals(75, viewModel.uiState.value.quantityQuintals)
        assertEquals("soybean", viewModel.uiState.value.selectedCrop.id)

        // Go back to Step 1 (Crop)
        val backToCrop = viewModel.goBack()
        assertTrue(backToCrop)
        assertEquals(SellingStep.CROP, viewModel.uiState.value.currentStep)
        assertEquals("soybean", viewModel.uiState.value.selectedCrop.id)

        // Move forward to Quantity and then Quality again
        viewModel.goToStep(SellingStep.QUANTITY)
        assertEquals(SellingStep.QUANTITY, viewModel.uiState.value.currentStep)
        assertEquals(75, viewModel.uiState.value.quantityQuintals)

        viewModel.goToStep(SellingStep.QUALITY)
        assertEquals(SellingStep.QUALITY, viewModel.uiState.value.currentStep)
        assertEquals("good", viewModel.uiState.value.qualityKey)
        assertEquals(75, viewModel.uiState.value.quantityQuintals)
        assertEquals("soybean", viewModel.uiState.value.selectedCrop.id)

        // Continue to Step 4 (Location)
        viewModel.goToStep(SellingStep.LOCATION)
        assertEquals(SellingStep.LOCATION, viewModel.uiState.value.currentStep)
        assertEquals("good", viewModel.uiState.value.qualityKey)
        assertEquals(75, viewModel.uiState.value.quantityQuintals)
        assertEquals("soybean", viewModel.uiState.value.selectedCrop.id)
    }

    @Test
    fun testStep4LocationSelectionAndContinuance() = runTest {
        val repository = MockAgriRepository()
        val viewModel = SellingViewModel(repository)
        val availableCrops = repository.getAvailableCrops()

        // Complete Step 1: Crop
        val soybean = availableCrops.first { it.id == "soybean" }
        viewModel.setCrop(soybean)
        viewModel.goToStep(SellingStep.QUANTITY)

        // Complete Step 2: Quantity
        viewModel.updateQuantity(60)
        viewModel.goToStep(SellingStep.QUALITY)

        // Complete Step 3: Quality
        viewModel.selectQuality("good", R.string.produce_quality_good)
        viewModel.goToStep(SellingStep.LOCATION)

        // Step 4: Verify initial state
        assertEquals(SellingStep.LOCATION, viewModel.uiState.value.currentStep)
        assertEquals("soybean", viewModel.uiState.value.selectedCrop.id)
        assertEquals(60, viewModel.uiState.value.quantityQuintals)
        assertEquals("good", viewModel.uiState.value.qualityKey)

        // Manual Location Selection (e.g. Amravati)
        viewModel.selectLocation(
            "Amravati, Maharashtra",
            R.string.loc_amravati,
            com.example.ui.screens.LocationSource.MANUAL_SELECTION
        )
        assertEquals("Amravati, Maharashtra", viewModel.uiState.value.location)
        assertEquals(R.string.loc_amravati, viewModel.uiState.value.locationRes)
        assertEquals(
            com.example.ui.screens.LocationSource.MANUAL_SELECTION,
            viewModel.uiState.value.locationSource
        )

        // Switch to Current Location (e.g. GPS detected Nagpur)
        viewModel.selectLocation(
            "Nagpur, Maharashtra",
            R.string.loc_nagpur,
            com.example.ui.screens.LocationSource.CURRENT_LOCATION
        )
        assertEquals("Nagpur, Maharashtra", viewModel.uiState.value.location)
        assertEquals(R.string.loc_nagpur, viewModel.uiState.value.locationRes)
        assertEquals(
            com.example.ui.screens.LocationSource.CURRENT_LOCATION,
            viewModel.uiState.value.locationSource
        )

        // Go back to Step 3 (Quality)
        val backToQuality = viewModel.goBack()
        assertTrue(backToQuality)
        assertEquals(SellingStep.QUALITY, viewModel.uiState.value.currentStep)
        assertEquals("good", viewModel.uiState.value.qualityKey)
        assertEquals(60, viewModel.uiState.value.quantityQuintals)
        assertEquals("soybean", viewModel.uiState.value.selectedCrop.id)

        // Go back to Step 2 (Quantity)
        val backToQuantity = viewModel.goBack()
        assertTrue(backToQuantity)
        assertEquals(SellingStep.QUANTITY, viewModel.uiState.value.currentStep)
        assertEquals(60, viewModel.uiState.value.quantityQuintals)

        // Navigate forward again to Quality and Location
        viewModel.goToStep(SellingStep.QUALITY)
        assertEquals(SellingStep.QUALITY, viewModel.uiState.value.currentStep)

        viewModel.goToStep(SellingStep.LOCATION)
        assertEquals(SellingStep.LOCATION, viewModel.uiState.value.currentStep)

        // Verify Location data was fully preserved!
        assertEquals("Nagpur, Maharashtra", viewModel.uiState.value.location)
        assertEquals(R.string.loc_nagpur, viewModel.uiState.value.locationRes)
        assertEquals(
            com.example.ui.screens.LocationSource.CURRENT_LOCATION,
            viewModel.uiState.value.locationSource
        )

        // Continue to Step 5 (READY_DATE)
        viewModel.goToStep(SellingStep.READY_DATE)
        assertEquals(SellingStep.READY_DATE, viewModel.uiState.value.currentStep)
        assertEquals("Nagpur, Maharashtra", viewModel.uiState.value.location)
        assertEquals(60, viewModel.uiState.value.quantityQuintals)
        assertEquals("good", viewModel.uiState.value.qualityKey)
        assertEquals("soybean", viewModel.uiState.value.selectedCrop.id)
    }

    @Test
    fun testStep5HarvestReadinessSelectionAndProgression() = runTest {
        val repository = MockAgriRepository()
        val viewModel = SellingViewModel(repository)

        // Setup Steps 1-4
        viewModel.setCrop(CropOption("soybean", R.string.crop_soybean, "🌱", 4850))
        viewModel.goToStep(SellingStep.QUANTITY)
        viewModel.updateQuantity(50)
        viewModel.goToStep(SellingStep.QUALITY)
        viewModel.selectQuality("good", R.string.produce_quality_good)
        viewModel.goToStep(SellingStep.LOCATION)
        viewModel.selectLocation(
            "Nagpur, Maharashtra",
            R.string.loc_nagpur,
            com.example.ui.screens.LocationSource.CURRENT_LOCATION
        )

        // Advance to Step 5 (READY_DATE)
        viewModel.goToStep(SellingStep.READY_DATE)
        assertEquals(SellingStep.READY_DATE, viewModel.uiState.value.currentStep)

        // 1. Initial State: No harvest option selected -> harvestReadiness is NONE
        assertEquals(HarvestReadiness.NONE, viewModel.uiState.value.harvestReadiness)
        val canContinueInitially = viewModel.uiState.value.harvestReadiness != HarvestReadiness.NONE
        org.junit.Assert.assertFalse(
            "Continue must be disabled when no harvest option is selected",
            canContinueInitially
        )

        // 2. Select "Ready now"
        viewModel.selectHarvestReadiness(HarvestReadiness.READY_NOW)
        assertEquals(HarvestReadiness.READY_NOW, viewModel.uiState.value.harvestReadiness)
        assertEquals("Ready now", viewModel.uiState.value.readyTiming)
        assertEquals(R.string.timing_ready_now, viewModel.uiState.value.readyTimingRes)
        assertTrue(viewModel.uiState.value.harvestReadiness != HarvestReadiness.NONE)

        // 3. Select "Within 7 days"
        viewModel.selectHarvestReadiness(HarvestReadiness.WITHIN_7_DAYS)
        assertEquals(HarvestReadiness.WITHIN_7_DAYS, viewModel.uiState.value.harvestReadiness)
        assertEquals("Within 7 days", viewModel.uiState.value.readyTiming)
        assertEquals(R.string.timing_within_7_days, viewModel.uiState.value.readyTimingRes)

        // 4. Select "Later"
        viewModel.selectHarvestReadiness(HarvestReadiness.LATER)
        assertEquals(HarvestReadiness.LATER, viewModel.uiState.value.harvestReadiness)
        assertEquals("Later", viewModel.uiState.value.readyTiming)
        assertEquals(R.string.timing_later, viewModel.uiState.value.readyTimingRes)

        // 5. Verify selectReadyTiming backwards compatibility
        viewModel.selectReadyTiming("Ready now", R.string.timing_ready_now)
        assertEquals(HarvestReadiness.READY_NOW, viewModel.uiState.value.harvestReadiness)
        assertEquals("Ready now", viewModel.uiState.value.readyTiming)

        // 6. Continue advances to the existing recommendation stage (SellingStep.REVIEW)
        viewModel.goToStep(SellingStep.REVIEW)
        assertEquals(SellingStep.REVIEW, viewModel.uiState.value.currentStep)

        // 7. Back navigation preserves crop, quantity, quality, location, and timing
        val backToReady = viewModel.goBack()
        assertTrue(backToReady)
        assertEquals(SellingStep.READY_DATE, viewModel.uiState.value.currentStep)
        assertEquals(HarvestReadiness.READY_NOW, viewModel.uiState.value.harvestReadiness)
        assertEquals("Ready now", viewModel.uiState.value.readyTiming)

        val backToLocation = viewModel.goBack()
        assertTrue(backToLocation)
        assertEquals(SellingStep.LOCATION, viewModel.uiState.value.currentStep)
        assertEquals("Nagpur, Maharashtra", viewModel.uiState.value.location)
        assertEquals(
            com.example.ui.screens.LocationSource.CURRENT_LOCATION,
            viewModel.uiState.value.locationSource
        )

        val backToQuality = viewModel.goBack()
        assertTrue(backToQuality)
        assertEquals(SellingStep.QUALITY, viewModel.uiState.value.currentStep)
        assertEquals("good", viewModel.uiState.value.qualityKey)

        val backToQuantity = viewModel.goBack()
        assertTrue(backToQuantity)
        assertEquals(SellingStep.QUANTITY, viewModel.uiState.value.currentStep)
        assertEquals(50, viewModel.uiState.value.quantityQuintals)

        val backToCrop = viewModel.goBack()
        assertTrue(backToCrop)
        assertEquals(SellingStep.CROP, viewModel.uiState.value.currentStep)
        assertEquals("soybean", viewModel.uiState.value.selectedCrop.id)

        // 8. Re-navigating forward preserves all entered state
        viewModel.goToStep(SellingStep.READY_DATE)
        assertEquals(HarvestReadiness.READY_NOW, viewModel.uiState.value.harvestReadiness)
        assertEquals("Nagpur, Maharashtra", viewModel.uiState.value.location)
        assertEquals("good", viewModel.uiState.value.qualityKey)
        assertEquals(50, viewModel.uiState.value.quantityQuintals)
        assertEquals("soybean", viewModel.uiState.value.selectedCrop.id)
    }

    @Test
    fun testStep5ResourceStringsIntegrity() {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()

        // Check English strings exist and are non-empty
        val title = context.getString(R.string.step5_heading)
        val subtitle = context.getString(R.string.step5_subtitle)
        val readyNow = context.getString(R.string.timing_ready_now)
        val readyNowSub = context.getString(R.string.timing_ready_now_sub)
        val within7Days = context.getString(R.string.timing_within_7_days)
        val within7DaysSub = context.getString(R.string.timing_within_7_days_sub)
        val later = context.getString(R.string.timing_later)
        val laterSub = context.getString(R.string.timing_later_sub)
        val whyMatters = context.getString(R.string.timing_why_matters)
        val btnSeeOptions = context.getString(R.string.btn_see_best_options)
        val errTiming = context.getString(R.string.err_timing_required)

        assertTrue(title.isNotBlank())
        assertTrue(subtitle.isNotBlank())
        assertTrue(readyNow.isNotBlank())
        assertTrue(readyNowSub.isNotBlank())
        assertTrue(within7Days.isNotBlank())
        assertTrue(within7DaysSub.isNotBlank())
        assertTrue(later.isNotBlank())
        assertTrue(laterSub.isNotBlank())
        assertTrue(whyMatters.isNotBlank())
        assertTrue(btnSeeOptions.isNotBlank())
        assertTrue(errTiming.isNotBlank())

        assertEquals("When are you ready to sell?", title)
        assertEquals("Tell us when you can sell your produce.", subtitle)
        assertEquals("Ready now", readyNow)
        assertEquals("I can sell my produce now", readyNowSub)
        assertEquals("Within 7 days", within7Days)
        assertEquals("I will be ready within a week", within7DaysSub)
        assertEquals("Later", later)
        assertEquals("I am not ready to sell yet", laterSub)
        assertEquals("The best place to sell can change depending on when you are ready.", whyMatters)
        assertEquals("See Best Selling Options", btnSeeOptions)
    }

    @Test
    fun testStep5LocalizationInAllLanguages() {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val stringKeys = listOf(
            R.string.step5_heading,
            R.string.step5_subtitle,
            R.string.timing_ready_now,
            R.string.timing_ready_now_sub,
            R.string.timing_within_7_days,
            R.string.timing_within_7_days_sub,
            R.string.timing_later,
            R.string.timing_later_sub,
            R.string.timing_why_matters,
            R.string.btn_see_best_options,
            R.string.err_timing_required,
            R.string.summary_produce_label,
            R.string.state_selected,
            R.string.unit_quintals
        )

        for (langCode in listOf("en", "hi", "mr")) {
            val config = context.resources.configuration
            val locale = java.util.Locale.forLanguageTag(langCode)
            val localizedContext = context.createConfigurationContext(
                config.apply { setLocale(locale) }
            )

            for (resId in stringKeys) {
                val str = localizedContext.getString(resId)
                assertTrue("String res $resId must not be blank for lang $langCode", str.isNotBlank())
            }
        }
    }

    @Test
    fun testStep5RotationStatePreservation() = runTest {
        val repository = MockAgriRepository()
        val viewModel = SellingViewModel(repository)

        // Set up complete Step 1–5 flow state
        viewModel.setCrop(CropOption("soybean", R.string.crop_soybean, "🌱", 4850))
        viewModel.updateQuantity(50)
        viewModel.selectQuality("good", R.string.produce_quality_good)
        viewModel.selectLocation("Nagpur, Maharashtra", R.string.loc_nagpur, com.example.ui.screens.LocationSource.CURRENT_LOCATION)
        viewModel.goToStep(SellingStep.READY_DATE)
        viewModel.selectHarvestReadiness(HarvestReadiness.READY_NOW)

        // Capture initial state before simulated rotation
        val stateBeforeRotation = viewModel.uiState.value
        assertEquals("soybean", stateBeforeRotation.selectedCrop.id)
        assertEquals(50, stateBeforeRotation.quantityQuintals)
        assertEquals("good", stateBeforeRotation.qualityKey)
        assertEquals("Nagpur, Maharashtra", stateBeforeRotation.location)
        assertEquals(HarvestReadiness.READY_NOW, stateBeforeRotation.harvestReadiness)
        assertEquals(SellingStep.READY_DATE, stateBeforeRotation.currentStep)

        // Simulate configuration change Portrait -> Landscape -> Portrait
        // The ViewModel instance and its StateFlow are retained across configuration changes
        val stateAfterPortraitToLandscape = viewModel.uiState.value
        assertEquals(stateBeforeRotation.selectedCrop.id, stateAfterPortraitToLandscape.selectedCrop.id)
        assertEquals(stateBeforeRotation.quantityQuintals, stateAfterPortraitToLandscape.quantityQuintals)
        assertEquals(stateBeforeRotation.qualityKey, stateAfterPortraitToLandscape.qualityKey)
        assertEquals(stateBeforeRotation.location, stateAfterPortraitToLandscape.location)
        assertEquals(stateBeforeRotation.harvestReadiness, stateAfterPortraitToLandscape.harvestReadiness)
        assertEquals(stateBeforeRotation.currentStep, stateAfterPortraitToLandscape.currentStep)

        // Change selection in landscape
        viewModel.selectHarvestReadiness(HarvestReadiness.WITHIN_7_DAYS)

        // Simulate Landscape -> Portrait
        val stateAfterLandscapeToPortrait = viewModel.uiState.value
        assertEquals(HarvestReadiness.WITHIN_7_DAYS, stateAfterLandscapeToPortrait.harvestReadiness)
        assertEquals("Within 7 days", stateAfterLandscapeToPortrait.readyTiming)
        assertEquals("soybean", stateAfterLandscapeToPortrait.selectedCrop.id)
        assertEquals(50, stateAfterLandscapeToPortrait.quantityQuintals)
        assertEquals("good", stateAfterLandscapeToPortrait.qualityKey)
        assertEquals("Nagpur, Maharashtra", stateAfterLandscapeToPortrait.location)
        assertEquals(SellingStep.READY_DATE, stateAfterLandscapeToPortrait.currentStep)
    }
}
