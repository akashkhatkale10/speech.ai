package com.speechai.speechai.data.repository

import com.speechai.speechai.data.models.UserData

interface LoginRepository {

    suspend fun loginUser(user: UserData): Result<UserData>
    suspend fun logoutUser()
    suspend fun getCurrentUser(): Result<UserData?>
    suspend fun getCurrentUserData(): Result<UserData?>
}