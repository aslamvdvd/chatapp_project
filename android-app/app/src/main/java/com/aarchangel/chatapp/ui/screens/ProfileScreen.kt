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
import com.aarchangel.chatapp.ui.theme.ChatAppTheme
import com.aarchangel.chatapp.viewmodel.LoginViewModel

@Composable
fun ProfileScreen(
    loginViewModel: LoginViewModel
) {
    val profileState by loginViewModel.profileUiState.collectAsState()

    ChatAppTheme {
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
                Text(
                    text = profileState.userProfile?.let { userProfile ->
                        val firstName = userProfile.firstName
                        val middleName = userProfile.middleName
                        val lastName = userProfile.lastName
            
                        // Construct the full name
                        val fullName = buildString {
                            append(firstName)
                            if (!middleName.isNullOrBlank()) {
                                append(" ")
                                append(middleName)
                            }
                            append(" ")
                            append(lastName)
                        }.trim()
            
                        // Display "Welcome, User" if the constructed name is empty, otherwise "Welcome, Full Name"
                        if (fullName.isNotBlank()) {
                            "Welcome, $fullName!"
                        } else {
                            "Welcome, User!"
                        }
                    } ?: "Welcome, User", // Fallback if userProfile itself is null
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(32.dp))
            
                profileState.userProfile?.let {
                    Text("Email: ${it.email}", style = MaterialTheme.typography.bodyLarge)
                    Text("Joined: ${it.createdAt}", style = MaterialTheme.typography.bodyLarge)
                }
            
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { loginViewModel.logout() }) {
                    Text("Log Out")
                }
            }
        }
    }
} 