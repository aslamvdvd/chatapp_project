package com.aarchangel.chatapp.ui.screens

// ChatApp by aarchangel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.aarchangel.chatapp.config.AppConfig
import com.aarchangel.chatapp.ui.theme.ChatAppTheme

/**
 * The Welcome Screen for the ChatApp.
 * Displays the platform name, a subtitle, and options to Sign Up or Log In.
 * If showTitleAndSlogan is false, it only shows the action buttons, assuming
 * the title and slogan are displayed by a parent composable (e.g., for animations).
 * // ChatApp by aarchangel
 *
 * @param navController The NavController used for navigating to other screens.
 * @param showTitleAndSlogan Whether to display the title and slogan within this screen.
 */
@Composable
fun WelcomeScreen(navController: NavController, showTitleAndSlogan: Boolean = true) {
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
                if (showTitleAndSlogan) {
                    Text(
                        text = AppConfig.PLATFORM_NAME,
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Private. Secure. Modern.",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }

                if (!showTitleAndSlogan) {
                    Spacer(modifier = Modifier.weight(1f))
                }

                Button(
                    onClick = { navController.navigate("auth_options") /* TODO: Navigate to SignUpScreen -> Now AuthOptions */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(text = "Sign Up")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate("auth_options") /* TODO: Navigate to LoginScreen -> Now AuthOptions */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(text = "Log In")
                }

                if (!showTitleAndSlogan) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Preview for the WelcomeScreen.
 * Provides a design-time view of the WelcomeScreen in Android Studio.
 * // ChatApp by aarchangel
 */
@Preview(showBackground = true, name = "Welcome Screen with Title")
@Composable
fun WelcomeScreenPreviewWithTitle() {
    ChatAppTheme {
        WelcomeScreen(navController = rememberNavController(), showTitleAndSlogan = true)
    }
}

@Preview(showBackground = true, name = "Welcome Screen (Buttons Only)")
@Composable
fun WelcomeScreenPreviewButtonsOnly() {
    ChatAppTheme {
        WelcomeScreen(navController = rememberNavController(), showTitleAndSlogan = false)
    }
} 