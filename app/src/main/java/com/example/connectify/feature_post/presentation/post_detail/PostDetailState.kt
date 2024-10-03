package com.example.connectify.feature_post.presentation.post_detail

import com.example.connectify.core.domain.models.Comment
import com.example.connectify.core.domain.models.Post

data class PostDetailState(
    val post: Post? = null,
    val selectedCommentId: String? = null,
    val isLoadingPost: Boolean = false,
    val isDescriptionVisible: Boolean = false,
    val isDropdownExpanded: Boolean = false,
)