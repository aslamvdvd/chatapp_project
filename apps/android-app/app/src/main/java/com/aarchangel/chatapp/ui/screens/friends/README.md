# Friends Module

This module is responsible for displaying the user's list of friends.

## Components

### `FriendListScreen.kt`
This is the main screen for the friends feature. It displays a paginated list of the current user's friends. It uses the `FriendListViewModel` to fetch the data and manage the UI state.

### `FriendListViewModel.kt`
This ViewModel handles the business logic for the `FriendListScreen`. It fetches the list of friends from the `FriendService` and exposes the UI state to the screen. It supports pagination to efficiently load the friend list.

### `FriendCard.kt`
This is a reusable composable that displays a single friend's information, including their profile picture and username. It also provides buttons to "Message" and "Call" the friend.

## State Management
The UI state is managed using a sealed class `FriendListUiState`, which represents the following states:
- `Loading`: When the friend list is being fetched from the server.
- `Success`: When the friend list has been successfully fetched. This state contains the list of friends and a boolean flag to indicate if more friends can be loaded.
- `Error`: When an error occurs while fetching the friend list. This state contains an error message to be displayed to the user. 