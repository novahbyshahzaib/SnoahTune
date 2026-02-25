package com.snoahtune.app.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.compose.*
import com.snoahtune.app.ui.components.MiniPlayer
import com.snoahtune.app.ui.screens.*
import com.snoahtune.app.ui.theme.*
import com.snoahtune.app.viewmodel.HomeViewModel
import com.snoahtune.app.viewmodel.PlayerViewModel

@Composable
fun NavGraph(playerViewModel: PlayerViewModel, homeViewModel: HomeViewModel) {
    val navController = rememberNavController()
    val currentSong  by playerViewModel.currentSong.collectAsState()
    val isPlaying    by playerViewModel.isPlaying.collectAsState()
    val progress     by playerViewModel.progress.collectAsState()

    Scaffold(
        containerColor = Background,
        bottomBar = {
            Column {
                currentSong?.let { song ->
                    MiniPlayer(
                        song        = song,
                        isPlaying   = isPlaying,
                        progress    = progress,
                        onPlayPause = playerViewModel::playPause,
                        onNext      = playerViewModel::skipNext,
                        onPrevious  = playerViewModel::skipPrevious,
                        onClick     = { navController.navigate("now_playing") }
                    )
                }
                NavigationBar(containerColor = SurfaceWhite, tonalElevation = 0.dp) {
                    val entry by navController.currentBackStackEntryAsState()
                    val route  = entry?.destination?.route

                    NavigationBarItem(
                        icon     = { Icon(Icons.Default.Home, null) },
                        label    = { Text("HOME",    style = MaterialTheme.typography.labelSmall) },
                        selected = route == "home",
                        onClick  = { navController.navigate("home")    { launchSingleTop = true } },
                        colors   = NavigationBarItemDefaults.colors(indicatorColor = ElectricYellow)
                    )
                    NavigationBarItem(
                        icon     = { Icon(Icons.Default.LibraryMusic, null) },
                        label    = { Text("LIBRARY", style = MaterialTheme.typography.labelSmall) },
                        selected = route == "library",
                        onClick  = { navController.navigate("library") { launchSingleTop = true } },
                        colors   = NavigationBarItemDefaults.colors(indicatorColor = HotPink)
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController    = navController,
            startDestination = "home",
            modifier         = Modifier.padding(padding)
        ) {
            composable("home") {
                HomeScreen(homeVM = homeViewModel, playerVM = playerViewModel)
            }
            composable("library") {
                LibraryScreen(homeVM = homeViewModel, playerVM = playerViewModel)
            }
            composable("now_playing") {
                NowPlayingScreen(
                    playerVM = playerViewModel,
                    onBack   = { navController.popBackStack() }
                )
            }
        }
    }
}
