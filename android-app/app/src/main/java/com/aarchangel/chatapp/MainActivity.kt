package com.aarchangel.chatapp

// ChatApp by aarchangel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aarchangel.chatapp.config.AppConfig
import com.aarchangel.chatapp.navigation.AppScreen
import com.aarchangel.chatapp.ui.components.SharedAppHeader
import com.aarchangel.chatapp.ui.screens.AuthOptionsScreen
import com.aarchangel.chatapp.ui.screens.WelcomeScreen
import com.aarchangel.chatapp.ui.screens.auth.CreateAccountDetailsScreen
import com.aarchangel.chatapp.ui.screens.auth.LoginScreen
import com.aarchangel.chatapp.ui.theme.ChatAppTheme
import com.aarchangel.chatapp.ui.theme.Dimens
import com.aarchangel.chatapp.viewmodel.AuthOptionsViewModel
import com.aarchangel.chatapp.viewmodel.EmailAuthViewModel
import com.aarchangel.chatapp.viewmodel.LoginViewModel
import com.aarchangel.chatapp.viewmodel.WelcomeViewModel
import kotlinx.coroutines.launch

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
                ChatAppRoot()
            }
        }
    }
}

/**
 * Main navigation composable for the ChatApp.
 * Sets up the NavHost and defines all navigation routes and their corresponding screens.
 * Observes navigation events from ViewModels.
 * // ChatApp by aarchangel
 *
 * @param modifier Modifier for styling.
 * @param navController The NavHostController for managing navigation.
 * @param welcomeViewModel ViewModel for WelcomeScreen.
 * @param authOptionsViewModel ViewModel for AuthOptionsScreen.
 * @param emailAuthViewModel ViewModel for Email/Password authentication flow.
 * @param loginViewModel ViewModel for LoginScreen.
 * @param snackbarHostState Host state for showing Snackbars.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    welcomeViewModel: WelcomeViewModel,
    authOptionsViewModel: AuthOptionsViewModel,
    emailAuthViewModel: EmailAuthViewModel,
    loginViewModel: LoginViewModel,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()

    // Observe navigation events from WelcomeViewModel
    LaunchedEffect(welcomeViewModel.navigationEvent) {
        welcomeViewModel.navigationEvent.collect { route ->
            navController.navigate(route)
        }
    }

    // Observe navigation events from AuthOptionsViewModel
    LaunchedEffect(authOptionsViewModel.navigationEvent) {
        authOptionsViewModel.navigationEvent.collect { route ->
            navController.navigate(route)
        }
    }

    // Observe snackbar messages from AuthOptionsViewModel
    LaunchedEffect(authOptionsViewModel.snackbarMessage) {
        authOptionsViewModel.snackbarMessage.collect { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    // Observe navigation events from EmailAuthViewModel
    LaunchedEffect(emailAuthViewModel.navigationEvent) {
        emailAuthViewModel.navigationEvent.collect { route ->
            navController.navigate(route)
        }
    }

    // Observe snackbar messages from EmailAuthViewModel
    LaunchedEffect(emailAuthViewModel.snackbarMessage) {
        emailAuthViewModel.snackbarMessage.collect { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    // Observe navigation events from LoginViewModel
    LaunchedEffect(loginViewModel.navigationEvent) {
        loginViewModel.navigationEvent.collect { route ->
            navController.navigate(route)
        }
    }

    // Observe snackbar messages from LoginViewModel
    LaunchedEffect(loginViewModel.snackbarMessage) {
        loginViewModel.snackbarMessage.collect { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppScreen.Welcome.route,
        modifier = modifier
    ) {
        composable(route = AppScreen.Welcome.route) {
            WelcomeScreen(welcomeViewModel = welcomeViewModel)
        }

        composable(
            route = AppScreen.AuthOptions.route,
            arguments = listOf(navArgument("flowType") { type = NavType.StringType }),
            enterTransition = { fadeIn(animationSpec = tween(durationMillis = 200, delayMillis = 200)) },
            popExitTransition = { fadeOut(animationSpec = tween(durationMillis = 200)) }
        ) { backStackEntry ->
            val flowType = backStackEntry.arguments?.getString("flowType") ?: "login"
            AuthOptionsScreen(
                flowType = flowType,
                authOptionsViewModel = authOptionsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // EmailEntryScreen is no longer used in the primary signup flow.
        // composable(
        //     route = AppScreen.EmailEntry.route,
        //     arguments = listOf(navArgument("flowType") { type = NavType.StringType })
        // ) { backStackEntry ->
        //     EmailEntryScreen(
        //         emailAuthViewModel = emailAuthViewModel,
        //         onNavigateBack = { navController.popBackStack() }
        //     )
        // }

        // PasswordEntryScreen is no longer used in the primary signup flow.
        // It might be used for a "login with password" flow if LoginScreen is not comprehensive enough,
        // or for a "change password" feature later.
        // composable(
        //     route = AppScreen.PasswordEntry.route,
        //     arguments = listOf(
        //         navArgument("flowType") { type = NavType.StringType },
        //         navArgument("email") { type = NavType.StringType }
        //     )
        // ) { backStackEntry ->
        //     PasswordEntryScreen(
        //         emailAuthViewModel = emailAuthViewModel,
        //         onNavigateBack = { navController.popBackStack() }
        //     )
        // }

        composable(
            route = AppScreen.CreateAccountDetails.route,
            arguments = listOf(
                navArgument("flowType") { type = NavType.StringType } // Only flowType now
            )
        ) { backStackEntry ->
            CreateAccountDetailsScreen(
                emailAuthViewModel = emailAuthViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Placeholder for Email Auth Screen (Main/Dashboard after login)
        composable(route = AppScreen.EmailAuth.route) { // This was a placeholder, might need to be re-evaluated or removed
            // This route name "email_auth" is a bit confusing now.
            // It was originally for a generic email auth screen.
            // For now, let's assume it was a placeholder for a screen after successful email/password auth.
            // We should define a proper HomeScreen/DashboardScreen route later.
        }

        composable(route = AppScreen.Login.route) {
            LoginScreen(
                loginViewModel = loginViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * Root composable for the ChatApp.
 * Sets up the theme, NavController, ViewModels, and Scaffold structure.
 * // ChatApp by aarchangel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAppRoot() {
    ChatAppTheme {
        val navController = rememberNavController()
        val welcomeViewModel: WelcomeViewModel = viewModel()
        val authOptionsViewModel: AuthOptionsViewModel = viewModel()
        val emailAuthViewModel: EmailAuthViewModel = viewModel()
        val loginViewModel: LoginViewModel = viewModel()
        val snackbarHostState = remember { SnackbarHostState() }
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                // Show SharedAppHeader only on WelcomeScreen
                if (currentRoute == AppScreen.Welcome.route) {
                    SharedAppHeader(isPrimaryScreen = true)
                }
                // Other screens (EmailEntry, PasswordEntry, AuthOptions) manage their own TopAppBars or don't have one.
                // Future screens post-auth might use SharedAppHeader(isPrimaryScreen = false)
            }
        ) {
            ChatAppNavigation(
                modifier = Modifier.padding(it),
                navController = navController,
                welcomeViewModel = welcomeViewModel,
                authOptionsViewModel = authOptionsViewModel,
                emailAuthViewModel = emailAuthViewModel,
                loginViewModel = loginViewModel,
                snackbarHostState = snackbarHostState
            )
        }
    }
}

/**
 * A preview composable for the MainActivity content (ChatAppNavigation).
 * This preview now calls ChatAppRoot to reflect the full app structure for previews.
 * // ChatApp by aarchangel
 */
@Preview(showBackground = true, name = "ChatApp - Root (Welcome)")
@Composable
fun ChatAppRootPreviewWelcome() { // Renamed and updated to call ChatAppRoot
    ChatAppTheme { ChatAppRoot() }
}

@Preview(showBackground = true, name = "ChatApp - Root (Welcome) - Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ChatAppRootPreviewWelcomeDark() { // Added dark mode preview for root
    ChatAppTheme(darkTheme = true) { ChatAppRoot() }
}

@Preview(showBackground = true, name = "ChatApp - Root (Welcome) - Tablet", device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun ChatAppRootPreviewWelcomeTablet() { // Renamed and updated to call ChatAppRoot
    ChatAppTheme { ChatAppRoot() }
}

// It's generally better to preview individual screens or ChatAppRoot.
// Previewing ChatAppNavigation directly can be complex due to ViewModel dependencies that ChatAppRoot handles.
// Removed ChatAppNavigationPreviewAuthOptions if it existed, as ChatAppRoot previews are more comprehensive. 