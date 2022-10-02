package com.example.musicplayerproject.activities

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.Window
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayerproject.R

var currentURL: String = "null" //global var to save currently playing URL

class MainTestPlay : AppCompatActivity() {
    private var buttonPlay: Button? = null
    private var buttonURL: Button? = null
    private var loadingCircle: ProgressBar? = null
    private var mPlayer: MediaPlayer? = null
    //private var songs: MutableList<Song>? = null

    //private var length: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*2 lines below will hide the title bar*/
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()

        setContentView(R.layout.test_music_menu)

        buttonPlay = findViewById(R.id.buttonPlay)
        buttonURL = findViewById(R.id.buttonURL)
        loadingCircle = findViewById(R.id.progressBar)

        //Test playing two different URLs, one at a time
        buttonPlay?.setOnClickListener {
            playFromURL("https://mp3-s1-m-zmp3.zmdcdn.me/b36802b4ccf325ad7ce2/1729810351076897325?authen=exp=1664803082~acl=/b36802b4ccf325ad7ce2/*~hmac=e549f62406528d14440bb2f86e598620")
        }
        buttonURL?.setOnClickListener{
            playFromURL("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3")
        }
    }

    private fun playFromURL(url: String) {
        MusicTask(this).execute(url)
    }



    internal inner class MusicTask(var context: Context) : AsyncTask<String, Void, Void>() {



        @Deprecated("Deprecated in Java")
        override fun onPreExecute() {
            super.onPreExecute()

            //Insert whatever loading animation you want here
            Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
            loadingCircle?.visibility = VISIBLE
        }

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg url: String?): Void? {
            if (mPlayer == null || url[0] != currentURL) {
                mPlayer?.reset() // Only reset the player if we're playing a different URL

                currentURL = url[0].toString()
                mPlayer = MediaPlayer().apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                        )
                    }
                    setDataSource(url[0])
                    prepare()
                }
            }
            return null
        }


        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

            //Disable loading animation from above
            loadingCircle?.visibility = INVISIBLE

            //Play/Pause
            if (!(mPlayer!!.isPlaying)) {
                Toast.makeText(context, "Playing!", Toast.LENGTH_SHORT).show()
                mPlayer!!.start()
            } else {
                Toast.makeText(context, "Paused!", Toast.LENGTH_SHORT).show()
                mPlayer?.pause()
            }
        }
    }




}

