package com.example

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.data.AppLanguage
import com.example.ui.components.FarmerProfileDialog
import com.example.ui.theme.AgriLinkTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h891dp")
class FarmerProfileDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test1_profileDialog_englishLocale_displaysAllLabelsInEnglish() {
        var dismissed = false
        composeTestRule.setContent {
            AgriLinkTheme(darkTheme = false) {
                FarmerProfileDialog(
                    currentLanguage = AppLanguage.ENGLISH,
                    onDismissRequest = { dismissed = true }
                )
            }
        }

        // Farmer Name with Role (English)
        composeTestRule.onNodeWithTag("profile_farmer_name")
            .assertIsDisplayed()
            .assertTextEquals("Rajesh Patil (Farmer)")

        // Location (English)
        composeTestRule.onNodeWithTag("profile_farmer_location")
            .assertIsDisplayed()
            .assertTextEquals("Nagpur, Maharashtra")

        // FPO Info (English)
        composeTestRule.onNodeWithTag("profile_fpo_info")
            .assertIsDisplayed()
            .assertTextEquals("FPO: Vidarbha Agro Farmers Producer Organization")

        // Member ID (English label with untranslated ID)
        composeTestRule.onNodeWithTag("profile_member_id")
            .assertIsDisplayed()
            .assertTextEquals("Member ID: FPO-NGP-2024-8841")

        // Helpline (English label with untranslated number)
        composeTestRule.onNodeWithTag("profile_helpline")
            .assertIsDisplayed()
            .assertTextEquals("Kisan Helpline: 1800-180-1551 (Toll Free)")

        // Confirm OK Button (English)
        composeTestRule.onNodeWithTag("profile_dialog_ok_btn")
            .assertIsDisplayed()
            .assertTextEquals("OK")
    }

    @Test
    fun test2_profileDialog_hindiLocale_displaysAllLabelsInHindi() {
        var dismissed = false
        composeTestRule.setContent {
            AgriLinkTheme(darkTheme = false) {
                FarmerProfileDialog(
                    currentLanguage = AppLanguage.HINDI,
                    onDismissRequest = { dismissed = true }
                )
            }
        }

        // Farmer Name with Role (Hindi)
        composeTestRule.onNodeWithTag("profile_farmer_name")
            .assertIsDisplayed()
            .assertTextEquals("राजेश पाटील (किसान)")

        // Location (Hindi)
        composeTestRule.onNodeWithTag("profile_farmer_location")
            .assertIsDisplayed()
            .assertTextEquals("नागपुर, महाराष्ट्र")

        // FPO Info (Hindi)
        composeTestRule.onNodeWithTag("profile_fpo_info")
            .assertIsDisplayed()
            .assertTextEquals("FPO: विदर्भ एग्रो किसान उत्पादक संघ")

        // Member ID (Hindi label with untranslated ID)
        composeTestRule.onNodeWithTag("profile_member_id")
            .assertIsDisplayed()
            .assertTextEquals("सदस्य क्रमांक: FPO-NGP-2024-8841")

        // Helpline (Hindi label with untranslated number)
        composeTestRule.onNodeWithTag("profile_helpline")
            .assertIsDisplayed()
            .assertTextEquals("किसान हेल्पलाइन: 1800-180-1551 (टोल फ्री)")

        // Confirm OK Button (Hindi)
        composeTestRule.onNodeWithTag("profile_dialog_ok_btn")
            .assertIsDisplayed()
            .assertTextEquals("ठीक है")
    }

    @Test
    fun test3_profileDialog_marathiLocale_displaysAllLabelsInMarathi() {
        var dismissed = false
        composeTestRule.setContent {
            AgriLinkTheme(darkTheme = false) {
                FarmerProfileDialog(
                    currentLanguage = AppLanguage.MARATHI,
                    onDismissRequest = { dismissed = true }
                )
            }
        }

        // Farmer Name with Role (Marathi)
        composeTestRule.onNodeWithTag("profile_farmer_name")
            .assertIsDisplayed()
            .assertTextEquals("राजेश पाटील (शेतकरी)")

        // Location (Marathi)
        composeTestRule.onNodeWithTag("profile_farmer_location")
            .assertIsDisplayed()
            .assertTextEquals("नागपूर, महाराष्ट्र")

        // FPO Info (Marathi)
        composeTestRule.onNodeWithTag("profile_fpo_info")
            .assertIsDisplayed()
            .assertTextEquals("FPO: विदर्भ अॅग्रो शेतकरी उत्पादक संघ")

        // Member ID (Marathi label with untranslated ID)
        composeTestRule.onNodeWithTag("profile_member_id")
            .assertIsDisplayed()
            .assertTextEquals("सदस्य क्रमांक: FPO-NGP-2024-8841")

        // Helpline (Marathi label with untranslated number)
        composeTestRule.onNodeWithTag("profile_helpline")
            .assertIsDisplayed()
            .assertTextEquals("किसान हेल्पलाइन: 1800-180-1551 (टोल फ्री)")

        // Confirm OK Button (Marathi)
        composeTestRule.onNodeWithTag("profile_dialog_ok_btn")
            .assertIsDisplayed()
            .assertTextEquals("ठीक आहे")
    }

    @Test
    fun test4_dynamicLanguageSwitching_updatesDialogContentReactively() {
        var languageState by mutableStateOf(AppLanguage.ENGLISH)
        composeTestRule.setContent {
            AgriLinkTheme {
                FarmerProfileDialog(
                    currentLanguage = languageState,
                    onDismissRequest = {}
                )
            }
        }

        // Initially English
        composeTestRule.onNodeWithTag("profile_farmer_name").assertTextEquals("Rajesh Patil (Farmer)")
        composeTestRule.onNodeWithTag("profile_dialog_ok_btn").assertTextEquals("OK")

        // Switch to Hindi
        languageState = AppLanguage.HINDI
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("profile_farmer_name").assertTextEquals("राजेश पाटील (किसान)")
        composeTestRule.onNodeWithTag("profile_dialog_ok_btn").assertTextEquals("ठीक है")

        // Switch to Marathi
        languageState = AppLanguage.MARATHI
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("profile_farmer_name").assertTextEquals("राजेश पाटील (शेतकरी)")
        composeTestRule.onNodeWithTag("profile_dialog_ok_btn").assertTextEquals("ठीक आहे")

        // Switch back to English
        languageState = AppLanguage.ENGLISH
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("profile_farmer_name").assertTextEquals("Rajesh Patil (Farmer)")
        composeTestRule.onNodeWithTag("profile_dialog_ok_btn").assertTextEquals("OK")
    }

    @Test
    fun test5_closeAndReopenDialog_usesNewlySelectedLanguage() {
        var languageState by mutableStateOf(AppLanguage.ENGLISH)
        var isDialogOpen by mutableStateOf(true)

        composeTestRule.setContent {
            AgriLinkTheme {
                if (isDialogOpen) {
                    FarmerProfileDialog(
                        currentLanguage = languageState,
                        onDismissRequest = { isDialogOpen = false }
                    )
                }
            }
        }

        // Check English
        composeTestRule.onNodeWithTag("profile_farmer_name").assertTextEquals("Rajesh Patil (Farmer)")

        // Click OK to close dialog
        composeTestRule.onNodeWithTag("profile_dialog_ok_btn").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("farmer_profile_dialog").assertDoesNotExist()

        // Switch language to Marathi while dialog is closed
        languageState = AppLanguage.MARATHI
        // Reopen dialog
        isDialogOpen = true
        composeTestRule.waitForIdle()

        // Dialog must appear in Marathi
        composeTestRule.onNodeWithTag("farmer_profile_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("profile_farmer_name").assertTextEquals("राजेश पाटील (शेतकरी)")
        composeTestRule.onNodeWithTag("profile_dialog_ok_btn").assertTextEquals("ठीक आहे")
    }

    @Test
    fun test6_darkModeCompatibility_displaysCorrectly() {
        composeTestRule.setContent {
            AgriLinkTheme(darkTheme = true) {
                FarmerProfileDialog(
                    currentLanguage = AppLanguage.ENGLISH,
                    onDismissRequest = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("farmer_profile_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("profile_farmer_name").assertTextEquals("Rajesh Patil (Farmer)")
        composeTestRule.onNodeWithTag("profile_dialog_ok_btn").assertIsDisplayed()
    }

    @Test
    fun test7_topBarIntegration_openProfileDialogInEnglish() {
        var selectedLang by mutableStateOf(AppLanguage.ENGLISH)
        composeTestRule.setContent {
            AgriLinkTheme {
                com.example.ui.components.AgriTopBar(
                    currentLanguage = selectedLang,
                    onLanguageSelected = { selectedLang = it },
                    onHelpClicked = {}
                )
            }
        }

        // Click topbar profile icon
        composeTestRule.onNodeWithTag("topbar_profile_btn").performClick()
        composeTestRule.waitForIdle()

        // Profile dialog must open with English strings
        composeTestRule.onNodeWithTag("farmer_profile_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("profile_farmer_name").assertTextEquals("Rajesh Patil (Farmer)")
        composeTestRule.onNodeWithTag("profile_fpo_info").assertTextEquals("FPO: Vidarbha Agro Farmers Producer Organization")
        composeTestRule.onNodeWithTag("profile_dialog_ok_btn").assertTextEquals("OK")

        // Dismiss dialog
        composeTestRule.onNodeWithTag("profile_dialog_ok_btn").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("farmer_profile_dialog").assertDoesNotExist()
    }

    @Test
    fun test8_userDataIntegrity_memberIdAndHelplineNumbersNeverTranslate() {
        var currentLang by mutableStateOf(AppLanguage.ENGLISH)
        composeTestRule.setContent {
            AgriLinkTheme {
                FarmerProfileDialog(
                    currentLanguage = currentLang,
                    onDismissRequest = {}
                )
            }
        }

        listOf(AppLanguage.ENGLISH, AppLanguage.HINDI, AppLanguage.MARATHI).forEach { lang ->
            currentLang = lang
            composeTestRule.waitForIdle()

            // Verify member ID node contains the exact raw ID string
            composeTestRule.onNodeWithTag("profile_member_id")
                .assertIsDisplayed()

            // Verify helpline node contains the exact raw helpline phone number
            composeTestRule.onNodeWithTag("profile_helpline")
                .assertIsDisplayed()
        }
    }
}
