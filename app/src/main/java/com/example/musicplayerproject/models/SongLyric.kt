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

    var sentences: MutableList<MutableList<Word>> = mutableListOf<MutableList<Word>>()
    lateinit var file: String

    companion object
    {
        fun parseData(data: JSONObject): SongLyric
        {
            var gson = Gson()
            var file = data.getString("file")
            var sentenceObject = data.getJSONArray("sentences")
            var songLyric = SongLyric()
            for (i in 0 until sentenceObject.length())
            {
                var listWordType = object: TypeToken<MutableList<Word>>() {}.type
                var s = sentenceObject.toString()
                var listWord : MutableList<Word> = gson.fromJson(sentenceObject.getJSONObject(i).getJSONArray("words").toString(), listWordType)
                songLyric.sentences.add(listWord)
            }
            songLyric.file = file
            return songLyric

        }
    }


}