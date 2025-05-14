package com.project.lumina.client.essentials

import android.content.Context
import android.content.Intent
import android.net.Uri

class ServerInit {

    companion object {
        fun addMinecraftServer(context: Context, localIp: String) {
            val uri = Uri.parse("minecraft://?addExternalServer=§bLumina Client|${localIp}:19132")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }


    }

}