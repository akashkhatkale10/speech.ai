package com.speechai.speechai.screens.results

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.speechai.speechai.CustomTextStyle
import com.speechai.speechai.R
import com.speechai.speechai.audio.player.AndroidAudioPlayer
import com.speechai.speechai.backgroundColor
import com.speechai.speechai.composables.CustomTopBar
import com.speechai.speechai.composables.ProgressBar
import com.speechai.speechai.composables.RecordingState
import com.speechai.speechai.models.AnalysisScreenData
import com.speechai.speechai.screens.onboarding.PropertiesSection
import com.speechai.speechai.screens.onboarding.TimerContent
import com.speechai.speechai.secondaryColor
import com.speechai.speechai.subtitleTextColor
import com.speechai.speechai.tertiaryColor
import com.speechai.speechai.whiteColor

@Composable
fun ResultScreen(
    screenData: AnalysisScreenData,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val audioRecorder = remember {
        AndroidAudioPlayer(context)
    }
    val timer = audioRecorder.timerState.collectAsState()

    LaunchedEffect(Unit) {
        audioRecorder.setFile(screenData.file)
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

            TimerContent(
                timer = timer.value,
                modifier = Modifier
                    .padding(top = 10.dp),
                mainTextSize = 14,
                secondaryTextSize = 14
            )

            ProgressBar(
                progress = { 0.22f },
                modifier = Modifier
                    .padding(top = 20.dp),
                height = 6
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
                    .padding(top = 30.dp)
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
                        .background(screenData.totalScoreColor, shape = RoundedCornerShape(100.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(50.dp)
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