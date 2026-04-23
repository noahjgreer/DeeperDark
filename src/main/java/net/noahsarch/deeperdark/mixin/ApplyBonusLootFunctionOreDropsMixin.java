package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.core.Holder;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to modify Fortune enchantment behavior for ALL fortune-based drops.
 * This implements a custom Fortune system that is more balanced than vanilla:
 * - Fortune I: 25% chance to drop 2 items (configurable)
 * - Fortune II: 50% chance to drop 2 items (configurable)
 * - Fortune III: 75% chance to drop 2 items (configurable)
 * - Maximum drops are capped at 2 (configurable)
 *
 * This affects ALL fortune formulas (OreDrops, UniformBonusCount, BinomialWithBonusCount)
 * so it nerfs Fortune for ores, crops, gravel, glowstone, and everything else.
 */
@Mixin(ApplyBonusCount.class)
public class ApplyBonusLootFunctionOreDropsMixin {

    @Shadow @Final private Holder<Enchantment> enchantment;

    @Inject(
        method = "process(Lnet/minecraft/item/ItemStack;Lnet/minecraft/loot/context/LootContext;)Lnet/minecraft/item/ItemStack;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setCount(I)V"),
        cancellable = true
    )
    private void applyCustomFortuneLogic(ItemStack stack, LootContext context, CallbackInfoReturnable<ItemStack> cir) {
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();

        // If custom Fortune is disabled, use vanilla behavior
        if (!config.customFortuneEnabled) {
            return;
        }

        // Get the tool that was used
        net.minecraft.world.item.ItemInstance tool = context.getParameter(LootContextParams.TOOL);
        if (tool == null) {
            return;
        }

        // Get the fortune level
        int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(this.enchantment, tool);

        // If no fortune, don't modify
        if (fortuneLevel <= 0) {
            return;
        }

        // Get original count
        int originalCount = stack.getCount();

        // Determine drop chance based on Fortune level
        double dropChance;
        if (fortuneLevel == 1) {
            dropChance = config.fortune1DropChance;
        } else if (fortuneLevel == 2) {
            dropChance = config.fortune2DropChance;
        } else {
            // Fortune III and higher
            dropChance = config.fortune3DropChance;
        }

        // Apply custom logic: either original count or +1
        int newCount = originalCount;
        if (context.getRandom().nextDouble() < dropChance) {
            newCount = originalCount + 1;
        }

        // Cap at configured maximum
        newCount = Math.min(newCount, originalCount * config.fortuneMaxDrops);

        // Set the count and return
        stack.setCount(newCount);
        cir.setReturnValue(stack);
    }
}

