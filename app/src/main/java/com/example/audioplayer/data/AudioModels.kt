package com.example.audioplayer.data

// Represents a single song
data class Song(
    val title: String,
    val resId: Int
)

// Represents a folder containing a list of songs
data class AudioFolder(
    val name: String,
    val songs: List<Song>
)