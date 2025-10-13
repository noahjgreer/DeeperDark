package net.noahsarch.deeperdark;

import net.minecraft.block.ComposterBlock;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registry;
import net.noahsarch.deeperdark.potion.CustomBrewingRecipeHandler;
import net.noahsarch.deeperdark.potion.ScentlessPotion;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.registry.entry.RegistryEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Deeperdark implements ModInitializer {
	public static final String MOD_ID = "deeperdark";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

//    public static final StatusEffect SCENTLESS = new DeeperDarkStatusEffects.ScentlessStatusEffect();
//    public static RegistryEntry<StatusEffect> SCENTLESS_ENTRY;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
        DeepDarkBiomeModifier.init();

        // Register diamond as compostable (silly easter egg!)
        // Since registerCompostableItem is private, we access the public map directly
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(Items.DIAMOND.asItem(), 1.0f);

        // Register the effects
//        Registry.register(Registries.STATUS_EFFECT, Identifier.of("deeperdark:scentless"), SCENTLESS);
//        SCENTLESS_ENTRY = Registries.STATUS_EFFECT.getEntry(Identifier.of("deeperdark:scentless")).orElseThrow();

        // Register the potion itself somewhere else, as you already do
//        ScentlessPotion.registerPotions();

        CustomBrewingRecipeHandler.register();
	}
}