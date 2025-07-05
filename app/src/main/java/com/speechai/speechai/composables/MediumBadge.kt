package com.speechai.speechai.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.speechai.speechai.CustomTextStyle
import com.speechai.speechai.secondaryColor
import com.speechai.speechai.tertiaryColor
import com.speechai.speechai.whiteColor

@Composable
fun MediumBadge(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    backgroundColor: Color = tertiaryColor,
    borderColor: Color = whiteColor.copy(0.25f),
    contentColor: Color = whiteColor,
    textStyle: TextStyle = CustomTextStyle.copy(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    )
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(40.dp)
            .background(backgroundColor)
            .border(width = 1.dp, color = borderColor)
            .padding(start = 20.dp, end = 10.dp)
    ) {
        Text(
            text = text,
            color = contentColor,
            style = textStyle
        )
        Spacer(Modifier.width(6.dp))
        Icon(
            icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier
                .size(20.dp)
        )
    }
}