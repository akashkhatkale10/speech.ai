package com.speechai.speechai.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.speechai.speechai.CustomTextStyle
import com.speechai.speechai.R
import com.speechai.speechai.auth.AuthViewModel
import com.speechai.speechai.backgroundColor
import com.speechai.speechai.composables.Loading
import com.speechai.speechai.models.Screen
import com.speechai.speechai.prefs.SharedPrefs
import com.speechai.speechai.prefs.SharedPrefs.Companion.ONBOARDING_COMPLETED
import com.speechai.speechai.whiteColor
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    sharedPrefs: SharedPrefs,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val authViewModel: AuthViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        // todo optimise this delay
        delay(500)
        if (authViewModel.getCurrentUser() != null) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Splash.route) {
                    inclusive = true
                }
            }
        } else {
            if (sharedPrefs.getBoolean(ONBOARDING_COMPLETED, false)) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Splash.route) {
                        inclusive = true
                    }
                }
            } else {
                navController.navigate(Screen.Onboarding.route) {
                    popUpTo(Screen.Splash.route) {
                        inclusive = true
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.title),
                style = CustomTextStyle.copy(
                    color = whiteColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Loading(
                modifier = Modifier
                    .padding(top = 30.dp)
            )
        }
    }
}