package com.project.lumina.client.constructors


import android.util.Log
import com.project.lumina.client.application.AppContext
import com.project.lumina.client.game.InterceptablePacket
import com.project.lumina.client.game.entity.LocalPlayer
import com.project.lumina.client.game.event.EventManager
import com.project.lumina.client.game.event.EventPacketInbound
import com.project.lumina.client.game.registry.BlockMapping
import com.project.lumina.client.game.registry.BlockMappingProvider
import com.project.lumina.client.game.world.Level
import com.project.lumina.client.game.world.World
import com.project.lumina.client.overlay.MiniMapOverlay
import com.project.lumina.client.overlay.OverlayModuleList
import com.project.lumina.client.overlay.PacketNotificationOverlay
import com.project.lumina.client.overlay.Position
import com.project.lumina.client.overlay.SessionStatsOverlay
import com.project.lumina.client.overlay.SpeedometerOverlay
import com.project.lumina.relay.LuminaRelaySession
import com.project.lumina.client.game.registry.ItemMapping
import com.project.lumina.client.game.registry.ItemMappingProvider
import com.project.lumina.client.game.registry.LegacyBlockMapping
import com.project.lumina.client.game.registry.LegacyBlockMappingProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.cloudburstmc.math.vector.Vector3f
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket
import org.cloudburstmc.protocol.bedrock.packet.StartGamePacket
import org.cloudburstmc.protocol.bedrock.packet.TextPacket
import java.util.Collections

@Suppress("MemberVisibilityCanBePrivate")
class NetBound(val luminaRelaySession: LuminaRelaySession) : ComposedPacketHandler, com.project.lumina.client.game.event.Listenable {

    override val eventManager = EventManager()

    val world = World(this)
    val level = Level(this)
    val localPlayer = LocalPlayer(this)

    private val proxyPlayerNames: MutableSet<String> = Collections.synchronizedSet(mutableSetOf())

    val protocolVersion: Int
        get() = luminaRelaySession.server.codec.protocolVersion



    private val mappingProviderContext = AppContext.instance

    private val blockMappingProvider = BlockMappingProvider(mappingProviderContext)
    private val itemMappingProvider = ItemMappingProvider(mappingProviderContext)
    private val legacyBlockMappingProvider = LegacyBlockMappingProvider(mappingProviderContext)

    lateinit var blockMapping: BlockMapping
    lateinit var itemMapping: ItemMapping
    lateinit var legacyBlockMapping: LegacyBlockMapping

    private var startGameReceived = false
    private val pendingPackets = mutableListOf<BedrockPacket>()


    private val mainScope = CoroutineScope(Dispatchers.Main)

    private var playerPosition = Position(0f, 0f)
    private var playerRotation = 0f
    private val entityPositions = mutableMapOf<Long, Position>()
    private var minimapEnabled = false
    private var minimapUpdateScheduled = false
    private var minimapSize = 100f
    private var minimapZoom = 1.0f
    private var minimapDotSize = 5
    private var tracersEnabled = false


    private val versionName by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        AppContext.instance.packageManager.getPackageInfo(
            AppContext.instance.packageName, 0
        ).versionName
    }

    fun clientBound(packet: BedrockPacket) {
        luminaRelaySession.clientBound(packet)
    }

    fun serverBound(packet: BedrockPacket) {
        luminaRelaySession.serverBound(packet)
    }

    override fun beforePacketBound(packet: BedrockPacket): Boolean {
        if (packet is TextPacket && packet.type == TextPacket.Type.CHAT) {
            proxyPlayerNames.add(packet.sourceName)
        }

        if (packet is StartGamePacket && !startGameReceived) {
            startGameReceived = true

            try {
                blockMapping = blockMappingProvider.craftMapping(protocolVersion)
                itemMapping = itemMappingProvider.craftMapping(protocolVersion)
                legacyBlockMapping = legacyBlockMappingProvider.craftMapping(protocolVersion)

                Log.i("GameSession", "✅ Loaded mappings for protocol $protocolVersion")
            } catch (e: Exception) {
                Log.e("GameSession", "❌ Failed to load mappings for protocol $protocolVersion", e)
            }
        }

        localPlayer.onPacketBound(packet)
        world.onPacket(packet)
        level.onPacketBound(packet)

        val event = EventPacketInbound(this, packet)
        eventManager.emit(event)

        if (event.isCanceled()) return true

        val interceptablePacket = InterceptablePacket(packet)
        for (module in GameManager.elements) {
            module.beforePacketBound(interceptablePacket)
            if (interceptablePacket.isIntercepted) return true
        }

        displayClientMessage("[Lumina V4]", TextPacket.Type.TIP)

        return false
    }


    override fun afterPacketBound(packet: BedrockPacket) {
        for (module in GameManager.elements) {
            module.afterPacketBound(packet)
        }
    }

    override fun onDisconnect(reason: String) {
        localPlayer.onDisconnect()
        level.onDisconnect()
        proxyPlayerNames.clear()

        for (module in GameManager.elements) {
            module.onDisconnect(reason)
        }
    }

    fun displayClientMessage(message: String, type: TextPacket.Type = TextPacket.Type.RAW) {
        val textPacket = TextPacket()
        textPacket.type = type
        textPacket.isNeedsTranslation = false
        textPacket.sourceName = ""
        textPacket.message = message
        textPacket.xuid = ""
        textPacket.platformChatId = ""
        textPacket.filteredMessage = ""
        clientBound(textPacket)
    }


    fun launchOnMain(block: suspend CoroutineScope.() -> Unit) {
        mainScope.launch {
            block()
        }
    }

    suspend fun showSessionStatsOverlay(initialStats: List<String>): SessionStatsOverlay =
        withContext(Dispatchers.Main) {
            try {

                val overlay = SessionStatsOverlay.showSessionStats(initialStats)

                overlay
            } catch (e: Exception) {

                e.printStackTrace()
                throw e
            }
        }

    fun showNotification(title: String, subtitle: String, ResId: Int) {
        mainScope.launch {
            try {

                PacketNotificationOverlay.showNotification(
                    title = title,
                    subtitle = subtitle,
                    iconRes = ResId,
                    duration = 1000L
                )
            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }
    fun showSpeedometer(position: Vector3f) {
        mainScope.launch {
            try {

                SpeedometerOverlay.showOverlay()
                SpeedometerOverlay.updatePosition(position)
            } catch (e: Exception) {

            }
        }
    }


    fun updatePlayerPosition(x: Float, z: Float) {
        playerPosition = Position(x, z)
        if (minimapEnabled) {
            scheduleMinimapUpdate()
        }
    }

    fun updatePlayerRotation(yaw: Float) {
        playerRotation = yaw
        if (minimapEnabled) {
            scheduleMinimapUpdate()
        }
    }

    fun updateEntityPosition(entityId: Long, x: Float, z: Float) {
        entityPositions[entityId] = Position(x, z)
        if (minimapEnabled && !minimapUpdateScheduled) {
            scheduleMinimapUpdate()
        }
    }

    fun updateMinimapSize(size: Float) {
        minimapSize = size
        if (minimapEnabled) {
            mainScope.launch {
                //println("[GameSession] Updating minimap size to: $size")
                MiniMapOverlay.setMinimapSize(size)
                updateMinimap()
            }
        }
    }

    fun updateMinimapZoom(zoom: Float) {
        minimapZoom = zoom
        if (minimapEnabled) {
            mainScope.launch {
                //println("[GameSession] Updating minimap zoom to: $zoom")
                MiniMapOverlay.overlayInstance.minimapZoom = zoom
                updateMinimap()
            }
        }
    }

    fun updateDotSize(dotSize: Int) {
        minimapDotSize = dotSize
        if (minimapEnabled) {
            mainScope.launch {
                //println("[GameSession] Updating minimap dot size to: $dotSize")
                MiniMapOverlay.overlayInstance.minimapDotSize = dotSize
                updateMinimap()
            }
        }
    }

    private fun scheduleMinimapUpdate() {
        if (!minimapUpdateScheduled) {
            minimapUpdateScheduled = true
            mainScope.launch {
                updateMinimap()
                minimapUpdateScheduled = false
            }
        }
    }

    fun enableMinimap(enable: Boolean) {
        if (enable != minimapEnabled) {
            minimapEnabled = enable
            if (enable) {
                mainScope.launch {
                    //println("[GameSession] Enabling minimap overlay")
                    MiniMapOverlay.setOverlayEnabled(true)
                    MiniMapOverlay.setMinimapSize(minimapSize)
                    updateMinimap()
                }
            } else {
                mainScope.launch {
                    //println("[GameSession] Disabling minimap overlay")
                    MiniMapOverlay.setOverlayEnabled(false)
                }
            }
        }
    }

    private fun updateMinimap() {
        try {
            //println("[GameSession] Updating minimap: player at (${playerPosition.x}, ${playerPosition.y}) rotation: $playerRotation")
            MiniMapOverlay.setCenter(playerPosition.x, playerPosition.y)
            MiniMapOverlay.setPlayerRotation(playerRotation)

            MiniMapOverlay.overlayInstance.minimapZoom = minimapZoom
            MiniMapOverlay.overlayInstance.minimapDotSize = minimapDotSize

            val targets = entityPositions.values.toList()

            val finalTargets = if (targets.isEmpty()) {
                val dummyTargets = mutableListOf<Position>()
                for (i in 0 until 4) {
                    val angle = i * Math.PI / 2
                    val distance = 20f
                    val x = playerPosition.x + (distance * Math.cos(angle)).toFloat()
                    val z = playerPosition.y + (distance * Math.sin(angle)).toFloat()
                    dummyTargets.add(Position(x, z))
                }
                dummyTargets
            } else {
                targets
            }

            //println("[GameSession] Setting ${finalTargets.size} targets on minimap")
            MiniMapOverlay.setTargets(finalTargets)
            MiniMapOverlay.showOverlay()
        } catch (e: Exception) {
            //println("[GameSession] Minimap update failed: ${e.message}")
            e.printStackTrace()
        }
    }

    fun clearEntityPositions() {
        entityPositions.clear()
        if (minimapEnabled) {
            scheduleMinimapUpdate()
        }

    }

    fun showMinimap(centerX: Float, centerZ: Float, targets: List<Position>) {
        mainScope.launch {
            //println("[GameSession] Legacy showMinimap called")
            MiniMapOverlay.setOverlayEnabled(true)
            MiniMapOverlay.setMinimapSize(minimapSize)
            MiniMapOverlay.setCenter(centerX, centerZ)
            MiniMapOverlay.setTargets(targets)
            MiniMapOverlay.showOverlay()

            minimapEnabled = true
            playerPosition = Position(centerX, centerZ)
        }
    }

    fun enableArrayList(boolean: Boolean) {
        OverlayModuleList.setOverlayEnabled(enabled = boolean)
    }

    fun setArrayListMode(boolean: Boolean) {

        OverlayModuleList.setCapitalizeAndMerge(boolean)

    }

    fun arrayListUi(string: String){

        OverlayModuleList.setDisplayMode(string)

    }


    fun isProxyPlayer(playerName: String): Boolean {
        return proxyPlayerNames.contains(playerName)
    }
}

