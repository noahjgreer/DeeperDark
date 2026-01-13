/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractCauldronBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Falling
 *  net.minecraft.block.PointedDripstoneBlock
 *  net.minecraft.block.PointedDripstoneBlock$1
 *  net.minecraft.block.PointedDripstoneBlock$DrippingFluid
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.block.enums.Thickness
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.FallingBlockEntity
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.damage.DamageSource
 *  net.minecraft.entity.projectile.ProjectileEntity
 *  net.minecraft.entity.projectile.TridentEntity
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.FluidTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.function.BooleanBiFunction
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$Mutable
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.math.Direction$AxisDirection
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.attribute.EnvironmentAttributes
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.event.GameEvent$Emitter
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Falling;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.Thickness;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class PointedDripstoneBlock
extends Block
implements Falling,
Waterloggable {
    public static final MapCodec<PointedDripstoneBlock> CODEC = PointedDripstoneBlock.createCodec(PointedDripstoneBlock::new);
    public static final EnumProperty<Direction> VERTICAL_DIRECTION = Properties.VERTICAL_DIRECTION;
    public static final EnumProperty<Thickness> THICKNESS = Properties.THICKNESS;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final int field_31205 = 11;
    private static final int field_31207 = 2;
    private static final float field_31208 = 0.02f;
    private static final float field_31209 = 0.12f;
    private static final int field_31210 = 11;
    private static final float WATER_DRIP_CHANCE = 0.17578125f;
    private static final float LAVA_DRIP_CHANCE = 0.05859375f;
    private static final double field_31213 = 0.6;
    private static final float field_31214 = 1.0f;
    private static final int field_31215 = 40;
    private static final int field_31200 = 6;
    private static final float field_31201 = 2.5f;
    private static final int field_31202 = 2;
    private static final float field_33566 = 5.0f;
    private static final float field_33567 = 0.011377778f;
    private static final int MAX_STALACTITE_GROWTH = 7;
    private static final int STALACTITE_FLOOR_SEARCH_RANGE = 10;
    private static final VoxelShape TIP_MERGE_SHAPE = Block.createColumnShape((double)6.0, (double)0.0, (double)16.0);
    private static final VoxelShape UP_TIP_SHAPE = Block.createColumnShape((double)6.0, (double)0.0, (double)11.0);
    private static final VoxelShape DOWN_TIP_SHAPE = Block.createColumnShape((double)6.0, (double)5.0, (double)16.0);
    private static final VoxelShape FRUSTUM_SHAPE = Block.createColumnShape((double)8.0, (double)0.0, (double)16.0);
    private static final VoxelShape MIDDLE_SHAPE = Block.createColumnShape((double)10.0, (double)0.0, (double)16.0);
    private static final VoxelShape BASE_SHAPE = Block.createColumnShape((double)12.0, (double)0.0, (double)16.0);
    private static final double DOWN_TIP_Y = DOWN_TIP_SHAPE.getMin(Direction.Axis.Y);
    private static final float MAX_HORIZONTAL_MODEL_OFFSET = (float)BASE_SHAPE.getMin(Direction.Axis.X);
    private static final VoxelShape DRIP_COLLISION_SHAPE = Block.createColumnShape((double)4.0, (double)0.0, (double)16.0);

    public MapCodec<PointedDripstoneBlock> getCodec() {
        return CODEC;
    }

    public PointedDripstoneBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)VERTICAL_DIRECTION, (Comparable)Direction.UP)).with((Property)THICKNESS, (Comparable)Thickness.TIP)).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{VERTICAL_DIRECTION, THICKNESS, WATERLOGGED});
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return PointedDripstoneBlock.canPlaceAtWithDirection((WorldView)world, (BlockPos)pos, (Direction)((Direction)state.get((Property)VERTICAL_DIRECTION)));
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (direction != Direction.UP && direction != Direction.DOWN) {
            return state;
        }
        Direction direction2 = (Direction)state.get((Property)VERTICAL_DIRECTION);
        if (direction2 == Direction.DOWN && tickView.getBlockTickScheduler().isQueued(pos, (Object)this)) {
            return state;
        }
        if (direction == direction2.getOpposite() && !this.canPlaceAt(state, world, pos)) {
            if (direction2 == Direction.DOWN) {
                tickView.scheduleBlockTick(pos, (Block)this, 2);
            } else {
                tickView.scheduleBlockTick(pos, (Block)this, 1);
            }
            return state;
        }
        boolean bl = state.get((Property)THICKNESS) == Thickness.TIP_MERGE;
        Thickness thickness = PointedDripstoneBlock.getThickness((WorldView)world, (BlockPos)pos, (Direction)direction2, (boolean)bl);
        return (BlockState)state.with((Property)THICKNESS, (Comparable)thickness);
    }

    protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        ServerWorld serverWorld;
        if (world.isClient()) {
            return;
        }
        BlockPos blockPos = hit.getBlockPos();
        if (world instanceof ServerWorld && projectile.canModifyAt(serverWorld = (ServerWorld)world, blockPos) && projectile.canBreakBlocks(serverWorld) && projectile instanceof TridentEntity && projectile.getVelocity().length() > 0.6) {
            world.breakBlock(blockPos, true);
        }
    }

    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        if (state.get((Property)VERTICAL_DIRECTION) == Direction.UP && state.get((Property)THICKNESS) == Thickness.TIP) {
            entity.handleFallDamage(fallDistance + 2.5, 2.0f, world.getDamageSources().stalagmite());
        } else {
            super.onLandedUpon(world, state, pos, entity, fallDistance);
        }
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!PointedDripstoneBlock.canDrip((BlockState)state)) {
            return;
        }
        float f = random.nextFloat();
        if (f > 0.12f) {
            return;
        }
        PointedDripstoneBlock.getFluid((World)world, (BlockPos)pos, (BlockState)state).filter(fluid -> f < 0.02f || PointedDripstoneBlock.isFluidLiquid((Fluid)fluid.fluid)).ifPresent(fluid -> PointedDripstoneBlock.createParticle((World)world, (BlockPos)pos, (BlockState)state, (Fluid)fluid.fluid, (BlockPos)fluid.pos));
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (PointedDripstoneBlock.isPointingUp((BlockState)state) && !this.canPlaceAt(state, (WorldView)world, pos)) {
            world.breakBlock(pos, true);
        } else {
            PointedDripstoneBlock.spawnFallingBlock((BlockState)state, (ServerWorld)world, (BlockPos)pos);
        }
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        PointedDripstoneBlock.dripTick((BlockState)state, (ServerWorld)world, (BlockPos)pos, (float)random.nextFloat());
        if (random.nextFloat() < 0.011377778f && PointedDripstoneBlock.isHeldByPointedDripstone((BlockState)state, (WorldView)world, (BlockPos)pos)) {
            PointedDripstoneBlock.tryGrow((BlockState)state, (ServerWorld)world, (BlockPos)pos, (Random)random);
        }
    }

    @VisibleForTesting
    public static void dripTick(BlockState state, ServerWorld world, BlockPos pos, float dripChance) {
        float f;
        if (dripChance > 0.17578125f && dripChance > 0.05859375f) {
            return;
        }
        if (!PointedDripstoneBlock.isHeldByPointedDripstone((BlockState)state, (WorldView)world, (BlockPos)pos)) {
            return;
        }
        Optional optional = PointedDripstoneBlock.getFluid((World)world, (BlockPos)pos, (BlockState)state);
        if (optional.isEmpty()) {
            return;
        }
        Fluid fluid = ((DrippingFluid)optional.get()).fluid;
        if (fluid == Fluids.WATER) {
            f = 0.17578125f;
        } else if (fluid == Fluids.LAVA) {
            f = 0.05859375f;
        } else {
            return;
        }
        if (dripChance >= f) {
            return;
        }
        BlockPos blockPos = PointedDripstoneBlock.getTipPos((BlockState)state, (WorldAccess)world, (BlockPos)pos, (int)11, (boolean)false);
        if (blockPos == null) {
            return;
        }
        if (((DrippingFluid)optional.get()).sourceState.isOf(Blocks.MUD) && fluid == Fluids.WATER) {
            BlockState blockState = Blocks.CLAY.getDefaultState();
            world.setBlockState(((DrippingFluid)optional.get()).pos, blockState);
            Block.pushEntitiesUpBeforeBlockChange((BlockState)((DrippingFluid)optional.get()).sourceState, (BlockState)blockState, (WorldAccess)world, (BlockPos)((DrippingFluid)optional.get()).pos);
            world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_CHANGE, ((DrippingFluid)optional.get()).pos, GameEvent.Emitter.of((BlockState)blockState));
            world.syncWorldEvent(1504, blockPos, 0);
            return;
        }
        BlockPos blockPos2 = PointedDripstoneBlock.getCauldronPos((World)world, (BlockPos)blockPos, (Fluid)fluid);
        if (blockPos2 == null) {
            return;
        }
        world.syncWorldEvent(1504, blockPos, 0);
        int i = blockPos.getY() - blockPos2.getY();
        int j = 50 + i;
        BlockState blockState2 = world.getBlockState(blockPos2);
        world.scheduleBlockTick(blockPos2, blockState2.getBlock(), j);
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction;
        BlockPos blockPos;
        World worldAccess = ctx.getWorld();
        Direction direction2 = PointedDripstoneBlock.getDirectionToPlaceAt((WorldView)worldAccess, (BlockPos)(blockPos = ctx.getBlockPos()), (Direction)(direction = ctx.getVerticalPlayerLookDirection().getOpposite()));
        if (direction2 == null) {
            return null;
        }
        boolean bl = !ctx.shouldCancelInteraction();
        Thickness thickness = PointedDripstoneBlock.getThickness((WorldView)worldAccess, (BlockPos)blockPos, (Direction)direction2, (boolean)bl);
        return (BlockState)((BlockState)((BlockState)this.getDefaultState().with((Property)VERTICAL_DIRECTION, (Comparable)direction2)).with((Property)THICKNESS, (Comparable)thickness)).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(worldAccess.getFluidState(blockPos).getFluid() == Fluids.WATER));
    }

    protected FluidState getFluidState(BlockState state) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape voxelShape = switch (1.field_55786[((Thickness)state.get((Property)THICKNESS)).ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> TIP_MERGE_SHAPE;
            case 2 -> {
                if (state.get((Property)VERTICAL_DIRECTION) == Direction.DOWN) {
                    yield DOWN_TIP_SHAPE;
                }
                yield UP_TIP_SHAPE;
            }
            case 3 -> FRUSTUM_SHAPE;
            case 4 -> MIDDLE_SHAPE;
            case 5 -> BASE_SHAPE;
        };
        return voxelShape.offset(state.getModelOffset(pos));
    }

    protected boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    protected float getMaxHorizontalModelOffset() {
        return MAX_HORIZONTAL_MODEL_OFFSET;
    }

    public void onDestroyedOnLanding(World world, BlockPos pos, FallingBlockEntity fallingBlockEntity) {
        if (!fallingBlockEntity.isSilent()) {
            world.syncWorldEvent(1045, pos, 0);
        }
    }

    public DamageSource getDamageSource(Entity attacker) {
        return attacker.getDamageSources().fallingStalactite(attacker);
    }

    private static void spawnFallingBlock(BlockState state, ServerWorld world, BlockPos pos) {
        BlockPos.Mutable mutable = pos.mutableCopy();
        BlockState blockState = state;
        while (PointedDripstoneBlock.isPointingDown((BlockState)blockState)) {
            FallingBlockEntity fallingBlockEntity = FallingBlockEntity.spawnFromBlock((World)world, (BlockPos)mutable, (BlockState)blockState);
            if (PointedDripstoneBlock.isTip((BlockState)blockState, (boolean)true)) {
                int i = Math.max(1 + pos.getY() - mutable.getY(), 6);
                float f = 1.0f * (float)i;
                fallingBlockEntity.setHurtEntities(f, 40);
                break;
            }
            mutable.move(Direction.DOWN);
            blockState = world.getBlockState((BlockPos)mutable);
        }
    }

    @VisibleForTesting
    public static void tryGrow(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockState blockState2;
        BlockState blockState = world.getBlockState(pos.up(1));
        if (!PointedDripstoneBlock.canGrow((BlockState)blockState, (BlockState)(blockState2 = world.getBlockState(pos.up(2))))) {
            return;
        }
        BlockPos blockPos = PointedDripstoneBlock.getTipPos((BlockState)state, (WorldAccess)world, (BlockPos)pos, (int)7, (boolean)false);
        if (blockPos == null) {
            return;
        }
        BlockState blockState3 = world.getBlockState(blockPos);
        if (!PointedDripstoneBlock.canDrip((BlockState)blockState3) || !PointedDripstoneBlock.canGrow((BlockState)blockState3, (ServerWorld)world, (BlockPos)blockPos)) {
            return;
        }
        if (random.nextBoolean()) {
            PointedDripstoneBlock.tryGrow((ServerWorld)world, (BlockPos)blockPos, (Direction)Direction.DOWN);
        } else {
            PointedDripstoneBlock.tryGrowStalagmite((ServerWorld)world, (BlockPos)blockPos);
        }
    }

    private static void tryGrowStalagmite(ServerWorld world, BlockPos pos) {
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (int i = 0; i < 10; ++i) {
            mutable.move(Direction.DOWN);
            BlockState blockState = world.getBlockState((BlockPos)mutable);
            if (!blockState.getFluidState().isEmpty()) {
                return;
            }
            if (PointedDripstoneBlock.isTip((BlockState)blockState, (Direction)Direction.UP) && PointedDripstoneBlock.canGrow((BlockState)blockState, (ServerWorld)world, (BlockPos)mutable)) {
                PointedDripstoneBlock.tryGrow((ServerWorld)world, (BlockPos)mutable, (Direction)Direction.UP);
                return;
            }
            if (PointedDripstoneBlock.canPlaceAtWithDirection((WorldView)world, (BlockPos)mutable, (Direction)Direction.UP) && !world.isWater(mutable.down())) {
                PointedDripstoneBlock.tryGrow((ServerWorld)world, (BlockPos)mutable.down(), (Direction)Direction.UP);
                return;
            }
            if (PointedDripstoneBlock.canDripThrough((BlockView)world, (BlockPos)mutable, (BlockState)blockState)) continue;
            return;
        }
    }

    private static void tryGrow(ServerWorld world, BlockPos pos, Direction direction) {
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        if (PointedDripstoneBlock.isTip((BlockState)blockState, (Direction)direction.getOpposite())) {
            PointedDripstoneBlock.growMerged((BlockState)blockState, (WorldAccess)world, (BlockPos)blockPos);
        } else if (blockState.isAir() || blockState.isOf(Blocks.WATER)) {
            PointedDripstoneBlock.place((WorldAccess)world, (BlockPos)blockPos, (Direction)direction, (Thickness)Thickness.TIP);
        }
    }

    private static void place(WorldAccess world, BlockPos pos, Direction direction, Thickness thickness) {
        BlockState blockState = (BlockState)((BlockState)((BlockState)Blocks.POINTED_DRIPSTONE.getDefaultState().with((Property)VERTICAL_DIRECTION, (Comparable)direction)).with((Property)THICKNESS, (Comparable)thickness)).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(world.getFluidState(pos).getFluid() == Fluids.WATER));
        world.setBlockState(pos, blockState, 3);
    }

    private static void growMerged(BlockState state, WorldAccess world, BlockPos pos) {
        BlockPos blockPos2;
        BlockPos blockPos;
        if (state.get((Property)VERTICAL_DIRECTION) == Direction.UP) {
            blockPos = pos;
            blockPos2 = pos.up();
        } else {
            blockPos2 = pos;
            blockPos = pos.down();
        }
        PointedDripstoneBlock.place((WorldAccess)world, (BlockPos)blockPos2, (Direction)Direction.DOWN, (Thickness)Thickness.TIP_MERGE);
        PointedDripstoneBlock.place((WorldAccess)world, (BlockPos)blockPos, (Direction)Direction.UP, (Thickness)Thickness.TIP_MERGE);
    }

    public static void createParticle(World world, BlockPos pos, BlockState state) {
        PointedDripstoneBlock.getFluid((World)world, (BlockPos)pos, (BlockState)state).ifPresent(fluid -> PointedDripstoneBlock.createParticle((World)world, (BlockPos)pos, (BlockState)state, (Fluid)fluid.fluid, (BlockPos)fluid.pos));
    }

    private static void createParticle(World world, BlockPos pos, BlockState state, Fluid fluid, BlockPos fluidPos) {
        Vec3d vec3d = state.getModelOffset(pos);
        double d = 0.0625;
        double e = (double)pos.getX() + 0.5 + vec3d.x;
        double f = (double)pos.getY() + DOWN_TIP_Y - 0.0625;
        double g = (double)pos.getZ() + 0.5 + vec3d.z;
        ParticleEffect particleEffect = PointedDripstoneBlock.getParticleEffect((World)world, (Fluid)fluid, (BlockPos)fluidPos);
        world.addParticleClient(particleEffect, e, f, g, 0.0, 0.0, 0.0);
    }

    private static @Nullable BlockPos getTipPos(BlockState state, WorldAccess world, BlockPos pos, int range, boolean allowMerged) {
        if (PointedDripstoneBlock.isTip((BlockState)state, (boolean)allowMerged)) {
            return pos;
        }
        Direction direction = (Direction)state.get((Property)VERTICAL_DIRECTION);
        BiPredicate<BlockPos, BlockState> biPredicate = (posx, statex) -> statex.isOf(Blocks.POINTED_DRIPSTONE) && statex.get((Property)VERTICAL_DIRECTION) == direction;
        return PointedDripstoneBlock.searchInDirection((WorldAccess)world, (BlockPos)pos, (Direction.AxisDirection)direction.getDirection(), biPredicate, (T statex) -> PointedDripstoneBlock.isTip((BlockState)statex, (boolean)allowMerged), (int)range).orElse(null);
    }

    private static @Nullable Direction getDirectionToPlaceAt(WorldView world, BlockPos pos, Direction direction) {
        Direction direction2;
        if (PointedDripstoneBlock.canPlaceAtWithDirection((WorldView)world, (BlockPos)pos, (Direction)direction)) {
            direction2 = direction;
        } else if (PointedDripstoneBlock.canPlaceAtWithDirection((WorldView)world, (BlockPos)pos, (Direction)direction.getOpposite())) {
            direction2 = direction.getOpposite();
        } else {
            return null;
        }
        return direction2;
    }

    private static Thickness getThickness(WorldView world, BlockPos pos, Direction direction, boolean tryMerge) {
        Direction direction2 = direction.getOpposite();
        BlockState blockState = world.getBlockState(pos.offset(direction));
        if (PointedDripstoneBlock.isPointedDripstoneFacingDirection((BlockState)blockState, (Direction)direction2)) {
            if (tryMerge || blockState.get((Property)THICKNESS) == Thickness.TIP_MERGE) {
                return Thickness.TIP_MERGE;
            }
            return Thickness.TIP;
        }
        if (!PointedDripstoneBlock.isPointedDripstoneFacingDirection((BlockState)blockState, (Direction)direction)) {
            return Thickness.TIP;
        }
        Thickness thickness = (Thickness)blockState.get((Property)THICKNESS);
        if (thickness == Thickness.TIP || thickness == Thickness.TIP_MERGE) {
            return Thickness.FRUSTUM;
        }
        BlockState blockState2 = world.getBlockState(pos.offset(direction2));
        if (!PointedDripstoneBlock.isPointedDripstoneFacingDirection((BlockState)blockState2, (Direction)direction)) {
            return Thickness.BASE;
        }
        return Thickness.MIDDLE;
    }

    public static boolean canDrip(BlockState state) {
        return PointedDripstoneBlock.isPointingDown((BlockState)state) && state.get((Property)THICKNESS) == Thickness.TIP && (Boolean)state.get((Property)WATERLOGGED) == false;
    }

    private static boolean canGrow(BlockState state, ServerWorld world, BlockPos pos) {
        Direction direction = (Direction)state.get((Property)VERTICAL_DIRECTION);
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        if (!blockState.getFluidState().isEmpty()) {
            return false;
        }
        if (blockState.isAir()) {
            return true;
        }
        return PointedDripstoneBlock.isTip((BlockState)blockState, (Direction)direction.getOpposite());
    }

    private static Optional<BlockPos> getSupportingPos(World world, BlockPos pos, BlockState state, int range) {
        Direction direction = (Direction)state.get((Property)VERTICAL_DIRECTION);
        BiPredicate<BlockPos, BlockState> biPredicate = (posx, statex) -> statex.isOf(Blocks.POINTED_DRIPSTONE) && statex.get((Property)VERTICAL_DIRECTION) == direction;
        return PointedDripstoneBlock.searchInDirection((WorldAccess)world, (BlockPos)pos, (Direction.AxisDirection)direction.getOpposite().getDirection(), biPredicate, (T statex) -> !statex.isOf(Blocks.POINTED_DRIPSTONE), (int)range);
    }

    private static boolean canPlaceAtWithDirection(WorldView world, BlockPos pos, Direction direction) {
        BlockPos blockPos = pos.offset(direction.getOpposite());
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.isSideSolidFullSquare((BlockView)world, blockPos, direction) || PointedDripstoneBlock.isPointedDripstoneFacingDirection((BlockState)blockState, (Direction)direction);
    }

    private static boolean isTip(BlockState state, boolean allowMerged) {
        if (!state.isOf(Blocks.POINTED_DRIPSTONE)) {
            return false;
        }
        Thickness thickness = (Thickness)state.get((Property)THICKNESS);
        return thickness == Thickness.TIP || allowMerged && thickness == Thickness.TIP_MERGE;
    }

    private static boolean isTip(BlockState state, Direction direction) {
        return PointedDripstoneBlock.isTip((BlockState)state, (boolean)false) && state.get((Property)VERTICAL_DIRECTION) == direction;
    }

    private static boolean isPointingDown(BlockState state) {
        return PointedDripstoneBlock.isPointedDripstoneFacingDirection((BlockState)state, (Direction)Direction.DOWN);
    }

    private static boolean isPointingUp(BlockState state) {
        return PointedDripstoneBlock.isPointedDripstoneFacingDirection((BlockState)state, (Direction)Direction.UP);
    }

    private static boolean isHeldByPointedDripstone(BlockState state, WorldView world, BlockPos pos) {
        return PointedDripstoneBlock.isPointingDown((BlockState)state) && !world.getBlockState(pos.up()).isOf(Blocks.POINTED_DRIPSTONE);
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    private static boolean isPointedDripstoneFacingDirection(BlockState state, Direction direction) {
        return state.isOf(Blocks.POINTED_DRIPSTONE) && state.get((Property)VERTICAL_DIRECTION) == direction;
    }

    private static @Nullable BlockPos getCauldronPos(World world, BlockPos pos, Fluid fluid) {
        Predicate<BlockState> predicate = state -> state.getBlock() instanceof AbstractCauldronBlock && ((AbstractCauldronBlock)state.getBlock()).canBeFilledByDripstone(fluid);
        BiPredicate<BlockPos, BlockState> biPredicate = (posx, state) -> PointedDripstoneBlock.canDripThrough((BlockView)world, (BlockPos)posx, (BlockState)state);
        return PointedDripstoneBlock.searchInDirection((WorldAccess)world, (BlockPos)pos, (Direction.AxisDirection)Direction.DOWN.getDirection(), biPredicate, predicate, (int)11).orElse(null);
    }

    public static @Nullable BlockPos getDripPos(World world, BlockPos pos) {
        BiPredicate<BlockPos, BlockState> biPredicate = (posx, state) -> PointedDripstoneBlock.canDripThrough((BlockView)world, (BlockPos)posx, (BlockState)state);
        return PointedDripstoneBlock.searchInDirection((WorldAccess)world, (BlockPos)pos, (Direction.AxisDirection)Direction.UP.getDirection(), biPredicate, PointedDripstoneBlock::canDrip, (int)11).orElse(null);
    }

    public static Fluid getDripFluid(ServerWorld world, BlockPos pos) {
        return PointedDripstoneBlock.getFluid((World)world, (BlockPos)pos, (BlockState)world.getBlockState(pos)).map(fluid -> fluid.fluid).filter(PointedDripstoneBlock::isFluidLiquid).orElse(Fluids.EMPTY);
    }

    private static Optional<DrippingFluid> getFluid(World world, BlockPos pos, BlockState state) {
        if (!PointedDripstoneBlock.isPointingDown((BlockState)state)) {
            return Optional.empty();
        }
        return PointedDripstoneBlock.getSupportingPos((World)world, (BlockPos)pos, (BlockState)state, (int)11).map(posx -> {
            BlockPos blockPos = posx.up();
            BlockState blockState = world.getBlockState(blockPos);
            Object fluid = blockState.isOf(Blocks.MUD) && (Boolean)world.getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.WATER_EVAPORATES_GAMEPLAY, blockPos) == false ? Fluids.WATER : world.getFluidState(blockPos).getFluid();
            return new DrippingFluid(blockPos, fluid, blockState);
        });
    }

    private static boolean isFluidLiquid(Fluid fluid) {
        return fluid == Fluids.LAVA || fluid == Fluids.WATER;
    }

    private static boolean canGrow(BlockState dripstoneBlockState, BlockState waterState) {
        return dripstoneBlockState.isOf(Blocks.DRIPSTONE_BLOCK) && waterState.isOf(Blocks.WATER) && waterState.getFluidState().isStill();
    }

    private static ParticleEffect getParticleEffect(World world, Fluid fluid, BlockPos pos) {
        if (fluid.matchesType(Fluids.EMPTY)) {
            return (ParticleEffect)world.getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.DEFAULT_DRIPSTONE_PARTICLE_VISUAL, pos);
        }
        return fluid.isIn(FluidTags.LAVA) ? ParticleTypes.DRIPPING_DRIPSTONE_LAVA : ParticleTypes.DRIPPING_DRIPSTONE_WATER;
    }

    private static Optional<BlockPos> searchInDirection(WorldAccess world, BlockPos pos, Direction.AxisDirection direction, BiPredicate<BlockPos, BlockState> continuePredicate, Predicate<BlockState> stopPredicate, int range) {
        Direction direction2 = Direction.get((Direction.AxisDirection)direction, (Direction.Axis)Direction.Axis.Y);
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (int i = 1; i < range; ++i) {
            mutable.move(direction2);
            BlockState blockState = world.getBlockState((BlockPos)mutable);
            if (stopPredicate.test(blockState)) {
                return Optional.of(mutable.toImmutable());
            }
            if (!world.isOutOfHeightLimit(mutable.getY()) && continuePredicate.test((BlockPos)mutable, blockState)) continue;
            return Optional.empty();
        }
        return Optional.empty();
    }

    private static boolean canDripThrough(BlockView world, BlockPos pos, BlockState state) {
        if (state.isAir()) {
            return true;
        }
        if (state.isOpaqueFullCube()) {
            return false;
        }
        if (!state.getFluidState().isEmpty()) {
            return false;
        }
        VoxelShape voxelShape = state.getCollisionShape(world, pos);
        return !VoxelShapes.matchesAnywhere((VoxelShape)DRIP_COLLISION_SHAPE, (VoxelShape)voxelShape, (BooleanBiFunction)BooleanBiFunction.AND);
    }
}

