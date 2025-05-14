package com.project.lumina.client.essentials

object stringify {
    fun decode(encoded: String): String {
        return String(android.util.Base64.decode(encoded, android.util.Base64.DEFAULT))

    }
}