package com.example.connectify.feature_profile.domain.use_case

data class ProfileUseCases(
    val getProfile: GetProfileUseCase,
    val getSkills: GetSkillsUseCase,
    val updateProfile: UpdateProfileUseCase
)