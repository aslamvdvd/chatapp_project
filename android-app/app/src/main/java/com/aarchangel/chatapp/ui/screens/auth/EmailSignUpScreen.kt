package com.aarchangel.chatapp.ui.screens.auth

// ChatApp by aarchangel

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.aarchangel.chatapp.ui.components.AppButton
import com.aarchangel.chatapp.ui.components.AppTextField
import com.aarchangel.chatapp.ui.theme.ChatAppTheme
import com.aarchangel.chatapp.ui.theme.Dimens
import com.aarchangel.chatapp.viewmodel.EmailAuthViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Consolidated screen for new user email signup.
 * Collects email, password, and all other user details.
 * // ChatApp by aarchangel
 *
 * @param emailAuthViewModel The ViewModel handling the authentication logic.
 * @param onNavigateBack Lambda to call when the back button is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailSignUpScreen( // Renamed from CreateAccountDetailsScreen
    emailAuthViewModel: EmailAuthViewModel, // Removed default viewModel() to use passed instance
    onNavigateBack: () -> Unit,
    flowType: String // Added flowType argument from NavHost
) {
    val uiState by emailAuthViewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Date picker state
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) }

    // Gender selection state
    var showGenderDropdown by remember { mutableStateOf(false) }
    val genderOptions = listOf("Male", "Female", "Non-binary", "Prefer not to say")

    var passwordFocused by remember { mutableStateOf(false) }

    // Initialize ViewModel state based on navigation arguments
    // This is important if flowType or initial email needs to be set from nav args.
    // EmailAuthViewModel's init block already handles flowType from SavedStateHandle,
    // and can also handle email if it's made part of the SavedStateHandle arguments there.
    // For now, explicitly ensure flowType is correct if it was passed and could differ.
    LaunchedEffect(flowType) {
        if (uiState.flowType != flowType) {
            // This might be redundant if ViewModel's init already correctly sets it from SavedStateHandle
            // based on how NavHost arguments are propagated to ViewModel's SavedStateHandle.
            // For safety, or if direct manipulation is needed:
            // emailAuthViewModel.setFlowType(flowType) // Example: if a setter method existed
        }
        // If email needs to be pre-filled from a nav arg specifically for this screen:
        // val initialEmail = savedStateHandle.get<String>("email") // Example access
        // if (initialEmail != null && uiState.email.isEmpty()) {
        //     emailAuthViewModel.onEmailChanged(initialEmail)
        // }
    }

    BackHandler {
        emailAuthViewModel.clearSignUpForm()
        onNavigateBack()
    }

    if (showDatePicker) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis(), // Or load from viewmodel if already set
            yearRange = (year - 100)..(year) // Example: allow selection from 100 years ago up to current year
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    datePickerState.selectedDateMillis?.let { millis ->
                        calendar.timeInMillis = millis
                        emailAuthViewModel.onDateOfBirthChanged(dateFormatter.format(calendar.time))
                    }
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Your Account") }, // Updated title
                navigationIcon = {
                    IconButton(onClick = {
                        emailAuthViewModel.clearSignUpForm() // Clear form on back navigation
                        onNavigateBack()
                    }) {
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
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall) // Reduced spacing
        ) {
            Text(
                text = "Enter your details to sign up", // Updated text
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

            AppTextField(
                value = uiState.middleName,
                onValueChange = { emailAuthViewModel.onMiddleNameChanged(it) },
                label = "Middle Name (Optional)",
                placeholder = "Enter your middle name",
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                singleLine = true
            )

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
            Box {
                AppTextField(
                    value = uiState.dateOfBirth, // Display formatted date
                    onValueChange = { emailAuthViewModel.onDateOfBirthManuallyChanged(it) }, // Handle manual input
                    label = "Date of Birth",
                    placeholder = "DD-MM-YYYY",
                    isError = uiState.dateOfBirthError != null,
                    errorMessage = uiState.dateOfBirthError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    singleLine = true,
                    readOnly = false, // Allow manual editing
                    visualTransformation = DateVisualTransformation(), // Apply custom formatting
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Filled.CalendarToday, contentDescription = "Select Date")
                        }
                    }
                )
                // Overlay a clickable Box to ensure the entire field area triggers the picker if desired,
                // but AppTextField's trailingIcon is usually sufficient.
                // For this iteration, trailingIcon is likely enough.
            }

            // Gender Field with Dropdown
            ExposedDropdownMenuBox(
                expanded = showGenderDropdown,
                onExpandedChange = { showGenderDropdown = !showGenderDropdown }
            ) {
                AppTextField(
                    modifier = Modifier.menuAnchor(), // Important for positioning the dropdown
                    value = uiState.gender,
                    onValueChange = {}, // Not directly editable
                    label = "Gender",
                    placeholder = "Select your gender",
                    isError = uiState.genderError != null,
                    errorMessage = uiState.genderError,
                    readOnly = true, // Make TextField read-only
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showGenderDropdown)
                    }
                )
                ExposedDropdownMenu(
                    expanded = showGenderDropdown,
                    onDismissRequest = { showGenderDropdown = false }
                ) {
                    genderOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                emailAuthViewModel.onGenderChanged(selectionOption)
                                showGenderDropdown = false
                            }
                        )
                    }
                }
            }

            AppTextField(
                modifier = Modifier.onFocusChanged { focusState ->
                    passwordFocused = focusState.isFocused
                },
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

            if (passwordFocused && uiState.passwordError == null) {
                Text(
                    text = "Min. 8 characters, with letters, numbers, & special characters.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = Dimens.PaddingSmall, end = Dimens.PaddingSmall, top = Dimens.PaddingExtraSmall)
                )
            } else if (uiState.passwordError != null) {
                // The error message is already handled by AppTextField's errorMessage parameter
                // This space is intentionally left, or you could add more specific guidance if needed.
            }

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
                        emailAuthViewModel.onSignUpAttempt()
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
                    emailAuthViewModel.onSignUpAttempt()
                },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(Dimens.PaddingSmall))
                CircularProgressIndicator()
            }
        }
    }
}

@Preview(showBackground = true, name = "EmailSignUpScreen - Light") // Renamed Preview
@Composable
fun EmailSignUpScreenPreview() { // Renamed Preview function
    ChatAppTheme {
        // Preview requires a ViewModel; for simplicity, using a default one.
        // In a real app, consider a mock ViewModel or ensure the default provides enough for preview.
        EmailSignUpScreen(emailAuthViewModel = EmailAuthViewModel(SavedStateHandle()), onNavigateBack = {}, flowType = "signup")
    }
}

@Preview(showBackground = true, name = "EmailSignUpScreen - Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES) // Renamed Preview
@Composable
fun EmailSignUpScreenDarkPreview() { // Renamed Preview function
    ChatAppTheme(darkTheme = true) {
        EmailSignUpScreen(emailAuthViewModel = EmailAuthViewModel(SavedStateHandle()), onNavigateBack = {}, flowType = "signup")
    }
} 