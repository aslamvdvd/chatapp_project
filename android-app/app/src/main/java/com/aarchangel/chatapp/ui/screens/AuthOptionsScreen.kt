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
import com.aarchangel.chatapp.ui.theme.Dimens
import com.aarchangel.chatapp.viewmodel.AuthOptionsViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aarchangel.chatapp.ui.components.AppButton

/**
 * Authentication Options Screen for ChatApp.
 * Provides various methods for users to sign up or log in.
 * // ChatApp by aarchangel
 *
 * @param flowType A string indicating if the flow is for "signup" or "login".
 * @param authOptionsViewModel The ViewModel for this screen.
 * @param onNavigateBack Lambda to call when the back button is pressed.
 */
@Composable
fun AuthOptionsScreen(
    flowType: String,
    authOptionsViewModel: AuthOptionsViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    ChatAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(Dimens.PaddingMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.PaddingLarge),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // GhostTalk logo and slogan can remain if desired, or be removed if this screen is purely for *alternative* auth methods
                // For now, let's keep them for consistency if user lands here from a different path in future.

                Spacer(modifier = Modifier.height(Dimens.PaddingExtraLarge))

                AppButton(
                    text = "Continue with Email",
                    onClick = { authOptionsViewModel.onContinueWithEmailClicked(flowType) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(Dimens.PaddingLarge))

                AppButton(
                    text = "Continue with Google",
                    onClick = { authOptionsViewModel.onContinueWithGoogleClicked() },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
                AppButton(
                    text = "Sign in with Apple",
                    onClick = { authOptionsViewModel.onSignInWithAppleClicked() },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
                AppButton(
                    text = "Use Phone Number",
                    onClick = { authOptionsViewModel.onUsePhoneNumberClicked() },
                    modifier = Modifier.fillMaxWidth()
                )

                // Back Button - uses standard Button as it has custom alignment and icon
                Button(
                    onClick = {
                        authOptionsViewModel.onBackClicked()
                        onNavigateBack()
                    },
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = Dimens.PaddingMedium, bottom = Dimens.PaddingMedium, start = Dimens.PaddingMedium), // Adjusted padding
                    shape = RoundedCornerShape(Dimens.RoundedCornerMedium),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary) // Different style for back
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back to Welcome", tint = MaterialTheme.colorScheme.onSecondary)
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Back", color = MaterialTheme.colorScheme.onSecondary)
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
@Preview(showBackground = true, name = "Auth Options - Default")
@Composable
fun AuthOptionsScreenPreview() {
    ChatAppTheme {
        AuthOptionsScreen(
            flowType = "signup",
            authOptionsViewModel = AuthOptionsViewModel(),
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Auth Options - Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AuthOptionsScreenDarkPreview() {
    ChatAppTheme {
        AuthOptionsScreen(
            flowType = "login",
            authOptionsViewModel = AuthOptionsViewModel(),
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Auth Options - Tablet", device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun AuthOptionsScreenTabletPreview() {
    ChatAppTheme {
        AuthOptionsScreen(
            flowType = "signup",
            authOptionsViewModel = AuthOptionsViewModel(),
            onNavigateBack = {}
        )
    }
} 