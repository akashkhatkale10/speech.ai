package com.speechai.speechai.screens.history

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.speechai.speechai.greenColor
import com.speechai.speechai.lightGreenColor
import com.speechai.speechai.models.StateTag
import com.speechai.speechai.whiteColor

object HistoryUtils {

    val emptyStates = listOf(
        HistoryEmptyStateModel(
            title = "every great speaker starts with their first word",
            subtitle = "record your first speech to begin your journey",
            buttonText = "record now",
            tag = StateTag.OTHER
        ),
        HistoryEmptyStateModel(
            title = "no excellent recordings yet — but progress takes time",
            subtitle = "keep practicing to reach top-level fluency",
            buttonText = "record now",
            tag = StateTag.EXCELLENT
        ),
        HistoryEmptyStateModel(
            title = "no fair scores yet — that's a good sign!",
            subtitle = "looks like you're skipping right past average!",
            buttonText = "record another one",
            tag = StateTag.FAIR
        ),
        HistoryEmptyStateModel(
            title = "no low-scoring speeches",
            subtitle = "let’s keep it that way!",
            buttonText = "keep practicing",
            tag = StateTag.BAD
        )
    )


    val menuItems = listOf(
        "all",
        "excellent",
        "fair",
        "bad"
    )

    val signInBenefits = listOf(
        buildAnnotatedString {
            withStyle(
                SpanStyle(
                    color = whiteColor,
                    background = greenColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp /*, background = ... won't work here*/
                )
            ) {
                append("save and compare")
            }
            withStyle(
                SpanStyle(
                    color = whiteColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            ) {
                append("  your speech scores\n\nto see how you're improving")
            }
        },

        buildAnnotatedString {
            withStyle(
                SpanStyle(
                    color = whiteColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            ) {
                append("get smarter, more tailored  ")
            }
            withStyle(
                SpanStyle(
                    color = whiteColor,
                    background = greenColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp /*, background = ... won't work here*/
                )
            ) {
                append("insights")
            }
            withStyle(
                SpanStyle(
                    color = whiteColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            ) {
                append("\n\nbased on your past performance")
            }
        },

        buildAnnotatedString {
            withStyle(
                SpanStyle(
                    color = whiteColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            ) {
                append("never lose your data. your recordings\n\n")
            }
            withStyle(
                SpanStyle(
                    color = whiteColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            ) {
                append("and feedbacks are  ")
            }
            withStyle(
                SpanStyle(
                    color = whiteColor,
                    background = greenColor,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp /*, background = ... won't work here*/
                )
            ) {
                append("  securely stored")
            }
        }
    )

}