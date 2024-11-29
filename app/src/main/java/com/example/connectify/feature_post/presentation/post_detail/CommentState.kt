package com.example.connectify.feature_post.presentation.post_detail

import com.example.connectify.feature_post.presentation.util.CommentFilter

data class CommentState(
    val commentFilter: CommentFilter = CommentFilter.MOST_RECENT,
    val isLoading: Boolean = false
)