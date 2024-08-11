package com.example.connectify.feature_profile.presentation.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.connectify.R
import com.example.connectify.core.domain.models.User
import com.example.connectify.core.presentation.ui.theme.SpaceLarge
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall

@Composable
fun ProfileHeaderSection(
    user: User,
    modifier: Modifier = Modifier,
    isOwnProfile: Boolean = true,
    isFollowing: Boolean = true,
    onFollowClick: () -> Unit = {},
    onMessageClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = user.username,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp
                ),
                textAlign = TextAlign.Center,
            )
            if(!isOwnProfile) {
                Spacer(modifier = Modifier.width(SpaceSmall))
                IconButton(
                    onClick = onMessageClick,
                    modifier = Modifier.size(30.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.message_icon_profile),
                        contentDescription = stringResource(id = R.string.message),
                        modifier = modifier.size(22.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(SpaceMedium))
        if(user.description.isNotBlank()) {
            Text(
                text = user.description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(SpaceLarge))
        }
        ProfileStats(
            user = user,
            isOwnProfile = isOwnProfile,
            isFollowing = isFollowing,
            onFollowClick = onFollowClick
        )
    }
}