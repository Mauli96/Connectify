package com.example.connectify.feature_profile.presentation.edit_profile

import android.net.Uri
import com.example.connectify.core.presentation.crop_image.cropview.CropType
import com.example.connectify.feature_profile.domain.models.Skill

sealed class EditProfileEvent {
    data class EnteredUsername(val value: String): EditProfileEvent()
    data class EnteredGitHubUrl(val value: String): EditProfileEvent()
    data class EnteredInstagramUrl(val value: String): EditProfileEvent()
    data class EnteredLinkedInUrl(val value: String): EditProfileEvent()
    data class EnteredBio(val value: String): EditProfileEvent()
    data class CropBannerImage(val uri: Uri?): EditProfileEvent()
    data class CropProfilePicture(val uri: Uri?): EditProfileEvent()
    data class SetSkillSelected(val skill: Skill): EditProfileEvent()
    data class OnNavigatingToCrop(val type: CropType): EditProfileEvent()
    data object OnNavigatingToBackFromCrop: EditProfileEvent()
    data object UpdateProfile: EditProfileEvent()
}