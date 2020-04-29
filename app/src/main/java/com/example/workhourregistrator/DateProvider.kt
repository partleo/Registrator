package com.example.workhourregistrator

import java.text.SimpleDateFormat
import java.util.*

class DateProvider {

    private val sdf = SimpleDateFormat( "dd.MM.yyyy", Locale.getDefault())
    private val stf = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun getCurrentDate(): String {
        return sdf.format(System.currentTimeMillis())
    }

    fun getCurrentTime(): String {
        return stf.format(System.currentTimeMillis())
    }
}