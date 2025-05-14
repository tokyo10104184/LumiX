package com.project.lumina.relay.listener

import com.project.lumina.relay.util.IXboxIdentityTokenCache
import com.project.lumina.relay.util.XboxDeviceInfo
import com.project.lumina.relay.util.XboxIdentityToken
import com.project.lumina.relay.util.fetchChain
import com.project.lumina.relay.util.fetchIdentityToken
import com.project.lumina.relay.util.signJWT
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket
import org.cloudburstmc.protocol.bedrock.packet.DisconnectPacket
import org.cloudburstmc.protocol.bedrock.packet.LoginPacket

@Suppress("MemberVisibilityCanBePrivate")
class XboxLoginPacketListener(
    val accessToken: () -> String,
    val deviceInfo: XboxDeviceInfo
) : EncryptedLoginPacketListener() {

    var tokenCache: IXboxIdentityTokenCache? = null

    private var identityToken = XboxIdentityToken("", 0)
        get() {
            if (field.notAfter < System.currentTimeMillis() / 1000) {
                field = tokenCache?.checkCache(deviceInfo)?.also {
                    println("Token cache hit")
                } ?: fetchIdentityToken(accessToken(), deviceInfo).also {
                    tokenCache?.let { cache ->
                        println("Saving token cache")
                        cache.cache(deviceInfo, it)
                    }
                }
            }

            return field
        }

    private val chain: List<String>
        get() = fetchChain(identityToken.token, keyPair)

    fun forceFetchChain() {
        chain
    }

    override fun beforeClientBound(packet: BedrockPacket): Boolean {
        if (packet is LoginPacket) {
            try {
                packet.chain.clear()
                packet.chain.addAll(chain)
                packet.extra = signJWT(packet.extra.split('.')[1], keyPair, base64Encoded = true)
            } catch (e: Throwable) {
                luminaRelaySession.clientBound(DisconnectPacket().apply {
                    kickMessage = e.toString()
                })
                println("Login failed: $e")
            }

            println("Login success")
            loginPacket = packet
            connectServer()
            return true
        }
        return false
    }

}