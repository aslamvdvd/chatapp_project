package com.aarchangel.chatapp.ui.screens.home.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aarchangel.chatapp.data.TokenStorage
import com.aarchangel.chatapp.ui.screens.home.HomeScreen
import com.aarchangel.chatapp.ui.screens.home.screens.FriendListScreen
import com.aarchangel.chatapp.ui.screens.home.state.BottomNavItem
import com.aarchangel.chatapp.viewmodel.ViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aarchangel.chatapp.ui.screens.home.viewmodels.FriendViewModel
import androidx.compose.ui.platform.LocalContext

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    onNavigateToSearch: () -> Unit
) {
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
            val factory = ViewModelFactory(LocalContext.current)
            val friendViewModel: FriendViewModel = viewModel(factory = factory)
            FriendListScreen(viewModel = friendViewModel)
        }
        composable(route = BottomNavItem.Videos.route) {
            // Placeholder for Videos Screen
        }
        composable(route = BottomNavItem.Photos.route) {
            // Placeholder for Photos Screen
        }
    }
}