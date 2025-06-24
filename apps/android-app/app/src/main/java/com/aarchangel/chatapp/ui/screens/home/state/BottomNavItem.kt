package com.aarchangel.chatapp.ui.screens.home.state

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.ui.graphics.vector.ImageVector
import com.aarchangel.chatapp.config.AppConfig

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Phone : BottomNavItem("phone", Icons.Default.Phone, "Phone")
    object Friends : BottomNavItem("home/friends", Icons.Default.People, AppConfig.FRIENDS_TAB_LABEL)
    object Videos : BottomNavItem("videos", Icons.Default.Videocam, "Videos")
    object Photos : BottomNavItem("photos", Icons.Default.PhotoLibrary, "Photos")
} 