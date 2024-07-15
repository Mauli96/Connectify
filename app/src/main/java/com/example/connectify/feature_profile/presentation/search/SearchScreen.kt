package com.example.connectify.feature_profile.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.connectify.R
import com.example.connectify.core.domain.models.User
import com.example.connectify.core.presentation.components.UserProfileItem
import com.example.connectify.core.presentation.ui.theme.IconSizeMedium
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.util.Screen

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalMaterialApi
@Composable
fun SearchScreen(
    onNavigate: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchBar(
            query = searchText,
            onQueryChange = viewModel::onSearchTextChange,
            onSearch = viewModel::onSearchTextChange,
            active = isSearching,
            onActiveChange = { viewModel.onToogleSearch() },
            placeholder = {
                Text(
                    text = "Search...",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            colors = SearchBarDefaults.colors(
                MaterialTheme.colorScheme.secondary
            ),
            tonalElevation = 0.dp,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.search),
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(IconSizeMedium)
                )
            },
            trailingIcon = {
                if(isSearching) {
                    Icon(
                        modifier = Modifier
                            .size(IconSizeMedium)
                            .clickable {
                                viewModel.onToogleSearch()
                            },
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.clear),
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {

        }
        Spacer(modifier = Modifier.height(SpaceMedium))
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(10) {
                UserProfileItem(
                    user = User(
                        userId = "6689706018e6bc04477642e9",
                        username = "mauli.waghmore",
                        profilePictureUrl = "",
                        description = "✨ Less is more | Simplicity lover\n" + "\uD83C\uDF3F Embracing minimalism\n" + "\uD83D\uDDA4 Black and white aesthetic\n" + "\uD83E\uDDD8\u200D♂\uFE0F Mindful living",
                        followerCount = 120,
                        followingCount = 50,
                        postCount = 23
                    ),
                    actionIcon = {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(IconSizeMedium)
                        )
                    },
                    onItemClick = {
                        onNavigate(Screen.ProfileScreen.route + "?userId=6689706018e6bc04477642e9")
                    }
                )
                Spacer(modifier = Modifier.height(SpaceMedium))
            }
        }
    }
}