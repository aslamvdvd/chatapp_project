package com.aarchangel.chatapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aarchangel.chatapp.config.AppConfig
import com.aarchangel.chatapp.dto.LoginRequest
import com.aarchangel.chatapp.dto.UserProfileDto
import com.aarchangel.chatapp.ui.theme.ChatAppTheme
import com.aarchangel.chatapp.ui.theme.Dimens
import com.aarchangel.chatapp.viewmodel.LoginViewModel
import com.aarchangel.chatapp.viewmodel.ViewModelFactory

@Composable
fun EmailLoginScreen(
    onNavigateBack: () -> Unit,
    onLoginSuccess: (UserProfileDto) -> Unit
) {
    val factory = ViewModelFactory(LocalContext.current)
    val viewModel: LoginViewModel = viewModel(factory = factory)
    val loginState by viewModel.loginState.collectAsState()
    val focusManager = LocalFocusManager.current

    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.loginEvent.collect { userProfile ->
            onLoginSuccess(userProfile)
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.PaddingExtraLarge),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = AppConfig.PLATFORM_NAME,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = AppConfig.PLATFORM_SLOGAN,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = Dimens.PaddingHuge)
            )

            OutlinedTextField(
                value = emailOrUsername,
                onValueChange = { emailOrUsername = it },
                label = { Text("Email or Username") },
                isError = loginState.fieldErrors?.containsKey("email_or_username") == true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                singleLine = true
            )
            
            loginState.fieldErrors?.get("email_or_username")?.let {
                Text(it.joinToString(), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            
            Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                isError = loginState.fieldErrors?.containsKey("password") == true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.login(
                            LoginRequest(
                                emailOrUsername = emailOrUsername,
                                password = password
                            )
                        )
                    }
                ),
                singleLine = true,
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                    }
                }
            )
            
            loginState.fieldErrors?.get("password")?.let {
                Text(it.joinToString(), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(Dimens.PaddingLarge))

            Button(
                onClick = {
                    viewModel.login(
                        LoginRequest(
                            emailOrUsername = emailOrUsername,
                            password = password
                        )
                    )
                },
                enabled = !loginState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (loginState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Login")
                }
            }

            loginState.error?.let {
                if (loginState.fieldErrors == null) {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(top = Dimens.PaddingSmall))
                }
            }

            TextButton(onClick = onNavigateBack) {
                Text("Back")
            }
        }
    }
}

@Preview(showBackground = true, name = "Email Login Screen")
@Composable
fun EmailLoginScreenPreview() {
    ChatAppTheme(darkTheme = true) {
        EmailLoginScreen(onNavigateBack = {}, onLoginSuccess = { /* Preview doesn't handle this */ })
    }
} 