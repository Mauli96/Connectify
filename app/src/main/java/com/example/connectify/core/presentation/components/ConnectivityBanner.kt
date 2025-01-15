package com.example.connectify.core.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.connectify.R
import com.example.connectify.core.domain.states.NetworkConnectionState
import com.example.connectify.core.presentation.ui.theme.IconSizeMediumSmall
import com.example.connectify.core.presentation.ui.theme.SpaceSmall
import com.example.connectify.core.presentation.ui.theme.Typography
import kotlinx.coroutines.delay

@Composable
fun ConnectivityBanner(
    networkState: NetworkConnectionState,
    modifier: Modifier = Modifier
) {

    var showBanner by rememberSaveable {
        mutableStateOf(false)
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.internet))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    val message = when (networkState) {
        NetworkConnectionState.Available -> stringResource(R.string.back_online)
        NetworkConnectionState.Unavailable -> stringResource(R.string.no_internet_connection)
    }
    val backgroundColor = when(networkState) {
        NetworkConnectionState.Available -> MaterialTheme.colorScheme.primary
        NetworkConnectionState.Unavailable -> Color(0xFFFF2c2c)
    }

    LaunchedEffect(networkState) {
        if(networkState == NetworkConnectionState.Unavailable) {
            showBanner = true
        }else if (networkState == NetworkConnectionState.Available) {
            delay(2000)
            showBanner = false
        }
    }

    AnimatedVisibility(
        visible = showBanner,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
    ) {
        Box(
            modifier = modifier
                .padding(horizontal = 40.dp)
                .shadow(10.dp, RoundedCornerShape(12.dp))
                .background(backgroundColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                LottieAnimation(
                    modifier = Modifier.size(IconSizeMediumSmall),
                    composition = composition,
                    progress = {
                        progress
                    },
                )
                Spacer(modifier = Modifier.width(SpaceSmall))
                Text(
                    text = message,
                    style = Typography.labelSmall
                )
            }
        }
    }
}