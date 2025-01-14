package com.example.connectify.feature_auth.presentation.on_boarding.component

import androidx.annotation.RawRes
import com.example.connectify.R

sealed class OnBoardingPage(
    @RawRes
    val animation: Int,
    val title: String,
    val description: String
) {
    data object First : OnBoardingPage(
        animation = R.raw.boarding_page_first,
        title = "Your Social Network",
        description = "Discover and connect with friends, share moments, and stay in touch effortlessly."
    )

    data object Second : OnBoardingPage(
        animation = R.raw.boarding_page_second,
        title = "Coordinate Your Activities",
        description = "Organize events and coordinate plans with ease, keeping everyone in the loop."
    )

    data object Third : OnBoardingPage(
        animation = R.raw.boarding_page_third,
        title = "Join the Conversation",
        description = "Engage with friends and followers, share ideas, and grow your community."
    )

    companion object {
        val pages = listOf(First, Second, Third)
    }
}