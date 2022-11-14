package com.example.musicplayerproject.models

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class SongLyric {
    class Word
    {
        var startTime = 0f
        var endTime = 0f
        var data = ""
    }

    lateinit var sentences: MutableList<MutableList<Word>>
    lateinit var file: String

    fun parseData(data: JSONObject): SongLyric
    {
        var gson = Gson()
        var file = data.getString("file")
        var sentenceObject = data.getJSONArray("sentences")
        var listSentenceType = object: TypeToken<MutableList<MutableList<Word>>>() {}.type
        var listSentence : MutableList<MutableList<Word>> = gson.fromJson(sentenceObject.toString(), listSentenceType)
        var songLyric = SongLyric()
        songLyric.file = file
        songLyric.sentences = listSentence
        return songLyric

    }
}