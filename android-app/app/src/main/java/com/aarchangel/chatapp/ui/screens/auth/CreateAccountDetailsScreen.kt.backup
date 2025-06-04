package com.aarchangel.chatapp.ui.screens.auth

// ChatApp by aarchangel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aarchangel.chatapp.ui.components.AppButton
import com.aarchangel.chatapp.ui.components.AppTextField
import com.aarchangel.chatapp.ui.theme.ChatAppTheme
import com.aarchangel.chatapp.ui.theme.Dimens
import com.aarchangel.chatapp.viewmodel.EmailAuthViewModel
import android.util.Log

/**
 * Screen for entering additional user details during the signup flow.
 * // ChatApp by aarchangel
 *
 * @param emailAuthViewModel The ViewModel handling the authentication logic.
 * @param onNavigateBack Lambda to call when the back button is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountDetailsScreen(
    emailAuthViewModel: EmailAuthViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by emailAuthViewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Account") },
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
                .padding(Dimens.PaddingMedium)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
        ) {
            Text(
                text = "Account Details",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = Dimens.PaddingSmall)
            )


            AppTextField(
                value = uiState.email,
                onValueChange = { emailAuthViewModel.onEmailChanged(it) },
                label = "Email",
                placeholder = "Enter your email address",
                isError = uiState.emailError != null,
                errorMessage = uiState.emailError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )

           

            // Username
            AppTextField(
                value = uiState.username,
                onValueChange = { emailAuthViewModel.onUsernameChanged(it) },
                label = "Username",
                placeholder = "Choose a unique username",
                isError = uiState.usernameError != null,
                errorMessage = uiState.usernameError,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true
            )

            // First Name
            AppTextField(
                value = uiState.firstName,
                onValueChange = { emailAuthViewModel.onFirstNameChanged(it) },
                label = "First Name",
                placeholder = "Enter your first name",
                isError = uiState.firstNameError != null,
                errorMessage = uiState.firstNameError,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                singleLine = true
            )

            // Middle Name (Optional)
            AppTextField(
                value = uiState.middleName,
                onValueChange = { emailAuthViewModel.onMiddleNameChanged(it) },
                label = "Middle Name (Optional)",
                placeholder = "Enter your middle name",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                singleLine = true
            )

            // Last Name
            AppTextField(
                value = uiState.lastName,
                onValueChange = { emailAuthViewModel.onLastNameChanged(it) },
                label = "Last Name",
                placeholder = "Enter your last name",
                isError = uiState.lastNameError != null,
                errorMessage = uiState.lastNameError,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                singleLine = true
            )

            // Date of Birth
            AppTextField(
                value = uiState.dateOfBirth,
                onValueChange = { emailAuthViewModel.onDateOfBirthChanged(it) },
                label = "Date of Birth",
                placeholder = "YYYY-MM-DD", // Add a DatePicker later for better UX
                isError = uiState.dateOfBirthError != null,
                errorMessage = uiState.dateOfBirthError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                singleLine = true
            )

            // Gender
            AppTextField(
                value = uiState.gender,
                onValueChange = { emailAuthViewModel.onGenderChanged(it) },
                label = "Gender",
                placeholder = "e.g., Male, Female, Other", // Consider RadioButtons or Dropdown later
                isError = uiState.genderError != null,
                errorMessage = uiState.genderError,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
                singleLine = true
            )

            // Password
            AppTextField(
                value = uiState.password,
                onValueChange = { emailAuthViewModel.onPasswordChanged(it) },
                label = "Password",
                placeholder = "Enter your password",
                isError = uiState.passwordError != null,
                errorMessage = uiState.passwordError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
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

            // Confirm Password
            AppTextField(
                value = uiState.confirmPassword,
                onValueChange = { emailAuthViewModel.onConfirmPasswordChanged(it) },
                label = "Confirm Password",
                placeholder = "Re-enter your password",
                isError = uiState.confirmPasswordError != null,
                errorMessage = uiState.confirmPasswordError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        emailAuthViewModel.onCreateAccountDetailsContinue() // Existing action
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
                singleLine = true
            )

            Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

            AppButton(
                text = "Sign Up",
                onClick = {
                    keyboardController?.hide()
                    emailAuthViewModel.onCreateAccountDetailsContinue()
                },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, name = "CreateAccountDetailsScreen - Light")
@Composable
fun CreateAccountDetailsScreenPreview() {
    ChatAppTheme {
        CreateAccountDetailsScreen(onNavigateBack = {})
    }
}

@Preview(showBackground = true, name = "CreateAccountDetailsScreen - Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CreateAccountDetailsScreenDarkPreview() {
    ChatAppTheme(darkTheme = true) {
        CreateAccountDetailsScreen(onNavigateBack = {})
    }
}

@Preview(showBackground = true, name = "CreateAccountDetailsScreen - Tablet", device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun CreateAccountDetailsScreenTabletPreview() {
    ChatAppTheme {
        CreateAccountDetailsScreen(onNavigateBack = {})
    }
} 