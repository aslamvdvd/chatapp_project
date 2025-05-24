package com.aarchangel.chatapp.ui.screens

// ChatApp by aarchangel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.aarchangel.chatapp.ui.theme.ChatAppTheme
import kotlinx.coroutines.launch

/**
 * Authentication Options Screen for ChatApp.
 * Provides various methods for users to sign up or log in.
 * // ChatApp by aarchangel
 *
 * @param navController The NavController for navigation.
 * @param snackbarHostState The ScaffoldMessengerState to show Snackbars.
 */
@Composable
fun AuthOptionsScreen(navController: NavController, snackbarHostState: SnackbarHostState) {
    val scope = rememberCoroutineScope()

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
            ) {
                Text(
                    text = "Choose Authentication Method",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                Button(
                    onClick = { navController.navigate("email_auth") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Continue with Email")
                }

                Button(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("TODO: Implement Phone Authentication")
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Continue with Phone")
                }

                Button(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("TODO: Implement Gmail Authentication")
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Continue with Gmail")
                }

                Button(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("TODO: Implement Apple Authentication")
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Continue with Apple")
                }
            }
        }
    }
}

/**
 * Preview for the AuthOptionsScreen.
 * Provides a design-time view of the AuthOptionsScreen in Android Studio.
 * // ChatApp by aarchangel
 */
@Preview(showBackground = true)
@Composable
fun AuthOptionsScreenPreview() {
    ChatAppTheme {
        AuthOptionsScreen(
            navController = rememberNavController(),
            snackbarHostState = SnackbarHostState()
        )
    }
} 