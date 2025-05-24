package com.aarchangel.chatapp.ui.screens.auth

// ChatApp by aarchangel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aarchangel.chatapp.ui.components.AppButton
import com.aarchangel.chatapp.ui.components.AppTextField
import com.aarchangel.chatapp.ui.theme.ChatAppTheme
import com.aarchangel.chatapp.ui.theme.Dimens
import com.aarchangel.chatapp.viewmodel.EmailAuthViewModel

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tell Us More About You") },
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
                text = "Account Details for ${uiState.email}", // Display email for context
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = Dimens.PaddingSmall)
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

            Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

            AppButton(
                text = "Continue",
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