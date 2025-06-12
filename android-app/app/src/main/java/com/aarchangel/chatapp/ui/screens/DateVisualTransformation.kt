package com.aarchangel.chatapp.ui.screens

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Formats a string of digits (e.g., "11062025") into a date "11-06-2025"
        val trimmed = if (text.text.length >= 8) text.text.substring(0..7) else text.text
        val out = buildString {
            for (i in trimmed.indices) {
                append(trimmed[i])
                if (i == 1 && i < trimmed.length - 1) {
                    append('-')
                }
                if (i == 3 && i < trimmed.length - 1) {
                    append('-')
                }
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // Maps cursor position from original text (digits only) to transformed text (with hyphens)
                var transformedOffset = offset
                if (offset > 1) transformedOffset++
                if (offset > 3) transformedOffset++
                return transformedOffset.coerceAtMost(out.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                // Maps cursor position from transformed text back to original text
                var originalOffset = offset
                if (offset > 2) originalOffset--
                if (offset > 5) originalOffset--
                return originalOffset.coerceAtMost(trimmed.length)
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
} 