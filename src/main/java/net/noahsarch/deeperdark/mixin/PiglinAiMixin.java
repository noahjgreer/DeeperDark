package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.noahsarch.deeperdark.duck.CollarHolder;
import net.noahsarch.deeperdark.item.CollarItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {

    @Inject(method = "isWearingSafeArmor", at = @At("RETURN"), cancellable = true)
    private static void deeperdark$checkCollarGold(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        if (!(entity instanceof CollarHolder holder)) return;
        ItemStack collar = holder.deeperdark$getCollarItem();
        if (collar.isEmpty() || !(collar.getItem() instanceof CollarItem)) return;
        ItemContainerContents contents = collar.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        NonNullList<ItemStack> trinkets = NonNullList.withSize(5, ItemStack.EMPTY);
        contents.copyInto(trinkets);
        for (ItemStack trinket : trinkets) {
            if (trinket.is(Items.GOLD_INGOT)) {
                cir.setReturnValue(true);
                return;
            }
        }
    }
}
