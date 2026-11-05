package com.flute.music

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.flute.music.di.AppContainer
import com.flute.music.ui.components.MiniPlayer
import com.flute.music.ui.navigation.FluteNavHost
import com.flute.music.ui.navigation.Screen
import com.flute.music.ui.navigation.bottomNavItems
import com.flute.music.ui.player.PlayerViewModel
import com.flute.music.ui.theme.BackgroundDark
import com.flute.music.ui.theme.FlutePurple
import com.flute.music.ui.theme.FluteTheme
import com.flute.music.ui.theme.SurfaceDark
import com.flute.music.ui.theme.TextPrimary
import com.flute.music.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // Permissions handled
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestRequiredPermissions()

        val appContainer: AppContainer = try {
            (application as? FluteApp)?.appContainer ?: AppContainer(application)
        } catch (e: Exception) {
            Log.e("MainActivity", "Fallback to new AppContainer", e)
            AppContainer(application)
        }

        val prefs = getSharedPreferences("flute_crash_log", MODE_PRIVATE)
        val initialCrash = prefs.getString("last_crash", null)

        setContent {
            FluteTheme {
                var crashInfo by remember { mutableStateOf(initialCrash) }

                if (crashInfo != null) {
                    // Safe Recovery Screen if a previous crash occurred
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(BackgroundDark)
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Flute Diagnostic Log",
                            color = FlutePurple,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "The app encountered an error on its last launch. Here are the details:",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceDark)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = crashInfo ?: "Unknown error",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Button(
                            onClick = {
                                prefs.edit().remove("last_crash").apply()
                                crashInfo = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FlutePurple)
                        ) {
                            Text("Dismiss & Launch Flute")
                        }
                    }
                } else {
                    MainAppContent(appContainer)
                }
            }
        }
    }

    @Composable
    private fun MainAppContent(appContainer: AppContainer) {
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
