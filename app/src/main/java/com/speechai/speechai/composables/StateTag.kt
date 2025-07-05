package com.speechai.speechai.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.speechai.speechai.CustomTextStyle
import com.speechai.speechai.models.StateTag
import com.speechai.speechai.models.getStateTagModel
import com.speechai.speechai.whiteColor

@Composable
fun StateTag(
    tag: StateTag,
    modifier: Modifier = Modifier,
) {
    val tagModel = getStateTagModel(tag)

    tagModel?.let {
        Box(
            modifier = modifier
                .widthIn(min = 60.dp)
                .background(
                    color = tagModel.bgColor,
                    shape = RoundedCornerShape(4.dp)
                )
                .border(
                    1.dp,
                    tagModel.borderColor,
                    RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 10.dp, vertical = 2.dp)
        ) {
            Text(
                text = tagModel.title,
                style = CustomTextStyle.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = whiteColor,
                ),
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }
}