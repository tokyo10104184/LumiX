package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS

class HealthBoostElement : BaseEffectElement(
    name = "health_boost",
    displayNameResId = R.string.module_health_boost_display_name,
    effectId = Effect.HEALTH_BOOST,
    effectSetting = EFFECTS.HEALTH_BOOST
)