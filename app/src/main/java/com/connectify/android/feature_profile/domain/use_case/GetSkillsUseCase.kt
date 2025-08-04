package com.connectify.android.feature_profile.domain.use_case

import com.connectify.android.core.util.Resource
import com.connectify.android.feature_profile.domain.models.Skill
import com.connectify.android.core.domain.repository.ProfileRepository

class GetSkillsUseCase(
    private val repository: ProfileRepository
) {

    suspend operator fun invoke(): Resource<List<Skill>> {
        return repository.getSkills()
    }
}