package com.speechai.speechai.data.models

import androidx.annotation.Keep

@Keep
data class DetailedAudioAnalysisModel(
    val data: AudioAnalyseModel? = null,
    val id: String? = null,
    val userId: String? = null,
    val downloadUrl: String? = null,
    val timestamp: Long? = null,
    val totalScore: Int? = null,
    val audioMetadata: AudioMetadata? = null
)

@Keep
data class AudioMetadata(
    val sizeBytes: Long? = null,
    val contentType: String? = null,
    val name: String? = null,
    val updatedTimeMillis: Long? = null,
    val durationMillis: Long? = null
)
