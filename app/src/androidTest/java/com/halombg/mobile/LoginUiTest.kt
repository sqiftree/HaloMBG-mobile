package com.halombg.mobile

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginUiTest {

    @Rule
    @JvmField
    val composeTestRule = createAndroidComposeRule<LoginActivity>()

    @Test
    fun testLoginValidationError() {
        // Leave fields empty and trigger login click
        composeTestRule.onNodeWithText("Masuk").performClick()
        
        // Assert the application doesn't navigate and stays on Login screen
        composeTestRule.onNodeWithText("PLATFORM MONITORING MBG").assertDoesNotExist()
    }
}
