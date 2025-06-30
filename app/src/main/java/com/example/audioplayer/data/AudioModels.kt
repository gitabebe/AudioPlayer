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

// Represents a top-level category containing a list of audio folders
data class MainCategory(
    val name: String,
    val folders: List<AudioFolder>
)
