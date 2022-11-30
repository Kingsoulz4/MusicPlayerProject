package com.example.musicplayerproject.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerproject.R
import com.example.musicplayerproject.adapters.HomeItemAdapter
import com.example.musicplayerproject.databinding.FragmentHome2Binding
import com.example.musicplayerproject.models.data.Song
import com.example.musicplayerproject.models.data.ZingAPI
import com.example.musicplayerproject.models.ui.ItemDisplayData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home2.view.*
import okhttp3.Call
import org.json.JSONObject
import java.io.IOException
import java.time.LocalTime
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var fragmentHomeBinding: FragmentHome2Binding
    lateinit var listNewReleaseVpop: MutableList<Song>
    lateinit var listNewReleaseOther: MutableList<Song>
    lateinit var listSongRecent: MutableList<ItemDisplayData>
    lateinit var dicPlaylist: MutableMap<String, MutableList<ItemDisplayData>>

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var myObj: LocalTime
    private var currentTime: Int? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myObj = LocalTime.now()
        currentTime = myObj.hour
        Log.v("Music", "$currentTime")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fragmentHomeBinding = FragmentHome2Binding.inflate(inflater, container, false)

        when (currentTime) {
            in (18..23), in (0..6) -> {
                fragmentHomeBinding.timeBar.text = "Good Evening"
            }
            in (7..11) -> {
                fragmentHomeBinding.timeBar.text = "Good Morning"
            }
            else -> {
                fragmentHomeBinding.timeBar.text = "Good Afternoon"
            }
        }


        listNewReleaseOther = mutableListOf()
        listNewReleaseVpop = mutableListOf()
        listSongRecent = mutableListOf()
        dicPlaylist = mutableMapOf()

        firebaseDatabase = Firebase.database
        firebaseAuth = FirebaseAuth.getInstance()

        ZingAPI.getInstance(this.context!!).getHome(object : ZingAPI.OnRequestCompleteListener {
            override fun onSuccess(call: Call, response: String) {
                var data = JSONObject(response).getJSONObject("data")
                var itemsObject = data.getJSONArray("items")
                for(i in 0 until itemsObject.length())
                {
                    var itemObject = itemsObject.getJSONObject(i)
                    if (itemObject.getString("sectionType").equals("new-release"))
                    {
                        var vpop = itemObject.getJSONObject("items").getJSONArray("vPop")
                        var other = itemObject.getJSONObject("items").getJSONArray("others")
                        for(j in 0 until vpop.length())
                        {
                            var song = Song.parseSongViaJsonObject(vpop.getJSONObject(j))
                            listNewReleaseVpop.add(song)
                        }

                        for(j in 0 until other.length())
                        {
                            var song = Song.parseSongViaJsonObject(other.getJSONObject(j))
                            listNewReleaseOther.add(song)
                        }
                    }
                    if (itemObject.getString("sectionType").equals("playlist"))
                    {
                        var listPlaylistObject = itemObject.getJSONArray("items")
                        var listPlaylist = mutableListOf<ItemDisplayData>()
                        for(j in 0 until listPlaylistObject.length())
                        {
                            var playlistItemObject = listPlaylistObject.getJSONObject(j)
                            var playlistID = playlistItemObject.getString("encodeId")
                            var playlistTitle = playlistItemObject.getString("title")
                            var playlistThumb = playlistItemObject.getString("thumbnail")
                            var sortDescription = playlistItemObject.getString("sortDescription")
                            listPlaylist.add(ItemDisplayData(ItemDisplayData.ITEM_TYPE.PLAYLIST, playlistID, playlistTitle,sortDescription, playlistThumb))
                        }
                        dicPlaylist.put(itemObject.getString("sectionId"), listPlaylist)
                    }
                }
                activity!!.runOnUiThread {
                    displayAllItemHome()

                }


            }

            override fun onError(call: Call, e: IOException) {

            }
        })

        return fragmentHomeBinding.root
    }

    fun displayAllItemHome()
    {
        var newReleaseVpopRecycleView = fragmentHomeBinding.recycleViewNewReleaseVpop
        var listItemNewReleaseVpop = listNewReleaseVpop.map { ItemDisplayData(it) }
        var sliderVpopAdapter = HomeItemAdapter.createHomeItemAdapter(this.context!!, R.layout.item_home_poster, listItemNewReleaseVpop)
        newReleaseVpopRecycleView.adapter = sliderVpopAdapter
        newReleaseVpopRecycleView.adapter!!.notifyDataSetChanged()

        var newReleaseOtherRecycleView = fragmentHomeBinding.recycleViewNewReleaseOther
        var listItemNewReleaseOther = listNewReleaseOther.map { ItemDisplayData(it) }
        var sliderOtherAdapter = HomeItemAdapter.createHomeItemAdapter(this.context!!, R.layout.item_home_poster, listItemNewReleaseOther)
        newReleaseOtherRecycleView.adapter = sliderOtherAdapter
        newReleaseOtherRecycleView.adapter!!.notifyDataSetChanged()

        for (entry in dicPlaylist)
        {
            var rv = RecyclerView(this.context!!)
            if (entry.key.equals("hAutoTheme1"))
            {
                rv = fragmentHomeBinding.recycleViewMidWeekEnergy

            }
            else if (entry.key.equals("h100"))
            {
                rv = fragmentHomeBinding.recycleViewTopOneHundreds
            }
            else if (entry.key.equals("hXone"))
            {
                rv = fragmentHomeBinding.recycleViewXone
            }
            var adapter = HomeItemAdapter.createHomeItemAdapter(this.context!!, R.layout.item_home_poster, entry.value)
            rv.adapter = adapter
            rv.adapter!!.notifyDataSetChanged()

        }

        var recentRecycleView = fragmentHomeBinding.recycleViewRecent
        var sliderRecentAdapter = HomeItemAdapter.createHomeItemAdapter(this.context!!, R.layout.item_home_poster, listSongRecent)
        recentRecycleView.adapter = sliderRecentAdapter
        recentRecycleView.adapter!!.notifyDataSetChanged()

        firebaseDatabase.reference.child("History").child(firebaseAuth.currentUser!!.uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listSongRecent.clear()
                var childs = snapshot.children

                val childs1 = childs
                for (snap in childs1)
                {
                    listSongRecent.add(snap.getValue(ItemDisplayData::class.java)!!)
                }

                sliderRecentAdapter.listItemDisplayData = listSongRecent
                sliderRecentAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }
}