package com.sweetljk.vitalassist.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Avatar.class)
public interface PlayerEntityAccessor {
    @Accessor(value = "DATA_PLAYER_MODE_CUSTOMISATION", remap = false)
    static EntityDataAccessor<Byte> vitalassist$getPlayerModelParts() {
        throw new AssertionError();
    }
}
