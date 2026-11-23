package com.sweetljk.vitalassist.mixin.compat;

import com.sweetljk.vitalassist.cine.CineDirector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Jade（玉）的方块提示走自己的渲染通道，不受原版 HUD 隐藏影响，
 * 这里让它在本帧判断「是否显示」时直接返回 false。
 */
@Mixin(targets = "snownee.jade.overlay.OverlayRenderer")
public abstract class JadeOverlayMixin {

    @Inject(method = "shouldShow", at = @At("RETURN"), cancellable = true, require = 0, remap = false)
    private static void vitalassist$hideOverlay(CallbackInfoReturnable<Boolean> cir) {
        if (CineDirector.shouldHideExternalHud()) cir.setReturnValue(false);
    }
}
