package com.example.connectify.feature_profile.presentation.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import com.example.connectify.R
import com.example.connectify.core.presentation.components.CustomCircularProgressIndicator
import com.example.connectify.core.presentation.components.StandardSearchField
import com.example.connectify.core.presentation.components.UserProfileItem
import com.example.connectify.core.presentation.ui.theme.IconSizeMedium
import com.example.connectify.core.presentation.ui.theme.IconSizeSmall
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.presentation.util.asString
import com.example.connectify.core.presentation.util.showKeyboard
import com.example.connectify.core.util.Screen
import kotlinx.coroutines.flow.collectLatest

@ExperimentalMaterialApi
@Composable
fun SearchScreen(
    imageLoader: ImageLoader,
    onNavigate: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    scaffoldState: ScaffoldState,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchFieldState by viewModel.searchFieldState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    val focusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.uiText.asString(context)
                    )
                }
                else -> {
                    null
                }
            }
        }
    }

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
                        keyboardController?.hide()
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
                StandardSearchField(
                    query = searchFieldState.text,
                    onQueryChanged = {
                        viewModel.onEvent(SearchEvent.Query(it))
                    },
                    focusRequester = focusRequester,
                    placeholderText = stringResource(id = R.string.search_for_user),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.search_icon),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.background,
                            modifier = Modifier.size(IconSizeSmall)
                        )
                    },
                    trailingIcon = {
                        if(searchFieldState.text.isNotEmpty()) {
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
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Column(
                modifier = Modifier
                    .padding(top = SpaceMedium)
                    .fillMaxSize()
            ) {
                if(state.userItems.isEmpty() && searchFieldState.text.isNotEmpty() && !state.isLoading) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.no_user_found),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(modifier = Modifier.height(SpaceMedium))
                        Text(
                            text = stringResource(R.string.no_user_found),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
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
                                    keyboardController?.hide()
                                    onNavigate(Screen.ProfileScreen.route + "?userId=${user.userId}")
                                }
                            )
                        }
                    }
                }
            }
        }
        if(state.isLoading) {
            CustomCircularProgressIndicator(
                modifier = Modifier.align(Center)
            )
        }
    }
}