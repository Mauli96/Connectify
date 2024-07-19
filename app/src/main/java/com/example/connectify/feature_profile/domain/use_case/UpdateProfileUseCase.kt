package com.example.connectify.feature_profile.domain.use_case

import android.net.Uri
import com.example.connectify.R
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.SimpleResource
import com.example.connectify.core.util.UiText
import com.example.connectify.feature_profile.domain.models.UpdateProfileData
import com.example.connectify.feature_profile.domain.repository.ProfileRepository

class UpdateProfileUseCase(
    private val repository: ProfileRepository
) {

    suspend operator fun invoke(
        bannerImageUri: Uri?,
        profilePictureUri: Uri?,
        updateProfileData: UpdateProfileData
    ): SimpleResource {
        if(updateProfileData.username.isBlank()) {
            return Resource.Error(
                uiText = UiText.StringResource(R.string.error_username_empty)
            )
        }
        return repository.updateProfile(
            bannerImageUri = bannerImageUri,
            profilePictureUri = profilePictureUri,
            updateProfileData = updateProfileData
        )
    }
}