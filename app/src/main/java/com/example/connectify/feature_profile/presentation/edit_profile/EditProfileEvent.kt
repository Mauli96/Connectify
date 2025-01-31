package com.example.connectify.feature_profile.presentation.edit_profile

import android.net.Uri
import com.example.connectify.core.presentation.crop_image.cropview.CropType
import com.example.connectify.feature_profile.domain.models.Skill

sealed interface EditProfileEvent {
    data class OnEnteredUsername(val value: String): EditProfileEvent
    data class OnEnteredGitHubUrl(val value: String): EditProfileEvent
    data class OnEnteredInstagramUrl(val value: String): EditProfileEvent
    data class OnEnteredLinkedInUrl(val value: String): EditProfileEvent
    data class OnEnteredBio(val value: String): EditProfileEvent
    data class OnCropBannerImage(val uri: Uri?): EditProfileEvent
    data class OnCropProfilePicture(val uri: Uri?): EditProfileEvent
    data class OnSetSkillSelected(val skill: Skill): EditProfileEvent
    data class OnNavigatingToCrop(val type: CropType): EditProfileEvent
    data object OnNavigatingToBackFromCrop: EditProfileEvent
    data object OnUpdateProfile: EditProfileEvent
}