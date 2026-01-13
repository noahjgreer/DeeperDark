/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.CampfireBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.CampfireBlockEntity
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCollisionHandler
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.ProjectileEntity
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.particle.SimpleParticleType
 *  net.minecraft.recipe.RecipePropertySet
 *  net.minecraft.recipe.RecipeType
 *  net.minecraft.recipe.ServerRecipeManager
 *  net.minecraft.recipe.ServerRecipeManager$MatchGetter
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.stat.Stats
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.Hand
 *  net.minecraft.util.function.BooleanBiFunction
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.recipe.RecipePropertySet;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class CampfireBlock
extends BlockWithEntity
implements Waterloggable {
    public static final MapCodec<CampfireBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.BOOL.fieldOf("spawn_particles").forGetter(block -> block.emitsParticles), (App)Codec.intRange((int)0, (int)1000).fieldOf("fire_damage").forGetter(block -> block.fireDamage), (App)CampfireBlock.createSettingsCodec()).apply((Applicative)instance, CampfireBlock::new));
    public static final BooleanProperty LIT = Properties.LIT;
    public static final BooleanProperty SIGNAL_FIRE = Properties.SIGNAL_FIRE;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Block.createColumnShape((double)16.0, (double)0.0, (double)7.0);
    private static final VoxelShape SMOKEY_SHAPE = Block.createColumnShape((double)4.0, (double)0.0, (double)16.0);
    private static final int field_31049 = 5;
    private final boolean emitsParticles;
    private final int fireDamage;

    public MapCodec<CampfireBlock> getCodec() {
        return CODEC;
    }

    public CampfireBlock(boolean emitsParticles, int fireDamage, AbstractBlock.Settings settings) {
        super(settings);
        this.emitsParticles = emitsParticles;
        this.fireDamage = fireDamage;
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)LIT, (Comparable)Boolean.valueOf(true))).with((Property)SIGNAL_FIRE, (Comparable)Boolean.valueOf(false))).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false))).with((Property)FACING, (Comparable)Direction.NORTH));
    }

    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CampfireBlockEntity) {
            CampfireBlockEntity campfireBlockEntity = (CampfireBlockEntity)blockEntity;
            ItemStack itemStack = player.getStackInHand(hand);
            if (world.getRecipeManager().getPropertySet(RecipePropertySet.CAMPFIRE_INPUT).canUse(itemStack)) {
                ServerWorld serverWorld;
                if (world instanceof ServerWorld && campfireBlockEntity.addItem(serverWorld = (ServerWorld)world, (LivingEntity)player, itemStack)) {
                    player.incrementStat(Stats.INTERACT_WITH_CAMPFIRE);
                    return ActionResult.SUCCESS_SERVER;
                }
                return ActionResult.CONSUME;
            }
        }
        return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
    }

    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
        if (((Boolean)state.get((Property)LIT)).booleanValue() && entity instanceof LivingEntity) {
            entity.serverDamage(world.getDamageSources().campfire(), (float)this.fireDamage);
        }
        super.onEntityCollision(state, world, pos, entity, handler, bl);
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos;
        World worldAccess = ctx.getWorld();
        boolean bl = worldAccess.getFluidState(blockPos = ctx.getBlockPos()).getFluid() == Fluids.WATER;
        return (BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(bl))).with((Property)SIGNAL_FIRE, (Comparable)Boolean.valueOf(this.isSignalFireBaseBlock(worldAccess.getBlockState(blockPos.down()))))).with((Property)LIT, (Comparable)Boolean.valueOf(!bl))).with((Property)FACING, (Comparable)ctx.getHorizontalPlayerFacing());
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (direction == Direction.DOWN) {
            return (BlockState)state.with((Property)SIGNAL_FIRE, (Comparable)Boolean.valueOf(this.isSignalFireBaseBlock(neighborState)));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    private boolean isSignalFireBaseBlock(BlockState state) {
        return state.isOf(Blocks.HAY_BLOCK);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!((Boolean)state.get((Property)LIT)).booleanValue()) {
            return;
        }
        if (random.nextInt(10) == 0) {
            world.playSoundClient((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 0.5f + random.nextFloat(), random.nextFloat() * 0.7f + 0.6f, false);
        }
        if (this.emitsParticles && random.nextInt(5) == 0) {
            for (int i = 0; i < random.nextInt(1) + 1; ++i) {
                world.addParticleClient((ParticleEffect)ParticleTypes.LAVA, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, (double)(random.nextFloat() / 2.0f), 5.0E-5, (double)(random.nextFloat() / 2.0f));
            }
        }
    }

    public static void extinguish(@Nullable Entity entity, WorldAccess world, BlockPos pos, BlockState state) {
        if (world.isClient()) {
            for (int i = 0; i < 20; ++i) {
                CampfireBlock.spawnSmokeParticle((World)((World)world), (BlockPos)pos, (boolean)((Boolean)state.get((Property)SIGNAL_FIRE)), (boolean)true);
            }
        }
        world.emitGameEvent(entity, (RegistryEntry)GameEvent.BLOCK_CHANGE, pos);
    }

    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!((Boolean)state.get((Property)Properties.WATERLOGGED)).booleanValue() && fluidState.getFluid() == Fluids.WATER) {
            boolean bl = (Boolean)state.get((Property)LIT);
            if (bl) {
                if (!world.isClient()) {
                    world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                }
                CampfireBlock.extinguish(null, (WorldAccess)world, (BlockPos)pos, (BlockState)state);
            }
            world.setBlockState(pos, (BlockState)((BlockState)state.with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(true))).with((Property)LIT, (Comparable)Boolean.valueOf(false)), 3);
            world.scheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate((WorldView)world));
            return true;
        }
        return false;
    }

    protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        BlockPos blockPos = hit.getBlockPos();
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            if (projectile.isOnFire() && projectile.canModifyAt(serverWorld, blockPos) && !((Boolean)state.get((Property)LIT)).booleanValue() && !((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
                world.setBlockState(blockPos, (BlockState)state.with((Property)Properties.LIT, (Comparable)Boolean.valueOf(true)), 11);
            }
        }
    }

    public static void spawnSmokeParticle(World world, BlockPos pos, boolean isSignal, boolean lotsOfSmoke) {
        Random random = world.getRandom();
        SimpleParticleType simpleParticleType = isSignal ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
        world.addImportantParticleClient((ParticleEffect)simpleParticleType, true, (double)pos.getX() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1), (double)pos.getY() + random.nextDouble() + random.nextDouble(), (double)pos.getZ() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1), 0.0, 0.07, 0.0);
        if (lotsOfSmoke) {
            world.addParticleClient((ParticleEffect)ParticleTypes.SMOKE, (double)pos.getX() + 0.5 + random.nextDouble() / 4.0 * (double)(random.nextBoolean() ? 1 : -1), (double)pos.getY() + 0.4, (double)pos.getZ() + 0.5 + random.nextDouble() / 4.0 * (double)(random.nextBoolean() ? 1 : -1), 0.0, 0.005, 0.0);
        }
    }

    public static boolean isLitCampfireInRange(World world, BlockPos pos) {
        for (int i = 1; i <= 5; ++i) {
            BlockPos blockPos = pos.down(i);
            BlockState blockState = world.getBlockState(blockPos);
            if (CampfireBlock.isLitCampfire((BlockState)blockState)) {
                return true;
            }
            boolean bl = VoxelShapes.matchesAnywhere((VoxelShape)SMOKEY_SHAPE, (VoxelShape)blockState.getCollisionShape((BlockView)world, pos, ShapeContext.absent()), (BooleanBiFunction)BooleanBiFunction.AND);
            if (!bl) continue;
            BlockState blockState2 = world.getBlockState(blockPos.down());
            return CampfireBlock.isLitCampfire((BlockState)blockState2);
        }
        return false;
    }

    public static boolean isLitCampfire(BlockState state) {
        return state.contains((Property)LIT) && state.isIn(BlockTags.CAMPFIRES) && (Boolean)state.get((Property)LIT) != false;
    }

    protected FluidState getFluidState(BlockState state) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get((Property)FACING)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{LIT, SIGNAL_FIRE, WATERLOGGED, FACING});
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CampfireBlockEntity(pos, state);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world2, BlockState state2, BlockEntityType<T> type) {
        if (world2 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world2;
            if (((Boolean)state2.get((Property)LIT)).booleanValue()) {
                ServerRecipeManager.MatchGetter matchGetter = ServerRecipeManager.createCachedMatchGetter((RecipeType)RecipeType.CAMPFIRE_COOKING);
                return CampfireBlock.validateTicker(type, (BlockEntityType)BlockEntityType.CAMPFIRE, (world, pos, state, blockEntity) -> CampfireBlockEntity.litServerTick((ServerWorld)serverWorld, (BlockPos)pos, (BlockState)state, (CampfireBlockEntity)blockEntity, (ServerRecipeManager.MatchGetter)matchGetter));
            }
            return CampfireBlock.validateTicker(type, (BlockEntityType)BlockEntityType.CAMPFIRE, CampfireBlockEntity::unlitServerTick);
        }
        if (((Boolean)state2.get((Property)LIT)).booleanValue()) {
            return CampfireBlock.validateTicker(type, (BlockEntityType)BlockEntityType.CAMPFIRE, CampfireBlockEntity::clientTick);
        }
        return null;
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    public static boolean canBeLit(BlockState state) {
        return state.isIn(BlockTags.CAMPFIRES, statex -> statex.contains((Property)WATERLOGGED) && statex.contains((Property)LIT)) && (Boolean)state.get((Property)WATERLOGGED) == false && (Boolean)state.get((Property)LIT) == false;
    }
}

