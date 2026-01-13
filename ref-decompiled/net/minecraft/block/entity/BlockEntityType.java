/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityType
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.entity.BannerBlockEntity
 *  net.minecraft.block.entity.BarrelBlockEntity
 *  net.minecraft.block.entity.BeaconBlockEntity
 *  net.minecraft.block.entity.BedBlockEntity
 *  net.minecraft.block.entity.BeehiveBlockEntity
 *  net.minecraft.block.entity.BellBlockEntity
 *  net.minecraft.block.entity.BlastFurnaceBlockEntity
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.BlockEntityType$BlockEntityFactory
 *  net.minecraft.block.entity.BrewingStandBlockEntity
 *  net.minecraft.block.entity.BrushableBlockEntity
 *  net.minecraft.block.entity.CalibratedSculkSensorBlockEntity
 *  net.minecraft.block.entity.CampfireBlockEntity
 *  net.minecraft.block.entity.ChestBlockEntity
 *  net.minecraft.block.entity.ChiseledBookshelfBlockEntity
 *  net.minecraft.block.entity.CommandBlockBlockEntity
 *  net.minecraft.block.entity.ComparatorBlockEntity
 *  net.minecraft.block.entity.ConduitBlockEntity
 *  net.minecraft.block.entity.CopperGolemStatueBlockEntity
 *  net.minecraft.block.entity.CrafterBlockEntity
 *  net.minecraft.block.entity.CreakingHeartBlockEntity
 *  net.minecraft.block.entity.DaylightDetectorBlockEntity
 *  net.minecraft.block.entity.DecoratedPotBlockEntity
 *  net.minecraft.block.entity.DispenserBlockEntity
 *  net.minecraft.block.entity.DropperBlockEntity
 *  net.minecraft.block.entity.EnchantingTableBlockEntity
 *  net.minecraft.block.entity.EndGatewayBlockEntity
 *  net.minecraft.block.entity.EndPortalBlockEntity
 *  net.minecraft.block.entity.EnderChestBlockEntity
 *  net.minecraft.block.entity.FurnaceBlockEntity
 *  net.minecraft.block.entity.HangingSignBlockEntity
 *  net.minecraft.block.entity.HopperBlockEntity
 *  net.minecraft.block.entity.JigsawBlockEntity
 *  net.minecraft.block.entity.JukeboxBlockEntity
 *  net.minecraft.block.entity.LecternBlockEntity
 *  net.minecraft.block.entity.MobSpawnerBlockEntity
 *  net.minecraft.block.entity.PistonBlockEntity
 *  net.minecraft.block.entity.SculkCatalystBlockEntity
 *  net.minecraft.block.entity.SculkSensorBlockEntity
 *  net.minecraft.block.entity.SculkShriekerBlockEntity
 *  net.minecraft.block.entity.ShelfBlockEntity
 *  net.minecraft.block.entity.ShulkerBoxBlockEntity
 *  net.minecraft.block.entity.SignBlockEntity
 *  net.minecraft.block.entity.SkullBlockEntity
 *  net.minecraft.block.entity.SmokerBlockEntity
 *  net.minecraft.block.entity.StructureBlockBlockEntity
 *  net.minecraft.block.entity.TestBlockEntity
 *  net.minecraft.block.entity.TestInstanceBlockEntity
 *  net.minecraft.block.entity.TrappedChestBlockEntity
 *  net.minecraft.block.entity.TrialSpawnerBlockEntity
 *  net.minecraft.block.entity.VaultBlockEntity
 *  net.minecraft.datafixer.TypeReferences
 *  net.minecraft.registry.Registries
 *  net.minecraft.registry.Registry
 *  net.minecraft.registry.entry.RegistryEntry$Reference
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.BlockView
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.block.entity;

import com.mojang.datafixers.DSL;
import com.mojang.logging.LogUtils;
import java.util.Set;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.block.entity.CalibratedSculkSensorBlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.block.entity.CopperGolemStatueBlockEntity;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.block.entity.CreakingHeartBlockEntity;
import net.minecraft.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.entity.HangingSignBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.entity.SculkCatalystBlockEntity;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.block.entity.ShelfBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.entity.SmokerBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.entity.TestBlockEntity;
import net.minecraft.block.entity.TestInstanceBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class BlockEntityType<T extends BlockEntity>
implements FabricBlockEntityType {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final BlockEntityType<FurnaceBlockEntity> FURNACE = BlockEntityType.create((String)"furnace", FurnaceBlockEntity::new, (Block[])new Block[]{Blocks.FURNACE});
    public static final BlockEntityType<ChestBlockEntity> CHEST = BlockEntityType.create((String)"chest", ChestBlockEntity::new, (Block[])new Block[]{Blocks.CHEST, Blocks.COPPER_CHEST, Blocks.EXPOSED_COPPER_CHEST, Blocks.WEATHERED_COPPER_CHEST, Blocks.OXIDIZED_COPPER_CHEST, Blocks.WAXED_COPPER_CHEST, Blocks.WAXED_EXPOSED_COPPER_CHEST, Blocks.WAXED_WEATHERED_COPPER_CHEST, Blocks.WAXED_OXIDIZED_COPPER_CHEST});
    public static final BlockEntityType<TrappedChestBlockEntity> TRAPPED_CHEST = BlockEntityType.create((String)"trapped_chest", TrappedChestBlockEntity::new, (Block[])new Block[]{Blocks.TRAPPED_CHEST});
    public static final BlockEntityType<EnderChestBlockEntity> ENDER_CHEST = BlockEntityType.create((String)"ender_chest", EnderChestBlockEntity::new, (Block[])new Block[]{Blocks.ENDER_CHEST});
    public static final BlockEntityType<JukeboxBlockEntity> JUKEBOX = BlockEntityType.create((String)"jukebox", JukeboxBlockEntity::new, (Block[])new Block[]{Blocks.JUKEBOX});
    public static final BlockEntityType<DispenserBlockEntity> DISPENSER = BlockEntityType.create((String)"dispenser", DispenserBlockEntity::new, (Block[])new Block[]{Blocks.DISPENSER});
    public static final BlockEntityType<DropperBlockEntity> DROPPER = BlockEntityType.create((String)"dropper", DropperBlockEntity::new, (Block[])new Block[]{Blocks.DROPPER});
    public static final BlockEntityType<SignBlockEntity> SIGN = BlockEntityType.create((String)"sign", SignBlockEntity::new, (Block[])new Block[]{Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.CHERRY_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.PALE_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.CHERRY_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN, Blocks.PALE_OAK_WALL_SIGN, Blocks.CRIMSON_SIGN, Blocks.CRIMSON_WALL_SIGN, Blocks.WARPED_SIGN, Blocks.WARPED_WALL_SIGN, Blocks.MANGROVE_SIGN, Blocks.MANGROVE_WALL_SIGN, Blocks.BAMBOO_SIGN, Blocks.BAMBOO_WALL_SIGN});
    public static final BlockEntityType<HangingSignBlockEntity> HANGING_SIGN = BlockEntityType.create((String)"hanging_sign", HangingSignBlockEntity::new, (Block[])new Block[]{Blocks.OAK_HANGING_SIGN, Blocks.SPRUCE_HANGING_SIGN, Blocks.BIRCH_HANGING_SIGN, Blocks.ACACIA_HANGING_SIGN, Blocks.CHERRY_HANGING_SIGN, Blocks.JUNGLE_HANGING_SIGN, Blocks.DARK_OAK_HANGING_SIGN, Blocks.PALE_OAK_HANGING_SIGN, Blocks.CRIMSON_HANGING_SIGN, Blocks.WARPED_HANGING_SIGN, Blocks.MANGROVE_HANGING_SIGN, Blocks.BAMBOO_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN, Blocks.CHERRY_WALL_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN, Blocks.PALE_OAK_WALL_HANGING_SIGN, Blocks.CRIMSON_WALL_HANGING_SIGN, Blocks.WARPED_WALL_HANGING_SIGN, Blocks.MANGROVE_WALL_HANGING_SIGN, Blocks.BAMBOO_WALL_HANGING_SIGN});
    public static final BlockEntityType<MobSpawnerBlockEntity> MOB_SPAWNER = BlockEntityType.create((String)"mob_spawner", MobSpawnerBlockEntity::new, (Block[])new Block[]{Blocks.SPAWNER});
    public static final BlockEntityType<CreakingHeartBlockEntity> CREAKING_HEART = BlockEntityType.create((String)"creaking_heart", CreakingHeartBlockEntity::new, (Block[])new Block[]{Blocks.CREAKING_HEART});
    public static final BlockEntityType<PistonBlockEntity> PISTON = BlockEntityType.create((String)"piston", PistonBlockEntity::new, (Block[])new Block[]{Blocks.MOVING_PISTON});
    public static final BlockEntityType<BrewingStandBlockEntity> BREWING_STAND = BlockEntityType.create((String)"brewing_stand", BrewingStandBlockEntity::new, (Block[])new Block[]{Blocks.BREWING_STAND});
    public static final BlockEntityType<EnchantingTableBlockEntity> ENCHANTING_TABLE = BlockEntityType.create((String)"enchanting_table", EnchantingTableBlockEntity::new, (Block[])new Block[]{Blocks.ENCHANTING_TABLE});
    public static final BlockEntityType<EndPortalBlockEntity> END_PORTAL = BlockEntityType.create((String)"end_portal", EndPortalBlockEntity::new, (Block[])new Block[]{Blocks.END_PORTAL});
    public static final BlockEntityType<BeaconBlockEntity> BEACON = BlockEntityType.create((String)"beacon", BeaconBlockEntity::new, (Block[])new Block[]{Blocks.BEACON});
    public static final BlockEntityType<SkullBlockEntity> SKULL = BlockEntityType.create((String)"skull", SkullBlockEntity::new, (Block[])new Block[]{Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD, Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD, Blocks.PIGLIN_HEAD, Blocks.PIGLIN_WALL_HEAD});
    public static final BlockEntityType<DaylightDetectorBlockEntity> DAYLIGHT_DETECTOR = BlockEntityType.create((String)"daylight_detector", DaylightDetectorBlockEntity::new, (Block[])new Block[]{Blocks.DAYLIGHT_DETECTOR});
    public static final BlockEntityType<HopperBlockEntity> HOPPER = BlockEntityType.create((String)"hopper", HopperBlockEntity::new, (Block[])new Block[]{Blocks.HOPPER});
    public static final BlockEntityType<ComparatorBlockEntity> COMPARATOR = BlockEntityType.create((String)"comparator", ComparatorBlockEntity::new, (Block[])new Block[]{Blocks.COMPARATOR});
    public static final BlockEntityType<BannerBlockEntity> BANNER = BlockEntityType.create((String)"banner", BannerBlockEntity::new, (Block[])new Block[]{Blocks.WHITE_BANNER, Blocks.ORANGE_BANNER, Blocks.MAGENTA_BANNER, Blocks.LIGHT_BLUE_BANNER, Blocks.YELLOW_BANNER, Blocks.LIME_BANNER, Blocks.PINK_BANNER, Blocks.GRAY_BANNER, Blocks.LIGHT_GRAY_BANNER, Blocks.CYAN_BANNER, Blocks.PURPLE_BANNER, Blocks.BLUE_BANNER, Blocks.BROWN_BANNER, Blocks.GREEN_BANNER, Blocks.RED_BANNER, Blocks.BLACK_BANNER, Blocks.WHITE_WALL_BANNER, Blocks.ORANGE_WALL_BANNER, Blocks.MAGENTA_WALL_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, Blocks.YELLOW_WALL_BANNER, Blocks.LIME_WALL_BANNER, Blocks.PINK_WALL_BANNER, Blocks.GRAY_WALL_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, Blocks.CYAN_WALL_BANNER, Blocks.PURPLE_WALL_BANNER, Blocks.BLUE_WALL_BANNER, Blocks.BROWN_WALL_BANNER, Blocks.GREEN_WALL_BANNER, Blocks.RED_WALL_BANNER, Blocks.BLACK_WALL_BANNER});
    public static final BlockEntityType<StructureBlockBlockEntity> STRUCTURE_BLOCK = BlockEntityType.create((String)"structure_block", StructureBlockBlockEntity::new, (Block[])new Block[]{Blocks.STRUCTURE_BLOCK});
    public static final BlockEntityType<EndGatewayBlockEntity> END_GATEWAY = BlockEntityType.create((String)"end_gateway", EndGatewayBlockEntity::new, (Block[])new Block[]{Blocks.END_GATEWAY});
    public static final BlockEntityType<CommandBlockBlockEntity> COMMAND_BLOCK = BlockEntityType.create((String)"command_block", CommandBlockBlockEntity::new, (Block[])new Block[]{Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK});
    public static final BlockEntityType<ShulkerBoxBlockEntity> SHULKER_BOX = BlockEntityType.create((String)"shulker_box", ShulkerBoxBlockEntity::new, (Block[])new Block[]{Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX});
    public static final BlockEntityType<BedBlockEntity> BED = BlockEntityType.create((String)"bed", BedBlockEntity::new, (Block[])new Block[]{Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED});
    public static final BlockEntityType<ConduitBlockEntity> CONDUIT = BlockEntityType.create((String)"conduit", ConduitBlockEntity::new, (Block[])new Block[]{Blocks.CONDUIT});
    public static final BlockEntityType<BarrelBlockEntity> BARREL = BlockEntityType.create((String)"barrel", BarrelBlockEntity::new, (Block[])new Block[]{Blocks.BARREL});
    public static final BlockEntityType<SmokerBlockEntity> SMOKER = BlockEntityType.create((String)"smoker", SmokerBlockEntity::new, (Block[])new Block[]{Blocks.SMOKER});
    public static final BlockEntityType<BlastFurnaceBlockEntity> BLAST_FURNACE = BlockEntityType.create((String)"blast_furnace", BlastFurnaceBlockEntity::new, (Block[])new Block[]{Blocks.BLAST_FURNACE});
    public static final BlockEntityType<LecternBlockEntity> LECTERN = BlockEntityType.create((String)"lectern", LecternBlockEntity::new, (Block[])new Block[]{Blocks.LECTERN});
    public static final BlockEntityType<BellBlockEntity> BELL = BlockEntityType.create((String)"bell", BellBlockEntity::new, (Block[])new Block[]{Blocks.BELL});
    public static final BlockEntityType<JigsawBlockEntity> JIGSAW = BlockEntityType.create((String)"jigsaw", JigsawBlockEntity::new, (Block[])new Block[]{Blocks.JIGSAW});
    public static final BlockEntityType<CampfireBlockEntity> CAMPFIRE = BlockEntityType.create((String)"campfire", CampfireBlockEntity::new, (Block[])new Block[]{Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE});
    public static final BlockEntityType<BeehiveBlockEntity> BEEHIVE = BlockEntityType.create((String)"beehive", BeehiveBlockEntity::new, (Block[])new Block[]{Blocks.BEE_NEST, Blocks.BEEHIVE});
    public static final BlockEntityType<SculkSensorBlockEntity> SCULK_SENSOR = BlockEntityType.create((String)"sculk_sensor", SculkSensorBlockEntity::new, (Block[])new Block[]{Blocks.SCULK_SENSOR});
    public static final BlockEntityType<CalibratedSculkSensorBlockEntity> CALIBRATED_SCULK_SENSOR = BlockEntityType.create((String)"calibrated_sculk_sensor", CalibratedSculkSensorBlockEntity::new, (Block[])new Block[]{Blocks.CALIBRATED_SCULK_SENSOR});
    public static final BlockEntityType<SculkCatalystBlockEntity> SCULK_CATALYST = BlockEntityType.create((String)"sculk_catalyst", SculkCatalystBlockEntity::new, (Block[])new Block[]{Blocks.SCULK_CATALYST});
    public static final BlockEntityType<SculkShriekerBlockEntity> SCULK_SHRIEKER = BlockEntityType.create((String)"sculk_shrieker", SculkShriekerBlockEntity::new, (Block[])new Block[]{Blocks.SCULK_SHRIEKER});
    public static final BlockEntityType<ChiseledBookshelfBlockEntity> CHISELED_BOOKSHELF = BlockEntityType.create((String)"chiseled_bookshelf", ChiseledBookshelfBlockEntity::new, (Block[])new Block[]{Blocks.CHISELED_BOOKSHELF});
    public static final BlockEntityType<ShelfBlockEntity> SHELF = BlockEntityType.create((String)"shelf", ShelfBlockEntity::new, (Block[])new Block[]{Blocks.ACACIA_SHELF, Blocks.BAMBOO_SHELF, Blocks.BIRCH_SHELF, Blocks.CHERRY_SHELF, Blocks.CRIMSON_SHELF, Blocks.DARK_OAK_SHELF, Blocks.JUNGLE_SHELF, Blocks.MANGROVE_SHELF, Blocks.OAK_SHELF, Blocks.PALE_OAK_SHELF, Blocks.SPRUCE_SHELF, Blocks.WARPED_SHELF});
    public static final BlockEntityType<BrushableBlockEntity> BRUSHABLE_BLOCK = BlockEntityType.create((String)"brushable_block", BrushableBlockEntity::new, (Block[])new Block[]{Blocks.SUSPICIOUS_SAND, Blocks.SUSPICIOUS_GRAVEL});
    public static final BlockEntityType<DecoratedPotBlockEntity> DECORATED_POT = BlockEntityType.create((String)"decorated_pot", DecoratedPotBlockEntity::new, (Block[])new Block[]{Blocks.DECORATED_POT});
    public static final BlockEntityType<CrafterBlockEntity> CRAFTER = BlockEntityType.create((String)"crafter", CrafterBlockEntity::new, (Block[])new Block[]{Blocks.CRAFTER});
    public static final BlockEntityType<TrialSpawnerBlockEntity> TRIAL_SPAWNER = BlockEntityType.create((String)"trial_spawner", TrialSpawnerBlockEntity::new, (Block[])new Block[]{Blocks.TRIAL_SPAWNER});
    public static final BlockEntityType<VaultBlockEntity> VAULT = BlockEntityType.create((String)"vault", VaultBlockEntity::new, (Block[])new Block[]{Blocks.VAULT});
    public static final BlockEntityType<TestBlockEntity> TEST_BLOCK = BlockEntityType.create((String)"test_block", TestBlockEntity::new, (Block[])new Block[]{Blocks.TEST_BLOCK});
    public static final BlockEntityType<TestInstanceBlockEntity> TEST_INSTANCE_BLOCK = BlockEntityType.create((String)"test_instance_block", TestInstanceBlockEntity::new, (Block[])new Block[]{Blocks.TEST_INSTANCE_BLOCK});
    public static final BlockEntityType<CopperGolemStatueBlockEntity> COPPER_GOLEM_STATUE = BlockEntityType.create((String)"copper_golem_statue", CopperGolemStatueBlockEntity::new, (Block[])new Block[]{Blocks.COPPER_GOLEM_STATUE, Blocks.EXPOSED_COPPER_GOLEM_STATUE, Blocks.WEATHERED_COPPER_GOLEM_STATUE, Blocks.OXIDIZED_COPPER_GOLEM_STATUE, Blocks.WAXED_COPPER_GOLEM_STATUE, Blocks.WAXED_EXPOSED_COPPER_GOLEM_STATUE, Blocks.WAXED_WEATHERED_COPPER_GOLEM_STATUE, Blocks.WAXED_OXIDIZED_COPPER_GOLEM_STATUE});
    private static final Set<BlockEntityType<?>> POTENTIALLY_EXECUTES_COMMANDS = Set.of(COMMAND_BLOCK, LECTERN, SIGN, HANGING_SIGN, MOB_SPAWNER, TRIAL_SPAWNER);
    private final BlockEntityFactory<? extends T> factory;
    private final Set<Block> blocks;
    private final RegistryEntry.Reference<BlockEntityType<?>> registryEntry = Registries.BLOCK_ENTITY_TYPE.createEntry((Object)this);

    public static @Nullable Identifier getId(BlockEntityType<?> type) {
        return Registries.BLOCK_ENTITY_TYPE.getId(type);
    }

    private static <T extends BlockEntity> BlockEntityType<T> create(String id, BlockEntityFactory<? extends T> factory, Block ... blocks) {
        if (blocks.length == 0) {
            LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", (Object)id);
        }
        Util.getChoiceType((DSL.TypeReference)TypeReferences.BLOCK_ENTITY, (String)id);
        return (BlockEntityType)Registry.register((Registry)Registries.BLOCK_ENTITY_TYPE, (String)id, (Object)new BlockEntityType(factory, Set.of(blocks)));
    }

    private BlockEntityType(BlockEntityFactory<? extends T> factory, Set<Block> blocks) {
        this.factory = factory;
        this.blocks = blocks;
    }

    public T instantiate(BlockPos pos, BlockState state) {
        return (T)this.factory.create(pos, state);
    }

    public boolean supports(BlockState state) {
        return this.blocks.contains(state.getBlock());
    }

    @Deprecated
    public RegistryEntry.Reference<BlockEntityType<?>> getRegistryEntry() {
        return this.registryEntry;
    }

    public @Nullable T get(BlockView world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null || blockEntity.getType() != this) {
            return null;
        }
        return (T)blockEntity;
    }

    public boolean canPotentiallyExecuteCommands() {
        return POTENTIALLY_EXECUTES_COMMANDS.contains(this);
    }
}

