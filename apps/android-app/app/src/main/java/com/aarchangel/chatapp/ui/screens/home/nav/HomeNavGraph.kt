package com.aarchangel.chatapp.ui.screens.home.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.aarchangel.chatapp.ui.screens.friends.FriendListScreen
import com.aarchangel.chatapp.ui.screens.home.*

fun NavGraphBuilder.homeNavGraph() {
    navigation(
        startDestination = "home/chats",
        route = "home"
    ) {
        composable("home/chats") { ChatListScreen() }
        composable("home/groups") { GroupListScreen() }
        composable("home/channels") { ChannelListScreen() }
        composable("home/requests") { RequestListScreen() }
        composable("home/friends") { FriendListScreen() }
        composable("home/profile") { ProfileScreen() }
    }
} 