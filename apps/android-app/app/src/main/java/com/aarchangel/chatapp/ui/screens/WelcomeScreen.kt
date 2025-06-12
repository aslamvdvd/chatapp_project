package com.aarchangel.chatapp.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarchangel.chatapp.R
import com.aarchangel.chatapp.config.AppConfig
import com.aarchangel.chatapp.ui.theme.ChatAppTheme
import com.aarchangel.chatapp.ui.theme.Dimens

@Composable
fun WelcomeScreen(
    onAgreeAndContinue: () -> Unit
) {
    val context = LocalContext.current
    val termsText = remember {
        context.resources.openRawResource(R.raw.terms_and_conditions)
            .bufferedReader().use { it.readText() }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp)
                    .verticalScroll(rememberScrollState())
                    .semantics { contentDescription = "Terms and Conditions text" }
            ) {
                Text(text = termsText, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = onAgreeAndContinue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Agree and Continue")
            }
        }
    }
}

@Preview(showBackground = true, name = "Welcome Screen")
@Composable
fun WelcomeScreenPreview() {
    ChatAppTheme(darkTheme = true) {
        WelcomeScreen(
            onAgreeAndContinue = { Log.d("WelcomeScreen", "Agree and Continue clicked") }
        )
    }
} 