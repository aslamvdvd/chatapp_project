package com.aarchangel.chatapp.ui.screens.splash

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
    val hasAgreedToTerms by mainViewModel.hasAgreedToTerms.collectAsState()

    LaunchedEffect(sessionState, hasAgreedToTerms) {
        // This logic ensures we only navigate away from splash once a decision can be made
        if (sessionState is SessionState.Loading) {
            return@LaunchedEffect // Wait until session check is complete
        }

        val destination = when (sessionState) {
            is SessionState.LoggedIn -> AppScreen.Home.route
            is SessionState.LoggedOut -> {
                if (hasAgreedToTerms) AppScreen.AuthEntry.route else AppScreen.Welcome.route
            }
            else -> null // Should not happen if loading check is above
        }

        destination?.let {
            navController.navigate(it) {
                popUpTo(AppScreen.Splash.route) { inclusive = true }
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