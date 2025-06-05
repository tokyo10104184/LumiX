package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS

class InvisibilityElement : BaseEffectElement(
    name = "invisibility",
    displayNameResId = R.string.module_invisibility_display_name,
    effectId = Effect.INVISIBILITY,
    effectSetting = EFFECTS.INVISIBILITY
)