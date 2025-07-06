package com.speechai.speechai.data.repository

import android.net.Uri
import androidx.annotation.Keep
import com.speechai.speechai.data.models.AudioMetadata

@Keep
data class FileUploadResult(
    val downloadUrl: String,
    val metadata: AudioMetadata,
)

interface StorageRepository {
    suspend fun uploadFile(
        localFileUri: Uri,
        remotePath: String,
    ): FileUploadResult
}