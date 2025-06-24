package com.aarchangel.chatapp.ui.screens.home

import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aarchangel.chatapp.ui.screens.home.env.AppEnv
import com.aarchangel.chatapp.ui.screens.home.env.MockAppEnv
import com.aarchangel.chatapp.ui.screens.home.state.HomeViewModel
import com.aarchangel.chatapp.ui.theme.ChatAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavTabs(
    appEnv: AppEnv,
    viewModel: HomeViewModel,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf(
        "Chats",
        appEnv.groupChatAlias,
        appEnv.channelAlias,
        "Requests"
    )
    val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()

    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
            )
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = {
                    viewModel.selectTab(index)
                    onTabSelected(index)
                },
                text = { Text(text = title) },
                modifier = Modifier.semantics { contentDescription = "$title Tab" }
            )
        }
    }
}

@Preview
@Composable
fun TopNavTabsPreview() {
    ChatAppTheme(darkTheme = true) {
        val viewModel: HomeViewModel = viewModel()
        TopNavTabs(appEnv = MockAppEnv, viewModel = viewModel, onTabSelected = {})
    }
} 