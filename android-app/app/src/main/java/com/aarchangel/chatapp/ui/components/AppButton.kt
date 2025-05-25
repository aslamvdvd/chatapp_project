package com.aarchangel.chatapp.ui.components

// ChatApp by aarchangel

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.aarchangel.chatapp.ui.theme.ChatAppTheme
import com.aarchangel.chatapp.ui.theme.Dimens

/**
 * A primary styled button for common actions in the ChatApp.
 * // ChatApp by aarchangel
 *
 * @param text The text to display on the button.
 * @param onClick Lambda to be invoked when the button is clicked.
 * @param modifier Modifier for this button.
 * @param enabled Whether the button is enabled.
 * @param icon Optional leading icon for the button.
 * @param contentPadding Padding around the button content.
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.PaddingExtraLarge, vertical = Dimens.PaddingSmall),
        shape = RoundedCornerShape(Dimens.RoundedCornerMedium),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = Dimens.ElevationMedium),
        enabled = enabled,
        contentPadding = contentPadding
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null, // Text should describe the action
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        }
        Text(text = text)
    }
}

/**
 * Preview for the AppButton.
 */
@Preview(showBackground = true, name = "AppButton Preview")
@Composable
fun AppButtonPreview() {
    ChatAppTheme {
        AppButton(text = "Sample Button", onClick = {})
    }
}

@Preview(showBackground = true, name = "AppButton Preview - Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AppButtonPreviewDark() {
    ChatAppTheme {
        AppButton(text = "Sample Button", onClick = {})
    }
}

@Preview(showBackground = true, name = "AppButton Disabled Preview")
@Composable
fun AppButtonDisabledPreview() {
    ChatAppTheme {
        AppButton(text = "Disabled Button", onClick = {}, enabled = false)
    }
} 