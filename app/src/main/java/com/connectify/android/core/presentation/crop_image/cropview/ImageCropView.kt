package com.connectify.android.core.presentation.crop_image.cropview

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class ImageCrop(
    private var bitmapImage: Bitmap
) : OnCrop {

    private lateinit var cropU: CropUtil


    @Composable
    fun ImageCropView(
        modifier: Modifier = Modifier,
        guideLineColor: Color = Color(0xFFD1CBE2),
        guideLineWidth: Dp = 2.dp,
        edgeCircleSize: Dp = 8.dp,
        showGuideLines: Boolean = true,
        cropType: CropType = CropType.FULL_PICTURE,
        edgeType: EdgeType = EdgeType.CIRCULAR
    ) {

        val cropUtil by remember { mutableStateOf(CropUtil(bitmapImage)) }
        cropU = cropUtil

        if (cropU.cropType == null || cropType != cropU.cropType) {
            cropU.updateCropType(cropType)
        }

        Canvas(
            modifier = modifier
                .fillMaxSize()
                .onSizeChanged { intSize ->
                    cropUtil.onCanvasSizeChanged(intSize = intSize)
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { touchPoint ->
                            cropUtil.onDragStart(touchPoint)
                        },
                        onDrag = { pointerInputChange, _ ->
                            // consume the drag points and update the rect
                            pointerInputChange.consume()

                            val dragPoint = pointerInputChange.position
                            cropUtil.onDrag(dragPoint)
                        },
                        onDragEnd = {
                            cropU.onDragEnd()
                        }
                    )
                },

            onDraw = {
                // Draw bitmap image on rect
                drawBitmap(
                    bitmap = bitmapImage,
                    canvasSize = cropUtil.canvasSize
                )

                // Circle
                if (cropType == CropType.PROFILE_PICTURE) {
                    val circleRadius: Float = (cropU.iRect.size.width / 2)
                    val circlePath = Path().apply {
                        addOval(
                            Rect(
                                center = Offset(
                                    cropU.iRect.topLeft.x + (circleRadius),
                                    cropU.iRect.topLeft.y + (circleRadius)
                                ),
                                radius = circleRadius - guideLineWidth.toPx()
                            )
                        )
                    }

                    clipPath(circlePath, clipOp = ClipOp.Difference) {
                        drawRect(SolidColor(Color.Black.copy(alpha = 0.5f)))
                    }
                }

                // Actual crop view rect
                drawCropRectangleView(
                    guideLineColor = guideLineColor,
                    guideLineWidth = guideLineWidth,
                    iRect = cropU.iRect
                )

                if (showGuideLines) {
                    drawGuideLines(
                        noOfGuideLines = 2,
                        guideLineColor = guideLineColor,
                        guideLineWidth = guideLineWidth,
                        iRect = cropU.iRect
                    )
                }

                // Circular edges of crop rect corner
                if (edgeType == EdgeType.CIRCULAR) {
                    drawCircularEdges(
                        edgeCircleSize = edgeCircleSize,
                        guideLineColor = guideLineColor,
                        iRect = cropU.iRect
                    )
                } else {
                    drawSquareBrackets(
                        guideLineColor = guideLineColor,
                        guideLineWidthGiven = guideLineWidth,
                        iRect = cropU.iRect
                    )
                }
            }
        )
    }

    override fun onCrop(): Bitmap {
        this.cropU.updateBitmapImage(bitmapImage)
        return this.cropU.cropImage()
    }

    override fun resetView() {
        this.cropU.resetCropIRect()
    }

}

private interface OnCrop {

    fun onCrop(): Bitmap

    fun resetView()
}