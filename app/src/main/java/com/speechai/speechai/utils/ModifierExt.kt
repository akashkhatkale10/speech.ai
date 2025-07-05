package com.speechai.speechai.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.roundToInt

fun Modifier.noInteractionClickable(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    onClick: () -> Unit
): Modifier =
    composed {
        clickable(
            indication = null,
            enabled = enabled,
            interactionSource = interactionSource ?: remember { MutableInteractionSource() }
        ) { onClick() }
    }

enum class ButtonState { Pressed, Idle }
fun Modifier.bounceClick(
    scaleDown: Float = 0.96f,
    enabled: Boolean = true,
    ignoreOffset: Boolean = true,
    onClick: () -> Unit,
) = composed {
    if (enabled.not()) return@composed Modifier

    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val scale by animateFloatAsState(if (buttonState == ButtonState.Pressed) scaleDown else 1f)
    val offsetY by animateOffsetAsState(if (buttonState == ButtonState.Pressed) Offset(0f, 14f) else Offset.Zero)
    val haptic = LocalHapticFeedback.current

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .then(
            if (ignoreOffset) Modifier else Modifier.offset {
                IntOffset(offsetY.x.roundToInt(), offsetY.y.roundToInt())
            }
        )
        .clickable(
            enabled = enabled,
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
        )
        .pointerInput(buttonState) {
            awaitPointerEventScope {
                buttonState = if (buttonState == ButtonState.Pressed) {
                    waitForUpOrCancellation()
                    ButtonState.Idle
                } else {
                    awaitFirstDown(false)
                    ButtonState.Pressed
                }
            }
        }
}

fun Modifier.bouncedOverscroll(
    mainScroll: ScrollableState,
    overscrollOffset: Animatable<Float, AnimationVector1D>? = null,
    orientation: Orientation = Orientation.Vertical,
    gestureThreshold: Int? = null,
    onGestureUp: () -> Unit = {  },
    onGestureDown: () -> Unit = {  }
) = composed {
    val overscrollOffset = overscrollOffset ?: remember { Animatable(0f) }

    graphicsLayer {
        if(orientation == Orientation.Vertical) translationY = overscrollOffset.value / exp(1f)
        else translationX = overscrollOffset.value / exp(1f)
    }.delegateOverscroll(mainScroll, overscrollOffset, orientation, gestureThreshold, onGestureUp, onGestureDown)
}
fun Modifier.delegateOverscroll(
    mainScroll: ScrollableState,
    overscrollOffset: Animatable<Float, AnimationVector1D>,
    orientation: Orientation = Orientation.Vertical,
    gestureThreshold: Int? = null,
    onGestureUp: () -> Unit = {  },
    onGestureDown: () -> Unit = {  }
) = composed {
    val scope = rememberCoroutineScope()

    nestedScroll(
        remember(
            mainScroll,
            overscrollOffset,
            orientation,
            gestureThreshold,
            onGestureDown,
            onGestureUp
        ) {
            nestedBouncedScrollConnection(
                mainScroll,
                overscrollOffset,
                orientation,
                scope,
                gestureThreshold,
                onGestureDown,
                onGestureUp
            )
        }
    )
}

private fun mainAxes(target: Offset, orientation: Orientation): Float {
    return if(orientation == Orientation.Vertical) target.y else target.x
}

private fun mainAxes(target: Velocity, orientation: Orientation): Float {
    return if(orientation == Orientation.Vertical) target.y else target.x
}

internal fun nestedBouncedScrollConnection(
    scrollState: ScrollableState,
    overscrollOffset: Animatable<Float, AnimationVector1D>,
    orientation: Orientation,
    scope: CoroutineScope,
    gestureThreshold: Int? = null,
    onGestureDown: () -> Unit = {  },
    onGestureUp: () -> Unit = {  },
): NestedScrollConnection {
    return object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val isRightAxis =
                if(available.x != 0f) orientation == Orientation.Horizontal
                else orientation == Orientation.Vertical

            if(isRightAxis && source == NestedScrollSource.UserInput) {
                if(overscrollOffset.targetValue != 0f) {
                    return if((overscrollOffset.value + mainAxes(available, orientation)) > 0 && !scrollState.canScrollBackward ||
                        (overscrollOffset.value + mainAxes(available, orientation)) < 0 && !scrollState.canScrollForward) {
                        scope.launch { overscrollOffset.snapTo((mainAxes(available, orientation) / exp(.5f)) + overscrollOffset.value) }
                        available
                    } else {
                        scope.launch { overscrollOffset.animateTo(0f, spring(1f, 1500f)) }
                        Offset.Zero
                    }
                }
            }

            return Offset.Zero
        }

        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
            val isRightAxis =
                if(available.x != 0f) orientation == Orientation.Horizontal
                else orientation == Orientation.Vertical

            return if(isRightAxis && source == NestedScrollSource.UserInput) {
                scope.launch { overscrollOffset.snapTo((mainAxes(available, orientation) / exp(.5f)) + overscrollOffset.value) }
                available
            } else {
                Offset.Zero
            }
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            val isRightAxis =
                if(available.x != 0f) orientation == Orientation.Horizontal
                else orientation == Orientation.Vertical

            if(isRightAxis) {
                if(overscrollOffset.targetValue != 0f) {
                    scope.launch { overscrollOffset.animateTo(0f, spring(1f, 200f), mainAxes(available, orientation) / 1.15f) }
                    return available.copy(y = overscrollOffset.value * -2 + 250f)
                }

                scope.launch { overscrollOffset.animateTo(0f, spring(1f, 1500f)) }
            }

            return Velocity.Zero
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            val isRightAxis =
                if(available.x != 0f) orientation == Orientation.Horizontal
                else orientation == Orientation.Vertical

            if(isRightAxis) {
                val velocity = mainAxes(available, orientation)

                scope.launch { overscrollOffset.animateTo(0f, spring(1f, 200f), velocity / 1.15f) }

                if(gestureThreshold != null) {
                    if (overscrollOffset.value > gestureThreshold && velocity > 0f)
                        onGestureDown()
                    else if(overscrollOffset.value < -gestureThreshold && velocity < 0f)
                        onGestureUp()
                } else {
                    if (
                        overscrollOffset.value > 300f && velocity > 100f ||
                        overscrollOffset.value > 200f && velocity > 2000f
                    )
                        onGestureDown()

                    else if (
                        overscrollOffset.value < -300f && velocity < -100f ||
                        overscrollOffset.value < -200f && velocity < -2000f
                    )
                        onGestureUp()
                }

                return available
            } else {
                return Velocity.Zero
            }
        }
    }
}