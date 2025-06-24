package com.aarchangel.chatapp.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aarchangel.chatapp.ui.screens.home.components.SearchBar
import com.aarchangel.chatapp.ui.theme.ChatAppTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SearchScreen(searchViewModel: SearchViewModel = viewModel()) {
    var query by remember { mutableStateOf("") }
    val searchResults by searchViewModel.searchResults.collectAsState()
    val isLoading by searchViewModel.isLoading.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            query = query,
            onQueryChanged = {
                query = it
                searchViewModel.searchUsers(it)
            }
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(searchResults) { userProfile ->
                    ProfileSearchResultCard(
                        uiUserProfile = userProfile,
                        onSendRequest = { searchViewModel.sendFriendRequest(it) },
                        onAcceptRequest = { searchViewModel.acceptFriendRequest(it) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    ChatAppTheme(darkTheme = true) {
        SearchScreen()
    }
} 