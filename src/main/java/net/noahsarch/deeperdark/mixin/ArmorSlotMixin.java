package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.ArmorSlot")
public abstract class ArmorSlotMixin {

    @Shadow private EquipmentSlot slot;

    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    private void deeperdark$allowAnyInHeadSlot(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (this.slot == EquipmentSlot.HEAD) {
            cir.setReturnValue(true);
        }
    }
}
