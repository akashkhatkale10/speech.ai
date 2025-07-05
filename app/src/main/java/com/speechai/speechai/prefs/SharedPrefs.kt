package com.speechai.speechai.prefs

interface SharedPrefs {

    companion object {
        const val ONBOARDING_COMPLETED = "onboarding_completed"
    }

    fun putString(key: String, value: String)
    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, default: Boolean): Boolean
}
