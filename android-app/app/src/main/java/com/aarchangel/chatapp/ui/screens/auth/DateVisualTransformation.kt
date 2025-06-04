package com.aarchangel.chatapp.ui.screens.auth

// ChatApp by aarchangel

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * A [VisualTransformation] that formats the input text as a date (DD-MM-YYYY).
 * It only allows digits and automatically inserts hyphens.
 */
class DateVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val digitsOnly = text.text.filter { it.isDigit() }
        val currentDigits = if (digitsOnly.length > 8) digitsOnly.substring(0, 8) else digitsOnly

        val out = StringBuilder()
        for (i in currentDigits.indices) {
            out.append(currentDigits[i])
            // Add hyphen after DD if MM part is started or will start
            if (i == 1 && currentDigits.length > 2) {
                out.append('-')
            } 
            // Add hyphen after MM if YYYY part is started or will start
            // Note: This is an 'else if' in the original user-accepted code, but should be independent.
            // However, to match the previous structure that was somewhat working:
            else if (i == 3 && currentDigits.length > 4) {
                out.append('-')
            }
        }
        val formattedText = out.toString()

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int { // offset in currentDigits
                var hyphens = 0
                // If cursor in original text is past the first 2 digits (DD)
                // and there are enough digits to warrant a first hyphen.
                if (offset > 2 && currentDigits.length >= 3) { 
                    hyphens++
                }
                // If cursor in original text is past the first 4 digits (DDMM)
                // and there are enough digits to warrant a second hyphen.
                if (offset > 4 && currentDigits.length >= 5) {
                    hyphens++
                }
                return (offset + hyphens).coerceAtMost(formattedText.length)
            }

            override fun transformedToOriginal(offset: Int): Int { // offset in formattedText
                var hyphens = 0
                // If cursor in transformed text is past where the first hyphen would be ("DD-")
                // and that hyphen actually exists in the current formattedText.
                if (offset > 2 && formattedText.getOrNull(2) == '-') {
                     hyphens++
                }
                // If cursor in transformed text is past where the second hyphen would be ("DD-MM-")
                // and that hyphen actually exists.
                if (offset > 5 && formattedText.getOrNull(5) == '-') {
                     hyphens++
                }
                return (offset - hyphens).coerceIn(0, currentDigits.length)
            }
        }

        return TransformedText(AnnotatedString(formattedText), offsetMapping)
    }
} 