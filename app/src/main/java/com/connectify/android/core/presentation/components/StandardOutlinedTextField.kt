package com.connectify.android.core.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.connectify.android.core.presentation.ui.theme.TextWhite
import com.connectify.android.core.presentation.ui.theme.Typography
import com.connectify.android.core.presentation.ui.theme.withColor
import com.connectify.android.core.presentation.ui.theme.withSize

@Composable
fun StandardOutlinedTextField(
    modifier: Modifier = Modifier,
    text: String = "",
    maxLength: Int = 400,
    error: String = "",
    singleLine: Boolean = true,
    maxLines: Int = 1,
    minLines: Int = 1,
    onValueChange: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        TextField(
            value = text,
            onValueChange = {
                if(it.length <= maxLength) {
                    onValueChange(it)
                }
            },
            maxLines = maxLines,
            minLines = minLines,
            textStyle = Typography.labelMedium.withColor(TextWhite),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                disabledTextColor = MaterialTheme.colorScheme.onBackground,
                errorTextColor = MaterialTheme.colorScheme.onBackground,
                cursorColor = MaterialTheme.colorScheme.onBackground,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                focusedBorderColor =  MaterialTheme.colorScheme.primary,
                unfocusedBorderColor =  Color.DarkGray,
            ),
            isError = error != "",
            singleLine = singleLine,
            modifier = modifier.fillMaxWidth()
        )
        if(error.isNotEmpty()) {
            Text(
                text = error,
                style = Typography.labelSmall.withSize(12.sp),
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}