package com.speechai.speechai.audio

import java.io.File

interface AudioRecorder {
    fun start(outputFile: File)
    fun stop()
    fun pause()
    fun resume()
}