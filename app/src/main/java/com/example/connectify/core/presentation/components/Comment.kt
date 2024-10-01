package com.example.connectify.core.presentation.components

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import com.example.connectify.R
import com.example.connectify.core.domain.models.Comment
import com.example.connectify.core.presentation.ui.theme.IconSizeSmall
import com.example.connectify.core.presentation.ui.theme.ProfilePictureSizeExtraSmall
import com.example.connectify.core.presentation.ui.theme.SpaceLarge
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.core.util.vibrate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun Comment(
    modifier: Modifier = Modifier,
    comment: Comment,
    context: Context,
    imageLoader: ImageLoader,
    scope: CoroutineScope = rememberCoroutineScope(),
    onLikeClick: (Boolean) -> Unit = {},
    onLikedByClick: () -> Unit = {},
    onLongPress: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    var isContextMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var pressOffset by remember {
        mutableStateOf(DpOffset.Zero)
    }
    var itemHeight by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current

    Card(
        modifier = modifier
            .onSizeChanged {
                itemHeight = with(density) { it.height.toDp() }
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
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
                        onLongPress = {
                            scope.launch {
                                if(comment.isOwnComment) {
                                    onLongPress()
                                    isContextMenuVisible = true
                                    pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                                    vibrate(context)
                                }
                            }
                        }
                    )
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = comment.profilePictureUrl,
                            imageLoader = imageLoader
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(ProfilePictureSizeExtraSmall)
                    )
                    Spacer(modifier = Modifier.width(SpaceSmall))
                    Text(
                        text = comment.username,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = comment.formattedTime,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(SpaceSmall))
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Spacer(modifier = Modifier.width(SpaceSmall))
                Text(
                    text = comment.comment,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(9f)
                )
                Spacer(modifier = Modifier.height(SpaceMedium))
                Column(
                    modifier = Modifier
                        .padding(
                            start = SpaceMedium,
                            end = SpaceSmall
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LikeButton(
                        isLiked = comment.isLiked,
                        onLikeClick = {
                            onLikeClick(comment.isLiked)
                        }
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = comment.likeCount.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground,
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
            }
        }
        AnimatedVisibility(
            visible = isContextMenuVisible,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            DropdownMenu(
                expanded = isContextMenuVisible,
                onDismissRequest = {
                    isContextMenuVisible = false
                },
                offset = pressOffset.copy(
                    y = pressOffset.y - itemHeight
                )
            ) {
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.width(SpaceSmall))
                            Icon(
                                painter = painterResource(id = R.drawable.delete_icon),
                                contentDescription = stringResource(id = R.string.log_out),
                                modifier = Modifier.size(IconSizeSmall),
                                tint = Color.Red
                            )
                            Spacer(modifier = Modifier.width(SpaceSmall))
                            Text(
                                text = stringResource(id = R.string.delete),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.Red
                                )
                            )
                            Spacer(modifier = Modifier.width(SpaceLarge))
                        }
                    },
                    onClick = {
                        isContextMenuVisible = false
                        onDeleteClick()
                    }
                )
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.width(SpaceSmall))
                            Icon(
                                painter = painterResource(id = R.drawable.cancel_icon),
                                contentDescription = stringResource(id = R.string.edit_profile),
                                modifier = Modifier.size(IconSizeSmall)
                            )
                            Spacer(modifier = Modifier.width(SpaceSmall))
                            Text(
                                text = stringResource(id = R.string.cancel),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.width(SpaceLarge))
                        }
                    },
                    onClick = {
                        isContextMenuVisible = false
                    }
                )
            }
        }
    }
}