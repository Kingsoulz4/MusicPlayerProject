package com.example.musicplayerproject.models.data

import org.json.JSONObject

class Artist {
    lateinit var id: String
    lateinit var name: String
    lateinit var alias: String
    lateinit var thumbnail: String
    lateinit var playListId: String

    constructor()

    companion object
    {
        fun parseArtistViaJsonObject(artistJSONObject: JSONObject): Artist
        {
            var artist = Artist()
            artist.id = artistJSONObject.getString("id")
            artist.name = artistJSONObject.getString("name")
            artist.thumbnail = artistJSONObject.getString("thumbnail")
            artist.playListId = artistJSONObject.getString("playlistId")
            return artist
        }
    }
}