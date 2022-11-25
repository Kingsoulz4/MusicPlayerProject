package com.example.musicplayerproject.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerproject.OnSearchItemClickListener
import com.example.musicplayerproject.R
import com.example.musicplayerproject.SearchInterface
import com.example.musicplayerproject.activities.PlayerActivity
import com.example.musicplayerproject.models.SearchItems
import com.example.musicplayerproject.models.data.Playlist
import com.example.musicplayerproject.models.data.Song
import com.example.musicplayerproject.models.data.Video


class SearchAdapter : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var displayList = mutableListOf<SearchItems>()

    private lateinit var recyclerView: RecyclerView
    private var searchInf: SearchInterface? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.search_items_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = displayList[position]
        holder.bind(entry, position)

        holder.setItemClickListener(object : OnSearchItemClickListener {
            override fun onClick(view: View, position: Int) {
                searchInf?.addToRecent(entry)
                val intent = Intent(view.context, PlayerActivity::class.java)
                intent.putExtra("playItem", entry)
                Log.v("Music", "Test")
                view.context.startActivity(intent)
            }
        })
    }

    override fun getItemCount(): Int {
        return displayList.size
    }

    fun setup(inf: SearchInterface) {
        this.searchInf = inf
    }

    fun addRecent(recent: MutableList<SearchItems>) {
        displayList.clear()
        displayList.addAll(recent)
    }

    fun addSongs(songs: MutableList<Song>) {
        displayList.clear()
        for (i in 0 until songs.size) {
            val searchItems = SearchItems()
            searchItems.title = songs[i].title
            searchItems.artistsNames = songs[i].artistsNames
            searchItems.listSong.plusAssign(songs[i])
            searchItems.type = 0
            displayList.plusAssign(searchItems)
        }

    }

    fun addVideos(videos: MutableList<Video>) {
        displayList.clear()
        for (i in 0 until videos.size) {
            val searchItems = SearchItems()
            searchItems.title = videos[i].title
            searchItems.artistsNames = videos[i].artistNames
            val song = Song()
            song.title = videos[i].title
            song.artistsNames = videos[i].artistNames
            song.streamingLink = videos[i].streamingLink
            searchItems.listSong.plusAssign(song)
            searchItems.type = 1
            displayList.plusAssign(searchItems)
        }
    }

    fun addPlaylists(playlists: MutableList<Playlist>) {
        displayList.clear()
        for (i in 0 until playlists.size) {
            val searchItems = SearchItems()
            searchItems.title = playlists[i].title
            searchItems.artistsNames = "Zing Playlist"
            searchItems.listSong.addAll(playlists[i].listSong)
            searchItems.type = 2
            displayList.plusAssign(searchItems)
        }
    }

    fun clearSongs() {
        displayList.clear()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val title: TextView = itemView.findViewById(R.id.searchSongTitle)
        private val artist: TextView = itemView.findViewById(R.id.searchArtistName)
        private val deleteEntry: ImageButton = itemView.findViewById(R.id.deleteEntry)

        private lateinit var itemClickListener: OnSearchItemClickListener
        private var position: Int? = null

        fun setItemClickListener(itemClickListener: OnSearchItemClickListener) {
            this.itemClickListener = itemClickListener
        }

        fun bind(entry: SearchItems, pos: Int) {
            title.text = entry.title
            artist.text = entry.artistsNames
            position = pos
            deleteEntry.setOnClickListener{
                displayList.removeAt(position!!)
                searchInf?.deleteEntry(position!!)
                this@SearchAdapter.notifyDataSetChanged()
            }
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (v != null) {
                itemClickListener.onClick(v, bindingAdapterPosition)
            }
        }
    }
}