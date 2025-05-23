package com.aarchangel.chatapp

// ChatApp by aarchangel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.aarchangel.chatapp.ui.screens.WelcomeScreen // Updated import
import com.aarchangel.chatapp.ui.theme.ChatAppTheme // Updated import

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatAppTheme { // Updated theme usage
                WelcomeScreen()
            }
        }
    }
} 