package com.example.workhourregistrator.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import android.widget.LinearLayout
import com.example.workhourregistrator.AlertDialogProvider
import com.example.workhourregistrator.MainActivity
import com.example.workhourregistrator.R
import com.example.workhourregistrator.SharedPreferencesEditor
import com.example.workhourregistrator.SharedPreferencesEditor.Companion.END_WORKING_DAY
import kotlinx.android.synthetic.main.fragment_start_duty.*
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationUtils = NotificationUtils(context)
        val notification = notificationUtils.getNotificationBuilder().build()

        val spe = SharedPreferencesEditor()
        spe.setupSharedPreferencesEditor(context)


        when (intent.extras?.getInt("alarm")) {
            0 -> {
                notificationUtils.getManager().notify(150, notification)
            }
            1 -> {
                spe.setStatus(END_WORKING_DAY, true)
            }
            2 -> {
                MainActivity().setAutomaticFinish(context, 0, true)
                notificationUtils.getManager().cancel(150)
            }
        }


    }
}