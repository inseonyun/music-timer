package com.media_music_timer.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.media_music_timer.R
import com.media_music_timer.databinding.ActivityMainBinding
import com.media_music_timer.view.timer.TimerFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setFrameLayout()
    }

    private fun setFrameLayout() {
        val fragment = searchFragment(TAG_TIMER_FRAGMENT)

        supportFragmentManager.commit {
            replace(R.id.frame_main, fragment, TAG_TIMER_FRAGMENT)
        }
    }

    private fun searchFragment(tag: String): Fragment {
        supportFragmentManager.findFragmentByTag(tag)?.let {
            return it
        }
        return TimerFragment()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_reset -> {
                resetTimer()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun resetTimer() {
        supportFragmentManager.findFragmentByTag(TAG_TIMER_FRAGMENT)?.let {
            if (it is TimerFragment) {
                it.resetTimer()
            }
        }
    }

    companion object {
        private const val TAG_TIMER_FRAGMENT = "TAG_TIMER_FRAGMENT"
    }
}
