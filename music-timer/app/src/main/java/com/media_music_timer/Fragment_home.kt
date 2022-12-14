package com.media_music_timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.media_music_timer.databinding.FragmentHomeBinding

class Fragment_home : Fragment() {
    // ViewBinding
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // timer run checker
    var timer_run_checker: Boolean = false

    var intent: Intent? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        AllEnabled(false)

        activity?.let{register(it)}

        // editText 숫자 제한 처리 : 23시 초과 안 됨, 분의 경우 59 초과 안 됨
        binding.etHour.addTextChangedListener {
            LimitChecker(binding.etHour,23)
        }
        binding.etMin.addTextChangedListener {
            LimitChecker(binding.etMin, 59)
        }

        // 타이머 시작 이벤트 처리
        binding.btnStart.setOnClickListener {
            var input_hour = binding.etHour.text.toString()
            var input_min = binding.etMin.text.toString()
            val input_sec = binding.tvSec.text.toString()

            if((input_hour.equals("") && input_min.equals("") ) || (input_hour.isNullOrEmpty() && input_min.isNullOrEmpty()))
            {
                Toast.makeText(view.context, "시간을 설정해주세요.", Toast.LENGTH_SHORT).show()
            }
            else
            {
                if(timer_run_checker)
                {
                    Toast.makeText(view.context, "타이머가 아직 실행중입니다.\n멈추고 시작 해주세요.", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    // 시, 분 둘 중 공백이 있으면 0으로 치환해줌
                    if(input_hour.equals(""))
                    {
                        input_hour = "0"
                    }

                    if(input_min.equals(""))
                    {
                        input_min = "0"
                    }

                    val int_input_hour = input_hour.toInt()
                    val int_input_min = input_min.toInt()
                    val int_input_sec = input_sec.toInt()

                    if(int_input_hour == 0 && int_input_min == 0 && int_input_sec == 0)
                    {
                        Toast.makeText(view.context, "시간을 설정해주세요.", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        timer_run_checker = true
                        binding.proressbar.progress = 100

                        intent = Intent(activity, TimerService::class.java)
                        intent!!.putExtra("input_hour", int_input_hour)
                        intent!!.putExtra("input_min", int_input_min)
                        intent!!.putExtra("input_sec", int_input_sec)

                        activity?.startForegroundService(intent)
                    }
                }
            }
        }

        // 타이머 멈춤 이벤트 처리
        binding.btnStop.setOnClickListener {
            if(!timer_run_checker)
            {
                Toast.makeText(view.context, "타이머를 먼저 실행해주세요.", Toast.LENGTH_SHORT).show()
            }
            else
            {
                stopTimer()
            }
        }

        return view
    }

    private fun register(ctx: Context) {
        AllEnabled(true)
        LocalBroadcastManager.getInstance(ctx).registerReceiver(
            TimerReceiver, IntentFilter("TimerService")
        )
        LocalBroadcastManager.getInstance(ctx).registerReceiver(
            TimerCancelReceiver, IntentFilter("TimerCancelService")
        )
    }

    fun unRegister(ctx: Context) {
        LocalBroadcastManager.getInstance(ctx).registerReceiver(
            TimerReceiver, IntentFilter("TimerService")
        )

        LocalBroadcastManager.getInstance(ctx).registerReceiver(
            TimerCancelReceiver, IntentFilter("TimerCancelService")
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.let { unRegister(it) }
    }

    private val TimerCancelReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(intent?.getBooleanExtra("finish", false)) {
                EditTextEnabled(true)
                timer_run_checker = false
            }
        }
    }

    private val TimerReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            timer_run_checker = true

            val int_progress_value = intent?.getIntExtra("int_progress_value", -1)
            val str_hour = intent?.getStringExtra("str_hour")
            val str_min = intent?.getStringExtra("str_min")
            val str_sec = intent?.getStringExtra("str_sec")

            if(int_progress_value != -1)
            {
                binding.proressbar.setProgress(int_progress_value, true)
            }

            if(!str_hour.equals("") && !str_hour.isNullOrEmpty())
            {
                binding.etHour.setText(str_hour)
            }

            if(!str_min.equals("") && !str_min.isNullOrEmpty())
            {
                binding.etMin.setText(str_min)
            }

            if(!str_sec.equals("") && !str_sec.isNullOrEmpty())
            {
                binding.tvSec.setText(str_sec)
            }

            if(int_progress_value == 0)
            {
                binding.etHour.setText("")
                binding.etMin.setText("")
                binding.tvSec.setText("00")

                timer_run_checker = false
                EditTextEnabled(true)
            }
            else
            {
                EditTextEnabled(false)
            }
        }
    }

    fun stopTimer() {
        EditTextEnabled(true)

        timer_run_checker = false

        intent = Intent(activity, TimerService::class.java)
        activity?.stopService(intent)
    }

    fun resetTimer() {
        stopTimer()

        binding.etHour.setText("")
        binding.etMin.setText("")
        binding.tvSec.setText("00")

        binding.proressbar.setProgress(0, true)

        Toast.makeText(context, "타이머가 초기화 되었습니다.", Toast.LENGTH_SHORT).show()
    }

    fun EditTextEnabled(enabled: Boolean) {
        binding.etHour.isEnabled = enabled
        binding.etMin.isEnabled = enabled
    }

    fun AllEnabled(enabled: Boolean) {
        binding.etHour.isEnabled = enabled
        binding.etMin.isEnabled = enabled

        binding.btnStop.isEnabled = enabled
        binding.btnStart.isEnabled = enabled
    }

    fun LimitChecker(et_time: EditText , limit_time: Int) {
            if(!et_time.text.toString().equals(""))
            {
                val time: Int = Integer.parseInt(et_time.text.toString())

                if(time > limit_time)
                {
                    // 초과 입력 시 제거 함
                    et_time.setText(et_time.text.substring(0, et_time.length() - 1))

                    // 입력 커서 앞으로 간 것을 맨 뒤로 다시 옮겨줌
                    et_time.setSelection(et_time.length())
                }
            }
    }
}