package com.example.connectify.feature_activity.presentation.components

import android.content.Context
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import com.example.connectify.R
import com.example.connectify.core.domain.models.Activity
import com.example.connectify.core.presentation.ui.theme.*
import com.example.connectify.core.util.Screen
import com.example.connectify.feature_activity.presentation.util.ActivityType

@Composable
fun ActivityItem(
    modifier: Modifier = Modifier,
    imageLoader: ImageLoader,
    activity: Activity,
    onNavigate: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val(fillerText, actionText) = remember(activity) {
        generateActivityText(context, activity)
    }

    Card(
        modifier = modifier
            .padding(horizontal = SpaceSmall)
            .height(60.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
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

            val annotatedText = buildAnnotatedString {
                val boldStyle = SpanStyle(
                    fontFamily = Quicksand,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = TextWhite
                )

                pushStringAnnotation(tag = "username", annotation = "username")
                withStyle(boldStyle) {
                    append(activity.username)
                }
                pop()
                append(" $fillerText ")

                pushStringAnnotation(tag = "parent", annotation = "parent")
                withStyle(boldStyle) {
                    append(actionText)
                }
            }

            var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

            Text(
                text = annotatedText,
                style = Typography.labelMedium
                    .withSize(14.sp)
                    .withColor(TextWhite),
                modifier = Modifier
                    .weight(1f)
                    .pointerInput(Unit) {
                        detectTapGestures { position ->
                            layoutResult?.let { result ->
                                val offset = result.getOffsetForPosition(position)
                                annotatedText.getStringAnnotations(
                                    tag = "username",
                                    start = offset,
                                    end = offset
                                ).firstOrNull()?.let {
                                    onNavigate(Screen.ProfileScreen.route + "?userId=${activity.userId}")
                                }
                                annotatedText.getStringAnnotations(
                                    tag = "parent",
                                    start = offset,
                                    end = offset
                                ).firstOrNull()?.let {
                                    onNavigate(Screen.PostDetailScreen.route + "/${activity.parentId}")
                                }
                            }
                        }
                    },
                onTextLayout = { layoutResult = it }
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

private fun generateActivityText(context: Context, activity: Activity): Pair<String, String> {
    return when(activity.activityType) {
        is ActivityType.LikedPost -> Pair(
            context.getString(R.string.liked),
            context.getString(R.string.your_post)
        )
        is ActivityType.CommentedOnPost -> Pair(
            context.getString(R.string.commented_on),
            context.getString(R.string.your_post)
        )
        is ActivityType.FollowedUser -> Pair(
            context.getString(R.string.followed_you),
            ""
        )
        is ActivityType.LikedComment -> Pair(
            context.getString(R.string.liked),
            context.getString(R.string.your_comment)
        )
    }
}