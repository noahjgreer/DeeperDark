package net.noahsarch.deeperdark.potion;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.ChatFormatting;

import java.util.List;
import java.util.Optional;

public class ScentlessPotion {

    // Method to create a completely custom scentless potion with custom effects
    public static ItemStack createScentlessPotion(boolean isLong) {
        ItemStack potionStack = new ItemStack(Items.POTION);

        // ...existing code...
        MobEffectInstance windChargedEffect = new MobEffectInstance(
            MobEffects.WIND_CHARGED,
            isLong ? 19200 : 9600,  // 8 minutes or 3 minutes
            0,                     // Amplifier
            false,                 // Ambient
            true,                  // ShowParticles
            false                   // ShowIcon
        );

        // ...existing code...
        PotionContents customPotionContents = new PotionContents(
            Optional.empty(),                     // No base potion
            Optional.of(0x529EC9),               // Custom purple color
            List.of(windChargedEffect),          // Our custom effect list
            Optional.empty()                     // No custom name in component (we'll use display name)
        );

        // Set potion Lore with different colors for each line
        ItemLore lore = new ItemLore(List.of(
            Component.translatable("effect.deeperdark.scentless.duration", isLong ? "16:00" : "8:00")
                    .formatted(ChatFormatting.RESET).formatted(ChatFormatting.BLUE).styled(style -> {
                        return style.withItalic(false);
                    }),
            Component.translatable("effect.deeperdark.scentless.desc.1")
                    .formatted(ChatFormatting.RESET).formatted(ChatFormatting.GRAY).styled(style -> {
                        return style.withItalic(false);
                    }),
            Component.translatable("effect.deeperdark.scentless.desc.2")
                    .formatted(ChatFormatting.RESET).formatted(ChatFormatting.GRAY).styled(style -> {
                        return style.withItalic(false);
                    })
        ));

        // Create tooltip display component to hide potion contents
        TooltipDisplay tooltip = new TooltipDisplay(false,
            new java.util.LinkedHashSet<>(java.util.Set.of(DataComponents.POTION_CONTENTS))
        );

        // Set the custom potion contents
        potionStack.set(DataComponents.POTION_CONTENTS, customPotionContents);

        // Set lore
        potionStack.set(DataComponents.LORE, lore);

        // Set tooltip display to hide potion contents
        potionStack.set(DataComponents.TOOLTIP_DISPLAY, tooltip);

        // Set custom name
        potionStack.set(DataComponents.CUSTOM_NAME,
            Component.translatable(isLong ? "potion.deeperdark.long_scentless.name" : "potion.deeperdark.scentless.name").formatted(ChatFormatting.RESET).formatted(ChatFormatting.WHITE).styled(style -> {
                return style.withItalic(false);
            }));

        return potionStack;
    }

    // Helper method to check if a potion is our custom scentless potion
    public static boolean isScentlessPotion(ItemStack stack) {
        if (!(stack.getItem() instanceof net.minecraft.world.item.PotionItem)) {
            return false;
        }

        // Check if it has our custom name
        Component customName = stack.get(DataComponents.CUSTOM_NAME);
        if (customName != null && customName.getContent() instanceof TranslatableContents translatable) {
            String key = translatable.getKey();
            return key.equals("potion.deeperdark.scentless.name") ||
                   key.equals("potion.deeperdark.long_scentless.name");
        }

        return false;
    }
}
