package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS

class SlowFallingElement : BaseEffectElement(
    name = "slow_falling",
    displayNameResId = R.string.module_slow_falling_display_name,
    effectId = Effect.SLOW_FALLING,
    effectSetting = EFFECTS.SLOW_FALLING
)
