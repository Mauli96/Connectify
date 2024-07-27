package com.example.connectify.core.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.connectify.R
import com.example.connectify.core.domain.models.User
import com.example.connectify.core.domain.models.UserItem
import com.example.connectify.core.presentation.ui.theme.IconSizeMedium
import com.example.connectify.core.presentation.ui.theme.ProfilePictureSizeSmall
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserProfileItem(
    user: UserItem,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
    actionIcon: @Composable () -> Unit = {},
    onItemClick: () -> Unit = {},
    onActionItemClick: () -> Unit = {},
    ownUserId: String = ""
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        backgroundColor = MaterialTheme.colorScheme.surface,
        onClick = onItemClick,
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
                    model = user.profilePictureUrl,
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
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(SpaceSmall))
                Text(
                    text = user.bio,
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
            }
            if(ownUserId != user.userId) {
                IconButton(
                    onClick = onActionItemClick,
                    modifier = Modifier.size(IconSizeMedium)

                ) {
                    actionIcon()
                }
            }
        }
    }
}