package com.example.musicplayerproject.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.musicplayerproject.R
import com.example.musicplayerproject.adapters.SearchAdapter
import com.example.musicplayerproject.databinding.ActivityPlaylistScreenBinding
import com.example.musicplayerproject.models.data.Playlist
import com.example.musicplayerproject.models.data.ZingAPI
import com.example.musicplayerproject.models.ui.ItemDisplayData
import okhttp3.Call
import org.json.JSONObject
import java.io.IOException

class PlaylistScreenActivity : AppCompatActivity() {

    private lateinit var playlistScreenBinding: ActivityPlaylistScreenBinding
    private lateinit var adapter: SearchAdapter
    private lateinit var playlistDisplay: ItemDisplayData
    private lateinit var playlistContainedInPlaylist: MutableList<ItemDisplayData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        playlistScreenBinding = ActivityPlaylistScreenBinding.inflate(layoutInflater)

        setContentView(playlistScreenBinding.root)

        playlistDisplay = intent.getSerializableExtra(getString(R.string.PLAYLIST_TO_DISPLAY)) as ItemDisplayData

        ZingAPI.getInstance(this).getPlaylist(playlistDisplay.encodeId, object : ZingAPI.OnRequestCompleteListener{
            override fun onSuccess(call: Call, response: String) {
                var data = JSONObject(response).getJSONObject("data")
                var playlist = Playlist.parseData(data)
                playlistContainedInPlaylist = (playlist.listSong.map { ItemDisplayData(it) }).toMutableList()
                runOnUiThread{
                    displayAllSong()
                }

            }

            override fun onError(call: Call, e: IOException) {

            }
        })
    }

    private fun displayAllSong() {
        adapter = SearchAdapter(this)
        adapter.addRecent(playlistContainedInPlaylist)
        playlistScreenBinding.recycleViewSongContain.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}