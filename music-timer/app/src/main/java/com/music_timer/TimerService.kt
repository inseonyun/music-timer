package com.music_timer

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class TimerService : Service() {
    // create countDownTimer
    var countDownTimer: CountDownTimer? = null

    // timer run checker
    var timer_run_checker: Boolean = false

    // now time
    var now_hour: Long = 0
    var now_min: Long = 0
    var now_sec: Long = 0

    // progress Value
    var first_progress_value = 0
    var now_progress_value = 0

    // data send intent
    var totalTime = 0L

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent == null){
            return START_STICKY
        }

        if (Build.VERSION.SDK_INT >= 26) {
            val serviceChannel = NotificationChannel(
                NotificationManager.EXTRA_NOTIFICATION_CHANNEL_ID,
                "음악 타이머",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)!!
            manager!!.createNotificationChannel(serviceChannel)

            val notificationIntent = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
            val notification: Notification = Notification.Builder(this, NotificationManager.EXTRA_NOTIFICATION_CHANNEL_ID)
                .setContentTitle("음악 타이머 동작중")
                .setContentText("알림 설명")
                .setSmallIcon(R.drawable.notification_ic_launcher)
                .setContentIntent(pendingIntent)
                .build()

            startForeground(1, notification)

            val input_hour = intent.getIntExtra("input_hour", 0)
            val input_min = intent.getIntExtra("input_min", 0)

            set_timer(input_hour, input_min)
            start_timer(totalTime)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        throw UnsupportedOperationException()
    }

    // countdownTimer
    fun set_timer(input_hour: Int, input_min: Int) {
        // 중복 실행 방지

        if((input_hour == null || input_hour.equals("")) || (input_min == null || input_min.equals("")))
        {
            Toast.makeText(applicationContext, "시간을 설정해주세요.", Toast.LENGTH_SHORT).show()
        }
        else
        {
            if(input_hour == 0 && input_min == 0)
            {
                Toast.makeText(applicationContext, "시간을 설정해주세요.", Toast.LENGTH_SHORT).show()
            }
            else
            {
                totalTime = ((input_hour * 60 * 60 * 1000) + (input_min * 60 * 1000)).toLong()

                // set Progressbar
                first_progress_value = totalTime.toInt() / 1000
                now_progress_value = first_progress_value
                //binding.proressbar.progress = 100
            }
        }
    }
    fun start_timer(totalTime: Long) {
        timer_run_checker = true

        countDownTimer = object : CountDownTimer(totalTime, 1000) {
            override fun onTick(p0: Long) {
                // tic 중에는 텍스트 입력할 수 없도록 함
                //EditTextEnabled(false)

                now_hour = p0 / (60 * 60 * 1000)

                var tmp_min = p0 - now_hour * 60 * 60 * 1000
                now_min = tmp_min / (60 * 1000)

                var tmp_sec = tmp_min - now_min * 60 * 1000
                now_sec = tmp_sec / 1000

                var str_hour = now_hour.toString()
                var str_min = now_min.toString()
                var str_sec = now_sec.toString()

                if (str_hour.length == 1) {
                    str_hour = "0" + str_hour
                }
                if (str_min.length == 1) {
                    str_min = "0" + str_min
                }
                if (str_sec.length == 1) {
                    str_sec = "0" + str_sec
                }

                // set Progressbar
                now_progress_value--

                val progress_percent_value =
                    (now_progress_value.toDouble() / first_progress_value.toDouble() * 100.0).toInt()

                val it: Intent = Intent("TimerService")
                it.putExtra("int_progress_value", progress_percent_value)
                it.putExtra("str_hour", str_hour)
                it.putExtra("str_min", str_min)
                it.putExtra("str_sec", str_sec)

                LocalBroadcastManager.getInstance(this@TimerService).sendBroadcast(it)
            }

            override fun onFinish() {
                stop_music()
                stopSelf()

                timer_run_checker = false
                countDownTimer = null

                //binding.proressbar.setProgress(0, true)

                Toast.makeText(applicationContext, "타이머 종료", Toast.LENGTH_SHORT).show()
            }
        }.start()
    }

    fun stop_music()
    {
        val mAudioManager: AudioManager = applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mAudioManager.requestAudioFocus(
            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN) // 오디오 포커스를 영속적으로 획득
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build())
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener {} // 위 소스와 마찬가지 이유로 listner 구현 안함.
                .build())
    }

}