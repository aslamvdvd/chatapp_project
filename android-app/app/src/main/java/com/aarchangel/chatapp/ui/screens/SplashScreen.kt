package com.aarchangel.chatapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.aarchangel.chatapp.model.SessionState
import com.aarchangel.chatapp.navigation.AppScreen
import com.aarchangel.chatapp.viewmodel.MainViewModel

@Composable
fun SplashScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    val sessionState by mainViewModel.sessionState.collectAsState()

    LaunchedEffect(sessionState) {
        when (sessionState) {
            is SessionState.LoggedIn -> {
                navController.navigate(AppScreen.Home.route) {
                    popUpTo(AppScreen.Splash.route) { inclusive = true }
                }
            }
            is SessionState.LoggedOut -> {
                navController.navigate(AppScreen.Welcome.route) {
                    popUpTo(AppScreen.Splash.route) { inclusive = true }
                }
            }
            SessionState.Loading -> {
                // Do nothing, just show the loading screen
            }
        }
    }

    Surface {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
} 