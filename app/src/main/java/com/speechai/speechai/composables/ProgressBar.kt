package com.speechai.speechai.composables

import android.widget.ProgressBar
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.speechai.speechai.goldenColor
import com.speechai.speechai.tertiaryColor
import com.speechai.speechai.whiteColor

@Composable
fun ProgressBar(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    height: Int = 10,
    showSeekBar: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((height / 2.5).dp)
                .background(
                    color = tertiaryColor
                )
                .align(Alignment.CenterStart)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(progress())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((height / 1.5).dp)
                    .background(
                        color = goldenColor
                    )
                    .align(Alignment.CenterStart)
            )

            if (showSeekBar) {
                Box(
                    modifier = Modifier
                        .offset(x = 4.dp)
                        .size(16.dp)
                        .background(
                            color = whiteColor,
                            shape = CircleShape
                        )
                        .align(Alignment.CenterEnd)
                )
            }
        }
    }
}