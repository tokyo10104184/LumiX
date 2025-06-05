package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS

class BadOmenElement : BaseEffectElement(
    name = "bad_omen",
    displayNameResId = R.string.module_bad_omen_display_name,
    effectId = Effect.BAD_OMEN,
    effectSetting = EFFECTS.BAD_OMEN
)