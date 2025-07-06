package com.speechai.speechai.audio

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.HarmBlockThreshold
import com.google.firebase.ai.type.HarmCategory
import com.google.firebase.ai.type.SafetySetting
import com.google.firebase.ai.type.TextPart
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.app
import com.speechai.speechai.audio.AudioUtils.buildResponse
import com.speechai.speechai.data.models.AudioAnalyseModel
import com.speechai.speechai.models.AudioAnalyseState
import com.speechai.speechai.utils.analyseAudioResponseSchema
import com.speechai.speechai.utils.readAudioFileFromAssets
import com.speechai.speechai.utils.toModelFromAssets
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(
    @ApplicationContext private val context: Context
): ViewModel() {
    
    private val _analysisResult = MutableStateFlow(AudioAnalyseState())
    val analysisResult: StateFlow<AudioAnalyseState> = _analysisResult.asStateFlow()

    fun analyseAudio(
        file: File
    ) = viewModelScope.launch {
        val bytes = readAudioFileFromAssets(context, "sample.mp3")
        _analysisResult.update {
            it.copy(
                isLoading = true,
                error = null,
                response = null
            )
        }
        delay(2000)
        try {
            val content = context.toModelFromAssets<AudioAnalyseModel>("sample_response.json")
            _analysisResult.update {
                it.copy(
                    isLoading = false,
                    error = null,
                    response = buildResponse(content)
                )
            }
//            generateAnalysis(
//                byte = bytes,
//                mimeType = "audio/mpeg"
//            ) { content ->
//                _analysisResult.update {
//                    it.copy(
//                        isLoading = false,
//                        error = null,
//                        response = toModel(content, AudioAnalyseModel::class.java)
//                    )
//                }
//            }
        } catch (e: Exception) {
            _analysisResult.update {
                it.copy(
                    isLoading = false,
                    error = e.localizedMessage,
                    response = null
                )
            }
        }
    }

    suspend fun generateAnalysis(byte: ByteArray, mimeType: String, onResult: (content: String?) -> Unit) {
        val generationConfig = generationConfig {
            responseMimeType = "application/json"
            responseSchema = analyseAudioResponseSchema
        }

        val safetySettings = listOf(
            SafetySetting(
                harmCategory = HarmCategory.HATE_SPEECH,
                threshold = HarmBlockThreshold.OFF
            ),
            SafetySetting(
                harmCategory = HarmCategory.DANGEROUS_CONTENT,
                threshold = HarmBlockThreshold.OFF
            ),
            SafetySetting(
                harmCategory = HarmCategory.SEXUALLY_EXPLICIT,
                threshold = HarmBlockThreshold.OFF
            ),
            SafetySetting(
                harmCategory = HarmCategory.HARASSMENT,
                threshold = HarmBlockThreshold.OFF
            )
        )
        val siText1 =
            TextPart("You are friendly speech assistant. Give me a detailed analysis of my audio")
        val systemInstruction = content {
            part(siText1)
        }
        val model = Firebase.ai(
            app = Firebase.app,
            backend = GenerativeBackend.vertexAI("global")
        ).generativeModel(
            modelName = "gemini-2.5-flash",
            generationConfig = generationConfig,
            safetySettings = safetySettings,
            systemInstruction = systemInstruction
        )
        val prompt = content {
            inlineData(byte, mimeType)
            text("Analyse this audio. Analyse the following properties:\\n1. Confidence: Analyse my confidence and give it a score out of 100 %. Also give reason for the provided score and examples where i lacked confidence.\\n2. Filler words: Detect and Analyse my filler words and give it a score out of 100%. Also give reason for the provided score and examples where i showed filler words. \\n3. Mumble: Detect and Analyse my mumble and give it a score out of 100%. Also give reason for the provided score and examples where i mumbled. \\n4. Fluency: Analyse my fluency and give it a score out of 100 %. Also give reason for the provided score and examples where i lacked fluency.\\n5. Grammar accuracy: Analyse my grammar accuracy and give it a score out of 100 %. Also give reason for the provided score and examples where i lacked grammar accuracy.\\n6. Pronunciation : Analyse my pronunciation accuracy and give it a score out of 100 %. Also give reason for the provided score and examples where i lacked pronunciation.\\n7. Speaking rate: Analyse my speaking rate and words per minute and give it a score out of 100%. If the speaking rate is perfectly fine or normal, give it a score of 100. Also give reason for the provided score and examples where i my speaking rate was not normal or lacked.")
        }
        val content = model.generateContent(prompt)
        onResult(content.text)
    }
}