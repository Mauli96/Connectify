package com.example.connectify.feature_activity.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import com.example.connectify.R
import com.example.connectify.core.domain.models.Activity
import com.example.connectify.core.presentation.ui.theme.ProfilePictureSizeMediumSmall
import com.example.connectify.core.presentation.ui.theme.Quicksand
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.core.presentation.ui.theme.TextWhite
import com.example.connectify.core.presentation.ui.theme.Typography
import com.example.connectify.core.presentation.ui.theme.withColor
import com.example.connectify.core.presentation.ui.theme.withSize
import com.example.connectify.core.util.Screen
import com.example.connectify.feature_activity.presentation.util.ActivityType

@Composable
fun ActivityItem(
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader,
    activity: Activity,
    onNavigate: (String) -> Unit = {}
) {
    Card(
        modifier = modifier
            .padding(
                start = SpaceSmall,
                end = SpaceSmall
            )
            .height(60.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = activity.profilePictureUrl,
                imageLoader = imageLoader,
                contentDescription = stringResource(R.string.profile_image),
                modifier = Modifier
                    .size(ProfilePictureSizeMediumSmall)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            val fillerText = when(activity.activityType) {
                is ActivityType.LikedPost ->
                    stringResource(id = R.string.liked)
                is ActivityType.CommentedOnPost ->
                    stringResource(id = R.string.commented_on)
                is ActivityType.FollowedUser ->
                    stringResource(id = R.string.followed_you)
                is ActivityType.LikedComment -> {
                    stringResource(id = R.string.liked)
                }
            }
            val actionText = when(activity.activityType) {
                is ActivityType.LikedPost ->
                    stringResource(id = R.string.your_post)
                is ActivityType.CommentedOnPost ->
                    stringResource(id = R.string.your_post)
                is ActivityType.FollowedUser -> ""
                is ActivityType.LikedComment -> {
                    stringResource(id = R.string.your_comment)
                }
            }
            val annotatedText = buildAnnotatedString {
                val boldStyle = SpanStyle(
                    fontFamily = Quicksand,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = TextWhite
                )
                pushStringAnnotation(
                    tag = "username",
                    annotation = "username"
                )
                withStyle(boldStyle) {
                    append(activity.username)
                }
                pop()
                append(" $fillerText ")

                pushStringAnnotation(
                    tag = "parent",
                    annotation = "parent"
                )
                withStyle(boldStyle) {
                    append(actionText)
                }
            }
            ClickableText(
                text = annotatedText,
                style = Typography.labelMedium
                    .withSize(14.sp)
                    .withColor(TextWhite),
                modifier = Modifier.weight(1f),
                onClick = { offset ->
                    annotatedText.getStringAnnotations(
                        tag = "username",
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let { annotation ->
                        // Clicked on user
                        onNavigate(
                            Screen.ProfileScreen.route + "?userId=${activity.userId}"
                        )
                    }
                    annotatedText.getStringAnnotations(
                        tag = "parent",
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let { annotation ->
                        // Clicked on parent
                        onNavigate(
                            Screen.PostDetailScreen.route + "/${activity.parentId}"
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            Text(
                text = activity.formattedTime,
                textAlign = TextAlign.Right,
                style = Typography.labelSmall.withSize(12.sp)
            )
        }
    }
}