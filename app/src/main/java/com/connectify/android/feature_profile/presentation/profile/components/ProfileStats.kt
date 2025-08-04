package com.connectify.android.feature_profile.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.connectify.android.R
import com.connectify.android.core.domain.models.User
import com.connectify.android.core.presentation.ui.theme.DarkGray
import com.connectify.android.core.presentation.ui.theme.SpaceLarge
import com.connectify.android.core.presentation.ui.theme.SpaceMedium
import com.connectify.android.core.presentation.ui.theme.SpaceSmall
import com.connectify.android.core.presentation.ui.theme.Typography
import com.connectify.android.core.presentation.ui.theme.withColor
import com.connectify.android.core.presentation.ui.theme.withSize

@Composable
fun ProfileStats(
    user: User,
    modifier: Modifier = Modifier,
    isOwnProfile: Boolean = true,
    isFollowing: Boolean = true,
    onFollowingClick: () -> Unit = {},
    onFollowerClick: () -> Unit = {},
    onFollowClick: () -> Unit = {},
    onMessageClick: () -> Unit = {}
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row {
            ProfileNumber(
                number = user.followerCount,
                text = stringResource(id = R.string.followers),
                onClick = onFollowerClick
            )
            Spacer(modifier = Modifier.width(SpaceLarge))
            ProfileNumber(
                number = user.followingCount,
                text = stringResource(id = R.string.following),
                onClick = onFollowingClick
            )
            Spacer(modifier = Modifier.width(SpaceLarge))
            ProfileNumber(
                number = user.postCount,
                text = stringResource(id = R.string.posts)
            )
        }
        Spacer(modifier = Modifier.height(SpaceMedium))
        Row {
            if(!isOwnProfile) {
                Button(
                    onClick = onFollowClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if(isFollowing) {
                            Color.White
                        } else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .height(30.dp)
                        .width(150.dp),
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
                Spacer(modifier = Modifier.width(SpaceSmall))
                Button(
                    onClick = onMessageClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    modifier = Modifier
                        .height(30.dp)
                        .width(150.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = stringResource(id = R.string.message),
                        style = Typography.labelMedium
                            .withSize(12.sp)
                            .withColor(DarkGray)
                    )
                }
            }
        }
    }
}