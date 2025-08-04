package com.connectify.android.feature_profile.domain.use_case

import com.connectify.android.core.domain.use_case.ToggleFollowStateForUserUseCase

data class ProfileUseCases(
    val getProfile: GetProfileUseCase,
    val getSkills: GetSkillsUseCase,
    val updateProfile: UpdateProfileUseCase,
    val setSkillUseCase: SetSkillSelectedUseCase,
    val getPostsForProfile: GetPostsForProfileUseCase,
    val searchUser: SearchUserUseCase,
    val toggleFollowStateForUser: ToggleFollowStateForUserUseCase,
    val getFollowsByUser: GetFollowsByUserUseCase,
    val getFollowedToUser: GetFollowedToUserUseCase,
    val logout: LogoutUseCase
)