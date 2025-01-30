package com.example.connectify.feature_profile.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.connectify.core.presentation.components.AnimatedCounter
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.core.presentation.ui.theme.Typography
import com.example.connectify.core.presentation.ui.theme.withSize

@Composable
fun ProfileNumber(
    number: Int,
    text: String,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedCounter(
            count = number,
            onClick = onClick,
            style = Typography.titleMedium
        )
        Spacer(modifier = Modifier.height(SpaceSmall))
        Text(
            text = text,
            style = Typography.labelSmall.withSize(12.sp),
            textAlign = TextAlign.Center
        )
    }
}