package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.inventory.ShulkerBoxSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.noahsarch.deeperdark.inventory.ContainerItemUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ShulkerBoxSlot.class)
public class ShulkerBoxSlotMixin {

    // getContainerSlot() and container are inherited from Slot, not declared on
    // ShulkerBoxSlot itself — cannot @Shadow them. Upcast to Slot instead.
    @Overwrite
    public boolean mayPlace(ItemStack stack) {
        // Block all container items (shulker boxes, our boxes) from nesting.
        if (ContainerItemUtil.getContainerSize(stack) >= 0) return false;
        Slot self = (Slot) (Object) this;
        return self.container.canPlaceItem(self.getContainerSlot(), stack);
    }
}
