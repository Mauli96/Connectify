package com.connectify.android.feature_post.domain.use_case

import com.connectify.android.core.domain.models.Post
import com.connectify.android.core.util.Constants
import com.connectify.android.core.util.Resource
import com.connectify.android.feature_post.domain.repository.PostRepository

class GetPostsForFollowsUseCase(
    private val repository: PostRepository
) {

    suspend operator fun invoke(
        page: Int,
        pageSize: Int = Constants.DEFAULT_PAGE_SIZE
    ): Resource<List<Post>> {
        return repository.getPostsForFollows(page, pageSize)
    }
}