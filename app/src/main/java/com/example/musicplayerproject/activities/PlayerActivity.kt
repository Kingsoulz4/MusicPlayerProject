package com.example.musicplayerproject.activities

import android.content.*
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayerproject.MusicService
import com.example.musicplayerproject.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

//var current_URL: String = ""

class PlayerActivity : AppCompatActivity(), ServiceConnection {
    private lateinit var progressBar: ProgressBar
    private lateinit var backButton: ImageButton
    private lateinit var menuButton: ImageButton
    private lateinit var playButton: FloatingActionButton
    private lateinit var songPrevButton: ImageButton
    private lateinit var songSkipButton: ImageButton
    private lateinit var songProgressBar: SeekBar
    private lateinit var songTimePassed: TextView
    private lateinit var songTimeTotal: TextView
    private lateinit var nowPlayingText: TextView


    private lateinit var newURL: String
    private var mediaSessionCompat: MediaSessionCompat? = null
    private var musicService: MusicService? = null
    private var handle: Handler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        setContentView(R.layout.music_play_screen)
        viewFinder()

        mediaSessionCompat = MediaSessionCompat(baseContext, "My audio")

        serviceSetup()

        onPlayButtonClick()
        onBackButtonClick()
        onProgressBarChange()
    }

    override fun onResume() {
        Log.v("Music", "Reached Resume")
        //var intent = Intent(this, MusicService::class.java)
        //bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        super.onResume()
    }

    private fun viewFinder() {
        progressBar = findViewById(R.id.progressBar)
        playButton = findViewById(R.id.playButton)
        songProgressBar = findViewById(R.id.songProgressBar)
        songSkipButton = findViewById(R.id.songSkipButton)
        songPrevButton = findViewById(R.id.songPrevButton)
        songTimePassed = findViewById(R.id.songTimePassed)
        songTimeTotal = findViewById(R.id.songTimeTotal)
        nowPlayingText = findViewById(R.id.nowPlayingText)
        backButton = findViewById(R.id.backButton)
        menuButton = findViewById(R.id.menuButton)
        Log.v("Music", "Reached ViewFinderDone")
    }

    private fun serviceSetup() {
        newURL = intent.getStringExtra("Song_URL").toString()
        var preferences: SharedPreferences = getSharedPreferences("MusicPlayerPref", MODE_PRIVATE)
        var editor: SharedPreferences.Editor? = preferences.edit()

        if (musicService != null || newURL != preferences.getString("url", "null")) {
            editor?.putString("url", newURL)
            editor?.apply()
            MusicTask(this).execute(newURL)
        }

    }

    private fun onPlayButtonClick() {
        playButton.setOnClickListener {
            playPause()
            var preferences: SharedPreferences = getSharedPreferences("MusicPlayerPref", MODE_PRIVATE)
            var temp: String? = preferences.getString("url", "null")
            Log.v("Music", "$temp")
        }
    }

    private fun onBackButtonClick() {
        backButton.setOnClickListener {
            var intent = Intent(this, MainTestPlay::class.java)
            Log.v("Music", "Go Back")
            startActivity(intent)
        }

    }
    private fun onProgressBarChange() {
        songProgressBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        musicService!!.seekTo(progress)
                    }
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {

                }
                override fun onStopTrackingTouch(p0: SeekBar?) {

                }
            }
        )
    }

    private fun createTimeLabel(time: Int): String {
        var timeLabel: String
        var min = time / 1000 / 60
        var sec = time / 1000 % 60

        timeLabel = "$min:"
        if (sec < 10) timeLabel += "0"
        timeLabel += sec

        return timeLabel
    }

    fun playPause() {
            if (musicService!!.isPlaying()) {
                musicService!!.pause()
                playButton.setImageResource(R.drawable.player_pause)
            } else {
                musicService!!.start()
                playButton.setImageResource(R.drawable.player_play)
            }
    }

    private fun doBindService() {
        bindService(Intent(this,
            MusicService::class.java), this, Context.BIND_AUTO_CREATE)

        val startNotStickyIntent = Intent(this, MusicService::class.java)
        startNotStickyIntent.putExtra("Song_URL", newURL)
        Log.v("Music", "Reached Binder")
        startService(startNotStickyIntent)
    }

    private fun passDataToMusicService() {
        intent = Intent(this, MusicService::class.java)
        intent.putExtra("Song_URL", newURL)
        startService(intent)
    }

    inner class MusicTask(private var context: Context) : AsyncTask<String, Void, String>() {
        @Deprecated("Deprecated in Java")
        override fun onPreExecute() {
            super.onPreExecute()
            nowPlayingText.text = "Loading..."
            progressBar.visibility = VISIBLE
        }

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg url: String): String? {
            Log.v("Music", "Reached Background")
            doBindService()
            Log.v("Music", "Reached ServiceBind")
            return url[0]
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            nowPlayingText.text = "Now playing"
            progressBar.visibility = INVISIBLE
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        var myBinder: MusicService.MyBinder = service as MusicService.MyBinder
        musicService = myBinder.getService()
        this@PlayerActivity.runOnUiThread(
            object : Runnable {
                override fun run() {
                    songProgressBar.max = musicService!!.getDuration()
                    songProgressBar.progress = musicService!!.getCurrentPosition()
                    songTimePassed.text = createTimeLabel(musicService!!.getCurrentPosition())
                    songTimeTotal.text = createTimeLabel(musicService!!.getDuration())

                    handle.postDelayed(this, 100)
                }
            }
        )
        Log.v("Music", "Reached ServiceConnect")
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }
}