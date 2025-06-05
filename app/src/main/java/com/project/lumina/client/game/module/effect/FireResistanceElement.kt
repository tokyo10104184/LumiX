package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS

class FireResistanceElement : BaseEffectElement(
    name = "fire_resistance",
    displayNameResId = R.string.module_fire_resistance_display_name,
    effectId = Effect.FIRE_RESISTANCE,
    effectSetting = EFFECTS.FIRE_RESISTANCE
)