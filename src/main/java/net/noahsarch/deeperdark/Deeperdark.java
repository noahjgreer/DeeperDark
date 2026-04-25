package net.noahsarch.deeperdark;

import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.noahsarch.deeperdark.event.*;
import net.noahsarch.deeperdark.portal.SlipPortalHandler;
import net.noahsarch.deeperdark.potion.CustomBrewingRecipeHandler;
import net.noahsarch.deeperdark.ported.LeavesBeGonePort;
import net.noahsarch.deeperdark.ported.UnloadedActivityPort;
import net.noahsarch.deeperdark.sound.ModSounds;
import net.noahsarch.deeperdark.villager.ModVillagers;
import net.noahsarch.deeperdark.worldgen.SlipChunkGenerator;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.noahsarch.deeperdark.worldgen.PaleMansionProcessor;
import net.minecraft.core.registries.Registries;

public class Deeperdark implements ModInitializer {
	public static final String MOD_ID = "deeperdark";
	public static StructureProcessorType<PaleMansionProcessor> PALE_MANSION_PROCESSOR;

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

//    public static final MobEffect SCENTLESS = new DeeperDarkStatusEffects.ScentlessStatusEffect();
//    public static Holder<MobEffect> SCENTLESS_ENTRY;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.

		// Initialize Config
		DeeperDarkConfig.load();

		// Register custom sounds (not in registry, just creates SoundEvent objects)
		ModSounds.registerSounds();

		// Register the Slip chunk generator
		Registry.register(net.minecraft.core.registries.BuiltInRegistries.CHUNK_GENERATOR,
				Identifier.fromNamespaceAndPath(MOD_ID, "slip_room_generator"),
				SlipChunkGenerator.CODEC);

		PALE_MANSION_PROCESSOR = Registry.register(net.minecraft.core.registries.BuiltInRegistries.STRUCTURE_PROCESSOR, Identifier.fromNamespaceAndPath(MOD_ID, "pale_mansion_processor"), () -> PaleMansionProcessor.CODEC);

		SiphonEvents.register();
		GoldenCauldronEvents.register();
		GunpowderBlockEvents.register();
		net.noahsarch.deeperdark.event.LeatherBlockEvents.register();
		net.noahsarch.deeperdark.event.FlintBlockEvents.register();
		net.noahsarch.deeperdark.event.RottenFleshBlockEvents.register();
		net.noahsarch.deeperdark.event.CustomBlockPickBlock.register();

		// Register custom ingredient for crafting
		net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer.register(net.noahsarch.deeperdark.recipe.ComponentIngredient.Serializer.INSTANCE);

		// Register custom recipe serializer for component-matching shapeless recipes
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
				net.noahsarch.deeperdark.recipe.ComponentShapelessRecipe.SERIALIZER_ID,
				net.noahsarch.deeperdark.recipe.ComponentShapelessRecipe.SERIALIZER);

		// Register tick handler for custom block tracker
		net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_LEVEL_TICK.register(serverWorld -> {
			// Run every 20 ticks (1 second) to be gentle
			if (serverWorld.getLevelData().getGameTime() % 20 == 0) {
				net.noahsarch.deeperdark.util.CustomBlockTracker.get(serverWorld).tick(serverWorld);
			}
		});

		// Pre-load custom block trackers when server starts
		net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			for (net.minecraft.server.level.ServerLevel world : server.getAllLevels()) {
				net.noahsarch.deeperdark.util.CustomBlockTracker.get(world);
			}
		});

		// Save custom block data when server stops
		net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			for (net.minecraft.server.level.ServerLevel world : server.getAllLevels()) {
				net.noahsarch.deeperdark.util.CustomBlockTracker.get(world).save();
			}
		});

		WorldBorderHandler.register();

        // Register custom commands
        net.noahsarch.deeperdark.command.DeeperDarkCommands.register();
		net.noahsarch.deeperdark.command.DeeperDarkClientCommands.register();

        ModVillagers.registerVillagers();

		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("[Deeper Dark] Mod initialized!");


        DeepDarkBiomeModifier.init();

        // Register diamond as compostable (silly easter egg!)
        // Since registerCompostableItem is private, we access the public map directly
        ComposterBlock.COMPOSTABLES.put(Items.DIAMOND.asItem(), 1.0f);

        // Register the effects
//        Registry.register(Registries.STATUS_EFFECT, Identifier.withDefaultNamespace("deeperdark:scentless"), SCENTLESS);
//        SCENTLESS_ENTRY = Registries.STATUS_EFFECT.getEntry(Identifier.withDefaultNamespace("deeperdark:scentless")).orElseThrow();

        // Register the potion itself somewhere else, as you already do
//        ScentlessPotion.registerPotions();


        // Register dimension freezing effect
        CustomBrewingRecipeHandler.register();

//        ModVillagers.registerVillagers();

        PlayerTickHandler.register();

        // Register Slip portal mechanic
        SlipPortalHandler.register();

        BoneHeadEvents.register();
		FishHeadEvents.register();

		// Server-side ports for unmaintained 1.21.11 mods.
		LeavesBeGonePort.register();
		UnloadedActivityPort.register();

		// Register moss growth handler for cobblestone/stone brick mossing
		net.noahsarch.deeperdark.event.MossGrowthHandler.register();

		// Register creature system
		net.noahsarch.deeperdark.creature.CreatureManager.register();

	}
}