package com.sweetljk.vitalassist.feature;

import fi.dy.masa.malilib.hotkeys.IKeybind;
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
        // 26.3 移除了 LWJGL/GLFW，直接问 malilib 自己记录的按键状态（后端无关）。
        return keybind.isKeybindHeld();
    }
}
