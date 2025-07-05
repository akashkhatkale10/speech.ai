package com.speechai.speechai.data.repository

import android.net.Uri

data class FileUploadResult(
    val downloadUrl: String,
    val metadata: Map<String, Any?>,
)

interface StorageRepository {
    suspend fun uploadFile(
        localFileUri: Uri,
        remotePath: String,
    ): FileUploadResult
}