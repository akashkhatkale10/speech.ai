package com.speechai.speechai.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.speechai.speechai.quaternaryColor
import com.speechai.speechai.tertiaryColor
import com.speechai.speechai.utils.bounceClick
import com.speechai.speechai.whiteColor

@Composable
fun SmallCircleButton(
    icon: ImageVector,
    iconSize: Dp = 30.dp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .bounceClick {
                onClick()
            }
            .size(54.dp)
            .background(
                color = tertiaryColor,
                shape = CircleShape
            )
            .border(
                width = 1.dp,
                color = quaternaryColor,
                shape = CircleShape
            )
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(iconSize),
            tint = whiteColor
        )
    }
}