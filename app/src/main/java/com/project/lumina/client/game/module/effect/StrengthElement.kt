package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS

class StrengthElement : BaseEffectElement(
    name = "strength",
    displayNameResId = R.string.module_strength_display_name,
    effectId = Effect.STRENGTH,
    effectSetting = EFFECTS.STRENGTH
)