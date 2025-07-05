package com.speechai.speechai.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.speechai.speechai.redColor
import com.speechai.speechai.utils.bounceClick
import com.speechai.speechai.whiteColor
import java.nio.file.Files.size
import kotlin.math.sin

enum class RecordingState {
    PLAYING, IDLE, PAUSED, STOPPED
}
@Composable
fun RecordingButton(
    state: RecordingState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val animateDp by animateDpAsState(
        targetValue = if (state == RecordingState.IDLE) 100.dp else 6.dp,
        animationSpec = tween(
            durationMillis = 100
        )
    )
    val animateSize by animateDpAsState(
        targetValue = if (state == RecordingState.IDLE) 54.dp  else 34.dp,
        animationSpec = tween(
            durationMillis = 200
        )
    )

    Box(
        modifier = modifier
            .bounceClick(scaleDown = 0.93f) {
                onClick()
            }
            .size(78.dp)
            .background(
                color = if (state == RecordingState.IDLE) Color(0xff3E2F2F) else Color.Transparent,
                shape = RoundedCornerShape(100.dp)
            )
            .border(
                width = 1.dp,
                color = whiteColor.copy(
                    alpha = if (state == RecordingState.IDLE) 1f else 0.4f
                ),
                shape = RoundedCornerShape(100.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(animateSize)
                .background(
                    color = redColor,
                    shape = RoundedCornerShape(animateDp)
                )
        )
    }
}

@Composable
fun AnimatedSineWave(
    modifier: Modifier = Modifier,
    waveColor: Color = Color.Cyan,
    amplitude: Float = 40f,
    wavelength: Float = 200f,
    speed: Float = 200f // pixels per second
) {
    val infiniteTransition = rememberInfiniteTransition()
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (1000 * wavelength / speed).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val path = Path()

        val centerY = height / 2
        val animatedShift = phase * wavelength

        path.moveTo(0f, centerY)

        for (x in 0..width.toInt()) {
            val y = centerY + amplitude * sin((x + animatedShift) * (2 * Math.PI / wavelength)).toFloat()
            path.lineTo(x.toFloat(), y)
        }

        drawPath(
            path = path,
            color = waveColor,
            style = Stroke(width = 3f, cap = StrokeCap.Round)
        )
    }
}