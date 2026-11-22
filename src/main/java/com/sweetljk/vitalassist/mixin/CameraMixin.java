package com.sweetljk.vitalassist.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.sweetljk.vitalassist.feature.Freecam;
import com.sweetljk.vitalassist.feature.HaoQiChongTian;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;alignWithEntity(F)V", shift = At.Shift.AFTER, remap = false), remap = false)
    private void vitalassist$afterAlignWithEntity(DeltaTracker tickCounter, CallbackInfo ci) {
        if (!HaoQiChongTian.isFrozen() && !Freecam.isActive()) return;

        float tickDelta = ((Camera) (Object) this).getCameraEntityPartialTicks(tickCounter);
        CameraAccessor accessor = (CameraAccessor) this;
        if (HaoQiChongTian.isFrozen()) {
            accessor.vitalassist$setPosition(
                    HaoQiChongTian.getCameraX(tickDelta),
                    HaoQiChongTian.getCameraY(tickDelta),
                    HaoQiChongTian.getCameraZ(tickDelta)
            );
            accessor.vitalassist$setRotation(
                    HaoQiChongTian.getCameraYaw(tickDelta),
                    HaoQiChongTian.getCameraPitch()
            );
            return;
        }
        accessor.vitalassist$setPosition(
                Freecam.getX(tickDelta),
                Freecam.getY(tickDelta),
                Freecam.getZ(tickDelta)
        );
        accessor.vitalassist$setRotation(
                Freecam.getYaw(tickDelta),
                Freecam.getPitch(tickDelta)
        );
    }

    @ModifyVariable(method = "getMaxZoom", at = @At("HEAD"), ordinal = 0, argsOnly = true, remap = false)
    private float vitalassist$modifyGetMaxZoom(float desiredCameraDistance) {
        return Freecam.isActive() || HaoQiChongTian.isFrozen() ? 0.0F : desiredCameraDistance;
    }
}
