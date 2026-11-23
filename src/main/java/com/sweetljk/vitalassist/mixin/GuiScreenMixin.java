package com.sweetljk.vitalassist.mixin;

import com.sweetljk.vitalassist.feature.ShulkerRestock;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 26.2 起界面切换从 {@code Minecraft.setScreen} 移到了 {@code Gui.setScreen}。
 */
@Mixin(Gui.class)
public abstract class GuiScreenMixin {
    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true, remap = false)
    private void vitalassist$setScreen(Screen screen, CallbackInfo ci) {
        if (ShulkerRestock.shouldHideScreen() && screen instanceof ShulkerBoxScreen) ci.cancel();
    }
}
