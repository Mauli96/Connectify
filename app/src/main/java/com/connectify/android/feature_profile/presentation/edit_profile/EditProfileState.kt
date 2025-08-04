package com.connectify.android.feature_profile.presentation.edit_profile

import android.net.Uri
import com.connectify.android.feature_profile.domain.models.Profile
import com.connectify.android.feature_profile.domain.models.Skill

data class EditProfileState(
    val profile: Profile? = null,
    val bannerUri: Uri? = null,
    val profileUri: Uri? = null,
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val skills: List<Skill> = emptyList(),
    val selectedSkills: List<Skill> = emptyList()
)
