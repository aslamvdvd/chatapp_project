package com.aarchangel.chatapp.ui.screens.auth

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.aarchangel.chatapp.config.AppConfig
import com.aarchangel.chatapp.ui.theme.ChatAppTheme
import com.aarchangel.chatapp.ui.theme.Dimens

@Composable
fun LoginMethodScreen(
    onContinueWithPhone: () -> Unit,
    onContinueWithEmail: () -> Unit,
    onContinueWithGoogle: () -> Unit,
    onContinueWithApple: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.PaddingExtraLarge),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.semantics { heading() },
                text = AppConfig.PLATFORM_NAME,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

            Text(
                text = AppConfig.PLATFORM_SLOGAN,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.PaddingHuge))

            Text(
                text = "Log In",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = Dimens.PaddingLarge)
            )

            
            Button(
                onClick = onContinueWithPhone,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Continue with Phone Number")
            }
            Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
            Button(
                onClick = onContinueWithEmail,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Continue with Email Address")
            }
            Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
            Button(
                onClick = onContinueWithGoogle,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Continue with Google")
            }
            Spacer(modifier = Modifier.height(Dimens.PaddingMedium))
            Button(
                onClick = onContinueWithApple,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Continue with Apple")
            }
        }
    }
}

@Preview(showBackground = true, name = "Login Method Screen")
@Composable
fun LoginMethodScreenPreview() {
    ChatAppTheme(darkTheme = true) {
        LoginMethodScreen(
            onContinueWithPhone = { Log.d("LoginMethodScreen", "Continue with Phone clicked") },
            onContinueWithEmail = { Log.d("LoginMethodScreen", "Continue with Email clicked") },
            onContinueWithGoogle = { Log.d("LoginMethodScreen", "Continue with Google clicked") },
            onContinueWithApple = { Log.d("LoginMethodScreen", "Continue with Apple clicked") }
        )
    }
} 