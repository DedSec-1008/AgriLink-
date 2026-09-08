package com.example

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.data.MockAgriRepository
import com.example.model.CropOption
import com.example.model.DestinationType
import com.example.model.LotStatus
import com.example.model.SellingOpportunity
import com.example.ui.screens.HarvestReadiness
import com.example.ui.screens.LotCreationStep
import com.example.ui.screens.MyLotsScreen
import com.example.ui.screens.SellScreen
import com.example.ui.screens.SellingStep
import com.example.ui.screens.SellingViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h891dp")
class Step7FarmerLotCreationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val repository = MockAgriRepository()

    private fun setupViewModelWithRecommendation(): SellingViewModel {
        val viewModel = SellingViewModel(repository)
        viewModel.selectCrop(CropOption("soybean", R.string.crop_soybean, "🌱", 4850))
        viewModel.updateQuantity(50)
        viewModel.selectQuality("good", R.string.produce_quality_good)
        viewModel.selectLocation("Nagpur, Maharashtra", R.string.loc_nagpur)
        viewModel.selectHarvestReadiness(HarvestReadiness.READY_NOW)

        val opp = SellingOpportunity(
            id = "opp_rec_1",
            buyerNameRes = R.string.buyer_abc_foods,
            buyerTypeRes = R.string.buyer_type_verified_direct,
            cropNameRes = R.string.crop_soybean,
            quantityQuintals = 50,
            quotedPricePerQ = 4850,
            transportExpensePerQ = 150,
            otherExpensePerQ = 0,
            netRealizationPerQ = 4700,
            estimatedTotalAmount = 235000,
            statusTextRes = R.string.status_strong_rec,
            isTopRecommendation = true,
            distanceKm = 12,
            destinationType = DestinationType.BUYER
        )
        viewModel.selectOpportunityToSell(opp)
        viewModel.goToStep(SellingStep.CONFIRM_SELL)
        return viewModel
    }

    @Test
    fun testLotCreationStepTransitionsAndPhotoHandling() {
        runBlocking {
            val viewModel = setupViewModelWithRecommendation()

            // 1. Starts at Step 1: PRODUCE_SUMMARY
            assertEquals(LotCreationStep.PRODUCE_SUMMARY, viewModel.uiState.value.lotCreationStep)

            // Step 1 -> Step 2
            viewModel.nextLotCreationStep()
            assertEquals(LotCreationStep.CHECK_DETAILS, viewModel.uiState.value.lotCreationStep)

            // Step 2 -> Step 3 (ADD_PHOTOS)
            viewModel.nextLotCreationStep()
            assertEquals(LotCreationStep.ADD_PHOTOS, viewModel.uiState.value.lotCreationStep)

            // Add photo
            val sampleUri = "content://media/photos/sample_1.jpg"
            viewModel.addPhoto(sampleUri)
            assertEquals(1, viewModel.uiState.value.lotPhotos.size)
            assertEquals(sampleUri, viewModel.uiState.value.lotPhotos.first())

            // Add another photo
            val sampleUri2 = "content://media/photos/sample_2.jpg"
            viewModel.addPhoto(sampleUri2)
            assertEquals(2, viewModel.uiState.value.lotPhotos.size)

            // Remove photo
            viewModel.removePhoto(sampleUri)
            assertEquals(1, viewModel.uiState.value.lotPhotos.size)
            assertEquals(sampleUri2, viewModel.uiState.value.lotPhotos.first())

            // Step 3 -> Step 4 (READY_TO_PUBLISH)
            viewModel.nextLotCreationStep()
            assertEquals(LotCreationStep.READY_TO_PUBLISH, viewModel.uiState.value.lotCreationStep)

            // Publish Lot
            viewModel.publishLot()

            // Verify result
            val createdLot = viewModel.uiState.value.createdLot
            assertNotNull("Lot must be created", createdLot)
            assertTrue("Lot ID should match format LOT-AGL-2026-", createdLot!!.lotId.startsWith("LOT-AGL-2026-"))
            assertEquals(LotStatus.PUBLISHED, createdLot.lotStatus)
            assertEquals(SellingStep.LOT_CREATED, viewModel.uiState.value.currentStep)
            assertEquals(1, createdLot.photos.size)
            assertEquals(4850, createdLot.expectedPricePerQ)
            assertEquals(4700, createdLot.estimatedNetPerQ)
            assertEquals(235000, createdLot.estimatedTotalAmount)
        }
    }

    @Test
    fun testUIFlowGuidedFourStepsToPublish() {
        val viewModel = setupViewModelWithRecommendation()
        var viewLotCalled = false
        var viewOffersCalled = false

        composeTestRule.setContent {
            SellScreen(
                repository = repository,
                viewModel = viewModel,
                onNavigateToMyLots = {},
                onNavigateToHome = {},
                onViewLotDetails = { viewLotCalled = true },
                onViewOffers = { viewOffersCalled = true }
            )
        }

        // Step 1: Produce Summary
        composeTestRule.onNodeWithTag("btn_lot_step1_continue").assertIsDisplayed()
        composeTestRule.onNodeWithTag("lot_btn_change_details").assertIsDisplayed()
        composeTestRule.onNodeWithTag("btn_lot_step1_continue").performClick()

        // Step 2: Check Details
        composeTestRule.onNodeWithTag("btn_lot_step2_continue").assertIsDisplayed()
        composeTestRule.onNodeWithTag("btn_lot_step2_continue").performClick()

        // Step 3: Photos Screen
        composeTestRule.onNodeWithTag("btn_skip_photos").assertIsDisplayed()
        composeTestRule.onNodeWithTag("btn_add_photo").assertIsDisplayed()
        composeTestRule.onNodeWithTag("btn_skip_photos").performClick()

        // Step 4: Ready to Publish
        composeTestRule.onNodeWithTag("btn_publish_lot").assertIsDisplayed()
        composeTestRule.onNodeWithTag("btn_publish_lot").performClick()

        // Step 10: Lot Published Screen
        composeTestRule.onNodeWithTag("badge_lot_published").assertIsDisplayed()
        composeTestRule.onNodeWithTag("view_my_lot_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("see_offers_lot_button").assertIsDisplayed()

        // Test Callbacks
        composeTestRule.onNodeWithTag("view_my_lot_button").performClick()
        assertTrue("View lot callback should be invoked", viewLotCalled)

        composeTestRule.onNodeWithTag("see_offers_lot_button").performClick()
        assertTrue("View offers callback should be invoked", viewOffersCalled)
    }

    @Test
    fun testPublishedLotInRepositoryAndExclusionFromOffers() {
        runBlocking {
            val lot = repository.publishLot(
                cropRes = R.string.crop_soybean,
                emoji = "🌱",
                quantity = 50,
                qualityRes = R.string.produce_quality_good,
                buyerNameRes = R.string.buyer_abc_foods,
                location = "Nagpur, Maharashtra",
                readyTiming = "Ready now",
                expectedPricePerQ = 4850,
                estimatedNetPerQ = 4700,
                photos = listOf("photo1.jpg")
            )

            assertNotNull(lot)
            assertEquals(LotStatus.PUBLISHED, lot.lotStatus)

            // Check lots list from repository contains the new lot
            val lots = repository.getMyLots().first()
            assertTrue("Repository must contain the published lot", lots.any { it.lotId == lot.lotId })

            // Check that offers for published lot are empty initially
            val offers = repository.getOffersForLot(lot.lotId).first()
            assertTrue("Published lots should start with 0 offers waiting for real buyers", offers.isEmpty())
        }
    }

    @Test
    fun testPublishedLotInMyLotsScreen() {
        runBlocking {
            val lot = repository.publishLot(
                cropRes = R.string.crop_soybean,
                emoji = "🌱",
                quantity = 50,
                qualityRes = R.string.produce_quality_good,
                buyerNameRes = R.string.buyer_abc_foods,
                location = "Nagpur, Maharashtra",
                readyTiming = "Ready now",
                expectedPricePerQ = 4850,
                estimatedNetPerQ = 4700
            )

            composeTestRule.setContent {
                MyLotsScreen(
                    repository = repository,
                    onNavigateToSell = {},
                    snackbarHostState = remember { SnackbarHostState() }
                )
            }

            // Verify the lot card is displayed in MyLotsScreen
            composeTestRule.onNodeWithTag("lot_card_${lot.lotId}").assertIsDisplayed()
            composeTestRule.onNodeWithTag("lot_status_tag_${lot.lotId}").assertIsDisplayed()
        }
    }
}
