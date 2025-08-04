package com.connectify.android.feature_profile.domain.use_case

import com.connectify.android.R
import com.connectify.android.core.util.Resource
import com.connectify.android.core.util.UiText
import com.connectify.android.feature_profile.domain.models.Skill
import com.connectify.android.feature_profile.domain.util.ProfileConstants

class SetSkillSelectedUseCase {

    operator fun invoke(
        selectedSkill: List<Skill>,
        skillToToggle: Skill
    ): Resource<List<Skill>> {
        if(skillToToggle in selectedSkill) {
            return Resource.Success(
                data = selectedSkill - skillToToggle
            )
        }
        return if(selectedSkill.size >= ProfileConstants.MAX_SELECTED_SKILL_COUNT) {
            Resource.Error(
                uiText = UiText.StringResource(R.string.error_max_skills_selected)
            )
        } else {
            Resource.Success(
                data = selectedSkill + skillToToggle
            )
        }
    }
}