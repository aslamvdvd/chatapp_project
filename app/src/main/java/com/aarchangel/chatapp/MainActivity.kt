package com.aarchangel.chatapp

// ChatApp by aarchangel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.aarchangel.chatapp.ui.screens.AuthOptionsScreen
import com.aarchangel.chatapp.ui.screens.WelcomeScreen
import com.aarchangel.chatapp.ui.theme.ChatAppTheme
import com.aarchangel.chatapp.config.AppConfig
import androidx.compose.material3.ExperimentalMaterial3Api

// KDoc for MainActivity
/**
 * Main activity for the ChatApp application.
 * This activity serves as the entry point and hosts the Jetpack Compose UI navigation.
 * // ChatApp by aarchangel
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is first created. This is where you should do all of your normal
     * static set up: create views, bind data to lists, etc. This method also provides you with
     * a Bundle containing the activity's previously frozen state, if there was one.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     * shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     * Note: Otherwise it is null.
     */
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatAppTheme {
                ChatAppNavigation()
            }
        }
    }
}

// KDoc for ChatAppNavigation
/**
 * Composable function that sets up the navigation graph for the ChatApp.
 * It uses a NavHost to define navigation routes and associate them with composable screens.
 * // ChatApp by aarchangel
 */
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChatAppNavigation() {
    val navController: NavHostController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), // Apply padding from Scaffold
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated Title and Slogan
            AnimatedVisibility(
                visible = currentRoute == "welcome",
                enter = slideInVertically(initialOffsetY = { -it / 2 }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it / 2 }) + fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 32.dp, bottom = 16.dp) // Add some padding
                ) {
                    Text(
                        text = AppConfig.PLATFORM_NAME,
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Private. Secure. Modern.",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            NavHost(
                navController = navController,
                startDestination = "welcome",
                // Modifier.padding(innerPadding) removed as Column handles Scaffold padding
            ) {
                composable("welcome") {
                    // Welcome screen content will now be mostly buttons, title/slogan are above
                    WelcomeScreen(navController = navController, showTitleAndSlogan = false)
                }
                composable("auth_options") {
                    AuthOptionsScreen(navController = navController, snackbarHostState = snackbarHostState)
                }
                composable("email_auth") {
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("Email Authentication Screen (TODO)", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }
        }
    }
}

// KDoc for MainActivityPreview
/**
 * A preview composable for the MainActivity content (ChatAppNavigation).
 * This allows for quick visualization of the navigation setup in Android Studio's preview panel.
 * // ChatApp by aarchangel
 */
@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    ChatAppTheme {
        ChatAppNavigation()
    }
} 