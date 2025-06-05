package com.project.lumina.client.game.module.impl.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.InterceptablePacket
import com.project.lumina.client.game.utils.constants.Effect
import com.project.lumina.client.game.module.api.setting.EFFECTS
import org.cloudburstmc.math.vector.Vector3f
import org.cloudburstmc.protocol.bedrock.data.PlayerAuthInputData
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket
import org.cloudburstmc.protocol.bedrock.packet.SetEntityMotionPacket
import kotlin.math.cos
import kotlin.math.sin

class PoseidonElement : BaseEffectElement(
    name = "poseidon",
    displayNameResId = R.string.module_poseidon_display_name,
    effectId = Effect.WATER_BREATHING,
    effectSetting = EFFECTS.WATER_BREATHING
) {
    private val speedMultiplier = 1.5f

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled) return

        val packet = interceptablePacket.packet
        if (packet is PlayerAuthInputPacket) {
            handlePlayerMovement(packet)
            if (session.localPlayer.tickExists % 20 == 0L) {
                addEffect()
                addEffect(Effect.NIGHT_VISION)
            }
        }
    }

    private fun handlePlayerMovement(packet: PlayerAuthInputPacket) {
        if (packet.inputData.contains(PlayerAuthInputData.START_SWIMMING) ||
            packet.inputData.contains(PlayerAuthInputData.AUTO_JUMPING_IN_WATER) ||
            session.localPlayer.motionY < 0
        ) {
            val yaw = Math.toRadians(packet.rotation.y.toDouble())
            val pitch = Math.toRadians(packet.rotation.x.toDouble())
            val motionX = -sin(yaw) * cos(pitch) * speedMultiplier
            val motionZ = cos(yaw) * cos(pitch) * speedMultiplier

            session.clientBound(SetEntityMotionPacket().apply {
                runtimeEntityId = session.localPlayer.runtimeEntityId
                motion = Vector3f.from(motionX.toFloat(), 0.05f, motionZ.toFloat())
            })

            packet.inputData.remove(PlayerAuthInputData.START_SWIMMING)
            packet.inputData.remove(PlayerAuthInputData.AUTO_JUMPING_IN_WATER)
        }
    }

    override fun onDisabled() {
        super.onDisabled()
        if (isSessionCreated) {
            removeEffect()
            removeEffect(Effect.NIGHT_VISION)
        }
    }
}

class AbsorptionElement : BaseEffectElement(
    name = "absorption",
    displayNameResId = R.string.module_absorption_display_name,
    effectId = Effect.ABSORPTION,
    effectSetting = EFFECTS.ABSORPTION
)

class DarknessElement : BaseEffectElement(
    name = "darkness",
    displayNameResId = R.string.module_darkness_display_name,
    effectId = Effect.DARKNESS,
    effectSetting = EFFECTS.DARKNESS
)

class ConduitPowerElement : BaseEffectElement(
    name = "conduit_power",
    displayNameResId = R.string.module_conduit_power_display_name,
    effectId = Effect.CONDUIT_POWER,
    effectSetting = EFFECTS.CONDUIT_POWER
)

class BlindnessElement : BaseEffectElement(
    name = "blindness",
    displayNameResId = R.string.module_blindness_display_name,
    effectId = Effect.BLINDNESS,
    effectSetting = EFFECTS.BLINDNESS
)

class InvisibilityElement : BaseEffectElement(
    name = "invisibility",
    displayNameResId = R.string.module_invisibility_display_name,
    effectId = Effect.INVISIBILITY,
    effectSetting = EFFECTS.INVISIBILITY
)

class InstantHealthElement : BaseEffectElement(
    name = "instant_health",
    displayNameResId = R.string.module_instant_health_display_name,
    effectId = Effect.INSTANT_HEALTH,
    effectSetting = EFFECTS.INSTANT_HEALTH
)

class InstantDamageElement : BaseEffectElement(
    name = "instant_damage",
    displayNameResId = R.string.module_instant_damage_display_name,
    effectId = Effect.INSTANT_DAMAGE,
    effectSetting = EFFECTS.INSTANT_DAMAGE
)

class HungerElement : BaseEffectElement(
    name = "hunger",
    displayNameResId = R.string.module_hunger_display_name,
    effectId = Effect.HUNGER,
    effectSetting = EFFECTS.HUNGER
)

class HealthBoostElement : BaseEffectElement(
    name = "health_boost",
    displayNameResId = R.string.module_health_boost_display_name,
    effectId = Effect.HEALTH_BOOST,
    effectSetting = EFFECTS.HEALTH_BOOST
)

class FireResistanceElement : BaseEffectElement(
    name = "fire_resistance",
    displayNameResId = R.string.module_fire_resistance_display_name,
    effectId = Effect.FIRE_RESISTANCE,
    effectSetting = EFFECTS.FIRE_RESISTANCE
)

// theres no point of this
class FatalPoisonElement : BaseEffectElement(
    name = "fatal_poison",
    displayNameResId = R.string.module_fatal_poison_display_name,
    effectId = Effect.FATAL_POISON,
    effectSetting = EFFECTS.FATAL_POISON
)

class RegenerationElement : BaseEffectElement(
    name = "regeneration",
    displayNameResId = R.string.module_regeneration_display_name,
    effectId = Effect.REGENERATION,
    effectSetting = EFFECTS.REGENERATION
)

class PoisonElement : BaseEffectElement(
    name = "poison",
    displayNameResId = R.string.module_poison_display_name,
    effectId = Effect.POISON,
    effectSetting = EFFECTS.POISON
)

class NauseaElement : BaseEffectElement(
    name = "nausea",
    displayNameResId = R.string.module_nausea_display_name,
    effectId = Effect.NAUSEA,
    effectSetting = EFFECTS.NAUSEA
)

class LevitationElement : BaseEffectElement(
    name = "levitation",
    displayNameResId = R.string.module_levitation_display_name,
    effectId = Effect.LEVITATION,
    effectSetting = EFFECTS.LEVITATION
)

class JumpBoostElement : BaseEffectElement(
    name = "jump_boost",
    displayNameResId = R.string.module_jump_boost_display_name,
    effectId = Effect.JUMP_BOOST,
    effectSetting = EFFECTS.JUMP_BOOST
)

class WitherElement : BaseEffectElement(
    name = "wither",
    displayNameResId = R.string.module_wither_display_name,
    effectId = Effect.WITHER,
    effectSetting = EFFECTS.WITHER
)

class WeaknessElement : BaseEffectElement(
    name = "weakness",
    displayNameResId = R.string.module_weakness_display_name,
    effectId = Effect.WEAKNESS,
    effectSetting = EFFECTS.WEAKNESS
)

class VillageHeroElement : BaseEffectElement(
    name = "village_hero",
    displayNameResId = R.string.module_village_hero_display_name,
    effectId = Effect.VILLAGE_HERO,
    effectSetting = EFFECTS.VILLAGE_HERO
)

class SlowFallingElement : BaseEffectElement(
    name = "slow_falling",
    displayNameResId = R.string.module_slow_falling_display_name,
    effectId = Effect.SLOW_FALLING,
    effectSetting = EFFECTS.SLOW_FALLING
)

class SaturationElement : BaseEffectElement(
    name = "saturation",
    displayNameResId = R.string.module_saturation_display_name,
    effectId = Effect.SATURATION,
    effectSetting = EFFECTS.SATURATION
)

class ResistanceElement : BaseEffectElement(
    name = "resistance",
    displayNameResId = R.string.module_resistance_display_name,
    effectId = Effect.RESISTANCE,
    effectSetting = EFFECTS.RESISTANCE
)

class SwiftnessElement : BaseEffectElement(
    name = "swiftness",
    displayNameResId = R.string.module_swiftness_display_name,
    effectId = Effect.SWIFTNESS,
    effectSetting = EFFECTS.SWIFTNESS
)

class StrengthElement : BaseEffectElement(
    name = "strength",
    displayNameResId = R.string.module_strength_display_name,
    effectId = Effect.STRENGTH,
    effectSetting = EFFECTS.STRENGTH
)
