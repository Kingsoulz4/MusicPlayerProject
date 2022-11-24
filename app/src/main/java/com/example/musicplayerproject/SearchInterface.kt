package com.example.musicplayerproject

import com.example.musicplayerproject.models.data.Song

interface SearchInterface {
    fun deleteEntry(pos: Int)
    fun addToRecent(song: Song)
}