package com.speechai.speechai.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.speechai.speechai.CustomTextStyle
import com.speechai.speechai.quaternaryColor
import com.speechai.speechai.tertiaryColor
import com.speechai.speechai.utils.bounceClick
import com.speechai.speechai.utils.noInteractionClickable


@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    borderColor: Color = quaternaryColor,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    CustomButton(
        text = text,
        icon = icon,
        onClick = onClick,
        borderColor = borderColor,
        bgColor = Color.Transparent,
        modifier = modifier,
    )
}
@Composable
fun CustomButton(
    text: String,
    isLoading: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    bgColor: Color = tertiaryColor,
    borderColor: Color = quaternaryColor,
    startComposable: @Composable (() -> Unit)? = null,
    icon: ImageVector? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Box(
            modifier = Modifier
                .bounceClick(enabled = isLoading.not()) {
                    onClick()
                }
                .fillMaxWidth()
                .height(50.dp)
                .background(bgColor, shape = RoundedCornerShape(100.dp))
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(100.dp)
                )
                .padding(horizontal = 20.dp),
        ) {
            if (isLoading.not()) {
                startComposable?.let {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                    ) {
                        startComposable()
                    }
                }
            }
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                ) {
                    Loading()
                }
            } else {
                Text(
                    text = text,
                    style = CustomTextStyle.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
            if (isLoading.not()) {
                icon?.let {
                    Icon(
                        it,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }
}