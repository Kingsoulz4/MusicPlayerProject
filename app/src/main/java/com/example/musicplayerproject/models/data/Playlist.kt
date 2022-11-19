package com.example.musicplayerproject.models.data

import org.json.JSONObject

class Playlist {
    lateinit var encodeId: String
    lateinit var  title: String
    lateinit var thumbnail: String
    var listSong: MutableList<Song> = mutableListOf<Song>()

    companion object {
        fun parseData(playlistJSONObject: JSONObject): Playlist
        {
            var playlist = Playlist()
            playlist.encodeId = playlistJSONObject.getString("encodeId")
            playlist.title = playlistJSONObject.getString("title")
            playlist.thumbnail = playlistJSONObject.getString("thumbnail")
            var listSongObject = playlistJSONObject.getJSONObject("song").getJSONArray("items")
            for (i in 0 until listSongObject.length())
            {
                var songObject = listSongObject.getJSONObject(i)
                var song = Song.parseSongViaJsonObject(songObject)
                playlist.listSong.add(song)
            }
            return playlist
        }
    }
}