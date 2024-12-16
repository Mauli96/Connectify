package com.example.connectify.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.connectify.R
import com.example.connectify.core.domain.states.StandardTextFieldState
import com.example.connectify.core.presentation.ui.theme.HintGray
import com.example.connectify.core.presentation.ui.theme.IconSizeMedium
import com.example.connectify.core.presentation.ui.theme.SpaceLargeExtra
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.core.util.Constants
import com.example.connectify.feature_post.presentation.util.CommentFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> PaginatedBottomSheet(
    title: String,
    bottomSheetState: SheetState,
    onDismissBottomSheet: () -> Unit = {},
    items: List<T> = emptyList(),
    isFirstLoading: Boolean = false,
    isNextLoading: Boolean = false,
    endReached: Boolean = false,
    loadNextPage: () -> Unit = {},
    selectedFilter: CommentFilter = CommentFilter.MOST_RECENT,
    isDropdownMenuExpanded: Boolean = false,
    onFilterSelected: (CommentFilter) -> Unit = {},
    onShowDropDownMenu: () -> Unit = {},
    onDismissDropdownMenu: () -> Unit = {},
    keyExtractor: (T) -> String,
    textFieldState: StandardTextFieldState,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    ownProfilePicture: String,
    hint: String = "",
    isUploading: Boolean = false,
    focusRequester: FocusRequester = FocusRequester(),
    itemContent: @Composable LazyItemScope.(index: Int, item: T) -> Unit
) {

    val lazyListState = rememberLazyListState()

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_comments_found))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    ModalBottomSheet(
        onDismissRequest = {
            onDismissBottomSheet()
        },
        sheetState = bottomSheetState,
        shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        ),
        dragHandle = {},
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(700.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_line),
                    contentDescription = null,
                    modifier = Modifier.size(IconSizeMedium),
                    tint = Color.Gray
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(SpaceSmall))
                HorizontalDivider(
                    modifier = Modifier.height(1.dp),
                    thickness = 0.2.dp,
                    color = HintGray
                )
                Spacer(modifier = Modifier.height(SpaceSmall))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    if(items.isEmpty() && !isFirstLoading && !isNextLoading) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = SpaceLargeExtra),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LottieAnimation(
                                modifier = Modifier.size(100.dp),
                                composition = composition,
                                progress = {
                                    progress
                                },
                            )
                            Text(
                                text = stringResource(R.string.no_comments_yet),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            state = lazyListState,
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            item {
                                if(items.isNotEmpty()) {
                                    CommentFilterDropdown(
                                        expanded = isDropdownMenuExpanded,
                                        onShowDropDownMenu = {
                                            onShowDropDownMenu()
                                        },
                                        onDismissDropdownMenu = {
                                            onDismissDropdownMenu()
                                        },
                                        selectedFilter = selectedFilter,
                                        onFilterSelected = { filterType ->
                                            onFilterSelected(filterType)
                                        }
                                    )
                                }
                            }
                            items(
                                count = items.size,
                                key = { i ->
                                    keyExtractor(items[i])
                                }
                            ) { index ->
                                val item = items[index]
                                if(index >= items.size - 1 && items.size >= Constants.DEFAULT_PAGE_SIZE
                                    && !endReached && !isFirstLoading && !isNextLoading) {
                                    loadNextPage()
                                }
                                itemContent(index, item)
                            }
                            if(isNextLoading) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = SpaceMedium),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CustomCircularProgressIndicator()
                                    }
                                }
                            }
                        }
                    }
                }
                SendTextField(
                    state = textFieldState,
                    onValueChange = onValueChange,
                    onSend = onSend,
                    ownProfilePicture = ownProfilePicture,
                    hint = hint,
                    isLoading = isUploading,
                    focusRequester = focusRequester,
                )
            }
            if(isFirstLoading) {
                CustomCircularProgressIndicator(
                    modifier = Modifier.align(Center)
                )
            }
        }
    }
}