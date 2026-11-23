package com.sweetljk.vitalassist.mixin;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

    /**
     * 1.21.11 的相机同步入口是 {@code Camera.setup(Level, Entity, boolean, boolean, float)}，
     * 它内部完成位置/朝向计算。在 TAIL 覆盖绝对机位即可接管本帧相机。
     */
    @Inject(method = "setup", at = @At("TAIL"))
    private void vitalassist$afterSetup(Level level, Entity entity, boolean detached, boolean thirdPersonReverse,
                                        float partialTick, CallbackInfo ci) {
        CameraAccessor accessor = (CameraAccessor) this;
        float tickDelta = partialTick;

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
}
