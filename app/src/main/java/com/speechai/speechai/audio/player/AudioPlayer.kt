package com.speechai.speechai.audio.player

import java.io.File

interface AudioPlayer {
    fun initialise()
    fun stop()
    fun pause()
    fun play()
    fun seekTo(duration: Long)
}