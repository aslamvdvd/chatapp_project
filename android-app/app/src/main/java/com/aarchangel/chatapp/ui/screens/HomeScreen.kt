package com.aarchangel.chatapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarchangel.chatapp.model.SessionState
import com.aarchangel.chatapp.ui.theme.ChatAppTheme
import com.aarchangel.chatapp.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    mainViewModel: MainViewModel
) {
    val sessionState by mainViewModel.sessionState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            when (val state = sessionState) {
                is SessionState.LoggedIn -> {
                    val userProfile = state.userProfile
                    Text("Welcome, ${userProfile.firstName}!", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = { mainViewModel.onLogout() }) {
                        Text("Log Out")
                    }
                }
                else -> {
                    // This should not happen if navigation is correct, but as a fallback:
                    Text("Loading...", style = MaterialTheme.typography.headlineMedium)
                }
            }
        }
    }
} 