package net.noahsarch.deeperdark.potion;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Optional;

public class ScentlessPotion {

    // Method to create a completely custom scentless potion with custom effects
    public static ItemStack createScentlessPotion(boolean isLong) {
        ItemStack potionStack = new ItemStack(Items.POTION);

        // Create custom potion contents with wind charged effect instead of any base potion
        StatusEffectInstance windChargedEffect = new StatusEffectInstance(
            StatusEffects.WIND_CHARGED,
            isLong ? 19200 : 9600,  // 8 minutes or 3 minutes
            0,                     // Amplifier
            false,                 // Ambient
            true,                  // ShowParticles
            false                   // ShowIcon
        );

        // Create potion contents with no base potion, just our custom effect
        // Using the correct constructor: (potion, customColor, customEffects, customName)
        PotionContentsComponent customPotionContents = new PotionContentsComponent(
            Optional.empty(),                     // No base potion
            Optional.of(0x529EC9),               // Custom purple color
            List.of(windChargedEffect),          // Our custom effect list
            Optional.empty()                     // No custom name in component (we'll use display name)
        );

        // Set potion Lore with different colors for each line
        LoreComponent lore = new LoreComponent(List.of(
            Text.literal(isLong ? "Scentlessness (16:00)" : "Scentlessness (8:00)")
                    .formatted(Formatting.RESET).formatted(Formatting.BLUE).styled(style -> {
                        return style.withItalic(false);
                    }),
            Text.literal("Inhibits being caught by")
                    .formatted(Formatting.RESET).formatted(Formatting.GRAY).styled(style -> {
                        return style.withItalic(false);
                    }),
            Text.literal("the Warden's sniff attack.")
                    .formatted(Formatting.RESET).formatted(Formatting.GRAY).styled(style -> {
                        return style.withItalic(false);
                    })
        ));

        // Create tooltip display component to hide potion contents
        TooltipDisplayComponent tooltip = new TooltipDisplayComponent(false,
            new java.util.LinkedHashSet<>(java.util.Set.of(DataComponentTypes.POTION_CONTENTS))
        );

        // Set the custom potion contents
        potionStack.set(DataComponentTypes.POTION_CONTENTS, customPotionContents);

        // Set lore
        potionStack.set(DataComponentTypes.LORE, lore);

        // Set tooltip display to hide potion contents
        potionStack.set(DataComponentTypes.TOOLTIP_DISPLAY, tooltip);

        // Set custom name
        potionStack.set(DataComponentTypes.CUSTOM_NAME,
            Text.literal(isLong ? "Long Potion of Scentlessness" : "Potion of Scentlessness").formatted(Formatting.RESET).formatted(Formatting.WHITE).styled(style -> {
                return style.withItalic(false);
            }));

        return potionStack;
    }

    // Helper method to check if a potion is our custom scentless potion
    public static boolean isScentlessPotion(ItemStack stack) {
        if (!(stack.getItem() instanceof net.minecraft.item.PotionItem)) {
            return false;
        }

        // Check if it has our custom name
        Text customName = stack.get(DataComponentTypes.CUSTOM_NAME);
        if (customName != null) {
            String nameString = customName.getString();
            return nameString.equals("Potion of Scentlessness") ||
                   nameString.equals("Long Potion of Scentlessness");
        }

        return false;
    }
}
