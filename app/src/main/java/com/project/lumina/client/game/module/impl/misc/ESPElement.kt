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
 * or don't bother pretending.
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

package com.project.lumina.client.game.module.impl.misc

import android.util.Log
import com.project.lumina.client.application.AppContext
import com.project.lumina.client.game.InterceptablePacket
import com.project.lumina.client.constructors.Element
import com.project.lumina.client.constructors.CheatCategory
import com.project.lumina.client.util.AssetManager
import com.project.lumina.client.game.entity.*
import com.project.lumina.client.overlay.manager.OverlayManager
import com.project.lumina.client.ui.opengl.ESPOverlayGLSurface
import org.cloudburstmc.math.vector.Vector3f
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket

class ESPElement : Element(
    name = "esp_module",
    category = CheatCategory.Misc,
    displayNameResId = AssetManager.getString("module_esp_display_name")
) {
    private var playersOnly by boolValue("Players", true)
    private var rangeValue by floatValue("Range", 10f, 2f..100f)
    private var multiTarget = true
    private var maxTargets = 100
    private var glSurface: ESPOverlayGLSurface? = null

    override fun onEnabled() {
        super.onEnabled()
        try {
            if (isSessionCreated && AppContext.instance != null) {
                glSurface = ESPOverlayGLSurface(AppContext.instance)
                OverlayManager.showCustomOverlay(glSurface!!)
                Log.d("ESPModule", "ESP Overlay enabled")
            } else {
                println("Session not created or AppContext not available, cannot enable ESP overlay.")
            }
        } catch (e: Exception) {
            println("Error enabling ESP overlay: ${e.message}")
        }
    }

    override fun onDisabled() {
        super.onDisabled()
        glSurface?.let {
            OverlayManager.dismissCustomOverlay(it)
            Log.d("ESPModule", "ESP Overlay disabled")
        }
        glSurface = null
    }

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled || !isSessionCreated || interceptablePacket.packet !is PlayerAuthInputPacket) return

        val packet = interceptablePacket.packet
        val position = Vector3f.from(packet.position.x, packet.position.y, packet.position.z)

        glSurface?.let {
            it.updatePlayerPosition(position)
            it.updateEntities(searchForClosestEntities().map { entity -> entity.vec3Position })
        }
    }

    private fun searchForClosestEntities(): List<Entity> {
        val entities = session.level.entityMap.values
            .mapNotNull {
                val distance = it.distance(session.localPlayer)
                if (distance < rangeValue && it.isTarget()) Pair(it, distance) else null
            }
            .sortedBy { it.second }
            .map { it.first }

        return if (multiTarget) entities.take(maxTargets) else entities.take(1)
    }

    private fun Entity.isTarget(): Boolean {
        return when (this) {
            is LocalPlayer -> false
            is Player -> playersOnly && !isBot()
            else -> false
        }
    }

    private fun Player.isBot(): Boolean {
        if (this is LocalPlayer) return false
        val playerList = session.level.playerMap[this.uuid] ?: return true
        return playerList.name.isBlank()
    }
}
