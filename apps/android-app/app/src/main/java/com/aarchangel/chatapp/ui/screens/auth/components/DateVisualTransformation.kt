package com.aarchangel.chatapp.ui.screens.auth.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class DateVisualTransformation(private val format: String = "YYYY-MM-DD") : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 8) text.text.substring(0..7) else text.text
        if (trimmed.isEmpty()) {
            return TransformedText(AnnotatedString(""), OffsetMapping.Identity)
        }

        val out = StringBuilder()
        when (format) {
            "YYYY-MM-DD" -> {
                for (i in trimmed.indices) {
                    out.append(trimmed[i])
                    if (i == 3 && i < trimmed.length -1) out.append('-')
                    if (i == 5 && i < trimmed.length -1) out.append('-')
                }
            }
            // "DD-MM-YYYY" is the fallback
            else -> {
                for (i in trimmed.indices) {
                    out.append(trimmed[i])
                    if (i == 1 && i < trimmed.length - 1) out.append('-')
                    if (i == 3 && i < trimmed.length - 1) out.append('-')
                }
            }
        }


        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var transformedOffset = offset
                if (format == "YYYY-MM-DD") {
                    if (offset > 3) transformedOffset++
                    if (offset > 5) transformedOffset++
                } else { // DD-MM-YYYY
                    if (offset > 1) transformedOffset++
                    if (offset > 3) transformedOffset++
                }
                return transformedOffset.coerceAtMost(out.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                var originalOffset = offset
                 if (format == "YYYY-MM-DD") {
                    if (offset > 4) originalOffset--
                    if (offset > 7) originalOffset--
                 } else { // DD-MM-YYYY
                    if (offset > 2) originalOffset--
                    if (offset > 5) originalOffset--
                 }
                return originalOffset.coerceAtMost(trimmed.length)
            }
        }

        return TransformedText(AnnotatedString(out.toString()), offsetMapping)
    }
}