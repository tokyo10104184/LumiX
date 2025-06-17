package com.project.lumina.client.game.module.api.commands

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.project.lumina.client.R
import com.project.lumina.client.constructors.BoolValue
import com.project.lumina.client.constructors.CheatCategory
import com.project.lumina.client.constructors.Element
import com.project.lumina.client.constructors.FloatValue
import com.project.lumina.client.constructors.GameManager
import com.project.lumina.client.constructors.IntValue
import com.project.lumina.client.game.InterceptablePacket
import com.project.lumina.client.remlink.TerminalViewModel
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket
import org.cloudburstmc.protocol.bedrock.packet.TextPacket

class CmdListener(private val moduleManager: GameManager) : Element(
    name = "ChatListener",
    category = CheatCategory.Misc,
    displayNameResId = R.string.module_chat_listener
) {

    companion object {
        const val PREFIX = "!"
        const val FEEDBACK_ENABLED = true
        const val HEADER_COLOR = "§6"
        const val ACCENT_COLOR = "§e"
        const val SUCCESS_COLOR = "§a"
        const val ERROR_COLOR = "§c"
        const val INFO_COLOR = "§7"
        const val VALUE_COLOR = "§b"

        var isModuleEnabled by mutableStateOf(true)
    }


    private var isInGame by mutableStateOf(false)

    
    fun interceptOutboundPacket(interceptablePacket: InterceptablePacket) {
        if (!isModuleEnabled) return

        
        if (interceptablePacket.packet is TextPacket) {
            val packet = interceptablePacket.packet as TextPacket
            val message = packet.message.trim()

            
            if (message.startsWith(PREFIX)) {
                
                interceptablePacket.isIntercepted = true

                
                processCommand(message)

                
                TerminalViewModel.addTerminalLog("GameSession", "Command intercepted and not sent to server: $message")
            }
        }
    }

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isModuleEnabled) return

        when (interceptablePacket.packet) {
            is PlayerAuthInputPacket -> isInGame = true
            is TextPacket -> {
                val packet = interceptablePacket.packet as TextPacket

                
                val message = packet.message.trim()
                if (message.startsWith(PREFIX) && session.isProxyPlayer(packet.sourceName)) {
                    
                    interceptablePacket.isIntercepted = true

                    
                    processCommand(message)

                    
                    TerminalViewModel.addTerminalLog("GameSession", "Command intercepted: $message")
                }
            }
        }
    }

    private fun processCommand(message: String) {
        if (!message.startsWith(PREFIX)) return

        TerminalViewModel.addTerminalLog("GameSession", "Processing command: $message")

        val args = message.substring(PREFIX.length).split(" ").filter { it.isNotBlank() }
        if (args.isEmpty()) {
            sendClientMessage("${ERROR_COLOR}Empty command. Use $ACCENT_COLOR!help")
            return
        }

        if (!isSessionCreated || !FEEDBACK_ENABLED) return

        when (val command = args[0].lowercase()) {
            "toggle" -> handleToggle(args.getOrNull(1))
            "ping" -> handlePing()
            "help" -> handleHelp(args.getOrNull(1))
            "set" -> handleSet(args.getOrNull(1), args.getOrNull(2), args.getOrNull(3))
            "list" -> handleList()
            "reset" -> handleReset(args.getOrNull(1))
            "info" -> handleInfo(args.getOrNull(1))
            "module" -> handleModuleToggle(args.getOrNull(1))
            else -> sendClientMessage("${ERROR_COLOR}Unknown command: $ACCENT_COLOR$command$INFO_COLOR - Try $ACCENT_COLOR!help")
        }
    }

    private fun sendClientMessage(message: String) {
        session.displayClientMessage(message, TextPacket.Type.RAW)
        TerminalViewModel.addTerminalLog("GameSession", "Feedback sent: $message")
    }

    private fun formatModuleName(rawName: String): String {
        return rawName.replace("_", " ")
            .replace(Regex("\\s+"), " ")
            .trim()
            .split(" ")
            .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
    }

    private fun parseModuleName(input: String): String {
        return input.replace(Regex("[^a-zA-Z0-9]"), "").lowercase()
    }

    private fun handleModuleToggle(state: String?) {
        if (state == null) {
            sendClientMessage("${ERROR_COLOR}Usage: $ACCENT_COLOR!module <on/off>")
            return
        }
        when (state.lowercase()) {
            "on" -> {
                isModuleEnabled = true
                sendClientMessage("${SUCCESS_COLOR}ChatListener module enabled")
            }
            "off" -> {
                isModuleEnabled = false
                sendClientMessage("${SUCCESS_COLOR}ChatListener module disabled")
            }
            else -> sendClientMessage("${ERROR_COLOR}Invalid state. Use 'on' or 'off'")
        }
        TerminalViewModel.addTerminalLog("GameSession", "Module enabled: $isModuleEnabled")
    }

    private fun handleToggle(moduleName: String?) {
        if (moduleName == null) {
            sendClientMessage("${ERROR_COLOR}Usage: $ACCENT_COLOR!toggle <module>")
            return
        }
        val parsedName = parseModuleName(moduleName)
        val module = moduleManager.getModule(parsedName)
            ?: return sendClientMessage("${ERROR_COLOR}Module '$ACCENT_COLOR$moduleName$ERROR_COLOR' not found.")

        module.isEnabled = !module.isEnabled
        val state = if (module.isEnabled) "on" else "off"
        val stateColor = if (module.isEnabled) SUCCESS_COLOR else ERROR_COLOR
        sendClientMessage("$HEADER_COLOR${formatModuleName(module.name)} $INFO_COLOR${stateColor}$state$INFO_COLOR.")
        TerminalViewModel.addTerminalLog("GameSession", "Toggled module ${module.name} to $state")
    }

    private fun handlePing() {
        sendClientMessage("${SUCCESS_COLOR}Pong!")
        TerminalViewModel.addTerminalLog("GameSession", "Executed ping command")
    }

    private fun handleHelp(moduleName: String?) {
        if (moduleName == null) {
            displayGeneralHelp()
        } else {
            displayModuleHelp(moduleName)
        }
    }

    private fun displayGeneralHelp() {
        val modules = moduleManager.elements.filter { !it.private }.sortedBy { formatModuleName(it.name) }
        if (modules.isEmpty()) {
            sendClientMessage("${ERROR_COLOR}No modules available.")
            return
        }

        sendClientMessage("${HEADER_COLOR}Command Help ${INFO_COLOR}▼")
        sendClientMessage("${ACCENT_COLOR}Available Commands:")
        listOf(
            "!module <on/off> - Enable/disable this module",
            "!toggle <module> - Toggle module on/off",
            "!set <module> <setting> <value> - Set module setting",
            "!help [module] - Show this help or module details",
            "!list - List all modules",
            "!reset <module> - Reset module settings",
            "!info <module> - Show module information",
            "!ping - Test command response"
        ).forEach { sendClientMessage("$INFO_COLOR- $ACCENT_COLOR$it") }

        sendClientMessage("${ACCENT_COLOR}Modules (use !help <module> for details):")
        modules.forEach { module ->
            val status = if (module.isEnabled) "${SUCCESS_COLOR}ON" else "${ERROR_COLOR}OFF"
            sendClientMessage("$INFO_COLOR- $ACCENT_COLOR${formatModuleName(module.name)} $INFO_COLOR[$status]")
        }
        TerminalViewModel.addTerminalLog("GameSession", "Displayed general help")
    }

    private fun displayModuleHelp(moduleName: String) {
        val parsedName = parseModuleName(moduleName)
        val module = moduleManager.getModule(parsedName)
            ?: return sendClientMessage("${ERROR_COLOR}Module '$ACCENT_COLOR$moduleName$ERROR_COLOR' not found.")

        sendClientMessage("$HEADER_COLOR${formatModuleName(module.name)} Help ${INFO_COLOR}▼")
        sendClientMessage("${ACCENT_COLOR}Status: ${if (module.isEnabled) "${SUCCESS_COLOR}Enabled" else "${ERROR_COLOR}Disabled"}")
        val settings = module.values.filter { it is BoolValue || it is FloatValue || it is IntValue }
        if (settings.isNotEmpty()) {
            sendClientMessage("${ACCENT_COLOR}Settings:")
            settings.forEach { value ->
                val currentValue = when (value) {
                    is BoolValue -> if (value.value) "${SUCCESS_COLOR}true" else "${ERROR_COLOR}false"
                    is FloatValue -> "$VALUE_COLOR${value.value}$INFO_COLOR (Range: ${value.range})"
                    is IntValue -> "$VALUE_COLOR${value.value}$INFO_COLOR (Range: ${value.range})"
                    else -> "N/A"
                }
                sendClientMessage("$INFO_COLOR- $ACCENT_COLOR${value.name}: $currentValue")
            }
        } else {
            sendClientMessage("${INFO_COLOR}No configurable settings.")
        }
        TerminalViewModel.addTerminalLog("GameSession", "Displayed help for module $moduleName")
    }

    private fun handleSet(moduleName: String?, settingName: String?, valueString: String?) {
        if (moduleName == null || settingName == null || valueString == null) {
            sendClientMessage("${ERROR_COLOR}Usage: $ACCENT_COLOR!set <module> <setting> <value>")
            return
        }

        val parsedName = parseModuleName(moduleName)
        val module = moduleManager.getModule(parsedName)
            ?: return sendClientMessage("${ERROR_COLOR}Module '$ACCENT_COLOR$moduleName$ERROR_COLOR' not found.")

        val value = module.values.find { it.name.equals(settingName, ignoreCase = true) }
            ?: return sendClientMessage("${ERROR_COLOR}Setting '$ACCENT_COLOR$settingName$ERROR_COLOR' not found.")

        try {
            when (value) {
                is BoolValue -> value.value = valueString.toBooleanStrict()
                is FloatValue -> {
                    val newValue = valueString.toFloat()
                    if (newValue in value.range) value.value = newValue
                    else throw IllegalArgumentException("Value out of range ${value.range}")
                }
                is IntValue -> {
                    val newValue = valueString.toInt()
                    if (newValue in value.range) value.value = newValue
                    else throw IllegalArgumentException("Value out of range ${value.range}")
                }
                else -> throw IllegalArgumentException("Unsupported setting type")
            }
            sendClientMessage("$HEADER_COLOR${formatModuleName(module.name)}.${settingName} ${INFO_COLOR}set to $VALUE_COLOR$valueString$INFO_COLOR.")
            TerminalViewModel.addTerminalLog("GameSession", "Set ${module.name}.${settingName} to $valueString")
        } catch (e: Exception) {
            sendClientMessage("${ERROR_COLOR}Invalid '$ACCENT_COLOR$valueString$ERROR_COLOR' for $ACCENT_COLOR$settingName$ERROR_COLOR: ${e.message}")
            Log.e("CmdListener", "Failed to set $settingName: ${e.message}")
            TerminalViewModel.addTerminalLog("GameSession", "Failed to set ${module.name}.${settingName}: ${e.message}")
        }
    }

    private fun handleList() {
        val modules = moduleManager.elements.filter { !it.private }.sortedBy { formatModuleName(it.name) }
        if (modules.isEmpty()) {
            sendClientMessage("${ERROR_COLOR}No modules available.")
            return
        }

        sendClientMessage("${HEADER_COLOR}Module List ${INFO_COLOR}▼")
        modules.forEach { module ->
            val status = if (module.isEnabled) "${SUCCESS_COLOR}ON" else "${ERROR_COLOR}OFF"
            sendClientMessage("$INFO_COLOR- $ACCENT_COLOR${formatModuleName(module.name)} $INFO_COLOR[$status]")
        }
        sendClientMessage("${INFO_COLOR}Total: ${modules.size} modules")
        TerminalViewModel.addTerminalLog("GameSession", "Listed all modules")
    }

    private fun handleReset(moduleName: String?) {
        if (moduleName == null) {
            sendClientMessage("${ERROR_COLOR}Usage: $ACCENT_COLOR!reset <module>")
            return
        }

        val parsedName = parseModuleName(moduleName)
        val module = moduleManager.getModule(parsedName)
            ?: return sendClientMessage("${ERROR_COLOR}Module '$ACCENT_COLOR$moduleName$ERROR_COLOR' not found.")

        module.values.forEach { value ->
            when (value) {
                is BoolValue -> value.value = false
                is FloatValue -> value.value = value.range.start
                is IntValue -> value.value = value.range.first
                else  -> return
            }
        }
        sendClientMessage("$HEADER_COLOR${formatModuleName(module.name)} ${INFO_COLOR}settings reset to defaults.")
        TerminalViewModel.addTerminalLog("GameSession", "Reset settings for module $moduleName")
    }

    private fun handleInfo(moduleName: String?) {
        if (moduleName == null) {
            sendClientMessage("${ERROR_COLOR}Usage: $ACCENT_COLOR!info <module>")
            return
        }

        val parsedName = parseModuleName(moduleName)
        val module = moduleManager.getModule(parsedName)
            ?: return sendClientMessage("${ERROR_COLOR}Module '$ACCENT_COLOR$moduleName$ERROR_COLOR' not found.")

        sendClientMessage("$HEADER_COLOR${formatModuleName(module.name)} Info ${INFO_COLOR}▼")
        sendClientMessage("${ACCENT_COLOR}Status: ${if (module.isEnabled) "${SUCCESS_COLOR}Enabled" else "${ERROR_COLOR}Disabled"}")
        sendClientMessage("${ACCENT_COLOR}Category: $INFO_COLOR${module.category}")
        sendClientMessage("${ACCENT_COLOR}Private: $INFO_COLOR${if (module.private) "Yes" else "No"}")
        TerminalViewModel.addTerminalLog("GameSession", "Displayed info for module $moduleName")
    }
}