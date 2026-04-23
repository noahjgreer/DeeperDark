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

    @Inject(method = "test", at = @At("HEAD"), cancellable = true)
    private void deeperdark$implicitSilkTouch(LootContext lootContext, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = (ItemStack) lootContext.getParameter(LootContextParams.TOOL);

        if (stack != null && deeperdark$isGoldenTool(stack)) {
             ItemStack modifiedStack = stack.copy();

             ItemEnchantments enchantments = modifiedStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
             ItemEnchantments.Mutable builder = new ItemEnchantments.Mutable(enchantments);

             Holder<Enchantment> silkTouch = lootContext.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(Enchantments.SILK_TOUCH).orElse(null);

             if (silkTouch != null && enchantments.getLevel(silkTouch) == 0) {
                 builder.set(silkTouch, 1);
                 modifiedStack.set(DataComponents.ENCHANTMENTS, builder.toImmutable());

                 boolean result = this.predicate().isEmpty() || this.predicate().get().test(modifiedStack);
                 cir.setReturnValue(result);
             }
        }
    }

    @Unique
    private boolean deeperdark$isGoldenTool(ItemStack stack) {
        return stack.getItem() == Items.GOLDEN_PICKAXE ||
               stack.getItem() == Items.GOLDEN_AXE ||
               stack.getItem() == Items.GOLDEN_SHOVEL ||
               stack.getItem() == Items.GOLDEN_HOE ||
               stack.getItem() == Items.GOLDEN_SWORD;
    }
}
