package com.aarchangel.chatapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aarchangel.chatapp.ui.screens.home.BottomNavigationBar
import com.aarchangel.chatapp.ui.screens.home.ExpandableFab
import com.aarchangel.chatapp.ui.screens.home.env.ProductionAppEnv
import com.aarchangel.chatapp.ui.screens.home.nav.BottomNavGraph

@Composable
fun MainScreen(
    onNavigateToSearch: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val fabVisible = currentDestination?.route in listOf("home", "friends")

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) },
        floatingActionButton = {
            if (fabVisible) {
                ExpandableFab(
                    appEnv = ProductionAppEnv,
                    onNewChat = { /*TODO*/ },
                    onNewGroup = { /*TODO*/ },
                    onNewChannel = { /*TODO*/ }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            BottomNavGraph(
                navController = navController,
                onNavigateToSearch = onNavigateToSearch,
                onLogoutClick = onLogoutClick
            )
        }
    }
}