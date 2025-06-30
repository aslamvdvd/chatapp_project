package com.aarchangel.chatapp.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.aarchangel.chatapp.model.dto.FriendStatus
import com.aarchangel.chatapp.model.dto.UserSearchResult

@Composable
fun SearchResultItem(
    result: UserSearchResult,
    onAddFriend: (String) -> Unit,
    onAcceptFriend: (String, String) -> Unit,
    onRejectFriend: (String, String) -> Unit,
    onCancelFriend: (String, String) -> Unit
) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 4.dp)) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = result.avatarUrl,
                contentDescription = "Avatar of ${result.username}",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = result.username)
                Text(text = result.fullName, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.width(8.dp))

            when (result.status) {
                FriendStatus.NONE -> Button(
                    onClick = { onAddFriend(result.userId) },
                    modifier = Modifier.testTag("add_friend_button")
                ) {
                    Text("Add")
                }
                FriendStatus.PENDING_INCOMING -> {
                    Row {
                        Button(
                            onClick = { result.requestId?.let { onAcceptFriend(it, result.userId) } },
                            modifier = Modifier.testTag("accept_friend_button")
                        ) {
                            Text("Accept")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { result.requestId?.let { onRejectFriend(it, result.userId) } },
                            modifier = Modifier.testTag("reject_friend_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Reject")
                        }
                    }
                }
                FriendStatus.PENDING_OUTGOING -> {
                    Button(
                        onClick = { result.requestId?.let { onCancelFriend(it, result.userId) } },
                        modifier = Modifier.testTag("cancel_friend_button"),
                        enabled = result.requestId != null
                    ) {
                        Text("Cancel")
                    }
                }
                FriendStatus.ACCEPTED -> Text("Friends")
            }
        }
    }
}

class SearchResultPreviewParameterProvider : PreviewParameterProvider<UserSearchResult> {
    override val values = sequenceOf(
        UserSearchResult("1", "jdoe", "John Doe", null, null, FriendStatus.NONE),
        UserSearchResult("2", "janedoe", "Jane Doe", null, "req123", FriendStatus.PENDING_INCOMING),
        UserSearchResult("3", "sam", "Sam Jones", null, "req456", FriendStatus.PENDING_OUTGOING),
        UserSearchResult("4", "alex", "Alex Smith", null, null, FriendStatus.ACCEPTED)
    )
}

@Preview(showBackground = true)
@Composable
fun SearchResultItemPreview(@PreviewParameter(SearchResultPreviewParameterProvider::class) result: UserSearchResult) {
    SearchResultItem(
        result = result,
        onAddFriend = {},
        onAcceptFriend = { _, _ -> },
        onRejectFriend = { _, _ -> },
        onCancelFriend = { _, _ -> }
    )
} 