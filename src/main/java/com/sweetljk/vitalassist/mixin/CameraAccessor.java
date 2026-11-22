package com.sweetljk.vitalassist.mixin;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraAccessor {
    @Invoker("setPosition")
    void vitalassist$setPosition(double x, double y, double z);

    @Invoker("setRotation")
    void vitalassist$setRotation(float yaw, float pitch);
}
