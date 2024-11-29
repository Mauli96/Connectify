package com.example.connectify.feature_post.data.remote

import com.example.connectify.core.data.dto.response.BasicApiResponse
import com.example.connectify.core.data.dto.response.UserItemDto
import com.example.connectify.core.domain.models.Post
import com.example.connectify.feature_post.data.remote.request.CreateCommentRequest
import com.example.connectify.feature_post.data.remote.request.LikeUpdateRequest
import com.example.connectify.feature_post.data.remote.response.CommentDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface PostApi {

    @GET("/api/post/get")
    suspend fun getPostsForFollows(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): List<Post>

    @GET("/api/post/details")
    suspend fun getPostDetails(
        @Query("postId") postId: String
    ): BasicApiResponse<Post>

    @GET("/api/user/posts")
    suspend fun getPostsForProfile(
        @Query("userId") userId: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): List<Post>

    @Multipart
    @POST("/api/post/create")
    suspend fun createPost(
        @Part postData: MultipartBody.Part,
        @Part postImage: MultipartBody.Part
    ): BasicApiResponse<Unit>

    @GET("/api/comment/get")
    suspend fun getCommentsForPost(
        @Query("postId") postId: String,
        @Query("filterType") filterType: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): List<CommentDto>

    @POST("/api/comment/create")
    suspend fun createComment(
        @Body request: CreateCommentRequest
    ): BasicApiResponse<Unit>

    @POST("/api/like")
    suspend fun likeParent(
        @Body request: LikeUpdateRequest
    ): BasicApiResponse<Unit>

    @DELETE("/api/unlike")
    suspend fun unlikeParent(
        @Query("parentId") parentId: String,
        @Query("parentType") parentType: Int
    ): BasicApiResponse<Unit>

    @GET("/api/like/parent")
    suspend fun getLikesForParent(
        @Query("parentId") parentId: String
    ): List<UserItemDto>

    @DELETE("/api/post/delete")
    suspend fun deletePost(
        @Query("postId") postId: String,
    )

    @DELETE("api/comment/delete")
    suspend fun deleteComment(
        @Query("commentId") commentId: String
    )

    @GET("/api/post/save/get")
    suspend fun getSavedPosts(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): List<Post>

    @POST("/api/post/save")
    suspend fun savePost(
        @Query("postId") postId: String
    ): BasicApiResponse<Unit>

    @DELETE("/api/post/unsave")
    suspend fun removeSavedPost(
        @Query("postId") postId: String
    ): BasicApiResponse<Unit>

    @GET("api/post/download")
    suspend fun getPostDownloadUrl(
        @Query("postId") postId: String
    ): BasicApiResponse<String>

    companion object {
        const val BASE_URL = "http://192.168.0.100:8001/"
    }
}