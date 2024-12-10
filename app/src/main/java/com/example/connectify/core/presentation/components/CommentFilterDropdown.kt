package com.example.connectify.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Icon
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.connectify.R
import com.example.connectify.core.presentation.ui.theme.IconSizeSmall
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.feature_post.presentation.util.CommentFilter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun CommentFilterDropdown(
    expanded: Boolean,
    onShowDropDownMenu: () -> Unit,
    onDismissDropdownMenu: () -> Unit,
    selectedFilter: CommentFilter,
    onFilterSelected: (CommentFilter) -> Unit
) {

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .padding(
                horizontal = SpaceSmall,
                vertical = SpaceSmall
            )
    ) {
        Row(
            modifier = Modifier
                .wrapContentSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            onShowDropDownMenu()
                        }
                    )
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(
                    id = when(selectedFilter) {
                        CommentFilter.MOST_RECENT -> R.string.most_recent_comments
                        CommentFilter.MOST_OLD -> R.string.most_old_commets
                        CommentFilter.MOST_POPULAR -> R.string.most_popular_comments
                    }
                ),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            Icon(
                painter = if(expanded) {
                    painterResource(R.drawable.ic_drop_down_up)
                } else {
                    painterResource(R.drawable.ic_drop_down)
                },
                contentDescription = stringResource(id = R.string.filter_comments),
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(IconSizeSmall)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.TopStart)
                .shadow(30.dp, MaterialTheme.shapes.extraLarge)
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    onDismissDropdownMenu()
                },
                offset = DpOffset(0.dp, 26.dp)
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(id = R.string.most_recent_comments),
                            style = if(selectedFilter == CommentFilter.MOST_RECENT) {
                                MaterialTheme.typography.labelLarge
                            } else {
                                MaterialTheme.typography.bodyLarge
                            }
                        )
                    },
                    onClick = {
                        onDismissDropdownMenu()
                        coroutineScope.launch {
                            delay(200)
                            onFilterSelected(CommentFilter.MOST_RECENT)
                        }
                    },
                    modifier = Modifier
                        .background(
                            if(selectedFilter == CommentFilter.MOST_RECENT) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Transparent
                            }
                        )
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(id = R.string.most_old_commets),
                            style = if(selectedFilter == CommentFilter.MOST_OLD) {
                                MaterialTheme.typography.labelLarge
                            } else {
                                MaterialTheme.typography.bodyLarge
                            }
                        )
                    },
                    onClick = {
                        onDismissDropdownMenu()
                        coroutineScope.launch {
                            delay(200)
                            onFilterSelected(CommentFilter.MOST_OLD)
                        }
                    },
                    modifier = Modifier
                        .background(
                            if(selectedFilter == CommentFilter.MOST_OLD) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Transparent
                            }
                        )
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(id = R.string.most_popular_comments),
                            style = if(selectedFilter == CommentFilter.MOST_POPULAR) {
                                MaterialTheme.typography.labelLarge
                            } else {
                                MaterialTheme.typography.bodyLarge
                            }
                        )
                    },
                    onClick = {
                        onDismissDropdownMenu()
                        coroutineScope.launch {
                            delay(200)
                            onFilterSelected(CommentFilter.MOST_POPULAR)
                        }
                    },
                    modifier = Modifier
                        .background(
                            if(selectedFilter == CommentFilter.MOST_POPULAR) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Transparent
                            }
                        )
                )
            }
        }
    }
}