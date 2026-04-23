package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.potion.ScentlessPotion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.item.alchemy.PotionContents;

@Mixin(Item.class)
public class PotionItemMixin {
    @Inject(method = "finishUsingItem", at = @At("RETURN"))
    private void onPotionConsumed(ItemStack stack, Level world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (!world.isClientSide() && stack.getItem() instanceof net.minecraft.world.item.PotionItem) {
            // Check if this is our custom scentless potion
            if (ScentlessPotion.isScentlessPotion(stack)) {
                // The potion already contains the WIND_CHARGED effect built-in
                // The vanilla potion consumption logic will handle applying the effects automatically
                // No additional code needed since effects are in the PotionContents
            }
        }
    }
}