package com.example.connectify.feature_profile.domain.use_case

import com.example.connectify.core.util.Resource
import com.example.connectify.feature_profile.domain.models.Skill
import com.example.connectify.core.domain.repository.ProfileRepository

class GetSkillsUseCase(
    private val repository: ProfileRepository
) {

    suspend operator fun invoke(): Resource<List<Skill>> {
        return repository.getSkills()
    }
}