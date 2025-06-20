package com.example.audioplayer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun AudioPlayerScreen(viewModel: AudioViewModel) {
    val isPlaying by viewModel.isPlaying
    val progress by viewModel.progress
    val title by viewModel.currentTitle

    val songs = listOf(
        "Relaxing Sound 1" to R.raw.audio1,
        "Chill Beat 2" to R.raw.audio2,
        "Nature Ambience 3" to R.raw.audio3
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Now Playing: $title", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        songs.forEach { (name, resId) ->
            Button(
                onClick = { viewModel.play(resId, name) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(name)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
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

            Slider(
                value = progress,
                onValueChange = {}, // Seeking not yet implemented
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            )
        }
    }
}