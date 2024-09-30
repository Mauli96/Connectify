package com.example.connectify.feature_post.presentation.main_feed

import androidx.compose.ui.unit.DpOffset
import com.example.connectify.feature_post.presentation.util.CommentFilter

data class MainFeedState(
    val profilePicture: String? = null,
    val selectedPostId: String? = null,
    val selectedCommentId: String? = null,
    val isUploading: Boolean = false,
    val isBottomSheetVisible: Boolean = false,
    val isDropdownMenuVisible: Boolean = false,
    val hasNavigated: Boolean = false,
    val contextMenuOffset: DpOffset = DpOffset.Zero,
    val commentFilter: CommentFilter = CommentFilter.MOST_RECENT,
)