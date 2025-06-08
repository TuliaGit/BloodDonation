package com.example.blooddonation.feature.theme

import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

val LocalIsDarkTheme = compositionLocalOf { false }
val LocalToggleTheme = compositionLocalOf { { } }

@Composable
fun ThemeSwitch() {
    val isDark = LocalIsDarkTheme.current
    val toggle = LocalToggleTheme.current
    Switch(checked = isDark, onCheckedChange = { toggle() })
}
