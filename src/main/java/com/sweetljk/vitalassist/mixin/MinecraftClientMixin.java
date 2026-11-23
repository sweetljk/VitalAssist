package com.sweetljk.vitalassist.mixin;

import com.sweetljk.vitalassist.feature.Freecam;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
    /** 自由视角时的准星射线：26.2 仍由 {@code Minecraft.pick(float)} 计算。 */
    @Inject(method = "pick", at = @At("HEAD"), cancellable = true, remap = false)
    private void vitalassist$pick(float tickProgress, CallbackInfo ci) {
        Minecraft client = (Minecraft) (Object) this;
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
