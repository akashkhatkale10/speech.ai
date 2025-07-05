package com.speechai.speechai.composables

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.speechai.speechai.secondaryColor
import com.speechai.speechai.tertiaryColor
import com.speechai.speechai.whiteColor

@Composable
fun Loading(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val phase by infiniteTransition.animateFloat(
        initialValue = 9f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )


    val infiniteTransition2 = rememberInfiniteTransition()
    val phase2 by infiniteTransition2.animateFloat(
        initialValue = 12f,
        targetValue = 9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .size((phase).dp)
                .background(whiteColor, shape = CircleShape)
                .align(Alignment.CenterStart)
        )
        Box(
            modifier = Modifier
                .offset(x = 18.dp)
                .size(phase2.dp)
                .background(whiteColor, shape = CircleShape)
                .align(Alignment.CenterStart)
        )
    }
}