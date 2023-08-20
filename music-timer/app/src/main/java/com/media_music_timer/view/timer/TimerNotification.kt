package com.media_music_timer.view.timer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import com.media_music_timer.R
import com.media_music_timer.view.MainActivity

class TimerNotification private constructor(context: Context) {
    private val notificationBuilder = Notification.Builder(context, CREATE_NOTIFICATION_CHANNEL_ID)
    private val notificationPendingIntent: PendingIntent
    private val notificationTimerCancelAction: Notification.Action

    private val notificationTitle: String by lazy {
        context.getString(R.string.timer_notification_title_text)
    }

    private val notificationContent: String by lazy {
        context.getString(R.string.timer_notification_content_text)
    }

    init {
        context.getSystemService(NotificationManager::class.java)?.createNotificationChannel(
            createNotificationChannel(context),
        )

        notificationPendingIntent = createNotificationPendingIntent(context)
        notificationTimerCancelAction = createNotificationTimerCancelAction(context)
    }

    private fun createNotificationChannel(context: Context): NotificationChannel {
        return NotificationChannel(
            CREATE_NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.app_name),
            NotificationManager.IMPORTANCE_LOW,
        )
    }

    fun getNotificationBuilder(): Notification = initNotificationBuilder().build()

    private fun initNotificationBuilder(): Notification.Builder {
        return notificationBuilder
            .setContentTitle(notificationTitle)
            .setContentText(notificationContent)
            .setSmallIcon(R.drawable.notification_ic_launcher)
            .setContentIntent(notificationPendingIntent)
            .setOngoing(true)
            .addAction(notificationTimerCancelAction)
    }

    private fun createNotificationPendingIntent(context: Context): PendingIntent {
        // 중복 실행 방지 setAction, add Category, Flag
        val notificationIntent = MainActivity.createIntent(context)
        return PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun createNotificationTimerCancelAction(context: Context): Notification.Action {
        return Notification.Action.Builder(
            Icon.createWithResource(
                context,
                R.drawable.notification_ic_launcher,
            ),
            context.getString(R.string.timer_notification_cancel_text),
            createNotificationTimerCancelIntent(context),
        ).build()
    }

    private fun createNotificationTimerCancelIntent(context: Context): PendingIntent {
        // 타이머 취소 인텐트 생성
        val timerCancelIntent = Intent(context, CancelNotificationBroadCastReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            0,
            timerCancelIntent,
            PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        private const val CREATE_NOTIFICATION_CHANNEL_ID = "CREATE_NOTIFICATION_CHANNEL_ID"

        @Volatile
        private var instance: TimerNotification? = null

        fun getInstance(context: Context): TimerNotification {
            synchronized(this) {
                instance?.let {
                    instance = it
                    return it
                }
                return TimerNotification(context)
            }
        }
    }
}
