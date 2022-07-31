package com.music_timer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // frame layout
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_main, Fragment_home())
            .commit()
    }
}