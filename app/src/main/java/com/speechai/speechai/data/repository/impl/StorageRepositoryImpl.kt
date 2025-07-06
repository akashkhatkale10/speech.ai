package com.speechai.speechai.data.repository.impl

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.speechai.speechai.data.models.AudioMetadata
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
        val map = hashMapOf(
            "sizeBytes" to "${metadata?.sizeBytes}",
            "contentType" to metadata?.contentType.orEmpty(),
            "name" to metadata?.name.orEmpty(),
            "updatedTimeMillis" to "${metadata?.updatedTimeMillis}",
        )
        return FileUploadResult(url, AudioMetadata(
            sizeBytes = metadata?.sizeBytes ?: 0,
            contentType = metadata?.contentType.orEmpty(),
            name = metadata?.name.orEmpty(),
            updatedTimeMillis = metadata?.updatedTimeMillis ?: 0,
        ))
    }
}
