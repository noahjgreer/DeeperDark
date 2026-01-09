package net.noahsarch.deeperdark.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(MatchToolLootCondition.class)
public abstract class MatchToolLootConditionMixin {

    @Shadow public abstract Optional<ItemPredicate> predicate();

    @Inject(method = "test(Lnet/minecraft/loot/context/LootContext;)Z", at = @At("HEAD"), cancellable = true)
    private void deeperdark$implicitSilkTouch(LootContext lootContext, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = lootContext.get(LootContextParameters.TOOL);

        if (stack != null && deeperdark$isGoldenTool(stack)) {
             // Create a copy to avoid modifying the actual world item
             ItemStack modifiedStack = stack.copy();

             // Get existing enchantments
             ItemEnchantmentsComponent enchantments = modifiedStack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
             ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(enchantments);

             // Check if Silk Touch is already present, if not, add it
             RegistryEntry<Enchantment> silkTouch = lootContext.getWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOptional(Enchantments.SILK_TOUCH).orElse(null);

             if (silkTouch != null && enchantments.getLevel(silkTouch) == 0) {
                 builder.set(silkTouch, 1);
                 modifiedStack.set(DataComponentTypes.ENCHANTMENTS, builder.build());

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

