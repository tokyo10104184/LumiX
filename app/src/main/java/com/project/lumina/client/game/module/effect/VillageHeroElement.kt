package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS

class VillageHeroElement : BaseEffectElement(
    name = "village_hero",
    displayNameResId = R.string.module_village_hero_display_name,
    effectId = Effect.HERO_OF_THE_VILLAGE,
    effectSetting = EFFECTS.HERO_OF_THE_VILLAGE
)