package com.example.musicplayerproject.activities

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadata
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
import com.example.musicplayerproject.*
import com.example.musicplayerproject.ApplicationClass.Companion.ACTION_NEXT
import com.example.musicplayerproject.ApplicationClass.Companion.ACTION_PLAY
import com.example.musicplayerproject.ApplicationClass.Companion.ACTION_PREVIOUS
import com.example.musicplayerproject.ApplicationClass.Companion.CHANNEL_ID_1
import com.example.musicplayerproject.models.SearchItems
import com.example.musicplayerproject.models.data.Song
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PlayerActivity : AppCompatActivity(), ServiceConnection, ActionPlaying {
    //UI components
    private lateinit var backButton: ImageButton
    private lateinit var playButton: FloatingActionButton
    private lateinit var repeatButton: ImageButton
    private lateinit var songPrevButton: ImageButton
    private lateinit var songSkipButton: ImageButton
    private lateinit var songProgressBar: SeekBar
    private lateinit var songTimePassed: TextView
    private lateinit var songTimeTotal: TextView
    private lateinit var nowPlayingText: TextView
    private lateinit var videoView: VideoView
    private lateinit var downloadButton: ImageButton
    private lateinit var songName: TextView
    private lateinit var authorName: TextView
    private lateinit var albumType: TextView
    private var image: Bitmap? = null

    //Song List Array
    private var songList = mutableListOf<Song>()
    private var currentPos: Int = 0

    //Misc
    private lateinit var newURL: String
    private var mediaSessionCompat: MediaSessionCompat? = null
    private var transportControls: MediaControllerCompat.TransportControls? = null
    private var musicService: MusicService? = null
    private var handle: Handler = Handler()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("Music", "Test")
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        setContentView(R.layout.music_play_screen)

        viewFinder()



       /* val entry = intent.getSerializableExtra("playItem") as SearchItems
        when(entry.type) {
            0 -> albumType.text = "Song"
            1 -> albumType.text = "Video"
            2 -> albumType.text = "Playlist: " + entry.title
        }
        songList = entry.listSong*/

        val entry = intent.getSerializableExtra(getString(R.string.SONG_TO_PLAY)) as Song
        songList.clear()
        songList.add(entry)

        newURL = songList[currentPos].linkQuality128
        songName.text = songList[currentPos].title
        authorName.text = songList[currentPos].artistsNames
        ImageTask().execute(entry.thumbnail)

        serviceSetup()

        listenerSetup()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onResume() {
        Log.v("Music", "Reached Resume")
        bindService(Intent(this,
        MusicService::class.java), this, Context.BIND_AUTO_CREATE)
        if (musicService != null) {
            val preferences: SharedPreferences = getSharedPreferences(Communication.PREF_FILE, MODE_PRIVATE)
            val uri: Uri = Uri.parse(preferences.getString(Communication.URL, "null"))
            videoView.setVideoURI(uri)
            videoView.start()
            videoView.seekTo(musicService!!.getCurrentPosition())
            if (preferences.getString(Communication.CONTROL, "null") == Communication.CONTROL_PLAY) {
                videoView.resume()
            } else {
                videoView.pause()
            }
            musicService?.pause()
        }
        autoNext()
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
        downloadButton = findViewById(R.id.downloadButton)
        repeatButton = findViewById(R.id.repeatButton)
        videoView = findViewById(R.id.videoView)
        songName = findViewById(R.id.songName)
        authorName = findViewById(R.id.authorName)
        albumType = findViewById(R.id.albumType)
        Log.v("Music", "Reached ViewFinderDone")
    }

    private fun listenerSetup() {
        onPlayButtonClick()
        onBackButtonClick()
        onProgressBarChange()
        onRepeatChange()
        onDownloadChange()
        videoView.setOnCompletionListener {
            playButton.setImageResource(R.drawable.player_play)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun serviceSetup() {
        nowPlayingText.text = getString(R.string.Loading)
        val preferences: SharedPreferences = getSharedPreferences(Communication.PREF_FILE, MODE_PRIVATE)
        val editor: SharedPreferences.Editor? = preferences.edit()
        if (newURL != preferences.getString("url", "null")) {
            editor?.putString(Communication.URL, newURL)
            editor?.putString(Communication.CONTROL, Communication.CONTROL_NEW)
            editor?.apply()
            doBindService()
        }
        showNotification(image, R.drawable.player_pause)
    }

    internal inner class ImageTask : AsyncTask<String, Void, Bitmap?>() {
        override fun doInBackground(vararg url: String?): Bitmap? {
            val imageURL = url[0]
            var image: Bitmap? = null
            try {
                val inStream = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(inStream)
            }
            catch (e: Exception) {
                Log.e("Error Message", e.message.toString())
                e.printStackTrace()
            }
            return image
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            image = result
        }
    }

    private fun onPlayButtonClick() {
        playButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                playPauseVideo()
            }
        }
        songPrevButton.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                playPrev()
            }
        }
        songSkipButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                playNext()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun autoNext() {
        musicService?.mediaPlayer?.setOnCompletionListener {
            playNext()
        }
        videoView.setOnCompletionListener {
            playNext()
        }
    }

    private fun onBackButtonClick() {
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
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
                    if (p0 != null) {
                        songTimePassed.text = createTimeLabel(p0.progress)
                    }
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
                videoView.setOnCompletionListener {
                    playButton.setImageResource(R.drawable.player_play)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    autoNext()
                }
                repeatButton.setImageResource(R.drawable.player_repeat)
            } else {
                musicService!!.repeat()
                videoView.setOnCompletionListener {
                    videoView.start()
                }
                repeatButton.setImageResource(R.drawable.repeat_active)
            }
        }
    }

    private fun onDownloadChange() {
        var downloadManager: DownloadManager
        val preferences: SharedPreferences = getSharedPreferences(Communication.PREF_FILE, MODE_PRIVATE)
        downloadButton.setOnClickListener{
            downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val uri = Uri.parse(preferences.getString(Communication.URL, "null"))
            val request = DownloadManager.Request(uri)
            .setTitle("MusicPlayerProject")
            .setDescription("Downloading...")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS.toString(), "test.mp4")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            val reference: Long = downloadManager.enqueue(request)
            Toast.makeText(this@PlayerActivity, "Downloading started!", Toast.LENGTH_SHORT).show()
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun playNext() {
        if (currentPos < (songList.size - 1)) {
            currentPos++
            newURL = songList[currentPos].streamingLink
            songName.text = songList[currentPos].title
            authorName.text = songList[currentPos].artistsNames
            serviceSetup()
            val uri: Uri = Uri.parse(newURL)
            videoView.setVideoURI(uri)
            videoView.start()
        } else {
            Toast.makeText(this@PlayerActivity, "You are already at the end of playlist!", Toast.LENGTH_SHORT).show()
        }
        autoNext()
        Log.v("Music", "Reached NextPlay")
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun playPrev() {
        if (currentPos > 0) {
            currentPos--
            newURL = songList[currentPos].streamingLink
            songName.text = songList[currentPos].title
            authorName.text = songList[currentPos].artistsNames
            serviceSetup()
            val uri: Uri = Uri.parse(newURL)
            videoView.setVideoURI(uri)
            videoView.start()
        } else {
            Toast.makeText(this@PlayerActivity, "You are already at the start of playlist!", Toast.LENGTH_SHORT).show()
        }
        autoNext()
        Log.v("Music", "Reached PrevPlay")
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
            showNotification(image, R.drawable.player_play)
        } else {
            musicService!!.start()
            editor?.putString(Communication.CONTROL, Communication.CONTROL_PLAY)
            editor?.apply()
            playButton.setImageResource(R.drawable.player_pause)
            showNotification(image, R.drawable.player_pause)
        }
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
            showNotification(image, R.drawable.player_play)
        } else {
            videoView.start()
            editor?.putString(Communication.CONTROL, Communication.CONTROL_PLAY)
            editor?.apply()
            playButton.setImageResource(R.drawable.player_pause)
            showNotification(image, R.drawable.player_pause)
        }
    }

    private fun doBindService() {
        intent = Intent(this, MusicService::class.java)
        intent.putExtra("Song_URL", newURL)
        intent.putExtra("control", "play")
        val uri: Uri = Uri.parse(newURL)
        videoView.setVideoURI(uri)
        videoView.start()
        bindService(Intent(this,
            MusicService::class.java), this, Context.BIND_AUTO_CREATE)
        startService(intent)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val myBinder: MusicService.MyBinder = service as MusicService.MyBinder
        musicService = myBinder.getService()
        musicService?.setup(this)
        this@PlayerActivity.runOnUiThread(
            object : Runnable {
                override fun run() {
                    if (videoView.isPlaying || musicService!!.isPlaying()) {
                        nowPlayingText.text = getString(R.string.Playing)
                    }

                    songProgressBar.max = videoView.duration
                    songProgressBar.progress = videoView.currentPosition
                    songTimePassed.text = createTimeLabel(videoView.currentPosition)
                    songTimeTotal.text = createTimeLabel(videoView.duration)

                    handle.postDelayed(this, 100)
                }
            }
        )
        autoNext()
        Log.v("Music", "Reached ServiceConnect")
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        Log.v("Music", "Reached ServiceDisconnect")
        musicService = null
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun showNotification(songImage: Bitmap?, playNoti: Int){
        Log.v("Music", "Reached Notif")

        val prevIntent = Intent(this, NotificationReceiver::class.java).setAction(ACTION_PREVIOUS)
        val prevPending = PendingIntent.getBroadcast(this, 0,prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val pauseIntent = Intent(this, NotificationReceiver::class.java).setAction(ACTION_PLAY)
        val pausePending = PendingIntent.getBroadcast(this, 0,pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(this, NotificationReceiver::class.java).setAction(ACTION_NEXT)
        val nextPending = PendingIntent.getBroadcast(this, 0,nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nBuilder = NotificationCompat.Builder(this, CHANNEL_ID_1)
        //val bitmapNone = BitmapFactory.decodeResource(resources, songImage)

        mediaSessionCompat = MediaSessionCompat(baseContext, "AudioPlayer")
        transportControls = mediaSessionCompat!!.controller.transportControls
        mediaSessionCompat?.isActive = true
        mediaSessionCompat!!.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

        nBuilder.setSmallIcon(R.drawable.logo).setLargeIcon(songImage).
        setContentTitle("Test Title").
        setContentText("Test Artist").

        setStyle(androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSessionCompat!!.sessionToken)
            .setShowActionsInCompactView(0, 1, 2)).
        addAction(R.drawable.player_back, "Previous", prevPending).
        addAction(playNoti, "Pause", pausePending).
        addAction(R.drawable.player_skip, "Next", nextPending).
        setPriority(NotificationCompat.PRIORITY_HIGH).
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC).
        setAutoCancel(true)

        mediaSessionCompat!!.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, songName.text as String?)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, authorName.text.toString())
                .build()
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, nBuilder.build())
    }
}