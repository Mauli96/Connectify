package com.example.connectify.core.presentation.crop_image

import android.graphics.Bitmap
import com.example.connectify.core.presentation.crop_image.cropview.CropType

sealed class CropEvents {
    data class CropImageBitmap(val imageBitmap: Bitmap) : CropEvents()
    data class ChangeCropType(val cropType: CropType) : CropEvents()
}
