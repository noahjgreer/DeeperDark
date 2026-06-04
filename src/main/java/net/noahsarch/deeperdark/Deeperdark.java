package net.noahsarch.deeperdark;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.noahsarch.deeperdark.menu.BoxMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.noahsarch.deeperdark.event.*;
import net.noahsarch.deeperdark.inventory.ItemBackedContainer;
import net.noahsarch.deeperdark.payload.AllIngredientsConsumableSyncPacket;
import net.noahsarch.deeperdark.payload.OpenContainerItemPayload;
import net.noahsarch.deeperdark.payload.PlayerLeashPacket;
import net.noahsarch.deeperdark.payload.VoidFogSyncPacket;
import net.noahsarch.deeperdark.item.ModItems;
import net.noahsarch.deeperdark.portal.SlipPortalHandler;
import net.noahsarch.deeperdark.potion.CustomBrewingRecipeHandler;
import net.noahsarch.deeperdark.ported.LeavesBeGonePort;
import net.noahsarch.deeperdark.sound.ModSounds;
import net.noahsarch.deeperdark.villager.ModVillagers;
import net.noahsarch.deeperdark.worldgen.SlipChunkGenerator;
import net.noahsarch.deeperdark.block.ModBlockEntities;
import net.noahsarch.deeperdark.block.ModBlocks;
import net.noahsarch.deeperdark.menu.ModMenus;
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

		// Register custom data components
		net.noahsarch.deeperdark.component.ModComponents.initialize();

		// Register custom blocks/items before recipes or events use them
		ModBlocks.initialize();
		ModBlockEntities.initialize();
		ModMenus.initialize();
		ModItems.initialize();

		// Register the Slip chunk generator
		Registry.register(net.minecraft.core.registries.BuiltInRegistries.CHUNK_GENERATOR,
				Identifier.fromNamespaceAndPath(MOD_ID, "slip_room_generator"),
				SlipChunkGenerator.CODEC);

		PALE_MANSION_PROCESSOR = Registry.register(net.minecraft.core.registries.BuiltInRegistries.STRUCTURE_PROCESSOR, Identifier.fromNamespaceAndPath(MOD_ID, "pale_mansion_processor"), () -> PaleMansionProcessor.CODEC);

		PayloadTypeRegistry.clientboundPlay().register(PlayerLeashPacket.ID, PlayerLeashPacket.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(VoidFogSyncPacket.ID, VoidFogSyncPacket.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(AllIngredientsConsumableSyncPacket.ID, AllIngredientsConsumableSyncPacket.CODEC);

		// Container-from-inventory: client → server
		PayloadTypeRegistry.serverboundPlay().register(OpenContainerItemPayload.ID, OpenContainerItemPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(OpenContainerItemPayload.ID, (payload, context) ->
			context.server().execute(() -> openContainerFromInventory(context.player(), payload.slot()))
		);

		// Sync config toggles to each client when they join
		net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(
					handler.player, new VoidFogSyncPacket(DeeperDarkConfig.get().voidFogEnabled));
			net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(
					handler.player, new AllIngredientsConsumableSyncPacket(DeeperDarkConfig.get().allIngredientsConsumable));
		});

		SiphonEvents.register();
		GunpowderTrailEvents.register();

		// Register glass door dye recipe
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
			Identifier.fromNamespaceAndPath(MOD_ID, "crafting_dyed_glass_door"),
			net.noahsarch.deeperdark.recipe.DyedGlassDoorRecipe.SERIALIZER);

		// Register custom ingredient for crafting
		net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer.register(net.noahsarch.deeperdark.recipe.ComponentIngredient.Serializer.INSTANCE);

		// Register custom recipe serializer for component-matching shapeless recipes
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
				net.noahsarch.deeperdark.recipe.ComponentShapelessRecipe.SERIALIZER_ID,
				net.noahsarch.deeperdark.recipe.ComponentShapelessRecipe.SERIALIZER);

		// Register wool shearing recipe (wool + shears = 4 string)
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
				net.noahsarch.deeperdark.recipe.WoolShearingRecipe.SERIALIZER_ID,
				net.noahsarch.deeperdark.recipe.WoolShearingRecipe.SERIALIZER);

		// Register box upgrade recipe (preserves inventory contents when upgrading tiers)
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
				net.noahsarch.deeperdark.recipe.BoxUpgradeRecipe.SERIALIZER_ID,
				net.noahsarch.deeperdark.recipe.BoxUpgradeRecipe.SERIALIZER);

		// Register collar upgrade recipe (preserves dye, trinkets, fuel when upgrading tiers)
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
				net.noahsarch.deeperdark.recipe.CollarUpgradeRecipe.SERIALIZER_ID,
				net.noahsarch.deeperdark.recipe.CollarUpgradeRecipe.SERIALIZER);

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
			// Populate the ingredient item set used by AllIngredientsConsumableMixin
			net.noahsarch.deeperdark.util.IngredientItemRegistry.buildIngredientSet(server.registryAccess());
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

        // Register collar trinket effects
        net.noahsarch.deeperdark.event.CollarEvents.register();

        // Register item magnet tick handler
        net.noahsarch.deeperdark.event.ItemMagnetHandler.register();

        // Register Slip portal mechanic
        SlipPortalHandler.register();

        BoneHeadEvents.register();
		FishHeadEvents.register();

		// Server-side ports for unmaintained 1.21.11 mods.
		LeavesBeGonePort.register();

		// Register moss growth handler for cobblestone/stone brick mossing
		net.noahsarch.deeperdark.event.MossGrowthHandler.register();

		// Register player leash handler (leash players with a lead)
		net.noahsarch.deeperdark.event.PlayerLeashHandler.register();

		// Register creature entity type
		net.noahsarch.deeperdark.entity.ModEntities.initialize();

		// Register creature system
		net.noahsarch.deeperdark.creature.CreatureManager.register();

	}

	/**
	 * Opens the appropriate container menu for a shulker box or box held in the
	 * player's inventory at the given slot. Called server-side from the
	 * OpenContainerItemPayload handler.
	 */
	private static void openContainerFromInventory(ServerPlayer player, int slot) {
		if (slot < 0 || slot >= player.getInventory().getContainerSize()) return;
		ItemStack stack = player.getInventory().getItem(slot);
		if (stack.isEmpty()) return;

		// Refuse to open if this exact item is already open from inventory.
		for (net.minecraft.world.inventory.Slot s : player.containerMenu.slots) {
			if (s.container instanceof ItemBackedContainer ibc && ibc.isTrackingItem(stack)) return;
		}

		if (stack.is(ItemTags.SHULKER_BOXES)) {
			ItemBackedContainer container = ItemBackedContainer.of(player, slot, 27);
			player.openMenu(new SimpleMenuProvider(
				(id, inv, p) -> new ShulkerBoxMenu(id, inv, container),
				stack.getHoverName()
			));
		} else if (stack.is(ModBlocks.FLIMSY_BOX.asItem())) {
			ItemBackedContainer container = ItemBackedContainer.of(player, slot, 3);
			player.openMenu(new SimpleMenuProvider(
				(id, inv, p) -> new BoxMenu(ModMenus.FLIMSY_BOX, id, inv, container, 1),
				stack.getHoverName()
			));
		} else if (stack.is(ModBlocks.STURDY_BOX.asItem())) {
			ItemBackedContainer container = ItemBackedContainer.of(player, slot, 6);
			player.openMenu(new SimpleMenuProvider(
				(id, inv, p) -> new BoxMenu(ModMenus.STURDY_BOX, id, inv, container, 2),
				stack.getHoverName()
			));
		} else if (stack.is(ModBlocks.REINFORCED_BOX.asItem())) {
			ItemBackedContainer container = ItemBackedContainer.of(player, slot, 9);
			player.openMenu(new SimpleMenuProvider(
				(id, inv, p) -> new DispenserMenu(id, inv, container),
				stack.getHoverName()
			));
		}
	}
}