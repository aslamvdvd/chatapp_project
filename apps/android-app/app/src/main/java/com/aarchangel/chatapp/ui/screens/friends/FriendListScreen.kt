package com.aarchangel.chatapp.ui.screens.friends

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aarchangel.chatapp.ChatApplication
import com.aarchangel.chatapp.viewmodel.ViewModelFactory

@Composable
fun FriendListScreen(
    viewModel: FriendListViewModel = viewModel(
        factory = ViewModelFactory((LocalContext.current.applicationContext as ChatApplication).container)
    )
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.snackbarMessages) {
        viewModel.snackbarMessages.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is FriendListUiState.Loading -> CircularProgressIndicator()
                is FriendListUiState.Error -> Text(text = uiState.message)
                is FriendListUiState.Success -> {
                    if (uiState.friends.isEmpty()) {
                        Text("You don't have any friends yet. Add some from the search screen!")
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(uiState.friends) { friend ->
                                FriendCard(friend = friend, onMessageClick = { /*TODO*/ }, onCallClick = { /*TODO*/ })
                            }
                            if (uiState.hasMore) {
                                item {
                                    Button(
                                        onClick = { viewModel.fetchFriends() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Text("Load More")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
} 