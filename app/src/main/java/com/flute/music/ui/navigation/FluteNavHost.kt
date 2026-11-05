package com.flute.music.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.flute.music.data.repository.LikedSongsRepository
import com.flute.music.data.repository.LocalMusicRepository
import com.flute.music.data.repository.OnlineMusicRepository
import com.flute.music.data.repository.PlaylistRepository
import com.flute.music.data.repository.RecentRepository
import com.flute.music.ui.home.HomeScreen
import com.flute.music.ui.home.HomeViewModel
import com.flute.music.ui.library.LibraryScreen
import com.flute.music.ui.library.LibraryViewModel
import com.flute.music.ui.player.PlayerScreen
import com.flute.music.ui.player.PlayerViewModel
import com.flute.music.ui.search.SearchScreen
import com.flute.music.ui.search.SearchViewModel
import com.flute.music.ui.settings.SettingsScreen

@Composable
fun FluteNavHost(
    navController: NavHostController,
    playerViewModel: PlayerViewModel,
    localMusicRepository: LocalMusicRepository,
    onlineMusicRepository: OnlineMusicRepository,
    playlistRepository: PlaylistRepository,
    likedSongsRepository: LikedSongsRepository,
    recentRepository: RecentRepository,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.Factory(
                    localMusicRepository,
                    onlineMusicRepository,
                    recentRepository
                )
            )
            HomeScreen(
                homeViewModel = homeViewModel,
                playerViewModel = playerViewModel
            )
        }

        composable(Screen.Search.route) {
            val searchViewModel: SearchViewModel = viewModel(
                factory = SearchViewModel.Factory(
                    localMusicRepository,
                    onlineMusicRepository
                )
            )
            SearchScreen(
                searchViewModel = searchViewModel,
                playerViewModel = playerViewModel
            )
        }

        composable(Screen.Library.route) {
            val libraryViewModel: LibraryViewModel = viewModel(
                factory = LibraryViewModel.Factory(
                    localMusicRepository,
                    playlistRepository,
                    likedSongsRepository
                )
            )
            LibraryScreen(
                libraryViewModel = libraryViewModel,
                playerViewModel = playerViewModel
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen()
        }

        composable(Screen.Player.route) {
            PlayerScreen(
                viewModel = playerViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
