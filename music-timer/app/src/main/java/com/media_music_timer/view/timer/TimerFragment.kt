package com.media_music_timer.view.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.media_music_timer.R
import com.media_music_timer.databinding.FragmentTimerBinding
import com.media_music_timer.model.TimeModel
import com.media_music_timer.model.TimeModel.Companion.toModel
import com.media_music_timer.util.getParcelableCompat
import com.media_music_timer.util.showToast
import com.media_music_timer.view.timer.TimerService.Companion.INTENT_FILTER_TIMER_CANCEL_SERVICE
import com.media_music_timer.view.timer.TimerService.Companion.INTENT_FILTER_TIMER_SERVICE
import com.media_music_timer.view.timer.TimerService.Companion.KEY_TIMER_SERVICE_FINISH
import com.media_music_timer.view.timer.TimerService.Companion.KEY_TIMER_SERVICE_PROGRESS
import com.media_music_timer.view.timer.TimerService.Companion.KEY_TIMER_SERVICE_TIME
import java.time.LocalTime

class TimerFragment : Fragment() {
    // ViewBinding
    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    // timer run checker
    private var timerRunChecker: Boolean = false

    private var intent: Intent? = null

    private val timerCancelReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            _binding ?: return

            if (intent.getBooleanExtra(KEY_TIMER_SERVICE_FINISH, false)) {
                setEnableAll(true)
                timerRunChecker = false
            }
        }
    }

    private val timerReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            _binding ?: return

            timerRunChecker = true

            val time = intent.getParcelableCompat<TimeModel>(KEY_TIMER_SERVICE_TIME) ?: return
            val progressValue = intent.getIntExtra(KEY_TIMER_SERVICE_PROGRESS, -1)

            if (progressValue != -1) {
                binding.progressbar.setProgress(progressValue, true)
            }

            setTime(time)

            if (progressValue == 0) {
                timerRunChecker = false
                setEnableAll(true)
            } else {
                setEnableAll(false)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { registerReceiver(it) }

        setEditTextChangeListener()

        setTimerStartButton()
        setTimerCancelButton()
    }

    private fun registerReceiver(context: Context) {
        LocalBroadcastManager.getInstance(context).registerReceiver(
            timerReceiver, IntentFilter(INTENT_FILTER_TIMER_SERVICE)
        )
        LocalBroadcastManager.getInstance(context).registerReceiver(
            timerCancelReceiver, IntentFilter(INTENT_FILTER_TIMER_CANCEL_SERVICE)
        )
    }

    private fun setTime(time: TimeModel) {
        time.toModel().apply {
            setHour(getHour())
            setMinute(getMinute())
            setSecond(getSecond())
        }
    }

    private fun setHour(hour: Int) {
        binding.etHour.setText(hour.toString())
    }

    private fun setMinute(minute: Int) {
        binding.etMin.setText(minute.toString())
    }

    private fun setSecond(second: Int) {
        binding.tvSec.text = second.toString()
    }

    private fun setEditTextChangeListener() {
        // editText 숫자 제한 처리 : 23시 초과 안 됨, 분의 경우 59 초과 안 됨
        binding.etHour.addTextChangedListener {
            checkTimeLimit(binding.etHour, 23)
        }
        binding.etMin.addTextChangedListener {
            checkTimeLimit(binding.etMin, 59)
        }
    }

    private fun setTimerStartButton() {
        // 타이머 시작 이벤트 처리
        binding.btnStart.setOnClickListener {
            val inputHour = binding.etHour.text.toString().toIntOrNull() ?: 0
            val inputMin = binding.etMin.text.toString().toIntOrNull() ?: 0
            val inputSec = binding.tvSec.text.toString().toIntOrNull() ?: 0
            val time = TimeModel(LocalTime.of(inputHour, inputMin, inputSec))

            if (inputHour == 0 && inputMin == 0 && inputSec == 0) {
                showToast(requireContext(), R.string.timer_toast_input)
                return@setOnClickListener
            }

            if (timerRunChecker) {
                showToast(requireContext(), R.string.timer_toast_timer_is_running)
                return@setOnClickListener
            }

            timerRunChecker = true
            binding.progressbar.progress = 100

            intent = TimerService.createIntent(requireContext(), time)
            activity?.startForegroundService(intent)
        }
    }

    private fun setTimerCancelButton() {
        // 타이머 멈춤 이벤트 처리
        binding.btnStop.setOnClickListener {
            if (!timerRunChecker) {
                showToast(requireContext(), R.string.timer_toast_please_timer_start)
                return@setOnClickListener
            }
            stopTimer()
        }
    }

    private fun stopTimer() {
        setEnableAll(true)

        timerRunChecker = false

        intent = Intent(activity, TimerService::class.java)
        activity?.stopService(intent)
    }

    fun resetTimer() {
        stopTimer()

        binding.etHour.setText("")
        binding.etMin.setText("")
        binding.tvSec.text = DEFAULT_ZERO_TEXT

        binding.progressbar.setProgress(0, true)
        showToast(requireContext(), R.string.timer_toast_timer_is_reset)
    }

    private fun setEnableAll(enabled: Boolean) {
        binding.etHour.isEnabled = enabled
        binding.etMin.isEnabled = enabled

        binding.btnStop.isEnabled = enabled
        binding.btnStart.isEnabled = enabled
    }

    private fun checkTimeLimit(time: EditText, limitTime: Int) {
        time.text.toString().toIntOrNull()?.let {
            if (it > limitTime) {
                // 초과 입력 시 제거 함
                time.setText(time.text.substring(0, time.length() - 1))

                // 입력 커서 앞으로 간 것을 맨 뒤로 다시 옮겨줌
                time.setSelection(time.length())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.let { registerReceiver(it) }
        _binding = null
        intent = null
    }

    companion object {
        private const val DEFAULT_ZERO_TEXT = "00"
    }
}
