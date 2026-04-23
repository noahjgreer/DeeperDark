package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ShulkerBoxBlockEntity.class)
public class ShulkerBoxBlockEntityMixin {
    @Overwrite
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return true;
    }
}
