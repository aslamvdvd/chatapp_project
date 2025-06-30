# Search Module

This module allows users to search for other users on the platform and manage friend requests.

## Components

### `SearchScreen.kt`
The main UI component for the search feature. It includes a text field for entering search queries and displays the results in a list. It uses the `SearchViewModel` to perform searches and handle user actions.

### `SearchViewModel.kt`
This ViewModel contains the business logic for the search screen. It debounces user input, calls the `FriendService` to search for users, and manages the UI state. It also handles sending, accepting, rejecting, and canceling friend requests, with optimistic UI updates.

### `SearchResultItem.kt`
A composable that displays a single user in the search results. It shows the user's profile picture and username, along with action buttons (`Add Friend`, `Accept`, `Reject`, `Cancel`) that change based on the `FriendStatus` between the current user and the user in the search result.

## State Management
The UI state is managed by the `SearchUiState` sealed class:
- `Idle`: The initial state before a search is performed.
- `Loading`: Displayed when a search is in progress.
- `Success`: When a search completes successfully. This state holds the list of `UserSearchResult` objects and a flag for pagination.
- `Error`: If an error occurs during the search. It contains an error message.

## Friend Request Handling
The `SearchViewModel` optimistically updates the UI when a friend request action is performed. For example, when a user sends a friend request, the button immediately changes to a "Requested" state, even before the network call completes. This provides a responsive user experience.
A known simplification in the current implementation is the use of `userId` in place of `request_id` for accept, reject, and cancel operations. This will need to be addressed in the future by updating the backend to provide the `request_id` in the search results. 