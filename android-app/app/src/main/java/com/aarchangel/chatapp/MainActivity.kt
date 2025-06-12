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
import com.aarchangel.chatapp.network.AuthService
import com.aarchangel.chatapp.network.AuthServiceImpl
import com.aarchangel.chatapp.ui.screens.AuthEntryScreen
import com.aarchangel.chatapp.ui.screens.EmailLoginScreen
import com.aarchangel.chatapp.ui.screens.EmailSignUpScreen
import com.aarchangel.chatapp.ui.screens.HomeScreen
import com.aarchangel.chatapp.ui.screens.LoginMethodScreen
import com.aarchangel.chatapp.ui.screens.SignUpMethodScreen
import com.aarchangel.chatapp.ui.screens.SplashScreen
import com.aarchangel.chatapp.ui.screens.WelcomeScreen
import com.aarchangel.chatapp.ui.theme.ChatAppTheme
import com.aarchangel.chatapp.viewmodel.MainViewModel
import kotlinx.coroutines.launch

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
            EmailSignUpScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(AppScreen.EmailLogin.route) {
            EmailLoginScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(AppScreen.Home.route) {
            HomeScreen(mainViewModel = mainViewModel)
        }
    }
}