package com.sweetljk.vitalassist.mixin;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.sweetljk.vitalassist.feature.AutoTool;

@Mixin(MultiPlayerGameMode.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @Inject(method = "startDestroyBlock", at = @At("HEAD"), remap = false)
    private void vitalassist$startDestroyBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        AutoTool.beforeBlockAttack(pos);
    }

    @Inject(method = "continueDestroyBlock", at = @At("HEAD"), remap = false)
    private void vitalassist$continueDestroyBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        AutoTool.beforeBlockAttack(pos);
    }
}
