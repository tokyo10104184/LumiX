package com.project.lumina.client.game.module.motion

import com.project.lumina.client.R
import com.project.lumina.client.game.InterceptablePacket
import com.project.lumina.client.constructors.Element
import com.project.lumina.client.constructors.CheatCategory
import org.cloudburstmc.math.vector.Vector3f
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket
import org.cloudburstmc.protocol.bedrock.packet.SetEntityMotionPacket
import kotlin.random.Random


class AntiAFKElement(iconResId: Int = R.drawable.ic_app_update) : Element(
    name = "AntiAfk",
    category = CheatCategory.Motion,
    iconResId,
    displayNameResId = R.string.module_anti_afk_display_name
) {

    private val glitchInterval by intValue("Interval", 200, 50..1000)
    private val intensity by floatValue("Intensity", 0.5f, 0.1f..2.0f)

    private var lastGlitchTime = 0L
    private var lastActionTime = System.currentTimeMillis()

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled) {
            return
        }

        val packet = interceptablePacket.packet
        if (packet is PlayerAuthInputPacket) {
            val currentTime = System.currentTimeMillis()

            if (currentTime - lastGlitchTime >= glitchInterval) {
                lastGlitchTime = currentTime

                val randomOffset = Vector3f.from(
                    (Random.nextFloat() - 0.5f) * intensity,
                    (Random.nextFloat() - 0.5f) * intensity,
                    (Random.nextFloat() - 0.5f) * intensity
                )

                val motionPacket = SetEntityMotionPacket().apply {
                    runtimeEntityId = session.localPlayer.runtimeEntityId
                    motion = randomOffset
                }
                session.clientBound(motionPacket)
            }
        }
    }

    override fun onDisabled() {
        super.onDisabled()
        if (isSessionCreated) {
            val resetPacket = SetEntityMotionPacket().apply {
                runtimeEntityId = session.localPlayer.runtimeEntityId
                motion = Vector3f.ZERO
            }
            session.clientBound(resetPacket)
        }
    }
}
