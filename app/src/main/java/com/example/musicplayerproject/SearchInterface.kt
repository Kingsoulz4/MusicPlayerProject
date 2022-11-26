package com.example.musicplayerproject

import com.example.musicplayerproject.models.SearchItems

interface SearchInterface {
    fun deleteEntry(pos: Int)
    fun addToRecent(recent: SearchItems)
}