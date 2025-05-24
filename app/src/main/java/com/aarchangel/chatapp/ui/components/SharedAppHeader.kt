package com.aarchangel.chatapp.ui.components

// ChatApp by aarchangel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.aarchangel.chatapp.config.AppConfig
import com.aarchangel.chatapp.ui.theme.ChatAppTheme
import com.aarchangel.chatapp.ui.theme.Dimens

/**
 * A shared application header that displays the platform name, slogan, and an optional title.
 * Animates its padding and slogan size based on whether it's on a primary (e.g., Welcome) or secondary screen.
 * // ChatApp by aarchangel
 *
 * @param isPrimaryScreen True if the header is on a primary screen like Welcome, which affects styling.
 * @param authFlowType If on an auth options screen, specifies the flow type ("login" or "signup") to display the correct title.
 */
@Composable
fun SharedAppHeader(
    isPrimaryScreen: Boolean,
    authFlowType: String? = null // e.g., "login" or "signup"
) {
    val sloganFontSize by animateFloatAsState(
        targetValue = if (isPrimaryScreen) 18f else 14f,
        animationSpec = tween(durationMillis = 300),
        label = "SloganFontSizeHeader"
    )
    val headerPaddingTop by animateDpAsState(
        targetValue = if (isPrimaryScreen) Dimens.PaddingHuge else Dimens.PaddingExtraLarge,
        animationSpec = tween(durationMillis = 300),
        label = "HeaderPaddingTopHeader"
    )
    val headerPaddingBottom by animateDpAsState(
        targetValue = Dimens.PaddingMedium, // Consistent bottom padding
        animationSpec = tween(durationMillis = 300),
        label = "HeaderPaddingBottomHeader"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = headerPaddingTop,
                start = Dimens.PaddingExtraLarge,
                end = Dimens.PaddingExtraLarge,
                bottom = headerPaddingBottom
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = AppConfig.PLATFORM_NAME,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(Dimens.PaddingSmall))
        Text(
            text = AppConfig.PLATFORM_SLOGAN,
            style = TextStyle(
                fontSize = sloganFontSize.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        )
        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

        AnimatedVisibility(
            visible = authFlowType != null,
            enter = fadeIn(animationSpec = tween(delayMillis = 150)) + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            val authOptionsTitle = if (authFlowType == "signup") "Choose Sign Up Method" else "Choose Log In Method"
            Text(
                text = authOptionsTitle,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = Dimens.PaddingSmall),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true, name = "Shared Header - Primary")
@Composable
fun SharedAppHeaderPrimaryPreview() {
    ChatAppTheme {
        SharedAppHeader(isPrimaryScreen = true)
    }
}

@Preview(showBackground = true, name = "Shared Header - Secondary")
@Composable
fun SharedAppHeaderSecondaryPreview() {
    ChatAppTheme {
        SharedAppHeader(isPrimaryScreen = false)
    }
}

@Preview(showBackground = true, name = "Shared Header - Auth Login")
@Composable
fun SharedAppHeaderAuthLoginPreview() {
    ChatAppTheme {
        SharedAppHeader(isPrimaryScreen = false, authFlowType = "login")
    }
}

@Preview(showBackground = true, name = "Shared Header - Auth Sign Up - Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SharedAppHeaderAuthSignUpDarkPreview() {
    ChatAppTheme(darkTheme = true) {
        SharedAppHeader(isPrimaryScreen = false, authFlowType = "signup")
    }
} 