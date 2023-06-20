package com.media_music_timer.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.media_music_timer.R
import com.media_music_timer.view.timer.TimerFragment

class MainActivity : AppCompatActivity() {
    var fragment_home: TimerFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragment_home = TimerFragment()

        // frame layout
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_main, fragment_home!!)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_reset -> { ResetTimer() }
        }

        return super.onOptionsItemSelected(item)
    }

    fun ResetTimer() {
        fragment_home!!.resetTimer()
    }
}
