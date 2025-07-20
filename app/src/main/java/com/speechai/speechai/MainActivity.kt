package com.speechai.speechai

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.speechai.speechai.models.AnalysisScreenData
import com.speechai.speechai.models.Screen
import com.speechai.speechai.prefs.SharedPrefs
import com.speechai.speechai.screens.onboarding.OnboardingRecordingScreen
import com.speechai.speechai.screens.onboarding.OnboardingScreen
import com.speechai.speechai.screens.home.HomeScreen
import com.speechai.speechai.screens.results.ResultScreen
import com.speechai.speechai.screens.splash.SplashScreen
import com.speechai.speechai.ui.theme.SpeechAiTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    internal lateinit var sharedPrefs: SharedPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpeechAiTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.Splash.route,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor),
                    enterTransition = {
                        fadeIn()
                    },
                    exitTransition = {
                        fadeOut()
                    }
                ) {
                    composable(Screen.Onboarding.route) {
                        OnboardingScreen(
                            sharedPrefs = sharedPrefs
                        ) {
                            navController.navigate(Screen.OnboardingRecording.route) {
                                popUpTo(Screen.Onboarding.route) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                    composable(Screen.OnboardingRecording.route) {
                        OnboardingRecordingScreen(navController = navController)
                    }
                    composable(
                        Screen.Home.route,
                    ) {
                        HomeScreen(navController = navController)
                    }
                    composable(Screen.Splash.route) {
                        SplashScreen(
                            sharedPrefs = sharedPrefs,
                            navController = navController
                        )
                    }

                    composable(
                        Screen.Result.route
                    ) {
                        val screenData =
                            navController.previousBackStackEntry?.savedStateHandle?.get<AnalysisScreenData>(
                                "analysis"
                            )
                        screenData?.let {
                            ResultScreen(
                                navController = navController,
                                screenData = it,
                            )
                        } ?: run {
                            // todo something went wrong
                        }

                    }

                    // Add composable(Screen.Home.route) { ... }
                }
            }
        }
    }
}