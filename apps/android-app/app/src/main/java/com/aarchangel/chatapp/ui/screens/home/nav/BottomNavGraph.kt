package com.aarchangel.chatapp.ui.screens.home.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aarchangel.chatapp.ChatApplication
import com.aarchangel.chatapp.ui.screens.friends.FriendListScreen
import com.aarchangel.chatapp.ui.screens.home.HomeScreen
import com.aarchangel.chatapp.ui.screens.home.state.BottomNavItem
import com.aarchangel.chatapp.viewmodel.ViewModelFactory

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    onNavigateToSearch: () -> Unit
) {
    val context = LocalContext.current
    val factory = ViewModelFactory((context.applicationContext as ChatApplication).container)

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route
    ) {
        composable(route = BottomNavItem.Home.route) {
            HomeScreen(onNavigateToSearch = onNavigateToSearch)
        }
        composable(route = BottomNavItem.Phone.route) {
            // Placeholder for Phone Screen
        }
        composable(route = BottomNavItem.Friends.route) {
            FriendListScreen(
                viewModel = viewModel(factory = factory)
            )
        }
        composable(route = BottomNavItem.Videos.route) {
            // Placeholder for Videos Screen
        }
        composable(route = BottomNavItem.Photos.route) {
            // Placeholder for Photos Screen
        }
    }
}