package com.example.audioplayer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.audioplayer.data.AudioFolder
import com.example.audioplayer.data.MainCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayerScreen(viewModel: AudioViewModel) {
    val selectedCategory by viewModel.selectedCategory
    val selectedFolder by viewModel.selectedFolder

    // Determine the current title and back navigation action based on the navigation depth
    val (title, onBackPressed) = when {
        selectedFolder != null -> selectedFolder!!.name to { viewModel.goBackToFolders() }
        selectedCategory != null -> selectedCategory!!.name to { viewModel.goBackToCategories() }
        else -> "Audio Player" to null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    // Show back button if onBackPressed is not null
                    onBackPressed?.let {
                        IconButton(onClick = it) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            // Conditionally display the correct screen based on selection state
            when {
                selectedFolder != null -> {
                    // Level 3: Player View for the selected folder
                    PlayerView(
                        viewModel = viewModel,
                        folder = selectedFolder!!
                    )
                }
                selectedCategory != null -> {
                    // Level 2: Folder List for the selected category
                    FolderList(
                        folders = selectedCategory!!.folders,
                        onFolderClick = { viewModel.selectFolder(it) }
                    )
                }
                else -> {
                    // Level 1: Top-level Category List
                    CategoryList(
                        categories = viewModel.categories,
                        onCategoryClick = { viewModel.selectCategory(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryList(
    categories: List<MainCategory>,
    onCategoryClick: (MainCategory) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(categories) { category ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onCategoryClick(category) },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderSpecial, // Using a different icon for categories
                        contentDescription = "Category Icon",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = category.name, style = MaterialTheme.typography.titleLarge)
                }
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
                    // Highlight the currently playing song
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
