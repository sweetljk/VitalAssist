package com.sweetljk.vitalassist.mixin;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.sweetljk.vitalassist.cine.CineProbe;

/** 只记录宿主原始输入，不拦截原版事件。 */
@Mixin(KeyboardHandler.class)
public abstract class CineKeyboardMixin {

    @Inject(method = "keyPress(JILnet/minecraft/client/input/KeyEvent;)V", at = @At("HEAD"), require = 1)
    private void vitalassist$cineKey(long window, int action, KeyEvent event, CallbackInfo ci) {
        if (window != 0L && window == Minecraft.getInstance().getWindow().handle()) {
            CineProbe.onKey(event.key(), action);
        }
    }
}
