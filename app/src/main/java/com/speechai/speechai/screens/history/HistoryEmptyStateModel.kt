package com.speechai.speechai.screens.history

import androidx.annotation.Keep
import com.speechai.speechai.models.StateTag

@Keep
data class HistoryEmptyStateModel(
    val tag: StateTag,
    val title: String,
    val subtitle: String,
    val buttonText: String
)
