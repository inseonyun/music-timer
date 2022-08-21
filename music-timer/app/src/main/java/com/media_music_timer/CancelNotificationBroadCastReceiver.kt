package com.media_music_timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CancelNotificationBroadCastReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val intent = Intent(p0?.applicationContext, TimerService::class.java)

        p0?.stopService(intent)
    }
}