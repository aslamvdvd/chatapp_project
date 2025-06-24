package com.aarchangel.chatapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aarchangel.chatapp.data.AuthRepository
import com.aarchangel.chatapp.data.TokenStorage
import com.aarchangel.chatapp.data.local.PreferenceManager
import com.aarchangel.chatapp.navigation.AppScreen
import com.aarchangel.chatapp.data.network.AuthService
import com.aarchangel.chatapp.data.network.AuthServiceImpl
import com.aarchangel.chatapp.ui.screens.MainScreen
import com.aarchangel.chatapp.ui.screens.auth.AuthEntryScreen
import com.aarchangel.chatapp.ui.screens.auth.EmailLoginScreen
import com.aarchangel.chatapp.ui.screens.auth.EmailSignUpScreen
import com.aarchangel.chatapp.ui.screens.auth.LoginMethodScreen
import com.aarchangel.chatapp.ui.screens.auth.SignUpMethodScreen
import com.aarchangel.chatapp.ui.screens.home.HomeScreen
import com.aarchangel.chatapp.ui.screens.search.SearchScreen
import com.aarchangel.chatapp.ui.screens.splash.SplashScreen
import com.aarchangel.chatapp.ui.screens.welcome.WelcomeScreen
import com.aarchangel.chatapp.ui.theme.ChatAppTheme
import com.aarchangel.chatapp.viewmodel.MainViewModel
import com.aarchangel.chatapp.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aarchangel.chatapp.ui.screens.search.SearchViewModel

class MainActivity : ComponentActivity() {
    private val authService: AuthService by lazy { AuthServiceImpl() }
    private val tokenStorage by lazy { TokenStorage(applicationContext) }
    private val authRepository by lazy { AuthRepository(authService, tokenStorage) }
    private val preferenceManager by lazy { PreferenceManager(applicationContext) }

    private val mainViewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return MainViewModel(authService, authRepository, preferenceManager) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatAppTheme(darkTheme = true) { // Enforcing dark theme for now
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatAppNavigation(mainViewModel = mainViewModel)
                }
            }
        }
    }
}

@Composable
fun ChatAppNavigation(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()

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
                    coroutineScope.launch {
                        mainViewModel.onTermsAgreed()
                    }
                    navController.navigate(AppScreen.AuthEntry.route) {
                        popUpTo(AppScreen.Welcome.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(AppScreen.AuthEntry.route) {
            AuthEntryScreen(
                onNavigateToSignUp = {
                    navController.navigate(AppScreen.SignUpMethod.route)
                },
                onNavigateToLogin = {
                    navController.navigate(AppScreen.LoginMethod.route)
                }
            )
        }
        composable(AppScreen.SignUpMethod.route) {
            SignUpMethodScreen(
                onContinueWithPhone = { Log.d("SignUpMethodScreen", "Continue with Phone clicked") },
                onContinueWithEmail = { navController.navigate(AppScreen.EmailSignUp.route) },
                onContinueWithGoogle = { Log.d("SignUpMethodScreen", "Continue with Google clicked") },
                onContinueWithApple = { Log.d("SignUpMethodScreen", "Continue with Apple clicked") }
            )
        }
        composable(AppScreen.LoginMethod.route) {
            LoginMethodScreen(
                onContinueWithPhone = { Log.d("LoginMethodScreen", "Continue with Phone clicked") },
                onContinueWithEmail = { navController.navigate(AppScreen.EmailLogin.route) },
                onContinueWithGoogle = { Log.d("LoginMethodScreen", "Continue with Google clicked") },
                onContinueWithApple = { Log.d("LoginMethodScreen", "Continue with Apple clicked") }
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
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(AppScreen.Home.route) {
            MainScreen(
                onNavigateToSearch = { navController.navigate(AppScreen.Search.route) }
            )
        }
        composable(AppScreen.Search.route) {
            val factory = ViewModelFactory(LocalContext.current)
            val searchViewModel: SearchViewModel = viewModel(factory = factory)
            SearchScreen(searchViewModel = searchViewModel)
        }
    }
}