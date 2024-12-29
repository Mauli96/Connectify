package com.example.connectify.core.presentation.crop_image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.connectify.core.presentation.crop_image.cropview.CropType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class CropViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _state = MutableStateFlow(CropState())
    val state = _state.asStateFlow()

    init {
        savedStateHandle.get<String>("imageUri")?.let { imageUriString ->
            _state.update {
                it.copy(
                    imageUri = Uri.parse(imageUriString)
                )
            }
        }
        savedStateHandle.get<String>("cropType")?.let { cropType ->
            _state.update {
                it.copy(
                    cropType = CropType.valueOf(cropType)
                )
            }
        }
    }

    fun onEvent(event: CropEvents) {
        when(event) {
            is CropEvents.CropImageBitmap -> {
                _state.update {
                    it.copy(
                        cropImageBitmap = event.imageBitmap
                    )
                }
            }
            is CropEvents.ChangeCropType -> {
                _state.update {
                    it.copy(
                        cropType = event.cropType
                    )
                }
            }
        }
    }

    private fun updateCropType(cropType: CropType) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    cropType = cropType
                )
            }
        }
    }

    fun saveMediaToStorage(
        bitmap: Bitmap,
        onComplete: (Uri?) -> Unit
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSavingMediaToStore = true
                )
            }

            val filename = "connectify_picture_${System.currentTimeMillis()}.jpg"
            var fileUri: Uri? = null

            try {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = File(imagesDir, filename)
                FileOutputStream(image).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                fileUri = Uri.fromFile(image)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                onComplete(fileUri)
                _state.update {
                    it.copy(
                        isSavingMediaToStore = false
                    )
                }
            }
        }
    }

    fun getBitmapFromUrl(context: Context) {
        viewModelScope.launch {
            val bm = getBitmap(context, state.value.imageUri)
            _state.update {
                it.copy(
                    imageBitmap = bm
                )
            }
        }
    }

    private suspend fun getBitmap(context: Context, imageUrl: Uri?): Bitmap? {
        val loading = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .build()

        val result = (loading.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
    }
}