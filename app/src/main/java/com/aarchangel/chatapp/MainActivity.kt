package com.aarchangel.chatapp

// ChatApp by aarchangel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aarchangel.chatapp.config.AppConfig
import com.aarchangel.chatapp.ui.screens.AuthOptionsScreen
import com.aarchangel.chatapp.ui.screens.WelcomeScreen
import com.aarchangel.chatapp.ui.theme.ChatAppTheme

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
    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatAppTheme {
                ChatAppNavigation()
            }
        }
    }
}

/**
 * Sets up the navigation graph and shared UI elements like the header.
 * // ChatApp by aarchangel
 */
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChatAppNavigation() {
    val navController: NavHostController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Animation states based on current route
    val isWelcomeScreen = currentRoute == "welcome"
    val isAuthOptionsScreen = currentRoute == "auth_options/{flowType}"

    val sloganFontSize by animateFloatAsState(
        targetValue = if (isWelcomeScreen) 18f else 14f, // sp values
        animationSpec = tween(durationMillis = 300),
        label = "Slogan Font Size"
    )
    val headerPaddingTop by animateDpAsState(
        targetValue = if (isWelcomeScreen) 64.dp else 48.dp, // Reduced for welcome
        animationSpec = tween(durationMillis = 300),
        label = "Header Padding Top"
    )
    val headerPaddingBottom by animateDpAsState(
        targetValue = if (isWelcomeScreen) 16.dp else 16.dp, // REDUCED welcome bottom padding from 32dp
        animationSpec = tween(durationMillis = 300), label = "headerPaddingBottom"
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Center the whole content block
        ) {
            // Persistent Header: Platform Name, Slogan, and Auth Options Title
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    // headerPaddingTop animates the top padding of this inner column
                    .padding(top = headerPaddingTop, start = 32.dp, end = 32.dp, bottom = headerPaddingBottom),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = AppConfig.PLATFORM_NAME,
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = AppConfig.PLATFORM_SLOGAN,
                    style = TextStyle( // Animate font size
                        fontSize = sloganFontSize.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f) // Slightly dimmer
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(
                    visible = isAuthOptionsScreen,
                    enter = fadeIn(animationSpec = tween(delayMillis = 150)) + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    val flowType = currentBackStackEntry?.arguments?.getString("flowType") ?: "login"
                    val authOptionsTitle = if (flowType == "signup") "Choose Sign Up Method" else "Choose Log In Method"
                    Text(
                        text = authOptionsTitle,
                        style = MaterialTheme.typography.titleMedium, // Bigger than small slogan
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Spacer(modifier = Modifier.weight(0.2f)) // Removed example spacer

            NavHost(
                navController = navController,
                startDestination = "welcome"
                // Modifier.weight(1f) removed - NavHost will wrap its content height
            ) {
                composable(
                    "welcome",
                    exitTransition = { fadeOut(animationSpec = tween(durationMillis = 200)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(durationMillis = 200, delayMillis = 200)) }
                ) {
                    WelcomeScreen(navController = navController)
                }
                composable(
                    route = "auth_options/{flowType}",
                    arguments = listOf(navArgument("flowType") { type = NavType.StringType }),
                    enterTransition = { fadeIn(animationSpec = tween(durationMillis = 200, delayMillis = 200)) },
                    popExitTransition = { fadeOut(animationSpec = tween(durationMillis = 200)) }
                ) { backStackEntry ->
                    val flowType = backStackEntry.arguments?.getString("flowType") ?: "login"
                    AuthOptionsScreen(
                        navController = navController,
                        snackbarHostState = snackbarHostState,
                        flowType = flowType
                    )
                }
                composable(
                    "email_auth",
                    enterTransition = { fadeIn(animationSpec = tween(durationMillis = 200, delayMillis = 200)) },
                    popExitTransition = { fadeOut(animationSpec = tween(durationMillis = 200)) }
                ) { // Placeholder
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

/**
 * A preview composable for the MainActivity content (ChatAppNavigation).
 * This allows for quick visualization of the navigation setup in Android Studio's preview panel.
 * // ChatApp by aarchangel
 */
@Preview(showBackground = true, name = "ChatApp - Welcome")
@Composable
fun ChatAppNavigationPreviewWelcome() {
    ChatAppTheme { ChatAppNavigation() }
}

// Add more specific previews if needed for different states of ChatAppNavigation 