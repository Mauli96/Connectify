package com.example.connectify.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.example.connectify.core.presentation.ui.theme.DarkGray
import com.example.connectify.core.presentation.ui.theme.Typography
import com.example.connectify.core.presentation.ui.theme.withColor
import com.example.connectify.feature_profile.domain.util.ProfileConstants

@Composable
fun StandardSearchField(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChanged: (String) -> Unit,
    placeholderText: String,
    focusRequester: FocusRequester,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null
) {

    BasicTextField(
        value = query,
        onValueChange =  {
            if(it.length <= ProfileConstants.MAX_LENGTH) {
                onQueryChanged(it)
            }
        },
        textStyle = Typography.labelMedium.withColor(DarkGray),
        singleLine = true,
        modifier = modifier
            .height(40.dp)
            .focusRequester(focusRequester = focusRequester)
            .background(
                color = MaterialTheme.colorScheme.onBackground,
                shape = MaterialTheme.shapes.medium
            ),
        decorationBox = { innerTextField ->
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(leadingIcon != null) {
                    Spacer(modifier = Modifier.width(12.dp))
                    leadingIcon()
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    if(query.isEmpty()) {
                        Text(
                            text = placeholderText,
                            style = Typography.labelMedium.withColor(DarkGray)
                        )
                    }
                    innerTextField()
                }
                if(trailingIcon != null) trailingIcon()
            }
        }
    )
}