package com.speechai.speechai.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.speechai.speechai.CustomTextStyle
import com.speechai.speechai.R
import com.speechai.speechai.backgroundColor
import com.speechai.speechai.composables.CustomButton
import com.speechai.speechai.composables.CustomTopBar
import com.speechai.speechai.prefs.SharedPrefs
import com.speechai.speechai.prefs.SharedPrefs.Companion.ONBOARDING_COMPLETED
import com.speechai.speechai.secondaryColor
import com.speechai.speechai.tertiaryColor
import com.speechai.speechai.whiteColor

enum class State {
    BIG, SMALL
}
@Composable
fun OnboardingScreen(
    sharedPrefs: SharedPrefs,
    onGetStarted: () -> Unit
) {
    val viewModel = hiltViewModel<OnboardingViewModel>()
    val benefits = viewModel.currentBenefit.collectAsState()

    LaunchedEffect(Unit) {
//        sharedPrefs.putBoolean(
//            ONBOARDING_COMPLETED,
//            true
//        )
        viewModel.emitBenefits()
    }

    Scaffold(
        containerColor = backgroundColor
    ) {
        Box(
            modifier = Modifier
                .padding(it)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor),
            ) {
                CustomTopBar(
                    midComposable = {
                        Text(
                            text = stringResource(R.string.title),
                            style = CustomTextStyle.copy(
                                color = whiteColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 32.dp, bottom = 30.dp)
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "welcome  \uD83D\uDC4B\uD83C\uDFFB",
                        style = CustomTextStyle.copy(
                            color = whiteColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Light
                        )
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(30.dp),
                        modifier = Modifier
                            .padding(top = 32.dp)
                    ) {
                        benefits.value.forEach {
                            var isVisible by remember {
                                mutableStateOf(false)
                            }
                            LaunchedEffect(Unit) {
                                isVisible = true
                            }
                            AnimatedVisibility(
                                visible = isVisible,
                                enter = fadeIn(
                                    animationSpec = tween(2000)
                                ),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = secondaryColor,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            shape = RoundedCornerShape(16.dp),
                                            color = tertiaryColor
                                        )
                                        .padding(20.dp)
                                ) {
                                    Text(
                                        text = it,
                                        style = CustomTextStyle.copy(
                                            color = whiteColor,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Light,
                                            lineHeight = 40.sp
                                        ),
                                        modifier = Modifier
                                    )
                                }
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = benefits.value.size == 4,
                    enter = fadeIn(
                        animationSpec = tween(1500)
                    ),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(backgroundColor)
                            .padding(vertical = 30.dp)
                    ) {
                        CustomButton(
                            text = "let’s get started",
                            icon = Icons.Default.ArrowForward,
                            onClick = {
                                onGetStarted()
                            },
                            modifier = Modifier
                                .padding(horizontal = 28.dp)
                        )
                    }
                }
            }
        }
    }
}
