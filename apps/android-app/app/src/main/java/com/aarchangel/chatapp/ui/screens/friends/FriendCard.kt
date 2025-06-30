package com.aarchangel.chatapp.ui.screens.friends

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.aarchangel.chatapp.model.dto.FriendListItem

@Composable
fun FriendCard(friend: FriendListItem, onMessageClick: () -> Unit, onCallClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Row {
                AsyncImage(
                    model = friend.user.profile_pic,
                    contentDescription = "Profile picture of ${friend.user.username}",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = friend.user.username)
            }
            Row {
                Button(onClick = onMessageClick) {
                    Text("Message")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onCallClick) {
                    Text("Call")
                }
            }
        }
    }
} 