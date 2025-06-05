package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS

class SwiftnessElement : BaseEffectElement(
    name = "swiftness",
    displayNameResId = R.string.module_swiftness_display_name,
    effectId = Effect.SWIFTNESS,
    effectSetting = EFFECTS.SWIFTNESS
)