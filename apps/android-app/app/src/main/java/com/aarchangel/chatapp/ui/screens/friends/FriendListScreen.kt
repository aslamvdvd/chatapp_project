package com.aarchangel.chatapp.ui.screens.friends

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aarchangel.chatapp.ChatApplication
import com.aarchangel.chatapp.R
import com.aarchangel.chatapp.viewmodel.ViewModelFactory
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import com.aarchangel.chatapp.model.dto.UserSearchResult
import com.aarchangel.chatapp.model.dto.FriendListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    viewModel: FriendListViewModel,
) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf(
        stringResource(id = R.string.friends_tab_my_friends),
        stringResource(id = R.string.friends_tab_incoming_requests),
        stringResource(id = R.string.friends_tab_blocked_users)
    )

    Column {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    text = { Text(text = title) }
                )
            }
        }
        when (tabIndex) {
            0 -> FriendList(viewModel)
            1 -> IncomingRequestsScreen(viewModel)
            2 -> BlockedUsersScreen(viewModel)
        }
    }
}

@Composable
fun FriendList(viewModel: FriendListViewModel) {
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
                    if (uiState.items.isEmpty()) {
                        Text("You don't have any friends yet. Add some from the search screen!")
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(uiState.items) { item ->
                                if (item is FriendListItem) {
                                    FriendCard(
                                        friend = item, 
                                        onMessageClick = { /*TODO*/ }, 
                                        onCallClick = { /*TODO*/ }
                                    )
                                }
                            }
                            if (uiState.hasMore) {
                                item {
                                    Button(
                                        onClick = { 
                                            viewModel.fetchFriends(
                                                listType = FriendListUiState.ListType.FRIENDS,
                                                page = 1,
                                                limit = 20
                                            ) 
                                        },
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

@Composable
fun IncomingRequestsScreen(viewModel: FriendListViewModel) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.fetchFriends(
            listType = FriendListUiState.ListType.INCOMING_REQUESTS,
            page = 1,
            limit = 20
        )
    }

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
                    if (uiState.items.isEmpty()) {
                        Text("No incoming friend requests")
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(uiState.items) { request ->
                                if (request is UserSearchResult) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = request.username)
                                        Row {
                                            Button(
                                                onClick = { 
                                                    request.requestId?.let { 
                                                        viewModel.acceptFriendRequest(it) 
                                                    }
                                                },
                                                modifier = Modifier.padding(end = 8.dp)
                                            ) {
                                                Text("Accept")
                                            }
                                            Button(
                                                onClick = { 
                                                    request.requestId?.let { 
                                                        viewModel.rejectFriendRequest(it) 
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.error
                                                )
                                            ) {
                                                Text("Reject")
                                            }
                                        }
                                    }
                                }
                            }
                            if (uiState.hasMore) {
                                item {
                                    Button(
                                        onClick = { 
                                            viewModel.fetchFriends(
                                                listType = FriendListUiState.ListType.INCOMING_REQUESTS,
                                                page = 1,
                                                limit = 20
                                            ) 
                                        },
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

@Composable
fun BlockedUsersScreen(viewModel: FriendListViewModel) {
    // Placeholder for blocked users
    Text("Blocked Users")
}

@Composable
fun FriendListScreen(
    viewModel: FriendListViewModel = viewModel(
        factory = ViewModelFactory(
            (LocalContext.current.applicationContext as ChatApplication).container
        )
    )
) {
    FriendsScreen(viewModel = viewModel)
} 