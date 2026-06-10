package com.halombg.mobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryNavy,
    secondary = SecondaryGreen,
    background = Surface2,
    surface = Surface1,
    onPrimary = Surface1,
    onSecondary = PrimaryNavy,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun HaloMBGTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
