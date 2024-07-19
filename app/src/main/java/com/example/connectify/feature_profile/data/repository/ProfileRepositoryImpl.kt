package com.example.connectify.feature_profile.data.repository

import android.net.Uri
import androidx.core.net.toFile
import com.example.connectify.R
import com.example.connectify.core.util.Resource
import com.example.connectify.core.util.SimpleResource
import com.example.connectify.core.util.UiText
import com.example.connectify.feature_profile.data.remote.ProfileApi
import com.example.connectify.feature_profile.domain.models.Profile
import com.example.connectify.feature_profile.domain.models.Skill
import com.example.connectify.feature_profile.domain.models.UpdateProfileData
import com.example.connectify.feature_profile.domain.repository.ProfileRepository
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.IOException

class ProfileRepositoryImpl(
    private val api: ProfileApi,
    private val gson: Gson
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

    override suspend fun updateProfile(
        bannerImageUri: Uri?,
        profilePictureUri: Uri?,
        updateProfileData: UpdateProfileData
    ): SimpleResource {
        val bannerImageFile = bannerImageUri?.toFile()
        val profilePictureFile = profilePictureUri?.toFile()

        return try {
            val response = api.updateProfile(
                bannerImage = bannerImageFile?.let {
                    MultipartBody.Part
                        .createFormData(
                            name = "banner_image",
                            filename = bannerImageFile.name,
                            body = bannerImageFile.asRequestBody()
                        )
                },
                profilePicture = profilePictureFile?.let {
                    MultipartBody.Part
                        .createFormData(
                            name = "profile_picture",
                            filename = profilePictureFile.name,
                            body = profilePictureFile.asRequestBody()
                        )
                },
                updateProfileData = MultipartBody.Part
                    .createFormData(
                        name = "update_profile_data",
                        value = gson.toJson(updateProfileData)
                    )
            )
            if(response.successful) {
                Resource.Success(Unit)
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

    override suspend fun getSkills(): Resource<List<Skill>> {
        return try {
            val response = api.getSkills()
            Resource.Success(
                data = response.map { it.toSkill() }
            )
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