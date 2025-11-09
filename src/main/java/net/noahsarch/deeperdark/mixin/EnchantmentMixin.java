package net.noahsarch.deeperdark.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    /**
     * Inject into modifyBlockExperience to make Fortune multiply block XP drops
     */
    @Inject(method = "modifyBlockExperience", at = @At("TAIL"))
    private void deeperdark$multiplyBlockExperience(ServerWorld world, int level, ItemStack stack, MutableFloat blockExperience, CallbackInfo ci) {
        // Check if this enchantment is Fortune
        if (deeperdark$isFortuneEnchantment()) {
            // Multiply XP by (1 + level), so Fortune I = 2x, Fortune II = 3x, Fortune III = 4x
            float currentXp = blockExperience.floatValue();
            if (currentXp > 0) {
                blockExperience.setValue(currentXp * (1 + level));
            }
        }
    }

    /**
     * Inject into modifyMobExperience to make Fortune multiply mob XP drops
     */
    @Inject(method = "modifyMobExperience", at = @At("TAIL"))
    private void deeperdark$multiplyMobExperience(ServerWorld world, int level, ItemStack stack, Entity user, MutableFloat mobExperience, CallbackInfo ci) {
        // Check if this enchantment is Fortune
        if (deeperdark$isFortuneEnchantment()) {
            // Multiply XP by (1 + level), so Fortune I = 2x, Fortune II = 3x, Fortune III = 4x
            float currentXp = mobExperience.floatValue();
            if (currentXp > 0) {
                mobExperience.setValue(currentXp * (1 + level));
            }
        }
    }

    /**
     * Helper method to check if an enchantment is Fortune
     * We check the translation key which contains "enchantment.minecraft.fortune"
     */
    @Unique
    private boolean deeperdark$isFortuneEnchantment() {
        Enchantment self = (Enchantment) (Object) this;
        String description = self.description().getString();
        // Check if the description contains "Fortune" (works across different languages for the base English translation)
        // The translation key for Fortune is "enchantment.minecraft.fortune"
        return description.toLowerCase().contains("fortune");
    }
}

