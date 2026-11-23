package com.sweetljk.vitalassist.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.sweetljk.vitalassist.cine.CineCameraRotation;
import com.sweetljk.vitalassist.cine.CineDirector;
import com.sweetljk.vitalassist.cine.CinePose;
import com.sweetljk.vitalassist.feature.Freecam;
import com.sweetljk.vitalassist.feature.HaoQiChongTian;

@Mixin(Camera.class)
public abstract class CameraMixin implements CineCameraRotation {

    @Unique
    private Float vitalassist$cineRoll;

    @Override
    public float cineRollDegrees() {
        return vitalassist$cineRoll == null ? Float.NaN : vitalassist$cineRoll;
    }

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;alignWithEntity(F)V", shift = At.Shift.AFTER, remap = false), remap = false)
    private void vitalassist$afterAlignWithEntity(DeltaTracker tickCounter, CallbackInfo ci) {
        Camera camera = (Camera) (Object) this;
        CameraAccessor accessor = (CameraAccessor) this;
        float tickDelta = camera.getCameraEntityPartialTicks(tickCounter);

        // 电影视角优先级最高：覆盖本帧绝对机位，并顺带写入 roll。
        if (CineDirector.isActive()) {
            CinePose pose = CineDirector.currentPose(tickDelta);
            if (pose != null) {
                accessor.vitalassist$setPosition(pose.x(), pose.y(), pose.z());
                vitalassist$cineRoll = pose.rollDeg();
                try {
                    accessor.vitalassist$setRotation(pose.yawDeg(), pose.pitchDeg());
                } finally {
                    vitalassist$cineRoll = null;
                }
                return;
            }
        }

        if (!HaoQiChongTian.isFrozen() && !Freecam.isActive()) return;

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
