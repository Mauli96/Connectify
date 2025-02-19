package com.example.connectify.feature_profile.presentation.edit_profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectify.R
import com.example.connectify.core.data.connectivity.ConnectivityObserver
import com.example.connectify.core.domain.states.NetworkConnectionState
import com.example.connectify.core.domain.states.StandardTextFieldState
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.UiText
import com.example.connectify.feature_profile.domain.models.UpdateProfileData
import com.example.connectify.feature_profile.domain.use_case.ProfileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases,
    private val connectivityObserver: ConnectivityObserver,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _editProfileState = MutableStateFlow(EditProfileState())
    val editProfileState = _editProfileState
        .onStart { getSkills() }
        .stateIn(viewModelScope, SharingStarted.Lazily, EditProfileState())

    private val _usernameState = MutableStateFlow(StandardTextFieldState())
    val usernameState = _usernameState.asStateFlow()

    private val _githubTextFieldState = MutableStateFlow(StandardTextFieldState())
    val githubTextFieldState = _githubTextFieldState.asStateFlow()

    private val _instagramTextFieldState = MutableStateFlow(StandardTextFieldState())
    val instagramTextFieldState = _instagramTextFieldState.asStateFlow()

    private val _linkedInTextFieldState = MutableStateFlow(StandardTextFieldState())
    val linkedInTextFieldState = _linkedInTextFieldState.asStateFlow()

    private val _bioState = MutableStateFlow(StandardTextFieldState())
    val bioState = _bioState.asStateFlow()

    private val _cropState = MutableStateFlow(CropPictureState())
    val cropState = _cropState.asStateFlow()

    val networkState: StateFlow<NetworkConnectionState> =
        connectivityObserver.networkConnection
            .stateIn(viewModelScope, SharingStarted.Lazily, NetworkConnectionState.Available)

    private val _eventFlow = Channel<UiEvent>()
    val eventFlow = _eventFlow.receiveAsFlow()

    init {
        savedStateHandle.get<String>("userId")?.let { userId ->
            getProfile(userId)
        }
    }

    fun onEvent(event: EditProfileEvent) {
        when(event) {
            is EditProfileEvent.OnEnteredUsername -> {
                _usernameState.update {
                    it.copy(
                        text = event.value
                    )
                }
            }
            is EditProfileEvent.OnEnteredGitHubUrl -> {
                _githubTextFieldState.update {
                    it.copy(
                        text = event.value
                    )
                }
            }
            is EditProfileEvent.OnEnteredInstagramUrl -> {
                _instagramTextFieldState.update {
                    it.copy(
                        text = event.value
                    )
                }
            }
            is EditProfileEvent.OnEnteredLinkedInUrl -> {
                _linkedInTextFieldState.update {
                    it.copy(
                        text = event.value
                    )
                }
            }
            is EditProfileEvent.OnEnteredBio -> {
                _bioState.update {
                    it.copy(
                        text = event.value
                    )
                }
            }
            is EditProfileEvent.OnCropBannerImage -> {
                _editProfileState.update {
                    it.copy(
                        bannerUri = event.uri
                    )
                }
            }
            is EditProfileEvent.OnCropProfilePicture -> {
                _editProfileState.update {
                    it.copy(
                        profileUri = event.uri
                    )
                }
            }
            is EditProfileEvent.OnSetSkillSelected -> {
                val result = profileUseCases.setSkillUseCase(
                    selectedSkill = editProfileState.value.selectedSkills,
                    skillToToggle = event.skill
                )
                viewModelScope.launch {
                    when(result) {
                        is Resource.Success -> {
                            _editProfileState.update {
                                it.copy(
                                    selectedSkills = result.data ?: kotlin.run {
                                        _eventFlow.send(UiEvent.ShowSnackbar(
                                            uiText = result.uiText ?: UiText.unknownError()
                                        ))
                                        return@launch
                                    }
                                )
                            }
                        }
                        is Resource.Error -> {
                            _eventFlow.send(UiEvent.ShowSnackbar(
                                uiText = result.uiText ?: UiText.unknownError()
                            ))
                        }
                    }
                }
            }
            is EditProfileEvent.OnNavigatingToCrop -> {
                _cropState.update {
                    it.copy(
                        isNavigatedToCrop = true,
                        cropType = event.type
                    )
                }
            }
            is EditProfileEvent.OnNavigatingToBackFromCrop -> {
                _cropState.update {
                    it.copy(
                        isNavigatedToCrop = false
                    )
                }
            }
            is EditProfileEvent.OnUpdateProfile -> {
                updateProfile()
            }
        }
    }

    private fun getSkills() {
        viewModelScope.launch {
            val result = profileUseCases.getSkills()
            when(result) {
                is Resource.Success -> {
                    _editProfileState.update {
                        it.copy(
                            skills = result.data ?: kotlin.run {
                                _eventFlow.send(
                                    UiEvent.ShowSnackbar(
                                        uiText = UiText.StringResource(R.string.error_couldnt_load_skills)
                                    )
                                )
                                return@launch
                            }
                        )
                    }
                }
                is Resource.Error -> {
                    _eventFlow.send(UiEvent.ShowSnackbar(
                        uiText = result.uiText ?: UiText.unknownError()
                    ))
                    return@launch
                }
            }
        }
    }

    private fun getProfile(userId: String) {
        viewModelScope.launch {
            _editProfileState.update {
                it.copy(
                    isLoading = true
                )
            }
            val result = profileUseCases.getProfile(userId)
            when(result) {
                is Resource.Success -> {
                    val profile = result.data ?: kotlin.run {
                        _eventFlow.send(UiEvent.ShowSnackbar(
                                uiText = UiText.StringResource(R.string.error_couldnt_load_profile)
                        ))
                        return@launch
                    }
                    _usernameState.update {
                        it.copy(
                            text = profile.username
                        )
                    }
                    _githubTextFieldState.update {
                        it.copy(
                            text = profile.gitHubUrl ?: ""
                        )
                    }
                    _instagramTextFieldState.update {
                        it.copy(
                            text = profile.instagramUrl ?: ""
                        )
                    }
                    _linkedInTextFieldState.update {
                        it.copy(
                            text = profile.linkedInUrl ?: ""
                        )
                    }
                    _bioState.update {
                        it.copy(
                            text = profile.bio
                        )
                    }
                    _editProfileState.update {
                        it.copy(
                            selectedSkills = profile.topSkills
                        )
                    }
                    _editProfileState.update {
                        it.copy(
                            profile = profile,
                            isLoading = false
                        )
                    }
                }
                is Resource.Error -> {
                    _editProfileState.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                    _eventFlow.send(UiEvent.ShowSnackbar(
                        uiText = result.uiText ?: UiText.unknownError()
                    ))
                    return@launch
                }
            }
        }
    }

    private fun updateProfile() {
        viewModelScope.launch {
            _editProfileState.update {
                it.copy(
                    isUpdating = true
                )
            }
            val result = profileUseCases.updateProfile(
                bannerImageUri = editProfileState.value.bannerUri,
                profilePictureUri = editProfileState.value.profileUri,
                updateProfileData = UpdateProfileData(
                    username = usernameState.value.text,
                    gitHubUrl = githubTextFieldState.value.text,
                    linkedInUrl = linkedInTextFieldState.value.text,
                    instagramUrl = instagramTextFieldState.value.text,
                    bio = bioState.value.text,
                    skills = editProfileState.value.selectedSkills
                )
            )
            when(result) {
                is Resource.Success -> {
                    _eventFlow.send(UiEvent.ShowSnackbar(
                        uiText = UiText.StringResource(R.string.updated_profile)
                    ))
                    _eventFlow.send(UiEvent.NavigateUp)
                }
                is Resource.Error -> {
                    _eventFlow.send(UiEvent.ShowSnackbar(
                        uiText = result.uiText ?: UiText.unknownError()
                    ))
                }
            }
            _editProfileState.update {
                it.copy(
                    isUpdating = false
                )
            }
        }
    }
}