package com.sweetljk.vitalassist.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.sweetljk.vitalassist.cine.CineDirector;
import com.sweetljk.vitalassist.cine.CineOverlay;

/**
 * 电影视角叠加层的绘制入口。
 *
 * <p>TAIL 注入点在原版 {@code options.hideGui} 判断之后仍有执行路径，
 * 因此运镜隐藏 HUD 时黑边与歌词照常绘制。</p>
 */
@Mixin(Gui.class)
public abstract class CineHudMixin {

    @Inject(method = "extractRenderState", at = @At("TAIL"), require = 1, remap = false)
    private void vitalassist$cineOverlay(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!CineDirector.isActive()) return;
        CineOverlay.render(graphics);
    }
}
