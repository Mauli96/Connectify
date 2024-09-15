package com.example.connectify.feature_post.presentation.post_detail

import com.example.connectify.core.domain.models.Comment
import com.example.connectify.core.domain.models.Post

data class PostDetailState(
    val post: Post? = null,
    val comments: List<Comment> = emptyList(),
    val selectedCommentId: String? = null,
    val isLoadingPost: Boolean = false,
    val isLoadingComments: Boolean = false,
    val isDropdownExpanded: Boolean = false,
    val isBottomSheetVisible: Boolean = false
)