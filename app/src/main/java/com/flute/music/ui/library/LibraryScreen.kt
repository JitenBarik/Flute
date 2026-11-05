package com.flute.music.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flute.music.ui.components.EmptyState
import com.flute.music.ui.components.SectionHeader
import com.flute.music.ui.components.SongListItem
import com.flute.music.ui.player.PlayerViewModel
import com.flute.music.ui.theme.BackgroundDark
import com.flute.music.ui.theme.CardDark
import com.flute.music.ui.theme.FlutePurple
import com.flute.music.ui.theme.HeartRed
import com.flute.music.ui.theme.SurfaceDark
import com.flute.music.ui.theme.TextPrimary
import com.flute.music.ui.theme.TextSecondary

@Composable
fun LibraryScreen(
    libraryViewModel: LibraryViewModel,
    playerViewModel: PlayerViewModel,
    modifier: Modifier = Modifier
) {
    val state by libraryViewModel.uiState.collectAsState()
    var newPlaylistName by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.currentTab != LibraryTab.OVERVIEW) {
                    IconButton(onClick = { libraryViewModel.setTab(LibraryTab.OVERVIEW) }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                val title = when (state.currentTab) {
                    LibraryTab.OVERVIEW -> "Your Library"
                    LibraryTab.ALL_SONGS -> "All Songs"
                    LibraryTab.LIKED_SONGS -> "Liked Songs"
                    LibraryTab.PLAYLISTS -> "Playlists"
                }

                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Content switch based on active view
            when (state.currentTab) {
                LibraryTab.OVERVIEW -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            LibraryCategoryCard(
                                icon = Icons.Rounded.Favorite,
                                iconTint = HeartRed,
                                title = "Liked Songs",
                                subtitle = "${state.likedSongs.size} songs",
                                onClick = { libraryViewModel.setTab(LibraryTab.LIKED_SONGS) }
                            )
                        }

                        item {
                            LibraryCategoryCard(
                                icon = Icons.Rounded.MusicNote,
                                iconTint = FlutePurple,
                                title = "All Songs",
                                subtitle = "${state.allSongs.size} tracks on device",
                                onClick = { libraryViewModel.setTab(LibraryTab.ALL_SONGS) }
                            )
                        }

                        item {
                            LibraryCategoryCard(
                                icon = Icons.Rounded.QueueMusic,
                                iconTint = Color(0xFF29B6F6),
                                title = "Playlists",
                                subtitle = "${state.playlists.size} playlists",
                                onClick = { libraryViewModel.setTab(LibraryTab.PLAYLISTS) }
                            )
                        }
                    }
                }

                LibraryTab.ALL_SONGS -> {
                    if (state.allSongs.isEmpty()) {
                        EmptyState(
                            icon = Icons.Rounded.MusicNote,
                            title = "No songs found",
                            subtitle = "Make sure audio files are saved on your phone."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 100.dp)
                        ) {
                            itemsIndexed(state.allSongs) { index, song ->
                                SongListItem(
                                    song = song,
                                    onClick = { playerViewModel.playAll(state.allSongs, index) }
                                )
                            }
                        }
                    }
                }

                LibraryTab.LIKED_SONGS -> {
                    if (state.likedSongs.isEmpty()) {
                        EmptyState(
                            icon = Icons.Rounded.Favorite,
                            title = "No liked songs yet",
                            subtitle = "Tap the heart icon while playing any song to save it here."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 100.dp)
                        ) {
                            itemsIndexed(state.likedSongs) { index, song ->
                                SongListItem(
                                    song = song,
                                    onClick = { playerViewModel.playAll(state.likedSongs, index) }
                                )
                            }
                        }
                    }
                }

                LibraryTab.PLAYLISTS -> {
                    if (state.playlists.isEmpty()) {
                        EmptyState(
                            icon = Icons.Rounded.QueueMusic,
                            title = "No playlists created",
                            subtitle = "Tap the + button below to create your first playlist."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 100.dp)
                        ) {
                            items(state.playlists) { playlist ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { /* Future detail screen */ }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = playlist.name,
                                            color = TextPrimary,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Playlist",
                                            color = TextSecondary,
                                            fontSize = 13.sp
                                        )
                                    }
                                    IconButton(onClick = { libraryViewModel.deletePlaylist(playlist.id) }) {
                                        Icon(
                                            imageVector = Icons.Rounded.Delete,
                                            contentDescription = "Delete",
                                            tint = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // FAB to create Playlist
        if (state.currentTab == LibraryTab.PLAYLISTS || state.currentTab == LibraryTab.OVERVIEW) {
            FloatingActionButton(
                onClick = { libraryViewModel.showCreatePlaylistDialog(true) },
                containerColor = FlutePurple,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 90.dp, end = 20.dp)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Create Playlist")
            }
        }

        // Dialog for playlist creation
        if (state.showCreatePlaylistDialog) {
            AlertDialog(
                onDismissRequest = { libraryViewModel.showCreatePlaylistDialog(false) },
                title = { Text("New Playlist", color = TextPrimary) },
                text = {
                    OutlinedTextField(
                        value = newPlaylistName,
                        onValueChange = { newPlaylistName = it },
                        placeholder = { Text("Playlist Name") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            libraryViewModel.createPlaylist(newPlaylistName)
                            newPlaylistName = ""
                        }
                    ) {
                        Text("Create", color = FlutePurple)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { libraryViewModel.showCreatePlaylistDialog(false) }) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = SurfaceDark
            )
        }
    }
}

@Composable
private fun LibraryCategoryCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 13.sp
            )
        }
    }
}
