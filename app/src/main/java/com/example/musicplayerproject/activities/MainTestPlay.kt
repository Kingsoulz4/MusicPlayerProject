package com.example.musicplayerproject.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.musicplayerproject.R

class MainTestPlay : AppCompatActivity() {
    private lateinit var buttonPlay: Button
    private lateinit var buttonURL: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*2 lines below will hide the title bar*/
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()

        setContentView(R.layout.test_music_menu)

        buttonPlay = findViewById(R.id.buttonPlay)
        buttonURL = findViewById(R.id.buttonURL)
        //Test playing two different URLs, one at a time
        buttonPlay.setOnClickListener {
            //playFromURL("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3")
            playFromURL("https://mcloud-bf-s7-mv-zmp3.zmdcdn.me/CzdoiUkfjGg/347a4caac1ee28b071ff/04ade142f2071b594216/1080/So-Far-Away.mp4?authen=exp=1668864080~acl=/CzdoiUkfjGg/*~hmac=7078868bc3f219f3be3885e97a4e92cc")
            //playFromURL("https://mp3-s1-m-zmp3.zmdcdn.me/b36802b4ccf325ad7ce2/1729810351076897325?authen=exp=1665064231~acl=/b36802b4ccf325ad7ce2/*~hmac=367ff8cee54ba10712d7bfe5c8261a9e")
        }
        buttonURL.setOnClickListener{
            playFromURL("https://media.discordapp.net/attachments/699391546365050901/1009080041075900447/redditsave.com_new_minecraft_update-nrv4ezcpe2i91-220.mp4")
        }

        //Check for storage permissions. Implement this in full app, or download function won't work
        checkPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            101)
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@MainTestPlay, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainTestPlay, arrayOf(permission), requestCode)
        } else {
            Toast.makeText(this@MainTestPlay, "Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playFromURL(url: String) {
        var intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("Song_URL", url)

        Log.v("Music", "Start PlayerActivity")
        startActivity(intent)
    }
}

