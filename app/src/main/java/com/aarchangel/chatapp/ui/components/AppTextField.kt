package com.aarchangel.chatapp.ui.components

// ChatApp by aarchangel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.aarchangel.chatapp.ui.theme.ChatAppTheme
import com.aarchangel.chatapp.ui.theme.Dimens

/**
 * A styled OutlinedTextField for use across the ChatApp.
 * // ChatApp by aarchangel
 *
 * @param value The input text to be shown in the text field.
 * @param onValueChange The callback that is triggered when the input service updates the text.
 * @param modifier Modifier for this text field.
 * @param enabled Controls the enabled state of the text field.
 * @param readOnly Controls the editable state of the text field.
 * @param label The label to be displayed inside or above the text field.
 * @param placeholder The placeholder to be displayed when the text field is in focus and empty.
 * @param leadingIcon Optional leading icon for the text field.
 * @param trailingIcon Optional trailing icon for the text field.
 * @param isError Indicates if the text field's current value is in error.
 * @param errorMessage The error message to be displayed below the text field if `isError` is true.
 * @param visualTransformation Transforms the visual representation of the input text.
 * @param keyboardOptions Software keyboard options that contains configuration such as [KeyboardType] and [ImeAction].
 * @param keyboardActions When the input service emits an IME action, the corresponding callback is called.
 * @param singleLine When set to true, this text field becomes a single horizontally scrolling text field instead of wrapping onto multiple lines.
 * @param maxLines The maximum number of lines to be displayed in the text field.
 * @param shape Defines the shape of the text field's border.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    shape: Shape = MaterialTheme.shapes.small
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.PaddingSmall),
            enabled = enabled,
            readOnly = readOnly,
            label = label?.let { { Text(it) } },
            placeholder = placeholder?.let { { Text(it) } },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            shape = shape,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            )
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = Dimens.PaddingSmall, top = Dimens.PaddingExtraSmall)
            )
        }
    }
}

@Preview(showBackground = true, name = "AppTextField Preview")
@Composable
fun AppTextFieldPreview() {
    ChatAppTheme {
        AppTextField(
            value = "Sample Text",
            onValueChange = {},
            label = "Email"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "AppTextField Preview - Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AppTextFieldPreviewDark() {
    ChatAppTheme(darkTheme = true) {
        AppTextField(
            value = "Sample Text",
            onValueChange = {},
            label = "Email",
            placeholder = "Enter your email"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "AppTextField Error Preview")
@Composable
fun AppTextFieldErrorPreview() {
    ChatAppTheme {
        AppTextField(
            value = "Invalid Text",
            onValueChange = {},
            label = "Email",
            isError = true,
            errorMessage = "This email is not valid."
        )
    }
} 