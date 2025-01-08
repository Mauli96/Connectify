package com.example.connectify.feature_profile.presentation.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import com.example.connectify.R
import com.example.connectify.core.presentation.ui.theme.HintGray
import com.example.connectify.core.presentation.ui.theme.IconSizeSmall
import com.example.connectify.core.presentation.ui.theme.SpaceLarge
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.ui.theme.SpaceMediumLarge
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.core.util.toPx
import com.example.connectify.feature_profile.domain.models.Skill

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BannerSection(
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    iconSize: Dp = 35.dp,
    leftIconModifier: Modifier = Modifier,
    rightIconModifier: Modifier = Modifier,
    bannerUrl: String? = null,
    expanded: Boolean = false,
    onShowDropDownMenu: () -> Unit,
    onDismissDropdownMenu: () -> Unit,
    topSkills: List<Skill> = emptyList(),
    shouldShowGitHub: Boolean = false,
    shouldShowInstagram: Boolean = false,
    shouldShowLinkedIn: Boolean = false,
    isOwnProfile: Boolean = false,
    onNavigateUp: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onSavedClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onGitHubClick: () -> Unit = {},
    onInstagramClick: () -> Unit = {},
    onLinkedInClick: () -> Unit = {},
) {

    BoxWithConstraints(
        modifier = modifier
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = bannerUrl,
                imageLoader = imageLoader
            ),
            contentDescription = stringResource(id = R.string.banner_image),
            contentScale = ContentScale.Crop,
            modifier = imageModifier
                .fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.TopEnd)
                .padding(end = SpaceSmall)
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    onDismissDropdownMenu()
                },
                modifier = Modifier
                    .shadow(20.dp)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.width(SpaceSmall))
                            Icon(
                                painter = painterResource(id = R.drawable.ic_edit),
                                contentDescription = stringResource(id = R.string.edit_profile),
                                modifier = Modifier.size(IconSizeSmall)
                            )
                            Spacer(modifier = Modifier.width(SpaceSmall))
                            Text(
                                text = stringResource(id = R.string.edit_profile),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.width(SpaceLarge))
                        }
                    },
                    onClick = {
                        onDismissDropdownMenu()
                        onEditClick()
                    }
                )
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.width(SpaceSmall))
                            Icon(
                                painter = painterResource(id = R.drawable.ic_unsave),
                                contentDescription = stringResource(id = R.string.saved),
                                modifier = Modifier.size(IconSizeSmall)
                            )
                            Spacer(modifier = Modifier.width(SpaceSmall))
                            Text(
                                text = stringResource(id = R.string.saved),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.width(SpaceLarge))
                        }
                    },
                    onClick = {
                        onDismissDropdownMenu()
                        onSavedClick()
                    }
                )
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = HintGray,
                    modifier = Modifier.padding(start = SpaceMediumLarge)
                )
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.width(SpaceSmall))
                            Icon(
                                painter = painterResource(id = R.drawable.ic_logout),
                                contentDescription = stringResource(id = R.string.log_out),
                                modifier = Modifier.size(IconSizeSmall),
                                tint = Color.Red
                            )
                            Spacer(modifier = Modifier.width(SpaceSmall))
                            Text(
                                text = stringResource(id = R.string.log_out),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.Red
                                )
                            )
                            Spacer(modifier = Modifier.width(SpaceLarge))
                        }
                    },
                    onClick = {
                        onDismissDropdownMenu()
                        onLogoutClick()
                    }
                )
            }
        }
        TopAppBar(
            title = { /*TODO*/ },
            navigationIcon = {
                if(!isOwnProfile) {
                    IconButton(
                        onClick = {
                            onNavigateUp()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(id = R.string.back),
                            modifier = Modifier.size(IconSizeSmall)
                        )
                    }
                }
            },
            actions = {
                if(isOwnProfile) {
                    IconButton(
                        onClick = {
                            onShowDropDownMenu()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_more_item),
                            contentDescription = stringResource(id = R.string.more_items),
                            modifier = Modifier.size(IconSizeSmall)
                        )
                    }
                }
            },
            colors = TopAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
                navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                titleContentColor = Color.Transparent,
                actionIconContentColor = MaterialTheme.colorScheme.onBackground
            )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black
                        ),
                        startY = constraints.maxHeight - iconSize.toPx() * 2f
                    )
                )
        )
        Row(
            modifier = leftIconModifier
                .height(iconSize)
                .align(Alignment.BottomStart)
                .padding(SpaceSmall)
        ) {
            topSkills.forEach { skill ->
                Spacer(modifier = Modifier.width(SpaceMedium))
                Image(
                    painter = rememberAsyncImagePainter(
                        model = skill.imageUrl,
                        imageLoader = imageLoader
                    ),
                    contentDescription = null,
                    modifier = Modifier.height(iconSize)
                )
            }
        }
        Row(
            modifier = rightIconModifier
                .height(iconSize)
                .align(Alignment.BottomEnd)
                .padding(SpaceSmall)
        ) {
            if(shouldShowGitHub) {
                IconButton(
                    onClick = onGitHubClick,
                    modifier = Modifier.size(iconSize)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_github),
                        contentDescription = "GitHub",
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
            if(shouldShowInstagram) {
                IconButton(
                    onClick = onInstagramClick,
                    modifier = Modifier.size(iconSize)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_instagram),
                        contentDescription = "Instagram",
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
            if(shouldShowLinkedIn) {
                IconButton(
                    onClick = onLinkedInClick,
                    modifier = Modifier.size(iconSize)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_linkedin),
                        contentDescription = "LinkedIn",
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
        }
    }
}