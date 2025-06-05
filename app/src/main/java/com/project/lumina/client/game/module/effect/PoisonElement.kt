package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS

class PoisonElement : BaseEffectElement(
    name = "poison",
    displayNameResId = R.string.module_poison_display_name,
    effectId = Effect.POISON,
    effectSetting = EFFECTS.POISON
)