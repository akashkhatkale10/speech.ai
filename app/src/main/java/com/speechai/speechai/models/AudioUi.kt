package com.speechai.speechai.models

import androidx.annotation.Keep
import androidx.compose.ui.graphics.Color
import com.speechai.speechai.data.models.AudioAnalyseModel
import com.speechai.speechai.data.models.PropertiesModel

@Keep
data class AudioAnalyseState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val response: AnalysisScreenData? = null
)

@Keep
data class AnalysisScreenData(
    val id: String? = null,
    val response: AudioAnalyseModel?,
    val totalScore: Int,
    val bestScore: List<PropertyUiModel>,
    val worstScore: List<PropertyUiModel>,
    val otherScore: List<PropertyUiModel>,
    val totalScoreColor: Color,
)

@Keep
data class PropertyUiModel(
    val propertiesModel: PropertiesModel,
    val tag: StateTag,
    val title: String,
)