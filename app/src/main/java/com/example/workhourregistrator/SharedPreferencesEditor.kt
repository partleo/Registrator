package com.example.workhourregistrator

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


class SharedPreferencesEditor {

    companion object {
        const val DO_NOT_ASK_AGAIN= "DoNotAskAgain"
        const val CURRENT_MONTH_AND_YEAR = "CurrentMonthAndYear"
        const val CURRENT_DATE = "CurrentDate"

        const val START_TIME_DAY = "StartTimeDay"
        const val START_TIME_WORK = "StartTimeWork"

        const val LAST_ROW = "LastRow"
        const val LAST_COLUMN = "LastColumn"

        const val CURRENT_WEEK = "CurrentWeek"

        const val CURRENT_WORK_NUMBER = "CurrentWorkNumber"

        const val PATH = "Path"
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

    fun setStatus(key: String, status: String) {
        sharedPreferences.edit().putString(key, status).apply()
    }

    fun getStatus(key: String, defValue: String): String {
        return sharedPreferences.getString(key, defValue) as String
    }

    fun setStatus(key: String, status: Int) {
        sharedPreferences.edit().putInt(key, status).apply()
    }

    fun getStatus(key: String, defValue: Int): Int {
        return sharedPreferences.getInt(key, defValue)
    }

}