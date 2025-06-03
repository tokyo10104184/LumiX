package com.project.lumina.client.game.module.misc

import com.project.lumina.client.R
import com.project.lumina.client.constructors.ArrayListManager
import com.project.lumina.client.constructors.BoolValue
import com.project.lumina.client.constructors.CheatCategory
import com.project.lumina.client.constructors.Element
import com.project.lumina.client.game.InterceptablePacket

class ToggleSound(iconResId: Int = ir.alirezaivaz.tablericons.R.drawable.ic_music) : Element(
    name = "ToggleSound",
    category = CheatCategory.Misc,
    displayNameResId = R.string.module_togglesound,
    iconResId = iconResId
) {

    private var sound1 by boolValue("Celestia", true)
    private var sound2 by boolValue("Nursultan", false)
    private var sound3 by boolValue("Smooth", false)

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled) {
            session.toggleSounds(false)
            return
        }
        session.toggleSounds(true)


     if(sound1){
         sound2 = false
         sound3 =  false
         session.soundList(ArrayListManager.SoundSet.CELESTIAL)

     } else if (sound2){
         sound3 = false
         sound1 =  false
         session.soundList(ArrayListManager.SoundSet.ALTERNATE)

     } else if (sound3){
         sound2 = false
         sound1 =  false
         session.soundList(ArrayListManager.SoundSet.SPECIAL)

     }



    }



}
