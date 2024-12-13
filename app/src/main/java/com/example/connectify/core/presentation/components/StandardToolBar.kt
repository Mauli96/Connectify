package com.example.connectify.core.presentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.connectify.R
import com.example.connectify.core.presentation.ui.theme.IconSizeSmall


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardToolbar(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit = {},
    showBackArrow: Boolean = false,
    title: @Composable () -> Unit = {},
    navActions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = {
            if(showBackArrow) {
                IconButton(
                    onClick = {
                        onNavigateUp()
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = stringResource(id = R.string.back),
                        modifier = Modifier.size(IconSizeSmall)
                    )
                }
            } else null
        },
        actions = navActions,
        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}