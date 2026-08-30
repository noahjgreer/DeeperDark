package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.inventory.FurnaceFuelSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Lava buckets stack now (see
 * {@link net.noahsarch.deeperdark.item.StackableBuckets}), but a furnace only
 * returns the empty bucket once its fuel stack runs out — a stack of 64 would
 * burn 63 buckets away. Cap the fuel slot at one, exactly as vanilla already
 * caps empty buckets there.
 */
@Mixin(FurnaceFuelSlot.class)
public class FurnaceFuelSlotMixin {

    @Inject(method = "getMaxStackSize(Lnet/minecraft/world/item/ItemStack;)I", at = @At("HEAD"), cancellable = true)
    private void deeperdark$capFilledBuckets(ItemStack itemStack, CallbackInfoReturnable<Integer> cir) {
        if (itemStack.is(Items.LAVA_BUCKET)) {
            cir.setReturnValue(1);
        }
    }
}
