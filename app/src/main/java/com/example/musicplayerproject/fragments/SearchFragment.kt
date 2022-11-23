package com.example.musicplayerproject.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerproject.Communication
import com.example.musicplayerproject.R
import com.example.musicplayerproject.SearchInterface
import com.example.musicplayerproject.adapters.SearchAdapter
import com.example.musicplayerproject.models.data.Song
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
    private lateinit var textViewSearch: TextView


    private lateinit var recyclerAdapter: SearchAdapter
    private var songsList = mutableListOf<Song>()
    private var temp = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)


        textViewSearch = view.findViewById(R.id.textViewSearch)
        searchButton = view.findViewById(R.id.search_text_button)
        editTextSearch = view.findViewById(R.id.editTextSearch)
        scalingLayout = view.findViewById(R.id.scalingLayout)
        searchLayout = view.findViewById(R.id.searchLayout)
        recyclerView = view.findViewById(R.id.Artist_search_list)
        deleteSearches = view.findViewById(R.id.deleteSearches)

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
                            textViewSearch.visibility = View.VISIBLE
                        }

                        override fun onAnimationEnd(view: View) {
                            searchLayout.visibility = View.INVISIBLE
                        }

                        override fun onAnimationCancel(view: View) {}
                    }).start()
            }
            override fun onExpanded() {
                ViewCompat.animate(textViewSearch).alpha(0f).setDuration(200).start()
                ViewCompat.animate(searchLayout).alpha(1f).setDuration(200)
                    .setListener(object : ViewPropertyAnimatorListener {
                        override fun onAnimationStart(view: View) {
                            searchLayout.visibility = View.VISIBLE
                        }

                        override fun onAnimationEnd(view: View) {
                            textViewSearch.visibility = View.INVISIBLE
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

        searchButton.setOnClickListener{
            var song = Song()
            song.title = editTextSearch.text.toString()
            if (temp % 2 == 0) {
                song.artistsNames = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
            } else {
                song.artistsNames = "https://mcloud-bf-s7-mv-zmp3.zmdcdn.me/CzdoiUkfjGg/347a4caac1ee28b071ff/04ade142f2071b594216/1080/So-Far-Away.mp4?authen=exp=1669252460~acl=/CzdoiUkfjGg/*~hmac=8f6bcda15e5b32849f124e6a79653920"
            }
            temp++
            if (songsList.size > Communication.SEARCH_SIZE) {
                songsList.removeFirst()
            }

            songsList.plusAssign(song)
            Log.v("Music", song.title + " " + song.artistsNames)
            recyclerAdapter.addSongs(songsList)

            recyclerAdapter.notifyDataSetChanged()
        }



        recyclerAdapter = SearchAdapter()
        recyclerAdapter.addSongs(this.songsList)
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(getView()?.context)
        recyclerAdapter.setup(this)

        deleteSearches.setOnClickListener {
            songsList.clear()
            recyclerAdapter.clearSongs()
            editTextSearch.setText("")
            recyclerAdapter.notifyDataSetChanged()
            Log.v("Music", "DeleteSearch")
        }
        return view
    }

    override fun deleteEntry(pos: Int) {
        songsList.removeAt(pos)
        recyclerAdapter.notifyDataSetChanged()
    }
}