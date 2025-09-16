package net.noahsarch.deeperdark.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potions;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class PotionItemMixin {
    @Inject(method = "finishUsing", at = @At("RETURN"))
    private void onPotionConsumed(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (!world.isClient && stack.getItem() instanceof net.minecraft.item.PotionItem) {
            // Get the potion contents using data components
            PotionContentsComponent potionContents = stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);

            // Check if this is our "scentless" potion (water breathing or long water breathing)
            if (potionContents.potion().isPresent()) {
                boolean isScentlessPotion = potionContents.potion().get() == Potions.WATER_BREATHING ||
                        potionContents.potion().get() == Potions.LONG_WATER_BREATHING;

                if (isScentlessPotion) {
                    // Check if it's a long potion
                    boolean isLong = potionContents.potion().get() == Potions.LONG_WATER_BREATHING;

                    // Apply WIND_CHARGED effect
                    user.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.WIND_CHARGED,  // Use the vanilla effect
                            isLong ? 9600 : 3600,        // Duration based on long/regular (8min vs 3min)
                            0,                           // Amplifier
                            false,                       // Ambient
                            false,                       // ShowParticles
                            true                         // ShowIcon
                    ));
                }
            }
        }
    }
}