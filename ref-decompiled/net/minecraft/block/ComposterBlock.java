/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  it.unimi.dsi.fastutil.objects.Object2FloatMap
 *  it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.ComposterBlock
 *  net.minecraft.block.ComposterBlock$ComposterInventory
 *  net.minecraft.block.ComposterBlock$DummyInventory
 *  net.minecraft.block.ComposterBlock$FullComposterInventory
 *  net.minecraft.block.InventoryProvider
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.inventory.SidedInventory
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.stat.Stats
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.Hand
 *  net.minecraft.util.Util
 *  net.minecraft.util.function.BooleanBiFunction
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.event.GameEvent$Emitter
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class ComposterBlock
extends Block
implements InventoryProvider {
    public static final MapCodec<ComposterBlock> CODEC = ComposterBlock.createCodec(ComposterBlock::new);
    public static final int NUM_LEVELS = 8;
    public static final int MIN_LEVEL = 0;
    public static final int MAX_LEVEL = 7;
    public static final IntProperty LEVEL = Properties.LEVEL_8;
    public static final Object2FloatMap<ItemConvertible> ITEM_TO_LEVEL_INCREASE_CHANCE = new Object2FloatOpenHashMap();
    private static final int field_55750 = 12;
    private static final VoxelShape[] COLLISION_SHAPES_BY_LEVEL = (VoxelShape[])Util.make(() -> {
        VoxelShape[] voxelShapes = Block.createShapeArray((int)8, level -> VoxelShapes.combineAndSimplify((VoxelShape)VoxelShapes.fullCube(), (VoxelShape)Block.createColumnShape((double)12.0, (double)Math.clamp((long)(1 + level * 2), 2, 16), (double)16.0), (BooleanBiFunction)BooleanBiFunction.ONLY_FIRST));
        voxelShapes[8] = voxelShapes[7];
        return voxelShapes;
    });

    public MapCodec<ComposterBlock> getCodec() {
        return CODEC;
    }

    public static void registerDefaultCompostableItems() {
        ITEM_TO_LEVEL_INCREASE_CHANCE.defaultReturnValue(-1.0f);
        float f = 0.3f;
        float g = 0.5f;
        float h = 0.65f;
        float i = 0.85f;
        float j = 1.0f;
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.JUNGLE_LEAVES);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.OAK_LEAVES);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.SPRUCE_LEAVES);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.DARK_OAK_LEAVES);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.PALE_OAK_LEAVES);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.ACACIA_LEAVES);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.CHERRY_LEAVES);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.BIRCH_LEAVES);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.AZALEA_LEAVES);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.MANGROVE_LEAVES);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.OAK_SAPLING);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.SPRUCE_SAPLING);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.BIRCH_SAPLING);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.JUNGLE_SAPLING);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.ACACIA_SAPLING);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.CHERRY_SAPLING);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.DARK_OAK_SAPLING);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.PALE_OAK_SAPLING);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.MANGROVE_PROPAGULE);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.BEETROOT_SEEDS);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.DRIED_KELP);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.SHORT_GRASS);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.KELP);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.MELON_SEEDS);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.PUMPKIN_SEEDS);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.SEAGRASS);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.SWEET_BERRIES);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.GLOW_BERRIES);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.WHEAT_SEEDS);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.MOSS_CARPET);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.PALE_MOSS_CARPET);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.PALE_HANGING_MOSS);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.PINK_PETALS);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.WILDFLOWERS);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.LEAF_LITTER);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.SMALL_DRIPLEAF);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.HANGING_ROOTS);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.MANGROVE_ROOTS);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.TORCHFLOWER_SEEDS);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.PITCHER_POD);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.FIREFLY_BUSH);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.BUSH);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.CACTUS_FLOWER);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.SHORT_DRY_GRASS);
        ComposterBlock.registerCompostableItem((float)0.3f, (ItemConvertible)Items.TALL_DRY_GRASS);
        ComposterBlock.registerCompostableItem((float)0.5f, (ItemConvertible)Items.DRIED_KELP_BLOCK);
        ComposterBlock.registerCompostableItem((float)0.5f, (ItemConvertible)Items.TALL_GRASS);
        ComposterBlock.registerCompostableItem((float)0.5f, (ItemConvertible)Items.FLOWERING_AZALEA_LEAVES);
        ComposterBlock.registerCompostableItem((float)0.5f, (ItemConvertible)Items.CACTUS);
        ComposterBlock.registerCompostableItem((float)0.5f, (ItemConvertible)Items.SUGAR_CANE);
        ComposterBlock.registerCompostableItem((float)0.5f, (ItemConvertible)Items.VINE);
        ComposterBlock.registerCompostableItem((float)0.5f, (ItemConvertible)Items.NETHER_SPROUTS);
        ComposterBlock.registerCompostableItem((float)0.5f, (ItemConvertible)Items.WEEPING_VINES);
        ComposterBlock.registerCompostableItem((float)0.5f, (ItemConvertible)Items.TWISTING_VINES);
        ComposterBlock.registerCompostableItem((float)0.5f, (ItemConvertible)Items.MELON_SLICE);
        ComposterBlock.registerCompostableItem((float)0.5f, (ItemConvertible)Items.GLOW_LICHEN);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.SEA_PICKLE);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.LILY_PAD);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.PUMPKIN);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.CARVED_PUMPKIN);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.MELON);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.APPLE);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.BEETROOT);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.CARROT);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.COCOA_BEANS);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.POTATO);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.WHEAT);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.BROWN_MUSHROOM);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.RED_MUSHROOM);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.MUSHROOM_STEM);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.CRIMSON_FUNGUS);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.WARPED_FUNGUS);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.NETHER_WART);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.CRIMSON_ROOTS);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.WARPED_ROOTS);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.SHROOMLIGHT);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.DANDELION);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.POPPY);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.BLUE_ORCHID);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.ALLIUM);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.AZURE_BLUET);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.RED_TULIP);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.ORANGE_TULIP);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.WHITE_TULIP);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.PINK_TULIP);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.OXEYE_DAISY);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.CORNFLOWER);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.LILY_OF_THE_VALLEY);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.WITHER_ROSE);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.OPEN_EYEBLOSSOM);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.CLOSED_EYEBLOSSOM);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.FERN);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.SUNFLOWER);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.LILAC);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.ROSE_BUSH);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.PEONY);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.LARGE_FERN);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.SPORE_BLOSSOM);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.AZALEA);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.MOSS_BLOCK);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.PALE_MOSS_BLOCK);
        ComposterBlock.registerCompostableItem((float)0.65f, (ItemConvertible)Items.BIG_DRIPLEAF);
        ComposterBlock.registerCompostableItem((float)0.85f, (ItemConvertible)Items.HAY_BLOCK);
        ComposterBlock.registerCompostableItem((float)0.85f, (ItemConvertible)Items.BROWN_MUSHROOM_BLOCK);
        ComposterBlock.registerCompostableItem((float)0.85f, (ItemConvertible)Items.RED_MUSHROOM_BLOCK);
        ComposterBlock.registerCompostableItem((float)0.85f, (ItemConvertible)Items.NETHER_WART_BLOCK);
        ComposterBlock.registerCompostableItem((float)0.85f, (ItemConvertible)Items.WARPED_WART_BLOCK);
        ComposterBlock.registerCompostableItem((float)0.85f, (ItemConvertible)Items.FLOWERING_AZALEA);
        ComposterBlock.registerCompostableItem((float)0.85f, (ItemConvertible)Items.BREAD);
        ComposterBlock.registerCompostableItem((float)0.85f, (ItemConvertible)Items.BAKED_POTATO);
        ComposterBlock.registerCompostableItem((float)0.85f, (ItemConvertible)Items.COOKIE);
        ComposterBlock.registerCompostableItem((float)0.85f, (ItemConvertible)Items.TORCHFLOWER);
        ComposterBlock.registerCompostableItem((float)0.85f, (ItemConvertible)Items.PITCHER_PLANT);
        ComposterBlock.registerCompostableItem((float)1.0f, (ItemConvertible)Items.CAKE);
        ComposterBlock.registerCompostableItem((float)1.0f, (ItemConvertible)Items.PUMPKIN_PIE);
    }

    private static void registerCompostableItem(float levelIncreaseChance, ItemConvertible item) {
        ITEM_TO_LEVEL_INCREASE_CHANCE.put((Object)item.asItem(), levelIncreaseChance);
    }

    public ComposterBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)LEVEL, (Comparable)Integer.valueOf(0)));
    }

    public static void playEffects(World world, BlockPos pos, boolean fill) {
        BlockState blockState = world.getBlockState(pos);
        world.playSoundAtBlockCenterClient(pos, fill ? SoundEvents.BLOCK_COMPOSTER_FILL_SUCCESS : SoundEvents.BLOCK_COMPOSTER_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
        double d = blockState.getOutlineShape((BlockView)world, pos).getEndingCoord(Direction.Axis.Y, 0.5, 0.5) + 0.03125;
        double e = 2.0;
        double f = 0.1875;
        double g = 0.625;
        Random random = world.getRandom();
        for (int i = 0; i < 10; ++i) {
            double h = random.nextGaussian() * 0.02;
            double j = random.nextGaussian() * 0.02;
            double k = random.nextGaussian() * 0.02;
            world.addParticleClient((ParticleEffect)ParticleTypes.COMPOSTER, (double)pos.getX() + 0.1875 + 0.625 * (double)random.nextFloat(), (double)pos.getY() + d + (double)random.nextFloat() * (1.0 - d), (double)pos.getZ() + 0.1875 + 0.625 * (double)random.nextFloat(), h, j, k);
        }
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPES_BY_LEVEL[(Integer)state.get((Property)LEVEL)];
    }

    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.fullCube();
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPES_BY_LEVEL[0];
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if ((Integer)state.get((Property)LEVEL) == 7) {
            world.scheduleBlockTick(pos, state.getBlock(), 20);
        }
    }

    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int i = (Integer)state.get((Property)LEVEL);
        if (i < 8 && ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey((Object)stack.getItem())) {
            if (i < 7 && !world.isClient()) {
                BlockState blockState = ComposterBlock.addToComposter((Entity)player, (BlockState)state, (WorldAccess)world, (BlockPos)pos, (ItemStack)stack);
                world.syncWorldEvent(1500, pos, state != blockState ? 1 : 0);
                player.incrementStat(Stats.USED.getOrCreateStat((Object)stack.getItem()));
                stack.decrementUnlessCreative(1, (LivingEntity)player);
            }
            return ActionResult.SUCCESS;
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        int i = (Integer)state.get((Property)LEVEL);
        if (i == 8) {
            ComposterBlock.emptyFullComposter((Entity)player, (BlockState)state, (World)world, (BlockPos)pos);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public static BlockState compost(Entity user, BlockState state, ServerWorld world, ItemStack stack, BlockPos pos) {
        int i = (Integer)state.get((Property)LEVEL);
        if (i < 7 && ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey((Object)stack.getItem())) {
            BlockState blockState = ComposterBlock.addToComposter((Entity)user, (BlockState)state, (WorldAccess)world, (BlockPos)pos, (ItemStack)stack);
            stack.decrement(1);
            return blockState;
        }
        return state;
    }

    public static BlockState emptyFullComposter(Entity user, BlockState state, World world, BlockPos pos) {
        if (!world.isClient()) {
            Vec3d vec3d = Vec3d.add((Vec3i)pos, (double)0.5, (double)1.01, (double)0.5).addHorizontalRandom(world.random, 0.7f);
            ItemEntity itemEntity = new ItemEntity(world, vec3d.getX(), vec3d.getY(), vec3d.getZ(), new ItemStack((ItemConvertible)Items.BONE_MEAL));
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity((Entity)itemEntity);
        }
        BlockState blockState = ComposterBlock.emptyComposter((Entity)user, (BlockState)state, (WorldAccess)world, (BlockPos)pos);
        world.playSound(null, pos, SoundEvents.BLOCK_COMPOSTER_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
        return blockState;
    }

    static BlockState emptyComposter(@Nullable Entity user, BlockState state, WorldAccess world, BlockPos pos) {
        BlockState blockState = (BlockState)state.with((Property)LEVEL, (Comparable)Integer.valueOf(0));
        world.setBlockState(pos, blockState, 3);
        world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of((Entity)user, (BlockState)blockState));
        return blockState;
    }

    static BlockState addToComposter(@Nullable Entity user, BlockState state, WorldAccess world, BlockPos pos, ItemStack stack) {
        int i = (Integer)state.get((Property)LEVEL);
        float f = ITEM_TO_LEVEL_INCREASE_CHANCE.getFloat((Object)stack.getItem());
        if (i == 0 && f > 0.0f || world.getRandom().nextDouble() < (double)f) {
            int j = i + 1;
            BlockState blockState = (BlockState)state.with((Property)LEVEL, (Comparable)Integer.valueOf(j));
            world.setBlockState(pos, blockState, 3);
            world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of((Entity)user, (BlockState)blockState));
            if (j == 7) {
                world.scheduleBlockTick(pos, state.getBlock(), 20);
            }
            return blockState;
        }
        return state;
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if ((Integer)state.get((Property)LEVEL) == 7) {
            world.setBlockState(pos, (BlockState)state.cycle((Property)LEVEL), 3);
            world.playSound(null, pos, SoundEvents.BLOCK_COMPOSTER_READY, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        return (Integer)state.get((Property)LEVEL);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{LEVEL});
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
        int i = (Integer)state.get((Property)LEVEL);
        if (i == 8) {
            return new FullComposterInventory(state, world, pos, new ItemStack((ItemConvertible)Items.BONE_MEAL));
        }
        if (i < 7) {
            return new ComposterInventory(state, world, pos);
        }
        return new DummyInventory();
    }
}

