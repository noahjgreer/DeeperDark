package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.inventory.ShulkerBoxSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ShulkerBoxSlot.class)
public class ShulkerBoxSlotMixin {

    @Overwrite
    public boolean mayPlace(ItemStack stack) {
        return true;
    }
}
