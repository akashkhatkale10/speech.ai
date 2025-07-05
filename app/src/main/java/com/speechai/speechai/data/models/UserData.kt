package com.speechai.speechai.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserData(
    @field:SerializedName("userId") val userId: String? = null,
    @field:SerializedName("personalDetails") val personalDetails: PersonalDetails? = null,
): Parcelable {

    @Parcelize
    data class PersonalDetails(
        @field:SerializedName("name") val name: String? = null,
        @field:SerializedName("email") val email: String? = null,
        @field:SerializedName("photoUrl") val photoUrl: String? = null,
    ): Parcelable
}
