package com.connectify.android.feature_post.domain.use_case

import com.connectify.android.core.domain.models.Comment
import com.connectify.android.core.util.Constants
import com.connectify.android.core.util.Resource
import com.connectify.android.feature_post.domain.repository.PostRepository
import com.connectify.android.feature_post.presentation.util.CommentFilter

class GetCommentsForPostUseCase(
    private val repository: PostRepository
) {

    suspend operator fun invoke(
        postId: String,
        filterType: CommentFilter,
        page: Int,
        pageSize: Int = Constants.DEFAULT_PAGE_SIZE
    ): Resource<List<Comment>> {
        return repository.getCommentsForPost(
            postId = postId,
            filterType = filterType,
            page = page,
            pageSize = pageSize
        )
    }
}