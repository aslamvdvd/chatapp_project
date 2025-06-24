package com.aarchangel.chatapp.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aarchangel.chatapp.ui.screens.home.env.ProductionAppEnv
import com.aarchangel.chatapp.ui.screens.home.state.HomeViewModel
import com.aarchangel.chatapp.ui.theme.ChatAppTheme

@Composable
fun HomeScreen(onNavigateToSearch: () -> Unit) {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = viewModel()

    Column {
        AppTopBar(
            appEnv = ProductionAppEnv,
            onSearchClick = onNavigateToSearch
        )
        TopNavTabs(
            appEnv = ProductionAppEnv,
            viewModel = homeViewModel,
            onTabSelected = { index ->
                val route = when (index) {
                    0 -> "chats"
                    1 -> "groups"
                    2 -> "channels"
                    else -> "requests"
                }
                navController.navigate(route) {
                    navController.graph.startDestinationRoute?.let { popUpTo(it) }
                    launchSingleTop = true
                }
            }
        )
        NavHost(
            navController = navController,
            startDestination = "chats"
        ) {
            composable("chats") { ChatListScreen() }
            composable("groups") { GroupListScreen() }
            composable("channels") { ChannelListScreen() }
            composable("requests") { RequestListScreen() }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ChatAppTheme(darkTheme = true) {
        HomeScreen(onNavigateToSearch = {})
    }
}