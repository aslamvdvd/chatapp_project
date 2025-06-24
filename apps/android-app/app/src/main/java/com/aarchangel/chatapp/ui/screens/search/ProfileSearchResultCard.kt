package com.aarchangel.chatapp.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aarchangel.chatapp.ui.model.UiUserProfile
import com.aarchangel.chatapp.ui.model.FriendRequestStatus

@Composable
fun ProfileSearchResultCard(
    uiUserProfile: UiUserProfile,
    onSendRequest: (String) -> Unit,
    onAcceptRequest: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = uiUserProfile.profile.username, style = MaterialTheme.typography.titleMedium)
                Text(text = uiUserProfile.profile.email, style = MaterialTheme.typography.bodyMedium)
            }
            when (uiUserProfile.requestStatus) {
                FriendRequestStatus.NONE -> {
                    Button(onClick = { onSendRequest(uiUserProfile.profile.id) }) {
                        Text("Add")
                    }
                }
                FriendRequestStatus.PENDING -> {
                    OutlinedButton(onClick = {}, enabled = false) {
                        Text("Pending")
                    }
                }
                FriendRequestStatus.INCOMING -> {
                    Button(onClick = { onAcceptRequest(uiUserProfile.profile.id) }) {
                        Text("Accept")
                    }
                }
                FriendRequestStatus.FRIENDS -> {
                    Text("Friends", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
