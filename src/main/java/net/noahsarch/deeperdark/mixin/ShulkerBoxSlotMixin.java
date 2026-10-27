package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.inventory.ShulkerBoxSlot;
import net.minecraft.world.item.ItemStack;
import net.noahsarch.deeperdark.inventory.ContainerItemUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Shulker slots use the shared container rule (vanilla's canFitInsideContainerItems refuses boxes and vaults). */
@Mixin(ShulkerBoxSlot.class)
public class ShulkerBoxSlotMixin {

    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    private void deeperdark$sharedRule(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(ContainerItemUtil.canStore(ContainerItemUtil.SHULKER_TIER, stack));
    }
}
