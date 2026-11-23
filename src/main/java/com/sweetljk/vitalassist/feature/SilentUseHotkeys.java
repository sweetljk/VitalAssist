package com.sweetljk.vitalassist.feature;

import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import net.minecraft.client.Minecraft;
import com.sweetljk.vitalassist.config.Configs;

public final class SilentUseHotkeys {
    private static boolean fireworkWasPressed;

    private SilentUseHotkeys() {
    }

    public static void tick(Minecraft client) {
        if (!InventoryHelper.canOperateInventory(client)) {
            fireworkWasPressed = false;
            return;
        }

        SilentUseAction.tick();
        if (client.gui.screen() != null) return;

        boolean fireworkPressed = isPressed(client, Configs.SILENT_FIREWORK.getKeybind());

        if (fireworkPressed && !fireworkWasPressed) SilentUseAction.useFirework();

        fireworkWasPressed = fireworkPressed;
    }

    private static boolean isPressed(Minecraft client, IKeybind keybind) {
        if (!keybind.isValid() || keybind.getKeys().isEmpty()) return false;
        // 26.3 移除了 LWJGL/GLFW，这里改用 malilib 自己的按键状态查询：
        // 它按扫描码判断物理按键是否按住，与旧版直接轮询 GLFW 等价
        // （不能用 isKeybindHeld()，那依赖 malilib 每 tick 维护的 pressed 状态）。
        for (int key : keybind.getKeys()) {
            if (!KeybindMulti.isKeyDown(key)) return false;
        }
        return true;
    }
}
