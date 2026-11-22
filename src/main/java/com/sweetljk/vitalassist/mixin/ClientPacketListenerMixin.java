package com.sweetljk.vitalassist.mixin;

import com.sweetljk.vitalassist.feature.ZhouLi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    @Unique
    private boolean vitalassist$zhouLiProcessing;

    @Inject(method = "sendChat", at = @At("HEAD"), cancellable = true, remap = false)
    private void vitalassist$sendChat(String message, CallbackInfo ci) {
        if (!ZhouLi.isActive() || vitalassist$zhouLiProcessing) return;
        if (ZhouLi.shouldSkip(message)) return;

        ci.cancel();
        vitalassist$zhouLiProcessing = true;
        String original = message;
        ClientPacketListener self = (ClientPacketListener) (Object) this;

        ZhouLi.rewriteAsync(original).thenAcceptAsync(rewritten -> {
            self.sendChat(rewritten);
            vitalassist$zhouLiProcessing = false;
        }, Minecraft.getInstance());
    }
}
