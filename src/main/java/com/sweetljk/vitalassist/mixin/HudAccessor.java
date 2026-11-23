package com.sweetljk.vitalassist.mixin;

import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 26.2 把 HUD 隐藏状态从 {@code Options.hideGui} 挪到了 {@link Hud#isHidden()}，
 * 但只暴露了 {@code isHidden()} 与 {@code toggle()}，没有 setter。
 * 电影视角需要确定性地隐藏/恢复 HUD，这里直接读写该字段。
 */
@Mixin(Hud.class)
public interface HudAccessor {
    @Accessor("isHidden")
    void vitalassist$setHidden(boolean hidden);
}
