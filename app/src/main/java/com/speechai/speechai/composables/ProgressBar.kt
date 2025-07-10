package com.speechai.speechai.composables

import android.widget.ProgressBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.speechai.speechai.goldenColor
import com.speechai.speechai.tertiaryColor

@Composable
fun ProgressBar(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    height: Int = 10
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
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
                .height(10.dp)
                .background(
                    color = goldenColor
                )
                .align(Alignment.CenterStart)
        )
    }
}