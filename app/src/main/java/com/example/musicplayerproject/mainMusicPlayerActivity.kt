package com.example.musicplayerproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window

class mainMusicPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*2 lines below will hide the title bar*/
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()

        setContentView(R.layout.music_play_screen)
        //cdd
    }
}