package com.connectify.android.feature_profile.presentation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.connectify.android.R
import com.connectify.android.core.domain.states.StandardTextFieldState
import com.connectify.android.core.presentation.components.ConnectivityBanner
import com.connectify.android.core.presentation.components.CustomCircularProgressIndicator
import com.connectify.android.core.presentation.components.StandardSearchField
import com.connectify.android.core.presentation.components.UserProfileItem
import com.connectify.android.core.presentation.ui.theme.IconSizeSmall
import com.connectify.android.core.presentation.ui.theme.LottieIconSize
import com.connectify.android.core.presentation.ui.theme.SpaceLargeExtra
import com.connectify.android.core.presentation.ui.theme.SpaceMedium
import com.connectify.android.core.presentation.ui.theme.SpaceSmall
import com.connectify.android.core.presentation.ui.theme.Typography
import com.connectify.android.core.presentation.util.ObserveAsEvents
import com.connectify.android.core.presentation.util.UiEvent
import com.connectify.android.core.presentation.util.asString
import com.connectify.android.core.presentation.util.showKeyboard
import com.connectify.android.core.util.Screen

@ExperimentalMaterialApi
@Composable
fun SearchScreen(
    imageLoader: ImageLoader,
    onNavigate: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    snackbarHostState: SnackbarHostState,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchFieldState by viewModel.searchFieldState.collectAsStateWithLifecycle()
    val networkState by viewModel.networkState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    val focusRequester = remember { FocusRequester() }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_result_found))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    LaunchedEffect(key1 = true) {
        context.showKeyboard()
        focusRequester.requestFocus()
    }

    ObserveAsEvents(viewModel.eventFlow) { event ->
        when(event) {
            is UiEvent.ShowSnackbar -> {
                snackbarHostState.showSnackbar(
                    message = event.uiText.asString(context)
                )
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            SearchHeader(
                state = searchFieldState,
                onNavigateUp = {
                    keyboardController?.hide()
                    onNavigateUp()
                },
                focusRequester = focusRequester,
                onQueryChanged = { viewModel.onEvent(SearchEvent.OnQuery(it)) },
                onClearSearch = { viewModel.onEvent(SearchEvent.OnToggleSearch("")) }
            )
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                if(state.userItems.isEmpty() && searchFieldState.text.isNotEmpty() && !state.isLoading) {
                    NoResultsFound(
                        composition = composition,
                        progress = progress
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = SpaceMedium)
                    ) {
                        items(state.userItems) { user ->
                            UserProfileItem(
                                user = user,
                                imageLoader = imageLoader,
                                modifier = Modifier.fillMaxWidth(),
                                isFollowing = user.isFollowing,
                                onActionItemClick = {
                                    viewModel.onEvent(SearchEvent.OnToggleFollow(user.userId))
                                },
                                onItemClick = {
                                    keyboardController?.hide()
                                    onNavigate(Screen.ProfileScreen.route + "?userId=${user.userId}")
                                }
                            )
                        }
                    }
                }
                ConnectivityBanner(
                    networkState = networkState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
        if(state.isLoading) {
            CustomCircularProgressIndicator(
                modifier = Modifier.align(Center)
            )
        }
    }
}

@Composable
private fun SearchHeader(
    state: StandardTextFieldState,
    onNavigateUp: () -> Unit,
    focusRequester: FocusRequester,
    onQueryChanged: (String) -> Unit,
    onClearSearch: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = SpaceMedium, end = SpaceSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onNavigateUp
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = stringResource(R.string.navigate_back),
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(IconSizeSmall)
            )
        }
        Spacer(modifier = Modifier.width(SpaceSmall))
        StandardSearchField(
            query = state.text,
            onQueryChanged = onQueryChanged,
            focusRequester = focusRequester,
            placeholderText = stringResource(id = R.string.search_for_user),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier.size(IconSizeSmall)
                )
            },
            trailingIcon = {
                if(state.text.isNotEmpty()) {
                    IconButton(
                        onClick = onClearSearch
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_cancel),
                            contentDescription = stringResource(R.string.clear),
                            tint = MaterialTheme.colorScheme.background,
                            modifier = Modifier.size(IconSizeSmall)
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun NoResultsFound(
    composition: LottieComposition?,
    progress: Float
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = SpaceLargeExtra),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            modifier = Modifier.size(LottieIconSize),
            composition = composition,
            progress = { progress }
        )
        Text(
            text = stringResource(R.string.no_user_found),
            style = Typography.labelSmall,
            textAlign = TextAlign.Center
        )
    }
}