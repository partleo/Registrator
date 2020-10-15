package com.example.workhourregistrator

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DateProvider {

    private val sdf = SimpleDateFormat( "dd.MM.yyyy", Locale.getDefault())
    private val stf = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val swf = SimpleDateFormat( "EE", Locale.getDefault())

    private val smf = SimpleDateFormat( "MM-yyyy", Locale.getDefault())

    fun getCurrentDate(): String {
        return sdf.format(System.currentTimeMillis())
    }

    fun getCurrentTime(): String {
        return stf.format(System.currentTimeMillis())
    }

    fun getCurrentMonth(): String {
        val date = getCurrentDate().split(".")
        return date[1]
    }

    fun getCurrentWeekday(): String {
        return swf.format(System.currentTimeMillis())
    }

    fun getTimeDifference(startTime: String, endTime: String): String {
        var minutes = TimeUnit.MILLISECONDS.toMinutes(stf.parse(endTime).time - stf.parse(startTime).time)
        val hours = TimeUnit.MINUTES.toHours(minutes)
        minutes -= TimeUnit.HOURS.toMinutes(hours)
        if (minutes < 10) {
            return "$hours:0$minutes"
        }
        return "$hours:$minutes"
    }

    fun getEndTimeFromStartTime(startTime: String): String {
        return stf.format(stf.parse(startTime).time + 1800000)
    }

    fun getCurrentMonthAndYear(): String {
        return smf.format(System.currentTimeMillis())
    }

    fun getMonthAndYearFromDate(date: Date): String {
        return smf.format(date)
    }

    fun getCurrentWeekNumber(): Int {
        return Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
    }

}