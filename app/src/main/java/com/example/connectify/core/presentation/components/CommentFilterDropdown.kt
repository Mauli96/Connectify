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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
    selectedFilter: CommentFilter,
    onShowDropDownMenu: () -> Unit,
    onDismissDropdownMenu: () -> Unit,
    onFilterSelected: (CommentFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier.padding(SpaceSmall)
    ) {
        FilterHeader(
            expanded = expanded,
            selectedFilter = selectedFilter,
            onClick = onShowDropDownMenu
        )

        FilterDropdownMenu(
            expanded = expanded,
            selectedFilter = selectedFilter,
            onDismissRequest = onDismissDropdownMenu,
            onFilterSelected = { filter ->
                onDismissDropdownMenu()
                scope.launch {
                    delay(200) // Animation delay
                    onFilterSelected(filter)
                }
            }
        )
    }
}

@Composable
private fun FilterHeader(
    expanded: Boolean,
    selectedFilter: CommentFilter,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .wrapContentSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onClick()
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
            painter = painterResource(
                if(expanded) {
                    R.drawable.ic_drop_down_up
                } else R.drawable.ic_drop_down
            ),
            contentDescription = stringResource(id = R.string.filter_comments),
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(IconSizeSmall)
        )
    }
}

@Composable
private fun FilterDropdownMenu(
    expanded: Boolean,
    selectedFilter: CommentFilter,
    onDismissRequest: () -> Unit,
    onFilterSelected: (CommentFilter) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
            .shadow(elevation = 30.dp, shape = MaterialTheme.shapes.extraLarge)
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            offset = DpOffset(0.dp, 26.dp)
        ) {
            CommentFilter.entries.forEach { filter ->
                FilterMenuItem(
                    filter = filter,
                    isSelected = filter == selectedFilter,
                    onClick = { onFilterSelected(filter) }
                )
            }
        }
    }
}

@Composable
private fun FilterMenuItem(
    filter: CommentFilter,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val stringResId = when(filter) {
        CommentFilter.MOST_RECENT -> R.string.most_recent_comments
        CommentFilter.MOST_OLD -> R.string.most_old_commets
        CommentFilter.MOST_POPULAR -> R.string.most_popular_comments
    }

    DropdownMenuItem(
        text = {
            Text(
                text = stringResource(id = stringResId),
                style = if(isSelected) {
                    MaterialTheme.typography.labelLarge
                } else {
                    MaterialTheme.typography.bodyLarge
                }
            )
        },
        onClick = onClick,
        modifier = Modifier.background(
            if(isSelected) MaterialTheme.colorScheme.primary
            else Color.Transparent
        )
    )
}