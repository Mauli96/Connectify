package com.example.connectify.core.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.connectify.core.presentation.ui.theme.SpaceMediumLarge
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.core.util.Constants

@Composable
fun Post(
    post: Post,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
    onLikeClick: () -> Unit = {},
    onLikedByClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onUsernameClick: () -> Unit = {},
    onMoreItemClick: () -> Unit = {},
    isDescriptionVisible: Boolean,
    onDescriptionToggle: () -> Unit = {},
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkGray)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                    start = SpaceSmall,
                    end = SpaceSmall,
                    bottom = SpaceMedium
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = post.profilePictureUrl,
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
                            .width(150.dp)
                    ) {
                        Text(
                            text = post.username,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            onUsernameClick()
                                        }
                                    )
                                }
                        )
                    }
                }
                Icon(
                    painter = painterResource(R.drawable.ic_more_item),
                    contentDescription = stringResource(R.string.more_items),
                    modifier = Modifier
                        .size(IconSizeSmall)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    onMoreItemClick()
                                }
                            )
                        }
                )
            }
            Image(
                painter = rememberAsyncImagePainter(
                    model = post.imageUrl,
                    imageLoader = imageLoader
                ),
                contentDescription = stringResource(id = R.string.post_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 5f)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceMedium)
            ) {
                ActionRow(
                    modifier = Modifier.fillMaxWidth(),
                    isLiked = post.isLiked,
                    isSaved = post.isSaved,
                    likeCount = post.likeCount,
                    commentCount = post.commentCount,
                    onLikeClick = onLikeClick,
                    onCommentClick = {
                        onCommentClick()
                    },
                    onShareClick = onShareClick,
                    onSaveClick = onSaveClick,
                    onLikedByClick = onLikedByClick
                )
                Spacer(modifier = Modifier.height(SpaceSmall))
                val descriptionText = buildAnnotatedString {
                    append(post.description)
                    if(isDescriptionVisible) {
                        withStyle(SpanStyle(
                            color = HintGray
                        )) {
                            append(
                                " " + LocalContext.current.getString(
                                    R.string.read_less
                                )
                            )
                        }
                    } else {
                        withStyle(SpanStyle(
                            color = HintGray
                        )) {
                            append(
                                " " + LocalContext.current.getString(
                                    R.string.read_more
                                )
                            )
                        }
                    }
                }
                Text(
                    text = descriptionText,
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = if(isDescriptionVisible) {
                        Int.MAX_VALUE
                    } else Constants.MAX_POST_DESCRIPTION_LINES,
                    modifier = Modifier
                        .animateContentSize(
                            animationSpec = tween(300)
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    onDescriptionToggle()
                                }
                            )
                        }
                )
            }
        }
    }
}

@Composable
fun ActionRow(
    modifier: Modifier = Modifier,
    isLiked: Boolean = false,
    isSaved: Boolean = false,
    likeCount: Int = 0,
    commentCount: Int = 0,
    onLikeClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onSaveClick: () -> Unit,
    onLikedByClick: () -> Unit = {}
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        EngagementButtons(
            isLiked = isLiked,
            likeCount = likeCount,
            commentCount = commentCount,
            onLikeClick = onLikeClick,
            onCommentClick = onCommentClick,
            onShareClick = onShareClick,
            onLikedByClick = onLikedByClick
        )
        Icon(
            painter = if(isSaved) {
                painterResource(id = R.drawable.ic_save)
            } else {
                painterResource(id = R.drawable.ic_unsave)
            },
            contentDescription = stringResource(id = R.string.save_post),
            modifier = Modifier
                .size(IconSizeSmall)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            onSaveClick()
                        }
                    )
                }
        )
    }
}

@Composable
fun EngagementButtons(
    modifier: Modifier = Modifier,
    isLiked: Boolean = false,
    likeCount: Int = 0,
    commentCount: Int = 0,
    onLikeClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onLikedByClick: () -> Unit = {}
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
        if(likeCount != 0) {
            Spacer(modifier = Modifier.width(SpaceSmall))
            Text(
                text = likeCount.toString(),
                fontSize = 17.sp,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                onLikedByClick()
                            }
                        )
                    }
            )
        }
        Spacer(modifier = Modifier.width(SpaceMediumLarge))
        Icon(
            painter = painterResource(id = R.drawable.ic_comment),
            contentDescription = stringResource(id = R.string.comment),
            modifier = Modifier
                .size(IconSizeSmall)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            onCommentClick()
                        }
                    )
                }
        )
        if(commentCount != 0) {
            Spacer(modifier = Modifier.width(SpaceSmall))
            Text(
                text = commentCount.toString(),
                fontSize = 17.sp,
                style = MaterialTheme.typography.labelMedium
            )
        }
        Spacer(modifier = Modifier.width(SpaceMediumLarge))
        Icon(
            painter = painterResource(id = R.drawable.ic_share),
            contentDescription = stringResource(id = R.string.share),
            modifier = Modifier
                .size(IconSizeSmall)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            onShareClick()
                        }
                    )
                }
        )
    }
}