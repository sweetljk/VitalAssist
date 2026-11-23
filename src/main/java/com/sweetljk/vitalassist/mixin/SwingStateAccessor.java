package com.sweetljk.vitalassist.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** {@code LivingEntity.SwingState} 内部字段的读写口，用于把本体的挥手进度复制到假人身上。 */
@Mixin(LivingEntity.SwingState.class)
public interface SwingStateAccessor {
    @Accessor("currentSwing")
    LivingEntity.SwingDescription vitalassist$getCurrentSwing();

    @Accessor("currentSwing")
    void vitalassist$setCurrentSwing(LivingEntity.SwingDescription swing);

    @Accessor("ticks")
    int vitalassist$getTicks();

    @Accessor("ticks")
    void vitalassist$setTicks(int ticks);

    @Accessor("oldAnimation")
    float vitalassist$getOldAnimation();

    @Accessor("oldAnimation")
    void vitalassist$setOldAnimation(float value);

    @Accessor("animation")
    float vitalassist$getAnimation();

    @Accessor("animation")
    void vitalassist$setAnimation(float value);
}
