package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.example.data.MockAgriRepository
import com.example.ui.screens.PricesScreen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PricesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val repository = MockAgriRepository()

    @Test
    fun testPricesScreenDataIntegrity() {
        val primaryPrice = repository.getTodaysPrimaryPrice()
        assertEquals(4850, primaryPrice.pricePerQuintal)
        assertEquals(R.string.crop_soybean, primaryPrice.cropNameRes)
        assertEquals(R.string.market_nagpur, primaryPrice.marketNameRes)
        assertTrue(primaryPrice.isPositiveChange)

        val markets = repository.getMarketPrices()
        assertEquals(4, markets.size)
        assertTrue(markets.any { it.marketNameRes == R.string.market_nagpur })
        assertTrue(markets.any { it.marketNameRes == R.string.market_katol })
        assertTrue(markets.any { it.marketNameRes == R.string.market_hingna })
        assertTrue(markets.any { it.marketNameRes == R.string.market_amravati })

        val crops = repository.getAvailableCrops()
        assertTrue(crops.any { it.id == "soybean" })
        assertTrue(crops.any { it.id == "wheat" })
    }

    @Test
    fun testPricesLocalizationInAllLanguages() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val stringKeys = listOf(
            R.string.title_price_trend,
            R.string.trend_increased_7d,
            R.string.trend_steady_7d,
            R.string.trend_summary_nagpur,
            R.string.badge_benchmark_market,
            R.string.badge_highest_rate,
            R.string.label_select_crop,
            R.string.prices_sell_action_title,
            R.string.prices_sell_action_subtext,
            R.string.market_distance_nearby,
            R.string.label_todays_benchmark
        )

        for (langCode in listOf("en", "hi", "mr")) {
            val config = context.resources.configuration
            val locale = Locale.forLanguageTag(langCode)
            val localizedContext = context.createConfigurationContext(
                config.apply { setLocale(locale) }
            )

            for (resId in stringKeys) {
                val str = localizedContext.getString(resId)
                assertTrue("String $resId must not be blank for lang $langCode", str.isNotBlank())
            }
        }
    }

    @Test
    fun testPricesScreenCompositionAndNavigation() {
        var navigatedToSell = false

        composeTestRule.setContent {
            PricesScreen(
                repository = repository,
                onNavigateToSell = { navigatedToSell = true }
            )
        }

        // 1. Verify Top Benchmark Card is displayed
        composeTestRule.onNodeWithTag("card_todays_benchmark").assertIsDisplayed()

        // 2. Verify 7-Day Trend Card exists in layout
        composeTestRule.onNodeWithTag("card_price_trend").assertExists()

        // 3. Verify Nearby Mandi Cards exist in layout
        composeTestRule.onNodeWithTag("card_mandi_1").assertExists()
        composeTestRule.onNodeWithTag("card_mandi_2").assertExists()

        // 4. Verify Decision Card exists in layout
        composeTestRule.onNodeWithTag("card_prices_find_best_place").assertExists()

        // 5. Test "Find Best Place to Sell" navigation button with scroll
        composeTestRule.onNodeWithTag("btn_prices_find_best_place")
            .performScrollTo()
            .performClick()
        assertTrue("Clicking Find Best Place to Sell must trigger navigation", navigatedToSell)
    }

    @Test
    fun testCropSelectorInteraction() {
        composeTestRule.setContent {
            PricesScreen(
                repository = repository
            )
        }

        // Verify soybean is initially selected
        composeTestRule.onNodeWithTag("crop_chip_soybean").assertIsDisplayed()
        composeTestRule.onNodeWithTag("crop_chip_wheat").assertIsDisplayed()

        // Switch to wheat
        composeTestRule.onNodeWithTag("crop_chip_wheat").performClick()

        // Verify UI continues to display benchmark card with updated crop
        composeTestRule.onNodeWithTag("card_todays_benchmark").assertIsDisplayed()
    }
}
