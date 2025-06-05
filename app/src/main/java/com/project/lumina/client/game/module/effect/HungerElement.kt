package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS

class HungerElement : BaseEffectElement(
    name = "hunger",
    displayNameResId = R.string.module_hunger_display_name,
    effectId = Effect.HUNGER,
    effectSetting = EFFECTS.HUNGER
)