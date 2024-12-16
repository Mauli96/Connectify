package com.example.connectify.feature_profile.presentation.edit_profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import com.example.connectify.R
import com.example.connectify.core.presentation.components.ConnectivityBanner
import com.example.connectify.core.presentation.components.CustomCircularProgressIndicator
import com.example.connectify.core.presentation.components.StandardOutlinedTextField
import com.example.connectify.core.presentation.components.StandardToolbar
import com.example.connectify.core.presentation.ui.theme.ProfilePictureSizeLarge
import com.example.connectify.core.presentation.ui.theme.SpaceLarge
import com.example.connectify.core.presentation.ui.theme.SpaceMedium
import com.example.connectify.core.presentation.util.CropActivityResultContract
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.presentation.util.asString
import com.example.connectify.feature_profile.presentation.edit_profile.components.Chip
import com.example.connectify.feature_profile.presentation.util.EditProfileError
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EditProfileScreen(
    snackbarHostState: SnackbarHostState,
    imageLoader: ImageLoader,
    onNavigate: (String) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    viewModel: EditProfileViewModel = hiltViewModel(),
    profilePictureSize: Dp = ProfilePictureSizeLarge
) {

    val editProfileState by viewModel.editProfileState.collectAsStateWithLifecycle()
    val usernameState by viewModel.usernameState.collectAsStateWithLifecycle()
    val githubTextFieldState by viewModel.githubTextFieldState.collectAsStateWithLifecycle()
    val instagramTextFieldState by viewModel.instagramTextFieldState.collectAsStateWithLifecycle()
    val linkedInTextFieldState by viewModel.linkedInTextFieldState.collectAsStateWithLifecycle()
    val bioState by viewModel.bioState.collectAsStateWithLifecycle()
    val skills by viewModel.skills.collectAsStateWithLifecycle()
    val networkState by viewModel.networkState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val cropBannerImageLauncher = rememberLauncherForActivityResult(
        contract = CropActivityResultContract(5f, 2f)
    ) {
        viewModel.onEvent(EditProfileEvent.CropBannerImage(it))
    }
    val bannerImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        if(it != null) {
            cropBannerImageLauncher.launch(it)
        }
    }

    val cropProfilePictureLauncher = rememberLauncherForActivityResult(
        contract = CropActivityResultContract(1f, 1f)
    ) {
        viewModel.onEvent(EditProfileEvent.CropProfilePicture(it))
    }
    val profilePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        if(it != null) {
            cropProfilePictureLauncher.launch(it)
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when(event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.uiText.asString(context)
                    )
                }
                is UiEvent.NavigateUp -> {
                    onNavigateUp()
                }
                else -> {
                    null
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            StandardToolbar(
                onNavigateUp = onNavigateUp,
                showBackArrow = true,
                navActions = {
                    IconButton(onClick = {
                        viewModel.onEvent(EditProfileEvent.UpdateProfile)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(id = R.string.save_changes),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(id = R.string.edit_your_profile),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    BannerEditSection(
                        bannerImage = rememberAsyncImagePainter(
                            model = editProfileState.bannerUri ?: editProfileState.profile?.bannerUrl,
                            imageLoader = imageLoader
                        ),
                        profileImage = rememberAsyncImagePainter(
                            model = editProfileState.profileUri ?: editProfileState.profile?.profilePictureUrl,
                            imageLoader = imageLoader
                        ),
                        profilePictureSize = profilePictureSize,
                        onBannerImageClick = {
                            bannerImageLauncher.launch("image/*")
                        },
                        onProfilePictureClick = {
                            profilePictureLauncher.launch("image/*")
                        }
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SpaceLarge)
                    ) {
                        Spacer(modifier = Modifier.height(SpaceMedium))
                        Text(
                            text = stringResource(id = R.string.username),
                            style = MaterialTheme.typography.labelSmall
                        )
                        StandardOutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = usernameState.text,
                            error = when(usernameState.error) {
                                is EditProfileError.FieldEmpty -> {
                                    stringResource(id = R.string.this_field_cant_be_empty)
                                }
                                else -> ""
                            },
                            onValueChange = {
                                viewModel.onEvent(
                                    EditProfileEvent.EnteredUsername(it)
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(SpaceMedium))
                        Text(
                            text = stringResource(id = R.string.github_profile_url),
                            style = MaterialTheme.typography.labelSmall
                        )
                        StandardOutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = githubTextFieldState.text,
                            error = when(githubTextFieldState.error) {
                                is EditProfileError.FieldEmpty -> {
                                    stringResource(id = R.string.this_field_cant_be_empty)
                                }
                                else -> ""
                            },
                            onValueChange = {
                                viewModel.onEvent(
                                    EditProfileEvent.EnteredGitHubUrl(it)
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(SpaceMedium))
                        Text(
                            text = stringResource(id = R.string.instagram_profile_url),
                            style = MaterialTheme.typography.labelSmall
                        )
                        StandardOutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = instagramTextFieldState.text,
                            error = when(instagramTextFieldState.error) {
                                is EditProfileError.FieldEmpty -> {
                                    stringResource(id = R.string.this_field_cant_be_empty)
                                }
                                else -> ""
                            },
                            onValueChange = {
                                viewModel.onEvent(
                                    EditProfileEvent.EnteredInstagramUrl(it)
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(SpaceMedium))
                        Text(
                            text = stringResource(id = R.string.linked_in_profile_url),
                            style = MaterialTheme.typography.labelSmall
                        )
                        StandardOutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = linkedInTextFieldState.text,
                            error = when(linkedInTextFieldState.error) {
                                is EditProfileError.FieldEmpty -> {
                                    stringResource(id = R.string.this_field_cant_be_empty)
                                }
                                else -> ""
                            },
                            onValueChange = {
                                viewModel.onEvent(
                                    EditProfileEvent.EnteredLinkedInUrl(it)
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(SpaceMedium))
                        Text(
                            text = stringResource(id = R.string.your_bio),
                            style = MaterialTheme.typography.labelSmall
                        )
                        StandardOutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = bioState.text,
                            error = when(bioState.error) {
                                is EditProfileError.FieldEmpty -> {
                                    stringResource(id = R.string.this_field_cant_be_empty)
                                }
                                else -> ""
                            },
                            singleLine = false,
                            maxLines = 3,
                            minLines = 3,
                            onValueChange = {
                                viewModel.onEvent(
                                    EditProfileEvent.EnteredBio(it)
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(SpaceMedium))
                        Text(
                            text = stringResource(id = R.string.select_top_3_skills),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 20.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(SpaceLarge))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            mainAxisAlignment = MainAxisAlignment.Center,
                            mainAxisSpacing = SpaceMedium,
                            crossAxisSpacing = SpaceMedium
                        ) {
                            skills.skills.forEach { skill ->
                                Chip(
                                    text = skill.name,
                                    selected = skill in viewModel.skills.value.selectedSkills,
                                    onChipClick = {
                                        viewModel.onEvent(EditProfileEvent.SetSkillSelected(skill))
                                    }
                                )
                            }
                        }
                    }
                }
                ConnectivityBanner(
                    networkState = networkState,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                )
            }
        }
        if(editProfileState.isLoading) {
            CustomCircularProgressIndicator(
                modifier = Modifier.align(Center)
            )
        }
    }
}

@Composable
fun BannerEditSection(
    bannerImage: Painter,
    profileImage: Painter,
    profilePictureSize: Dp = ProfilePictureSizeLarge,
    onBannerImageClick: () -> Unit = {},
    onProfilePictureClick: () -> Unit = {}
) {
    val bannerHeight = (LocalConfiguration.current.screenWidthDp / 2.5f).dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(bannerHeight + profilePictureSize / 2f)
    ) {
        Image(
            painter = bannerImage,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(bannerHeight)
                .clickable {
                    onBannerImageClick()
                }
        )
        Image(
            painter = profileImage,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(profilePictureSize)
                .clip(CircleShape)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface,
                    shape = CircleShape
                )
                .clickable {
                    onProfilePictureClick()
                }
        )
    }
}