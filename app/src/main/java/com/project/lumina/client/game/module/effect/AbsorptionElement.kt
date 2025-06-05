package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS

class AbsorptionElement : BaseEffectElement(
    name = "absorption",
    displayNameResId = R.string.module_absorption_display_name,
    effectId = Effect.ABSORPTION,
    effectSetting = EFFECTS.ABSORPTION
)

