package com.example.audioplayer

import android.app.Application
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel

class AudioViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

    private val _progress = mutableStateOf(0f)
    val progress: State<Float> = _progress

    private val _currentTitle = mutableStateOf("")
    val currentTitle: State<String> = _currentTitle

    // New properties
    private val _currentSongIndex = mutableStateOf(0)
    val currentSongIndex: State<Int> = _currentSongIndex

    private var songs: List<Pair<String, Int>> = emptyList()

    private val handler = Handler(Looper.getMainLooper())
    private val updateProgress = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    _progress.value = it.currentPosition.toFloat() / it.duration
                    handler.postDelayed(this, 500)
                }
            }
        }
    }

    init {
        // Initialize the list of songs here instead of in the AudioPlayerScreen
        songs = listOf(
            "a1" to R.raw.a1,
            "a2" to R.raw.a2,
            "a3" to R.raw.a3,
            "a4" to R.raw.a4
        )
    }

    fun play(index: Int) {
        if (index < 0 || index >= songs.size) return //Prevent index out of bound exception
        val (title, resId) = songs[index]
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, resId).apply {
            start()
            setOnCompletionListener {
                playNext() // Play next song when current finishes
            }
        }
        _isPlaying.value = true
        _currentTitle.value = title
        _currentSongIndex.value = index
        handler.post(updateProgress)
    }

    fun play(resId: Int, title: String) { //Kept play() so you don't have to change code elsewhere.
        val index = songs.indexOfFirst { it.second == resId }
        if (index != -1) {
            play(index)
        } else {
            //Handle if the resource id is not found in the song list.
        }

    }


    fun togglePlayPause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
                handler.removeCallbacks(updateProgress)
            } else {
                it.start()
                _isPlaying.value = true
                handler.post(updateProgress)
            }
        }
    }

    fun seekTo(position: Float) {
        mediaPlayer?.let {
            val newPosition = (it.duration * position).toInt()
            it.seekTo(newPosition)
        }
    }

    fun playNext() {
        val nextIndex = (_currentSongIndex.value + 1) % songs.size
        play(nextIndex)
    }

    fun playPrevious() {
        val previousIndex = (_currentSongIndex.value - 1 + songs.size) % songs.size
        play(previousIndex)
    }

    override fun onCleared() {
        mediaPlayer?.release()
        handler.removeCallbacks(updateProgress)
        super.onCleared()
    }
}
