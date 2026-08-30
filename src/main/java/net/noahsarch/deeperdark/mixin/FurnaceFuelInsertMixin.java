package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The hopper-side counterpart to {@link FurnaceFuelSlotMixin}: hoppers bypass
 * the menu slot, so the one-filled-bucket fuel cap has to be enforced on the
 * container as well or a hopper would quietly stack lava buckets into the fuel
 * slot and burn the empty buckets away.
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public class FurnaceFuelInsertMixin {

    @Inject(method = "canPlaceItem", at = @At("RETURN"), cancellable = true)
    private void deeperdark$capFilledBuckets(int slot, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        if (slot == 1 && cir.getReturnValueZ() && itemStack.is(Items.LAVA_BUCKET)
                && !((Container) this).getItem(1).isEmpty()) {
            cir.setReturnValue(false);
        }
    }
}
