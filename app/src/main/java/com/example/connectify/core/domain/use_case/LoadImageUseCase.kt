package com.example.connectify.core.domain.use_case

import android.graphics.Bitmap
import android.net.Uri
import com.example.connectify.core.domain.repository.ImageRepository

class LoadImageUseCase(
    private val repository: ImageRepository
) {

    suspend operator fun invoke(uri: Uri): Result<Bitmap> {
        return repository.loadImage(uri)
    }
}