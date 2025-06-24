package com.aarchangel.chatapp.ui.screens.home.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aarchangel.chatapp.ui.screens.home.ChannelListScreen
import com.aarchangel.chatapp.ui.screens.home.ChatListScreen
import com.aarchangel.chatapp.ui.screens.home.GroupListScreen
import com.aarchangel.chatapp.ui.screens.home.RequestListScreen

fun NavGraphBuilder.homeNavGraph() {
    navigation(
        startDestination = "home/chats",
        route = "home_graph"
    ) {
        composable("home/chats") { ChatListScreen() }
        composable("home/groups") { GroupListScreen() }
        composable("home/channels") { ChannelListScreen() }
        composable("home/requests") { RequestListScreen() }
    }
} 