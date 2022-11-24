package com.example.musicplayerproject.adapters

import android.content.Intent
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
import com.example.musicplayerproject.models.data.Song
import com.example.musicplayerproject.models.data.Video


class SearchAdapter : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var songsList = mutableListOf<Song>()
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
        val song = songsList[position]
        holder.bind(song, position)
        holder.setItemClickListener(object : OnSearchItemClickListener {
            override fun onClick(view: View, position: Int) {
                searchInf?.addToRecent(song)

                val intent = Intent(view.context, PlayerActivity::class.java)
                intent.putExtra("Song_URL", songsList[position].streamingLink)
                view.context.startActivity(intent)
            }
        })
    }

    override fun getItemCount(): Int {
        return songsList.size
    }

    fun setup(inf: SearchInterface) {
        this.searchInf = inf
    }

    fun addSongs(songs: MutableList<Song>) {
        songsList.clear()
        songsList.addAll(songs)
    }

    fun addVideos(videos: MutableList<Video>) {
        songsList.clear()

        for (i in 0 until videos.size)  {
            val song = Song()
            song.title = videos[i].title
            song.artistsNames = videos[i].artistNames
            song.streamingLink = videos[i].streamingLink
            songsList.plusAssign(song)
        }
    }

    fun clearSongs() {
        songsList.clear()
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

        fun bind(song: Song, pos: Int) {
            title.text = song.title
            artist.text = song.artistsNames
            position = pos
            deleteEntry.setOnClickListener{
                songsList.removeAt(position!!)
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