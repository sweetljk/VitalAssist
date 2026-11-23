package com.sweetljk.vitalassist.mixin;

import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.sweetljk.vitalassist.feature.Freecam;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    /** 26.3 的签名：{@code renderItemInHand(CameraRenderState, PlayerRenderState, GpuTextureView)}。 */
    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true, remap = false)
    private void vitalassist$renderItemInHand(CameraRenderState camera, PlayerRenderState playerState,
                                              GpuTextureView texture, CallbackInfo ci) {
        if (!Freecam.renderHands()) ci.cancel();
    }
}
