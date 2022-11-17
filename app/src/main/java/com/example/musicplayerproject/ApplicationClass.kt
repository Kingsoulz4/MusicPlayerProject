package com.example.musicplayerproject

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.os.Build
import com.example.musicplayerproject.activities.Communication

class ApplicationClass: Application() {
    companion object{
        const val CHANNEL_ID_1 = "channel1"
        const val ACTION_PREVIOUS = "actionprevious"
        const val ACTION_NEXT = "actionnext"
        const val ACTION_PLAY = "actionplay"
    }
    override fun onCreate() {
        super.onCreate()
        var preferences: SharedPreferences = getSharedPreferences(Communication.PREF_FILE, MODE_PRIVATE)
        var editor: SharedPreferences.Editor? = preferences.edit()
        editor?.putString(Communication.URL, "null")
        editor?.putString(Communication.CONTROL, "null")
        editor?.apply()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            var channel1 = NotificationChannel(CHANNEL_ID_1, "channel(1)", NotificationManager.IMPORTANCE_LOW)
            channel1.description = "Channel 1 Desc.."
            var notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel1)
        }
    }
}