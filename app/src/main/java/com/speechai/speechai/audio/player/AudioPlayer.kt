package com.speechai.speechai.audio.player

import java.io.File

interface AudioPlayer {
    fun setFile(file: File)
    fun stop()
    fun pause()
    fun play()
}