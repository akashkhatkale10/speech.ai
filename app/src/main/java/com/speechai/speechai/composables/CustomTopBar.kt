package com.speechai.speechai.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CustomTopBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = com.speechai.speechai.backgroundColor,
    midComposable: @Composable () -> Unit = {},
    startComposable: @Composable () -> Unit = {},
    endComposable: @Composable () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .background(backgroundColor)
    ) {
        Box(
            modifier = modifier
                .height(60.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                startComposable()
            }

            Box(
                modifier = Modifier.align(Alignment.Center),
            ) {
                midComposable()
            }


            Box(
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                endComposable()
            }
        }
    }
}