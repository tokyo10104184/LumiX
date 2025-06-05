package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS

class ResistanceElement : BaseEffectElement(
    name = "resistance",
    displayNameResId = R.string.module_resistance_display_name,
    effectId = Effect.RESISTANCE,
    effectSetting = EFFECTS.RESISTANCE
)