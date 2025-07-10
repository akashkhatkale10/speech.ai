package com.speechai.speechai.audio.player

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.core.net.toUri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class AndroidAudioPlayer(
    private val context: Context
): AudioPlayer {

    private val _timerState = MutableStateFlow(0L)
    val timerState: StateFlow<Long> = _timerState.asStateFlow()

    private var player: MediaPlayer? = null

    override fun setFile(file: File) {
        MediaPlayer.create(context, file.toUri()).apply {
            player = this
        }
    }

    override fun stop() {
        player?.stop()
        player?.release()
        player = null
    }

    override fun pause() {
        player?.pause()
    }

    override fun play() {
        player?.start()
    }
}