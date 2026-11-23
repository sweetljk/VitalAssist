package com.sweetljk.vitalassist.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.sweetljk.vitalassist.cine.CineDirector;

/**
 * 1.21.11 的投影 FOV 由 {@code GameRenderer.getFov(Camera, float, boolean)} 计算，
 * 在主投影与裁剪共同使用它之前叠加同一 partialTick 的脉冲。
 */
@Mixin(GameRenderer.class)
public abstract class CineFovMixin {

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true, require = 1)
    private void vitalassist$cineFov(Camera camera, float partialTick, boolean useFovSetting,
                                     CallbackInfoReturnable<Float> cir) {
        if (!CineDirector.isActive()) return;
        float offset = CineDirector.fovOffsetDeg(partialTick);
        if (offset != 0f) {
            cir.setReturnValue(cir.getReturnValue() + offset);
        }
    }
}
