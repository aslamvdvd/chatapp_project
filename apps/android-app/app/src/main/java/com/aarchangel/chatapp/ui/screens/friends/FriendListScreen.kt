package com.aarchangel.chatapp.ui.screens.friends

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (uiState) {
            is FriendListUiState.Loading -> CircularProgressIndicator()
            is FriendListUiState.Error -> Text(text = uiState.message)
            is FriendListUiState.Success -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.friends) { friend ->
                        FriendCard(friend = friend, onMessageClick = { /*TODO*/ }, onCallClick = { /*TODO*/ })
                    }
                    if (uiState.hasMore) {
                        item {
                            Button(
                                onClick = { viewModel.fetchFriends() },
                                modifier = Modifier.fillMaxWidth().padding(16.dp)
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