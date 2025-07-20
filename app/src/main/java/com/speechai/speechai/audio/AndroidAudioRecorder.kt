package com.speechai.speechai.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.speechai.speechai.composables.RecordingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class AndroidAudioRecorder(
    private val context: Context
): AudioRecorder {

    private var recorder: MediaRecorder? = null
    private var amplitudeJob: Job? = null

    private val _amplitude = MutableStateFlow(0)
    val amplitude: StateFlow<Int> get() = _amplitude.asStateFlow()

    var recordingState by mutableStateOf(RecordingState.IDLE)

    private val _timerState = MutableStateFlow(0L)
    val timerState: StateFlow<Long> = _timerState.asStateFlow()
    private var timerJob: Job? = null


    private fun createRecorder(): MediaRecorder {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

    override fun start(outputFile: File) {
        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(FileOutputStream(outputFile).fd)

            recordingState = RecordingState.PLAYING
            _timerState.value = 0
            prepare()
            start()
            startTimer()

            recorder = this
            startAmplitudeUpdates()
        }
    }

    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            // todo: add logic for max timer limit, currently it is 60 secs
            while (timerState.value < 60 && recordingState == RecordingState.PLAYING) {
                delay(1000L)
                _timerState.value++
            }
        }
    }


    override fun stop() {
        recordingState = RecordingState.STOPPED
        recorder?.stop()
        recorder?.reset()
        timerJob?.cancel()
        recorder = null
    }

    override fun pause() {
        recordingState = RecordingState.PAUSED
        timerJob?.cancel()
        recorder?.pause()
    }

    override fun resume() {
        recordingState = RecordingState.PLAYING
        startTimer()
        recorder?.resume()
    }

    override fun cancel() {
        recordingState = RecordingState.IDLE
        recorder?.stop()
        recorder?.reset()
        timerJob?.cancel()
        _timerState.value = 0
        recorder = null
    }

    private fun startAmplitudeUpdates() {
        amplitudeJob?.cancel()
        val scope = CoroutineScope(Dispatchers.Default)
        amplitudeJob = scope.launch {
            while (recorder != null && isActive) {
                // getMaxAmplitude returns 0 if called before start() or after stop()
                val currentAmplitude = try {
                    recorder?.maxAmplitude ?: 0
                } catch (e: Exception) {
                    0
                }
                _amplitude.value = currentAmplitude
                delay(100) // update rate: every 50ms
            }
        }
    }
}

//class AndroidAudioRecorder(private val context: Context) {
//
//    private var recorder: MediaRecorder? = null
//    private var audioFile: File? = null
//    private var amplitudeJob: Job? = null
//
//    // Expose amplitude as a StateFlow
//    private val _amplitude = MutableStateFlow(0)
//    val amplitude: StateFlow<Int> get() = _amplitude
//
//    suspend fun onRecord() {
//        withContext(Dispatchers.IO) {
//            audioFile = File.createTempFile("audio_record_", ".m4a", context.cacheDir)
//            recorder = MediaRecorder().apply {
//                setAudioSource(MediaRecorder.AudioSource.MIC)
//                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
//                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
//                setOutputFile(audioFile!!.absolutePath)
//                prepare()
//                start()
//            }
//        }
//        startAmplitudeUpdates()
//    }
//
//    suspend fun onStop(): ByteArray? = withContext(Dispatchers.IO) {
//        amplitudeJob?.cancel()
//        amplitudeJob = null
//        try {
//            recorder?.apply {
//                stop()
//                release()
//            }
//            recorder = null
//            _amplitude.value = 0
//            val bytes = audioFile?.readBytes()
//            audioFile?.delete()
//            audioFile = null
//            return@withContext bytes
//        } catch (e: Exception) {
//            e.printStackTrace()
//            _amplitude.value = 0
//            return@withContext null
//        }
//    }
//
//    /**
//     * Starts observing the current amplitude and emits it to the [amplitude] StateFlow.
//     */
//    private fun startAmplitudeUpdates() {
//        amplitudeJob?.cancel()
//        val scope = CoroutineScope(Dispatchers.Default)
//        amplitudeJob = scope.launch {
//            while (recorder != null && isActive) {
//                // getMaxAmplitude returns 0 if called before start() or after stop()
//                val currentAmplitude = try {
//                    recorder?.maxAmplitude ?: 0
//                } catch (e: Exception) {
//                    0
//                }
//                _amplitude.value = currentAmplitude
//                delay(50) // update rate: every 50ms
//            }
//        }
//    }
//}