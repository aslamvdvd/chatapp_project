package com.aarchangel.chatapp.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarchangel.chatapp.ui.screens.home.env.AppEnv
import com.aarchangel.chatapp.ui.screens.home.env.MockAppEnv
import com.aarchangel.chatapp.ui.theme.ChatAppTheme

@Composable
fun ExpandableFab(
    appEnv: AppEnv,
    onNewChat: () -> Unit,
    onNewGroup: () -> Unit,
    onNewChannel: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.End) {
        AnimatedVisibility(visible = expanded) {
            Column {
                SmallFloatingActionButton(onClick = onNewChat) {
                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "New Chat")
                }
                Spacer(modifier = Modifier.height(8.dp))
                SmallFloatingActionButton(onClick = onNewGroup) {
                    Icon(Icons.Default.GroupAdd, contentDescription = "New ${appEnv.groupChatAlias}")
                }
                Spacer(modifier = Modifier.height(8.dp))
                SmallFloatingActionButton(onClick = onNewChannel) {
                    Icon(Icons.Default.Campaign, contentDescription = "New ${appEnv.channelAlias}")
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        FloatingActionButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}

@Preview
@Composable
fun ExpandableFabPreview() {
    ChatAppTheme(darkTheme = true) {
        ExpandableFab(
            appEnv = MockAppEnv,
            onNewChat = {},
            onNewGroup = {},
            onNewChannel = {}
        )
    }
}
