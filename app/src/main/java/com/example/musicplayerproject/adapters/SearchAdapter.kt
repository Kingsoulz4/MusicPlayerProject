package com.example.musicplayerproject.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerproject.Communication
import com.example.musicplayerproject.OnSearchItemClickListener
import com.example.musicplayerproject.R
import com.example.musicplayerproject.SearchInterface
import com.example.musicplayerproject.activities.PlayerActivity
import com.example.musicplayerproject.models.data.Song


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
                var intent = Intent(view.context, PlayerActivity::class.java)
                intent.putExtra("Song_URL", songsList[position].artistsNames)

                Log.v("Music", "Start PlayerActivity")
                view.context.startActivity(intent)
                Log.v("Music", songsList[position].title + " " + songsList[position].artistsNames)
            }

        })
    }

    override fun getItemCount(): Int {
        return if (songsList.size > Communication.SEARCH_SIZE) {
            Communication.SEARCH_SIZE
        } else {
            songsList.size
        }
    }

    fun setup(inf: SearchInterface) {
        this.searchInf = inf
    }

    fun addSongs(songs: MutableList<Song>) {
        songsList.clear()
        songsList.addAll(songs)
    }

    fun clearSongs() {
        songsList.clear()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val title: TextView = itemView.findViewById(R.id.searchSongTitle)
        private val artist: TextView = itemView.findViewById(R.id.searchArtistName)
        private val deleteEntry: ImageButton = itemView.findViewById(R.id.deleteEntry)
        val mainItem: ConstraintLayout = itemView.findViewById(R.id.mainConstraint)

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
                Log.v("Music", "DeleteEntry $position")
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