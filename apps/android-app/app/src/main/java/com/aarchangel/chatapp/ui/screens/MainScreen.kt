package com.aarchangel.chatapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.aarchangel.chatapp.ui.screens.home.BottomNavigationBar
import com.aarchangel.chatapp.ui.screens.home.ExpandableFab
import com.aarchangel.chatapp.ui.screens.home.env.ProductionAppEnv
import com.aarchangel.chatapp.ui.screens.home.nav.BottomNavGraph

@Composable
fun MainScreen(onNavigateToSearch: () -> Unit) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) },
        floatingActionButton = {
            ExpandableFab(
                appEnv = ProductionAppEnv,
                onNewChat = { /*TODO*/ },
                onNewGroup = { /*TODO*/ },
                onNewChannel = { /*TODO*/ }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            BottomNavGraph(
                navController = navController,
                onNavigateToSearch = onNavigateToSearch
            )
        }
    }
}