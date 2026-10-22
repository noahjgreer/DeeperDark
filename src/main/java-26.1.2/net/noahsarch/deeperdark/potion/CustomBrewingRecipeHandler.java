package net.noahsarch.deeperdark.potion;

import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Optional;

public class CustomBrewingRecipeHandler {
    public static void register() {
        // Register specific recipes for creating scentless potions
        FabricPotionBrewingBuilder.BUILD.register(builder -> {
            // Register specific recipes for each base potion type to avoid conflicts
            System.out.println("DEBUG - Registering brewing recipes");

            // Water + Echo Shard = Scentless
            builder.registerPotionRecipe(
                    Potions.WATER,                          // Water bottle base
                    Ingredient.of(Items.ECHO_SHARD),   // Echo shard ingredient
                    Potions.WATER                           // Output will be replaced by mixin
            );

            // Mundane + Echo Shard = Scentless
            builder.registerPotionRecipe(
                    Potions.MUNDANE,                        // Mundane potion base
                    Ingredient.of(Items.ECHO_SHARD),   // Echo shard ingredient
                    Potions.MUNDANE                         // Output will be replaced by mixin
            );

            // Awkward + Echo Shard = Scentless
            builder.registerPotionRecipe(
                    Potions.AWKWARD,                        // Awkward potion base
                    Ingredient.of(Items.ECHO_SHARD),   // Echo shard ingredient
                    Potions.AWKWARD                         // Output will be replaced by mixin
            );

            // The redstone extension is handled by the mixin directly, not registered here
        });
    }

    // 26.3 builds the same potion from data/deeperdark/recipe/brewing/; keep them in sync.
    public static ItemStack createScentlessPotion(boolean isLong) {
        ItemStack potionStack = new ItemStack(Items.POTION);

        MobEffectInstance windChargedEffect = new MobEffectInstance(
            MobEffects.WIND_CHARGED,
            isLong ? 19200 : 9600,  // 16 or 8 minutes
            0,                     // Amplifier
            false,                 // Ambient
            true,                  // ShowParticles
            false                   // ShowIcon
        );

        PotionContents customPotionContents = new PotionContents(
            Optional.empty(),                     // No base potion
            Optional.of(0x529EC9),               // Custom purple color
            List.of(windChargedEffect),          // Our custom effect list
            Optional.empty()                     // No custom name in component (we'll use display name)
        );

        // Set potion Lore with different colors for each line
        ItemLore lore = new ItemLore(List.of(
            Component.translatable("effect.deeperdark.scentless.duration", isLong ? "16:00" : "8:00")
                    .withStyle(ChatFormatting.RESET).withStyle(ChatFormatting.BLUE).withStyle(style -> style.withItalic(false)),
            Component.translatable("effect.deeperdark.scentless.desc.1")
                    .withStyle(ChatFormatting.RESET).withStyle(ChatFormatting.GRAY).withStyle(style -> style.withItalic(false)),
            Component.translatable("effect.deeperdark.scentless.desc.2")
                    .withStyle(ChatFormatting.RESET).withStyle(ChatFormatting.GRAY).withStyle(style -> style.withItalic(false))
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
            Component.translatable(isLong ? "potion.deeperdark.long_scentless.name" : "potion.deeperdark.scentless.name").withStyle(ChatFormatting.RESET).withStyle(ChatFormatting.WHITE).withStyle(style -> style.withItalic(false)));

        return potionStack;
    }
}
