package net.noahsarch.deeperdark;

import net.minecraft.block.ComposterBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.noahsarch.deeperdark.event.PlayerTickHandler;
import net.noahsarch.deeperdark.portal.SlipPortalHandler;
import net.noahsarch.deeperdark.potion.CustomBrewingRecipeHandler;
//import net.noahsarch.deeperdark.villager.ModVillagers;
import net.noahsarch.deeperdark.worldgen.SlipChunkGenerator;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.structure.processor.StructureProcessorType;
import net.noahsarch.deeperdark.block.entity.ActiveSpongeBlockEntity;
import net.noahsarch.deeperdark.block.entity.ModBlockEntities;
import net.noahsarch.deeperdark.event.SiphonEvents;
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

		// Register the Slip chunk generator
		Registry.register(Registries.CHUNK_GENERATOR,
				Identifier.of(MOD_ID, "slip_room_generator"),
				SlipChunkGenerator.CODEC);

		PALE_MANSION_PROCESSOR = Registry.register(Registries.STRUCTURE_PROCESSOR, Identifier.of(MOD_ID, "pale_mansion_processor"), () -> PaleMansionProcessor.CODEC);

		SiphonEvents.register();

		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("[Deeper Dark] Mod initialized!");

		ModBlockEntities.ACTIVE_SPONGE = Registry.register(
				Registries.BLOCK_ENTITY_TYPE,
				Identifier.of(MOD_ID, "active_sponge"),
				FabricBlockEntityTypeBuilder.create(ActiveSpongeBlockEntity::new, Blocks.WET_SPONGE).build()
		);

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