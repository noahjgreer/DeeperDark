package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(MatchTool.class)
public abstract class MatchToolLootConditionMixin {

    @Shadow public abstract Optional<ItemPredicate> predicate();

    @Inject(method = "test(Lnet/minecraft/loot/context/LootContext;)Z", at = @At("HEAD"), cancellable = true)
    private void deeperdark$implicitSilkTouch(LootContext lootContext, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = lootContext.get(LootContextParams.TOOL);

        if (stack != null && deeperdark$isGoldenTool(stack)) {
             // Create a copy to avoid modifying the actual world item
             ItemStack modifiedStack = stack.copy();

             // Get existing enchantments
             ItemEnchantments enchantments = modifiedStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.DEFAULT);
             ItemEnchantments.Builder builder = new ItemEnchantments.Builder(enchantments);

             // Check if Silk Touch is already present, if not, add it
             Holder<Enchantment> silkTouch = lootContext.getWorld().getRegistryManager().getOrThrow(Registries.ENCHANTMENT).getOptional(Enchantments.SILK_TOUCH).orElse(null);

             if (silkTouch != null && enchantments.getLevel(silkTouch) == 0) {
                 builder.set(silkTouch, 1);
                 modifiedStack.set(DataComponents.ENCHANTMENTS, builder.build());

                 // Run the predicate check on the modified stack
                 boolean result = this.predicate().isEmpty() || this.predicate().get().test(modifiedStack);
                 cir.setReturnValue(result);
             }
        }
    }

    @Unique
    private boolean deeperdark$isGoldenTool(ItemStack stack) {
        return stack.isOf(Items.GOLDEN_PICKAXE) ||
               stack.isOf(Items.GOLDEN_AXE) ||
               stack.isOf(Items.GOLDEN_SHOVEL) ||
               stack.isOf(Items.GOLDEN_HOE) ||
               stack.isOf(Items.GOLDEN_SWORD);
    }
}

