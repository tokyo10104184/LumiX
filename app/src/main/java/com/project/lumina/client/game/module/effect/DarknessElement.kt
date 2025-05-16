package com.project.lumina.client.game.module.effect

import com.project.lumina.client.R
import com.project.lumina.client.game.InterceptablePacket
import com.project.lumina.client.constructors.Element
import com.project.lumina.client.constructors.CheatCategory
import com.project.lumina.client.game.data.Effect
import com.project.lumina.client.game.module.setting.EFFECTS
import com.project.lumina.client.game.module.setting.EffectSetting
import org.cloudburstmc.protocol.bedrock.data.entity.EntityDataTypes
import org.cloudburstmc.protocol.bedrock.packet.MobEffectPacket
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket

class DarknessElement : Element(
    name = "darkness",
    category = CheatCategory.World,
    displayNameResId = R.string.module_darkness_display_name
) {

    private val amplifierValue by floatValue("amplifier", 1f, 1f..5f)
    private val effect by EffectSetting(this, EntityDataTypes.VISIBLE_MOB_EFFECTS, EFFECTS.DARKNESS)

    override fun onDisabled() {
        super.onDisabled()
        if (isSessionCreated) {
            session.clientBound(MobEffectPacket().apply {
                runtimeEntityId = session.localPlayer.runtimeEntityId
                event = MobEffectPacket.Event.REMOVE
                effectId = Effect.DARKNESS
            })
        }
    }

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled) {
            return
        }

        val packet = interceptablePacket.packet
        if (packet is PlayerAuthInputPacket) {
            if (session.localPlayer.tickExists % 20 == 0L) {
                session.clientBound(MobEffectPacket().apply {
                    runtimeEntityId = session.localPlayer.runtimeEntityId
                    event = MobEffectPacket.Event.ADD
                    effectId = Effect.DARKNESS
                    amplifier = amplifierValue.toInt() - 1
                    isParticles = false
                    duration = 360000
                })
            }
        }
    }


}