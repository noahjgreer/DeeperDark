package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.noahsarch.deeperdark.inventory.ContainerItemUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * An open container item stays in the player inventory: anywhere else (e.g. the item-opened
 * ender chest's own grid) it would leave the menu tracking it. Plain slots never ask the
 * container, so this is checked here, on both sides.
 */
@Mixin(Slot.class)
public class SlotOpenContainerMixin {

    @Shadow @Final public Container container;

    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    private void deeperdark$keepOpenItemInInventory(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!(this.container instanceof Inventory) && ContainerItemUtil.isOpen(stack)) cir.setReturnValue(false);
    }
}
