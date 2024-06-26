package com.example.connectify.feature_post.domain.repository

import android.net.Uri
import com.example.connectify.core.domain.models.Post
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.SimpleResource

interface PostRepository {

    suspend fun getPostsForFollows(page: Int, pageSize: Int): Resource<List<Post>>

    suspend fun createPost(description: String, imageUri: Uri): SimpleResource

    suspend fun getPostDetails(postId: String): Resource<Post>
}