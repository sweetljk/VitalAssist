package com.sweetljk.vitalassist.feature;

import fi.dy.masa.malilib.hotkeys.IKeybind;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
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
        if (client.screen != null) return;

        boolean fireworkPressed = isPressed(client, Configs.SILENT_FIREWORK.getKeybind());

        if (fireworkPressed && !fireworkWasPressed) SilentUseAction.useFirework();

        fireworkWasPressed = fireworkPressed;
    }

    private static boolean isPressed(Minecraft client, IKeybind keybind) {
        if (!keybind.isValid() || keybind.getKeys().isEmpty()) return false;
        long handle = client.getWindow().handle();
        for (int key : keybind.getKeys()) {
            if (key < 0) {
                int button = key + 100;
                if (button < GLFW.GLFW_MOUSE_BUTTON_1 || button > GLFW.GLFW_MOUSE_BUTTON_LAST) return false;
                if (GLFW.glfwGetMouseButton(handle, button) != GLFW.GLFW_PRESS) return false;
            } else if (GLFW.glfwGetKey(handle, key) != GLFW.GLFW_PRESS) {
                return false;
            }
        }
        return true;
    }
}
