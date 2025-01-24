package com.example.connectify.core.domain.use_case

import android.graphics.Bitmap
import android.net.Uri
import com.example.connectify.core.domain.repository.ImageRepository

class CompressImageUseCase(
    private val repository: ImageRepository
) {

    suspend operator fun invoke(bitmap: Bitmap): Result<Uri> {
        return repository.compressImage(
            bitmap = bitmap,
            compressionThreshold = 200 * 1024L
        )
    }
}