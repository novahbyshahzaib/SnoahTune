package com.snoahtune.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.snoahtune.app.ui.navigation.NavGraph
import com.snoahtune.app.ui.screens.PermissionScreen
import com.snoahtune.app.ui.theme.SnoahTuneTheme
import com.snoahtune.app.viewmodel.HomeViewModel
import com.snoahtune.app.viewmodel.PlayerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val playerViewModel: PlayerViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SnoahTuneTheme {
                val permissions = buildList {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        add(Manifest.permission.READ_MEDIA_AUDIO)
                        add(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        add(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }

                val permState = rememberMultiplePermissionsState(permissions)

                LaunchedEffect(permState.allPermissionsGranted) {
                    if (permState.allPermissionsGranted) {
                        homeViewModel.refreshSongs()
                        playerViewModel.connectToService()
                    }
                }

                if (permState.allPermissionsGranted) {
                    NavGraph(playerViewModel = playerViewModel, homeViewModel = homeViewModel)
                } else {
                    PermissionScreen(
                        onRequestPermission = { permState.launchMultiplePermissionRequest() }
                    )
                    LaunchedEffect(Unit) {
                        permState.launchMultiplePermissionRequest()
                    }
                }
            }
        }
    }
}
