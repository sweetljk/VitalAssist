package com.sweetljk.vitalassist.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * 静默使用物品（烟花 / 珍珠等）：把目标物品临时换到手持槽使用，用完再换回原物品。
 *
 * <p>注意：背包点击是服务端权威的，{@code handleInventoryMouseClick} 只是发包，
 * 客户端要等服务器回包才会看到槽位变化。所以「换到手上」和「使用」必须分两 tick 做，
 * 否则使用动作拿到的是换走前的旧物品，表现为按快捷键没反应。</p>
 */
public final class SilentUseAction {
    private static final int RESTOCK_TIMEOUT_TICKS = 20;
    /** 等服务器确认交换的超时（20 tick = 1 秒，正常延迟下一两 tick 就够）。 */
    private static final int SWAP_TIMEOUT_TICKS = 20;

    /** 物品暂时不在背包里，等潜影盒补货后再用。 */
    private static Item pendingItem;
    private static int pendingTicks;

    /** 已经发出「换到手持槽」请求、正在等服务器确认的物品。 */
    private static Item swapItem;
    private static int swapItemSlot = -1;
    private static int swapHotbarSlot = -1;
    private static int swapTicks;

    private SilentUseAction() {
    }

    public static void tick() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.gameMode == null || client.gui.screen() != null) return;

        if (pendingItem != null) {
            tickPending(client);
            return;
        }
        if (swapItem != null) {
            tickSwap(client);
        }
    }

    public static void useFirework() {
        useItem(Items.FIREWORK_ROCKET);
    }

    private static void tickPending(Minecraft client) {
        Item item = pendingItem;
        if (InventoryHelper.findItemInInventory(client, item) != -1) {
            pendingItem = null;
            useItem(item);
            return;
        }
        if (--pendingTicks <= 0) pendingItem = null;
    }

    private static void tickSwap(Minecraft client) {
        if (++swapTicks > SWAP_TIMEOUT_TICKS) {
            // 服务器迟迟没有回包：不盲目再发一次交换，避免把背包搞乱。
            resetSwap();
            return;
        }

        ItemStack selected = client.player.getInventory().getSelectedItem();
        if (selected.getItem() != swapItem) return;

        // 手里已经是目标物品，可以安全使用；使用与换回同一个 tick 发出，服务器按顺序处理。
        client.gameMode.useItem(client.player, InteractionHand.MAIN_HAND);
        InventoryHelper.swapWithHotbar(client, swapItemSlot, swapHotbarSlot);
        resetSwap();
    }

    private static void useItem(Item item) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.gameMode == null || client.gui.screen() != null) return;
        if (swapItem != null || pendingItem != null) return;

        int itemSlot = InventoryHelper.findItemInInventory(client, item);
        if (itemSlot == -1) {
            if (ShulkerRestock.hasPendingAction()) return;
            if (!ShulkerRestock.tryRestock(client, item)) return;
            pendingItem = item;
            pendingTicks = RESTOCK_TIMEOUT_TICKS;
            return;
        }

        int selectedSlot = InventoryHelper.getSelectedHotbarSlot(client);
        if (itemSlot == selectedSlot) {
            client.gameMode.useItem(client.player, InteractionHand.MAIN_HAND);
            return;
        }

        InventoryHelper.swapWithHotbar(client, itemSlot, selectedSlot);
        swapItem = item;
        swapItemSlot = itemSlot;
        swapHotbarSlot = selectedSlot;
        swapTicks = 0;
    }

    private static void resetSwap() {
        swapItem = null;
        swapItemSlot = -1;
        swapHotbarSlot = -1;
        swapTicks = 0;
    }
}
