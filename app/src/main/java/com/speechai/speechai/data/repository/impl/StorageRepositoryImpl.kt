package com.speechai.speechai.data.repository.impl

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.speechai.speechai.data.repository.FileUploadResult
import com.speechai.speechai.data.repository.StorageRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
) : StorageRepository {
    override suspend fun uploadFile(
        localFileUri: Uri,
        remotePath: String,
    ): FileUploadResult {
        val ref = storage.reference.child(remotePath)
        val taskSnapshot = ref.putFile(localFileUri).await()
        val url = ref.downloadUrl.await().toString()
        val metadata = taskSnapshot.metadata
        val map = hashMapOf<String, Any?>(
            "sizeBytes" to metadata?.sizeBytes,
            "contentType" to metadata?.contentType,
            "name" to metadata?.name,
            "updatedTimeMillis" to metadata?.updatedTimeMillis
        )
        return FileUploadResult(url, map)
    }
}
