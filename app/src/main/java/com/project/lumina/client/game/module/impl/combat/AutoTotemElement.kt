package com.project.lumina.client.game.module.impl.combat

import com.project.lumina.client.constructors.CheatCategory
import com.project.lumina.client.constructors.Element
import com.project.lumina.client.constructors.NetBound // NetBoundのimportを追加
import com.project.lumina.client.game.event.EventHook // EventHookのimportを追加
import com.project.lumina.client.game.event.EventTick // EventTickのimportを追加
import com.project.lumina.client.game.inventory.PlayerInventory // PlayerInventoryのimportを追加
import com.project.lumina.client.game.registry.itemDefinition // ItemData.itemDefinition拡張プロパティのimport
import com.project.lumina.client.util.AssetManager
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData // ItemData の import を追加

class AutoTotemElement(iconResId: Int = AssetManager.getAsset("ic_placeholder_black_24dp")) : Element( // TODO: 適切なアイコンを探すか作成する
    name = "AutoTotem",
    category = CheatCategory.Combat,
    iconResId = iconResId,
    displayNameResId = AssetManager.getString("module_autototem_display_name") // TODO: strings.xmlに表示名を追加する
) {
    val autoTotemEnabled by boolValue("AutoTotem", true)

    private var tickListener: EventHook<EventTick>? = null

    override fun onEnabled() {
        super.onEnabled()
        if (autoTotemEnabled && isEnabled) {
            registerTickListener()
        }
    }

    override fun onDisabled() {
        super.onDisabled()
        unregisterTickListener()
    }

    override fun onValueChanged(value: com.project.lumina.client.constructors.Value<*>) {
        super.onValueChanged(value)
        if (value.name == "AutoTotem") {
            if (isEnabled) {
                if (autoTotemEnabled) {
                    registerTickListener()
                } else {
                    unregisterTickListener()
                }
            }
        }
    }

    private fun registerTickListener() {
        if (tickListener == null && isSessionCreated) { // isSessionCreated を確認
            tickListener = EventHook(EventTick::class.java) { event ->
                onTick(event)
            }
            session.eventManager.register(tickListener!!)
        }
    }

    private fun unregisterTickListener() {
        tickListener?.let {
            if (isSessionCreated) { // isSessionCreated を確認
                session.eventManager.removeHandler(it)
            }
            tickListener = null
        }
    }

    private fun onTick(event: EventTick) {
        if (!isEnabled || !autoTotemEnabled || !isSessionCreated) return // モジュールと設定が有効でセッションが作成されているか

        val player = session.localPlayer ?: return
        val inventory = player.inventory ?: return

        // オフハンドにトーテムが既にあるか確認
        if (inventory.offhand.itemDefinition.identifier == "minecraft:totem_of_undying") {
            return
        }

        // メインインベントリ (0-35) からトーテムを探す
        for (i in 0..35) { // プレイヤーインベントリのメイン部分 (ホットバー + 主なインベントリ)
            val itemInSlot = inventory.content[i]
            if (itemInSlot != null && itemInSlot != ItemData.AIR && itemInSlot.itemDefinition.identifier == "minecraft:totem_of_undying") {
                // トーテムを見つけたらオフハンドに移動
                // PlayerInventory.SLOT_OFFHAND はオフハンドスロットの正しいインデックス (40)
                // AbstractInventory.moveItem の引数に合わせる
                // moveItemの第一引数は移動元スロットのインデックス、第二引数は移動先スロットのインデックス
                // オフハンドは独立したインベントリではなくPlayerInventoryの一部なので、destinationInventoryはinventory自身
                // SLOT_OFFHAND (40) を AbstractInventory の moveItem に渡す場合、
                // PlayerInventory 内での slot 40 を指すため、destinationSlot は PlayerInventory.SLOT_OFFHAND で良い
                inventory.moveItem(
                    sourceSlot = i,
                    destinationSlot = PlayerInventory.SLOT_OFFHAND, // PlayerInventoryの定数を使用
                    destinationInventory = inventory, // 自分自身のインベントリ
                    session = session
                )
                // 1回のtickで1つのトーテムを移動したら処理を終了
                return
            }
        }
    }
}
