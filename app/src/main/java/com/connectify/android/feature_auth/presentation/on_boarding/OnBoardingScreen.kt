package com.connectify.android.feature_auth.presentation.on_boarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.connectify.android.R
import com.connectify.android.core.presentation.ui.theme.DarkGray
import com.connectify.android.core.presentation.ui.theme.SpaceMedium
import com.connectify.android.core.presentation.ui.theme.Typography
import com.connectify.android.core.presentation.ui.theme.withColor
import com.connectify.android.core.presentation.ui.theme.withSize
import com.connectify.android.feature_auth.presentation.on_boarding.component.OnBoardingPage
import com.google.accompanist.pager.HorizontalPagerIndicator

@Composable
fun OnBoardingScreen(
    onSaveOnBoarding: () -> Unit = {},
    onBoardingViewModel: OnBoardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { OnBoardingPage.pages.size }
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            modifier = Modifier.weight(8f),
            state = pagerState,
            verticalAlignment = Alignment.Top
        ) { position ->
            PagerScreen(onBoardingPage = OnBoardingPage.pages[position])
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            pageCount = OnBoardingPage.pages.size,
            activeColor = MaterialTheme.colorScheme.primary,
            inactiveColor = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .weight(1f)
        )
        FinishButton(
            pagerState = pagerState,
            modifier = Modifier.weight(1f)
        ) {
            onBoardingViewModel.onSaveOnBoardingState(completed = true)
            onSaveOnBoarding()
        }
    }
}

@Composable
fun PagerScreen(
    onBoardingPage: OnBoardingPage
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(onBoardingPage.animation))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        LottieAnimation(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .fillMaxHeight(0.85f),
            composition = composition,
            progress = { progress }
        )
        Column(
            modifier = Modifier
                .weight(2f)
                .padding(horizontal = SpaceMedium),
            verticalArrangement = Arrangement.spacedBy(SpaceMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = onBoardingPage.title,
                style = Typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = onBoardingPage.description,
                style = Typography.labelSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FinishButton(
    modifier: Modifier,
    pagerState: PagerState,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = SpaceMedium),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            modifier = Modifier.fillMaxWidth(),
            visible = pagerState.currentPage == 2,
            enter = fadeIn(
                initialAlpha = 0f,
                animationSpec = tween(durationMillis = 300)
            ),
            exit = fadeOut(
                targetAlpha = 0f,
                animationSpec = tween(durationMillis = 300)
            )
        ) {
            Button(
                onClick = onClick,
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.lets_connect),
                    style = Typography.labelMedium
                        .withSize(15.sp)
                        .withColor(DarkGray)
                )
            }
        }
    }
}