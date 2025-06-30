package com.aarchangel.chatapp.ui.screens.search

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aarchangel.chatapp.ChatApplication
import com.aarchangel.chatapp.viewmodel.ViewModelFactory
import androidx.compose.ui.tooling.preview.Preview
import com.aarchangel.chatapp.ui.theme.ChatAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel(
        factory = ViewModelFactory((LocalContext.current.applicationContext as ChatApplication).container)
    )
) {
    val uiState = viewModel.uiState
    val searchQuery = viewModel.searchQuery
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.snackbarMessages) {
        viewModel.snackbarMessages.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                label = { Text("Search for users") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("search_textfield")
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (uiState) {
                    is SearchUiState.Idle -> Text("Enter a query to search for users.")
                    is SearchUiState.Loading -> CircularProgressIndicator()
                    is SearchUiState.Error -> Text(text = uiState.message)
                    is SearchUiState.Success -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(uiState.users) { result ->
                                SearchResultItem(
                                    result = result,
                                    onAddFriend = { viewModel.sendFriendRequest(it) },
                                    onAcceptFriend = { requestId, userId -> viewModel.acceptFriendRequest(requestId, userId) },
                                    onRejectFriend = { requestId, userId -> viewModel.rejectFriendRequest(requestId, userId) },
                                    onCancelFriend = { requestId, userId -> viewModel.cancelFriendRequest(requestId, userId) }
                                )
                            }
                            if (uiState.hasMore) {
                                item {
                                    Button(
                                        onClick = { viewModel.searchUsers(false) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                            .testTag("load_more_button")
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

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    ChatAppTheme(darkTheme = true) {
        SearchScreen()
    }
} 