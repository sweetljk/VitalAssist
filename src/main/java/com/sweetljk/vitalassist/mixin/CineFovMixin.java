package com.sweetljk.vitalassist.mixin;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.sweetljk.vitalassist.cine.CineDirector;

/** 在世界 FOV 写回相机之前叠加同一 partialTick 的脉冲，使主投影和裁剪一起更新。 */
@Mixin(Camera.class)
public abstract class CineFovMixin {

    @Inject(method = "calculateFov(F)F", at = @At("RETURN"), cancellable = true, require = 1, remap = false)
    private void vitalassist$cineFov(float partialTick, CallbackInfoReturnable<Float> cir) {
        if (!CineDirector.isActive()) return;
        float offset = CineDirector.fovOffsetDeg(partialTick);
        if (offset != 0f) {
            cir.setReturnValue(cir.getReturnValue() + offset);
        }
    }
}
