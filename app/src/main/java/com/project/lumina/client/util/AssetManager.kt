package com.project.lumina.client.util

object AssetManager {
    private val drawableCache: Map<String, Int> by lazy {
        val result = mutableMapOf<String, Int>()
        val drawableClass = Class.forName("com.project.lumina.client.R\$drawable")
        val fields = drawableClass.declaredFields

        for (field in fields) {
            field.isAccessible = true
            val name = field.name
            val value = field.getInt(null)
            result[name] = value
        }

        result
    }

    private val stringCache: Map<String, Int> by lazy {
        val result = mutableMapOf<String, Int>()
        val stringClass = Class.forName("com.project.lumina.client.R\$string")
        val fields = stringClass.declaredFields

        for (field in fields) {
            field.isAccessible = true
            val name = field.name
            val value = field.getInt(null)
            result[name] = value
        }

        result
    }

    fun getString(str: String): Int =
        stringCache[str] ?: error("String resource $str not found")

    fun getAsset(str: String): Int =
        drawableCache[str] ?: error("Drawable resource $str not found")
}
