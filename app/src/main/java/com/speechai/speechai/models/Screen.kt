package com.speechai.speechai.models

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object OnboardingRecording : Screen("onboarding_recording")
    // Add other screens here as needed, e.g.
    object Home : Screen("home")
}