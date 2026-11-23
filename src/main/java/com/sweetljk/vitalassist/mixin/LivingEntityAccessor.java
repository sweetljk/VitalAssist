package com.sweetljk.vitalassist.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 26.3 把挥手动画移进了 {@code LivingEntity} 的私有 {@code SwingState}，
 * 原来的 swinging / swingTime / attackAnim 公有字段全部删除。
 * 自由视角的假人要跟本体保持同一套挥手状态，这里把状态对象取出来。
 */
@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor("swingState")
    LivingEntity.SwingState vitalassist$swingState();
}
