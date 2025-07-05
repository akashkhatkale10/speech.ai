package com.speechai.speechai.composables

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Shader
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.FloatRange
import com.speechai.speechai.R
import kotlin.math.round

data class Point(
    var x: Float,
    var y: Float
)
class WavePaintConfig(context: Context, attrs: AttributeSet?) {

    var middleColor = 0
        set(value) {
            field = value
            paintWave.color = value
        }
    var startColor = 0
    var endColor = 0

    @FloatRange(from = 0.0, to = 0.5)
    var colorGradientPositionOffset = DEFAULT_GRADIENT_POSITION_OFFSET
    var thickness = DEFAULT_THICKNESS
    var thicknessMiddle = DEFAULT_THICKNESS_MIDDLE
    var paintWave = Paint()
        private set

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.VoiceWave, 0, 0)
        if (attrs != null) {
            thickness = a.getFloat(R.styleable.VoiceWave_lineThickness, DEFAULT_THICKNESS)
            thicknessMiddle = a.getFloat(R.styleable.VoiceWave_middleLineThickness, DEFAULT_THICKNESS_MIDDLE)
            middleColor = a.getColor(R.styleable.VoiceWave_middleColor, Color.parseColor("#691A40"))
            startColor = a.getColor(R.styleable.VoiceWave_startColor, Color.parseColor("#93278F"))
            endColor = a.getColor(R.styleable.VoiceWave_endColor, Color.parseColor("#00A99D"))
            colorGradientPositionOffset = a.getFloat(R.styleable.VoiceWave_gradientOffset, DEFAULT_GRADIENT_POSITION_OFFSET)
            a.recycle()
            updatePaint()
        }
    }

    fun updatePaint() {
        paintWave = Paint()
        paintWave.strokeWidth = thickness
        paintWave.isAntiAlias = true
        paintWave.style = Paint.Style.STROKE
        paintWave.color = middleColor
        paintWave.alpha = 255
    }

    fun setMainLine(isMainLine: Boolean, view: View) {
        if (isMainLine) {
            setGradients(view)
        } else {
            paintWave.shader = null
        }
        paintWave.strokeWidth = (if (isMainLine) thickness else thicknessMiddle).toFloat()
    }

    private fun setGradients(view: View) {
        paintWave.shader = LinearGradient(
            0f, 0f, 0f, view.height.toFloat(),
            arrayOf(startColor, endColor, startColor).toIntArray(),
            arrayOf(
                GRADIENT_POSITION_MIDDLE - colorGradientPositionOffset,
                GRADIENT_POSITION_MIDDLE,
                GRADIENT_POSITION_MIDDLE + colorGradientPositionOffset
            ).toFloatArray(),
            Shader.TileMode.MIRROR
        )
    }

    companion object {
        private const val GRADIENT_POSITION_MIDDLE = 0.5f
        private const val DEFAULT_GRADIENT_POSITION_OFFSET = 0.1f
        private const val DEFAULT_THICKNESS = 6f
        private const val DEFAULT_THICKNESS_MIDDLE = 3f
    }
}
enum class AnimationSpeed {
    SLOW, NORMAL, FAST
}

class SpeechWavesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var rawAudioBytes: ByteArray? = null
    private var pointCount: Int = EXTREMUM_NUMBER_MAX
    private var points: Array<Point> = Array(pointCount + 1) { Point(0f, 0f) }
    private var bezierControlStartPoints: Array<Point> = Array(pointCount + 1) { Point(0f, 0f) }
    private var bezierControlEndPoints: Array<Point> = Array(pointCount + 1) { Point(0f, 0f) }
    private var prevY: FloatArray = FloatArray(pointCount + 1)
    private var currentY: FloatArray = FloatArray(pointCount + 1)
    private var maxBatchCount = MAX_ANIM_BATCH_COUNT
    private var batchCount = 0
    private val rect = Rect()
    private var widthOffset = -1f

    @FloatRange(from = 0.0,to = 0.5)
    var windowPadding = DEFAULT_WINDOW_PADDING
    var wavePaintConfig: WavePaintConfig = WavePaintConfig(context, attrs)
        set(value) {
            field = value
            invalidate()
        }

    private var pathList: Array<Path> = emptyArray()
    private var linesOffset = 1f
    var pathCount = DEFAULT_PATH_COUNT
        set(value) {
            field = value
            pathList = Array(value) { Path() }
            linesOffset = if (value == 1) 1f else 2f / (value - 1)
        }
    @FloatRange(from = 0.1,to = 1.0)
    var density = DEFAULT_DENSITY
        set(value) {
            field = value
            pointCount = (EXTREMUM_NUMBER_MAX * field).toInt()
            if (pointCount < EXTREMUM_NUMBER_MIN) pointCount = EXTREMUM_NUMBER_MIN
            createArraysIfChanged()
            widthOffset = -1f
        }
    var speed: AnimationSpeed = AnimationSpeed.NORMAL
        set(value) {
            field = value
            maxBatchCount = MAX_ANIM_BATCH_COUNT - field.ordinal
        }

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.VoiceWave, 0, 0)
        if (attrs != null) {
            density = a.getFloat(R.styleable.VoiceWave_density, DEFAULT_DENSITY)
            speed = a.getColor(R.styleable.VoiceWave_waveSpeed, AnimationSpeed.NORMAL.ordinal).toAnimationSpeed()
            pathCount = a.getColor(R.styleable.VoiceWave_lineCount, DEFAULT_PATH_COUNT)
            windowPadding = a.getFloat(R.styleable.VoiceWave_windowPadding, DEFAULT_WINDOW_PADDING)
            a.recycle()
        }
        createArraysIfChanged()
    }

    fun update(bytes: ByteArray?) {
        updateRawByteArray(bytes)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val bytes = rawAudioBytes ?: return

        rect.set(0, 0, width, height)

        initializeBezierPoints()
        findDestinationBezierPointForBatch(bytes)
        smoothAnimation()
        calculateBezierCurveControlPoints()

        wavePaintConfig.updatePaint()

        drawPath(canvas)
    }

    private fun updateRawByteArray(bytes: ByteArray?) {
        if (bytes != null && bytes.all { it.toInt() == 0 }) {
            this.rawAudioBytes = ByteArray(bytes.size) { -128 }
        } else {
            this.rawAudioBytes = bytes
        }
    }

    private fun createArraysIfChanged() {
        points = Array(pointCount + 1) { Point(0f, 0f) }
        bezierControlStartPoints = Array(pointCount + 1) { Point(0f, 0f) }
        bezierControlEndPoints = Array(pointCount + 1) { Point(0f, 0f) }
        prevY = FloatArray(pointCount + 1)
        currentY = FloatArray(pointCount + 1)
    }

    private fun initializeBezierPoints() {
        val heightCenter = rect.height() / 2
        if (widthOffset == -1f) {
            widthOffset = (rect.width() / pointCount).toFloat()

            for (i in points.indices) {
                val posX = rect.left + i * widthOffset
                val posY = heightCenter.toFloat()
                prevY[i] = posY
                currentY[i] = posY
                points[i].x = posX
                points[i].y = posY
            }
        }
    }

    private fun findDestinationBezierPointForBatch(rawAudioBytes: ByteArray) {
        val heightCenter = rect.height() / 2f
        val paddingHorizontal = AXIS_X_WIDTH * windowPadding
        if (batchCount == 0) {
            val lastPosY = currentY.last()
            for (i in points.indices) {
                val x = round(i * (rawAudioBytes.size / pointCount.toFloat())).toInt()
                val posY = if (x > paddingHorizontal && x < AXIS_X_WIDTH - paddingHorizontal) {
                    heightCenter + (rawAudioBytes[x] + BYTE_SIZE).toByte() * heightCenter / BYTE_SIZE
                } else {
                    heightCenter
                }

                prevY[i] = currentY[i]
                currentY[i] = posY
            }
            currentY[points.size - 1] = lastPosY
        }
    }

    private fun smoothAnimation() {
        batchCount++

        for (i in points.indices) {
            points[i].y = prevY[i] + batchCount.toFloat() / maxBatchCount * (currentY[i] - prevY[i])
        }

        if (batchCount == maxBatchCount) batchCount = 0
    }

    private fun calculateBezierCurveControlPoints() {
        for (i in 1 until points.size) {
            val bezierControlX = (points[i].x + points[i - 1].x) / 2
            bezierControlStartPoints[i].x = bezierControlX
            bezierControlStartPoints[i].y = points[i - 1].y
            bezierControlEndPoints[i].x = bezierControlX
            bezierControlEndPoints[i].y = points[i].y
        }
    }

    private fun drawPath(canvas: Canvas) {
        pathList.forEachIndexed { index, path ->
            path.rewind()

            val coefficient = 1 - index * linesOffset
            path.moveTo(points[0].x, getRelativeY(points[0].y, coefficient))
            for (i in 1 until points.size) {
                path.cubicTo(
                    bezierControlStartPoints[i].x,
                    getRelativeY(bezierControlStartPoints[i].y, coefficient),
                    bezierControlEndPoints[i].x,
                    getRelativeY(bezierControlEndPoints[i].y, coefficient),
                    points[i].x,
                    getRelativeY(points[i].y, coefficient)
                )
            }

            val isMainLine = pathList.firstOrLast(index)
            wavePaintConfig.setMainLine(isMainLine, this)

            canvas.drawPath(path, wavePaintConfig.paintWave)
        }
    }

    private fun getRelativeY(y: Float, coefficient: Float): Float {
        val heightCenter = rect.height() / 2
        val diff = y - heightCenter
        return heightCenter + diff * coefficient
    }

    companion object {
        private const val EXTREMUM_NUMBER_MAX = 54
        private const val EXTREMUM_NUMBER_MIN = 3
        private const val DEFAULT_DENSITY = 0.2f
        private const val MAX_ANIM_BATCH_COUNT = 4
        private const val AXIS_X_WIDTH = 1024
        private const val DEFAULT_WINDOW_PADDING = 0.24f
        private const val DEFAULT_PATH_COUNT = 4
        private const val BYTE_SIZE = 128
    }
}

fun <T> Array<T>.firstOrLast(index: Int): Boolean = index == 0 || index == size - 1

fun Int.toAnimationSpeed(default: AnimationSpeed = AnimationSpeed.NORMAL): AnimationSpeed {
    return AnimationSpeed.values().find { it.ordinal == this } ?: default
}