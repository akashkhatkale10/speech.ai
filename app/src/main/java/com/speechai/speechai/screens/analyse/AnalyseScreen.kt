package com.speechai.speechai.screens.analyse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.speechai.speechai.CustomTextStyle
import com.speechai.speechai.R
import com.speechai.speechai.backgroundColor
import com.speechai.speechai.composables.CustomTopBar
import com.speechai.speechai.secondaryColor
import com.speechai.speechai.whiteColor

@Composable
fun AnalyseScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column {
            CustomTopBar(
                modifier = Modifier
                    .padding(horizontal = 20.dp),
                startComposable = {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                color = secondaryColor,
                                shape = CircleShape
                            )
                            .padding(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = whiteColor,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                },
                endComposable = {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFEDDD53),
                                        Color(0xFFFFA12D),
                                    )
                                ),
                                shape = CircleShape
                            )
                            .padding(8.dp)
                    ) {
                        Icon(
                            painterResource(R.drawable.crown),
                            contentDescription = null,
                            tint = whiteColor,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                },
                midComposable = {
                    Text(
                        text = stringResource(R.string.title),
                        style = CustomTextStyle.copy(
                            color = whiteColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            )
        }
    }
}