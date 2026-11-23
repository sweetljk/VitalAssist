package com.sweetljk.vitalassist.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.sweetljk.vitalassist.cine.CineDirector;
import com.sweetljk.vitalassist.cine.CineOverlay;

/**
 * 电影视角叠加层的绘制入口。
 *
 * <p>1.21.11 的 {@code Gui.render(GuiGraphics, DeltaTracker)} 在 {@code options.hideGui}
 * 为真时只是跳过各分区绘制、并不会提前 return，因此 TAIL 注入点仍会执行，
 * 运镜隐藏 HUD 时黑边与歌词照常绘制。</p>
 */
@Mixin(Gui.class)
public abstract class CineHudMixin {

    @Inject(method = "render", at = @At("TAIL"), require = 1)
    private void vitalassist$cineOverlay(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!CineDirector.isActive()) return;
        CineOverlay.render(graphics);
    }
}
