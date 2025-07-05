package com.speechai.speechai.data.repository.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.speechai.speechai.data.models.DetailedAudioAnalysisModel
import com.speechai.speechai.data.repository.UserRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): UserRepository {
    override suspend fun postAudioAnalysis(
        audioAnalyseState: DetailedAudioAnalysisModel,
    ): Result<Boolean> {
        try {
            val ref = firestore.collection("recordings").document()
            firestore
                .collection("recordings")
                .document(ref.id)
                .set(audioAnalyseState.copy(
                    id = ref.id,
                ))
                .await()

            return Result.success(true)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}