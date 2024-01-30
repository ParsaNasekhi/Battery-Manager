package com.example.batterymanager.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import androidx.core.app.NotificationCompat
import com.example.batterymanager.R

class BatteryAlarmService : Service() {

    private var manager: NotificationManager? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startNotification()
        registerReceiver(batteryInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN)
            manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    private var batteryInfoReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context, intent: Intent) {
            val chargeLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            var plugState = when (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)) {
                0 -> "Your phone is using battery."
                else -> "Your phone is charging."
            }
            var imageNum = 0
            if (plugState == "Your phone is charging." && chargeLevel != 100) {
                imageNum = 1
            } else if (plugState == "Your phone is charging." && chargeLevel == 100) {
                imageNum = 2
                plugState = "Your phone battery is fully charged."
                startAlarm()
            } else if (plugState == "Your phone is using battery." && chargeLevel > 15) {
                imageNum = 3
            } else if (plugState == "Your phone is using battery." && chargeLevel <= 15) {
                imageNum = 4
            }

            updateNotification(chargeLevel, plugState, imageNum)
        }
    }

    private fun startNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Loading...")
            .setContentText("wait for battery data!")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun startAlarm() {
        val alarm: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ring = RingtoneManager.getRingtone(applicationContext, alarm)
        ring.play()
        val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(10000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            v.vibrate(10000)
        }
    }

    private fun updateNotification(batteryLevel: Int, plugState: String, imageNum: Int) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(plugState)
            .setContentText("battery charge: $batteryLevel%")
            .setSmallIcon(

                when (imageNum) {
                    1 -> R.drawable.charging
                    2 -> R.drawable.charged
                    3 -> R.drawable.normal_charge
                    4 -> R.drawable.need_charging
                    else -> R.mipmap.ic_launcher
                }

            )
            .build()

        manager?.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "BatteryManagerChannel"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_NAME = "BatteryManagerService"
    }
}