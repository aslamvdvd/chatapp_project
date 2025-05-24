package com.aarchangel.chatapp.ui.screens.auth

// ChatApp by aarchangel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aarchangel.chatapp.ui.components.AppButton
import com.aarchangel.chatapp.ui.components.AppTextField
import com.aarchangel.chatapp.ui.theme.ChatAppTheme
import com.aarchangel.chatapp.ui.theme.Dimens
import com.aarchangel.chatapp.viewmodel.LoginViewModel

/**
 * Screen for user login with email/username and password.
 * // ChatApp by aarchangel
 *
 * @param loginViewModel The ViewModel handling the login logic.
 * @param onNavigateBack Lambda to call when the back button is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by loginViewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log In") },
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
                .padding(paddingValues)
                .padding(Dimens.PaddingMedium),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome Back!",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = Dimens.PaddingLarge)
            )

            AppTextField(
                value = uiState.emailOrUsername,
                onValueChange = { loginViewModel.onEmailOrUsernameChanged(it) },
                label = "Email or Username",
                placeholder = "Enter your email or username",
                isError = uiState.loginError != null,
                // Error message will be displayed below based on uiState.loginError by AppTextField
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(Dimens.PaddingSmall)) // Reduced space

            AppTextField(
                value = uiState.password,
                onValueChange = { loginViewModel.onPasswordChanged(it) },
                label = "Password",
                placeholder = "Enter your password",
                isError = uiState.loginError != null,
                errorMessage = uiState.loginError, // Display general login error here or below button
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        loginViewModel.onLoginClicked()
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
                singleLine = true
            )

            val currentLoginError = uiState.loginError
            if (currentLoginError != null && !uiState.isLoading) {
                 Text(
                    text = currentLoginError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = Dimens.PaddingExtraSmall)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.PaddingLarge))

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                AppButton(
                    text = "Log In",
                    onClick = {
                        keyboardController?.hide()
                        loginViewModel.onLoginClicked()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "LoginScreen - Light")
@Composable
fun LoginScreenPreview() {
    ChatAppTheme {
        LoginScreen(onNavigateBack = {})
    }
}

@Preview(showBackground = true, name = "LoginScreen - Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoginScreenDarkPreview() {
    ChatAppTheme(darkTheme = true) {
        LoginScreen(onNavigateBack = {})
    }
}

@Preview(showBackground = true, name = "LoginScreen - Tablet", device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun LoginScreenTabletPreview() {
    ChatAppTheme {
        LoginScreen(onNavigateBack = {})
    }
} 