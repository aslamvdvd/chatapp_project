package com.aarchangel.chatapp.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.aarchangel.chatapp.model.dto.FriendStatus
import com.aarchangel.chatapp.model.dto.UserSearchResult

@Composable
fun SearchResultItem(
    result: UserSearchResult,
    onAddFriend: (String) -> Unit,
    onAcceptFriend: (String) -> Unit,
    onRejectFriend: (String) -> Unit,
    onCancelFriend: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Row {
                AsyncImage(
                    model = result.user.profile_pic,
                    contentDescription = "Profile picture of ${result.user.username}",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = result.user.username)
            }
            when (result.friend_status) {
                FriendStatus.NONE -> Button(onClick = { onAddFriend(result.user.id) }) {
                    Text("Add Friend")
                }
                FriendStatus.PENDING_INCOMING -> {
                    Button(onClick = { onAcceptFriend(result.user.id) }) {
                        Text("Accept")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onRejectFriend(result.user.id) }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                        Text("Reject")
                    }
                }
                FriendStatus.PENDING_OUTGOING -> Button(onClick = { onCancelFriend(result.user.id) }) {
                    Text("Cancel")
                }
                FriendStatus.ACCEPTED -> Text("Friends")
            }
        }
    }
} 