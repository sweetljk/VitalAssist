package com.sweetljk.vitalassist.mixin.compat;

import com.sweetljk.vitalassist.cine.CineDirector;
import fi.dy.masa.malilib.render.GuiContext;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * MiniHUD 的信息行不读取原版 HUD 隐藏状态，电影视角要单独把它压掉。
 *
 * <p>两个注入点对应不同 MiniHUD 版本的 HUD 渲染入口：
 * 0.38.x 叫 {@code onRenderGameOverlayPostAdvanced}，0.40.x 改名为 {@code onExtractGuiOverlayPost}。
 * 不存在的那个用 {@code require = 0} 跳过，所以跨版本都能用。</p>
 */
@Mixin(targets = "fi.dy.masa.minihud.event.RenderHandler")
public abstract class MiniHudHudMixin {

    @Inject(method = "onExtractGuiOverlayPost", at = @At("HEAD"), cancellable = true, require = 0, remap = false)
    private void vitalassist$hideHudPost(GuiContext context, float tickDelta, ProfilerFiller profiler, CallbackInfo ci) {
        if (CineDirector.shouldHideExternalHud()) ci.cancel();
    }

    @Inject(method = "onRenderGameOverlayPostAdvanced", at = @At("HEAD"), cancellable = true, require = 0, remap = false)
    private void vitalassist$hideHudPostAdvanced(GuiContext context, float tickDelta, ProfilerFiller profiler, CallbackInfo ci) {
        if (CineDirector.shouldHideExternalHud()) ci.cancel();
    }
}
