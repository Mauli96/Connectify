package com.example.connectify.core.presentation.crop_image.cropview

import android.graphics.Bitmap
import android.graphics.PointF
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CropUtil(
    private var mBitmapImage: Bitmap
) {

    var cropType: CropType? = null
    private var bitmapImage: Bitmap? = mBitmapImage

    var canvasSize: CanvasSize by mutableStateOf(CanvasSize())
    var iRect: IRect by mutableStateOf(IRect())
    private var touchRect: IRect by mutableStateOf(IRect())
    private var isTouchedInsideRectMove: Boolean by mutableStateOf(false)
    private var rectEdgeTouched: RectEdge by mutableStateOf(RectEdge.NULL)
    private var irectTopleft: Offset by mutableStateOf(Offset(0.0f, 0.0f))
    private var touchAreaRectTopLeft: Offset by mutableStateOf(Offset(0.0f, 0.0f))
    private val paddingForTouchRect = 70F
    private val minLimit: Float = paddingForTouchRect * 3F

    private var maxSquareLimit: Float = 0F
        set(value) {
            minSquareLimit = value * 0.2F
            field = value
        }

    private var minSquareLimit: Float = maxSquareLimit * 0.3F
    private var lastPointUpdated: Offset? = null

    init {
        resetCropIRect()
    }

    fun onCanvasSizeChanged(intSize: IntSize) {
        canvasSize = CanvasSize(
            intSize.width.toFloat(),
            intSize.height.toFloat()
        )
        resetCropIRect()
    }

    fun resetCropIRect() {
        // Irect resetting
        val canWidth = canvasSize.canvasWidth
        val canHeight = canvasSize.canvasHeight

        when(getCurrCropType()) {
            CropType.POST_PICTURE -> {
                val width = minOf(canWidth - 100F, (canHeight - 100F) * 0.8F) // 4/5 = 0.8
                val height = width * 1.25F // 5/4 = 1.25
                irectTopleft = Offset(
                    x = (canWidth - width) / 2,
                    y = (canHeight - height) / 2
                )
                iRect = IRect(topLeft = irectTopleft, size = Size(width, height))
            }
            CropType.PROFILE_PICTURE -> {
                // Square style Rect positioning
                val squareSize = getSquareSize(canWidth, canHeight)
                irectTopleft = getSquarePosition(canWidth, canHeight, squareSize.width)
                iRect = IRect(topLeft = irectTopleft, size = squareSize)
            }
            else -> {
                // Free style Rect positioning
                irectTopleft = Offset(x = 0.0F, y = 0.0F)
                iRect = IRect(topLeft = irectTopleft, size = Size(canWidth, canHeight))
            }
        }
        updateTouchRect()
    }

    private fun getSquareSize(width: Float, height: Float): Size {
        val squareSize = minOf(width, height) - 100F
        maxSquareLimit = squareSize + 100F
        return Size(squareSize, squareSize)
    }

    private fun getSquarePosition(width: Float, height: Float, squareSize: Float): Offset {
        val x = (width - squareSize) / 2
        val y = (height - squareSize) / 2
        return Offset(x, y)
    }


    private fun updateTouchRect() {
        // Touch rect resetting
        val size = iRect.size
        val insidePadding = (paddingForTouchRect * 2)
        val touchRectTopleft = Offset(
            x = (irectTopleft.x + paddingForTouchRect),
            y = (irectTopleft.y + paddingForTouchRect)
        )
        touchRect = IRect(
            topLeft = touchRectTopleft,
            size = Size(
                width = (size.width - (insidePadding)),
                height = (size.height - (insidePadding))
            )
        )
    }

    fun onDragStart(touchPoint: Offset) { // First event of pointer input
        isTouchedInsideRectMove = isTouchInputInsideTheTouchRect(touchPoint)
        rectEdgeTouched = getRectEdge(touchPoint)
        lastPointUpdated = touchPoint
    }

    fun onDrag(dragPoint: Offset) { // Second event of pointer input
        if (isTouchedInsideRectMove) {
            processIRectDrag(dragPoint = dragPoint)
        } else {
            when (rectEdgeTouched) {
                RectEdge.TOP_LEFT -> {
                    topLeftCornerDrag(dragPoint)
                }

                RectEdge.TOP_RIGHT -> {
                    topRightCornerDrag(dragPoint)
                }

                RectEdge.BOTTOM_LEFT -> {
                    bottomLeftCornerDrag(dragPoint)
                }

                RectEdge.BOTTOM_RIGHT -> {
                    bottomRightCornerDrag(dragPoint)
                }

                else -> Unit
            }
        }
    }

    fun onDragEnd() { // Third event of pointer input
        isTouchedInsideRectMove = false
        lastPointUpdated = null
        rectEdgeTouched = RectEdge.NULL
    }


    private fun processIRectDrag(dragPoint: Offset) {
        dragDiffCalculation(dragPoint)?.let { diffOffset ->
            val offsetCheck = Offset(
                x = (irectTopleft.x + diffOffset.x),
                y = (irectTopleft.y + diffOffset.y)
            )

            // before updating the top left point in rect need to check the irect stays inside the canvas
            val isIRectStaysInsideCanvas = isDragPointInsideTheCanvas(offsetCheck)

            // one point may reach any of corner but other way rect can still move
            if (offsetCheck.x >= 0F && offsetCheck.y >= 0F && isIRectStaysInsideCanvas) {
                updateIRectTopLeftPoint(offsetCheck)
            } else {
                // one point may reach any of corner but other way rect can still move
                val x = offsetCheck.x
                val y = offsetCheck.y
                var newOffset: Offset? = null

                if (y <= 0F && x > 0.0F && (x + iRect.size.width in 0F..canvasSize.canvasWidth)) {
                    // top side touched to edge
                    newOffset = Offset(x, 0.0F)

                } else if (x <= 0F && y > 0F && (y + iRect.size.height in 0F..canvasSize.canvasHeight)) {
                    // left side touched to edge
                    newOffset = Offset(0.0F, y)

                } else if ((x + iRect.size.width >= canvasSize.canvasWidth) && y >= 0F
                    && (y + iRect.size.height in 0F..canvasSize.canvasHeight)
                ) {
                    // right side touched to edge
                    newOffset = Offset((canvasSize.canvasWidth - iRect.size.width), y)

                } else if ((y + iRect.size.height >= canvasSize.canvasHeight) &&
                    x > 0F &&
                    (x + iRect.size.width in 0F..canvasSize.canvasWidth)
                ) {
                    // bottom side touched to edge
                    newOffset = Offset(x, (canvasSize.canvasHeight - iRect.size.height))
                }
                if (newOffset != null) {
                    updateIRectTopLeftPoint(newOffset)
                }
            }
        }
    }

    private fun maintainAspectRatio(newWidth: Float, newHeight: Float): Size {
        return when (cropType) {
            CropType.POST_PICTURE -> {
                // Maintain 4:5 aspect ratio
                val targetRatio = 0.8F // 4:5 ratio
                if (newWidth / newHeight > targetRatio) {
                    // Width is too large, adjust it
                    Size(newHeight * targetRatio, newHeight)
                } else {
                    // Height is too large, adjust it
                    Size(newWidth, newWidth / targetRatio)
                }
            }
            CropType.PROFILE_PICTURE -> {
                // Maintain square aspect ratio
                val size = min(newWidth, newHeight)
                Size(size, size)
            }
            else -> Size(newWidth, newHeight)
        }
    }

    private fun topLeftCornerDrag(dragPoint: Offset) {
        dragDiffCalculation(dragPoint)?.let { dragDiff ->
            val (canvasWidth, canvasHeight) = canvasSize
            val size = iRect.size


            val x = (0f.coerceAtLeast(irectTopleft.x + dragDiff.x))
                .coerceAtMost(canvasWidth - minLimit)

            val y = (0f.coerceAtLeast(irectTopleft.y + dragDiff.y))
                .coerceAtMost(canvasHeight - minLimit)


            // Calculate new width and height based on drag direction
            val newWidth = calculateNewSize(size.width, dragDiff.x)
            val newHeight = calculateNewSize(size.height, dragDiff.y)

            irectTopleft = Offset(x, y)

            val sizeOfIRect = when(cropType) {
                CropType.POST_PICTURE -> {
                    val maintainedSize = maintainAspectRatio(newWidth, newHeight)
                    val totalHeight = (maintainedSize.height + irectTopleft.y)
                    val diff = canvasHeight - totalHeight
                    if (diff < 0) {
                        irectTopleft = irectTopleft.copy(y = (irectTopleft.y + diff))
                    }

                    maintainedSize
                }
                CropType.PROFILE_PICTURE -> {
                    val sqSide = min(newWidth, canvasWidth)
                    val totalHeight = (sqSide + irectTopleft.y)
                    val diff = canvasHeight - totalHeight
                    if (diff < 0) {
                        irectTopleft = irectTopleft.copy(
                            y = (irectTopleft.y + diff)
                        )
                    }

                    Size(width = sqSide, height = sqSide)
                }
                else -> {
                    Size(
                        width = min(newWidth, canvasWidth),
                        height = min(newHeight, canvasHeight)
                    )
                }
            }

            iRect = iRect.copy(
                topLeft = irectTopleft,
                size = sizeOfIRect
            )

            updateTouchRect()
        }
    }

    private fun calculateNewSize(currentSize: Float, dragDiff: Float): Float {
        return if (dragDiff < 0F) {
            // Dimension will increase
            (currentSize + abs(dragDiff))
        } else {
            // Dimension will reduce
            max(currentSize - abs(dragDiff), minLimit)
        }
    }

    private fun topRightCornerDrag(dragPoint: Offset) {
        dragDiffCalculation(dragPoint)?.let { dragDiff ->
            // If irect y is already at 0 and dragDiff y is negative, no need to update
            if (iRect.topLeft.y <= 0F && dragDiff.y < 0F) return

            val size = iRect.size
            val (canvasWidth, canvasHeight) = canvasSize
            val irectX = iRect.topLeft.x
            val irectY = iRect.topLeft.y

            // Calculate new width based on drag direction
            val newWidth = if (dragDiff.x < 0F) {
                (size.width - abs(dragDiff.x))
            } else (size.width + abs(dragDiff.x))

            // Limit width based on canvas boundaries
            val width = if ((newWidth + irectX) > canvasWidth) {
                canvasWidth - irectX
            } else {
                if (newWidth <= minLimit) return
                newWidth
            }

            // Calculate new height based on drag direction
            var height = if (dragDiff.y <= 0F) {
                (size.height + abs(dragDiff.y))
            } else {
                (size.height - abs(dragDiff.y))
            }

            // Limit height based on canvas boundaries
            if (height > canvasHeight) height = canvasHeight

            // Calculate new y-point within canvas boundaries
            val yLimitPoint = canvasHeight - minLimit
            var yPoint = irectY + dragDiff.y
            yPoint = if (yPoint <= 0F) 0F else {
                if (yPoint >= yLimitPoint) yLimitPoint else yPoint
            }
            // Update top-left point and rectangle size
            irectTopleft = irectTopleft.copy(y = yPoint)

            val sizeOfIRect = when (cropType) {
                CropType.POST_PICTURE -> {
                    val maintainedSize = maintainAspectRatio(newWidth, height)
                    val totalHeight = (maintainedSize.height + irectTopleft.y)
                    val diff = canvasHeight - totalHeight
                    if (diff < 0) {
                        irectTopleft = irectTopleft.copy(y = (irectTopleft.y + diff))
                    }

                    maintainedSize
                }
                CropType.PROFILE_PICTURE -> {
                    val sqSide = maxOf(minLimit, width)
                    val totalHeight = (sqSide + irectTopleft.y)
                    val diff = canvasHeight - totalHeight

                    if (diff < 0) {
                        irectTopleft = irectTopleft.copy(
                            y = (irectTopleft.y + diff)
                        )
                    }
                    Size(width = sqSide, height = sqSide)
                }

                else -> { // Free style
                    Size(width = maxOf(minLimit, width), height = maxOf(minLimit, height))
                }
            }


            iRect = iRect.copy(
                topLeft = irectTopleft,
                size = sizeOfIRect
            )

            updateTouchRect()
        }
    }

    private fun bottomLeftCornerDrag(dragPoint: Offset) {
        dragDiffCalculation(dragPoint)?.let { dragDiff ->
            val canvasHeight = canvasSize.canvasHeight
            val size = iRect.size

            // For Y-Axis
            val h = (size.height + dragDiff.y)
            val height = if ((h + iRect.topLeft.y) > (canvasSize.canvasHeight)) {
                (canvasSize.canvasHeight - iRect.topLeft.y)
            } else h


            // For X-Axis
            val x = if ((iRect.topLeft.x + dragDiff.x) >= (canvasSize.canvasWidth - minLimit)) {
                canvasSize.canvasWidth - minLimit
            } else {
                val a = iRect.topLeft.x + dragDiff.x
                if (a < 0F) return
                a
            }

            // Update top-left point and rectangle size
            irectTopleft = Offset(x = if (x < 0F) 0F else x, y = iRect.topLeft.y)

            // For Irect Width
            var width = if (dragDiff.x < 0F) {
                (size.width + abs(dragDiff.x))
            } else (size.width - abs(dragDiff.x))

            if (width >= canvasSize.canvasWidth) width = canvasSize.canvasWidth

            val sizeOfIRect = when (cropType) {
                CropType.POST_PICTURE -> {
                    val maintainedSize = maintainAspectRatio(width, height)
                    val totalHeight = (maintainedSize.height + irectTopleft.y)
                    val diff = canvasHeight - totalHeight
                    if (diff < 0) {
                        irectTopleft = irectTopleft.copy(y = (irectTopleft.y + diff))
                    }

                    maintainedSize
                }
                CropType.PROFILE_PICTURE -> {
                    val sqSide = maxOf(minLimit, width)
                    val totalHeight = (sqSide + irectTopleft.y)
                    val diff = canvasHeight - totalHeight

                    if (diff < 0) {
                        irectTopleft = irectTopleft.copy(
                            y = (irectTopleft.y + diff)
                        )
                    }

                    Size(width = sqSide, height = sqSide)
                }

                else -> {
                    Size(width = maxOf(minLimit, width), height = maxOf(minLimit, height))
                }

            }

            iRect = iRect.copy(
                topLeft = irectTopleft,
                size = sizeOfIRect
            )

            updateTouchRect()
        }
    }

    private fun bottomRightCornerDrag(dragPoint: Offset) {
        dragDiffCalculation(dragPoint)?.let { dragDiff ->
            val canvasHeight = canvasSize.canvasHeight
            val (sizeWidth, sizeHeight) = iRect.size

            val newWidth = (sizeWidth + dragDiff.x)
                .coerceAtMost(canvasSize.canvasWidth - iRect.topLeft.x)
            val newHeight = (sizeHeight + dragDiff.y)
                .coerceAtMost(canvasSize.canvasHeight - iRect.topLeft.y)

            val sizeOfIrect = when (cropType) {
                CropType.POST_PICTURE -> {
                    val maintainedSize = maintainAspectRatio(newWidth, newHeight)
                    val totalHeight = (maintainedSize.height + irectTopleft.y)
                    val diff = canvasHeight - totalHeight
                    if (diff < 0) {
                        irectTopleft = irectTopleft.copy(y = (irectTopleft.y + diff))
                    }

                    maintainedSize
                }
                CropType.PROFILE_PICTURE -> {
                    val sqSide = minLimit.coerceAtLeast(newWidth)
                    val totalHeight = (sqSide + irectTopleft.y)
                    val diff = canvasHeight - totalHeight

                    if (diff < 0) {
                        irectTopleft = irectTopleft.copy(
                            y = (irectTopleft.y + diff)
                        )
                    }
                    Size(width = sqSide, height = sqSide)
                }

                else -> { // Free style
                    Size(
                        width = newWidth.coerceAtLeast(minLimit),
                        height = newHeight.coerceAtLeast(minLimit)
                    )
                }
            }

            // Update rectangle size
            iRect = iRect.copy(topLeft = irectTopleft, size = sizeOfIrect)
            updateTouchRect()
        }
    }

    private fun updateIRectTopLeftPoint(offset: Offset) {
        irectTopleft = Offset(
            x = offset.x,
            y = offset.y
        )

        touchAreaRectTopLeft = Offset(
            x = (irectTopleft.x + paddingForTouchRect),
            y = (irectTopleft.y + paddingForTouchRect)
        )

        iRect = iRect.copy(
            topLeft = irectTopleft
        )
        touchRect = touchRect.copy(
            topLeft = touchAreaRectTopLeft
        )
    }

    private fun isDragPointInsideTheCanvas(dragPoint: Offset): Boolean {
        val x = (dragPoint.x + iRect.size.width)
        val y = (dragPoint.y + iRect.size.height)
        return (x in 0F..canvasSize.canvasWidth && y in 0F..canvasSize.canvasHeight)
    }

    private fun dragDiffCalculation(dragPoint: Offset): Offset? {
        if (lastPointUpdated != null && lastPointUpdated != dragPoint) {
            val difference = getDiffBetweenTwoOffset(lastPointUpdated!!, dragPoint)
            lastPointUpdated = dragPoint
            // Return the difference in coordinates
            return Offset(difference.x, difference.y)
        }
        lastPointUpdated = dragPoint
        return null
    }

    private fun getDiffBetweenTwoOffset(pointOne: Offset, pointTwo: Offset): PointF {
        val dx = pointTwo.x - pointOne.x // calculate the difference in the x coordinates
        val dy = pointTwo.y - pointOne.y // calculate the difference in the y coordinates
        // pointF holds the two x,y values
        return PointF(dx, dy)
    }

    private fun getRectEdge(touchPoint: Offset): RectEdge {
        val iRectSize = iRect.size
        val topleftX = iRect.topLeft.x
        val topleftY = iRect.topLeft.y

        val rectWidth = (topleftX + iRectSize.width)
        val rectHeight = (iRect.topLeft.y + iRectSize.height)

        val padding = minLimit

        // For bottom right edge
        val width = (touchPoint.x in (rectWidth - padding..rectWidth + padding))
        val height = (touchPoint.y in (rectHeight - padding..rectHeight + padding))

        // For bottom left edge
        val widthLeft = (touchPoint.x in (topleftX - padding..topleftX + padding))

        // For top right edge
        val isOnY = (touchPoint.y in (topleftY - padding..topleftY + padding))

        // For top left edge
        val x = (touchPoint.x in (topleftX - padding..topleftX + padding))
        val y = (touchPoint.y in (topleftY - padding..topleftY + padding))


        if (width && height) {
            // BOTTOM_RIGHT edge
            return RectEdge.BOTTOM_RIGHT
        } else if (height && widthLeft) {
            // BOTTOM_LEFT edge
            return RectEdge.BOTTOM_LEFT
        } else if (width && isOnY) {
            // TOP_RIGHT edge
            return RectEdge.TOP_RIGHT
        } else if (x && y) {
            // TOP_LEFT
            return RectEdge.TOP_LEFT
        }

        return RectEdge.NULL
    }

    private fun isTouchInputInsideTheTouchRect(touchPoint: Offset): Boolean {
        val xStartPoint = touchRect.topLeft.x
        val xEndPoint = (touchRect.topLeft.x + touchRect.size.width)

        val yStartPoint = touchRect.topLeft.y
        val yEndPoint = (touchRect.topLeft.y + touchRect.size.height)

        return (touchPoint.x in xStartPoint..xEndPoint && touchPoint.y in yStartPoint..yEndPoint)
    }

    fun cropImage(): Bitmap {
        val canvasWidth = canvasSize.canvasWidth.toInt()
        val canvasHeight = canvasSize.canvasHeight.toInt()
        val rect = getRectFromPoints()

        // Create scaled source bitmap
        val bitmap: Bitmap = bitmapImage?.let {
            Bitmap.createScaledBitmap(it, maxOf(1, canvasWidth), maxOf(1, canvasHeight), true)
        } ?: Bitmap.createScaledBitmap(mBitmapImage, maxOf(1, canvasWidth), maxOf(1, canvasHeight), true)

        // Calculate crop dimensions
        var imgLeft = maxOf(0, rect.left.toInt())
        var imgTop = maxOf(0, rect.top.toInt())
        var imgWidth = minOf(rect.width.toInt(), canvasWidth - imgLeft)
        var imgHeight = minOf(rect.height.toInt(), canvasHeight - imgTop)

        // Ensure minimum dimensions
        imgWidth = maxOf(1, imgWidth)
        imgHeight = maxOf(1, imgHeight)

        // Create initial crop
        val cropBitmap = try {
            Bitmap.createBitmap(
                bitmap,
                imgLeft,
                imgTop,
                imgWidth,
                imgHeight
            )
        } catch (e: IllegalArgumentException) {
            bitmap
        }

        return when (cropType) {
            CropType.POST_PICTURE -> {
                // Calculate dimensions maintaining 4:5 aspect ratio
                val aspectRatio = 4f / 5f // Width to height ratio
                val maxWidth = 1080 // Standard Instagram post width
                val maxHeight = (maxWidth / aspectRatio).toInt() // Will be 1350

                // Calculate target dimensions while maintaining aspect ratio
                val currentRatio = imgWidth.toFloat() / imgHeight.toFloat()

                val (targetWidth, targetHeight) = if (currentRatio > aspectRatio) {
                    // Current image is too wide
                    val width = (imgHeight * aspectRatio).toInt()
                    Pair(width, imgHeight)
                } else {
                    // Current image is too tall
                    val height = (imgWidth / aspectRatio).toInt()
                    Pair(imgWidth, height)
                }

                // Scale to final dimensions
                val finalWidth = minOf(maxWidth, targetWidth)
                val finalHeight = minOf(maxHeight, targetHeight)

                Bitmap.createScaledBitmap(
                    cropBitmap,
                    maxOf(1, finalWidth),
                    maxOf(1, finalHeight),
                    true
                )
            }
            CropType.PROFILE_PICTURE -> {
                val targetSize = maxOf(1, maxSquareLimit.toInt())
                Bitmap.createScaledBitmap(
                    cropBitmap,
                    targetSize,
                    targetSize,
                    true
                )
            }
            else -> Bitmap.createScaledBitmap(
                cropBitmap,
                maxOf(1, canvasWidth),
                maxOf(1, canvasHeight),
                true
            )
        }
    }

    fun updateCropType(type: CropType) {
        cropType = type
        resetCropIRect()
    }

    private fun getCurrCropType(): CropType {
        return cropType ?: CropType.FULL_PICTURE
    }

    fun updateBitmapImage(bitmap: Bitmap) {
        bitmapImage = bitmap
    }

    private fun getRectFromPoints(): Rect {
        val size = iRect.size
        val right = (size.width + irectTopleft.x)
        val bottom = (size.height + irectTopleft.y)
        return Rect(
            irectTopleft.x,    //left
            irectTopleft.y,    //top
            right,             //right
            bottom,            //bottom
        )
    }
}