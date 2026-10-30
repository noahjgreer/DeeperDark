package net.noahsarch.deeperdark.mixin;

import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Inject(method = "getMaxLevel", at = @At("HEAD"), cancellable = true)
    private void deeperdark$modifyMaxLevel(CallbackInfoReturnable<Integer> cir) {
        if (deeperdark$appliesUnbreakingV()) {
            cir.setReturnValue(5);
        }
    }

    @Inject(method = "getMinCost", at = @At("HEAD"), cancellable = true)
    private void deeperdark$modifyMinCost(int level, CallbackInfoReturnable<Integer> cir) {
        if (deeperdark$appliesUnbreakingV()) {
            // Keep vanilla feel while making level 5 realistically rollable.
            cir.setReturnValue(5 + (level - 1) * 7);
        }
    }

    @Inject(method = "getMaxCost", at = @At("HEAD"), cancellable = true)
    private void deeperdark$modifyMaxCost(int level, CallbackInfoReturnable<Integer> cir) {
        if (deeperdark$appliesUnbreakingV()) {
            cir.setReturnValue(5 + (level - 1) * 7 + 45);
        }
    }

    /**
     * Inject into modifyBlockExperience to make Fortune multiply block XP drops
     */
    @Inject(method = "modifyBlockExperience", at = @At("TAIL"))
    private void deeperdark$multiplyBlockExperience(ServerLevel world, int level, ItemStack stack, MutableFloat blockExperience, CallbackInfo ci) {
        if (deeperdark$appliesFortuneXpBonus()) {
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
    private void deeperdark$multiplyMobExperience(ServerLevel world, int level, ItemStack stack, Entity user, MutableFloat mobExperience, CallbackInfo ci) {
        if (deeperdark$appliesFortuneXpBonus()) {
            // Multiply XP by (1 + level), so Fortune I = 2x, Fortune II = 3x, Fortune III = 4x
            float currentXp = mobExperience.floatValue();
            if (currentXp > 0) {
                mobExperience.setValue(currentXp * (1 + level));
            }
        }
    }

    /** True for Fortune while fortune_xp_bonus is on; matched by translation key. */
    @Unique
    private boolean deeperdark$appliesFortuneXpBonus() {
        return DeeperDarkConfig.get().fortuneXpBonus && deeperdark$hasDescriptionKey("enchantment.minecraft.fortune");
    }

    /** True for Unbreaking while unbreaking_v is on. */
    @Unique
    private boolean deeperdark$appliesUnbreakingV() {
        return DeeperDarkConfig.get().unbreakingV && deeperdark$hasDescriptionKey("enchantment.minecraft.unbreaking");
    }

    @Unique
    private boolean deeperdark$hasDescriptionKey(String key) {
        Enchantment self = (Enchantment) (Object) this;
        return self.description().getContents() instanceof TranslatableContents translatable && key.equals(translatable.getKey());
    }
}
