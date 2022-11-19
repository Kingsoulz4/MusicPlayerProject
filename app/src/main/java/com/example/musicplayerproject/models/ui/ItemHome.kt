package com.example.musicplayerproject.models.ui

import com.example.musicplayerproject.models.data.Album
import com.example.musicplayerproject.models.data.Artist

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
    lateinit var alias: String
    lateinit var thumbnail: String
}