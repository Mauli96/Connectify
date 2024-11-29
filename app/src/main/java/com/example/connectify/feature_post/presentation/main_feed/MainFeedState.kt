package com.example.connectify.feature_post.presentation.main_feed

import com.example.connectify.feature_post.presentation.util.CommentFilter

data class MainFeedState(
    val profilePicture: String? = null,
    val selectedPostId: String? = null,
    val selectedPostUsername: String? = null,
    val isOwnPost: Boolean? = false,
    val selectedCommentId: String? = null,
    val isUploading: Boolean = false,
    val isBottomSheetVisible: Boolean = false,
    val isCommentBottomSheetVisible: Boolean = false,
    val isDropdownMenuVisible: Boolean = false,
    val isNavigatedToSearchScreen: Boolean = false,
    val isNavigatedToPersonListScreen: Boolean = false,
    val isDescriptionVisible: Map<String, Boolean> = emptyMap(),
    val commentFilter: CommentFilter = CommentFilter.MOST_RECENT,
)