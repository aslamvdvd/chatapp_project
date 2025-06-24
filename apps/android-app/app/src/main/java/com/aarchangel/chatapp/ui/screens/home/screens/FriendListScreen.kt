package com.aarchangel.chatapp.ui.screens.home.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aarchangel.chatapp.ui.screens.home.components.FriendCard
import com.aarchangel.chatapp.ui.screens.home.viewmodels.FriendListUiState
import com.aarchangel.chatapp.ui.screens.home.viewmodels.FriendViewModel
import com.aarchangel.chatapp.ui.theme.ChatAppTheme

@Composable
fun FriendListScreen(viewModel: FriendViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is FriendListUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is FriendListUiState.Success -> {
            LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
                items(state.friends) { friend ->
                    FriendCard(friend = friend, onChatClicked = { /* TODO: Navigate to chat */ })
                }
            }
        }
        is FriendListUiState.Empty -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("You haven't added any friends yet.")
            }
        }
        is FriendListUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.message)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FriendListScreenPreview() {
    ChatAppTheme(darkTheme = true) {
        // This preview will show the loading state by default
        FriendListScreen()
    }
} 