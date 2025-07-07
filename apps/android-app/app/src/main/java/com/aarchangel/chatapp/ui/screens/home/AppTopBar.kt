package com.aarchangel.chatapp.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarchangel.chatapp.R
import com.aarchangel.chatapp.ui.screens.home.env.AppEnv
import com.aarchangel.chatapp.ui.screens.home.env.MockAppEnv
import com.aarchangel.chatapp.ui.theme.ChatAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    appEnv: AppEnv,
    onSearchClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = appEnv.platformName,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with actual profile pic
                contentDescription = stringResource(R.string.user_profile_picture_description),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .clickable { /* Navigate to profile */ }
            )
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search_content_description)
                )
            }
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.more_options_content_description)
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(text = { Text("New Chat") }, onClick = { /* Handle action */ })
                DropdownMenuItem(text = { Text("New ${appEnv.groupChatAlias}") }, onClick = { /* Handle action */ })
                DropdownMenuItem(text = { Text("New ${appEnv.channelAlias}") }, onClick = { /* Handle action */ })
                DropdownMenuItem(text = { Text("Profile") }, onClick = { /*Handle Profile opening action */})
                DropdownMenuItem(text = { Text("Settings") }, onClick = { /* Handle action */ })
                DropdownMenuItem(
                    text = { Text("Logout") },
                    onClick = {
                        showMenu = false
                        onLogoutClick()
                    }
                )
            }
        },
        modifier = Modifier.semantics { contentDescription = "Top application bar" }
    )
}

@Preview(showBackground = true)
@Composable
fun AppTopBarPreview() {
    ChatAppTheme(darkTheme = true) {
        AppTopBar(appEnv = MockAppEnv, onSearchClick = {}, onLogoutClick = {})
    }
} 