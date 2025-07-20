package com.speechai.speechai.audio.player

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import com.speechai.speechai.composables.RecordingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class AndroidAudioPlayer(
    private val context: Context,
    private val file: File
): AudioPlayer {

    private val _timerState = MutableStateFlow(0L)
    val timerState: StateFlow<Long> = _timerState.asStateFlow()

    private var player: MediaPlayer? = null

    var recordingState by mutableStateOf(RecordingState.IDLE)
    var duration by mutableStateOf(1L)

    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)


    override fun initialise() {
        MediaPlayer.create(context, file.toUri()).apply {
            _timerState.value = 0L
            player = this
            this@AndroidAudioPlayer.duration = (player?.duration?.toLong() ?: 0L) / 1000

            setOnCompletionListener {
                recordingState = RecordingState.STOPPED
                stopTimer()
            }
        }
    }
    override fun seekTo(duration: Long) {
        player?.seekTo(duration.toInt())
    }

    override fun stop() {
        recordingState = RecordingState.STOPPED
        stopTimer()
        player?.stop()
        player?.release()
        player = null
    }

    override fun pause() {
        recordingState = RecordingState.PAUSED
        stopTimer()
        player?.pause()
    }

    override fun play() {
        if (player == null) {
            initialise()
        }
        recordingState = RecordingState.PLAYING
        player?.start()
        startTimer()
    }

    private fun startTimer() {
        if (timerJob?.isActive == true) return
        timerJob = scope.launch {
            while (recordingState == RecordingState.PLAYING) {
                delay(1000L)
                _timerState.value++
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }
}