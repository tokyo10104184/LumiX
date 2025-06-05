package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS

class ConduitPowerElement : BaseEffectElement(
    name = "conduit_power",
    displayNameResId = R.string.module_conduit_power_display_name,
    effectId = Effect.CONDUIT_POWER,
    effectSetting = EFFECTS.CONDUIT_POWER
)