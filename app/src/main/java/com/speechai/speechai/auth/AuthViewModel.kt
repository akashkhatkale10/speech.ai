package com.speechai.speechai.auth

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.speechai.speechai.data.models.AudioAnalyseModel
import com.speechai.speechai.data.models.AudioMetadata
import com.speechai.speechai.data.models.DetailedAudioAnalysisModel
import com.speechai.speechai.data.models.UserData
import com.speechai.speechai.data.repository.LoginRepository
import com.speechai.speechai.data.repository.StorageRepository
import com.speechai.speechai.data.repository.UserRepository
import com.speechai.speechai.models.AnalysisScreenData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: LoginRepository,
    private val storageRepository: StorageRepository,
    private val userRepository: UserRepository
): ViewModel() {
    private val _loginState: MutableStateFlow<LoginState> = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun getCurrentUser() = Firebase.auth.currentUser

    fun loadingState(loading: Boolean = true) = viewModelScope.launch {
        _loginState.update {
            it.copy(isLoading = loading)
        }
    }

    fun loginUser(
        signInResult: SignInResult,
        file: File? = null,
        audioAnalyseModel: AudioAnalyseModel? = null,
        totalScore: Int? = null,
        duration: Long? = null
    ) = viewModelScope.launch {
        _loginState.update {
            it.copy(isLoading = true)
        }
        signInResult.data?.let {
            val user = it.copy(
                personalDetails = UserData.PersonalDetails(
                    name = it.personalDetails?.name,
                    email = it.personalDetails?.email,
                    photoUrl = it.personalDetails?.photoUrl
                )
            )
            repository.loginUser(user).onSuccess { data ->
                if (file != null && audioAnalyseModel != null) {
                    val storageResult = storageRepository.uploadFile(
                        localFileUri = file.toUri(),
                        remotePath = "recordings/${data.userId.orEmpty()}/${file.name}"
                    )
                    userRepository.postAudioAnalysis(
                        audioAnalyseState = DetailedAudioAnalysisModel(
                            data = audioAnalyseModel,
                            downloadUrl = storageResult.downloadUrl,
                            timestamp = System.currentTimeMillis(),
                            userId = data.userId,
                            totalScore = totalScore,
                            audioMetadata = storageResult.metadata.copy(
                                durationMillis = duration
                            )
                        )
                    )
                }
                _loginState.update { login ->
                    login.copy(isLoading = false, error = null, data = data)
                }
            }.onFailure { err ->
                _loginState.update { login ->
                    login.copy(isLoading = false, error = err)
                }
            }
        } ?: run {
            _loginState.update { login ->
                login.copy(isLoading = false, error = Exception("something went wrong"))
            }
        }

    }

}

data class LoginState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val data: UserData? = null
)