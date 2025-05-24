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
import com.aarchangel.chatapp.ui.theme.ChatAppTheme

/**
 * The Welcome Screen for the ChatApp.
 * Displays options to Sign Up or Log In.
 * The main platform title and slogan are handled by a shared header component.
 * // ChatApp by aarchangel
 *
 * @param navController The NavController used for navigating to other screens.
 */
@Composable
fun WelcomeScreen(navController: NavController) {
    ChatAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { navController.navigate("auth_options/signup") },
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
                    onClick = { navController.navigate("auth_options/login") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(text = "Log In")
                }
            }
        }
    }
}

/**
 * Preview for the WelcomeScreen (now buttons only).
 * Provides a design-time view of the WelcomeScreen in Android Studio.
 * // ChatApp by aarchangel
 */
@Preview(showBackground = true, name = "Welcome Screen Buttons")
@Composable
fun WelcomeScreenPreview() {
    ChatAppTheme {
        WelcomeScreen(navController = rememberNavController())
    }
} 