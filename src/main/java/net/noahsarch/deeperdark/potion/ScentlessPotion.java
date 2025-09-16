package net.noahsarch.deeperdark.potion;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.potion.Potion;
import net.minecraft.text.Text;

public class ScentlessPotion {
    // Base potion types for the brewing system - these can be changed to any vanilla potion
    private static final RegistryEntry<Potion> BASE_SCENTLESS_POTION = Potions.WATER_BREATHING;
    private static final RegistryEntry<Potion> BASE_SCENTLESS_LONG_POTION = Potions.LONG_WATER_BREATHING;

    // Public constants for brewing recipes and detection - these use methods for flexibility
    public static final RegistryEntry<Potion> SCENTLESS = getScentlessPotion();
    public static final RegistryEntry<Potion> SCENTLESS_LONG = getScentlessLongPotion();

    // Methods to get the registry entries - you can modify these to change the base potions
    private static RegistryEntry<Potion> getScentlessPotion() {
        // You can add custom logic here if needed
        return BASE_SCENTLESS_POTION;
    }

    private static RegistryEntry<Potion> getScentlessLongPotion() {
        // You can add custom logic here if needed
        return BASE_SCENTLESS_LONG_POTION;
    }

    // Method to create a fully customized scentless potion ItemStack
    public static ItemStack createScentlessPotion(boolean isLong) {
        ItemStack potionStack = new ItemStack(Items.POTION);

        // Set the potion using data components (modern way)
        // Use the constants which call the methods
        PotionContentsComponent potionContents = new PotionContentsComponent(isLong ? SCENTLESS_LONG : SCENTLESS);
        potionStack.set(DataComponentTypes.POTION_CONTENTS, potionContents);

        // Add custom name to distinguish it from regular water breathing potions
        potionStack.set(DataComponentTypes.CUSTOM_NAME, Text.translatable(isLong ? "item.deeperdark.scentless_potion_long" : "item.deeperdark.scentless_potion"));

        return potionStack;
    }

    // Alternative method to create scentless potion with custom properties
    public static ItemStack createCustomScentlessPotion(boolean isLong, String customName) {
        ItemStack potionStack = createScentlessPotion(isLong);

        if (customName != null && !customName.isEmpty()) {
            potionStack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(customName));
        }

        return potionStack;
    }

    // Helper method to check if a potion is our "scentless" potion
    public static boolean isScentlessPotion(ItemStack stack) {
        PotionContentsComponent potionContents = stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);

        if (potionContents.potion().isPresent()) {
            RegistryEntry<Potion> potion = potionContents.potion().get();
            return potion == SCENTLESS || potion == SCENTLESS_LONG;
        }
        return false;
    }
}
