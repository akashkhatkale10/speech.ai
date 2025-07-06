package com.speechai.speechai.audio

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.speechai.speechai.data.models.AudioAnalyseModel
import com.speechai.speechai.greenColor
import com.speechai.speechai.models.AnalysisScreenData
import com.speechai.speechai.models.PropertyUiModel
import com.speechai.speechai.models.StateTag
import com.speechai.speechai.redColor
import com.speechai.speechai.yellowColor
import java.util.UUID
import kotlin.math.roundToInt

object AudioUtils {
    fun buildResponse(content: AudioAnalyseModel?): AnalysisScreenData {
        val bestScore = mutableListOf<PropertyUiModel>()
        val worstScore = mutableListOf<PropertyUiModel>()
        val others = mutableListOf<PropertyUiModel>()
        var totalScore = 0
        var totalItems = 0

        content?.confidence?.let {
            val model = PropertyUiModel(
                propertiesModel = it,
                tag = getTag(it.score),
                title = "confidence"
            )
            if (it.score == null) {
                others.add(model)
            } else if (isExcellent(it.score)) {
                totalScore += it.score
                totalItems += 1
                bestScore.add(model)
            } else {
                worstScore.add(model)
                totalScore += it.score
                totalItems += 1
            }
        }
        content?.pronunciation?.let {
            val model = PropertyUiModel(
                propertiesModel = it,
                tag = getTag(it.score),
                title = "pronunciation"
            )
            if (it.score == null) {
                others.add(model)
            } else if (isExcellent(it.score)) {
                totalScore += it.score
                totalItems += 1
                bestScore.add(model)
            } else {
                worstScore.add(model)
                totalScore += it.score
                totalItems += 1
            }
        }
        content?.fillerWords?.let {
            val model = PropertyUiModel(
                propertiesModel = it,
                tag = getTag(it.score),
                title = "filler words"
            )
            if (it.score == null) {
                others.add(model)
            } else if (isExcellent(it.score)) {
                totalScore += it.score
                totalItems += 1
                bestScore.add(model)
            } else {
                worstScore.add(model)
                totalScore += it.score
                totalItems += 1
            }
        }
        content?.speakingRate?.let {
            val model = PropertyUiModel(
                propertiesModel = it,
                tag = getTag(it.score),
                title = "speaking rate"
            )
            if (it.score == null) {
                others.add(model)
            } else if (isExcellent(it.score)) {
                totalScore += it.score
                totalItems += 1
                bestScore.add(model)
            } else {
                worstScore.add(model)
                totalScore += it.score
                totalItems += 1
            }
        }
        content?.mumble?.let {
            val model = PropertyUiModel(
                propertiesModel = it,
                tag = getTag(it.score),
                title = "mumble"
            )
            if (it.score == null) {
                others.add(model)
            } else if (isExcellent(it.score)) {
                totalScore += it.score
                totalItems += 1
                bestScore.add(model)
            } else {
                worstScore.add(model)
                totalScore += it.score
                totalItems += 1
            }
        }
        content?.fluency?.let {
            val model = PropertyUiModel(
                propertiesModel = it,
                tag = getTag(it.score),
                title = "fluency"
            )
            if (it.score == null) {
                others.add(model)
            } else if (isExcellent(it.score)) {
                totalScore += it.score
                totalItems += 1
                bestScore.add(model)
            } else {
                worstScore.add(model)
                totalScore += it.score
                totalItems += 1
            }
        }
        content?.grammarAccuracy?.let {
            val model = PropertyUiModel(
                propertiesModel = it,
                tag = getTag(it.score),
                title = "grammar accuracy"
            )
            if (it.score == null) {
                others.add(model)
            } else if (isExcellent(it.score)) {
                totalScore += it.score
                totalItems += 1
                bestScore.add(model)
            } else {
                worstScore.add(model)
                totalScore += it.score
                totalItems += 1
            }
        }


        val score = (totalScore / totalItems.toFloat()).roundToInt()
        Log.d("AKASH_LOG", "buildResponse: scoree $score")
        return AnalysisScreenData(
            id = UUID.randomUUID().toString(),
            response = content,
            totalScore = score,
            bestScore = bestScore,
            worstScore = worstScore,
            otherScore = others,
            totalScoreColor = getTotalScoreColor(score)
        )
    }

    fun getTotalScoreColor(score: Int?): Color {
        if (score == null) return Color.Unspecified

        return if (isExcellent(score)) {
            greenColor
        } else if (isExcellent(score)) {
            yellowColor
        } else {
            redColor
        }
    }

    fun getTag(score: Int?): StateTag {
        if (score == null) return StateTag.OTHER

        return if (isExcellent(score)) {
            StateTag.EXCELLENT
        } else if (isFair(score)) {
            StateTag.FAIR
        } else {
            StateTag.BAD
        }
    }

    fun isExcellent(score: Int) = score > 89
    fun isFair(score: Int) = score > 74
    fun isBad(score: Int) = score < 75
}