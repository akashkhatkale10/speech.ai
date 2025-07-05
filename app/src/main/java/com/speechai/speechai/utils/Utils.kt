package com.speechai.speechai.utils

import android.content.Context
import android.os.Build
import android.os.Message
import android.util.Log
import com.google.firebase.BuildConfig
import com.google.gson.Gson
import java.io.ByteArrayOutputStream

fun readAudioFileFromAssets(context: Context, filename: String): ByteArray {
    val buffer = ByteArrayOutputStream()
    val inputStream = context.assets.open(filename)
    val tmp = ByteArray(1024)
    var bytesRead: Int
    while (inputStream.read(tmp).also { bytesRead = it } != -1) {
        buffer.write(tmp, 0, bytesRead)
    }
    buffer.flush()
    val byteData = buffer.toByteArray()
    buffer.close()
    inputStream.close()
    return byteData
}

fun <T> toModel(value: String?, classOf: Class<T>): T? {
    return try {
        Gson().fromJson(value, classOf)
    } catch (e: Exception) {
        Log.d("TAG---", "${e.cause}")
        null
    }
}

fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    if (seconds < 60) {
        return "00 : ${if (seconds < 10) "0$seconds" else seconds}"
    } else if (hours == 0L) {
        return "${if (minutes.toInt() < 10) "0${minutes.toInt()}" else minutes.toInt().toString()} : ${if (secs < 10) "0$secs" else secs}"
    } else {
        val h = if (hours.toInt() < 10) "0${hours.toInt()}" else hours.toInt().toString()
        val m = if (minutes.toInt() < 10) "0${minutes.toInt()}" else minutes.toInt().toString()
        val s = if (secs.toInt() < 10) "0${secs.toInt()}" else secs.toInt().toString()
        return "${h} : ${m} :${s} "
    }
}


inline fun<reified T> Context.toModelFromAssets(jsonFileNameFromAssets: String): T? {
    val json = assets?.open(jsonFileNameFromAssets)?.bufferedReader().use {
        it?.readText()
    }
    return toModel(json, T::class.java)
}