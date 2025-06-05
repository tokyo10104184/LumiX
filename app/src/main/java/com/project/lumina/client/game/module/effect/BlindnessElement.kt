package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS

class BlindnessElement : BaseEffectElement(
    name = "blindness",
    displayNameResId = R.string.module_blindness_display_name,
    effectId = Effect.BLINDNESS,
    effectSetting = EFFECTS.BLINDNESS
)