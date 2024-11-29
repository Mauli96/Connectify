package com.example.connectify.feature_post.domain.repository

import android.net.Uri
import com.example.connectify.core.domain.models.Comment
import com.example.connectify.core.domain.models.Post
import com.example.connectify.core.domain.models.UserItem
import com.example.connectify.core.util.Constants
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.SimpleResource
import com.example.connectify.feature_post.presentation.util.CommentFilter

interface PostRepository {

    suspend fun getPostsForFollows(
        page: Int,
        pageSize: Int
    ): Resource<List<Post>>

    suspend fun createPost(
        description: String,
        imageUri: Uri
    ): SimpleResource

    suspend fun getPostDetails(postId: String): Resource<Post>

    suspend fun getCommentsForPost(
        postId: String,
        filterType: CommentFilter,
        page: Int,
        pageSize: Int
    ): Resource<List<Comment>>

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

    suspend fun deletePost(postId: String): SimpleResource

    suspend fun deleteComment(commentId: String) : SimpleResource

    suspend fun getSavedPosts(
        page: Int,
        pageSize: Int
    ): Resource<List<Post>>

    suspend fun savePost(postId: String): SimpleResource

    suspend fun removeSavedPost(postId: String): SimpleResource

    suspend fun getPostDownloadUrl(postId: String): Resource<String>
}