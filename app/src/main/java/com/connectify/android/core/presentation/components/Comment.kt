package com.connectify.android.core.presentation.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import com.connectify.android.R
import com.connectify.android.core.domain.models.Comment
import com.connectify.android.core.presentation.ui.theme.IconSizeSmall
import com.connectify.android.core.presentation.ui.theme.ProfilePictureSizeExtraSmall
import com.connectify.android.core.presentation.ui.theme.SpaceLarge
import com.connectify.android.core.presentation.ui.theme.SpaceMedium
import com.connectify.android.core.presentation.ui.theme.SpaceSmall
import com.connectify.android.core.presentation.ui.theme.Typography
import com.connectify.android.core.presentation.ui.theme.withColor
import com.connectify.android.core.presentation.ui.theme.withSize
import com.connectify.android.core.util.vibrate

@Composable
fun Comment(
    modifier: Modifier = Modifier,
    comment: Comment,
    context: Context,
    imageLoader: ImageLoader,
    onLikeClick: (Boolean) -> Unit = {},
    onLikedByClick: () -> Unit = {},
    onLongPress: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    var isContextMenuVisible by rememberSaveable { mutableStateOf(false) }
    var pressOffset by remember { mutableStateOf(DpOffset.Zero) }
    var itemHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Card(
        modifier = modifier.onSizeChanged {
            itemHeight = with(density) { it.height.toDp() }
        },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        CommentContent(
            comment = comment,
            imageLoader = imageLoader,
            onLikeClick = onLikeClick,
            onLikedByClick = onLikedByClick,
            onLongPress = {
                if(comment.isOwnComment) {
                    onLongPress()
                    isContextMenuVisible = true
                    pressOffset = it
                    vibrate(context)
                }
            }
        )

        CommentContextMenu(
            isVisible = isContextMenuVisible,
            pressOffset = pressOffset,
            itemHeight = itemHeight,
            onDismiss = { isContextMenuVisible = false },
            onDeleteClick = {
                isContextMenuVisible = false
                onDeleteClick()
            }
        )
    }
}

@Composable
private fun CommentContent(
    comment: Comment,
    imageLoader: ImageLoader,
    onLikeClick: (Boolean) -> Unit,
    onLikedByClick: () -> Unit,
    onLongPress: (DpOffset) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = SpaceMedium,
                bottom = SpaceMedium,
                start = 10.dp,
                end = 10.dp,
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { offset ->
                        onLongPress(DpOffset(offset.x.toDp(), offset.y.toDp()))
                    }
                )
            }
    ) {
        CommentHeader(
            comment = comment,
            imageLoader = imageLoader
        )

        Spacer(modifier = Modifier.height(SpaceSmall))

        CommentBody(
            comment = comment,
            onLikeClick = onLikeClick,
            onLikedByClick = onLikedByClick
        )
    }
}

@Composable
private fun CommentHeader(
    comment: Comment,
    imageLoader: ImageLoader
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = comment.profilePictureUrl,
                contentDescription = stringResource(R.string.profile_image),
                imageLoader = imageLoader,
                modifier = Modifier
                    .size(ProfilePictureSizeExtraSmall)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            Text(
                text = comment.username,
                fontWeight = FontWeight.Bold,
                style = Typography.labelSmall.withSize(12.sp)
            )
        }
        Text(
            text = comment.formattedTime,
            style = Typography.labelSmall.withSize(12.sp)
        )
    }
}

@Composable
private fun CommentBody(
    comment: Comment,
    onLikeClick: (Boolean) -> Unit,
    onLikedByClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Spacer(modifier = Modifier.width(SpaceLarge))
        Text(
            text = comment.comment,
            style = Typography.labelSmall.withSize(12.sp),
            modifier = Modifier.weight(9f)
        )
        CommentActions(
            comment = comment,
            onLikeClick = onLikeClick,
            onLikedByClick = onLikedByClick
        )
    }
}

@Composable
private fun CommentActions(
    comment: Comment,
    onLikeClick: (Boolean) -> Unit,
    onLikedByClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(
            start = SpaceMedium,
            end = SpaceSmall
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LikeButton(
            isLiked = comment.isLiked,
            onLikeClick = { onLikeClick(comment.isLiked) },
            size = 15.dp
        )
        if(comment.likeCount != 0) {
            Text(
                text = comment.likeCount.toString(),
                style = Typography.labelSmall.withSize(12.sp),
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(onTap = { onLikedByClick() })
                }
            )
        }
    }
}

@Composable
private fun CommentContextMenu(
    isVisible: Boolean,
    pressOffset: DpOffset,
    itemHeight: androidx.compose.ui.unit.Dp,
    onDismiss: () -> Unit,
    onDeleteClick: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
    ) {
        DropdownMenu(
            expanded = isVisible,
            onDismissRequest = onDismiss,
            offset = pressOffset.copy(y = pressOffset.y - itemHeight)
        ) {
            DropdownMenuItem(
                text = {
                    MenuItemContent(
                        iconId = R.drawable.ic_delete,
                        textId = R.string.delete,
                        color = Color.Red
                    )
                },
                onClick = onDeleteClick
            )
            DropdownMenuItem(
                text = {
                    MenuItemContent(
                        iconId = R.drawable.ic_cancel,
                        textId = R.string.cancel
                    )
                },
                onClick = onDismiss
            )
        }
    }
}

@Composable
private fun MenuItemContent(
    iconId: Int,
    textId: Int,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(SpaceSmall))
        Image(
            painter = painterResource(id = iconId),
            contentDescription = stringResource(id = textId),
            modifier = Modifier.size(IconSizeSmall)
        )
        Spacer(modifier = Modifier.width(SpaceSmall))
        Text(
            text = stringResource(id = textId),
            style = Typography.labelSmall.withColor(color)
        )
        Spacer(modifier = Modifier.width(SpaceLarge))
    }
}