package com.example.connectify.feature_profile.presentation.edit_profile

import com.example.connectify.feature_profile.domain.models.Skill

data class SkillsState(
    val skills: List<Skill> = emptyList(),
    val selectedSkills: List<Skill> = emptyList()
)
