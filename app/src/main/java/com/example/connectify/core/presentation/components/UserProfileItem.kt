package com.example.connectify.core.presentation.components

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import com.example.connectify.R
import com.example.connectify.core.domain.models.UserItem
import com.example.connectify.core.presentation.ui.theme.DarkGray
import com.example.connectify.core.presentation.ui.theme.ProfilePictureSizeSmall
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.core.presentation.ui.theme.Typography
import com.example.connectify.core.presentation.ui.theme.withColor
import com.example.connectify.core.presentation.ui.theme.withSize


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserProfileItem(
    user: UserItem,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
    isFollowing: Boolean = false,
    onItemClick: () -> Unit = {},
    onActionItemClick: () -> Unit = {},
    ownUserId: String = ""
) {
    Card(
        modifier = modifier,
        backgroundColor = MaterialTheme.colorScheme.background,
        onClick = onItemClick,
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
            AsyncImage(
                model = user.profilePictureUrl,
                imageLoader = imageLoader,
                contentDescription = stringResource(R.string.profile_image),
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
                    style = Typography.labelLarge
                )
                if(user.bio.isNotBlank()) {
                    Spacer(modifier = Modifier.height(SpaceSmall))
                    Text(
                        text = user.bio,
                        style = Typography.labelSmall.withSize(12.sp),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        modifier = Modifier.heightIn(
                            min = 12.sp.value.dp * 2.5f
                        )
                    )
                }
            }
            if(ownUserId != user.userId) {
                Button(
                    onClick = onActionItemClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if(isFollowing) {
                            Color.White
                        } else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .height(30.dp)
                        .width(100.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = if(isFollowing) {
                            stringResource(id = R.string.unfollow)
                        } else stringResource(id = R.string.follow),
                        style = Typography.labelMedium
                            .withSize(12.sp)
                            .withColor(DarkGray)
                    )
                }
            }
        }
    }
}