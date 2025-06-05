package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS

class DarknessElement : BaseEffectElement(
    name = "darkness",
    displayNameResId = R.string.module_darkness_display_name,
    effectId = Effect.DARKNESS,
    effectSetting = EFFECTS.DARKNESS
)