package com.example.connectify.feature_profile.presentation.profile

import com.example.connectify.feature_post.presentation.util.CommentFilter
import com.example.connectify.feature_profile.domain.models.Profile

data class ProfileState(
    val profile: Profile? = null,
    val profilePicture: String? = null,
    val selectedPostId: String? = null,
    val selectedCommentId: String? = null,
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val isDropdownMenuVisible: Boolean = false,
    val isLogoutDialogVisible: Boolean = false,
    val isBottomSheetVisible: Boolean = false,
    val isDeleteSheetVisible: Boolean = false,
    val isFilterMenuVisible: Boolean = false,
    val commentFilter: CommentFilter = CommentFilter.MOST_RECENT,
)