/*
 * © Project Lumina 2025 — Licensed under GNU GPLv3
 * You are free to use, modify, and redistribute this code under the terms
 * of the GNU General Public License v3. See the LICENSE file for details.
 *
 * ─────────────────────────────────────────────────────────────────────────────
 * This is open source — not open credit.
 *
 * If you're here to build, welcome. If you're here to repaint and reupload
 * with your tag slapped on it… you're not fooling anyone.
 *
 * Changing colors and class names doesn't make you a developer.
 * Copy-pasting isn't contribution.
 *
 * You have legal permission to fork. But ask yourself — are you improving,
 * or are you just recycling someone else's work to feed your ego?
 *
 * Open source isn't about low-effort clones or chasing clout.
 * It's about making things better. Sharper. Cleaner. Smarter.
 *
 * So go ahead, fork it — but bring something new to the table,
 * or don’t bother pretending.
 *
 * This message is philosophical. It does not override your legal rights under GPLv3.
 * ─────────────────────────────────────────────────────────────────────────────
 *
 * GPLv3 Summary:
 * - You have the freedom to run, study, share, and modify this software.
 * - If you distribute modified versions, you must also share the source code.
 * - You must keep this license and copyright intact.
 * - You cannot apply further restrictions — the freedom stays with everyone.
 * - This license is irrevocable, and applies to all future redistributions.
 *
 * Full text: https://www.gnu.org/licenses/gpl-3.0.html
 */


package com.project.lumina.client.game.module.combat

import com.project.lumina.client.R
import com.project.lumina.client.game.InterceptablePacket
import com.project.lumina.client.constructors.Element
import com.project.lumina.client.constructors.CheatCategory
import org.cloudburstmc.protocol.bedrock.packet.SetEntityMotionPacket

class VelocityElement(iconResId: Int = R.drawable.ic_file_download_black_24dp) : Element(
    name = "Velocity",
    category = CheatCategory.Combat,
    iconResId,
    displayNameResId = R.string.module_velocity_display_name
) {
    private var mode: String = "Vanilla"
        set(value) {
            field = if (value in arrayOf("Vanilla", "Simple")) value else "Vanilla"
            
        }

    private var horizontalValue: Float = 0f
        set(value) {
            field = value.coerceIn(0f, 1f)
            
        }

    private var verticalValue: Float = 0f
        set(value) {
            field = value.coerceIn(0f, 1f)
            
        }

    private object Vanilla {
        fun handlePacket(interceptablePacket: InterceptablePacket) {
            if (interceptablePacket.packet is SetEntityMotionPacket) {
                interceptablePacket.isIntercepted = true
            }
        }
    }

    private object Simple {
        fun handlePacket(interceptablePacket: InterceptablePacket, horizontal: Float, vertical: Float) {
            if (interceptablePacket.packet is SetEntityMotionPacket) {
                val packet = interceptablePacket.packet as SetEntityMotionPacket
                packet.motion = packet.motion.mul(horizontal, vertical, horizontal)
            }
        }
    }

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled) {
            return
        }

        when (mode) {
            "Vanilla" -> Vanilla.handlePacket(interceptablePacket)
            "Simple" -> Simple.handlePacket(interceptablePacket, horizontalValue, verticalValue)
        }
    }

    
    fun updateMode(newMode: String) {
        mode = newMode
    }

    fun updateHorizontalValue(value: Float) {
        horizontalValue = value
    }

    fun updateVerticalValue(value: Float) {
        verticalValue = value
    }
}