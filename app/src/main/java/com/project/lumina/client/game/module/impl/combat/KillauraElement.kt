package com.project.lumina.client.game.module.impl.combat

import com.project.lumina.client.R
import com.project.lumina.client.game.InterceptablePacket
import com.project.lumina.client.constructors.Element
import com.project.lumina.client.constructors.CheatCategory
import com.project.lumina.client.game.entity.Entity
import com.project.lumina.client.game.entity.EntityUnknown
import com.project.lumina.client.game.entity.LocalPlayer
import com.project.lumina.client.game.entity.MobList
import com.project.lumina.client.game.entity.Player
import org.cloudburstmc.math.vector.Vector3f
import org.cloudburstmc.protocol.bedrock.packet.MovePlayerPacket
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket
import kotlin.math.cos
import kotlin.math.sin
import com.project.lumina.client.util.AssetManager

class KillauraElement(iconResId: Int = AssetManager.getAsset("ic_sword_cross_black_24dp")) : Element(
    name = "KillAura",
    category = CheatCategory.Combat,
    iconResId,
    displayNameResId = AssetManager.getString("module_killaura_display_name")
) {

    private val playerOnly by boolValue("Players", false)
    private var mobsOnly by boolValue("Mobs", true)
    private var tpAuraEnabled by boolValue("TP Aura", false) 
    private var strafe by boolValue("Strafe", false)
    private var teleportBehind by boolValue("Teleport Behind", false) 
    private var rangeValue by floatValue("Range", 3.7f, 2f..7f)
    private var attackInterval by intValue("Delay", 5, 1..20)
    private var cpsValue by intValue("CPS", 5, 1..20)
    private var packets by intValue("Packets", 1, 1..10)
    private var tpSpeed by intValue("TP Speed", 500, 100..2000)

    private var distanceToKeep by floatValue("Keep Distance", 2.0f, 1f..5f)
    private var strafeAngle = 0.0f
    private val strafeSpeed by floatValue("Strafe Speed", 1.0f, 0.1f..2.0f)
    private val strafeRadius by floatValue("Strafe Radius", 1.0f, 0.1f..5.0f)
    private var lastAttackTime = 0L
    private var tpCooldown = 0L 

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled) return

        val packet = interceptablePacket.packet
        if (packet is PlayerAuthInputPacket) {
            val currentTime = System.currentTimeMillis()
            val minAttackDelay = 1000L / cpsValue

            if (packet.tick % attackInterval == 0L && (currentTime - lastAttackTime) >= minAttackDelay) {
                val closestEntities = searchForClosestEntities()
                if (closestEntities.isEmpty()) return

                closestEntities.forEach { entity ->
                    
                    if (tpAuraEnabled && (currentTime - tpCooldown) >= tpSpeed) { 
                        teleportTo(entity, distanceToKeep)
                        tpCooldown = currentTime 
                    }

                    repeat(packets) {
                        session.localPlayer.attack(entity) 
                    }
                    if (strafe) {
                        strafeAroundTarget(entity)
                    }
                    lastAttackTime = currentTime
                }
            }
        }
    }

    private fun strafeAroundTarget(entity: Entity) {
        val targetPos = entity.vec3Position

        
        strafeAngle += strafeSpeed
        if (strafeAngle >= 360.0) {
            strafeAngle -= 360.0f
        }

        
        val offsetX = strafeRadius * cos(strafeAngle)
        val offsetZ = strafeRadius * sin(strafeAngle)

        
        val newPosition = targetPos.add(offsetX.toFloat(), 0f, offsetZ.toFloat())

        val movePlayerPacket = MovePlayerPacket().apply {
            runtimeEntityId = session.localPlayer.runtimeEntityId
            position = newPosition
            rotation = Vector3f.from(0f, 0f, 0f) 
            mode = MovePlayerPacket.Mode.NORMAL
            isOnGround = true
            ridingRuntimeEntityId = 0
            tick = session.localPlayer.tickExists
        }

        session.clientBound(movePlayerPacket)
    }

    private fun teleportTo(entity: Entity, distance: Float) {
        val targetPosition = entity.vec3Position
        val playerPosition = session.localPlayer.vec3Position

        val newPosition = if (teleportBehind) {
            val targetYaw = Math.toRadians(entity.vec3Rotation.y.toDouble()).toFloat()

            
            val direction = Vector3f.from(
                sin(targetYaw),  
                0f,
                -cos(targetYaw)
            )

            val length = direction.length()
            val normalizedDirection = if (length != 0f) {
                Vector3f.from(direction.x / length, 0f, direction.z / length)
            } else {
                direction
            }

            Vector3f.from(
                targetPosition.x + normalizedDirection.x * distance,
                targetPosition.y,
                targetPosition.z + normalizedDirection.z * distance
            )
        } else {
            
            val direction = Vector3f.from(
                targetPosition.x - playerPosition.x,
                0f,  
                targetPosition.z - playerPosition.z
            )

            
            val length = direction.length()
            val normalizedDirection = if (length != 0f) {
                Vector3f.from(
                    direction.x / length,
                    0f,
                    direction.z / length
                )  
            } else {
                direction
            }

            
            Vector3f.from(
                targetPosition.x - normalizedDirection.x * distance,
                targetPosition.y,  
                targetPosition.z - normalizedDirection.z * distance
            )
        }

        
        val movePlayerPacket = MovePlayerPacket().apply {
            runtimeEntityId = session.localPlayer.runtimeEntityId
            position = newPosition
            rotation = entity.vec3Rotation
            mode = MovePlayerPacket.Mode.NORMAL 
            isOnGround = false 
            ridingRuntimeEntityId = 0 
            tick = session.localPlayer.tickExists 
        }

        
        session.clientBound(movePlayerPacket)
    }


    private fun Entity.isTarget(): Boolean {
        return when (this) {
            is LocalPlayer -> false
            is Player -> {
                if (playerOnly || (playerOnly && mobsOnly)) {
                    !this.isBot()
                } else {
                    false
                }
            }

            is EntityUnknown -> {
                if (mobsOnly || (playerOnly && mobsOnly)) {
                    isMob()
                } else {
                    false
                }
            }

            else -> false
        }
    }


    private fun EntityUnknown.isMob(): Boolean {
        return this.identifier in MobList.mobTypes
    }

    private fun Player.isBot(): Boolean {
        if (this is LocalPlayer) return false
        val playerList = session.level.playerMap[this.uuid] ?: return true
        return playerList.name.isBlank()
    }

    private fun searchForClosestEntities(): List<Entity> {
        return session.level.entityMap.values
            .filter { entity -> entity.distance(session.localPlayer) < rangeValue && entity.isTarget() }
    }
}