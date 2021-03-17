package com.example.workhourregistrator

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.widget.Toast


class SharedPreferencesEditor {

    companion object {
        const val DO_NOT_ASK_AGAIN= "DoNotAskAgain"
        //const val CURRENT_MONTH_AND_YEAR = "CurrentMonthAndYear"
        const val CURRENT_DATE = "CurrentDate"

        const val MONTH_AND_YEAR = "MonthAndYear"

        const val START_TIME_DAY = "StartTimeDay"
        const val START_TIME_WORK = "StartTimeWork"

        const val LAST_ROW = "LastRow"
        const val LAST_COLUMN = "LastColumn"

        const val CURRENT_WEEK = "CurrentWeek"

        const val CURRENT_WORK_NUMBER = "CurrentWorkNumber"

        const val PATH = "Path"

        const val WORK_NUMBER_LIST = "WorkNumbers"

        const val EMAIL_ADDRESS = "EmailAddress"

        const val WORK_IN_PROGRESS = "InProgress"


        const val WORKBOOK_LIST = "WorkbookList"

        const val DESCRIPTION = "Description"


        const val END_WORKING_DAY = "EndWorkingDay"

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

    fun setStatus(key: String, status: Boolean) {
        sharedPreferences.edit().putBoolean(key, status).apply()
    }

    fun getStatus(key: String, defValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defValue)
    }

    fun setWorkNumberList(phoneNumberList: ArrayList<String>) {
        val set: Set<String> = HashSet(phoneNumberList)
        sharedPreferences.edit().putStringSet(WORK_NUMBER_LIST, set).apply()
    }

    fun getWorkNumberList(): ArrayList<String> {
        val set = sharedPreferences.getStringSet(WORK_NUMBER_LIST, setOf())
        return ArrayList(set)
    }

    fun deleteWorkNumberFromList(phoneNumber: String) {
        val list = getWorkNumberList()
        if (list.contains(phoneNumber)) {
            list.remove(phoneNumber)
            //Toast.makeText(c, c.getText(R.string.number_deleted), Toast.LENGTH_SHORT).show()
            setWorkNumberList(list)
            /*
            if (list.isEmpty()) {
                setPhoneNumber("")
                (c as MainActivity).recreate()
            }
            else {
                if (phoneNumber == getPhoneNumber()) {
                    setPhoneNumber(list[0])
                }
            }

             */
        }
        else {
            //Toast.makeText(c, c.getText(R.string.not_phone_number), Toast.LENGTH_SHORT).show()
        }
    }

    fun setWorkbookList(workbookList: ArrayList<String>) {
        val set: Set<String> = HashSet(workbookList)
        sharedPreferences.edit().putStringSet(WORKBOOK_LIST, set).apply()
    }

    fun getWorkbookList(): ArrayList<String> {
        val set = sharedPreferences.getStringSet(WORKBOOK_LIST, setOf())
        return ArrayList(set)
    }

    fun deleteWorkbookFromList(workbook: String) {
        val list = getWorkbookList()
        if (list.contains(workbook)) {
            list.remove(workbook)
            //Toast.makeText(c, "DELETED", Toast.LENGTH_SHORT).show()
            setWorkbookList(list)
        }
        else {
            //Toast.makeText(c, "NOT DELETED !!!", Toast.LENGTH_SHORT).show()
        }
    }

}