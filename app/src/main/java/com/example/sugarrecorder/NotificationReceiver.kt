package com.example.sugarrecorder

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Message
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("message") ?: "Message not found"

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "Remind user channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Remind to record", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Record your Sugar")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)   //system time is used for different ids

        val nextTrigger = System.currentTimeMillis() + 24 * 60 * 60 * 1000 // 24 hours in ms
        scheduleNotification(context, nextTrigger, message)
    }
}

//suppressed because permission already granted when app is launched
@SuppressLint("ScheduleExactAlarm")
fun scheduleNotification(context: Context, triggerTime: Long, time: String) {
    val intent = Intent(context, NotificationReceiver::class.java).apply{
        putExtra("message", "Hey its time to measure your $time sugar")
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context, triggerTime.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
}

