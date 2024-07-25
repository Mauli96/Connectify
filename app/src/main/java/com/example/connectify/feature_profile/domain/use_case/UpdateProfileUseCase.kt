package com.example.connectify.feature_profile.domain.use_case

import android.net.Uri
import com.example.connectify.R
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.SimpleResource
import com.example.connectify.core.util.UiText
import com.example.connectify.feature_profile.domain.models.UpdateProfileData
import com.example.connectify.core.domain.repository.ProfileRepository
import com.example.connectify.feature_profile.domain.util.ProfileConstants

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
        val isValidGithubUrl = updateProfileData.gitHubUrl.isBlank() ||
                ProfileConstants.GITHUB_PROFILE_REGEX.matches(updateProfileData.gitHubUrl)
        if (!isValidGithubUrl) {
            return Resource.Error(
                uiText = UiText.StringResource(R.string.error_invalid_github_url)
            )
        }

        val isValidInstagramUrl = updateProfileData.instagramUrl.isBlank() ||
                ProfileConstants.INSTAGRAM_PROFILE_REGEX.matches(updateProfileData.instagramUrl)
        if (!isValidInstagramUrl) {
            return Resource.Error(
                uiText = UiText.StringResource(R.string.error_invalid_instagram_url)
            )
        }

        val isValidLinkedUrl = updateProfileData.linkedInUrl.isBlank() ||
                ProfileConstants.LINKED_IN_PROFILE_REGEX.matches(updateProfileData.linkedInUrl)
        if (!isValidLinkedUrl) {
            return Resource.Error(
                uiText = UiText.StringResource(R.string.error_invalid_linked_in_url)
            )
        }
        return repository.updateProfile(
            bannerImageUri = bannerImageUri,
            profilePictureUri = profilePictureUri,
            updateProfileData = updateProfileData
        )
    }
}