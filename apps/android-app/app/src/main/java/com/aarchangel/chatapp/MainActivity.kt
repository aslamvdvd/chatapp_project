package com.aarchangel.chatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aarchangel.chatapp.navigation.AppScreen
import com.aarchangel.chatapp.ui.screens.MainScreen
import com.aarchangel.chatapp.ui.screens.auth.AuthEntryScreen
import com.aarchangel.chatapp.ui.screens.auth.EmailLoginScreen
import com.aarchangel.chatapp.ui.screens.auth.EmailSignUpScreen
import com.aarchangel.chatapp.ui.screens.auth.LoginMethodScreen
import com.aarchangel.chatapp.ui.screens.auth.SignUpMethodScreen
import com.aarchangel.chatapp.ui.screens.search.SearchScreen
import com.aarchangel.chatapp.ui.screens.splash.SplashScreen
import com.aarchangel.chatapp.ui.screens.welcome.WelcomeScreen
import com.aarchangel.chatapp.ui.theme.ChatAppTheme
import com.aarchangel.chatapp.viewmodel.MainViewModel
import com.aarchangel.chatapp.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContainer = (application as ChatApplication).container
        setContent {
            ChatAppTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatAppNavigation(appContainer)
                }
            }
        }
    }
}

@Composable
fun ChatAppNavigation(appContainer: com.aarchangel.chatapp.di.AppContainer) {
    val navController = rememberNavController()
    val factory = ViewModelFactory(appContainer)
    val mainViewModel: MainViewModel = viewModel(factory = factory)

    NavHost(
        navController = navController,
        startDestination = AppScreen.Splash.route
    ) {
        composable(AppScreen.Splash.route) {
            SplashScreen(navController = navController, mainViewModel = mainViewModel)
        }
        composable(AppScreen.Welcome.route) {
            WelcomeScreen(
                onAgreeAndContinue = {
                    mainViewModel.onTermsAgreed()
                    navController.navigate(AppScreen.AuthEntry.route) {
                        popUpTo(AppScreen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }
        composable(AppScreen.AuthEntry.route) {
            AuthEntryScreen(
                onNavigateToSignUp = { navController.navigate(AppScreen.SignUpMethod.route) },
                onNavigateToLogin = { navController.navigate(AppScreen.LoginMethod.route) }
            )
        }
        composable(AppScreen.SignUpMethod.route) {
            SignUpMethodScreen(
                onContinueWithEmail = { navController.navigate(AppScreen.EmailSignUp.route) },
                onContinueWithPhone = {},
                onContinueWithGoogle = {},
                onContinueWithApple = {}
            )
        }
        composable(AppScreen.LoginMethod.route) {
            LoginMethodScreen(
                onContinueWithEmail = { navController.navigate(AppScreen.EmailLogin.route) },
                onContinueWithPhone = {},
                onContinueWithGoogle = {},
                onContinueWithApple = {}
            )
        }
        composable(AppScreen.EmailSignUp.route) {
            EmailSignUpScreen(
                onNavigateBack = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.navigate(AppScreen.EmailLogin.route) {
                        popUpTo(AppScreen.AuthEntry.route)
                    }
                }
            )
        }
        composable(AppScreen.EmailLogin.route) {
            EmailLoginScreen(
                onNavigateBack = { navController.popBackStack() },
                onLoginSuccess = { userProfile ->
                    mainViewModel.onLoginSuccess(userProfile)
                    navController.navigate(AppScreen.Home.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }
        composable(AppScreen.Home.route) {
            MainScreen(
                onNavigateToSearch = { navController.navigate(AppScreen.Search.route) },
                onLogoutClick = {
                    mainViewModel.onLogout()
                    navController.navigate(AppScreen.AuthEntry.route) {
                        popUpTo(AppScreen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(AppScreen.Search.route) {
            SearchScreen(viewModel = viewModel(factory = factory))
        }
    }
}