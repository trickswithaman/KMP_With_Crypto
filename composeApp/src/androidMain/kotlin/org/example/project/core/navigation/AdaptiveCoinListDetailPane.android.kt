package org.example.project.core.navigation

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    androidx.activity.compose.BackHandler(enabled = enabled, onBack = onBack)
}

// FIRST WE LAUNCHED THE APP ON THE DESKTOP THEN WEB AND THEN ANDROID