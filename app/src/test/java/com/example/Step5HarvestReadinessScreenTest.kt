package com.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Density
import com.example.data.MockAgriRepository
import com.example.model.CropOption
import com.example.ui.screens.HarvestReadiness
import com.example.ui.screens.LocationSource
import com.example.ui.screens.SellScreen
import com.example.ui.screens.SellingStep
import com.example.ui.screens.SellingViewModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h891dp")
class Step5HarvestReadinessScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val repository = MockAgriRepository()

    private fun setupViewModelToStep5(): SellingViewModel {
        val vm = SellingViewModel(repository)
        vm.setCrop(CropOption("soybean", R.string.crop_soybean, "🌱", 4850))
        vm.updateQuantity(50)
        vm.selectQuality("good", R.string.produce_quality_good)
        vm.selectLocation("Nagpur, Maharashtra", R.string.loc_nagpur, LocationSource.CURRENT_LOCATION)
        vm.goToStep(SellingStep.READY_DATE)
        return vm
    }

    @Test
    fun testStep5InitialStateContinueDisabled() {
        val viewModel = setupViewModelToStep5()

        composeTestRule.setContent {
            SellScreen(
                repository = repository,
                viewModel = viewModel,
                onNavigateToMyLots = {},
                onNavigateToHome = {}
            )
        }

        // 1. Verify produce summary is displayed
        composeTestRule.onNodeWithTag("step5_produce_context").assertExists().assertIsDisplayed()

        // 2. Verify explanation note is displayed
        composeTestRule.onNodeWithTag("timing_why_matters_note").assertExists().assertIsDisplayed()

        // 3. Verify all 3 options exist
        composeTestRule.onNodeWithTag("timing_option_ready_now").assertExists()
        composeTestRule.onNodeWithTag("timing_option_within_7_days").assertExists()
        composeTestRule.onNodeWithTag("timing_option_later").assertExists()

        // 4. Verify Continue button exists and is disabled before selection
        composeTestRule.onNodeWithTag("continue_step5_button").assertExists().assertIsNotEnabled()
    }

    @Test
    fun testStep5OptionSelectionsAndContinueEnabled() {
        val viewModel = setupViewModelToStep5()

        composeTestRule.setContent {
            SellScreen(
                repository = repository,
                viewModel = viewModel,
                onNavigateToMyLots = {},
                onNavigateToHome = {}
            )
        }

        // 1. Select "Ready now"
        composeTestRule.onNodeWithTag("timing_option_ready_now").performScrollTo().performClick()
        assertEquals(HarvestReadiness.READY_NOW, viewModel.uiState.value.harvestReadiness)
        composeTestRule.onNodeWithTag("continue_step5_button").performScrollTo().assertIsEnabled()

        // Verify RadioButton semantics
        composeTestRule.onNodeWithTag("timing_option_ready_now")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))

        // 2. Select "Within 7 days"
        composeTestRule.onNodeWithTag("timing_option_within_7_days").performScrollTo().performClick()
        assertEquals(HarvestReadiness.WITHIN_7_DAYS, viewModel.uiState.value.harvestReadiness)
        composeTestRule.onNodeWithTag("continue_step5_button").performScrollTo().assertIsEnabled()

        // 3. Select "Later"
        composeTestRule.onNodeWithTag("timing_option_later").performScrollTo().performClick()
        assertEquals(HarvestReadiness.LATER, viewModel.uiState.value.harvestReadiness)
        composeTestRule.onNodeWithTag("continue_step5_button").performScrollTo().assertIsEnabled()

        // 4. Click Continue -> advances to REVIEW (recommendation stage)
        composeTestRule.onNodeWithTag("continue_step5_button").performScrollTo().performClick()
        assertEquals(SellingStep.REVIEW, viewModel.uiState.value.currentStep)
    }

    @Test
    fun testStep5BackNavigationPreservesAllEnteredInfo() {
        val viewModel = setupViewModelToStep5()

        composeTestRule.setContent {
            SellScreen(
                repository = repository,
                viewModel = viewModel,
                onNavigateToMyLots = {},
                onNavigateToHome = {}
            )
        }

        // Select Ready Now
        composeTestRule.onNodeWithTag("timing_option_ready_now").performScrollTo().performClick()
        assertEquals(HarvestReadiness.READY_NOW, viewModel.uiState.value.harvestReadiness)

        // Navigate Back to Step 4 (Location)
        composeTestRule.onNodeWithTag("step_back_button").performClick()
        assertEquals(SellingStep.LOCATION, viewModel.uiState.value.currentStep)
        assertEquals("Nagpur, Maharashtra", viewModel.uiState.value.location)
        assertEquals(50, viewModel.uiState.value.quantityQuintals)
        assertEquals("good", viewModel.uiState.value.qualityKey)
        assertEquals("soybean", viewModel.uiState.value.selectedCrop.id)

        // Return to Step 5
        viewModel.goToStep(SellingStep.READY_DATE)
        assertEquals(HarvestReadiness.READY_NOW, viewModel.uiState.value.harvestReadiness)
    }

    @Test
    fun testStep5LargeFontScaleAccessibility() {
        val viewModel = setupViewModelToStep5()

        composeTestRule.setContent {
            // Simulate 1.5x font scale for accessibility
            CompositionLocalProvider(
                LocalDensity provides Density(density = 2.5f, fontScale = 1.5f)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    SellScreen(
                        repository = repository,
                        viewModel = viewModel,
                        onNavigateToMyLots = {},
                        onNavigateToHome = {}
                    )
                }
            }
        }

        // Verify elements render without crash under large font scale
        composeTestRule.onNodeWithTag("step5_produce_context").assertExists()
        composeTestRule.onNodeWithTag("timing_option_ready_now").assertExists()
        composeTestRule.onNodeWithTag("timing_option_within_7_days").assertExists()
        composeTestRule.onNodeWithTag("timing_option_later").assertExists()
        composeTestRule.onNodeWithTag("continue_step5_button").assertExists()
    }
}
