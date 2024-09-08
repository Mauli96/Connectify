package com.example.connectify.core.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.connectify.R
import com.example.connectify.core.presentation.ui.theme.IconSizeSmall
import com.example.connectify.core.presentation.ui.theme.SpaceLarge
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardBottomSheet(
    title: String,
    bottomSheetState: SheetState,
    onDismissRequest: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest()
        },
        sheetState = bottomSheetState,
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(SpaceMedium))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(
                        height = 50.dp,
                        width = 100.dp
                    )
                    .clickable {
                        onDeleteClick()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(SpaceMedium))
                Icon(
                    painter = painterResource(id = R.drawable.delete_icon),
                    contentDescription = null,
                    modifier = Modifier.size(IconSizeSmall),
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.width(SpaceSmall))
                Text(
                    text = stringResource(id = R.string.delete),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 18.sp
                    )
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(
                        height = 50.dp,
                        width = 100.dp
                    )
                    .clickable {
                        onCancelClick()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(SpaceMedium))
                Icon(
                    painter = painterResource(id = R.drawable.cancel_icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = stringResource(id = R.string.cancel),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 18.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(SpaceLarge))
        }
    }
}