package com.speechai.speechai.data.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class AudioAnalyseModel(
    @SerializedName("confidence") val confidence: PropertiesModel? = null,
    @SerializedName("pronunciation") val pronunciation: PropertiesModel? = null,
    @SerializedName("speaking_rate") val speakingRate: PropertiesModel? = null,
    @SerializedName("mumble") val mumble: PropertiesModel? = null,
    @SerializedName("grammar_accuracy") val grammarAccuracy: PropertiesModel? = null,
    @SerializedName("fluency") val fluency: PropertiesModel? = null,
    @SerializedName("filler_words") val fillerWords: PropertiesModel? = null,
): Parcelable

@Keep
@Parcelize
data class PropertiesModel(
    @SerializedName("score") val score: Int? = null,
    @SerializedName("did_mumble") val didMumble: Boolean? = null,
    @SerializedName("has_filler_words") val hasFillerWords: Boolean? = null,
    @SerializedName("words_per_minute") val wordsPerMinute: Int? = null,
    @SerializedName("reason_for_score") val reasonForScore: String? = null,
    @SerializedName("examples") val examples: List<String>? = null,
): Parcelable
