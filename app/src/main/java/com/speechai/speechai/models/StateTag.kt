package com.speechai.speechai.models

import androidx.compose.ui.graphics.Color
import com.speechai.speechai.greenColor
import com.speechai.speechai.lightGreenColor
import com.speechai.speechai.lightRedColor
import com.speechai.speechai.lightYellowColor
import com.speechai.speechai.redColor
import com.speechai.speechai.yellowColor

enum class StateTag {
    EXCELLENT, FAIR, BAD, OTHER
}

data class StateTagModel(
    val title: String,
    val borderColor: Color,
    val bgColor: Color,
)

fun getStateTagModel(stateTag: StateTag): StateTagModel? {
    return when (stateTag) {
        StateTag.EXCELLENT -> StateTagModel(
            title = "excellent",
            bgColor = lightGreenColor,
            borderColor = greenColor
        )

        StateTag.FAIR -> StateTagModel(
            title = "fair",
            bgColor = lightYellowColor,
            borderColor = yellowColor
        )

        StateTag.BAD -> StateTagModel(
            title = "bad",
            bgColor = lightRedColor,
            borderColor = redColor
        )

        else -> null
    }
}