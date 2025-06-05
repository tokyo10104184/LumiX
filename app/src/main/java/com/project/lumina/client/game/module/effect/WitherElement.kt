package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS

class WitherElement : BaseEffectElement(
    name = "wither",
    displayNameResId = R.string.module_wither_display_name,
    effectId = Effect.WITHER,
    effectSetting = EFFECTS.WITHER
)