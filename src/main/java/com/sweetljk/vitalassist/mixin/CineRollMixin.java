package com.sweetljk.vitalassist.mixin;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import com.sweetljk.vitalassist.cine.CineCameraRotation;

/** 在相机旋转四元数构造时加入 roll，让基向量随最终姿态一起更新。 */
@Mixin(Camera.class)
public abstract class CineRollMixin {

    @ModifyArg(method = "setRotation(FF)V",
            at = @At(value = "INVOKE",
                    target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;", remap = false),
            index = 2, require = 1, allow = 1, remap = false)
    private float vitalassist$cineRollRadians(float originalRollRadians) {
        if (!(this instanceof CineCameraRotation rotation)) return originalRollRadians;
        float roll = rotation.cineRollDegrees();
        return Float.isFinite(roll) ? -(float) Math.toRadians(roll) : originalRollRadians;
    }
}
