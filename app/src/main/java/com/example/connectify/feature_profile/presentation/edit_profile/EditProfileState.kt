package com.example.connectify.feature_profile.presentation.edit_profile

import android.net.Uri
import com.example.connectify.feature_profile.domain.models.Profile

data class EditProfileState(
    val profile: Profile? = null,
    val bannerUri: Uri? = null,
    val profileUri: Uri? = null,
    val isLoading: Boolean = false
)
