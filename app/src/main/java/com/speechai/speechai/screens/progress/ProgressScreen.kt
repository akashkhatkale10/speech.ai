package com.speechai.speechai.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.speechai.speechai.backgroundColor

@Composable
fun ProgressScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ){
        Text("progress")
    }
}