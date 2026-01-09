package net.minecraft.block.entity;

import com.mojang.logging.LogUtils;
import java.util.Set;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class BlockEntityType implements FabricBlockEntityType {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final BlockEntityType FURNACE;
   public static final BlockEntityType CHEST;
   public static final BlockEntityType TRAPPED_CHEST;
   public static final BlockEntityType ENDER_CHEST;
   public static final BlockEntityType JUKEBOX;
   public static final BlockEntityType DISPENSER;
   public static final BlockEntityType DROPPER;
   public static final BlockEntityType SIGN;
   public static final BlockEntityType HANGING_SIGN;
   public static final BlockEntityType MOB_SPAWNER;
   public static final BlockEntityType CREAKING_HEART;
   public static final BlockEntityType PISTON;
   public static final BlockEntityType BREWING_STAND;
   public static final BlockEntityType ENCHANTING_TABLE;
   public static final BlockEntityType END_PORTAL;
   public static final BlockEntityType BEACON;
   public static final BlockEntityType SKULL;
   public static final BlockEntityType DAYLIGHT_DETECTOR;
   public static final BlockEntityType HOPPER;
   public static final BlockEntityType COMPARATOR;
   public static final BlockEntityType BANNER;
   public static final BlockEntityType STRUCTURE_BLOCK;
   public static final BlockEntityType END_GATEWAY;
   public static final BlockEntityType COMMAND_BLOCK;
   public static final BlockEntityType SHULKER_BOX;
   public static final BlockEntityType BED;
   public static final BlockEntityType CONDUIT;
   public static final BlockEntityType BARREL;
   public static final BlockEntityType SMOKER;
   public static final BlockEntityType BLAST_FURNACE;
   public static final BlockEntityType LECTERN;
   public static final BlockEntityType BELL;
   public static final BlockEntityType JIGSAW;
   public static final BlockEntityType CAMPFIRE;
   public static final BlockEntityType BEEHIVE;
   public static final BlockEntityType SCULK_SENSOR;
   public static final BlockEntityType CALIBRATED_SCULK_SENSOR;
   public static final BlockEntityType SCULK_CATALYST;
   public static final BlockEntityType SCULK_SHRIEKER;
   public static final BlockEntityType CHISELED_BOOKSHELF;
   public static final BlockEntityType BRUSHABLE_BLOCK;
   public static final BlockEntityType DECORATED_POT;
   public static final BlockEntityType CRAFTER;
   public static final BlockEntityType TRIAL_SPAWNER;
   public static final BlockEntityType VAULT;
   public static final BlockEntityType TEST_BLOCK;
   public static final BlockEntityType TEST_INSTANCE_BLOCK;
   private static final Set POTENTIALLY_EXECUTES_COMMANDS;
   private final BlockEntityFactory factory;
   private final Set blocks;
   private final RegistryEntry.Reference registryEntry;

   @Nullable
   public static Identifier getId(BlockEntityType type) {
      return Registries.BLOCK_ENTITY_TYPE.getId(type);
   }

   private static BlockEntityType create(String id, BlockEntityFactory factory, Block... blocks) {
      if (blocks.length == 0) {
         LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", id);
      }

      Util.getChoiceType(TypeReferences.BLOCK_ENTITY, id);
      return (BlockEntityType)Registry.register(Registries.BLOCK_ENTITY_TYPE, (String)id, new BlockEntityType(factory, Set.of(blocks)));
   }

   private BlockEntityType(BlockEntityFactory factory, Set blocks) {
      this.registryEntry = Registries.BLOCK_ENTITY_TYPE.createEntry(this);
      this.factory = factory;
      this.blocks = blocks;
   }

   public BlockEntity instantiate(BlockPos pos, BlockState state) {
      return this.factory.create(pos, state);
   }

   public boolean supports(BlockState state) {
      return this.blocks.contains(state.getBlock());
   }

   /** @deprecated */
   @Deprecated
   public RegistryEntry.Reference getRegistryEntry() {
      return this.registryEntry;
   }

   @Nullable
   public BlockEntity get(BlockView world, BlockPos pos) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      return blockEntity != null && blockEntity.getType() == this ? blockEntity : null;
   }

   public boolean canPotentiallyExecuteCommands() {
      return POTENTIALLY_EXECUTES_COMMANDS.contains(this);
   }

   static {
      FURNACE = create("furnace", FurnaceBlockEntity::new, Blocks.FURNACE);
      CHEST = create("chest", ChestBlockEntity::new, Blocks.CHEST);
      TRAPPED_CHEST = create("trapped_chest", TrappedChestBlockEntity::new, Blocks.TRAPPED_CHEST);
      ENDER_CHEST = create("ender_chest", EnderChestBlockEntity::new, Blocks.ENDER_CHEST);
      JUKEBOX = create("jukebox", JukeboxBlockEntity::new, Blocks.JUKEBOX);
      DISPENSER = create("dispenser", DispenserBlockEntity::new, Blocks.DISPENSER);
      DROPPER = create("dropper", DropperBlockEntity::new, Blocks.DROPPER);
      SIGN = create("sign", SignBlockEntity::new, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.CHERRY_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.PALE_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.CHERRY_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN, Blocks.PALE_OAK_WALL_SIGN, Blocks.CRIMSON_SIGN, Blocks.CRIMSON_WALL_SIGN, Blocks.WARPED_SIGN, Blocks.WARPED_WALL_SIGN, Blocks.MANGROVE_SIGN, Blocks.MANGROVE_WALL_SIGN, Blocks.BAMBOO_SIGN, Blocks.BAMBOO_WALL_SIGN);
      HANGING_SIGN = create("hanging_sign", HangingSignBlockEntity::new, Blocks.OAK_HANGING_SIGN, Blocks.SPRUCE_HANGING_SIGN, Blocks.BIRCH_HANGING_SIGN, Blocks.ACACIA_HANGING_SIGN, Blocks.CHERRY_HANGING_SIGN, Blocks.JUNGLE_HANGING_SIGN, Blocks.DARK_OAK_HANGING_SIGN, Blocks.PALE_OAK_HANGING_SIGN, Blocks.CRIMSON_HANGING_SIGN, Blocks.WARPED_HANGING_SIGN, Blocks.MANGROVE_HANGING_SIGN, Blocks.BAMBOO_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN, Blocks.CHERRY_WALL_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN, Blocks.PALE_OAK_WALL_HANGING_SIGN, Blocks.CRIMSON_WALL_HANGING_SIGN, Blocks.WARPED_WALL_HANGING_SIGN, Blocks.MANGROVE_WALL_HANGING_SIGN, Blocks.BAMBOO_WALL_HANGING_SIGN);
      MOB_SPAWNER = create("mob_spawner", MobSpawnerBlockEntity::new, Blocks.SPAWNER);
      CREAKING_HEART = create("creaking_heart", CreakingHeartBlockEntity::new, Blocks.CREAKING_HEART);
      PISTON = create("piston", PistonBlockEntity::new, Blocks.MOVING_PISTON);
      BREWING_STAND = create("brewing_stand", BrewingStandBlockEntity::new, Blocks.BREWING_STAND);
      ENCHANTING_TABLE = create("enchanting_table", EnchantingTableBlockEntity::new, Blocks.ENCHANTING_TABLE);
      END_PORTAL = create("end_portal", EndPortalBlockEntity::new, Blocks.END_PORTAL);
      BEACON = create("beacon", BeaconBlockEntity::new, Blocks.BEACON);
      SKULL = create("skull", SkullBlockEntity::new, Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD, Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD, Blocks.PIGLIN_HEAD, Blocks.PIGLIN_WALL_HEAD);
      DAYLIGHT_DETECTOR = create("daylight_detector", DaylightDetectorBlockEntity::new, Blocks.DAYLIGHT_DETECTOR);
      HOPPER = create("hopper", HopperBlockEntity::new, Blocks.HOPPER);
      COMPARATOR = create("comparator", ComparatorBlockEntity::new, Blocks.COMPARATOR);
      BANNER = create("banner", BannerBlockEntity::new, Blocks.WHITE_BANNER, Blocks.ORANGE_BANNER, Blocks.MAGENTA_BANNER, Blocks.LIGHT_BLUE_BANNER, Blocks.YELLOW_BANNER, Blocks.LIME_BANNER, Blocks.PINK_BANNER, Blocks.GRAY_BANNER, Blocks.LIGHT_GRAY_BANNER, Blocks.CYAN_BANNER, Blocks.PURPLE_BANNER, Blocks.BLUE_BANNER, Blocks.BROWN_BANNER, Blocks.GREEN_BANNER, Blocks.RED_BANNER, Blocks.BLACK_BANNER, Blocks.WHITE_WALL_BANNER, Blocks.ORANGE_WALL_BANNER, Blocks.MAGENTA_WALL_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, Blocks.YELLOW_WALL_BANNER, Blocks.LIME_WALL_BANNER, Blocks.PINK_WALL_BANNER, Blocks.GRAY_WALL_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, Blocks.CYAN_WALL_BANNER, Blocks.PURPLE_WALL_BANNER, Blocks.BLUE_WALL_BANNER, Blocks.BROWN_WALL_BANNER, Blocks.GREEN_WALL_BANNER, Blocks.RED_WALL_BANNER, Blocks.BLACK_WALL_BANNER);
      STRUCTURE_BLOCK = create("structure_block", StructureBlockBlockEntity::new, Blocks.STRUCTURE_BLOCK);
      END_GATEWAY = create("end_gateway", EndGatewayBlockEntity::new, Blocks.END_GATEWAY);
      COMMAND_BLOCK = create("command_block", CommandBlockBlockEntity::new, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK);
      SHULKER_BOX = create("shulker_box", ShulkerBoxBlockEntity::new, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX);
      BED = create("bed", BedBlockEntity::new, Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED);
      CONDUIT = create("conduit", ConduitBlockEntity::new, Blocks.CONDUIT);
      BARREL = create("barrel", BarrelBlockEntity::new, Blocks.BARREL);
      SMOKER = create("smoker", SmokerBlockEntity::new, Blocks.SMOKER);
      BLAST_FURNACE = create("blast_furnace", BlastFurnaceBlockEntity::new, Blocks.BLAST_FURNACE);
      LECTERN = create("lectern", LecternBlockEntity::new, Blocks.LECTERN);
      BELL = create("bell", BellBlockEntity::new, Blocks.BELL);
      JIGSAW = create("jigsaw", JigsawBlockEntity::new, Blocks.JIGSAW);
      CAMPFIRE = create("campfire", CampfireBlockEntity::new, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE);
      BEEHIVE = create("beehive", BeehiveBlockEntity::new, Blocks.BEE_NEST, Blocks.BEEHIVE);
      SCULK_SENSOR = create("sculk_sensor", SculkSensorBlockEntity::new, Blocks.SCULK_SENSOR);
      CALIBRATED_SCULK_SENSOR = create("calibrated_sculk_sensor", CalibratedSculkSensorBlockEntity::new, Blocks.CALIBRATED_SCULK_SENSOR);
      SCULK_CATALYST = create("sculk_catalyst", SculkCatalystBlockEntity::new, Blocks.SCULK_CATALYST);
      SCULK_SHRIEKER = create("sculk_shrieker", SculkShriekerBlockEntity::new, Blocks.SCULK_SHRIEKER);
      CHISELED_BOOKSHELF = create("chiseled_bookshelf", ChiseledBookshelfBlockEntity::new, Blocks.CHISELED_BOOKSHELF);
      BRUSHABLE_BLOCK = create("brushable_block", BrushableBlockEntity::new, Blocks.SUSPICIOUS_SAND, Blocks.SUSPICIOUS_GRAVEL);
      DECORATED_POT = create("decorated_pot", DecoratedPotBlockEntity::new, Blocks.DECORATED_POT);
      CRAFTER = create("crafter", CrafterBlockEntity::new, Blocks.CRAFTER);
      TRIAL_SPAWNER = create("trial_spawner", TrialSpawnerBlockEntity::new, Blocks.TRIAL_SPAWNER);
      VAULT = create("vault", VaultBlockEntity::new, Blocks.VAULT);
      TEST_BLOCK = create("test_block", TestBlockEntity::new, Blocks.TEST_BLOCK);
      TEST_INSTANCE_BLOCK = create("test_instance_block", TestInstanceBlockEntity::new, Blocks.TEST_INSTANCE_BLOCK);
      POTENTIALLY_EXECUTES_COMMANDS = Set.of(COMMAND_BLOCK, LECTERN, SIGN, HANGING_SIGN, MOB_SPAWNER, TRIAL_SPAWNER);
   }

   @FunctionalInterface
   private interface BlockEntityFactory {
      BlockEntity create(BlockPos pos, BlockState state);
   }
}
