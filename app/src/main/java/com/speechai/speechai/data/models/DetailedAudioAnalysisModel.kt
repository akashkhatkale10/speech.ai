package com.speechai.speechai.data.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DetailedAudioAnalysisModel(
    @SerializedName("data") val data: AudioAnalyseModel? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("download_url") val downloadUrl: String? = null,
)
