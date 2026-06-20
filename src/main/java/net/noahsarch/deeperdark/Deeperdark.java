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
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.ChestMenu;
import net.noahsarch.deeperdark.inventory.ContainerItemUtil;
import net.noahsarch.deeperdark.inventory.ItemBackedContainer;
import net.noahsarch.deeperdark.inventory.ItemBackedVaultEntity;
import net.noahsarch.deeperdark.inventory.OpenMarkedContainer;
import net.noahsarch.deeperdark.menu.VaultMenu;
import net.noahsarch.deeperdark.sound.ModSounds;
import net.noahsarch.deeperdark.duck.CraftingPanelHolder;
import net.noahsarch.deeperdark.payload.AllIngredientsConsumableSyncPacket;
import net.noahsarch.deeperdark.payload.OpenContainerItemPayload;
import net.noahsarch.deeperdark.payload.OpenFromScreenPayload;
import net.noahsarch.deeperdark.payload.OpenNestedContainerPayload;
import net.noahsarch.deeperdark.payload.PickFromContainerPayload;
import net.noahsarch.deeperdark.payload.SyncCraftingPanelPayload;
import net.noahsarch.deeperdark.payload.PlayerLeashPacket;
import net.noahsarch.deeperdark.payload.VoidFogSyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.state.BlockState;
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

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/**
	 * Set by ItemBackedContainer/VaultEntity stopOpen() (nested-child branch) to signal
	 * that handleContainerClose's TAIL injection should reopen the parent synchronously,
	 * eliminating the one-tick flash between child close and parent reopen.
	 * Keys are player UUIDs; values are the inventory slot holding the parent container.
	 */
	public static final java.util.Map<java.util.UUID, Integer> PENDING_PARENT_REOPENS =
			new java.util.concurrent.ConcurrentHashMap<>();

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
		PayloadTypeRegistry.clientboundPlay().register(OpenFromScreenPayload.ID, OpenFromScreenPayload.CODEC);

		// Container-from-inventory: client → server
		PayloadTypeRegistry.serverboundPlay().register(OpenContainerItemPayload.ID, OpenContainerItemPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(OpenContainerItemPayload.ID, (payload, context) ->
			context.server().execute(() -> openContainerFromInventory(context.player(), payload.slot(), true))
		);

		// Container-within-container: client → server
		PayloadTypeRegistry.serverboundPlay().register(OpenNestedContainerPayload.ID, OpenNestedContainerPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(OpenNestedContainerPayload.ID, (payload, context) ->
			context.server().execute(() -> openContainerFromCurrentMenu(context.player(), payload.menuSlotIndex()))
		);

		// Collar crafting panel state sync: client → server
		PayloadTypeRegistry.serverboundPlay().register(SyncCraftingPanelPayload.ID, SyncCraftingPanelPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(SyncCraftingPanelPayload.ID, (payload, context) ->
			context.server().execute(() -> {
				ServerPlayer player = context.player();
				if (player.inventoryMenu instanceof CraftingPanelHolder holder) {
					holder.deeperdark$setPanelOpen(payload.open());
				}
			})
		);

		// Pick-block from containers: client → server
		PayloadTypeRegistry.serverboundPlay().register(PickFromContainerPayload.ID, PickFromContainerPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(PickFromContainerPayload.ID, (payload, context) ->
			context.server().execute(() -> {
				ServerPlayer player = context.player();
				ServerLevel level = (ServerLevel) player.level();
				BlockPos pos = payload.blockPos();
				LOGGER.info("[DD-pick] server received pos={} player={}", pos, player.getName().getString());
				if (!player.isWithinBlockInteractionRange(pos, 1.0)) { LOGGER.info("[DD-pick] fail: range"); return; }
				if (!level.isLoaded(pos)) { LOGGER.info("[DD-pick] fail: not loaded"); return; }
				BlockState state = level.getBlockState(pos);
				ItemStack target = state.getCloneItemStack(level, pos, false);
				LOGGER.info("[DD-pick] target={} empty={}", target, target.isEmpty());
				if (target.isEmpty()) return;
				if (!target.isItemEnabled(level.enabledFeatures())) { LOGGER.info("[DD-pick] fail: not enabled"); return; }
				if (player.hasInfiniteMaterials()) { LOGGER.info("[DD-pick] fail: creative"); return; }
				Inventory inventory = player.getInventory();
				int directSlot = inventory.findSlotMatchingItem(target);
				LOGGER.info("[DD-pick] direct slot={}", directSlot);
				if (directSlot != -1) return;
				boolean extracted = searchAndExtractForPickBlock(inventory, target);
				LOGGER.info("[DD-pick] extracted={}", extracted);
				if (extracted) {
					int slot = inventory.findSlotMatchingItem(target);
					if (slot != -1) {
						if (Inventory.isHotbarSlot(slot)) {
							inventory.setSelectedSlot(slot);
						} else {
							inventory.pickSlot(slot);
						}
						player.connection.send(new ClientboundSetHeldSlotPacket(inventory.getSelectedSlot()));
						player.inventoryMenu.broadcastChanges();
					}
				}
			})
		);

		// Sync config toggles to each client when they join
		net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(
					handler.player, new VoidFogSyncPacket(DeeperDarkConfig.get().voidFogEnabled));
			net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(
					handler.player, new AllIngredientsConsumableSyncPacket(DeeperDarkConfig.get().allIngredientsConsumable));
		});

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
        net.noahsarch.deeperdark.worldgen.ModWorldgen.initialize();

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
	 * Opens the appropriate container menu for a container item held in the player's
	 * inventory at the given slot. Public so it can also be called from the right-click
	 * mixin (ContainerItemUseInHandMixin).
	 *
	 * @param fromScreen true when invoked via the Alt key from inside an open inventory
	 *                   screen; causes the FROM_SCREEN_MARKER_KEY to be stamped so that
	 *                   Escape backs out to inventory rather than exiting to the game.
	 */
	public static void openContainerFromInventory(ServerPlayer player, int slot, boolean fromScreen) {
		if (slot < 0 || slot >= player.getInventory().getContainerSize()) return;
		ItemStack stack = player.getInventory().getItem(slot);
		if (stack.isEmpty()) return;

		// Refuse to open if this exact item is already being tracked by an open container.
		for (net.minecraft.world.inventory.Slot s : player.containerMenu.slots) {
			if (s.container instanceof ItemBackedContainer ibc && ibc.isTrackingItem(stack)) return;
			if (s.container instanceof OpenMarkedContainer omc && omc.isTrackingItem(stack)) return;
		}
		if (player.containerMenu instanceof VaultMenu vm && vm.isTrackingItem(stack)) return;

		// When opening from hand, clear any stale FROM_SCREEN_MARKER_KEY left by a
		// prior session where the item was dropped before the menu could clean it up.
		if (!fromScreen) {
			net.minecraft.world.item.component.CustomData existing = stack.get(DataComponents.CUSTOM_DATA);
			if (existing != null && existing.copyTag().contains(ItemBackedContainer.FROM_SCREEN_MARKER_KEY)) {
				net.minecraft.world.item.component.CustomData.update(DataComponents.CUSTOM_DATA, stack,
						tag -> tag.remove(ItemBackedContainer.FROM_SCREEN_MARKER_KEY));
			}
		}

		playSound(player, stack, true);

		java.util.OptionalInt result = java.util.OptionalInt.empty();
		if (stack.is(ItemTags.SHULKER_BOXES)) {
			ItemBackedContainer container = ItemBackedContainer.of(player, slot, 27, SoundEvents.SHULKER_BOX_CLOSE);
			if (fromScreen) stampFromScreen(player, slot);
			result = player.openMenu(new SimpleMenuProvider(
				(id, inv, p) -> new ShulkerBoxMenu(id, inv, container),
				stack.getHoverName()
			));
		} else if (stack.is(ModBlocks.FLIMSY_BOX.asItem())) {
			ItemBackedContainer container = ItemBackedContainer.of(player, slot, 3, ModSounds.BOX_CLOSE);
			if (fromScreen) stampFromScreen(player, slot);
			result = player.openMenu(new SimpleMenuProvider(
				(id, inv, p) -> new BoxMenu(ModMenus.FLIMSY_BOX, id, inv, container, 1),
				stack.getHoverName()
			));
		} else if (stack.is(ModBlocks.STURDY_BOX.asItem())) {
			ItemBackedContainer container = ItemBackedContainer.of(player, slot, 6, ModSounds.BOX_CLOSE);
			if (fromScreen) stampFromScreen(player, slot);
			result = player.openMenu(new SimpleMenuProvider(
				(id, inv, p) -> new BoxMenu(ModMenus.STURDY_BOX, id, inv, container, 2),
				stack.getHoverName()
			));
		} else if (stack.is(ModBlocks.REINFORCED_BOX.asItem())) {
			ItemBackedContainer container = ItemBackedContainer.of(player, slot, 9, ModSounds.BOX_CLOSE);
			if (fromScreen) stampFromScreen(player, slot);
			result = player.openMenu(new SimpleMenuProvider(
				(id, inv, p) -> new DispenserMenu(id, inv, container),
				stack.getHoverName()
			));
		} else if (stack.is(Items.ENDER_CHEST)) {
			OpenMarkedContainer container = new OpenMarkedContainer(
				player.getEnderChestInventory(), player, stack, SoundEvents.ENDER_CHEST_CLOSE
			);
			if (fromScreen) stampFromScreen(player, slot);
			result = player.openMenu(new SimpleMenuProvider(
				(id, inv, p) -> ChestMenu.threeRows(id, inv, container),
				Component.translatable("container.enderchest")
			));
		} else if (ContainerItemUtil.isVaultItem(stack)) {
			ItemBackedVaultEntity entity = new ItemBackedVaultEntity(player, stack);
			if (fromScreen) stampFromScreen(player, slot);
			result = player.openMenu(new SimpleMenuProvider(
				(id, inv, p) -> new VaultMenu(entity.getVaultMenuType(), id, inv, entity),
				stack.getHoverName()
			));
		}

		if (fromScreen && result.isPresent()) {
			ServerPlayNetworking.send(player, new OpenFromScreenPayload(result.getAsInt()));
		}
	}

	/**
	 * Opens a container item found at {@code menuSlotIndex} inside the player's currently
	 * active container menu (e.g. a shulker box inside an open ender chest).
	 *
	 * The parent container item (in the player's own inventory) is stamped with
	 * {@link ItemBackedContainer#NESTED_FROM_KEY} so that closing the nested container
	 * automatically reopens the parent.
	 */
	public static void openContainerFromCurrentMenu(ServerPlayer player, int menuSlotIndex) {
		net.minecraft.world.inventory.AbstractContainerMenu currentMenu = player.containerMenu;
		if (menuSlotIndex < 0 || menuSlotIndex >= currentMenu.slots.size()) return;

		net.minecraft.world.inventory.Slot slot = currentMenu.slots.get(menuSlotIndex);
		// Only operate on non-inventory container slots.
		if (slot.container instanceof net.minecraft.world.entity.player.Inventory) return;

		ItemStack stack = slot.getItem();
		if (stack.isEmpty()) return;

		// Refuse if already open.
		net.minecraft.world.item.component.CustomData existing = stack.get(DataComponents.CUSTOM_DATA);
		if (existing != null && existing.copyTag().contains(ItemBackedContainer.OPEN_MARKER_KEY)) return;

		net.minecraft.world.Container parentStorage = slot.container;
		int slotInParent = slot.getContainerSlot();

		// Guard: only item types we actually open nested are supported. Ender chests
		// would stamp NESTED_FROM and play a sound with no menu opening, leaving the
		// parent item in a stuck state.
		if (!stack.is(ItemTags.SHULKER_BOXES)
				&& !stack.is(ModBlocks.FLIMSY_BOX.asItem())
				&& !stack.is(ModBlocks.STURDY_BOX.asItem())
				&& !stack.is(ModBlocks.REINFORCED_BOX.asItem())
				&& !ContainerItemUtil.isVaultItem(stack)) {
			return;
		}

		// Find which player-inventory slot holds the parent container (has OPEN_MARKER_KEY).
		// -1 means the parent is a placed block container (chest, barrel, etc.) — that's fine;
		// we just skip the NESTED_FROM stamp so closing the child doesn't attempt to auto-reopen
		// a block container (which manages its own open/close independently).
		int parentInventorySlot = findParentInventorySlot(player);
		if (parentInventorySlot >= 0) {
			// Stamp NESTED_FROM_KEY on the parent item BEFORE player.openMenu() closes the
			// current menu and removes its own OPEN_MARKER_KEY.  Stored as a boolean flag —
			// findNestedFromSlot() uses the live loop index rather than a stored slot number,
			// so the parent can be moved freely without breaking the back-navigation.
			ItemStack parentItem = player.getInventory().getItem(parentInventorySlot);
			net.minecraft.world.item.component.CustomData.update(DataComponents.CUSTOM_DATA, parentItem,
					tag -> tag.putBoolean(ItemBackedContainer.NESTED_FROM_KEY, true));
		}

		playSound(player, stack, true);
		LOGGER.info("[DD-nested] opening {} slotInParent={} parentSlot={}", stack.getItem(), slotInParent, parentInventorySlot);

		if (stack.is(ItemTags.SHULKER_BOXES)) {
			ItemBackedContainer container = ItemBackedContainer.ofNested(player, parentStorage, slotInParent, 27, net.minecraft.sounds.SoundEvents.SHULKER_BOX_CLOSE);
			var result = player.openMenu(new SimpleMenuProvider(
				(id, inv, p) -> new net.minecraft.world.inventory.ShulkerBoxMenu(id, inv, container),
				stack.getHoverName()
			));
			LOGGER.info("[DD-nested] shulker openMenu result={}", result);
		} else if (stack.is(ModBlocks.FLIMSY_BOX.asItem())) {
			ItemBackedContainer container = ItemBackedContainer.ofNested(player, parentStorage, slotInParent, 3, net.noahsarch.deeperdark.sound.ModSounds.BOX_CLOSE);
			var result = player.openMenu(new SimpleMenuProvider(
				(id, inv, p) -> new net.noahsarch.deeperdark.menu.BoxMenu(ModMenus.FLIMSY_BOX, id, inv, container, 1),
				stack.getHoverName()
			));
			LOGGER.info("[DD-nested] flimsy openMenu result={}", result);
		} else if (stack.is(ModBlocks.STURDY_BOX.asItem())) {
			ItemBackedContainer container = ItemBackedContainer.ofNested(player, parentStorage, slotInParent, 6, net.noahsarch.deeperdark.sound.ModSounds.BOX_CLOSE);
			var result = player.openMenu(new SimpleMenuProvider(
				(id, inv, p) -> new net.noahsarch.deeperdark.menu.BoxMenu(ModMenus.STURDY_BOX, id, inv, container, 2),
				stack.getHoverName()
			));
			LOGGER.info("[DD-nested] sturdy openMenu result={}", result);
		} else if (stack.is(ModBlocks.REINFORCED_BOX.asItem())) {
			ItemBackedContainer container = ItemBackedContainer.ofNested(player, parentStorage, slotInParent, 9, net.noahsarch.deeperdark.sound.ModSounds.BOX_CLOSE);
			var result = player.openMenu(new SimpleMenuProvider(
				(id, inv, p) -> new net.minecraft.world.inventory.DispenserMenu(id, inv, container),
				stack.getHoverName()
			));
			LOGGER.info("[DD-nested] reinforced openMenu result={}", result);
		} else if (ContainerItemUtil.isVaultItem(stack)) {
			ItemBackedVaultEntity entity = new ItemBackedVaultEntity(player, parentStorage, slotInParent);
			var result = player.openMenu(new SimpleMenuProvider(
				(id, inv, p) -> new VaultMenu(entity.getVaultMenuType(), id, inv, entity),
				stack.getHoverName()
			));
			LOGGER.info("[DD-nested] vault openMenu result={}", result);
		}
		// Ender chests nested inside other containers are not supported.
	}

	/** Returns the player-inventory slot index of the item currently stamped with OPEN_MARKER_KEY, or -1. */
	private static int findParentInventorySlot(ServerPlayer player) {
		net.minecraft.world.entity.player.Inventory inv = player.getInventory();
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack s = inv.getItem(i);
			net.minecraft.world.item.component.CustomData data = s.get(DataComponents.CUSTOM_DATA);
			if (data != null && data.copyTag().contains(ItemBackedContainer.OPEN_MARKER_KEY)) return i;
		}
		return -1;
	}

	/** Stamps the FROM_SCREEN_MARKER_KEY into the item at the given inventory slot. */
	private static void stampFromScreen(ServerPlayer player, int slot) {
		ItemStack s = player.getInventory().getItem(slot);
		if (!s.isEmpty()) {
			net.minecraft.world.item.component.CustomData.update(DataComponents.CUSTOM_DATA, s,
					tag -> tag.putBoolean(ItemBackedContainer.FROM_SCREEN_MARKER_KEY, true));
		}
	}

	/** Scans all 36 main inventory slots for container/bundle items, finds the smallest
	 *  matching stack, extracts as many items as will fit, and adds them to the inventory.
	 *  Returns true if anything was extracted. */
	public static boolean searchAndExtractForPickBlock(Inventory inventory, ItemStack target) {
		int bestContainerSlot = -1;
		int bestBundleSlot = -1;
		int bestCount = Integer.MAX_VALUE;

		for (int i = 0; i < 36; i++) {
			ItemStack carrier = inventory.getItem(i);
			if (carrier.isEmpty()) continue;

			ItemContainerContents contents = carrier.get(DataComponents.CONTAINER);
			if (contents != null) {
				LOGGER.info("[DD-pick-srv] slot {} has CONTAINER carrier={}", i, carrier);
				for (ItemStack stored : contents.allItemsCopyStream().toList()) {
					LOGGER.info("[DD-pick-srv]   stored={} sameItem={}", stored, ItemStack.isSameItem(stored, target));
					if (!stored.isEmpty() && ItemStack.isSameItem(stored, target)
							&& stored.getCount() < bestCount) {
						bestCount = stored.getCount();
						bestContainerSlot = i;
						bestBundleSlot = -1;
					}
				}
			}

			BundleContents bundle = carrier.get(DataComponents.BUNDLE_CONTENTS);
			if (bundle != null) {
				for (ItemStackTemplate tmpl : bundle.items()) {
					ItemStack stored = tmpl.create();
					if (!stored.isEmpty() && ItemStack.isSameItem(stored, target)
							&& tmpl.count() < bestCount) {
						bestCount = tmpl.count();
						bestBundleSlot = i;
						bestContainerSlot = -1;
					}
				}
			}
		}

		int available = deeperdark$spaceForItem(inventory, target);
		if (available <= 0) return false;

		if (bestContainerSlot >= 0) {
			ItemStack carrier = inventory.getItem(bestContainerSlot);
			ItemContainerContents contents = carrier.get(DataComponents.CONTAINER);
			if (contents == null) return false;
			java.util.List<ItemStack> items = contents.allItemsCopyStream()
					.collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
			for (int j = 0; j < items.size(); j++) {
				ItemStack stored = items.get(j);
				if (!stored.isEmpty() && ItemStack.isSameItem(stored, target)) {
					int extract = Math.min(stored.getCount(), available);
					ItemStack extracted = stored.copyWithCount(extract);
					stored.shrink(extract);
					if (stored.isEmpty()) items.set(j, ItemStack.EMPTY);
					carrier.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
					inventory.add(extracted);
					return true;
				}
			}
		}

		if (bestBundleSlot >= 0) {
			ItemStack carrier = inventory.getItem(bestBundleSlot);
			BundleContents bundle = carrier.get(DataComponents.BUNDLE_CONTENTS);
			if (bundle == null) return false;
			java.util.List<ItemStackTemplate> templates = new java.util.ArrayList<>(bundle.items());
			for (int j = 0; j < templates.size(); j++) {
				ItemStackTemplate tmpl = templates.get(j);
				ItemStack stored = tmpl.create();
				if (!stored.isEmpty() && ItemStack.isSameItem(stored, target)) {
					int extract = Math.min(tmpl.count(), available);
					ItemStack extracted = stored.copyWithCount(extract);
					int remaining = tmpl.count() - extract;
					if (remaining <= 0) {
						templates.remove(j);
					} else {
						templates.set(j, tmpl.withCount(remaining));
					}
					carrier.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(java.util.List.copyOf(templates)));
					inventory.add(extracted);
					return true;
				}
			}
		}

		return false;
	}

	/** Returns total space available in the player's 36 main inventory slots for the given item type. */
	private static int deeperdark$spaceForItem(Inventory inventory, ItemStack target) {
		int space = 0;
		for (int i = 0; i < 36; i++) {
			ItemStack slot = inventory.getItem(i);
			if (slot.isEmpty()) {
				space += target.getMaxStackSize();
			} else if (ItemStack.isSameItem(slot, target)) {
				space += target.getMaxStackSize() - slot.getCount();
			}
		}
		return space;
	}

	private static void playSound(ServerPlayer player, ItemStack stack, boolean open) {
		net.minecraft.sounds.SoundEvent sound;
		if (stack.is(ItemTags.SHULKER_BOXES)) {
			sound = open ? SoundEvents.SHULKER_BOX_OPEN : SoundEvents.SHULKER_BOX_CLOSE;
		} else if (stack.is(Items.ENDER_CHEST)) {
			sound = open ? SoundEvents.ENDER_CHEST_OPEN : SoundEvents.ENDER_CHEST_CLOSE;
		} else if (ContainerItemUtil.isVaultItem(stack)) {
			sound = open ? SoundEvents.ENDER_CHEST_OPEN : SoundEvents.ENDER_CHEST_CLOSE;
		} else {
			sound = open ? ModSounds.BOX_OPEN : ModSounds.BOX_CLOSE;
		}
		player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
			sound, SoundSource.BLOCKS, 1.0f, 1.0f);
	}
}