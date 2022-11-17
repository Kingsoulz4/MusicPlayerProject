package com.example.musicplayerproject.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayerproject.R

class MainTestPlay : AppCompatActivity() {
    private lateinit var buttonPlay: Button
    private lateinit var buttonURL: Button

    //To update song time when it is playing
    /*private val mHandler: Handler = Handler()
    private val mUpdateTimeTask: Runnable = object : Runnable {
        override fun run() {
            val pos: Long = mPlayer!!.currentPosition.toLong()
            val total: Long = mPlayer!!.duration.toLong()
            val posMin: Long = pos.div(60000)
            val posSec: Long = (((pos.rem((1000 * 60 * 60))).rem((1000 * 60))).div(1000))


            //var progress: Int = getProgressPercentage(pos, total)
            numPos.text = "$posMin, $posSec"
            seekBar.progress = (pos * 100 / total).toInt()
            mHandler.postDelayed(this, 100)
        }
    }*/

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
        //Test playing two different URLs, one at a time
        buttonPlay.setOnClickListener {
            playFromURL("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3")
            //playFromURL("https://mp3-s1-m-zmp3.zmdcdn.me/b36802b4ccf325ad7ce2/1729810351076897325?authen=exp=1665064231~acl=/b36802b4ccf325ad7ce2/*~hmac=367ff8cee54ba10712d7bfe5c8261a9e")
        }
        buttonURL.setOnClickListener{
            playFromURL("https://media.discordapp.net/attachments/699391546365050901/1009080041075900447/redditsave.com_new_minecraft_update-nrv4ezcpe2i91-220.mp4")
        }
        /*seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                mHandler.removeCallbacks(mUpdateTimeTask)
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                mHandler.removeCallbacks(mUpdateTimeTask)
                val pos: Long = (seekBar.progress.toLong())
                val total: Long = mPlayer!!.duration.toLong()
                mPlayer?.seekTo((total * pos / 100).toInt())
                updateProgressBar()
            }

        })*/
    }

    private fun playFromURL(url: String) {
        //MusicTask(this).execute(url)
        var intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("Song_URL", url)

        Log.v("Music", "Start PlayerActivity")
        startActivity(intent)
    }

    /*private fun skipTest() {
        if (mPlayer!!.isPlaying && mPlayer != null) {
            mPlayer!!.seekTo(100000)
        }
    }

    private fun updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100)
    }



    private fun playOrPause() {
        if (!mPlayer!!.isPlaying) {
            Toast.makeText(this, "Playing!", Toast.LENGTH_SHORT).show()
            mPlayer!!.start()
            updateProgressBar()
        } else {
            Toast.makeText(this, "Paused!", Toast.LENGTH_SHORT).show()
            mPlayer!!.pause()
        }
    }

    private fun prepareURLTracks(url: String?) {
        if (mPlayer == null || url != currentURL) {
            mHandler.removeCallbacks(mUpdateTimeTask)
            mPlayer?.reset() // Only reset the player if we're playing a different URL

            currentURL = url.toString()
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
                setOnCompletionListener {
                    Toast.makeText(this@MainTestPlay, "Finished!", Toast.LENGTH_SHORT).show()
                }

                prepare()
            }
        }
    }

    internal inner class MusicTask(private var context: Context) : AsyncTask<String, Void, String>() {

        @Deprecated("Deprecated in Java")
        override fun onPreExecute() {
            super.onPreExecute()
            //Insert whatever loading animation you want here
            Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
            loadingCircle.visibility = VISIBLE
        }

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg url: String?): String? {
            prepareURLTracks(url[0])
            return url[0]
        }


        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            //Disable loading animation from above
            loadingCircle.visibility = INVISIBLE
            //seekBar.progress = 0
            seekBar.max = 100
            playOrPause()
        }
    }*/




}

