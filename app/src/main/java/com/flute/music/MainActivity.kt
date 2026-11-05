package com.flute.music

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.flute.music.ui.components.MiniPlayer
import com.flute.music.ui.navigation.FluteNavHost
import com.flute.music.ui.navigation.Screen
import com.flute.music.ui.navigation.bottomNavItems
import com.flute.music.ui.player.PlayerViewModel
import com.flute.music.ui.theme.BackgroundDark
import com.flute.music.ui.theme.FlutePurple
import com.flute.music.ui.theme.FluteTheme
import com.flute.music.ui.theme.SurfaceDark
import com.flute.music.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // Permissions handled; viewmodels reload if needed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestRequiredPermissions()

        val appContainer = (application as FluteApp).appContainer

        setContent {
            FluteTheme {
                val navController = rememberNavController()
                val playerViewModel: PlayerViewModel = viewModel(
                    factory = PlayerViewModel.Factory(appContainer.musicPlayerManager)
                )

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val showBottomBar = currentRoute != Screen.Player.route

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            Column {
                                MiniPlayer(
                                    viewModel = playerViewModel,
                                    onClick = {
                                        navController.navigate(Screen.Player.route)
                                    }
                                )
                                NavigationBar(
                                    containerColor = SurfaceDark,
                                    tonalElevation = 0.dp
                                ) {
                                    bottomNavItems.forEach { item ->
                                        val selected = currentRoute == item.route
                                        NavigationBarItem(
                                            selected = selected,
                                            onClick = {
                                                if (currentRoute != item.route) {
                                                    navController.navigate(item.route) {
                                                        popUpTo(navController.graph.findStartDestination().id) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                }
                                            },
                                            icon = {
                                                Icon(
                                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                                    contentDescription = item.title
                                                )
                                            },
                                            label = {
                                                Text(text = item.title)
                                            },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = FlutePurple,
                                                selectedTextColor = FlutePurple,
                                                indicatorColor = Color.Transparent,
                                                unselectedIconColor = TextSecondary,
                                                unselectedTextColor = TextSecondary
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    },
                    containerColor = BackgroundDark
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        FluteNavHost(
                            navController = navController,
                            playerViewModel = playerViewModel,
                            localMusicRepository = appContainer.localMusicRepository,
                            onlineMusicRepository = appContainer.onlineMusicRepository,
                            playlistRepository = appContainer.playlistRepository,
                            likedSongsRepository = appContainer.likedSongsRepository,
                            recentRepository = appContainer.recentRepository
                        )
                    }
                }
            }
        }
    }

    private fun requestRequiredPermissions() {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        try {
            if (permissions.isNotEmpty()) {
                permissionLauncher.launch(permissions.toTypedArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
