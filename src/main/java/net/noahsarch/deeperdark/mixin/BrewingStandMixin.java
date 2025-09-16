package net.noahsarch.deeperdark.mixin;

import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.noahsarch.deeperdark.potion.ScentlessPotion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandMixin {

    @Inject(method = "craft", at = @At("HEAD"), cancellable = true)
    private static void onCraft(World world, BlockPos pos, DefaultedList<ItemStack> slots, CallbackInfo ci) {
        System.out.println("DEBUG - craft method called!");

        // Try to handle custom brewing first
        if (handleCustomBrewing(world, slots)) {
            System.out.println("DEBUG - Custom brewing successful, canceling vanilla brewing");
            ci.cancel(); // Cancel vanilla brewing if we handled a custom recipe
        }
    }

    @Unique
    private static boolean handleCustomBrewing(World world, DefaultedList<ItemStack> slots) {
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
                net.minecraft.component.type.PotionContentsComponent potionContents =
                    potionStack.getOrDefault(net.minecraft.component.DataComponentTypes.POTION_CONTENTS,
                    net.minecraft.component.type.PotionContentsComponent.DEFAULT);

                System.out.println("DEBUG CUSTOM - Slot " + i + " - Is Echo Shard? " + (ingredient.getItem() == Items.ECHO_SHARD));

                // Recipe 1: (Water OR Mundane OR Awkward) + Echo Shard = Scentless Potion
                if (ingredient.getItem() == Items.ECHO_SHARD && potionContents.potion().isPresent()) {
                    RegistryEntry<net.minecraft.potion.Potion> potionEntry = potionContents.potion().get();
                    RegistryKey<net.minecraft.potion.Potion> potionKey = potionEntry.getKey().orElse(null);
                    RegistryKey<net.minecraft.potion.Potion> waterKey = Potions.WATER.getKey().orElse(null);
                    RegistryKey<net.minecraft.potion.Potion> mundaneKey = Potions.MUNDANE.getKey().orElse(null);
                    RegistryKey<net.minecraft.potion.Potion> awkwardKey = Potions.AWKWARD.getKey().orElse(null);

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
            ingredient.decrement(1);
            System.out.println("DEBUG CUSTOM - Ingredient consumed, returning true");
        }

        return customRecipeHandled;
    }
}
