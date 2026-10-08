package com.pippo2801.universalgamebot.storage

import android.content.Context
import android.content.SharedPreferences

class StorageManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("UniversalGameBotPrefs", Context.MODE_PRIVATE)

    fun saveString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultVal: String): String {
        return prefs.getString(key, defaultVal) ?: defaultVal
    }
}
