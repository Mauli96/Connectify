package com.connectify.android.feature_post.domain.repository

import android.net.Uri
import com.connectify.android.core.domain.models.Comment
import com.connectify.android.core.domain.models.Post
import com.connectify.android.core.domain.models.UserItem
import com.connectify.android.core.util.Resource
import com.connectify.android.core.util.SimpleResource
import com.connectify.android.feature_post.presentation.util.CommentFilter

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