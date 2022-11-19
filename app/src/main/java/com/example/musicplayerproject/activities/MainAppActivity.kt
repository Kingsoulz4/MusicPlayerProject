package com.example.musicplayerproject.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.musicplayerproject.R
import com.example.musicplayerproject.databinding.ActivityMainAppBinding

class MainAppActivity : AppCompatActivity() {
    private lateinit var mainAppBinding: ActivityMainAppBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainAppBinding = ActivityMainAppBinding.inflate(layoutInflater)
        setContentView(mainAppBinding.root)

    }
}