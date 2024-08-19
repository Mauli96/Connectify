package com.example.connectify.feature_profile.presentation.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.ImageLoader
import com.example.connectify.R
import com.example.connectify.core.presentation.components.UserProfileItem
import com.example.connectify.core.presentation.ui.theme.IconSizeMedium
import com.example.connectify.core.presentation.ui.theme.IconSizeSmall
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.core.presentation.util.showKeyboard
import com.example.connectify.core.util.Screen
import com.example.connectify.feature_profile.domain.util.ProfileConstants

@ExperimentalMaterialApi
@Composable
fun SearchScreen(
    imageLoader: ImageLoader,
    onNavigate: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state = viewModel.searchState.value

    val focusRequester = remember {
        FocusRequester()
    }

    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        context.showKeyboard()
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = SpaceMedium,
                        end = SpaceSmall
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        onNavigateUp()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(IconSizeMedium)
                    )
                }
                Spacer(modifier = Modifier.width(SpaceSmall))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .focusRequester(focusRequester = focusRequester),
                    value = viewModel.searchFieldState.value.text,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = MaterialTheme.colorScheme.onPrimary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    onValueChange = {
                        if(it.length <= ProfileConstants.MAX_LENGTH) {
                            viewModel.onEvent(SearchEvent.Query(it))
                        }
                    },
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.search),
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.search_icon),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.background,
                            modifier = Modifier.size(IconSizeSmall)
                        )
                    },
                    trailingIcon = {
                        if(viewModel.searchFieldState.value.text.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    viewModel.onEvent(SearchEvent.OnToggleSearch(""))
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.background,
                                    modifier = Modifier.size(IconSizeMedium)
                                )
                            }
                        }
                    }
                )
            }
            Column(
                modifier = Modifier
                    .padding(top = SpaceMedium)
                    .fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.userItems) { user ->
                        UserProfileItem(
                            user = user,
                            imageLoader = imageLoader,
                            modifier = Modifier.fillMaxWidth(),
                            isFollowing = user.isFollowing,
                            onActionItemClick = {
                                viewModel.onEvent(SearchEvent.ToggleFollow(user.userId))
                            },
                            onItemClick = {
                                onNavigate(
                                    Screen.ProfileScreen.route + "?userId=${user.userId}"
                                )
                            }
                        )
                    }
                }
            }
        }
        if(state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Center),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.dp,
                trackColor = Color.White
            )
        }
    }
}