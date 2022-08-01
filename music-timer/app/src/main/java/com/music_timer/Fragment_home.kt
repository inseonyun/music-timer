package com.music_timer

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.music_timer.databinding.FragmentHomeBinding

class Fragment_home : Fragment() {
    // ViewBinding
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        // editText 숫자 제한 처리 : 23시 초과 안 됨, 분의 경우 59 초과 안 됨
        binding.etHour.addTextChangedListener {
            LimitChecker(binding.etHour,23)
        }
        binding.etMin.addTextChangedListener {
            LimitChecker(binding.etMin, 59)
        }

        // 타이머 시작 이벤트 처리
        binding.btnStart.setOnClickListener {
            // 중복 실행 방지
            if(countDownTimer != null) {
                // 만약 input 값이 있는데 countDonwTimer가 null이 아니라면 멈춤 한 것이므로 다시 시작하도록 함
                if(!binding.etHour.text.toString().equals("") && !binding.etMin.text.toString().equals("") && !timer_run_checker)
                {
                    now_hour = binding.etHour.text.toString().toLong()
                    now_min = binding.etMin.text.toString().toLong()

                    val totalTime = ((now_hour * 60 * 60 * 1000) + (now_min * 60 * 1000) + (now_sec * 1000))

                    // set Progressbar
                    first_progress_value = totalTime.toInt() / 1000
                    now_progress_value = first_progress_value
                    binding.proressbar.progress = 100

                    timer_start(totalTime)
                }
                else
                {
                    Toast.makeText(view.context, "타이머가 아직 실행중입니다.\n멈추고 시작 해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                val input_hour = binding.etHour.text.toString()
                val input_min = binding.etMin.text.toString()

                if((input_hour == null || input_hour.equals("")) || (input_min == null || input_min.equals("")))
                {
                    Toast.makeText(view.context, "시간을 설정해주세요.", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    val int_hour = Integer.parseInt(input_hour)
                    val int_min = Integer.parseInt(input_min)

                    if(int_hour == 0 && int_min == 0)
                    {
                        Toast.makeText(view.context, "시간을 설정해주세요.", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        val totalTime = ((int_hour * 60 * 60 * 1000) + (int_min * 60 * 1000)).toLong()

                        // set Progressbar
                        first_progress_value = totalTime.toInt() / 1000
                        now_progress_value = first_progress_value
                        binding.proressbar.progress = 100

                        timer_start(totalTime)
                    }
                }
            }
        }

        // 타이머 멈춤 이벤트 처리
        binding.btnStop.setOnClickListener {
            if(countDownTimer == null)
            {
                Toast.makeText(view.context, "타이머를 먼저 실행해주세요.", Toast.LENGTH_SHORT).show()
            }
            else
            {
                binding.etHour.isEnabled = true
                binding.etMin.isEnabled = true
                binding.tvSec.setText("00")

                now_sec = 0

                timer_run_checker = false
                countDownTimer!!.cancel()
            }
        }

        return view
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

    fun timer_start(totalTime: Long) {
        timer_run_checker = true

        countDownTimer = object: CountDownTimer(totalTime, 1000) {
            override fun onTick(p0: Long) {
                // tic 중에는 텍스트 입력할 수 없도록 함
                binding.etHour.isEnabled = false
                binding.etMin.isEnabled = false

                now_hour = p0 / (60 * 60 * 1000)

                var tmp_min = p0 - now_hour * 60 * 60 * 1000
                now_min = tmp_min / (60 * 1000)

                var tmp_sec = tmp_min - now_min * 60 * 1000
                now_sec = tmp_sec / 1000

                var str_hour = now_hour.toString()
                var str_min = now_min.toString()
                var str_sec = now_sec.toString()

                if(str_hour.length == 1)
                {
                    str_hour = "0" + str_hour
                }
                if(str_min.length == 1)
                {
                    str_min = "0" + str_min
                }
                if(str_sec.length == 1)
                {
                    str_sec = "0" + str_sec
                }

                // set Progressbar
                now_progress_value--
                binding.proressbar.progress = (now_progress_value.toDouble() / first_progress_value.toDouble() * 100.0).toInt()

                binding.etHour.setText(str_hour)
                binding.etMin.setText(str_min)
                binding.tvSec.setText(str_sec)
            }

            override fun onFinish() {
                binding.etHour.isEnabled = true
                binding.etMin.isEnabled = true

                timer_run_checker = false
                countDownTimer = null

                // music stop
                stop_music()

                Toast.makeText(context, "타이머 종료", Toast.LENGTH_SHORT).show()
            }
        }.start()
    }

    fun stop_music()
    {
        val mAudioManager: AudioManager = context?.applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mAudioManager.requestAudioFocus(AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN) // 오디오 포커스를 영속적으로 획득
            .setAudioAttributes(AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build())
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener {} // 위 소스와 마찬가지 이유로 listner 구현 안함.
            .build())
    }
}