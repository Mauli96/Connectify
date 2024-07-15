package com.example.connectify.feature_profile.data.repository

import com.example.connectify.R
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.UiText
import com.example.connectify.feature_profile.data.remote.ProfileApi
import com.example.connectify.feature_profile.domain.models.Profile
import com.example.connectify.feature_profile.domain.repository.ProfileRepository
import retrofit2.HttpException
import java.io.IOException

class ProfileRepositoryImpl(
    private val api: ProfileApi
) : ProfileRepository {

    override suspend fun getProfile(userId: String): Resource<Profile> {
        return try {
            val response = api.getProfile(userId)
            if(response.successful) {
                Resource.Success(response.data?.toProfile())
            } else {
                response.message?.let { msg ->
                    Resource.Error(UiText.DynamicString(msg))
                } ?: Resource.Error(UiText.StringResource(R.string.error_unknown))
            }
        } catch(e: IOException) {
            Resource.Error(
                uiText = UiText.StringResource(R.string.error_couldnt_reach_server)
            )
        } catch(e: HttpException) {
            Resource.Error(
                uiText = UiText.StringResource(R.string.oops_something_went_wrong)
            )
        }
    }
}