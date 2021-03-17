package com.example.workhourregistrator.notifications

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import android.content.ContentResolver
import androidx.core.content.ContextCompat
import com.example.workhourregistrator.MainActivity
import com.example.workhourregistrator.R


class  NotificationUtils(val context: Context) : ContextWrapper(context) {

    companion object{
        const val CHANNEL_ID = "Notification ID"
        const val CHANNEL_NAME = "working day notification"
        const val OPEN_START_DUTY = "OpenStartDuty"
    }

    private var manager: NotificationManager? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels()
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannels() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        channel.enableVibration(true)
        channel.enableLights(true)
        channel.lightColor = ContextCompat.getColor(context,
            R.color.colorAccent
        )
        val uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.alarm)
        val r = RingtoneManager.getRingtone(context, uri)
        channel.setSound(uri, r.audioAttributes)

        getManager().createNotificationChannel(channel)
    }

    fun getManager() : NotificationManager {
        if (manager == null) manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return manager as NotificationManager
    }

    fun getNotificationBuilder(): NotificationCompat.Builder {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        intent.putExtra(OPEN_START_DUTY, true)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)


        val snoozeIntent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("alarm", 2)
        }
        val snoozePendingIntent: PendingIntent = PendingIntent.getBroadcast(this, 2, snoozeIntent, 0)

        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(context.getText(R.string.notification_title))
            .setContentText(context.getText(R.string.notification_description))
            .setSmallIcon(R.drawable.ic_registrator_register)
            .setColor(ContextCompat.getColor(context, R.color.colorPurpleDark))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle())
            .addAction(R.drawable.ic_registrator_register, getString(R.string.continue_working), snoozePendingIntent)
    }
}