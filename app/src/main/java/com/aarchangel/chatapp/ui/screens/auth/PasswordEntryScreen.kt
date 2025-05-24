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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.aarchangel.chatapp.ui.components.AppButton
import com.aarchangel.chatapp.ui.components.AppTextField
import com.aarchangel.chatapp.ui.theme.ChatAppTheme
import com.aarchangel.chatapp.ui.theme.Dimens
import com.aarchangel.chatapp.viewmodel.EmailAuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Screen for entering password (and confirming password for signup) during authentication.
 * // ChatApp by aarchangel
 *
 * @param emailAuthViewModel The ViewModel handling the authentication logic.
 * @param onNavigateBack Lambda to call when the back button is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordEntryScreen(
    emailAuthViewModel: EmailAuthViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by emailAuthViewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.flowType == "signup") "Create Password" else "Enter Password") },
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
                text = if (uiState.flowType == "signup") "Set up your password" else "Welcome back, ${uiState.email}!",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = Dimens.PaddingMedium)
            )
            Text(
                text = if (uiState.flowType == "signup") "Create a strong password for your account." else "Enter your password to continue.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = Dimens.PaddingLarge)
            )

            AppTextField(
                value = uiState.password,
                onValueChange = { emailAuthViewModel.onPasswordChanged(it) },
                label = "Password",
                placeholder = "Enter your password",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = if (uiState.flowType == "signup") ImeAction.Next else ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (uiState.flowType != "signup") {
                            keyboardController?.hide()
                            emailAuthViewModel.onSubmitCredentials()
                        }
                    }
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    val description = if (passwordVisible) "Hide password" else "Show password"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                },
                isError = uiState.passwordError != null,
                singleLine = true
            )
            if (uiState.passwordError != null) {
                Text(
                    text = uiState.passwordError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = Dimens.PaddingSmall, top = Dimens.PaddingExtraSmall)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

            if (uiState.flowType == "signup") {
                AppTextField(
                    value = uiState.confirmPassword,
                    onValueChange = { emailAuthViewModel.onConfirmPasswordChanged(it) },
                    label = "Confirm Password",
                    placeholder = "Re-enter your password",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            emailAuthViewModel.onSubmitCredentials()
                        }
                    ),
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (confirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                        val description = if (confirmPasswordVisible) "Hide password" else "Show password"
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(imageVector = image, description)
                        }
                    },
                    isError = uiState.confirmPasswordError != null,
                    singleLine = true
                )
                if (uiState.confirmPasswordError != null) {
                    Text(
                        text = uiState.confirmPasswordError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = Dimens.PaddingSmall, top = Dimens.PaddingExtraSmall)
                    )
                }
                Spacer(modifier = Modifier.height(Dimens.PaddingLarge))
            } else {
                 Spacer(modifier = Modifier.height(Dimens.PaddingSmall)) // Maintain some space before button for login
            }

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                AppButton(
                    text = if (uiState.flowType == "signup") "Create Account" else "Log In",
                    onClick = {
                        keyboardController?.hide()
                        emailAuthViewModel.onSubmitCredentials()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "PasswordEntryScreen - Login")
@Composable
fun PasswordEntryScreenLoginPreview() {
    ChatAppTheme {
        PasswordEntryScreen(onNavigateBack = {})
    }
}

@Preview(showBackground = true, name = "PasswordEntryScreen - Signup - Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PasswordEntryScreenSignupDarkPreview() {
    ChatAppTheme(darkTheme = true) {
        // Ideally, you'd mock the ViewModel to show the signup state for this preview.
        // For now, it will default to login or use a default EmailAuthViewModel state.
        PasswordEntryScreen(onNavigateBack = {})
    }
}

@Preview(showBackground = true, name = "PasswordEntryScreen - Tablet", device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun PasswordEntryScreenTabletPreview() {
    ChatAppTheme {
        PasswordEntryScreen(onNavigateBack = {})
    }
}