package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS

class InstantHealthElement : BaseEffectElement(
    name = "instant_health",
    displayNameResId = R.string.module_instant_health_display_name,
    effectId = Effect.INSTANT_HEALTH,
    effectSetting = EFFECTS.INSTANT_HEALTH
)