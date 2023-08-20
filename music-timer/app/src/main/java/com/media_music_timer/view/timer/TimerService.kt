package com.media_music_timer.view.timer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.CountDownTimer
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.media_music_timer.R
import com.media_music_timer.Time
import com.media_music_timer.model.TimeModel
import com.media_music_timer.model.TimeModel.Companion.toModel
import com.media_music_timer.model.TimeModel.Companion.toUIModel
import com.media_music_timer.util.getParcelableCompat
import com.media_music_timer.util.showToast

class TimerService : Service() {
    // create countDownTimer
    var countDownTimer: CountDownTimer? = null

    // progress Value
    var first_progress_value = 0
    var now_progress_value = 0

    // data send intent
    var totalTime = 0L

    var notification: Notification? = null
    var notificationBuilder: Notification.Builder? = null
    var notificationManager: NotificationManager? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            return START_STICKY
        }

        if (Build.VERSION.SDK_INT >= VERSION_CODES.P) {
            val serviceChannel = NotificationChannel(
                NotificationManager.EXTRA_NOTIFICATION_CHANNEL_ID,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_LOW,
            )

            notificationManager = getSystemService(NotificationManager::class.java)!!
            notificationManager!!.createNotificationChannel(serviceChannel)

            notification = TimerNotification.getInstance(this).getNotificationBuilder()

            startForeground(1, notification)

            intent.getParcelableCompat<TimeModel>(KEY_TIMER_SERVICE)?.let {
                setTimer(it)
                startTimer(totalTime)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        throw UnsupportedOperationException()
    }

    // 서비스 종료 될 때 타이머 멈춤 처리
    override fun onDestroy() {
        super.onDestroy()

        val it = Intent(INTENT_FILTER_TIMER_CANCEL_SERVICE)

        it.putExtra(KEY_TIMER_SERVICE_FINISH, true)
        LocalBroadcastManager.getInstance(this).sendBroadcast(it)
        countDownTimer?.cancel()
    }

    // countdownTimer
    private fun setTimer(time: TimeModel) {
        totalTime = time.toModel().getSecondOfDayTotalTime() * 1000L

        // set Progress
        first_progress_value = totalTime.toInt() / 1000
        now_progress_value = first_progress_value
    }

    private fun startTimer(totalTime: Long) {
        countDownTimer = object : CountDownTimer(totalTime, 1000) {
            override fun onTick(p0: Long) {
                val countTime = Time.from(p0 / 1000L)

                // set Progressbar
                now_progress_value--

                val progressPercentValue =
                    (now_progress_value.toDouble() / first_progress_value.toDouble() * 100.0).toInt()

                notificationBuilder?.setContentText("${countTime.getHour()}:${countTime.getMinute()}:${countTime.getSecond()}")

                notificationManager?.notify(1, notification)

                val intent = createTimerIntent(progressPercentValue, countTime.toUIModel())
                LocalBroadcastManager.getInstance(this@TimerService).sendBroadcast(intent)
            }

            override fun onFinish() {
                stopMusic()
                showToast(this@TimerService, R.string.timer_toast_timer_finish)
                stopSelf()
            }
        }.start()
    }

    private fun stopMusic() {
        val mAudioManager: AudioManager =
            applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mAudioManager.requestAudioFocus(
            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN) // 오디오 포커스를 영속적으로 획득
                .setAudioAttributes(
                    AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA).build(),
                ).setAcceptsDelayedFocusGain(true).setOnAudioFocusChangeListener {}.build(),
        )
    }

    private fun createTimerIntent(
        progress: Int,
        time: TimeModel,
    ): Intent {
        val intent = Intent(INTENT_FILTER_TIMER_SERVICE)
        intent.putExtra(KEY_TIMER_SERVICE_PROGRESS, progress)
        intent.putExtra(KEY_TIMER_SERVICE_TIME, time)

        return intent
    }

    companion object {
        internal const val INTENT_FILTER_TIMER_SERVICE = "INTENT_FILTER_TIMER_SERVICE"
        internal const val INTENT_FILTER_TIMER_CANCEL_SERVICE = "INTENT_FILTER_TIMER_CANCEL_SERVICE"

        internal const val KEY_TIMER_SERVICE = "KEY_TIMER_SERVICE"
        internal const val KEY_TIMER_SERVICE_TIME = "KEY_TIMER_SERVICE_TIME"
        internal const val KEY_TIMER_SERVICE_FINISH = "KEY_TIMER_SERVICE_FINISH"
        internal const val KEY_TIMER_SERVICE_PROGRESS = "KEY_TIMER_SERVICE_PROGRESS"

        fun createIntent(
            context: Context,
            time: TimeModel,
        ): Intent {
            val intent = Intent(context, TimerService::class.java)
            intent.putExtra(KEY_TIMER_SERVICE, time)
            return intent
        }
    }
}
