package com.example.connectify.feature_post.domain.use_case

import com.example.connectify.core.domain.models.Comment
import com.example.connectify.core.util.Resource
import com.example.connectify.feature_post.domain.repository.PostRepository
import com.example.connectify.feature_post.presentation.util.CommentFilter

class GetCommentsForPostUseCase(
    private val repository: PostRepository
) {

    suspend operator fun invoke(postId: String, filterType: CommentFilter): Resource<List<Comment>> {
        return repository.getCommentsForPost(postId, filterType)
    }
}