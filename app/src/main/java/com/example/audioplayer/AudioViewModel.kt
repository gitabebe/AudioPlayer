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

    fun play(resId: Int, title: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, resId).apply {
            start()
        }
        _isPlaying.value = true
        _currentTitle.value = title
        handler.post(updateProgress)
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

    override fun onCleared() {
        mediaPlayer?.release()
        handler.removeCallbacks(updateProgress)
        super.onCleared()
    }
}