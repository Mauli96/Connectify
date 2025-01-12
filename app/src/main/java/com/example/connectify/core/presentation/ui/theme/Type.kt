package com.example.connectify.core.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.connectify.R

private object FontSize {
    const val DISPLAY = 24
    const val HEADLINE = 22
    const val TITLE = 20
    const val BODY = 18
    const val LABEL = 16
}

val Quicksand = FontFamily(
    Font(R.font.quicksand_light, FontWeight.Light),
    Font(R.font.quicksand_regular, FontWeight.Normal),
    Font(R.font.quicksand_medium, FontWeight.Medium),
    Font(R.font.quicksand_semibold, FontWeight.SemiBold),
    Font(R.font.quicksand_bold, FontWeight.Bold)
)

private fun buildTextStyle(
    fontSize: Int,
    fontWeight: FontWeight
) = TextStyle(
    fontFamily = Quicksand,
    fontSize = fontSize.sp,
    fontWeight = fontWeight
)

val Typography = Typography(
    // Display styles - Used for the largest text elements
    displayLarge = buildTextStyle(
        fontSize = FontSize.DISPLAY,
        fontWeight = FontWeight.Bold
    ),
    displayMedium = buildTextStyle(
        fontSize = FontSize.DISPLAY,
        fontWeight = FontWeight.Medium
    ),
    displaySmall = buildTextStyle(
        fontSize = FontSize.DISPLAY,
        fontWeight = FontWeight.Normal
    ),

    // Headline styles - Used for important text elements
    headlineLarge = buildTextStyle(
        fontSize = FontSize.HEADLINE,
        fontWeight = FontWeight.Bold
    ),
    headlineMedium = buildTextStyle(
        fontSize = FontSize.HEADLINE,
        fontWeight = FontWeight.Medium
    ),
    headlineSmall = buildTextStyle(
        fontSize = FontSize.HEADLINE,
        fontWeight = FontWeight.Normal
    ),

    // Title styles - Used for medium emphasis text elements
    titleLarge = buildTextStyle(
        fontSize = FontSize.TITLE,
        fontWeight = FontWeight.Bold
    ),
    titleMedium = buildTextStyle(
        fontSize = FontSize.TITLE,
        fontWeight = FontWeight.Medium
    ),
    titleSmall = buildTextStyle(
        fontSize = FontSize.TITLE,
        fontWeight = FontWeight.Normal
    ),

    // Body styles - Used for regular text content
    bodyLarge = buildTextStyle(
        fontSize = FontSize.BODY,
        fontWeight = FontWeight.Bold
    ),
    bodyMedium = buildTextStyle(
        fontSize = FontSize.BODY,
        fontWeight = FontWeight.Medium
    ),
    bodySmall = buildTextStyle(
        fontSize = FontSize.BODY,
        fontWeight = FontWeight.Normal
    ),

    // Label styles - Used for smaller text elements
    labelLarge = buildTextStyle(
        fontSize = FontSize.LABEL,
        fontWeight = FontWeight.Bold
    ),
    labelMedium = buildTextStyle(
        fontSize = FontSize.LABEL,
        fontWeight = FontWeight.Medium
    ),
    labelSmall = buildTextStyle(
        fontSize = FontSize.LABEL,
        fontWeight = FontWeight.Normal
    )
)

fun TextStyle.withSize(size: TextUnit) = copy(fontSize = size)
fun TextStyle.withColor(color: androidx.compose.ui.graphics.Color) = copy(color = color)
fun TextStyle.withLineHeight(height: Int) = copy(lineHeight = height.sp)
fun TextStyle.withLetterSpacing(spacing: Float) = copy(letterSpacing = spacing.sp)