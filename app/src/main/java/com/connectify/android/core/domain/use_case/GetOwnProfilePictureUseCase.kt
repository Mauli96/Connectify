package com.connectify.android.core.domain.use_case

import com.connectify.android.core.domain.repository.ProfileRepository
import com.connectify.android.core.util.Resource

class GetOwnProfilePictureUseCase(
    private val profileRepository: ProfileRepository
) {

    suspend operator fun invoke(): Resource<String> {
        return profileRepository.getOwnProfilePicture()
    }
}