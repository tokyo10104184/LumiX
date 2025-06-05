package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS

class FatalPoisonElement : BaseEffectElement(
    name = "fatal_poison",
    displayNameResId = R.string.module_fatal_poison_display_name,
    effectId = Effect.FATAL_POISON,
    effectSetting = EFFECTS.FATAL_POISON
)