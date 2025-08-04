package com.connectify.android.feature_post.presentation.post_detail

import com.connectify.android.feature_post.presentation.util.CommentFilter

data class CommentState(
    val commentFilter: CommentFilter = CommentFilter.MOST_RECENT,
    val isLoading: Boolean = false
)