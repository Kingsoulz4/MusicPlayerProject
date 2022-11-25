package com.example.musicplayerproject.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerproject.R
import com.example.musicplayerproject.SearchInterface
import com.example.musicplayerproject.adapters.SearchAdapter
import com.example.musicplayerproject.models.SearchItems
import com.example.musicplayerproject.models.data.Playlist
import com.example.musicplayerproject.models.data.Song
import com.example.musicplayerproject.models.data.Video
import iammert.com.view.scalinglib.ScalingLayout
import iammert.com.view.scalinglib.ScalingLayoutListener
import iammert.com.view.scalinglib.State


//Search screen
class SearchFragment : Fragment(), SearchInterface {
    private lateinit var deleteSearches: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var scalingLayout: ScalingLayout
    private lateinit var searchLayout: RelativeLayout
    private lateinit var editTextSearch: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var deleteEditText: ImageButton
    private lateinit var textViewSearch: TextView
    private lateinit var buttonSongs: Button
    private lateinit var buttonVideos: Button
    private lateinit var buttonPlaylist: Button

    private var searchState = 0     //0 = songs, 1 = videos, 2 = playlists

    private lateinit var recyclerAdapter: SearchAdapter

    //Array to store recent clicked search entries, result songs/videos/playlists entries
    private var recentList = mutableListOf<SearchItems>()
    private var songsList = mutableListOf<Song>()
    private var videosList = mutableListOf<Video>()
    private var playlistList = mutableListOf<Playlist>()

    private var temp = 0            //Only for debugging

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)
        viewFinder(view)

        scalingLayout.setOnClickListener{
            if (scalingLayout.state == State.COLLAPSED) {
                scalingLayout.expand()
            }
        }
        scalingLayout.setListener(object : ScalingLayoutListener {
            override fun onCollapsed() {
                ViewCompat.animate(textViewSearch).alpha(1f).setDuration(150).start()

                ViewCompat.animate(searchLayout).alpha(0f).setDuration(150)
                    .setListener(object : ViewPropertyAnimatorListener {
                        override fun onAnimationStart(view: View) {
                            textViewSearch.visibility = VISIBLE
                        }

                        override fun onAnimationEnd(view: View) {
                            searchLayout.visibility = INVISIBLE
                        }

                        override fun onAnimationCancel(view: View) {}
                    }).start()
            }
            override fun onExpanded() {
                ViewCompat.animate(textViewSearch).alpha(0f).setDuration(200).start()
                ViewCompat.animate(searchLayout).alpha(1f).setDuration(200)
                    .setListener(object : ViewPropertyAnimatorListener {
                        override fun onAnimationStart(view: View) {
                            searchLayout.visibility = VISIBLE
                        }

                        override fun onAnimationEnd(view: View) {
                            textViewSearch.visibility = INVISIBLE
                        }

                        override fun onAnimationCancel(view: View) {}
                    }).start()
            }
            override fun onProgress(progress: Float) {}
        })
        view.findViewById<View>(R.id.rootLayout).setOnClickListener {
            if (scalingLayout.state == State.EXPANDED) {
                scalingLayout.collapse()
                if (editTextSearch.text.toString() == "") textViewSearch.text = "Search"
            }
        }

        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (editTextSearch.text.toString() == "") {
                    recyclerAdapter.addRecent(recentList)
                    recyclerAdapter.notifyDataSetChanged()
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
                if (editTextSearch.text.toString() == "") {
                    recyclerAdapter.addRecent(recentList)
                    recyclerAdapter.notifyDataSetChanged()
                }
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })

        searchButton.setOnClickListener{
            if (editTextSearch.text.toString() == "") {
                Toast.makeText(container?.context, "No search entries yet!", Toast.LENGTH_SHORT).show()
            } else {
                search()
            }
        }

        deleteEditText.setOnClickListener {
            editTextSearch.setText("")
        }

        searchCategoriesSetup()

        recyclerAdapter = SearchAdapter()
        recyclerAdapter.addSongs(this.songsList)
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(getView()?.context)
        recyclerAdapter.setup(this)

        deleteSearches.setOnClickListener {
            recentList.clear()
            songsList.clear()
            videosList.clear()
            playlistList.clear()
            recyclerAdapter.clearSongs()
            editTextSearch.setText("")
            recyclerAdapter.notifyDataSetChanged()
            Log.v("Music", "DeleteSearch")
        }

        return view
    }

    private fun viewFinder(view: View) {
        textViewSearch = view.findViewById(R.id.textViewSearch)
        searchButton = view.findViewById(R.id.search_text_button)
        editTextSearch = view.findViewById(R.id.editTextSearch)
        deleteEditText = view.findViewById(R.id.deleteEditText)
        scalingLayout = view.findViewById(R.id.scalingLayout)
        searchLayout = view.findViewById(R.id.searchLayout)
        recyclerView = view.findViewById(R.id.Artist_search_list)
        deleteSearches = view.findViewById(R.id.deleteSearches)
        buttonSongs = view.findViewById(R.id.buttonSongs)
        buttonVideos = view.findViewById(R.id.buttonVideos)
        buttonPlaylist = view.findViewById(R.id.buttonPlaylists)
    }

    fun search() {
        when (searchState) {
            0 -> {
                val song = Song()
                song.title = editTextSearch.text.toString()

                if (temp % 2 == 0) {
                    song.artistsNames = "Meilin Lee"
                    song.streamingLink = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
                } else {
                    song.artistsNames = "Ming Lee"
                    song.streamingLink = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
                }
                temp++
                songsList.plusAssign(song)
                recyclerAdapter.addSongs(songsList)
            }
            1 -> {
                val video = Video()
                video.title = editTextSearch.text.toString()
                if (temp % 2 == 0) {
                    video.artistNames = "The Nameless One"
                    video.streamingLink = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
                } else {
                    video.artistNames = "Redstone Golem"
                    video.streamingLink = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
                }
                temp++
                videosList.plusAssign(video)
                recyclerAdapter.addVideos(videosList)
            }
            2 -> {
                var playlist = Playlist()
                playlist.title = "Test Playlist"

                for (i in 1..3) {
                    var song = Song()
                    song.title = "PlaylistSong$i"
                    song.artistsNames = "Artist$i"
                    song.streamingLink = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-$i.mp3"
                    playlist.listSong.plusAssign(song)
                }
                playlistList.plusAssign(playlist)
                recyclerAdapter.addPlaylists(playlistList)
            }
        }
        recyclerAdapter.notifyDataSetChanged()
    }

    private fun searchCategoriesSetup() {
        buttonVideos.setTextColor(Color.GRAY)
        buttonPlaylist.setTextColor(Color.GRAY)

        buttonSongs.setOnClickListener {
            searchState = 0
            buttonSongs.setTextColor(Color.WHITE)
            buttonVideos.setTextColor(Color.GRAY)
            buttonPlaylist.setTextColor(Color.GRAY)
        }

        buttonVideos.setOnClickListener {
            searchState = 1
            buttonSongs.setTextColor(Color.GRAY)
            buttonVideos.setTextColor(Color.WHITE)
            buttonPlaylist.setTextColor(Color.GRAY)
        }

        buttonPlaylist.setOnClickListener {
            searchState = 2
            buttonSongs.setTextColor(Color.GRAY)
            buttonVideos.setTextColor(Color.GRAY)
            buttonPlaylist.setTextColor(Color.WHITE)
        }
    }

    override fun deleteEntry(pos: Int) {
        if (pos < recentList.size && recentList.isNotEmpty()) {
            recentList.removeAt(pos)
        }
        recyclerAdapter.notifyDataSetChanged()
    }

    override fun addToRecent(recent: SearchItems) {
        recentList.plusAssign(recent)
    }
}