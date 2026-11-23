package com.sweetljk.vitalassist.mixin;

import com.sweetljk.vitalassist.feature.ShulkerRestock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void vitalassist$setScreen(Screen screen, CallbackInfo ci) {
        if (ShulkerRestock.shouldHideScreen() && screen instanceof ShulkerBoxScreen) ci.cancel();
    }
}
