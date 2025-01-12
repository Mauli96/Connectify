package com.example.connectify.core.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.example.connectify.core.presentation.ui.theme.HintGray
import com.example.connectify.core.presentation.ui.theme.IconSizeMedium
import com.example.connectify.core.presentation.ui.theme.IconSizeSmall
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.core.presentation.ui.theme.Typography
import com.example.connectify.core.presentation.ui.theme.withColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardMessageSheet(
    bottomSheetState: SheetState,
    showDownloadOption: Boolean = false,
    showCopyOption: Boolean = false,
    showDeleteOption: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onDownloadClick: () -> Unit = {},
    onCopiedClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest()
        },
        sheetState = bottomSheetState,
        shape = MaterialTheme.shapes.large,
        dragHandle = {},
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(showDownloadOption) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(
                            height = 50.dp,
                            width = 100.dp
                        )
                        .clickable {
                            onDownloadClick()
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(SpaceMedium))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_download),
                        contentDescription = null,
                        modifier = Modifier.size(IconSizeSmall),
                    )
                    Spacer(modifier = Modifier.width(SpaceSmall))
                    Text(
                        text = stringResource(id = R.string.download),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 18.sp
                        )
                    )
                }
            }
            if(showCopyOption) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(
                            height = 50.dp,
                            width = 100.dp
                        )
                        .clickable {
                            onCopiedClick()
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(SpaceMedium))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_copy),
                        contentDescription = null,
                        modifier = Modifier.size(IconSizeSmall),
                    )
                    Spacer(modifier = Modifier.width(SpaceSmall))
                    Text(
                        text = stringResource(id = R.string.copy),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 18.sp
                        )
                    )
                }
            }

            if(showDeleteOption) {
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
                    Image(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = null,
                        modifier = Modifier.size(IconSizeSmall)
                    )
                    Spacer(modifier = Modifier.width(SpaceSmall))
                    Text(
                        text = stringResource(id = R.string.delete),
                        style = Typography.bodySmall.withColor(Color.Red)
                    )
                }
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
                    painter = painterResource(id = R.drawable.ic_cancel),
                    contentDescription = null,
                    modifier = Modifier.size(IconSizeSmall)
                )
                Spacer(modifier = Modifier.width(SpaceSmall))
                Text(
                    text = stringResource(id = R.string.cancel),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 18.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}