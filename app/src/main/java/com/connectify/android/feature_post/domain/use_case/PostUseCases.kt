package com.connectify.android.feature_post.domain.use_case

data class PostUseCases(
    val getPostsForFollows: GetPostsForFollowsUseCase,
    val createPost: CreatePostUseCase,
    val getPostDetails: GetPostDetailsUseCase,
    val getCommentsForPost: GetCommentsForPostUseCase,
    val createComment: CreateCommentUseCase,
    val toggleLikeForParent: ToggleLikeForParentUseCase,
    val getLikesForParent: GetLikesForParentUseCase,
    val deletePost: DeletePostUseCase,
    val deleteComment: DeleteCommentUseCase,
    val getSavedPosts: GetSavedPostsUseCase,
    val toggleSavePost: ToggleSavePostUseCase
)