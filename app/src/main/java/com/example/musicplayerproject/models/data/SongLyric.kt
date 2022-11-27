package com.example.musicplayerproject.models.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class SongLyric : java.io.Serializable {
    class Word
    {
        var startTime = 0f
        var endTime = 0f
        var data = ""
    }

    var sentences: MutableList<MutableList<Word>> = mutableListOf<MutableList<Word>>()

    var streamingURL = ""

    companion object
    {
        fun parseData(data: JSONObject): SongLyric
        {
            var songLyric = SongLyric()
            songLyric.streamingURL = data.getString("streamingUrl")
            try {
                var gson = Gson()
                var sentenceObject = data.getJSONArray("sentences")
                for (i in 0 until sentenceObject.length())
                {
                    var listWordType = object: TypeToken<MutableList<Word>>() {}.type
                    var s = sentenceObject.toString()
                    var listWord : MutableList<Word> = gson.fromJson(sentenceObject.getJSONObject(i).getJSONArray("words").toString(), listWordType)
                    songLyric.sentences.add(listWord)
                }
            }
            catch (e: Exception)
            {
                return  songLyric
            }

            return songLyric

        }
    }


}