package com.aarchangel.chatapp.ui.screens

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarchangel.chatapp.config.AppConfig
import com.aarchangel.chatapp.ui.theme.ChatAppTheme
import com.aarchangel.chatapp.ui.theme.Dimens
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aarchangel.chatapp.data.network.dto.SignUpRequest
import com.aarchangel.chatapp.viewmodel.EmailAuthViewModel
import com.aarchangel.chatapp.viewmodel.ViewModelFactory

@Composable
fun EmailSignUpScreen(
    onNavigateBack: () -> Unit,
    onSignUpSuccess: () -> Unit
) {
    val factory = ViewModelFactory(LocalContext.current)
    val viewModel: EmailAuthViewModel = viewModel(factory = factory)
    val signUpState by viewModel.signUpState.collectAsState()

    var firstName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(signUpState.isSuccess) {
        if (signUpState.isSuccess) {
            onSignUpSuccess()
        }
    }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val newDate = Calendar.getInstance()
            newDate.set(year, month, dayOfMonth)
            val format = SimpleDateFormat("dd-mm-yyyy", Locale.getDefault())
            dob = format.format(newDate.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Dimens.PaddingLarge),
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
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.PaddingLarge))
            
            OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First Name*") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = middleName, onValueChange = { middleName = it }, label = { Text("Middle Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last Name*") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username*") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address*") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth())

            OutlinedTextField(
                value = dob,
                onValueChange = { if (it.length <= 8) dob = it.filter { char -> char.isDigit() } },
                label = { Text("Date of Birth (dd-mm-yyyy)*") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = DateVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                }
            )

            OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Gender") }, modifier = Modifier.fillMaxWidth())
            
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password*") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                    }
                }
            )
            
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password*") },
                isError = signUpState.fieldErrors?.containsKey("confirm_password") == true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password")
                    }
                }
            )

            signUpState.fieldErrors?.get("confirm_password")?.let {
                Text(it.joinToString(), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(Dimens.PaddingExtraLarge))

            Button(
                onClick = {
                    viewModel.signUp(
                        SignUpRequest(
                            email = email,
                            username = username,
                            firstName = firstName,
                            middleName = middleName.ifEmpty { null },
                            lastName = lastName,
                            dateOfBirth = dob,
                            gender = gender.ifEmpty { null },
                            password = password,
                            confirmPassword = confirmPassword
                        )
                    )
                },
                enabled = !signUpState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (signUpState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Create Account")
                }
            }
            
            signUpState.error?.let {
                if (signUpState.fieldErrors == null) {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }
            }

            TextButton(onClick = onNavigateBack) {
                Text("Back")
            }
        }
    }
}

@Preview(showBackground = true, name = "Email Sign Up Screen")
@Composable
fun EmailSignUpScreenPreview() {
    ChatAppTheme(darkTheme = true) {
        EmailSignUpScreen(onNavigateBack = {}, onSignUpSuccess = {})
    }
} 