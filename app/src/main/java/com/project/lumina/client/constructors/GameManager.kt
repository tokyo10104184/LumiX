package com.project.lumina.client.constructors


import com.project.lumina.client.application.AppContext
import com.project.lumina.client.game.module.combat.AntiCrystalElement
import com.project.lumina.client.game.module.combat.CritBotElement
import com.project.lumina.client.game.module.combat.InfiniteAuraElement
import com.project.lumina.client.game.module.combat.KillauraElement
import com.project.lumina.client.game.module.combat.MaceAuraElement
import com.project.lumina.client.game.module.combat.QuickAttackElement
import com.project.lumina.client.game.module.combat.ReachElement
import com.project.lumina.client.game.module.world.StrafeElement
import com.project.lumina.client.game.module.combat.TPAuraElement
import com.project.lumina.client.game.module.combat.TriggerBotElement
import com.project.lumina.client.game.module.combat.VelocityElement
import com.project.lumina.client.game.module.config.ConfigManagerElement
import com.project.lumina.client.game.module.world.HasteElement
import com.project.lumina.client.game.module.misc.AntiKickElement
import com.project.lumina.client.game.module.misc.ArrayListElement
import com.project.lumina.client.game.module.misc.CrasherElement
import com.project.lumina.client.game.module.misc.DesyncElement
import com.project.lumina.client.game.module.misc.ESPElement
import com.project.lumina.client.game.module.world.NoClipElement
import com.project.lumina.client.game.module.misc.PositionLoggerElement

import com.project.lumina.client.game.module.misc.SessionInfoElement
import com.project.lumina.client.game.module.misc.SpeedoMeterElement
import com.project.lumina.client.game.module.misc.WaterMarkElement
import com.project.lumina.client.game.module.motion.AirJumpElement
import com.project.lumina.client.game.module.motion.AntiAFKElement
import com.project.lumina.client.game.module.world.AutoWalkElement
import com.project.lumina.client.game.module.motion.BhopElement
import com.project.lumina.client.game.module.combat.DamageBoostElement
import com.project.lumina.client.game.module.combat.HitboxElement
import com.project.lumina.client.game.module.motion.FlyElement
import com.project.lumina.client.game.module.world.FollowBotElement
import com.project.lumina.client.game.module.motion.FullStopElement
import com.project.lumina.client.game.module.motion.GlideElement
import com.project.lumina.client.game.module.motion.HighJumpElement
import com.project.lumina.client.game.module.world.JesusElement
import com.project.lumina.client.game.module.motion.JetPackElement
import com.project.lumina.client.game.module.motion.JitterFlyElement
import com.project.lumina.client.game.module.motion.LongJumpElement
import com.project.lumina.client.game.module.motion.MotionFlyElement
import com.project.lumina.client.game.module.combat.OpFightBotElement
import com.project.lumina.client.game.module.misc.CmdListener
import com.project.lumina.client.game.module.motion.AntiACFly
import com.project.lumina.client.game.module.world.PhaseElement
import com.project.lumina.client.game.module.motion.SpeedElement
import com.project.lumina.client.game.module.motion.SpiderElement

import com.project.lumina.client.game.module.motion.StepElement
import com.project.lumina.client.game.module.visual.FreeCameraElement
import com.project.lumina.client.game.module.visual.FullBrightElement
import com.project.lumina.client.game.module.visual.NoHurtCameraElement
import com.project.lumina.client.game.module.visual.TextSpoofElement
import com.project.lumina.client.game.module.visual.ZoomElement
import com.project.lumina.client.game.module.world.AutoNavigatorElement
import com.project.lumina.client.game.module.world.MinimapElement
import com.project.lumina.client.game.module.world.WorldDebuggerElement
import com.project.lumina.client.game.module.world.WorldSaveElement
import com.project.lumina.client.game.module.world.WorldSaveTesterElement
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import com.project.lumina.client.remlink.RemSession
import com.project.lumina.client.service.Services
import java.io.File

object GameManager {

    private val _elements: MutableList<Element> = ArrayList()

    val elements: List<Element> = _elements

    val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    init {
        with(_elements) {
            add(FlyElement())
            add(ZoomElement())
            add(AirJumpElement())
            add(AutoWalkElement())
            add(NoClipElement())
           
            add(HasteElement())
            add(SpeedElement())
            add(JetPackElement())
           
            add(HighJumpElement())

           
            add(BhopElement())
           
            add(NoHurtCameraElement())
            add(AntiAFKElement())

            add(PositionLoggerElement())
            add(MotionFlyElement())
            add(FreeCameraElement())
            add(KillauraElement())

           add(GlideElement())
            add(StepElement())
            add(LongJumpElement())
            add(SpiderElement())
           
            add(TPAuraElement())
            add(StrafeElement())
            add(FullStopElement())
            add(JitterFlyElement())
            add(PhaseElement())
            add(MaceAuraElement())
            add(TriggerBotElement())
            add(CritBotElement())
            add(InfiniteAuraElement())
          
            add(DamageBoostElement())
            add(FullBrightElement())
            add(OpFightBotElement())
            add(FollowBotElement())
            add(VelocityElement())
            add(AntiKickElement())
            add(QuickAttackElement())
            
            add(AutoNavigatorElement())
            add(ConfigManagerElement())


             add(AntiCrystalElement())
            add(CrasherElement())

            add(AntiACFly())
            add(TextSpoofElement())
            add(CmdListener(this@GameManager))

            if (Services.RemisOnline == false){
                add(SpeedoMeterElement())
                add(SessionInfoElement())
                add(ArrayListElement())
                add(WaterMarkElement())
                add(MinimapElement())
               
                add(DesyncElement())
                add(HitboxElement())

            }
        }


     
    }



    fun getModule(name: String): Element? {
        return elements.find { it.name.equals(name, ignoreCase = true) }
    }

    fun saveConfig() {
        val configsDir = AppContext.instance.filesDir.resolve("configs")
        configsDir.mkdirs()

        val config = configsDir.resolve("UserConfig.json")
        saveConfigToFile(config)
    }

    fun saveConfigToFile(configFile: File) {
        val jsonObject = buildJsonObject {
            put("modules", buildJsonObject {
                _elements.forEach {
                    put(it.name, it.toJson())
                }
            })
        }

        configFile.writeText(json.encodeToString(jsonObject))
    }

    fun loadConfig() {
        val configsDir = AppContext.instance.filesDir.resolve("configs")
        configsDir.mkdirs()

        val config = configsDir.resolve("UserConfig.json")
        if (!config.exists()) return


        loadConfigFromFile(config)
    }

    fun loadConfigFromFile(configFile: File) {
        if (!configFile.exists()) {
            return
        }

        val jsonString = configFile.readText()
        if (jsonString.isEmpty()) {
            return
        }

        val jsonObject = json.parseToJsonElement(jsonString).jsonObject
        val modules = jsonObject["modules"]!!.jsonObject
        _elements.forEach { module ->
            (modules[module.name] as? JsonObject)?.let {
                module.fromJson(it)
            }
        }
    }

}