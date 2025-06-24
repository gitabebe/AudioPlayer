package com.example.audioplayer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.audioplayer.data.AudioFolder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayerScreen(viewModel: AudioViewModel) {
    val selectedFolder by viewModel.selectedFolder

    // The content of the screen changes based on whether a folder is selected
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (selectedFolder == null) "Music Folders" else selectedFolder!!.name)
                },
                navigationIcon = {
                    if (selectedFolder != null) {
                        IconButton(onClick = { viewModel.goBackToFolders() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to folders")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (val folder = selectedFolder) {
                null -> FolderList(
                    folders = viewModel.folders,
                    onFolderClick = { viewModel.selectFolder(it) }
                )
                else -> PlayerView(
                    viewModel = viewModel,
                    folder = folder
                )
            }
        }
    }
}

@Composable
private fun FolderList(
    folders: List<AudioFolder>,
    onFolderClick: (AudioFolder) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(folders) { folder ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onFolderClick(folder) },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = "Folder Icon",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = folder.name, style = MaterialTheme.typography.titleLarge)
                }
            }
        }
    }
}

@Composable
private fun PlayerView(viewModel: AudioViewModel, folder: AudioFolder) {
    val isPlaying by viewModel.isPlaying
    val progress by viewModel.progress
    val title by viewModel.currentTitle

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Song List
        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(folder.songs) { index, song ->
                Button(
                    onClick = { viewModel.play(index) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = if (title == song.title) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.buttonColors()
                ) {
                    Text(song.title)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Now Playing Info
        Text("Now Playing: $title", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        // Scrubber / Slider
        Slider(
            value = progress,
            onValueChange = { viewModel.seekTo(it) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Player Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.playPrevious() }) {
                Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", modifier = Modifier.size(40.dp))
            }
            IconButton(onClick = { viewModel.togglePlayPause() }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.PauseCircleFilled else Icons.Default.PlayCircleFilled,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(64.dp)
                )
            }
            IconButton(onClick = { viewModel.playNext() }) {
                Icon(Icons.Default.SkipNext, contentDescription = "Next", modifier = Modifier.size(40.dp))
            }
        }
    }
}
