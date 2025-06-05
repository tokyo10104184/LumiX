package com.project.lumina.client.game.module.impl.motion

import com.project.lumina.client.R
import com.project.lumina.client.game.InterceptablePacket
import com.project.lumina.client.constructors.Element
import com.project.lumina.client.constructors.CheatCategory
import org.cloudburstmc.protocol.bedrock.data.PlayerAuthInputData
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket


class SprintElement(iconResId: Int = R.drawable.ic_run_fast_black_24dp) : Element(
    name = "Sprint",
    category = CheatCategory.Motion,
    iconResId,
    displayNameResId = R.string.module_sprint_display_name
) {
    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        val packet = interceptablePacket.packet

        if (packet is PlayerAuthInputPacket && isEnabled) {
            packet.inputData.add(PlayerAuthInputData.SPRINTING)
            packet.inputData.add(PlayerAuthInputData.START_SPRINTING)
        } else if (packet is PlayerAuthInputPacket && !isEnabled) {
            packet.inputData.add(PlayerAuthInputData.STOP_SPRINTING)
        }
    }
}