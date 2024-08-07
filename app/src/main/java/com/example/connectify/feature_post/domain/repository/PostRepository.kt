package com.example.connectify.feature_post.domain.repository

import android.net.Uri
import com.example.connectify.core.domain.models.Comment
import com.example.connectify.core.domain.models.Post
import com.example.connectify.core.domain.models.UserItem
import com.example.connectify.core.util.Constants
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.SimpleResource

interface PostRepository {

    suspend fun getPostsForFollows(
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_PAGE_SIZE
    ): Resource<List<Post>>

    suspend fun createPost(
        description: String,
        imageUri: Uri
    ): SimpleResource

    suspend fun getPostDetails(postId: String): Resource<Post>

    suspend fun getCommentsForPost(postId: String): Resource<List<Comment>>

    suspend fun createComment(
        postId: String,
        comment: String
    ): SimpleResource

    suspend fun likeParent(
        parentId: String,
        parentType: Int
    ): SimpleResource

    suspend fun unlikeParent(
        parentId: String,
        parentType: Int
    ): SimpleResource

    suspend fun getLikesForParent(parentId: String): Resource<List<UserItem>>
}