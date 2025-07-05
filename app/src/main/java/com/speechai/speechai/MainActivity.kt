package com.speechai.speechai

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.speechai.speechai.composables.WavePaintConfig
import com.speechai.speechai.databinding.MainActivityBinding
import com.speechai.speechai.models.Screen
import com.speechai.speechai.prefs.SharedPrefs
import com.speechai.speechai.screens.home.HomeScreen
import com.speechai.speechai.screens.onboarding.OnboardingRecordingScreen
import com.speechai.speechai.screens.onboarding.OnboardingScreen
import com.speechai.speechai.screens.onboarding.OnboardingViewModel
import com.speechai.speechai.screens.splash.SplashScreen
import com.speechai.speechai.ui.theme.SpeechAiTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random

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
                        .background(backgroundColor)
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
                    composable(Screen.Home.route) {
                        HomeScreen(navController = navController)
                    }
                    composable(Screen.Splash.route) {
                        SplashScreen(
                            sharedPrefs = sharedPrefs,
                            navController = navController
                        )
                    }

                    // Add composable(Screen.Home.route) { ... }
                }
            }
        }
    }
}