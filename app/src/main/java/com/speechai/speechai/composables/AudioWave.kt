package com.speechai.speechai.composables

import android.graphics.PathMeasure
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun AudioWave2(
    amplitude: Int,
    modifier: Modifier = Modifier,
    lineColor: Color = Color.Green,
    backgroundColor: Color = Color.Gray,
    numPoints: Int = 20,
    lineWidth: Float = 2f,
    lineHeight: Float = -90f // adjust height of wave
) {
    val maxAmplitude = 3000f // maximum from MediaRecorder.getMaxAmplitude()
    // Smoothly animate the wave's "head"
    val animatedAmp by animateFloatAsState(
        targetValue = (amplitude / maxAmplitude).coerceAtMost(maximumValue = 8000f),
        animationSpec = tween(durationMillis = 150)
    )

    // Ring buffer for recent amplitudes
    val points = remember { mutableStateListOf<Float>() }
    LaunchedEffect(animatedAmp) {
        if (points.size >= numPoints) points.removeAt(0)
        points.add(animatedAmp)
    }
    // Interpolate to ensure points stay up-to-date
    val displayedPoints = if (points.size < numPoints) {
        List(numPoints) { i -> points.getOrElse(i) { 0f } }
    } else {
        points.toList()
    }

    Box(
        modifier = modifier
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .drawWithCache {
                    val w = size.width
                    val h = size.height
                    val step = w / (numPoints - 1)
                    // Wave line: X is spread, Y is amplitude
                    val path = Path()
                    displayedPoints.forEachIndexed { i, amp ->
                        val x = i * step
                        // Center baseline; Y varies up/down by amp
                        val y = h / 2 + (amp - 0.5f) * lineHeight

                        if (i == 0) {
                            path.moveTo(x, y)
                        } else {
                            val px = (i - 1) * step
                            val py = h / 2 + ((displayedPoints[i - 1]) - 0.5f) * lineHeight

                            val cx1 = (px + x) / 2
                            val cx2 = (px + x) / 2

                            val cy1 = py
                            val cy2 = y

                            path.cubicTo(
                                cx1, cy1,
                                cx2, cy2,
                                x, y
                            )
//                            path.lineTo(x, y)
                        }
                    }
                    onDrawBehind {
                        drawPath(path, lineColor, style = Stroke(width = lineWidth.dp.toPx()))
                    }
                }
        )
    }
}


@Composable
fun AudioWave(
    modifier: Modifier = Modifier
) {
    val paths = remember {
        mutableStateListOf<Offset>(
            Offset(0f, 0f),
            Offset(50f, -20f),
            Offset(100f, -60f),
            Offset(250f, -20f),
            Offset(400f, -60f),
            Offset(600f, -100f),
            Offset(800f, -50f),
        )
    }
    val animationProgress = remember { androidx.compose.animation.core.Animatable(0f) }
    var duration by remember { mutableStateOf(60000) }

    LaunchedEffect(Unit) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(duration)
        )
    }

    LaunchedEffect(Unit) {
//        while (true) {
//            delay(200)
//            duration += 200
//            paths.add(Offset(900f, -20f))
//            delay(200)
//            duration += 200
//            paths.add(Offset(1000f, -100f))
//        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray)
            .height(200.dp)
    ) {
        Spacer(
            modifier = Modifier
                .background(Color.Gray)
                .align(Alignment.CenterStart)
                .drawWithCache {
                    val path = Path()
                    paths.forEachIndexed { index, offset ->
                        if (index > 0) {
                            val px = paths[index - 1].x
                            val py = paths[index - 1].y

                            val cx1 = (px + offset.x) / 2
                            val cx2 = (px + offset.x) / 2

                            val cy1 = py
                            val cy2 = offset.y

                            path.cubicTo(
                                cx1, cy1,
                                cx2, cy2,
                                offset.x, offset.y)
                        }
                    }

                    val pathMeasure = PathMeasure(path.asAndroidPath(), false)
                    val animationPath = android.graphics.Path()

                    pathMeasure.getSegment(
                        0f,
                        pathMeasure.length * animationProgress.value,
                        animationPath,
                        true
                    )

                    onDrawBehind {
                        drawPath(Path().apply { addPath(animationPath.asComposePath()) }, Color.Green, style = Stroke(width = 2.dp.toPx()))
                    }
                }
        )
    }
}

@Preview
@Composable
fun AudioWavePreview(modifier: Modifier = Modifier) {
    SmoothPathGrowingOverTime()
}

@Composable
fun SmoothPathGrowingOverTime(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val totalPoints = 31
    val interval = 2000L // 2 seconds

    val points = remember { mutableStateListOf<Float>() }
    val drawProgress = remember { Animatable(1f) }

    // Start with two points
    LaunchedEffect(Unit) {
        points.addAll(
            List(2) { Random.nextFloat().coerceIn(0.3f, 0.7f) }
        )

        repeat(totalPoints - 2) {
            //delay(interval)
            points.add(Random.nextFloat().coerceIn(0.3f, 0.7f))
            drawProgress.snapTo(0f)
            drawProgress.animateTo(
                1f,
                animationSpec = tween(durationMillis = interval.toInt(), easing = LinearOutSlowInEasing)
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        if (points.size < 2) return@Canvas

        val width = size.width
        val height = size.height
        val spacing = width / (totalPoints - 1)
        val midY = height / 2f

        val path = Path()

        // Draw full previous path
        for (i in 1 until points.size - 1) {
            val x0 = (i - 1) * spacing
            val y0 = midY + (points[i - 1] - 0.5f) * height * 0.6f

            val x1 = i * spacing
            val y1 = midY + (points[i] - 0.5f) * height * 0.6f

            val controlX = (x0 + x1) / 2

            if (i == 1) path.moveTo(x0, y0)

            path.cubicTo(controlX, y0, controlX, y1, x1, y1)
        }

        // Animate the newest segment (last 2 points)
        if (points.size >= 2) {
            val last = points.size - 1
            val x0 = (last - 1) * spacing
            val y0 = midY + (points[last - 1] - 0.5f) * height * 0.6f

            val x1 = last * spacing
            val y1 = midY + (points[last] - 0.5f) * height * 0.6f

            val controlX = (x0 + x1) / 2

            val steps = 20
            for (j in 1..(drawProgress.value * steps).toInt()) {
                val t = j / steps.toFloat()
                val oneMinusT = 1 - t

                // Cubic Bézier formula
                val xt = oneMinusT * oneMinusT * oneMinusT * x0 +
                        3 * oneMinusT * oneMinusT * t * controlX +
                        3 * oneMinusT * t * t * controlX +
                        t * t * t * x1

                val yt = oneMinusT * oneMinusT * oneMinusT * y0 +
                        3 * oneMinusT * oneMinusT * t * y0 +
                        3 * oneMinusT * t * t * y1 +
                        t * t * t * y1

                if (j == 1 && points.size == 2) path.moveTo(x0, y0)
                else path.lineTo(xt, yt)
            }
        }

        drawPath(
            path = path,
            color = Color(0xFFFF9800),
            style = Stroke(width = 5f, cap = StrokeCap.Round)
        )
    }
}


private fun generatePath(data: List<Offset>,): Path {
    val path = Path()
    data.forEachIndexed { index, offset ->
        if (index > 0) {
            val px = data[index - 1].x
            val py = data[index - 1].y

            val cx1 = (px + offset.x) / 2
            val cx2 = (px + offset.x) / 2

            val cy1 = py
            val cy2 = offset.y

            path.cubicTo(
                cx1, cy1,
                cx2, cy2,
                offset.x, offset.y)
        }
    }

    return path
}



