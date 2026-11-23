package com.sweetljk.vitalassist.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.sweetljk.vitalassist.cine.CineDirector;
import com.sweetljk.vitalassist.cine.CineOverlay;

/**
 * 电影视角叠加层的绘制入口。
 *
 * <p>26.2 把 HUD 渲染拆成了 {@code Gui} + {@code Hud}：{@code Gui.extractRenderState}
 * 只负责创建 {@code GuiGraphicsExtractor} 并转交给 {@code Hud}。因此这里直接注入
 * {@code Hud.extractRenderState} 的 RETURN —— 该注入点同时覆盖「正常绘制」与
 * 「HUD 被隐藏时提前 return」两条路径，运镜隐藏 HUD 时黑边与歌词照常绘制。</p>
 */
@Mixin(Hud.class)
public abstract class CineHudMixin {

    @Inject(method = "extractRenderState", at = @At("RETURN"), require = 1, remap = false)
    private void vitalassist$cineOverlay(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!CineDirector.isActive()) return;
        CineOverlay.render(graphics);
    }
}
