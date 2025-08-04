package com.connectify.android.feature_profile.domain.use_case

import com.connectify.android.core.domain.models.Post
import com.connectify.android.core.util.Resource
import com.connectify.android.core.domain.repository.ProfileRepository
import com.connectify.android.core.util.Constants

class GetPostsForProfileUseCase(
    private val repository: ProfileRepository
) {

    suspend operator fun invoke(
        userId: String,
        page: Int,
        pageSize: Int = Constants.DEFAULT_PAGE_SIZE
    ): Resource<List<Post>> {
        return repository.getPostsPaged(
            userId = userId,
            page = page,
            pageSize = pageSize
        )
    }
}