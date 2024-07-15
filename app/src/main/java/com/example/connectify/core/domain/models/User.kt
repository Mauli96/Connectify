package com.example.connectify.core.domain.models

data class User(
    val userId: String,
    val username: String,
    val profilePictureUrl: String,
    val description: String,
    val followerCount: Int,
    val followingCount: Int,
    val postCount: Int
)