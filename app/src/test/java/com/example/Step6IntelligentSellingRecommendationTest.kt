package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.data.MockAgriRepository
import com.example.domain.RecommendationEngine
import com.example.model.CropOption
import com.example.model.DestinationType
import com.example.model.RecommendationCalculationState
import com.example.ui.screens.HarvestReadiness
import com.example.ui.screens.LocationSource
import com.example.ui.screens.SellScreen
import com.example.ui.screens.SellingStep
import com.example.ui.screens.SellingViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h891dp")
class Step6IntelligentSellingRecommendationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val repository = MockAgriRepository()

    @Test
    fun testRecommendationEngineCalculations() {
        val cropId = "soybean"
        val quantity = 50
        val location = "Nagpur, Maharashtra"
        val quality = "good"
        val timing = "ready_now"

        val engine = RecommendationEngine()
        val result = engine.calculateRecommendations(cropId, quantity, quality, location, timing)
        assertTrue("Engine must return successful result", result.isSuccess)
        val opportunities = result.getOrThrow()

        // Must produce 3-5 ranked opportunities
        assertTrue("Must return at least 3 opportunities", opportunities.size >= 3)

        // Verify net realizations are ranked descending
        for (i in 0 until opportunities.size - 1) {
            assertTrue(
                "Opportunities must be sorted by net realization descending",
                opportunities[i].netRealizationPerQ >= opportunities[i + 1].netRealizationPerQ
            )
        }

        // Top recommendation must be marked
        assertTrue("First item must be marked as top recommendation", opportunities.first().isTopRecommendation)

        // Verify financial equations
        opportunities.forEach { opp ->
            // Quoted - Deductions = Net Realization
            assertEquals(
                "Net realization = quotedPrice - totalExpense",
                opp.quotedPricePerQ - opp.totalExpensePerQ,
                opp.netRealizationPerQ
            )
            // Total amount = netRealization * quantity
            assertEquals(
                "Estimated total = netRealization * quantity",
                opp.netRealizationPerQ * opp.quantityQuintals,
                opp.estimatedTotalAmount
            )
            // Total expense per Q = transport + other
            assertEquals(
                "Total expense = transport + other",
                opp.transportExpensePerQ + opp.otherExpensePerQ,
                opp.totalExpensePerQ
            )
        }

        // Verify destinations include both BUYER and MARKET
        assertTrue("Should include at least one BUYER", opportunities.any { it.destinationType == DestinationType.BUYER })
        assertTrue("Should include at least one MARKET", opportunities.any { it.destinationType == DestinationType.MARKET })
    }

    private fun setupViewModelToStep6(): SellingViewModel {
        val vm = SellingViewModel(repository)
        vm.setCrop(CropOption("soybean", R.string.crop_soybean, "🌱", 4850))
        vm.updateQuantity(50)
        vm.selectQuality("good", R.string.produce_quality_good)
        vm.selectLocation("Nagpur, Maharashtra", R.string.loc_nagpur, LocationSource.CURRENT_LOCATION)
        vm.selectHarvestReadiness(HarvestReadiness.READY_NOW)
        
        // Execute analysis and move to RECOMMENDATIONS
        val engine = RecommendationEngine()
        val opps = engine.calculateRecommendations(
            cropId = vm.uiState.value.selectedCrop.id,
            quantityQuintals = vm.uiState.value.quantityQuintals,
            qualityKey = vm.uiState.value.qualityKey,
            location = vm.uiState.value.location,
            readyTiming = vm.uiState.value.readyTiming
        ).getOrThrow()
        
        vm.setRecommendationsForTest(opps)
        return vm
    }

    @Test
    fun testRecommendationsScreenRendersBestOptionAndDetails() {
        val viewModel = setupViewModelToStep6()

        composeTestRule.setContent {
            SellScreen(
                repository = repository,
                viewModel = viewModel,
                onNavigateToMyLots = {},
                onNavigateToHome = {}
            )
        }

        // 1. Verify Produce Summary Card
        composeTestRule.onNodeWithTag("step6_produce_summary").assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithTag("change_details_button").assertExists()

        // 2. Verify Comparison Explanation Banner
        composeTestRule.onNodeWithTag("rec_comparison_explanation_banner").assertExists()

        // 3. Verify Top / Best Recommendation Card exists
        composeTestRule.onNodeWithTag("top_recommendation_card").assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithTag("best_rec_header").assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithTag("best_rec_destination_name").assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithTag("best_rec_quoted_price").assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithTag("best_rec_deductions").assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithTag("best_rec_net_price").assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithTag("best_rec_total_amount").assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithTag("best_rec_reasons_list").assertExists().assertIsDisplayed()

        // 4. Verify "Sell Here" primary button exists
        composeTestRule.onNodeWithTag("sell_here_best_button").performScrollTo().assertIsDisplayed()

        // 5. Expand "Why this suggestion?"
        composeTestRule.onNodeWithTag("why_this_suggestion_button").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("why_suggestion_explanation_card").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun testSelectSellHereNavigatesToConfirmation() {
        val viewModel = setupViewModelToStep6()

        composeTestRule.setContent {
            SellScreen(
                repository = repository,
                viewModel = viewModel,
                onNavigateToMyLots = {},
                onNavigateToHome = {}
            )
        }

        // Click "Sell Here" on the best option
        composeTestRule.onNodeWithTag("sell_here_best_button").performScrollTo().performClick()

        // Verify current step is now CONFIRM_SELL
        assertEquals(SellingStep.CONFIRM_SELL, viewModel.uiState.value.currentStep)
        assertNotNull(viewModel.uiState.value.selectedOpportunity)
    }

    @Test
    fun testChangeDetailsNavigatesBackToReview() {
        val viewModel = setupViewModelToStep6()

        composeTestRule.setContent {
            SellScreen(
                repository = repository,
                viewModel = viewModel,
                onNavigateToMyLots = {},
                onNavigateToHome = {}
            )
        }

        // Click "Change details" button
        composeTestRule.onNodeWithTag("change_details_button").performClick()

        // Verify navigation back to REVIEW
        assertEquals(SellingStep.REVIEW, viewModel.uiState.value.currentStep)
    }
}
