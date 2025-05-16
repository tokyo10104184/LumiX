package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.InterceptablePacket
import com.project.lumina.client.constructors.Element
import com.project.lumina.client.constructors.CheatCategory
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS
import com.project.lumina.client.game.module.setting.EffectSetting
import org.cloudburstmc.math.vector.Vector3f
import org.cloudburstmc.protocol.bedrock.data.PlayerAuthInputData
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataTypes
import org.cloudburstmc.protocol.bedrock.packet.MobEffectPacket
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket
import org.cloudburstmc.protocol.bedrock.packet.SetEntityMotionPacket
import kotlin.math.cos
import kotlin.math.sin

class PoseidonElement : Element(
    name = "poseidon",
    category = CheatCategory.World,
    displayNameResId = R.string.module_poseidon_display_name
) {

    private val speedMultiplier = 1.5f
    private val waterBreathingEffect by EffectSetting(this, EntityDataTypes.VISIBLE_MOB_EFFECTS, EFFECTS.WATER_BREATHING)
    private val conduitPowerEffect by EffectSetting(this, EntityDataTypes.VISIBLE_MOB_EFFECTS, EFFECTS.CONDUIT_POWER)

    override fun onDisabled() {
        super.onDisabled()
        if (isSessionCreated) {

            session.clientBound(MobEffectPacket().apply {
                event = MobEffectPacket.Event.REMOVE
                runtimeEntityId = session.localPlayer.runtimeEntityId
                effectId = Effect.NIGHT_VISION
            })
            session.clientBound(MobEffectPacket().apply {
                event = MobEffectPacket.Event.REMOVE
                runtimeEntityId = session.localPlayer.runtimeEntityId
                effectId = Effect.WATER_BREATHING
            })
        }
    }

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled) {
            return
        }

        val packet = interceptablePacket.packet
        if (packet is PlayerAuthInputPacket && isEnabled) {

            if (packet.inputData.contains(PlayerAuthInputData.START_SWIMMING) ||
                packet.inputData.contains(PlayerAuthInputData.AUTO_JUMPING_IN_WATER) ||
                session.localPlayer.motionY < 0
            ) {


                val yaw = Math.toRadians(packet.rotation.y.toDouble())
                val pitch = Math.toRadians(packet.rotation.x.toDouble())


                val motionX = -sin(yaw) * cos(pitch) * speedMultiplier
                val motionZ = cos(yaw) * cos(pitch) * speedMultiplier


                val motionPacket = SetEntityMotionPacket().apply {
                    runtimeEntityId = session.localPlayer.runtimeEntityId
                    motion = Vector3f.from(
                        motionX.toFloat(),
                        0.05f,
                        motionZ.toFloat()
                    )
                }
                session.clientBound(motionPacket)


                packet.inputData.remove(PlayerAuthInputData.START_SWIMMING)
                packet.inputData.remove(PlayerAuthInputData.AUTO_JUMPING_IN_WATER)
            }
        }


        if (isEnabled && session.localPlayer.tickExists % 20 == 0L) {

            session.clientBound(MobEffectPacket().apply {
                runtimeEntityId = session.localPlayer.runtimeEntityId
                event = MobEffectPacket.Event.ADD
                effectId = Effect.NIGHT_VISION
                amplifier = 0
                isParticles = false
                duration = 360000
            })


            session.clientBound(MobEffectPacket().apply {
                runtimeEntityId = session.localPlayer.runtimeEntityId
                event = MobEffectPacket.Event.ADD
                effectId = Effect.WATER_BREATHING
                amplifier = 0
                isParticles = false
                duration = 360000
            })
        }
    }
} 