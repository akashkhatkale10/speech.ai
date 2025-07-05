package com.speechai.speechai.data.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AudioAnalyseModel(
    @SerializedName("confidence") val confidence: PropertiesModel?,
    @SerializedName("pronunciation") val pronunciation: PropertiesModel?,
    @SerializedName("speaking_rate") val speakingRate: PropertiesModel?,
    @SerializedName("mumble") val mumble: PropertiesModel?,
    @SerializedName("grammar_accuracy") val grammarAccuracy: PropertiesModel?,
    @SerializedName("fluency") val fluency: PropertiesModel?,
    @SerializedName("filler_words") val fillerWords: PropertiesModel?,
)

@Keep
data class PropertiesModel(
    @SerializedName("score") val score: Int?,
    @SerializedName("did_mumble") val didMumble: Boolean?,
    @SerializedName("has_filler_words") val hasFillerWords: Boolean?,
    @SerializedName("words_per_minute") val wordsPerMinute: Int?,
    @SerializedName("reason_for_score") val reasonForScore: String?,
    @SerializedName("examples") val examples: List<String>?,
)
