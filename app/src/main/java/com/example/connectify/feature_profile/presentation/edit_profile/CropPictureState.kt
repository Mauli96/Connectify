package com.example.connectify.feature_profile.presentation.edit_profile

import com.example.connectify.core.presentation.crop_image.cropview.CropType

data class CropPictureState(
    val isNavigatedToCrop: Boolean = false,
    val cropType: CropType = CropType.FULL_PICTURE
)