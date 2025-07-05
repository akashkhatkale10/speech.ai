package com.speechai.speechai.screens.onboarding

import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(

): ViewModel() {
    private val _currentBenefit = MutableStateFlow<List<AnnotatedString>>(listOf())
    val currentBenefit = _currentBenefit.asStateFlow()

    fun emitBenefits() {
        viewModelScope.launch {
            for (benefit in benefits) {
                _currentBenefit.value = _currentBenefit.value.toMutableList().apply {
                    add(benefit)
                }
                delay(200)
            }
        }
    }
}