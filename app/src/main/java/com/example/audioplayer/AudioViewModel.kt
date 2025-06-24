package com.example.audioplayer

import android.app.Application
import android.media.MediaPlayer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audioplayer.data.AudioFolder
import com.example.audioplayer.data.Song
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private var mediaPlayer: MediaPlayer? = null

    // --- State Properties ---

    // UI State: Tracks the currently selected folder. If null, show the folder list.
    private val _selectedFolder = mutableStateOf<AudioFolder?>(null)
    val selectedFolder: State<AudioFolder?> = _selectedFolder

    // Player State
    private val _isPlaying = mutableStateOf(false)
    val isPlaying: State<Boolean> = _isPlaying

    private val _progress = mutableStateOf(0f)
    val progress: State<Float> = _progress

    private val _currentTitle = mutableStateOf("")
    val currentTitle: State<String> = _currentTitle

    // Tracks the index of the song *within the selected folder*
    private val _currentSongIndex = mutableStateOf(0)

    // --- Data ---
    val folders: List<AudioFolder>

    init {
        // Define the folder structure here.
        // Make sure you have added b1.amr, b2.amr, etc. to your res/raw folder!
        folders = listOf(
            AudioFolder(
                name = "Folder 1",
                songs = listOf(
                    Song(title = "a1", resId = R.raw.a1),
                    Song(title = "a2", resId = R.raw.a2),
                    Song(title = "a3", resId = R.raw.a3),
                    Song(title = "a4", resId = R.raw.a4)
                )
            ),
            AudioFolder(
                name = "Folder 2",
                songs = listOf(
                    Song(title = "b1", resId = R.raw.b1),
                    Song(title = "b2", resId = R.raw.b2),
                    Song(title = "b3", resId = R.raw.b3),
                    Song(title = "b4", resId = R.raw.b4)
                )
            )
        )
    }

    // --- Public Functions for UI Interaction ---

    fun selectFolder(folder: AudioFolder) {
        _selectedFolder.value = folder
    }

    fun goBackToFolders() {
        _selectedFolder.value = null
        stopAndReleasePlayer()
    }

    fun play(songIndex: Int) {
        _selectedFolder.value?.let { folder ->
            if (songIndex < 0 || songIndex >= folder.songs.size) return

            val song = folder.songs[songIndex]
            mediaPlayer?.release()

            try {
                mediaPlayer = MediaPlayer.create(context, song.resId).apply {
                    start()
                    setOnCompletionListener { playNext() }
                }

                _isPlaying.value = true
                _currentTitle.value = song.title
                _currentSongIndex.value = songIndex
                startProgressUpdater()
            } catch (e: Exception) {
                _isPlaying.value = false
                e.printStackTrace()
            }
        }
    }

    fun togglePlayPause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
            } else {
                it.start()
                _isPlaying.value = true
                startProgressUpdater()
            }
        }
    }

    fun seekTo(newProgress: Float) {
        mediaPlayer?.let {
            val newPosition = (it.duration * newProgress).toInt()
            it.seekTo(newPosition)
        }
    }

    fun playNext() {
        _selectedFolder.value?.let { folder ->
            val nextIndex = (_currentSongIndex.value + 1) % folder.songs.size
            play(nextIndex)
        }
    }

    fun playPrevious() {
        _selectedFolder.value?.let { folder ->
            val previousIndex = (_currentSongIndex.value - 1 + folder.songs.size) % folder.songs.size
            play(previousIndex)
        }
    }

    // --- Private Helper Functions ---

    private fun startProgressUpdater() {
        viewModelScope.launch {
            while (_isPlaying.value) {
                mediaPlayer?.let {
                    if (it.duration > 0) {
                        _progress.value = it.currentPosition.toFloat() / it.duration
                    }
                }
                delay(500)
            }
        }
    }

    private fun stopAndReleasePlayer() {
        _isPlaying.value = false
        mediaPlayer?.release()
        mediaPlayer = null
        _currentTitle.value = ""
        _progress.value = 0f
    }

    override fun onCleared() {
        stopAndReleasePlayer()
        super.onCleared()
    }
}
