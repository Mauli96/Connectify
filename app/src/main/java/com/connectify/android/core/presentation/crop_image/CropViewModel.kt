package com.connectify.android.core.presentation.crop_image

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.connectify.android.core.domain.use_case.LoadImageUseCase
import com.connectify.android.core.domain.use_case.CompressImageUseCase
import com.connectify.android.core.presentation.crop_image.cropview.CropType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CropViewModel @Inject constructor(
    private val loadImage: LoadImageUseCase,
    private val saveCroppedImage: CompressImageUseCase,
    savedStateHandle: SavedStateHandle
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

        loadImageIfNeeded()
    }

    fun onEvent(event: CropEvents) {
        when(event) {
            is CropEvents.OnCropImageBitmap -> {
                _state.update {
                    it.copy(
                        cropImageBitmap = event.imageBitmap
                    )
                }
            }
            is CropEvents.OnChangeCropType -> {
                _state.update {
                    it.copy(
                        cropType = event.cropType
                    )
                }
            }
        }
    }

    private fun loadImageIfNeeded() {
        val imageUri = state.value.imageUri ?: return
        viewModelScope.launch {
            loadImage(imageUri)
                .onSuccess { bitmap ->
                    _state.update { it.copy(imageBitmap = bitmap) }
                }
                .onFailure { error ->
                    error.printStackTrace()
                }
        }
    }

    fun saveImage(onComplete: (Uri?) -> Unit) {
        val bitmap = state.value.cropImageBitmap ?: return onComplete(null)

        viewModelScope.launch {
            _state.update { it.copy(isSavingMedia = true) }

            try {
                saveCroppedImage(bitmap)
                    .onSuccess { uri -> onComplete(uri) }
                    .onFailure { error ->
                        error.printStackTrace()
                        onComplete(null)
                    }
            } finally {
                _state.update { it.copy(isSavingMedia = false) }
            }
        }
    }
}