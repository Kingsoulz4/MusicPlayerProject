package com.example.musicplayerproject.activities

import android.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayerproject.databinding.ActivityMainAppBinding
import com.example.musicplayerproject.fragments.HomeFragment


class MainAppActivity : AppCompatActivity() {
    private lateinit var mainAppBinding: ActivityMainAppBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainAppBinding = ActivityMainAppBinding.inflate(layoutInflater)
        setContentView(mainAppBinding.root)

        supportActionBar?.hide()

        var homeFragment = HomeFragment()


        val fm = supportFragmentManager
        val transaction = fm.beginTransaction()
        transaction.replace(mainAppBinding.transactionLayout.id, homeFragment)
        transaction.commit()

    }
}