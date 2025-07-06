package com.speechai.speechai.data.repository

import com.speechai.speechai.data.models.DetailedAudioAnalysisModel

interface UserRepository {

    suspend fun postAudioAnalysis(
        audioAnalyseState: DetailedAudioAnalysisModel
    ): Result<Boolean>

    suspend fun getAudioHistory(): Result<List<DetailedAudioAnalysisModel>>

}