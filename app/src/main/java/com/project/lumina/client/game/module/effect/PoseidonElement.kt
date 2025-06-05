package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.InterceptablePacket
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS
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