package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.noahsarch.deeperdark.potion.ScentlessPotion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class PotionItemMixin {
    @Inject(method = "finishUsing", at = @At("RETURN"))
    private void onPotionConsumed(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (!world.isClient() && stack.getItem() instanceof net.minecraft.item.PotionItem) {
            // Check if this is our custom scentless potion
            if (ScentlessPotion.isScentlessPotion(stack)) {
                // The potion already contains the WIND_CHARGED effect built-in
                // The vanilla potion consumption logic will handle applying the effects automatically
                // No additional code needed since effects are in the PotionContentsComponent
            }
        }
    }
}