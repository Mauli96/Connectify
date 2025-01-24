package com.example.connectify.core.presentation.crop_image

import android.graphics.Bitmap
import com.example.connectify.core.presentation.crop_image.cropview.CropType

sealed interface CropEvents {
    data class OnCropImageBitmap(val imageBitmap: Bitmap) : CropEvents
    data class OnChangeCropType(val cropType: CropType) : CropEvents
}