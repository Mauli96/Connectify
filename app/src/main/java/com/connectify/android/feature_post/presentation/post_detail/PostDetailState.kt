package com.connectify.android.feature_post.presentation.post_detail

import com.connectify.android.core.domain.models.Post

data class PostDetailState(
    val post: Post? = null,
    val selectedPostUsername: String? = null,
    val isOwnPost: Boolean? = false,
    val selectedCommentId: String? = null,
    val isLoadingPost: Boolean = false,
    val isBottomSheetVisible: Boolean = false,
    val isDescriptionVisible: Boolean = false,
    val isDropdownExpanded: Boolean = false,
)