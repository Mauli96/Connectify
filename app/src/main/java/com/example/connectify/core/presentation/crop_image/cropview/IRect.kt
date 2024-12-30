package com.example.connectify.core.presentation.crop_image.cropview

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

data class IRect(
    val topLeft: Offset = Offset(0.0f, 0.0f),
    var size: Size = Size(0.0f, 0.0f)
)

fun IRect.verticalGuidelineDiff(noOfGuideLines: Int): Float {
    return size.height / (noOfGuideLines + 1)
}

fun IRect.horizontalGuidelineDiff(noOfGuideLines: Int): Float {
    return size.width / (noOfGuideLines + 1)
}