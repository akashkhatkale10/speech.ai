package com.speechai.speechai.screens.onboarding

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.speechai.speechai.lightGreenColor
import com.speechai.speechai.lightRedColor
import com.speechai.speechai.lightYellowColor
import com.speechai.speechai.whiteColor

val firstBenefit = buildAnnotatedString {
    withStyle(
        SpanStyle(
            color = whiteColor,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    ) {
        append("become a more ")
    }
    withStyle(
        SpanStyle(
            color = whiteColor,
            background = lightGreenColor,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp /*, background = ... won't work here*/
        )
    ) {
        append("  confident,  clear, and fluent  ")
    }
    withStyle(
        SpanStyle(
            color = whiteColor,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    ) {
        append(" speaker.  \uD83C\uDFA4")
    }
}

val secondBenefit = buildAnnotatedString {
    withStyle(
        SpanStyle(
            color = whiteColor,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    ) {
        append("Get ")
    }
    withStyle(
        SpanStyle(
            color = whiteColor,
            background = lightYellowColor,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp /*, background = ... won't work here*/
        )
    ) {
        append("  personalized insights  ")
    }
    withStyle(
        SpanStyle(
            color = whiteColor,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    ) {
        append("  on your pace, tone, and speaking habits. \uD83D\uDCC8")
    }
}

val thirdBenefit = buildAnnotatedString {
    withStyle(
        SpanStyle(
            color = whiteColor,
            background = lightRedColor,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp /*, background = ... won't work here*/
        )
    ) {
        append("  Spot your 'um's, 'uh's, and mumbling  ")
    }
    withStyle(
        SpanStyle(
            color = whiteColor,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    ) {
        append("  and learn how to fix them. \uD83D\uDDE3\uFE0F")
    }
}

val fourthBenefit = buildAnnotatedString {
    withStyle(
        SpanStyle(
            color = whiteColor,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp /*, background = ... won't work here*/
        )
    ) {
        append("Ready to start your journey to powerful communication? Let’s go!")
    }
}

val benefits = listOf(
    firstBenefit, secondBenefit, thirdBenefit, fourthBenefit,
)