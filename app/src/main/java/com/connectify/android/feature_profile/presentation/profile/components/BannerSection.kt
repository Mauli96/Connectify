package com.connectify.android.feature_profile.presentation.profile.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.rememberCoroutineScope
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
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.connectify.android.R
import com.connectify.android.core.presentation.ui.theme.HintGray
import com.connectify.android.core.presentation.ui.theme.IconSizeSmall
import com.connectify.android.core.presentation.ui.theme.SpaceLarge
import com.connectify.android.core.presentation.ui.theme.SpaceMedium
import com.connectify.android.core.presentation.ui.theme.SpaceMediumLarge
import com.connectify.android.core.presentation.ui.theme.SpaceSmall
import com.connectify.android.core.presentation.ui.theme.Typography
import com.connectify.android.core.presentation.ui.theme.withColor
import com.connectify.android.feature_profile.domain.models.Skill
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object BannerSectionConstants {
    val DIVIDER_THICKNESS = 0.5.dp
    val DROPDOWN_SHADOW = 20.dp
}

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
    isDropdownMenuVisible: Boolean = false,
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
    onLinkedInClick: () -> Unit = {}
) {

    Box(modifier = modifier) {
        AsyncImage(
            model = bannerUrl,
            contentDescription = stringResource(id = R.string.banner_image),
            imageLoader = imageLoader,
            modifier = imageModifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        ProfileDropdownMenu(
            expanded = isDropdownMenuVisible,
            onDismiss = onDismissDropdownMenu,
            onEditClick = onEditClick,
            onSavedClick = onSavedClick,
            onLogoutClick = onLogoutClick
        )

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
                        startY = 0.3f
                    )
                )
        )

        Row(
            modifier = leftIconModifier
                .height(iconSize)
                .align(Alignment.BottomStart)
                .padding(SpaceSmall)
        ) {
            topSkills.forEachIndexed { index, skill ->
                if (index > 0) {
                    Spacer(modifier = Modifier.width(SpaceMedium))
                }
                AsyncImage(
                    model = skill.imageUrl,
                    contentDescription = stringResource(R.string.select_top_3_skills),
                    imageLoader = imageLoader,
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

@Composable
private fun ProfileDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onSavedClick: () -> Unit,
    onLogoutClick: () -> Unit
) {

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopEnd)
            .padding(end = SpaceSmall)
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismiss,
            modifier = Modifier
                .shadow(BannerSectionConstants.DROPDOWN_SHADOW)
                .background(MaterialTheme.colorScheme.background)
        ) {
            DropdownMenuItem(
                text = { MenuItemContent(icon = R.drawable.ic_edit, text = R.string.edit_profile) },
                onClick = {
                    onDismiss()
                    scope.launch {
                        delay(50)
                        onEditClick()
                    }
                }
            )

            DropdownMenuItem(
                text = { MenuItemContent(icon = R.drawable.ic_unsave, text = R.string.saved) },
                onClick = {
                    onDismiss()
                    scope.launch {
                        delay(50)
                        onSavedClick()
                    }
                }
            )

            HorizontalDivider(
                thickness = BannerSectionConstants.DIVIDER_THICKNESS,
                color = HintGray,
                modifier = Modifier.padding(start = SpaceMediumLarge)
            )

            DropdownMenuItem(
                text = {
                    MenuItemContent(
                        icon = R.drawable.ic_logout,
                        text = R.string.log_out,
                        tint = Color.Red
                    )
                },
                onClick = {
                    onDismiss()
                    onLogoutClick()
                }
            )
        }
    }
}

@Composable
private fun MenuItemContent(
    @DrawableRes icon: Int,
    @StringRes text: Int,
    tint: Color = MaterialTheme.colorScheme.onBackground
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.width(SpaceSmall))
        Icon(
            painter = painterResource(icon),
            contentDescription = stringResource(text),
            modifier = Modifier.size(IconSizeSmall),
            tint = tint
        )
        Spacer(modifier = Modifier.width(SpaceSmall))
        Text(
            text = stringResource(text),
            style = Typography.labelMedium.withColor(tint)
        )
        Spacer(modifier = Modifier.width(SpaceLarge))
    }
}