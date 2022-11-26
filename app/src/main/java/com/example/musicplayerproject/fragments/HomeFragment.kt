package com.example.musicplayerproject.fragments

import android.content.ClipData.Item
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerproject.R
import com.example.musicplayerproject.adapters.HomeItemAdapter
import com.example.musicplayerproject.databinding.FragmentHome2Binding
import com.example.musicplayerproject.databinding.FragmentHomeBinding
import com.example.musicplayerproject.models.data.Playlist
import com.example.musicplayerproject.models.data.Song
import com.example.musicplayerproject.models.data.ZingAPI
import com.example.musicplayerproject.models.ui.ItemHome
import okhttp3.Call
import org.json.JSONObject
import java.io.IOException
import java.util.Dictionary

class HomeFragment : Fragment() {

    private lateinit var fragmentHomeBinding: FragmentHome2Binding
    lateinit var listNewReleaseVpop: MutableList<Song>
    lateinit var listNewReleaseOther: MutableList<Song>
    lateinit var listSongRecent: MutableList<Song>
    lateinit var dicPlaylist: MutableMap<String, MutableList<ItemHome>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeBinding = FragmentHome2Binding.inflate(inflater, container, false)

        listNewReleaseOther = mutableListOf<Song>()
        listNewReleaseVpop = mutableListOf<Song>()
        dicPlaylist = mutableMapOf<String, MutableList<ItemHome>>()

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
                        var listPlaylist = mutableListOf<ItemHome>()
                        for(j in 0 until listPlaylistObject.length())
                        {
                            var playlistItemObject = listPlaylistObject.getJSONObject(j)
                            var playlistID = playlistItemObject.getString("encodeId")
                            var playlistTitle = playlistItemObject.getString("title")
                            var playlistThumb = playlistItemObject.getString("thumbnail")
                            var sortDescription = playlistItemObject.getString("sortDescription")
                            listPlaylist.add(ItemHome(ItemHome.ITEM_TYPE.PLAYLIST, playlistID, playlistTitle,sortDescription, playlistThumb))
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



        /*var listItemHome = mutableListOf<ItemHome>()
        listItemHome.add(ItemHome(ItemHome.ITEM_TYPE.SONG, "Z6O66IB8", "Anh Tự Do Nhưng Cô Đơn", "Anh-Tu-Do-Nhung-Co-Don-Trung-Quan-Idol", "https://photo-resize-zmp3.zmdcdn.me/w240_r1x1_jpeg/cover/3/8/a/f/38af54f17dc91e441ad41298c5435d34.jpg"))
        listItemHome.add(ItemHome(ItemHome.ITEM_TYPE.SONG, "Z6O66IB8", "Anh Tự Do Nhưng Cô Đơn", "Anh-Tu-Do-Nhung-Co-Don-Trung-Quan-Idol", "https://photo-resize-zmp3.zmdcdn.me/w240_r1x1_jpeg/cover/3/8/a/f/38af54f17dc91e441ad41298c5435d34.jpg"))
        listItemHome.add(ItemHome(ItemHome.ITEM_TYPE.SONG, "Z6O66IB8", "Anh Tự Do Nhưng Cô Đơn", "Anh-Tu-Do-Nhung-Co-Don-Trung-Quan-Idol", "https://photo-resize-zmp3.zmdcdn.me/w240_r1x1_jpeg/cover/3/8/a/f/38af54f17dc91e441ad41298c5435d34.jpg"))
        listItemHome.add(ItemHome(ItemHome.ITEM_TYPE.SONG, "Z6O66IB8", "Anh Tự Do Nhưng Cô Đơn", "Anh-Tu-Do-Nhung-Co-Don-Trung-Quan-Idol", "https://photo-resize-zmp3.zmdcdn.me/w240_r1x1_jpeg/cover/3/8/a/f/38af54f17dc91e441ad41298c5435d34.jpg"))
        listItemHome.add(ItemHome(ItemHome.ITEM_TYPE.SONG, "Z6O66IB8", "Anh Tự Do Nhưng Cô Đơn", "Anh-Tu-Do-Nhung-Co-Don-Trung-Quan-Idol", "https://photo-resize-zmp3.zmdcdn.me/w240_r1x1_jpeg/cover/3/8/a/f/38af54f17dc91e441ad41298c5435d34.jpg"))
        listItemHome.add(ItemHome(ItemHome.ITEM_TYPE.SONG, "Z6O66IB8", "Anh Tự Do Nhưng Cô Đơn", "Anh-Tu-Do-Nhung-Co-Don-Trung-Quan-Idol", "https://photo-resize-zmp3.zmdcdn.me/w240_r1x1_jpeg/cover/3/8/a/f/38af54f17dc91e441ad41298c5435d34.jpg"))

        var sliderItemAdapter = HomeItemAdapter(this.context!!, R.layout.item_home_poster, listItemHome)
        val recycleViewPoster =  fragmentHomeBinding.recycleViewNewReleaseOther
        recycleViewPoster.adapter = sliderItemAdapter*/

        return fragmentHomeBinding.root
    }

    fun displayAllItemHome()
    {
        var newReleaseVpopRecycleView = fragmentHomeBinding.recycleViewNewReleaseVpop
        var listItemNewReleaseVpop = listNewReleaseVpop.map { ItemHome(it) }
        var sliderVpopAdapter = HomeItemAdapter.createHomeItemAdapter(this.context!!, R.layout.item_home_poster, listItemNewReleaseVpop)
        newReleaseVpopRecycleView.adapter = sliderVpopAdapter
        newReleaseVpopRecycleView.adapter!!.notifyDataSetChanged()

        var newReleaseOtherRecycleView = fragmentHomeBinding.recycleViewNewReleaseOther
        var listItemNewReleaseOther = listNewReleaseOther.map { ItemHome(it) }
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


    }
}