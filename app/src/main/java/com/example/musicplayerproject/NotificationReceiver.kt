package com.example.musicplayerproject

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.example.musicplayerproject.ApplicationClass.Companion.ACTION_NEXT
import com.example.musicplayerproject.ApplicationClass.Companion.ACTION_PLAY
import com.example.musicplayerproject.ApplicationClass.Companion.ACTION_PREVIOUS

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val preferences: SharedPreferences = context!!.getSharedPreferences("MusicPlayerPref",
            Service.MODE_PRIVATE
        )

        var actionName = intent?.action
        var serviceIntent = Intent(context, MusicService::class.java);
        if(actionName!=null) {
            when(actionName){
                ACTION_PLAY -> {
                    context.startService(serviceIntent)
                }
                ACTION_NEXT -> {

                }
                ACTION_PREVIOUS -> {

                }
            }
        }
    }
}