package com.speechai.speechai.screens.analyse

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.speechai.speechai.CustomTextStyle
import com.speechai.speechai.R
import com.speechai.speechai.audio.AndroidAudioRecorder
import com.speechai.speechai.audio.AudioViewModel
import com.speechai.speechai.backgroundColor
import com.speechai.speechai.composables.CustomTopBar
import com.speechai.speechai.composables.Loading
import com.speechai.speechai.composables.ProgressBar
import com.speechai.speechai.composables.RecordingState
import com.speechai.speechai.models.AnalysisScreenData
import com.speechai.speechai.screens.onboarding.AudioPermissionState
import com.speechai.speechai.screens.onboarding.RecordingSection
import com.speechai.speechai.screens.onboarding.TimerContent
import com.speechai.speechai.secondaryColor
import com.speechai.speechai.utils.getAudioFileFromAssets
import com.speechai.speechai.whiteColor
import java.io.File

@Composable
fun AnalyseScreen(
    navController: NavController,
    recordingFinished: (analysis: AnalysisScreenData) -> Unit,
    isRecording: (isRecording: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val audioViewModel = hiltViewModel<AudioViewModel>()
    val analysisResult = audioViewModel.analysisResult.collectAsState()

    val audioRecorder = remember { AndroidAudioRecorder(context) }
    var recordingState = audioRecorder.recordingState
    var audioFile: File? by remember {
        mutableStateOf(null)
    }
    var permissionState by remember { mutableStateOf<AudioPermissionState>(AudioPermissionState.NeverAsked) }
    var permissionRequested by remember { mutableStateOf(
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    ) }



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
            isRecording(true)
        } else {
            val intent =
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data =
                        Uri.fromParts("package", context.packageName, null)
                }
            settingsLauncher.launch(intent)
        }
    }
    val timer = audioRecorder.timerState.collectAsState()
    val progress = animateFloatAsState(
        targetValue = timer.value / 60f,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
    )

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        permissionState = if (granted) AudioPermissionState.Granted else AudioPermissionState.NeverAsked
    }


    LaunchedEffect(analysisResult.value) {
        analysisResult.value.response?.let {
            recordingFinished(analysisResult.value.response!!)
        }
    }


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column {
            CustomTopBar(
                modifier = Modifier
                    .padding(horizontal = 20.dp),
                startComposable = {
                    if (recordingState == RecordingState.IDLE) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(
                                    color = secondaryColor,
                                    shape = CircleShape
                                )
                                .padding(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = whiteColor,
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }
                    }
                },
                endComposable = {
                    if (recordingState == RecordingState.IDLE) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFEDDD53),
                                            Color(0xFFFFA12D),
                                        )
                                    ),
                                    shape = CircleShape
                                )
                                .padding(8.dp)
                        ) {
                            Icon(
                                painterResource(R.drawable.crown),
                                contentDescription = null,
                                tint = whiteColor,
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }
                    }
                },
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

            TimerContent(
                timer = timer.value,
                modifier = Modifier
                    .padding(top = 100.dp)
            )

            ProgressBar(
                progress = { progress.value },
                modifier = Modifier
                    .padding(top = 70.dp)
            )


            if (recordingState == RecordingState.IDLE) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Light,
                                color = whiteColor,
                            )
                        ) {
                            append("ready to speak ?\n")
                        }
                        withStyle(
                            SpanStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = whiteColor,
                            )
                        ) {
                            append("let’s analyze your speech together !")
                        }
                    },
                    style = CustomTextStyle.copy(
                        lineHeight = 40.sp
                    ),
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 80.dp)
                        .fillMaxWidth()
                )
            } else if (recordingState == RecordingState.PLAYING || recordingState == RecordingState.PAUSED) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = whiteColor,
                            )
                        ) {
                            append("we are analysing your speech.....")
                        }
                    },
                    style = CustomTextStyle.copy(
                        lineHeight = 40.sp
                    ),
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 80.dp)
                        .fillMaxWidth()
                )
            } else if (analysisResult.value.isLoading) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 80.dp)
                        .fillMaxWidth()
                ) {
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
                }
            }
        }

        if (recordingState != RecordingState.STOPPED) {
            RecordingSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 50.dp)
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
                    isRecording(false)
                    audioRecorder.cancel()
                },
                onRecordingClick = {
                    if (permissionState == AudioPermissionState.Denied || permissionState == AudioPermissionState.NeverAsked) {
                        audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    } else if (permissionState == AudioPermissionState.Granted){
                        isRecording(true)
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
                                    // todo change this
                                    audioViewModel.analyseAudio(context.getAudioFileFromAssets("sample.mp3"))
                                }
                            }
                        }
                    }
                }
            )
        }

    }
}