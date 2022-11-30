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
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayerproject.SearchInterface
import com.example.musicplayerproject.adapters.SearchAdapter
import com.example.musicplayerproject.databinding.FragmentSearchBinding
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
import iammert.com.view.scalinglib.ScalingLayoutListener
import iammert.com.view.scalinglib.State
import okhttp3.Call
import org.json.JSONObject
import java.io.IOException


//Search screen
class SearchFragment : Fragment(), SearchInterface {
    private lateinit var fragmentSearchBinding: FragmentSearchBinding

    private var searchState = 0     //0 = songs, 1 = videos, 2 = playlists

    private lateinit var recyclerAdapter: SearchAdapter

    //Array to store recent clicked search entries, result songs/videos/playlists entries
    private var recentList = mutableListOf<ItemDisplayData>()
    private var songsList = mutableListOf<Song>()
    private var videosList = mutableListOf<Video>()
    private var playlistList = mutableListOf<Playlist>()

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth

    private val loadingFragment = LoadingScreen()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentSearchBinding = FragmentSearchBinding.inflate(inflater, container, false)

        firebaseDatabase = Firebase.database
        firebaseAuth = FirebaseAuth.getInstance()

        fragmentSearchBinding.scalingLayout.setOnClickListener{
            if (fragmentSearchBinding.scalingLayout.state == State.COLLAPSED) {
                fragmentSearchBinding.scalingLayout.expand()
            }
        }
        fragmentSearchBinding.scalingLayout.setListener(object : ScalingLayoutListener {
            override fun onCollapsed() {
                ViewCompat.animate(fragmentSearchBinding.textViewSearch).alpha(1f).setDuration(150).start()

                ViewCompat.animate(fragmentSearchBinding.searchLayout).alpha(0f).setDuration(150)
                    .setListener(object : ViewPropertyAnimatorListener {
                        override fun onAnimationStart(view: View) {
                            fragmentSearchBinding.textViewSearch.visibility = VISIBLE
                        }

                        override fun onAnimationEnd(view: View) {
                            fragmentSearchBinding.searchLayout.visibility = INVISIBLE
                        }

                        override fun onAnimationCancel(view: View) {}
                    }).start()
            }
            override fun onExpanded() {
                ViewCompat.animate(fragmentSearchBinding.textViewSearch).alpha(0f).setDuration(200).start()
                ViewCompat.animate(fragmentSearchBinding.searchLayout).alpha(1f).setDuration(200)
                    .setListener(object : ViewPropertyAnimatorListener {
                        override fun onAnimationStart(view: View) {
                            fragmentSearchBinding.searchLayout.visibility = VISIBLE
                        }

                        override fun onAnimationEnd(view: View) {
                            fragmentSearchBinding.textViewSearch.visibility = INVISIBLE
                        }

                        override fun onAnimationCancel(view: View) {}
                    }).start()
            }
            override fun onProgress(progress: Float) {}
        })
        fragmentSearchBinding.rootLayout.setOnClickListener {
            if (fragmentSearchBinding.scalingLayout.state == State.EXPANDED) {
                fragmentSearchBinding.scalingLayout.collapse()
                if (fragmentSearchBinding.editTextSearch.text.toString() == "") fragmentSearchBinding.textViewSearch.text = "Search"
            }
        }

        fragmentSearchBinding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (fragmentSearchBinding.editTextSearch.text.toString() == "") {
                    test()
                    fragmentSearchBinding.searchType.text = "Recent History"
                    recyclerAdapter.notifyDataSetChanged()
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
                if (fragmentSearchBinding.editTextSearch.text.toString() == "") {
                    test()
                    fragmentSearchBinding.searchType.text = "Recent History"
                    recyclerAdapter.notifyDataSetChanged()
                }
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })

        fragmentSearchBinding.searchTextButton.setOnClickListener{
            if (fragmentSearchBinding.editTextSearch.text.toString() == "") {
                Toast.makeText(container?.context, "No search entries yet!", Toast.LENGTH_SHORT).show()
            } else {
                val fm = this@SearchFragment.activity?.supportFragmentManager
                val transaction = fm?.beginTransaction()
                transaction?.add(fragmentSearchBinding.rootLayout.id, loadingFragment)
                transaction?.commit()
                search()
            }
        }

        fragmentSearchBinding.deleteEditText.setOnClickListener {
            fragmentSearchBinding.editTextSearch.setText("")
        }

        searchCategoriesSetup()

        recyclerAdapter = SearchAdapter(context!!)

        fragmentSearchBinding.ArtistSearchList.adapter = recyclerAdapter
        fragmentSearchBinding.ArtistSearchList.layoutManager = LinearLayoutManager(view?.context)
        recyclerAdapter.setup(this)

        test()

        fragmentSearchBinding.deleteSearches.setOnClickListener {
            recentList.clear()
            songsList.clear()
            videosList.clear()
            playlistList.clear()
            recyclerAdapter.clearSongs()
            fragmentSearchBinding.editTextSearch.setText("")
            recyclerAdapter.notifyDataSetChanged()
            Log.v("Music", "DeleteSearch")
        }

        return fragmentSearchBinding.rootLayout
    }

    fun search() {
        songsList.clear()
        videosList.clear()
        playlistList.clear()

        ZingAPI.getInstance(this.context!!).search(fragmentSearchBinding.editTextSearch.text.toString(), object : ZingAPI.OnRequestCompleteListener {
            override fun onSuccess(call: Call, response: String) {
                var data = JSONObject(response)
                data = data.getJSONObject("data")
                val songs = data.getJSONArray("songs")
                for( i in 0 until songs.length())
                {
                    val songJSONObject = songs.getJSONObject(i)
                    val song = Song.parseSongViaJsonObject(songJSONObject)
                    // add URL here pls
                    songsList.add(song)
                }

                val videos = data.getJSONArray("videos")
                for (i in 0 until videos.length())
                {
                    val videoObject = videos.getJSONObject(i)
                    val vid = Video.parseVideoViaJsonObject(videoObject)
                    videosList.add(vid)
                }

                val playlistJSONObjects = data.getJSONArray("playlists")
                for (i in 0 until playlistJSONObjects.length())
                {
                    val playlistJSONObject = playlistJSONObjects.getJSONObject(i)
                    val playlist = Playlist.parseData(playlistJSONObject)
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
                val itemList = songsList.map { ItemDisplayData(it) }
                recyclerAdapter.addRecent(itemList.toMutableList())
            }
            1 -> {
                val itemList = videosList.map { ItemDisplayData(it) }
                recyclerAdapter.addRecent(itemList.toMutableList())
            }
            2 -> {
                val itemList = playlistList.map { ItemDisplayData(it) }
                recyclerAdapter.addRecent(itemList.toMutableList())
            }
        }
        fragmentSearchBinding.searchType.text = "Search from ZingMP3 Database"
        recyclerAdapter.notifyDataSetChanged()
        val fm = this@SearchFragment.activity?.supportFragmentManager
        val transaction = fm?.beginTransaction()
        transaction?.remove(loadingFragment)
        transaction?.commit()
    }

    private fun searchCategoriesSetup() {
        fragmentSearchBinding.buttonVideos.setTextColor(Color.GRAY)
        fragmentSearchBinding.buttonPlaylists.setTextColor(Color.GRAY)

        fragmentSearchBinding.buttonSongs.setOnClickListener {
            searchState = 0
            fragmentSearchBinding.buttonSongs.setTextColor(Color.WHITE)
            fragmentSearchBinding.buttonVideos.setTextColor(Color.GRAY)
            fragmentSearchBinding.buttonPlaylists.setTextColor(Color.GRAY)
            displayResult()
        }

        fragmentSearchBinding.buttonVideos.setOnClickListener {
            searchState = 1
            fragmentSearchBinding.buttonSongs.setTextColor(Color.GRAY)
            fragmentSearchBinding.buttonVideos.setTextColor(Color.WHITE)
            fragmentSearchBinding.buttonPlaylists.setTextColor(Color.GRAY)
            displayResult()
        }

        fragmentSearchBinding.buttonPlaylists.setOnClickListener {
            searchState = 2
            fragmentSearchBinding.buttonSongs.setTextColor(Color.GRAY)
            fragmentSearchBinding.buttonVideos.setTextColor(Color.GRAY)
            fragmentSearchBinding.buttonPlaylists.setTextColor(Color.WHITE)
            displayResult()
        }
    }

    override fun deleteEntry(pos: Int) {
        if (fragmentSearchBinding.searchType.text == "Recent History" && pos < recentList.size && recentList.isNotEmpty()) {
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

                for (snap in childs)
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