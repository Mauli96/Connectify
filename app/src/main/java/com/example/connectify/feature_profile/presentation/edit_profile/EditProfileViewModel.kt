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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
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

    private val _skills = MutableStateFlow(SkillsState())
    val skills = _skills
        .onStart { getSkills() }
        .stateIn(viewModelScope, SharingStarted.Lazily, SkillsState())

    private val _editProfileState = MutableStateFlow(EditProfileState())
    val editProfileState = _editProfileState.asStateFlow()

    val networkState: StateFlow<NetworkConnectionState> =
        connectivityObserver.networkConnection
            .stateIn(viewModelScope, SharingStarted.Lazily, NetworkConnectionState.Available)

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<String>("userId")?.let { userId ->
            getProfile(userId)
        }
    }

    fun onEvent(event: EditProfileEvent) {
        when(event) {
            is EditProfileEvent.EnteredUsername -> {
                _usernameState.update {
                    it.copy(
                        text = event.value
                    )
                }
            }
            is EditProfileEvent.EnteredGitHubUrl -> {
                _githubTextFieldState.update {
                    it.copy(
                        text = event.value
                    )
                }
            }
            is EditProfileEvent.EnteredInstagramUrl -> {
                _instagramTextFieldState.update {
                    it.copy(
                        text = event.value
                    )
                }
            }
            is EditProfileEvent.EnteredLinkedInUrl -> {
                _linkedInTextFieldState.update {
                    it.copy(
                        text = event.value
                    )
                }
            }
            is EditProfileEvent.EnteredBio -> {
                _bioState.update {
                    it.copy(
                        text = event.value
                    )
                }
            }
            is EditProfileEvent.CropBannerImage -> {
                _editProfileState.update {
                    it.copy(
                        bannerUri = event.uri
                    )
                }
            }
            is EditProfileEvent.CropProfilePicture -> {
                _editProfileState.update {
                    it.copy(
                        profileUri = event.uri
                    )
                }
            }
            is EditProfileEvent.SetSkillSelected -> {
                val result = profileUseCases.setSkillUseCase(
                    selectedSkill = skills.value.selectedSkills,
                    skillToToggle = event.skill
                )
                viewModelScope.launch {
                    when(result) {
                        is Resource.Success -> {
                            _skills.update {
                                it.copy(
                                    selectedSkills = result.data ?: kotlin.run {
                                        _eventFlow.emit(UiEvent.ShowSnackbar(
                                            uiText = result.uiText ?: UiText.unknownError()
                                        ))
                                        return@launch
                                    }
                                )
                            }
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.ShowSnackbar(
                                uiText = result.uiText ?: UiText.unknownError()
                            ))
                        }
                    }
                }
            }
            is EditProfileEvent.UpdateProfile -> {
                updateProfile()
            }
        }
    }

    private fun getSkills() {
        viewModelScope.launch {
            val result = profileUseCases.getSkills()
            when(result) {
                is Resource.Success -> {
                    _skills.update {
                        it.copy(
                            skills = result.data ?: kotlin.run {
                                _eventFlow.emit(
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
                    _eventFlow.emit(UiEvent.ShowSnackbar(
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
                        _eventFlow.emit(UiEvent.ShowSnackbar(
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
                    _skills.update {
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
                    _eventFlow.emit(UiEvent.ShowSnackbar(
                        uiText = result.uiText ?: UiText.unknownError()
                    ))
                    return@launch
                }
            }
        }
    }

    private fun updateProfile() {
        viewModelScope.launch {
            val result = profileUseCases.updateProfile(
                bannerImageUri = editProfileState.value.bannerUri,
                profilePictureUri = editProfileState.value.profileUri,
                updateProfileData = UpdateProfileData(
                    username = usernameState.value.text,
                    gitHubUrl = githubTextFieldState.value.text,
                    linkedInUrl = linkedInTextFieldState.value.text,
                    instagramUrl = instagramTextFieldState.value.text,
                    bio = bioState.value.text,
                    skills = skills.value.selectedSkills
                )
            )
            when(result) {
                is Resource.Success -> {
                    _eventFlow.emit(UiEvent.ShowSnackbar(
                        uiText = UiText.StringResource(R.string.updated_profile)
                    ))
                    _eventFlow.emit(UiEvent.NavigateUp)
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.ShowSnackbar(
                        uiText = result.uiText ?: UiText.unknownError()
                    ))
                }
            }
        }
    }
}