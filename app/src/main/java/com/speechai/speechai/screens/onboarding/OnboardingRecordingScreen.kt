package com.speechai.speechai.screens.onboarding

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.identity.Identity
import com.speechai.speechai.CustomTextStyle
import com.speechai.speechai.R
import com.speechai.speechai.audio.AndroidAudioRecorder
import com.speechai.speechai.audio.AudioViewModel
import com.speechai.speechai.auth.AuthViewModel
import com.speechai.speechai.auth.GoogleAuthUiClient
import com.speechai.speechai.backgroundColor
import com.speechai.speechai.composables.CustomButton
import com.speechai.speechai.composables.CustomTopBar
import com.speechai.speechai.composables.Loading
import com.speechai.speechai.composables.MediumBadge
import com.speechai.speechai.composables.RecordingButton
import com.speechai.speechai.composables.RecordingState
import com.speechai.speechai.composables.SecondaryButton
import com.speechai.speechai.composables.SmallBadge
import com.speechai.speechai.composables.SmallCircleButton
import com.speechai.speechai.composables.StateTag
import com.speechai.speechai.goldenColor
import com.speechai.speechai.lightRedColor
import com.speechai.speechai.models.AnalysisScreenData
import com.speechai.speechai.models.PropertyUiModel
import com.speechai.speechai.models.Screen
import com.speechai.speechai.secondaryColor
import com.speechai.speechai.subtitleTextColor
import com.speechai.speechai.tertiaryColor
import com.speechai.speechai.utils.bounceClick
import com.speechai.speechai.utils.formatTime
import com.speechai.speechai.whiteColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

sealed class AudioPermissionState {
    object Granted : AudioPermissionState()
    object Denied : AudioPermissionState()
    object NeverAsked : AudioPermissionState()
}
@Composable
fun OnboardingRecordingScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val authViewModel = hiltViewModel<AuthViewModel>()
    val audioViewModel = hiltViewModel<AudioViewModel>()
    val analysisResult = audioViewModel.analysisResult.collectAsState()
    var showSkipButton by remember { mutableStateOf(false) }
    val skipAlpha = animateFloatAsState(
        targetValue = if (showSkipButton) 1f else 0f
    )
    val context = LocalContext.current
    var permissionState by remember { mutableStateOf<AudioPermissionState>(AudioPermissionState.NeverAsked) }
    var permissionRequested by remember { mutableStateOf(
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    ) }

    val audioRecorder = remember { AndroidAudioRecorder(context) }
    var recordingState = audioRecorder.recordingState
    var audioFile: File? by remember {
        mutableStateOf(null)
    }

    // --- Permission Launcher ---
    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        permissionState = if (granted) AudioPermissionState.Granted else AudioPermissionState.Denied
    }
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionState = if (isGranted) AudioPermissionState.Granted else AudioPermissionState.Denied
        if (isGranted) {
            File.createTempFile("temp_audio", ".mp3", context.cacheDir,).also { file ->
                audioRecorder.start(file)
                audioFile = file
            }
            recordingState = RecordingState.PLAYING
        }
    }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        permissionState = if (granted) AudioPermissionState.Granted else AudioPermissionState.NeverAsked
    }

    val timer = audioRecorder.timerState.collectAsState()
    val progress = animateFloatAsState(
        targetValue = timer.value / 60f,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
    )

    LaunchedEffect(Unit) {
        delay(2000)
        showSkipButton = true
    }

    val listState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val googleAuthUiClient by remember {
        mutableStateOf(
            GoogleAuthUiClient(
                context = context,
                oneTapClient = Identity.getSignInClient(context)
            )
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if(result.resultCode == RESULT_OK) {
                scope.launch {
                    val signInResult = googleAuthUiClient.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    authViewModel.loginUser(
                        signInResult,
                        audioFile,
                        analysisResult.value.response?.response,
                        analysisResult.value.response?.totalScore,
                        duration = audioRecorder.timerState.value
                    )
                }
            } else {
                authViewModel.loadingState(false)
            }
        }
    )
    val state by authViewModel.loginState.collectAsState()

    LaunchedEffect(state) {
        if (state.data != null) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.OnboardingRecording.route) {
                    inclusive = true
                }
            }
        }
    }


    Scaffold(
        containerColor = backgroundColor
    ) {
        Box(
            modifier = modifier
                .padding(it)
        ) {
            Column(
                modifier = Modifier
                    .then(
                        if (analysisResult.value.response != null) {
                            Modifier
                                .verticalScroll(listState)
                                .padding(bottom = 240.dp)
                        } else Modifier
                    )
                    .fillMaxSize()
                    .background(backgroundColor),
            ) {
                CustomTopBar(
                    midComposable = {
                        Text(
                            text = if (analysisResult.value.isLoading) stringResource(R.string.title) else "your results",
                            style = CustomTextStyle.copy(
                                color = whiteColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                )

                AnimatedContent(
                    targetState = analysisResult.value.response,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(800, delayMillis = 200)) +
                                slideIn(animationSpec = tween(300, delayMillis = 90, easing = LinearEasing), initialOffset = {IntOffset(600, 0)}))
                            .togetherWith(fadeOut(animationSpec = tween(200)))
                    }
                ) { res ->
                    if (res != null) {
                        SuccessScreen(
                            screenData = res,
                        )
                    } else {
                        Column {
                            TimerContent(
                                timer = timer.value
                            )

                            Box(
                                modifier = Modifier
                                    .padding(top = 100.dp)
                                    .fillMaxWidth()
                                    .height(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .background(
                                            color = tertiaryColor
                                        )
                                        .align(Alignment.CenterStart)
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progress.value)
                                        .height(10.dp)
                                        .background(
                                            color = goldenColor
                                        )
                                        .align(Alignment.CenterStart)
                                )
                            }


                            Column(
                                modifier = Modifier
                                    .padding(top = 100.dp)
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                            ) {
                                when (permissionState) {
                                    is AudioPermissionState.Denied -> {
                                        AudioPermissionDeniedContent(
                                            onClick = {
                                                // Open app settings
                                                val intent =
                                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                        data =
                                                            Uri.fromParts("package", context.packageName, null)
                                                    }
                                                settingsLauncher.launch(intent)
                                            }
                                        )
                                    }

                                    else -> {
                                        if (analysisResult.value.isLoading) {
                                            Text(
                                                text = buildAnnotatedString {
                                                    withStyle(SpanStyle(
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = whiteColor,
                                                    )) {
                                                        append("hang tight – your speech results are coming\nup...")
                                                    }
                                                },
                                                style = CustomTextStyle.copy(
                                                    lineHeight = 40.sp
                                                ),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                            )

                                            Loading(
                                                modifier = Modifier
                                                    .padding(top = 50.dp)
                                            )

                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(78.dp)
                                            )
                                        }

                                        if (recordingState != RecordingState.STOPPED) {
                                            Text(
                                                text = buildAnnotatedString {
                                                    withStyle(SpanStyle(
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Light,
                                                        color = whiteColor,
                                                    )) {
                                                        append("let’s get to know your speech! Tell us:\n")
                                                    }
                                                    withStyle(SpanStyle(
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = whiteColor,
                                                    )) {
                                                        append("what do you like about yourself, and where \ndo you stay ?")
                                                    }
                                                },
                                                style = CustomTextStyle.copy(
                                                    lineHeight = 40.sp
                                                ),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }


            if (permissionState != AudioPermissionState.Denied && recordingState != RecordingState.STOPPED) {
                RecordingSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 100.dp)
                        .align(Alignment.BottomCenter),
                    recordingState = recordingState,
                    onPausePlayClick = {
                        if (recordingState == RecordingState.PLAYING) {
                            audioRecorder.pause()
                        } else if (recordingState == RecordingState.PAUSED) {
                            audioRecorder.resume()
                        }
                    },
                    onCancelClick = {
                        audioRecorder.cancel()
                    },
                    onRecordingClick = {
                        if (recordingState == RecordingState.IDLE && !permissionRequested) {
                            permissionRequested = true
                            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        } else {
                            when (recordingState) {
                                RecordingState.IDLE -> {
                                    File.createTempFile("temp_audio", ".mp3", context.cacheDir,)
                                        .also { file ->
                                            audioRecorder.start(file)
                                            audioFile = file
                                        }
                                }

                                else -> {
                                    audioRecorder.stop()
                                    audioFile?.let {
                                        audioViewModel.analyseAudio(it)
                                    }
                                }
                            }
                        }
                    }
                )
            }

            Box(
                modifier = Modifier
                    .padding(bottom = 30.dp)
                    .align(Alignment.BottomCenter)
            ) {
                if (analysisResult.value.response != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        OnboardingFooter(
                            onSignInClick = {
                                scope.launch {
                                    authViewModel.loadingState()
                                    val signInIntentSender = googleAuthUiClient.signIn()
                                    launcher.launch(
                                        IntentSenderRequest.Builder(
                                            signInIntentSender ?: return@launch
                                        ).build()
                                    )
                                }
                            },
                            onSkipClick = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.OnboardingRecording.route) {
                                        inclusive = true
                                    }
                                }
                            },
                            modifier = Modifier
                                .padding(top = 10.dp),
                            isLoading = state.isLoading
                        )
                    }
                } else {
                    SmallBadge(
                        backgroundColor = secondaryColor,
                        borderColor = tertiaryColor,
                        text = "skip to the app",
                        icon = Icons.Default.KeyboardArrowRight,
                        modifier = Modifier
                            .graphicsLayer {
                                alpha = skipAlpha.value
                            }
                            .bounceClick {
                                // skip to the app
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.OnboardingRecording.route) {
                                        inclusive = true
                                    }
                                }
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingFooter(
    isLoading: Boolean,
    onSignInClick: () -> Unit,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        backgroundColor,
                        backgroundColor,
                        backgroundColor,
                        backgroundColor,
                        backgroundColor,
                    )
                )
            )
            .padding(top = 50.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        CustomButton(
            text = "sign in to get started",
            isLoading = isLoading,
            icon = Icons.Default.ArrowForward,
            onClick = {
                onSignInClick()
            },
            modifier = Modifier
                .padding(horizontal = 28.dp),
            startComposable = {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = whiteColor,
                            shape = CircleShape
                        )
                        .padding(4.dp)
                ) {
                    Image(
                        painterResource(R.drawable.google),
                        contentDescription = null,
                    )
                }
            }
        )
        SecondaryButton(
            text = "skip to app",
            icon = Icons.Default.ArrowForward,
            onClick = {
                onSkipClick()
            },
            modifier = Modifier
                .padding(horizontal = 28.dp)
        )
        Text(
            text = "we’re committed to keeping your data safe",
            style = CustomTextStyle.copy(
                color = subtitleTextColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
fun SuccessScreen(
    screenData: AnalysisScreenData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
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

@Composable
fun PropertiesSection(
    title: AnnotatedString,
    propertiesModel: List<PropertyUiModel>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            style = CustomTextStyle.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            modifier = Modifier
        )

        Column(
            modifier = Modifier
                .padding(top = 26.dp)
                .fillMaxWidth()
                .background(
                    color = secondaryColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 1.dp,
                    color = tertiaryColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 20.dp, vertical = 4.dp)
        ) {
            propertiesModel.forEachIndexed { index, item ->
                PropertiesItemContent(
                    propertiesModel = item,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                )

                if (index != propertiesModel.lastIndex) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = tertiaryColor
                    )
                }
            }
        }
    }
}

@Composable
fun PropertiesItemContent(
    propertiesModel: PropertyUiModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = propertiesModel.title,
            style = CustomTextStyle.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = whiteColor
            ),
            modifier = Modifier
                .weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${propertiesModel.propertiesModel.score} %",
            style = CustomTextStyle.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = whiteColor,
                fontStyle = FontStyle.Italic
            ),
            modifier = Modifier
                .padding(start = 10.dp)
        )
        StateTag(
            tag = propertiesModel.tag,
            modifier = Modifier
                .padding(start = 14.dp)
        )
    }
}

@Composable
fun RecordingSection(
    onPausePlayClick: () -> Unit,
    onRecordingClick: () -> Unit,
    onCancelClick: () -> Unit,
    recordingState: RecordingState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        RecordingButtons(
            onRecordingClick = onRecordingClick,
            onPausePlayClick = onPausePlayClick,
            recordingState = recordingState,
            onCancelClick = onCancelClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp)
                .align(Alignment.CenterHorizontally)
                .padding(top = 60.dp)
        )
    }
}

@Composable
fun RecordingButtons(
    onPausePlayClick: () -> Unit,
    onRecordingClick: () -> Unit,
    onCancelClick: () -> Unit,
    recordingState: RecordingState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
    ) {
        if (recordingState == RecordingState.PLAYING || recordingState == RecordingState.PAUSED) {
            SmallCircleButton(
                iconSize = if (recordingState == RecordingState.PLAYING) 20.dp else 30.dp,
                icon = if (recordingState == RecordingState.PLAYING) ImageVector.vectorResource(R.drawable.pause) else Icons.Default.PlayArrow,
                modifier = Modifier
                    .align(Alignment.CenterStart),
                onClick = {
                    onPausePlayClick()
                }
            )
        }
        RecordingButton(
            state = recordingState,
            onClick = {
                onRecordingClick()
            },
            modifier = Modifier
                .align(Alignment.Center)
        )
        if (recordingState == RecordingState.PLAYING || recordingState == RecordingState.PAUSED) {
            SmallCircleButton(
                iconSize = if (recordingState == RecordingState.PLAYING) 20.dp else 30.dp,
                icon = Icons.Default.Close,
                modifier = Modifier
                    .align(Alignment.CenterEnd),
                onClick = {
                    onCancelClick()
                },
                bgColor = Color.Transparent,
                borderColor = tertiaryColor,
            )
        }
    }
}

@Composable
fun TimerContent(
    timer: Long,
    modifier: Modifier = Modifier
) {
    Text(
        text = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    color = whiteColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            ) {
                append(formatTime(timer))
            }
            withStyle(
                SpanStyle(
                    color = subtitleTextColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            ) {
                append("  /  01:00")
            }
        },
        style = CustomTextStyle.copy(
            color = whiteColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 30.dp)
    )
}

@Composable
fun ColumnScope.AudioPermissionDeniedContent(
    onClick: () -> Unit,
) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = whiteColor,
            )) {
                append("your mic is only used to record your speech \nfor analysis.")
            }
            withStyle(SpanStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = whiteColor,
            )) {
                append("\nyour privacy is our priority. \uD83D\uDD12")
            }
        },
        style = CustomTextStyle.copy(
            lineHeight = 40.sp
        ),
        modifier = Modifier
            .fillMaxWidth()
    )

    MediumBadge(
        text = "allow microphone access",
        icon = Icons.Default.KeyboardArrowRight,
        backgroundColor = lightRedColor,
        modifier = Modifier
            .padding(top = 60.dp)
            .bounceClick {
                onClick()
            }
    )
}