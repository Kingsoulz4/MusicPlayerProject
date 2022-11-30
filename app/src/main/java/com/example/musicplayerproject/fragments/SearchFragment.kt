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
import com.example.musicplayerproject.models.data.Playlist
import com.example.musicplayerproject.models.data.Song
import com.example.musicplayerproject.models.data.Video
import com.example.musicplayerproject.models.data.ZingAPI
import com.example.musicplayerproject.models.ui.ItemDisplayData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import iammert.com.view.scalinglib.ScalingLayout
import iammert.com.view.scalinglib.ScalingLayoutListener
import iammert.com.view.scalinglib.State
import okhttp3.Call
import org.json.JSONObject
import java.io.IOException


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
    private lateinit var searchType: TextView
    private lateinit var buttonSongs: Button
    private lateinit var buttonVideos: Button
    private lateinit var buttonPlaylist: Button

    private var searchState = 0     //0 = songs, 1 = videos, 2 = playlists

    private lateinit var recyclerAdapter: SearchAdapter

    //Array to store recent clicked search entries, result songs/videos/playlists entries
    private var recentList = mutableListOf<ItemDisplayData>()
    private var songsList = mutableListOf<Song>()
    private var videosList = mutableListOf<Video>()
    private var playlistList = mutableListOf<Playlist>()

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)
        viewFinder(view)

        firebaseDatabase = Firebase.database
        firebaseAuth = FirebaseAuth.getInstance()

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
                    test()
                    searchType.text = "Recent History"
                    recyclerAdapter.notifyDataSetChanged()
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
                if (editTextSearch.text.toString() == "") {
                    test()
                    searchType.text = "Recent History"
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

        recyclerAdapter = SearchAdapter(context!!)

        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(getView()?.context)
        recyclerAdapter.setup(this)

        test()

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
        searchType = view.findViewById(R.id.searchType)
    }

    fun search() {
        songsList.clear()
        videosList.clear()
        playlistList.clear()
        ZingAPI.getInstance(this.context!!).search(editTextSearch.text.toString(), object : ZingAPI.OnRequestCompleteListener {
            override fun onSuccess(call: Call, response: String) {
                var data = JSONObject(response)
                data = data.getJSONObject("data")
                var songs = data.getJSONArray("songs")
                for( i in 0 until songs.length())
                {
                    var songJSONObject = songs.getJSONObject(i)
                    var song = Song.parseSongViaJsonObject(songJSONObject)
                    // add URL here pls
                    songsList.add(song)
                }

                var videos = data.getJSONArray("videos")
                for (i in 0 until videos.length())
                {
                    var videoObject = videos.getJSONObject(i)
                    var vid = Video.parseVideoViaJsonObject(videoObject)
                    //Add url here pls
                    videosList.add(vid)
                }

                var playlistJSONObjects = data.getJSONArray("playlists")
                for (i in 0 until playlistJSONObjects.length())
                {
                    var playlistJSONObject = playlistJSONObjects.getJSONObject(i)
                    var playlist = Playlist.parseData(playlistJSONObject)
                    playlistList.add(playlist)
                }

                activity!!.runOnUiThread {
                    displayResult()
                }
            }

            override fun onError(call: Call, e: IOException) {

            }
        })
    }

    private fun displayResult() {
        when (searchState) {
            0 -> {
                var itemList = songsList.map { ItemDisplayData(it) }
                recyclerAdapter.addRecent(itemList.toMutableList())
            }
            1 -> {
                var itemList = videosList.map { ItemDisplayData(it) }
                recyclerAdapter.addRecent(itemList.toMutableList())
            }
            2 -> {
                var itemList = playlistList.map { ItemDisplayData(it) }
                recyclerAdapter.addRecent(itemList.toMutableList())
            }
        }
        searchType.text = "Search from ZingMP3 Database"
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
            displayResult()
        }

        buttonVideos.setOnClickListener {
            searchState = 1
            buttonSongs.setTextColor(Color.GRAY)
            buttonVideos.setTextColor(Color.WHITE)
            buttonPlaylist.setTextColor(Color.GRAY)
            displayResult()
        }

        buttonPlaylist.setOnClickListener {
            searchState = 2
            buttonSongs.setTextColor(Color.GRAY)
            buttonVideos.setTextColor(Color.GRAY)
            buttonPlaylist.setTextColor(Color.WHITE)
            displayResult()
        }
    }

    override fun deleteEntry(pos: Int) {
        if (searchType.text == "Recent History" && pos < recentList.size && recentList.isNotEmpty()) {
            recentList.removeAt(pos)
            //var deleteData: DatabaseReference = firebaseDatabase.reference.child("History").child(firebaseAuth.currentUser!!.uid)
            //deleteData.orderByChild()

            recyclerAdapter.notifyDataSetChanged()
        }
    }

    fun test() {
        firebaseDatabase.reference.child("History").child(firebaseAuth.currentUser!!.uid).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                recentList.clear()
                var childs = snapshot.children

                val childs1 = childs
                for (snap in childs1)
                {
                    recentList.add(snap.getValue(ItemDisplayData::class.java)!!)
                }

                recyclerAdapter.addRecent(recentList.toMutableList())
                recyclerAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}