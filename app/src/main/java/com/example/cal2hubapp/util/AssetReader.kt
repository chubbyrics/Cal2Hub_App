package com.example.cal2hubapp.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

inline fun <reified T> Context.readAssetJson(filename: String, gson: Gson): T {
    return try {
        val json = assets.open(filename).bufferedReader().use { it.readText() }
        val type = object : TypeToken<T>() {}.type
        gson.fromJson<T>(json, type)
    } catch (e: IOException) {
        throw RuntimeException("Failed to read asset file: $filename", e)
    } catch (e: Exception) {
        throw RuntimeException("Failed to parse JSON from: $filename", e)
    }
}
