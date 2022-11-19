package com.example.musicplayerproject.models.data

import org.json.JSONObject

class Video {
    lateinit var encodeId: String
    lateinit var title: String
    lateinit var artistNames: String
    lateinit var thumbnail: String
    lateinit var streamingLink: String

    constructor()

    companion object
    {
        fun parseVideoViaJsonObject(videoJSONObject: JSONObject): Video
        {
            var vid = Video()
            vid.encodeId = videoJSONObject.getString("encodeId")
            vid.title = videoJSONObject.getString("title")
            vid.artistNames = videoJSONObject.getString("artistNames")
            vid.thumbnail = videoJSONObject.getString("thumbnail")
            return vid
        }
    }



}