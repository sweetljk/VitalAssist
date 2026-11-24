package com.sweetljk.vitalassist.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/**
 * 静默使用物品（烟花 / 珍珠等）：把目标物品临时换到手持槽使用，用完立刻换回原物品。
 *
 * <p>换槽、使用、换回三步必须在同一 tick 内完成：
 * 26.3 的背包点击是「本地预测立即生效」，三步同 tick 时客户端手持物品在这一帧内
 * 换了又换回，既不会被渲染出来（看不见切换），也不会打断主手正在进行的持续使用
 * （例如长矛一直按右键）。拆成两 tick 会看到截图式的闪烁，并把主手右键打断一瞬。</p>
 */
public final class SilentUseAction {
    private static final int RESTOCK_TIMEOUT_TICKS = 20;

    /** 物品暂时不在背包里，等潜影盒补货后再用。 */
    private static Item pendingItem;
    private static int pendingTicks;

    private SilentUseAction() {
    }

    public static void tick() {
        if (pendingItem == null) return;

        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.gameMode == null || client.gui.screen() != null) return;

        if (InventoryHelper.findItemInInventory(client, pendingItem) != -1) {
            Item item = pendingItem;
            pendingItem = null;
            useItem(item);
            return;
        }

        if (--pendingTicks <= 0) pendingItem = null;
    }

    public static void useFirework() {
        useItem(Items.FIREWORK_ROCKET);
    }

    private static void useItem(Item item) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.gameMode == null || client.gui.screen() != null) return;
        if (pendingItem != null) return;

        int itemSlot = InventoryHelper.findItemInInventory(client, item);
        if (itemSlot == -1) {
            if (ShulkerRestock.hasPendingAction()) return;
            if (!ShulkerRestock.tryRestock(client, item)) return;
            pendingItem = item;
            pendingTicks = RESTOCK_TIMEOUT_TICKS;
            return;
        }

        int selectedSlot = InventoryHelper.getSelectedHotbarSlot(client);
        boolean alreadySelected = itemSlot == selectedSlot;

        if (!alreadySelected) {
            InventoryHelper.swapWithHotbar(client, itemSlot, selectedSlot);
        }

        client.gameMode.useItem(client.player, InteractionHand.MAIN_HAND);

        if (!alreadySelected) {
            InventoryHelper.swapWithHotbar(client, itemSlot, selectedSlot);
        }
    }
}
