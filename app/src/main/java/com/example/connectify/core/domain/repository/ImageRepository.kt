package com.example.connectify.core.domain.repository

import android.graphics.Bitmap
import android.net.Uri

interface ImageRepository {

    suspend fun loadImage(uri: Uri): Result<Bitmap>

    suspend fun compressImage(
        bitmap: Bitmap,
        compressionThreshold: Long
    ): Result<Uri>
}