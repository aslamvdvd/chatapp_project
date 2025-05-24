package com.aarchangel.chatapp.ui.screens.auth

// ChatApp by aarchangel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.aarchangel.chatapp.ui.components.AppButton
import com.aarchangel.chatapp.ui.components.AppTextField
import com.aarchangel.chatapp.ui.theme.ChatAppTheme
import com.aarchangel.chatapp.ui.theme.Dimens
import com.aarchangel.chatapp.viewmodel.EmailAuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel // For default viewModel instance

/**
 * Screen for entering email during login or signup flows.
 * // ChatApp by aarchangel
 *
 * @param emailAuthViewModel The ViewModel handling the authentication logic.
 * @param onNavigateBack Lambda to call when the back button is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailEntryScreen(
    emailAuthViewModel: EmailAuthViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by emailAuthViewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.flowType == "signup") "Create Account" else "Log In") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
                .padding(Dimens.PaddingMedium), // Additional screen padding
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "What's your email?",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = Dimens.PaddingLarge)
            )

            AppTextField(
                value = uiState.email,
                onValueChange = { emailAuthViewModel.onEmailChanged(it) },
                label = "Email",
                placeholder = "Enter your email address",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        emailAuthViewModel.onEmailContinue()
                    }
                ),
                isError = uiState.emailError != null,
                singleLine = true
            )
            if (uiState.emailError != null) {
                Text(
                    text = uiState.emailError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = Dimens.PaddingSmall, top = Dimens.PaddingExtraSmall)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.PaddingLarge))

            AppButton(
                text = "Continue",
                onClick = {
                    keyboardController?.hide()
                    emailAuthViewModel.onEmailContinue()
                },
                enabled = !uiState.isLoading
            )
        }
    }
}

@Preview(showBackground = true, name = "EmailEntryScreen - Login")
@Composable
fun EmailEntryScreenLoginPreview() {
    ChatAppTheme {
        // This preview won't have ViewModel interaction fully, 
        // as it's hard to provide SavedStateHandle here directly for flowType.
        // For full preview, consider a wrapper that provides a mock ViewModel or a test NavHost.
        EmailEntryScreen(onNavigateBack = {})
    }
}

@Preview(showBackground = true, name = "EmailEntryScreen - Signup - Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun EmailEntryScreenSignupDarkPreview() {
    ChatAppTheme(darkTheme = true) {
        EmailEntryScreen(onNavigateBack = {})
    }
}

@Preview(showBackground = true, name = "EmailEntryScreen - Tablet", device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun EmailEntryScreenTabletPreview() {
    ChatAppTheme {
        EmailEntryScreen(onNavigateBack = {})
    }
}