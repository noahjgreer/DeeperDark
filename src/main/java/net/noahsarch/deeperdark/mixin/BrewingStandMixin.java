package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.potion.ScentlessPotion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.PotionContents;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandMixin {

    @Inject(method = "doBrew", at = @At("HEAD"), cancellable = true)
    private static void onCraft(Level world, BlockPos pos, NonNullList<ItemStack> slots, CallbackInfo ci) {
        System.out.println("DEBUG - craft method called!");

        // Try to handle custom brewing first
        if (handleCustomBrewing(world, slots)) {
            System.out.println("DEBUG - Custom brewing successful, canceling vanilla brewing");
            ci.cancel(); // Cancel vanilla brewing if we handled a custom recipe
        }
    }

    @Unique
    private static boolean handleCustomBrewing(Level world, NonNullList<ItemStack> slots) {
        ItemStack ingredient = slots.get(3); // Ingredient slot

        System.out.println("DEBUG CUSTOM - Ingredient: " + ingredient.getItem());

        if (ingredient.isEmpty()) {
            System.out.println("DEBUG CUSTOM - No ingredient, returning false");
            return false;
        }

        boolean customRecipeHandled = false;

        // Check each potion slot (0, 1, 2)
        for (int i = 0; i < 3; i++) {
            ItemStack potionStack = slots.get(i);
            System.out.println("DEBUG CUSTOM - Checking slot " + i + ": " + potionStack.getItem());

            if (potionStack.getItem() == Items.POTION) {
                // Get the potion contents to check what type of potion it is
                net.minecraft.world.item.alchemy.PotionContents potionContents =
                    potionStack.getOrDefault(net.minecraft.core.component.DataComponents.POTION_CONTENTS,
                    net.minecraft.world.item.alchemy.PotionContents.EMPTY);

                System.out.println("DEBUG CUSTOM - Slot " + i + " - Is Echo Shard? " + (ingredient.getItem() == Items.ECHO_SHARD));

                // Recipe 1: (Water OR Mundane OR Awkward) + Echo Shard = Scentless Potion
                if (ingredient.getItem() == Items.ECHO_SHARD && potionContents.potion().isPresent()) {
                    Holder<net.minecraft.world.item.alchemy.Potion> potionEntry = potionContents.potion().get();
                    ResourceKey<net.minecraft.world.item.alchemy.Potion> potionKey = potionEntry.unwrapKey().orElse(null);
                    ResourceKey<net.minecraft.world.item.alchemy.Potion> waterKey = Potions.WATER.unwrapKey().orElse(null);
                    ResourceKey<net.minecraft.world.item.alchemy.Potion> mundaneKey = Potions.MUNDANE.unwrapKey().orElse(null);
                    ResourceKey<net.minecraft.world.item.alchemy.Potion> awkwardKey = Potions.AWKWARD.unwrapKey().orElse(null);

                    System.out.println("DEBUG CUSTOM - Potion Key: " + potionKey);
                    System.out.println("DEBUG CUSTOM - Mundane Key: " + mundaneKey);
                    System.out.println("DEBUG CUSTOM - Keys equal? " + (potionKey != null && potionKey.equals(mundaneKey)));

                    if (potionKey != null &&
                        (potionKey.equals(waterKey) ||
                         potionKey.equals(mundaneKey) ||
                         potionKey.equals(awkwardKey))) {
                        System.out.println("DEBUG - Creating scentless potion in slot " + i);
                        slots.set(i, ScentlessPotion.createScentlessPotion(false));
                        customRecipeHandled = true;
                    }
                }
                // Recipe 2: Scentless Potion + Redstone = Long Scentless Potion
                else if (ingredient.getItem() == Items.REDSTONE &&
                         ScentlessPotion.isScentlessPotion(potionStack)) {
                    System.out.println("DEBUG - Creating LONG scentless potion in slot " + i);
                    slots.set(i, ScentlessPotion.createScentlessPotion(true));
                    customRecipeHandled = true;
                }
            }
        }

        // Only consume ingredient if we actually handled a custom recipe
        if (customRecipeHandled) {
            ingredient.shrink(1);
            System.out.println("DEBUG CUSTOM - Ingredient consumed, returning true");
        }

        return customRecipeHandled;
    }
}
