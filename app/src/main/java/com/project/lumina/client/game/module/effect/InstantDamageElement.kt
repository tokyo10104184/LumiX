package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS

class InstantDamageElement : BaseEffectElement(
    name = "instant_damage",
    displayNameResId = R.string.module_instant_damage_display_name,
    effectId = Effect.INSTANT_DAMAGE,
    effectSetting = EFFECTS.INSTANT_DAMAGE
)