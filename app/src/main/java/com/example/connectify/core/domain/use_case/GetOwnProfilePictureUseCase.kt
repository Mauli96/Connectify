package com.example.connectify.core.domain.use_case

import com.example.connectify.core.domain.repository.ProfileRepository
import com.example.connectify.core.util.Resource

class GetOwnProfilePictureUseCase(
    private val profileRepository: ProfileRepository
) {

    suspend operator fun invoke(): Resource<String> {
        return profileRepository.getOwnProfilePicture()
    }
}