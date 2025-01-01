package com.example.connectify.core.presentation.crop_image

import android.graphics.Bitmap
import android.net.Uri
import com.example.connectify.core.presentation.crop_image.cropview.CropType

data class CropState(
    val imageUri: Uri? = null,
    val cropImageUri: Uri? = null,
    val imageBitmap: Bitmap? = null,
    val cropImageBitmap: Bitmap? = null,
    val isSavingMedia: Boolean = false,
    val cropType: CropType = CropType.FULL_PICTURE
)
