package com.connectify.android.feature_profile.data.remote.response

import com.connectify.android.feature_profile.domain.models.Skill

data class SkillDto(
    val name: String,
    val imageUrl: String
) {
    fun toSkill(): Skill {
        return Skill(
            name = name,
            imageUrl = imageUrl
        )
    }
}
