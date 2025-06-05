package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS

class RegenerationElement : BaseEffectElement(
    name = "regeneration",
    displayNameResId = R.string.module_regeneration_display_name,
    effectId = Effect.REGENERATION,
    effectSetting = EFFECTS.REGENERATION
)
