package com.example.connectify.feature_post.domain.use_case

import androidx.paging.PagingData
import com.example.connectify.core.domain.models.Post
import com.example.connectify.core.util.Constants
import com.example.connectify.core.util.Resource
import com.example.connectify.feature_post.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow

class GetPostsForFollowsUseCase(
    private val repository: PostRepository
) {

    suspend operator fun invoke(page: Int): Resource<List<Post>> {
        return repository.getPostsForFollows(page)
    }
}