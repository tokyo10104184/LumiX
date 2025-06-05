package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS

class SaturationElement : BaseEffectElement(
    name = "saturation",
    displayNameResId = R.string.module_saturation_display_name,
    effectId = Effect.SATURATION,
    effectSetting = EFFECTS.SATURATION
)