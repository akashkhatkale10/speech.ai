package com.speechai.speechai.screens.results

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.speechai.speechai.CustomTextStyle
import com.speechai.speechai.R
import com.speechai.speechai.audio.player.AndroidAudioPlayer
import com.speechai.speechai.backgroundColor
import com.speechai.speechai.composables.CustomTopBar
import com.speechai.speechai.composables.ProgressBar
import com.speechai.speechai.composables.RecordingState
import com.speechai.speechai.composables.SmallCircleButton
import com.speechai.speechai.models.AnalysisScreenData
import com.speechai.speechai.screens.onboarding.PropertiesSection
import com.speechai.speechai.screens.onboarding.TimerContent
import com.speechai.speechai.secondaryColor
import com.speechai.speechai.subtitleTextColor
import com.speechai.speechai.tertiaryColor
import com.speechai.speechai.utils.bounceClick
import com.speechai.speechai.whiteColor

@Composable
fun ResultScreen(
    navController: NavController,
    screenData: AnalysisScreenData,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val audioPlayer = remember {
        AndroidAudioPlayer(context, screenData.file)
    }

    LaunchedEffect(Unit) {
        audioPlayer.initialise()
    }

    Scaffold(
        modifier = modifier,
        containerColor = backgroundColor
    ) {
        Column(
            modifier = modifier
                .padding(it)
                .fillMaxSize()
        ) {
            CustomTopBar(
                modifier = Modifier
                    .padding(horizontal = 20.dp),
                startComposable = {
                    Box(
                        modifier = Modifier
                            .bounceClick {
                                navController.navigateUp()
                            }
                            .size(34.dp)
                            .background(
                                color = tertiaryColor,
                                shape = CircleShape
                            )
                            .padding(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            tint = whiteColor,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                },
                midComposable = {
                    Text(
                        text = stringResource(R.string.your_results),
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
                    .verticalScroll(rememberScrollState())
            ) {
                SmallAudioPlayer(
                    audioPlayer = audioPlayer,
                    modifier = Modifier
                        .padding(top = 10.dp)
                )

                Text(
                    text = "your speech score",
                    style = CustomTextStyle.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = subtitleTextColor,
                        lineHeight = 40.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .padding(top = 54.dp)
                        .fillMaxWidth()
                )

                if (screenData.totalScore != 0) {
                    Text(
                        text = "${screenData.totalScore} % score",
                        style = CustomTextStyle.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = whiteColor,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .align(Alignment.CenterHorizontally)
                            .background(
                                screenData.totalScoreColor,
                                shape = RoundedCornerShape(100.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.height(54.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(54.dp),
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                ) {
                    if (screenData.bestScore.isNotEmpty()) {
                        PropertiesSection(
                            modifier = Modifier,
                            title = buildAnnotatedString {
                                withStyle(
                                    SpanStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                ) {
                                    append("\uD83D\uDC4D\uD83C\uDFFB")
                                }
                                withStyle(
                                    SpanStyle(
                                        color = subtitleTextColor,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                ) {
                                    append("   you did best in:")
                                }
                            },
                            propertiesModel = screenData.bestScore
                        )
                    }

                    if (screenData.worstScore.isNotEmpty()) {
                        PropertiesSection(
                            modifier = Modifier,
                            title = buildAnnotatedString {
                                withStyle(
                                    SpanStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                ) {
                                    append("\uD83D\uDEA8")
                                }
                                withStyle(
                                    SpanStyle(
                                        color = subtitleTextColor,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                ) {
                                    append("   need to improve in:")
                                }
                            },
                            propertiesModel = screenData.worstScore
                        )
                    }

                    if (screenData.otherScore.isNotEmpty()) {
                        PropertiesSection(
                            modifier = Modifier,
                            title = buildAnnotatedString {
                                withStyle(
                                    SpanStyle(
                                        color = subtitleTextColor,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                ) {
                                    append("you can also check this:")
                                }
                            },
                            propertiesModel = screenData.otherScore
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SmallAudioPlayer(
    audioPlayer: AndroidAudioPlayer,
    modifier: Modifier = Modifier,
) {
    val timer = audioPlayer.timerState.collectAsState()
    val recordingState = audioPlayer.recordingState
    val progress = animateFloatAsState(
        targetValue = timer.value / audioPlayer.duration.toFloat(),
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
    )

    DisposableEffect(Unit) {
        onDispose {
            audioPlayer.stop() // ensure media and timer are stopped and released
        }
    }

    Column(
        modifier = modifier
    ) {
        TimerContent(
            timer = timer.value,
            modifier = Modifier,
            mainTextSize = 14,
            secondaryTextSize = 14,
            timerLimit = audioPlayer.duration,
        )

        ProgressBar(
            progress = { progress.value },
            modifier = Modifier
                .padding(top = 20.dp),
            height = 10,
        )

        Row(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            SmallCircleButton(
                size = 46.dp,
                iconSize = if (recordingState == RecordingState.PLAYING) 20.dp else 30.dp,
                icon = if (recordingState == RecordingState.PLAYING) ImageVector.vectorResource(R.drawable.pause) else Icons.Default.PlayArrow,
                borderColor = whiteColor,
                modifier = Modifier
                    .bounceClick {
                        if (recordingState == RecordingState.PLAYING) {
                            audioPlayer.pause()
                        } else if (
                            recordingState == RecordingState.STOPPED ||
                            recordingState == RecordingState.IDLE || recordingState == RecordingState.PAUSED
                        ) {
                            audioPlayer.play()
                        }
                    },
            )
        }
    }
}