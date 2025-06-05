package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS

class JumpBoostElement : BaseEffectElement(
    name = "jump_boost",
    displayNameResId = R.string.module_jump_boost_display_name,
    effectId = Effect.JUMP_BOOST,
    effectSetting = EFFECTS.JUMP_BOOST
)