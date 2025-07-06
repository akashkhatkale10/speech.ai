package com.speechai.speechai.screens.history

import android.os.Build
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speechai.speechai.audio.AudioUtils.isBad
import com.speechai.speechai.audio.AudioUtils.isExcellent
import com.speechai.speechai.audio.AudioUtils.isFair
import com.speechai.speechai.data.models.DetailedAudioAnalysisModel
import com.speechai.speechai.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _audioHistory = MutableStateFlow(HistoryUiState(isLoading = true))
    val audioHistory: StateFlow<HistoryUiState> = _audioHistory.asStateFlow()

    fun getAudioHistory() = viewModelScope.launch {
        _audioHistory.update {
            it.copy(
                isLoading = true
            )
        }
        userRepository.getAudioHistory().onSuccess { data ->
            val allHistory = data
            val excellentHistory = data.filter { d -> isExcellent(d.totalScore ?: 0) }
            val fairHistory = data.filter { d -> isFair(d.totalScore ?: 0) }
            val badHistory = data.filter { d -> isBad(d.totalScore ?: 0) }
            
            
            val allByDate = allHistory.sortedByDescending { it.timestamp }
                .groupBy {
                    getDayMonthYear(it.timestamp ?: 0)
                }
                .map { (date, items) -> HistorySectionModel(title = formatDateToOrdinalMonthYear(date.first, date.second, date.third), history = items) }

            val excellentByDate = excellentHistory.sortedByDescending { it.timestamp }
                .groupBy {
                    getDayMonthYear(it.timestamp ?: 0)
                }
                .map { (date, items) -> HistorySectionModel(title = formatDateToOrdinalMonthYear(date.first, date.second, date.third), history = items) }

            val fairByDate = fairHistory.sortedByDescending { it.timestamp }
                .groupBy {
                    getDayMonthYear(it.timestamp ?: 0)
                }
                .map { (date, items) -> HistorySectionModel(title = formatDateToOrdinalMonthYear(date.first, date.second, date.third), history = items) }

            val badByDate = badHistory.sortedByDescending { it.timestamp }
                .groupBy {
                    getDayMonthYear(it.timestamp ?: 0)
                }
                .map { (date, items) -> HistorySectionModel(title = formatDateToOrdinalMonthYear(date.first, date.second, date.third), history = items) }

            _audioHistory.update {
                it.copy(
                    isLoading = false,
                    error = null,
                    allHistory = allByDate,
                    excellentHistory = excellentByDate,
                    fairHistory = fairByDate,
                    badHistory = badByDate,
                )
            }
        }.onFailure {
            _audioHistory.update {
                it.copy(
                    isLoading = false,
                    error = it.error ?: "error fetching audio history"
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun getDayMonthYear(timeInMillis: Long): Triple<Int, Int, Int> {
    val instant = Instant.ofEpochMilli(timeInMillis)
    val localDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate()
    return Triple(localDate.dayOfMonth, localDate.monthValue, localDate.year)
}

@Keep
data class HistorySectionModel(
    val title: String,
    val history: List<DetailedAudioAnalysisModel>
)

fun formatDateToOrdinalMonthYear(day: Int, month: Int, year: Int): String {
    val suffix = when {
        day in 11..13 -> "th"
        day % 10 == 1 -> "st"
        day % 10 == 2 -> "nd"
        day % 10 == 3 -> "rd"
        else -> "th"
    }
    val monthName = when(month) {
        1 -> "jan"
        2 -> "feb"
        3 -> "march"
        4 -> "april"
        5 -> "may"
        6 -> "june"
        7 -> "july"
        8 -> "aug"
        9 -> "sept"
        10 -> "oct"
        11 -> "nov"
        12 -> "dec"
        else -> "Unknown"
    }
    return "$day$suffix $monthName $year"
}

@Keep
data class HistoryUiState(
    val allHistory: List<HistorySectionModel> = emptyList(),
    val excellentHistory: List<HistorySectionModel> = emptyList(),
    val fairHistory: List<HistorySectionModel> = emptyList(),
    val badHistory: List<HistorySectionModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)