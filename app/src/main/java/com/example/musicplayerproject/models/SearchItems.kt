package com.example.musicplayerproject.models

import com.example.musicplayerproject.models.data.Song

class SearchItems : java.io.Serializable {
    lateinit var title: String
    lateinit var artistsNames: String
    lateinit var thumbnail: String
    var listSong: MutableList<Song> = mutableListOf()
    var type: Int = 0       //0 = Song, 1 = Video, 2 = Playlist
}