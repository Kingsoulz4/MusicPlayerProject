package com.example.musicplayerproject.models.ui

import android.icu.text.CaseMap.Title
import com.example.musicplayerproject.models.data.Album
import com.example.musicplayerproject.models.data.Artist
import com.example.musicplayerproject.models.data.Playlist
import com.example.musicplayerproject.models.data.Song

class ItemHome {
    enum class ITEM_TYPE
    {
        SONG,
        PLAYLIST,
        ARTIS,
        VIDEO,
        BANNER
    }

    lateinit var type: ITEM_TYPE
    lateinit var encodeId: String
    lateinit var title: String
    var alias: String = ""
    lateinit var thumbnail: String

    constructor()

    constructor(song: Song)
    {
        this.type = ITEM_TYPE.SONG
        this.encodeId = song.encodeId
        this.title = song.title
        this.alias = song.alias
        this.thumbnail = song.thumbnail

    }

    constructor(playlist: Playlist)
    {
        this.type = ITEM_TYPE.PLAYLIST
        this.title = playlist.title
        this.encodeId = playlist.encodeId
        this.thumbnail = playlist.thumbnail

    }

    constructor(type: ITEM_TYPE, encodeId: String, title: String, alias: String, thumbnail:String)
    {
        this.type = type
        this.encodeId = encodeId
        this.title = title
        this.alias = alias
        this.thumbnail = thumbnail
    }

}