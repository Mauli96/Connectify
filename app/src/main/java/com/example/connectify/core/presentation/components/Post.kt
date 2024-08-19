package com.example.connectify.core.presentation.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import com.example.connectify.R
import com.example.connectify.core.domain.models.Post
import com.example.connectify.core.presentation.ui.theme.DarkGray
import com.example.connectify.core.presentation.ui.theme.HintGray
import com.example.connectify.core.presentation.ui.theme.IconSizeSmall
import com.example.connectify.core.presentation.ui.theme.ProfilePictureSizeExtraSmall
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.core.util.Constants
import com.example.connectify.core.util.vibrate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun Post(
    post: Post,
    context: Context,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
    scope: CoroutineScope = rememberCoroutineScope(),
    onPostClick: () -> Unit = {},
    onLikeClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onUsernameClick: () -> Unit = {},
    onLongPress: (String) -> Unit = {}
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkGray)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            onPostClick()
                        },
                        onLongPress = {
                            scope.launch {
                                if(post.isOwnPost) {
                                    onLongPress(post.id)
                                    vibrate(context)
                                }
                            }
                        }
                    )
                }
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = post.imageUrl,
                    imageLoader = imageLoader
                ),
                contentDescription = stringResource(id = R.string.post_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceMedium)
            ) {
                ActionRow(
                    username = post.username,
                    profilePictureUrl = post.profilePictureUrl,
                    imageLoader = imageLoader,
                    modifier = Modifier.fillMaxWidth(),
                    isLiked = post.isLiked,
                    onLikeClick = onLikeClick,
                    onCommentClick = onCommentClick,
                    onShareClick = onShareClick,
                    onUsernameClick = onUsernameClick,
                )
                Spacer(modifier = Modifier.height(SpaceSmall))
                Text(
                    text = buildAnnotatedString {
                        append(post.description)
                        withStyle(SpanStyle(
                            color = HintGray
                        )) {
                            append(
                                " " + LocalContext.current.getString(
                                    R.string.read_more
                                )
                            )
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = Constants.MAX_POST_DESCRIPTION_LINES
                )
                Spacer(modifier = Modifier.height(SpaceSmall))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.liked_by_x_people,
                            post.likeCount
                        ),
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = stringResource(
                            id = R.string.x_comments,
                            post.commentCount
                        ),
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
fun ActionRow(
    modifier: Modifier = Modifier,
    profilePictureUrl: String,
    imageLoader: ImageLoader,
    isLiked: Boolean = false,
    onLikeClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    username: String,
    onUsernameClick: () -> Unit = {}
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = profilePictureUrl,
                    imageLoader = imageLoader
                ),
                contentDescription = stringResource(id = R.string.profile_image),
                modifier = Modifier
                    .size(ProfilePictureSizeExtraSmall)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .width(120.dp)
            ) {
                Text(
                    text = username,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .clickable {
                            onUsernameClick()
                        }
                )
            }
        }
        EngagementButtons(
            isLiked = isLiked,
            onLikeClick = onLikeClick,
            onCommentClick = onCommentClick,
            onShareClick = onShareClick,
        )
    }
}

@Composable
fun EngagementButtons(
    modifier: Modifier = Modifier,
    isLiked: Boolean = false,
    iconSize: Dp = 30.dp,
    onLikeClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LikeButton(
            isLiked = isLiked,
            onLikeClick = onLikeClick
        )
        Spacer(modifier = Modifier.width(SpaceMedium))
        IconButton(
            onClick = {
                onCommentClick()
            },
            modifier = Modifier.size(iconSize)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.comment_icon),
                contentDescription = stringResource(id = R.string.comment),
                modifier = Modifier.size(IconSizeSmall)
            )
        }
        Spacer(modifier = Modifier.width(SpaceMedium))
        IconButton(onClick = {
            onShareClick()
        },
            modifier = Modifier.size(iconSize)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.share_icon),
                contentDescription = stringResource(id = R.string.share),
                modifier = Modifier.size(IconSizeSmall)
            )
        }
    }
}