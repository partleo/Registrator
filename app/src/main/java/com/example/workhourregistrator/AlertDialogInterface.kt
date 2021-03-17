package com.example.workhourregistrator

interface AlertDialogInterface {
    fun onAlertDialogRespond(continues: Boolean?, project: String, description: String, endTime: String)
}