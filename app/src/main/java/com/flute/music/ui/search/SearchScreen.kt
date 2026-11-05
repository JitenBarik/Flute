package com.flute.music.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.flute.music.ui.theme.SurfaceDark
import com.flute.music.ui.theme.TextPrimary
import com.flute.music.ui.theme.TextSecondary
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    playerViewModel: PlayerViewModel,
    modifier: Modifier = Modifier
) {
    val state by searchViewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Search Input Bar
        OutlinedTextField(
            value = state.query,
            onValueChange = { searchViewModel.onQueryChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            placeholder = { Text("Search songs, artists, albums...", color = TextSecondary) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search",
                    tint = TextSecondary
                )
            },
            trailingIcon = {
                if (state.query.isNotEmpty()) {
                    IconButton(onClick = { searchViewModel.onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = "Clear",
                            tint = TextSecondary
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceDark,
                unfocusedContainerColor = SurfaceDark,
                focusedBorderColor = FlutePurple,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = FlutePurple
            )
        )

        // Genres Row
        Text(
            text = "Browse Genres",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(searchViewModel.genres) { genre ->
                val isSelected = state.selectedGenre == genre
                FilterChip(
                    selected = isSelected,
                    onClick = { searchViewModel.selectGenre(genre) },
                    label = { Text(genre) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = CardDark,
                        labelColor = TextSecondary,
                        selectedContainerColor = FlutePurple,
                        selectedLabelColor = Color.White
                    ),
                    border = null,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search Content / Results
        if (state.isSearching) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Searching music...",
                    color = FlutePurple,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Genre results if a genre is picked
                if (state.selectedGenre != null && state.genreResults.isNotEmpty()) {
                    item {
                        SectionHeader(title = "${state.selectedGenre} Tracks")
                    }
                    itemsIndexed(state.genreResults) { index, song ->
                        SongListItem(
                            song = song,
                            onClick = { playerViewModel.playAll(state.genreResults, index) }
                        )
                    }
                } else if (state.query.isNotBlank()) {
                    // Local Results
                    if (state.localResults.isNotEmpty()) {
                        item {
                            SectionHeader(title = "On This Device")
                        }
                        itemsIndexed(state.localResults) { index, song ->
                            SongListItem(
                                song = song,
                                onClick = { playerViewModel.playAll(state.localResults, index) }
                            )
                        }
                    }

                    // Online Results
                    if (state.onlineResults.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            SectionHeader(title = "Online (Flute Stream)")
                        }
                        itemsIndexed(state.onlineResults) { index, song ->
                            SongListItem(
                                song = song,
                                onClick = { playerViewModel.playAll(state.onlineResults, index) }
                            )
                        }
                    }

                    // No results state
                    if (state.localResults.isEmpty() && state.onlineResults.isEmpty()) {
                        item {
                            EmptyState(
                                icon = Icons.Rounded.Search,
                                title = "No songs found",
                                subtitle = "Try searching for another song, artist, or album."
                            )
                        }
                    }
                } else if (state.selectedGenre == null) {
                    item {
                        EmptyState(
                            icon = Icons.Rounded.Search,
                            title = "Search for any music",
                            subtitle = "Find songs stored on your device or discover free indie music."
                        )
                    }
                }
            }
        }
    }
}
