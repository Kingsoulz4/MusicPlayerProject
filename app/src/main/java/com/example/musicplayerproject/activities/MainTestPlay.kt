package com.example.musicplayerproject.activities

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayerproject.R


class MainTestPlay : AppCompatActivity() {
    private var buttonPlay: Button? = null
    private var buttonURL: Button? = null

    private var mPlayer: MediaPlayer? = null
    //private var songs: MutableList<Song>? = null
    private var currentURL: String = "null" //save currently playing URL

    //private var length: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*2 lines below will hide the title bar*/
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()


        setContentView(R.layout.test_music_menu)


        buttonPlay = findViewById(R.id.buttonPlay)
        buttonURL = findViewById(R.id.buttonURL)

        //Test playing two different URLs, one at a time
        buttonPlay?.setOnClickListener {
            playFromURL("https://mp3-s1-m-zmp3.zmdcdn.me/b36802b4ccf325ad7ce2/1729810351076897325?authen=exp=1664803082~acl=/b36802b4ccf325ad7ce2/*~hmac=e549f62406528d14440bb2f86e598620")
        }
        buttonURL?.setOnClickListener{
            playFromURL("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3")
        }

    }

    private fun playFromURL(url: String) {
        if (mPlayer == null || url != currentURL) {
            mPlayer?.reset() // Only reset the player if we're playing a different URL

            currentURL = url
            mPlayer = MediaPlayer().apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                }
                setDataSource(url)
                prepare()
            }
        }

        //Play/Pause control
        if (!(mPlayer!!.isPlaying)) {

            mPlayer!!.start()
            Toast.makeText(this, "Now playing!", Toast.LENGTH_SHORT).show()
        } else {
            mPlayer?.pause()
            Toast.makeText(this, "Paused!", Toast.LENGTH_SHORT).show()
        }
    }




}

