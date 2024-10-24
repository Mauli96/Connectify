package com.example.connectify.feature_post.domain.use_case

import com.example.connectify.core.domain.models.Post
import com.example.connectify.core.util.Constants
import com.example.connectify.core.util.Resource
import com.example.connectify.feature_post.domain.repository.PostRepository

class GetSavedPostsUseCase(
    private val repository: PostRepository
) {

    suspend operator fun invoke(
        page: Int,
        pageSize: Int = Constants.DEFAULT_PAGE_SIZE
    ): Resource<List<Post>> {
        return repository.getSavedPosts(page, pageSize)
    }
}