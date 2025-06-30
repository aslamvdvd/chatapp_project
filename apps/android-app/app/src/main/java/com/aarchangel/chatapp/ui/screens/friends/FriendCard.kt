package com.aarchangel.chatapp.ui.screens.friends

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.aarchangel.chatapp.model.dto.FriendListItem
import com.aarchangel.chatapp.model.dto.UserDto

@Composable
fun FriendCard(friend: FriendListItem, onMessageClick: () -> Unit, onCallClick: () -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 4.dp)) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = friend.user.profile_pic,
                contentDescription = "Profile picture of ${friend.user.username}",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = friend.user.username,
                modifier = Modifier.weight(1f)
            )
            Row {
                Button(
                    onClick = onMessageClick,
                    modifier = Modifier.testTag("message_friend_button")
                ) {
                    Text("Message")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onCallClick,
                    modifier = Modifier.testTag("call_friend_button")
                ) {
                    Text("Call")
                }
            }
        }
    }
}

class FriendCardPreviewParameterProvider : PreviewParameterProvider<FriendListItem> {
    override val values = sequenceOf(
        FriendListItem(
            id = "1",
            user = UserDto(id = "user1", username = "john.doe", profile_pic = null),
            status = "accepted"
        )
    )
}

@Preview(showBackground = true)
@Composable
fun FriendCardPreview(@PreviewParameter(FriendCardPreviewParameterProvider::class) friend: FriendListItem) {
    FriendCard(
        friend = friend,
        onMessageClick = {},
        onCallClick = {}
    )
} 