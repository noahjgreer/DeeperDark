package net.noahsarch.deeperdark.mixin;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface BlastFurnaceBlockEntityAccessor {
    // (Accessor mixin removed, now handled by recipe generation)
}
