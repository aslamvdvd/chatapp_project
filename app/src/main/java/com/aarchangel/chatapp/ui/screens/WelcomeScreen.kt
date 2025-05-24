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
import com.aarchangel.chatapp.ui.theme.Dimens
import com.aarchangel.chatapp.viewmodel.WelcomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aarchangel.chatapp.ui.components.AppButton

/**
 * The Welcome Screen for the ChatApp.
 * Displays options to Sign Up or Log In.
 * The main platform title and slogan are handled by a shared header component.
 * // ChatApp by aarchangel
 *
 * @param navController The NavController used for navigating to other screens.
 * @param welcomeViewModel The ViewModel for this screen.
 */
@Composable
fun WelcomeScreen(
    // navController: NavController, // Navigation is now handled by ViewModel
    welcomeViewModel: WelcomeViewModel = viewModel()
) {
    ChatAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .padding(Dimens.PaddingMedium),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppButton(
                    text = "Sign Up",
                    onClick = { welcomeViewModel.onSignUpClicked() }
                    // Modifier and other properties will use AppButton defaults
                )
                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
                AppButton(
                    text = "Log In",
                    onClick = { welcomeViewModel.onLoginClicked() }
                )
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
        WelcomeScreen(welcomeViewModel = WelcomeViewModel())
    }
}

@Preview(showBackground = true, name = "Welcome Screen Buttons - Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun WelcomeScreenPreviewDark() {
    ChatAppTheme {
        WelcomeScreen(welcomeViewModel = WelcomeViewModel())
    }
} 