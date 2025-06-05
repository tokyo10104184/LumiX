package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS

class LevitationElement : BaseEffectElement(
    name = "levitation",
    displayNameResId = R.string.module_levitation_display_name,
    effectId = Effect.LEVITATION,
    effectSetting = EFFECTS.LEVITATION
)