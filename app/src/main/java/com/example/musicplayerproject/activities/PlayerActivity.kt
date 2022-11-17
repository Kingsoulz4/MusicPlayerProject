package com.example.musicplayerproject.activities

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.media.session.MediaSessionManager
import android.net.Uri
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.musicplayerproject.ActionPlaying
import com.example.musicplayerproject.ApplicationClass.Companion.ACTION_NEXT
import com.example.musicplayerproject.ApplicationClass.Companion.ACTION_PLAY
import com.example.musicplayerproject.ApplicationClass.Companion.ACTION_PREVIOUS
import com.example.musicplayerproject.ApplicationClass.Companion.CHANNEL_ID_1
import com.example.musicplayerproject.MusicService
import com.example.musicplayerproject.NotificationReceiver
import com.example.musicplayerproject.R
import com.google.android.material.floatingactionbutton.FloatingActionButton


//var current_URL: String = ""

class PlayerActivity : AppCompatActivity(), ServiceConnection, ActionPlaying {
    private lateinit var backButton: ImageButton
    private lateinit var menuButton: ImageButton
    private lateinit var playButton: FloatingActionButton
    private lateinit var repeatButton: ImageButton
    private lateinit var songPrevButton: ImageButton
    private lateinit var songSkipButton: ImageButton
    private lateinit var songProgressBar: SeekBar
    private lateinit var songTimePassed: TextView
    private lateinit var songTimeTotal: TextView
    private lateinit var nowPlayingText: TextView
    private lateinit var videoView: VideoView

    private lateinit var newURL: String
    private var mediaSessionManager: MediaSessionManager? = null
    private var mediaSessionCompat: MediaSessionCompat? = null
    private var transportControls: MediaControllerCompat.TransportControls? = null
    private var musicService: MusicService? = null
    private var handle: Handler = Handler()


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        setContentView(R.layout.music_play_screen)
        viewFinder()

        serviceSetup()

        onPlayButtonClick()
        onBackButtonClick()
        onProgressBarChange()
        onRepeatChange()
    }

    override fun onResume() {
        Log.v("Music", "Reached Resume")
        bindService(Intent(this,
        MusicService::class.java), this, Context.BIND_AUTO_CREATE)
        if (musicService != null) {

            val preferences: SharedPreferences = getSharedPreferences(Communication.PREF_FILE, MODE_PRIVATE)
            val uri: Uri = Uri.parse(preferences.getString("url", "null"))
            videoView.setVideoURI(uri)
            videoView.start()
            videoView.seekTo(musicService!!.getCurrentPosition())
            if (preferences.getString(Communication.CONTROL, "null") == Communication.CONTROL_PLAY) {
                videoView.resume()
            } else {
                videoView.pause()
            }
            musicService?.pause()
            //videoView.resume()
            /*if (musicService!!.isPlaying()) {
                videoView.resume()
            } else {
                videoView.pause()
            }*/
        }
        //var intent = Intent(this, MusicService::class.java)
        //bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        Log.v("Music", "Paused")
        if (videoView.isPlaying) {
            musicService!!.seekTo(videoView.currentPosition)
            musicService!!.start()
        } else {
            musicService!!.seekTo(videoView.currentPosition)
            musicService!!.pause()
        }
    }

    private fun viewFinder() {
        playButton = findViewById(R.id.playButton)
        songProgressBar = findViewById(R.id.songProgressBar)
        songSkipButton = findViewById(R.id.songSkipButton)
        songPrevButton = findViewById(R.id.songPrevButton)
        songTimePassed = findViewById(R.id.songTimePassed)
        songTimeTotal = findViewById(R.id.songTimeTotal)
        nowPlayingText = findViewById(R.id.nowPlayingText)
        backButton = findViewById(R.id.backButton)
        menuButton = findViewById(R.id.menuButton)
        repeatButton = findViewById(R.id.repeatButton)
        videoView = findViewById(R.id.videoView)
        Log.v("Music", "Reached ViewFinderDone")
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun serviceSetup() {
        newURL = intent.getStringExtra("Song_URL").toString()
        val preferences: SharedPreferences = getSharedPreferences("MusicPlayerPref", MODE_PRIVATE)
        val editor: SharedPreferences.Editor? = preferences.edit()
        if (newURL != preferences.getString("url", "null")) {
            editor?.putString(Communication.URL, newURL)
            editor?.putString(Communication.CONTROL, Communication.CONTROL_NEW)
            editor?.apply()
            MusicTask(this).execute(newURL)
        }
        showNotification(R.drawable.player_pause, R.drawable.player_pause)
    }

    private fun onPlayButtonClick() {
        playButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                playPauseVideo()
            }
        }
    }

    private fun onBackButtonClick() {
        backButton.setOnClickListener {
            val intent = Intent(this, MainTestPlay::class.java)
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
                        videoView.seekTo(progress)
                    }
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {

                }
                override fun onStopTrackingTouch(p0: SeekBar?) {

                }
            }
        )
    }

    private fun onRepeatChange() {
        repeatButton.setOnClickListener{
            if (musicService!!.isLooping()) {
                musicService!!.noRepeat()
                repeatButton.setImageResource(R.drawable.player_repeat)
            } else {
                musicService!!.repeat()
                repeatButton.setImageResource(R.drawable.repeat_active)
            }
        }
    }

    private fun createTimeLabel(time: Int): String {
        var timeLabel: String
        val min = time / 1000 / 60
        val sec = time / 1000 % 60

        timeLabel = "$min:"
        if (sec < 10) timeLabel += "0"
        timeLabel += sec

        return timeLabel
    }

    override fun playNext() {
        Log.v("Music", "Reached NextPlay")
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun playPause() {
        val preferences: SharedPreferences = getSharedPreferences("MusicPlayerPref", MODE_PRIVATE)
        val editor: SharedPreferences.Editor? = preferences.edit()
        if (preferences.getString(Communication.CONTROL, "null") == Communication.CONTROL_PLAY) {
            musicService!!.pause()
            editor?.putString(Communication.CONTROL, Communication.CONTROL_PAUSE)
            editor?.apply()
            playButton.setImageResource(R.drawable.player_play)
            showNotification(R.drawable.player_play, R.drawable.player_play)
        } else {
            musicService!!.start()
            editor?.putString(Communication.CONTROL, Communication.CONTROL_PLAY)
            editor?.apply()
            playButton.setImageResource(R.drawable.player_pause)
            showNotification(R.drawable.player_pause, R.drawable.player_pause)
        }
    }

    override fun playPrev() {
        Log.v("Music", "Reached PrevPlay")
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun playPauseVideo() {
        val preferences: SharedPreferences = getSharedPreferences("MusicPlayerPref", MODE_PRIVATE)
        val editor: SharedPreferences.Editor? = preferences.edit()
        if (videoView.isPlaying) {
            videoView.pause()
            editor?.putString(Communication.CONTROL, Communication.CONTROL_PAUSE)
            editor?.apply()
            playButton.setImageResource(R.drawable.player_play)
            showNotification(R.drawable.player_play, R.drawable.player_play)
        } else {
            videoView.start()
            editor?.putString(Communication.CONTROL, Communication.CONTROL_PLAY)
            editor?.apply()
            playButton.setImageResource(R.drawable.player_pause)
            showNotification(R.drawable.player_pause, R.drawable.player_pause)
        }
    }

    private fun doBindService() {
        intent = Intent(this, MusicService::class.java)
        intent.putExtra("Song_URL", newURL)
        intent.putExtra("control", "play")
        val uri: Uri = Uri.parse(newURL)
        videoView.setVideoURI(uri)
        videoView.start()
        startService(intent)
    }

    inner class MusicTask(private var context: Context) : AsyncTask<String, Void, String>() {
        @Deprecated("Deprecated in Java")
        override fun onPreExecute() {
            super.onPreExecute()
            nowPlayingText.text = "Loading..."
        }

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg url: String): String {
            Log.v("Music", "Reached Background")
            doBindService()
            Log.v("Music", "Reached ServiceBind")
            return url[0]
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            //musicService?.seekTo(0)
            //videoView.seekTo(0)
            nowPlayingText.text = "Now playing"
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val myBinder: MusicService.MyBinder = service as MusicService.MyBinder
        musicService = myBinder.getService()
        musicService?.setup(this)
        this@PlayerActivity.runOnUiThread(
            object : Runnable {
                override fun run() {
                    songProgressBar.max = videoView.duration
                    songProgressBar.progress = videoView.currentPosition
                    songTimePassed.text = createTimeLabel(videoView.currentPosition)
                    songTimeTotal.text = createTimeLabel(videoView.duration)

                    handle.postDelayed(this, 100)
                }
            }
        )
        Log.v("Music", "Reached ServiceConnect")
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun showNotification(playPauseBtn: Int, playNoti: Int){
        Log.v("Music", "Reached Notif")

        val prevIntent = Intent(this, NotificationReceiver::class.java).setAction(ACTION_PREVIOUS)
        val prevPending = PendingIntent.getBroadcast(this, 0,prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val pauseIntent = Intent(this, NotificationReceiver::class.java).setAction(ACTION_PLAY)
        val pausePending = PendingIntent.getBroadcast(this, 0,pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(this, NotificationReceiver::class.java).setAction(ACTION_NEXT)
        val nextPending = PendingIntent.getBroadcast(this, 0,nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nBuilder = NotificationCompat.Builder(this, CHANNEL_ID_1)
        val bitmapNone = BitmapFactory.decodeResource(resources, R.drawable.random_1)

        mediaSessionCompat = MediaSessionCompat(baseContext, "AudioPlayer")
        transportControls = mediaSessionCompat!!.controller.transportControls
        mediaSessionCompat?.isActive = true
        mediaSessionCompat!!.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

        nBuilder.setSmallIcon(playPauseBtn).setLargeIcon(bitmapNone)
        nBuilder.setContentTitle("Test Title").
        setContentText("Test Artist").
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC).
        setStyle(androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSessionCompat!!.sessionToken)
            .setShowActionsInCompactView(0, 1, 2)).
        addAction(R.drawable.player_back, "Previous", prevPending).
        addAction(playNoti, "Pause", pausePending).
        addAction(R.drawable.player_skip, "Next", nextPending).
        setPriority(NotificationCompat.PRIORITY_HIGH).

        setAutoCancel(true)

        mediaSessionCompat!!.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, "track.getTitle()")
                .putString(MediaMetadata.METADATA_KEY_ARTIST, "track.getArtist()")
                .build()
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, nBuilder.build())
    }
}