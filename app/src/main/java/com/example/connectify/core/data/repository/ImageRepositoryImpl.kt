package com.example.connectify.core.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.connectify.core.domain.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

class ImageRepositoryImpl(
    private val context: Context,
    private val imageLoader: ImageLoader
) : ImageRepository {

    private var mimeType: String? = null

    override suspend fun loadImage(uri: Uri): Result<Bitmap> {
        return try {
            mimeType = getMimeTypeFromUri(context, uri)
            val request = ImageRequest
                .Builder(context)
                .data(uri)
                .build()

            val result = (imageLoader.execute(request) as SuccessResult).drawable
            Result.success((result as BitmapDrawable).bitmap)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun compressImage(
        bitmap: Bitmap,
        compressionThreshold: Long
    ): Result<Uri> {
        return withContext(Dispatchers.IO) {
            try {
                val compressFormat = when(mimeType) {
                    "image/png" -> Bitmap.CompressFormat.PNG
                    "image/jpeg" -> Bitmap.CompressFormat.JPEG
                    "image/webp" -> if(Build.VERSION.SDK_INT >= 30) {
                        Bitmap.CompressFormat.WEBP_LOSSLESS
                    } else Bitmap.CompressFormat.WEBP
                    else -> Bitmap.CompressFormat.JPEG
                }

                var quality = 90
                val filename = "compressed_${System.currentTimeMillis()}.${mimeType?.substringAfter('/') ?: "jpg"}"
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val outputFile = File(imagesDir, filename)

                do {
                    FileOutputStream(outputFile).use { outputStream ->
                        bitmap.compress(compressFormat, quality, outputStream)
                        quality -= (quality * 0.1).roundToInt()
                    }
                } while(
                    isActive && outputFile.length() > compressionThreshold &&
                    quality > 5 && compressFormat != Bitmap.CompressFormat.PNG
                )

                Result.success(Uri.fromFile(outputFile))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun getMimeTypeFromUri(
        context: Context,
        uri: Uri
    ): String? {
        return context.contentResolver.getType(uri)
    }
}