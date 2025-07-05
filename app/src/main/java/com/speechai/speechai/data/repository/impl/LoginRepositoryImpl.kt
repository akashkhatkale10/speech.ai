package com.speechai.speechai.data.repository.impl

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.speechai.speechai.data.models.UserData
import com.speechai.speechai.data.repository.LoginRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): LoginRepository {

    override suspend fun loginUser(
        user: UserData,
    ): Result<UserData> {
        try {
            val firebaseUser = firestore
                .collection("users")
                .document(user.userId.orEmpty())
                .get().await()
            if (firebaseUser.exists()) {
                // user exists, get user data
                (firebaseUser.toObject(UserData::class.java))?.let {
                    return Result.success(it)
                }

                return Result.failure(Exception("Something went wrong"))
            } else {
                // user doesn't exists
                firestore
                    .collection("users")
                    .document(user.userId.orEmpty())
                    .set(user)
                    .await()
                return Result.success(user)
            }
        } catch (e: Exception) {
            // check if user exists
            Log.d("AKASH_LOG", "Exception occurred: ${e.message}")
            return Result.failure(e)
        }
    }

    override suspend fun logoutUser() {

    }

    override suspend fun getCurrentUser(): Result<UserData?> {
        try {
            if (auth.currentUser != null) {
                val firebaseUser = firestore.collection("users").document(auth.currentUser!!.uid).get().await()
                return if (firebaseUser.exists()) {
                    Result.success(
                        UserData(
                            userId = auth.currentUser!!.uid,
                            personalDetails = UserData.PersonalDetails(
                                name = auth.currentUser!!.displayName ?: "",
                                email = auth.currentUser!!.email ?: "",
                                photoUrl = auth.currentUser!!.photoUrl.toString()
                            )
                        )
                    )
                } else {
                    // network issue
                    Result.success(null)
                }
            } else {
                return Result.success(null)
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun getCurrentUserData(): Result<UserData?> {
        try {
            if (auth.currentUser != null) {
                val firebaseUser = firestore.collection("users").document(auth.currentUser!!.uid).get().await()
                return if (firebaseUser.exists()) {
                    (firebaseUser.toObject(UserData::class.java))?.let {
                        return Result.success(it)
                    }
                    return Result.failure(Exception("Something went wrong"))
                } else {
                    // network issue
                    Result.success(null)
                }
            } else {
                // go to login screen
                return Result.success(null)
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}