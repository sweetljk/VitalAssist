package com.sweetljk.vitalassist.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.sweetljk.vitalassist.feature.Freecam;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    /** 1.21.11 签名：{@code renderItemInHand(float partialTick, boolean sleeping, Matrix4f projectionMatrix)}。 */
    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    private void vitalassist$renderItemInHand(float partialTick, boolean sleeping, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (!Freecam.renderHands()) ci.cancel();
    }

    /**
     * 1.21.11 的准星射线在 {@code GameRenderer.pick(float)} 内计算，
     * 自由视角激活时用自由相机的位置与朝向替换原版视线。
     */
    @Inject(method = "pick(F)V", at = @At("HEAD"), cancellable = true)
    private void vitalassist$pick(float tickProgress, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (!Freecam.isActive() || client.player == null || client.level == null) return;

        Vec3 start = new Vec3(Freecam.getX(tickProgress), Freecam.getY(tickProgress), Freecam.getZ(tickProgress));
        Vec3 direction = Vec3.directionFromRotation(Freecam.getPitch(tickProgress), Freecam.getYaw(tickProgress));
        Vec3 end = start.add(direction.scale(client.player.blockInteractionRange()));

        // 方块射线
        client.hitResult = client.level.clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, client.player));

        // 实体射线
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                client.player,
                start, end,
                (new AABB(start, end)).inflate(1.0D),
                e -> !e.isSpectator() && e.isAlive() && e != Freecam.getFakePlayer(),
                client.player.entityInteractionRange()
        );
        if (entityHit != null) {
            double distToBlock = client.hitResult != null ? start.distanceToSqr(client.hitResult.getLocation()) : Double.MAX_VALUE;
            double distToEntity = start.distanceToSqr(entityHit.getLocation());
            if (distToEntity < distToBlock) {
                client.hitResult = entityHit;
                client.crosshairPickEntity = entityHit.getEntity();
            }
        }
        ci.cancel();
    }
}
