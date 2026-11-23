package com.sweetljk.vitalassist.mixin;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.sweetljk.vitalassist.feature.HaoQiChongTian;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntityMixin {
    @Inject(method = "sendPosition", at = @At("HEAD"), cancellable = true)
    private void vitalassist$sendPosition(CallbackInfo ci) {
        if (HaoQiChongTian.isFrozen()) ci.cancel();
    }
}
