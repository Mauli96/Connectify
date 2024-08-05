package com.example.connectify.feature_chat.presentation.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import com.example.connectify.core.presentation.ui.theme.ProfilePictureSizeSmall
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.feature_chat.domain.model.Chat
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatItem(
    item: Chat,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
    onItemClick: (Chat) -> Unit,
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        backgroundColor = MaterialTheme.colorScheme.surface,
        onClick = {
            onItemClick(item)
        },
        elevation = 5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    vertical = SpaceSmall,
                    horizontal = SpaceMedium
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = item.remoteUserProfilePictureUrl,
                    imageLoader = imageLoader
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(ProfilePictureSizeSmall)
                    .clip(CircleShape)
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = SpaceSmall)
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = item.remoteUsername,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(SpaceSmall))
                    Text(
                        text = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                            .format(item.timestamp)
                    )
                }
                Spacer(modifier = Modifier.height(SpaceSmall))
                Text(
                    text = item.lastMessage,
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.heightIn(
                        min = MaterialTheme.typography.bodySmall.fontSize.value.dp * 2.5f
                    )
                )
            }
        }
    }
}