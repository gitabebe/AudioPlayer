package com.example.audioplayer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon as Icon

@Composable
fun AudioPlayerScreen(viewModel: AudioViewModel) {
    val isPlaying by viewModel.isPlaying
    val progress by viewModel.progress
    val title by viewModel.currentTitle
    val currentSongIndex by viewModel.currentSongIndex

    val songs = remember {
        listOf(
            "a1" to R.raw.a1,
            "a2" to R.raw.a2,
            "a3" to R.raw.a3,
            "a4" to R.raw.a4
        )
    } //Moved song list to viewModel instead

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Now Playing: $title", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        songs.forEachIndexed { index, (name, resId) ->
            Button(
                onClick = { viewModel.play(index) },  // Now pass the index
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(name)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.playPrevious() }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous"
                )
            }
            IconButton(onClick = { viewModel.togglePlayPause() }) {
                Icon(
                    painter = painterResource(
                        id = if (isPlaying)
                            android.R.drawable.ic_media_pause
                        else
                            android.R.drawable.ic_media_play
                    ),
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }

            IconButton(onClick = { viewModel.playNext() }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next"
                )
            }


        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.seekTo(progress - 0.1f) }) {
                Icon(
                    imageVector = Icons.Default.FastRewind,
                    contentDescription = "Rewind"
                )
            }


            Slider(
                value = progress,
                onValueChange = { viewModel.seekTo(it) }, // Seeking is  implemented
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            )
            IconButton(onClick = { viewModel.seekTo(progress + 0.1f) }) {
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = "Forward"
                )
            }

        }
    }
}
