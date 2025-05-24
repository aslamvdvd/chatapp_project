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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

/**
 * Authentication Options Screen for ChatApp.
 * Provides various methods for users to sign up or log in.
 * // ChatApp by aarchangel
 *
 * @param navController The NavController for navigation.
 * @param snackbarHostState The ScaffoldMessengerState to show Snackbars.
 * @param flowType A string indicating if the flow is for "signup" or "login" (currently not used for title here, but kept for potential future use).
 */
@Composable
fun AuthOptionsScreen(navController: NavController, snackbarHostState: SnackbarHostState, flowType: String) {
    val scope = rememberCoroutineScope()
    // val titleText = if (flowType == "signup") "Choose Sign Up Method" else "Choose Log In Method" // Title now handled by SharedHeader

    ChatAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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

                // Back Button
                Button(
                    onClick = { navController.popBackStack("welcome", inclusive = false) },
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 16.dp, start = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back to Welcome")
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Back")
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
@Preview(showBackground = true, name = "Auth Options - Sign Up")
@Composable
fun AuthOptionsScreenSignUpPreview() {
    ChatAppTheme {
        AuthOptionsScreen(
            navController = rememberNavController(),
            snackbarHostState = SnackbarHostState(),
            flowType = "signup"
        )
    }
}

@Preview(showBackground = true, name = "Auth Options - Log In")
@Composable
fun AuthOptionsScreenLoginPreview() {
    ChatAppTheme {
        AuthOptionsScreen(
            navController = rememberNavController(),
            snackbarHostState = SnackbarHostState(),
            flowType = "login"
        )
    }
} 