package net.noahsarch.deeperdark;

import net.minecraft.block.ComposterBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.noahsarch.deeperdark.event.PlayerTickHandler;
import net.noahsarch.deeperdark.portal.SlipPortalHandler;
import net.noahsarch.deeperdark.potion.CustomBrewingRecipeHandler;
import net.noahsarch.deeperdark.villager.ModVillagers;
import net.noahsarch.deeperdark.worldgen.SlipChunkGenerator;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.structure.processor.StructureProcessorType;
import net.noahsarch.deeperdark.event.SiphonEvents;
import net.noahsarch.deeperdark.event.GoldenCauldronEvents;
import net.noahsarch.deeperdark.event.GunpowderBlockEvents;
import net.noahsarch.deeperdark.event.WorldBorderHandler;
import net.noahsarch.deeperdark.worldgen.PaleMansionProcessor;

public class Deeperdark implements ModInitializer {
	public static final String MOD_ID = "deeperdark";
	public static StructureProcessorType<PaleMansionProcessor> PALE_MANSION_PROCESSOR;

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

//    public static final StatusEffect SCENTLESS = new DeeperDarkStatusEffects.ScentlessStatusEffect();
//    public static RegistryEntry<StatusEffect> SCENTLESS_ENTRY;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.

		// Initialize Config
		DeeperDarkConfig.load();

		// Register the Slip chunk generator
		Registry.register(Registries.CHUNK_GENERATOR,
				Identifier.of(MOD_ID, "slip_room_generator"),
				SlipChunkGenerator.CODEC);

		PALE_MANSION_PROCESSOR = Registry.register(Registries.STRUCTURE_PROCESSOR, Identifier.of(MOD_ID, "pale_mansion_processor"), () -> PaleMansionProcessor.CODEC);

		SiphonEvents.register();
		GoldenCauldronEvents.register();
		GunpowderBlockEvents.register();
		net.noahsarch.deeperdark.event.LeatherBlockEvents.register();
		net.noahsarch.deeperdark.event.FlintBlockEvents.register();
		net.noahsarch.deeperdark.event.RottenFleshBlockEvents.register();

		// Register custom ingredient for crafting
		net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer.register(net.noahsarch.deeperdark.recipe.ComponentIngredient.Serializer.INSTANCE);

		// Register tick handler for custom block tracker
		net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_WORLD_TICK.register(world -> {
			if (world instanceof net.minecraft.server.world.ServerWorld serverWorld) {
				// Run every 20 ticks (1 second) to be gentle
				if (serverWorld.getTime() % 20 == 0) {
					net.noahsarch.deeperdark.util.CustomBlockTracker.get(serverWorld).tick(serverWorld);
				}
			}
		});

		WorldBorderHandler.register();

        // Register custom commands
        net.noahsarch.deeperdark.command.DeeperDarkCommands.register();

        ModVillagers.registerVillagers();

		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("[Deeper Dark] Mod initialized!");


        DeepDarkBiomeModifier.init();

        // Register diamond as compostable (silly easter egg!)
        // Since registerCompostableItem is private, we access the public map directly
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(Items.DIAMOND.asItem(), 1.0f);

        // Register the effects
//        Registry.register(Registries.STATUS_EFFECT, Identifier.of("deeperdark:scentless"), SCENTLESS);
//        SCENTLESS_ENTRY = Registries.STATUS_EFFECT.getEntry(Identifier.of("deeperdark:scentless")).orElseThrow();

        // Register the potion itself somewhere else, as you already do
//        ScentlessPotion.registerPotions();


        // Register dimension freezing effect
        CustomBrewingRecipeHandler.register();

//        ModVillagers.registerVillagers();

        PlayerTickHandler.register();

        // Register Slip portal mechanic
        SlipPortalHandler.register();

	}
}