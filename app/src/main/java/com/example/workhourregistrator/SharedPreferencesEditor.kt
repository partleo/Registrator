package com.example.workhourregistrator

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


class SharedPreferencesEditor {

    companion object {
        const val DO_NOT_ASK_AGAIN= "DoNotAskAgain"
    }

    private lateinit var c: Context
    private lateinit var sharedPreferences: SharedPreferences

    fun setupSharedPreferencesEditor(context: Context) {
        this.c = context
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c)
    }

    fun sharedPreferences(): SharedPreferences {
        return sharedPreferences
    }

    fun setStatus(status: String, key: String) {
        sharedPreferences.edit().putString(key, status).apply()
    }

    fun getStatus(key: String, defValue: String): String {
        return sharedPreferences.getString(key, defValue) as String
    }

}