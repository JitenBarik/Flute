package com.flute.music.ui.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flute.music.data.model.Song
import com.flute.music.ui.components.SectionHeader
import com.flute.music.ui.components.SongCard
import com.flute.music.ui.components.SongListItem
import com.flute.music.ui.player.PlayerViewModel
import com.flute.music.ui.theme.BackgroundDark
import com.flute.music.ui.theme.FlutePurple
import com.flute.music.ui.theme.TextPrimary

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    playerViewModel: PlayerViewModel,
    modifier: Modifier = Modifier
) {
    val state by homeViewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // App header & Greeting
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.GraphicEq,
                        contentDescription = "Flute Logo",
                        tint = FlutePurple,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Flute",
                        color = FlutePurple,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = state.greeting,
                    color = TextPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Section: Listen Again (YouTube Music style)
        if (state.recentSongs.isNotEmpty()) {
            item {
                SectionHeader(title = "Listen Again")
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.recentSongs) { song ->
                        SongCard(
                            song = song,
                            onClick = { playerViewModel.playSong(song) }
                        )
                    }
                }
            }
        }

        // Section: Trending on Jamendo (Free Online Music)
        if (state.trendingTracks.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(title = "Trending on Flute")
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(state.trendingTracks) { index, song ->
                        SongCard(
                            song = song,
                            onClick = { playerViewModel.playAll(state.trendingTracks, index) }
                        )
                    }
                }
            }
        }

        // Section: Quick Picks (Device Songs)
        if (state.quickPicks.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(title = "Quick Picks")
            }
            itemsIndexed(state.quickPicks) { index, song ->
                SongListItem(
                    song = song,
                    onClick = { playerViewModel.playAll(state.quickPicks, index) },
                    onMoreClick = { /* Can show options menu */ }
                )
            }
        }

        // Loading indicator if everything is still loading
        if (state.isLoading && state.recentSongs.isEmpty() && state.trendingTracks.isEmpty() && state.quickPicks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = FlutePurple)
                }
            }
        }
    }
}
