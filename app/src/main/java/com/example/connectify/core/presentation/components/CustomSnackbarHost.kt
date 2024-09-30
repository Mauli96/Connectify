package com.example.connectify.core.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import kotlinx.coroutines.delay

@Composable
fun CustomSnackbarHost(
    snackbarHostState: SnackbarHostState
) {

    val currentSnackbarData by rememberUpdatedState(
        newValue = snackbarHostState.currentSnackbarData
    )

    var isVisible by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(currentSnackbarData) {
        if(currentSnackbarData != null) {
            isVisible = true
            delay(3000)
            isVisible = false
            delay(3000)
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        currentSnackbarData?.let { snackbarData ->
            Snackbar(
                modifier = Modifier.padding(SpaceMedium),
                shape = MaterialTheme.shapes.medium,
                content = {
                    Text(
                        text = snackbarData.message,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            )
        }
    }
}