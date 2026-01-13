/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.floats.Float2FloatFunction
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractChestBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.ChestBlock
 *  net.minecraft.block.ChestBlock$4
 *  net.minecraft.block.DoubleBlockProperties
 *  net.minecraft.block.DoubleBlockProperties$PropertyRetriever
 *  net.minecraft.block.DoubleBlockProperties$PropertySource
 *  net.minecraft.block.DoubleBlockProperties$Type
 *  net.minecraft.block.HorizontalFacingBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.ChestBlockEntity
 *  net.minecraft.block.entity.LidOpenable
 *  net.minecraft.block.enums.ChestType
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.mob.PiglinBrain
 *  net.minecraft.entity.passive.CatEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.registry.Registries
 *  net.minecraft.screen.NamedScreenHandlerFactory
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.stat.Stat
 *  net.minecraft.stat.Stats
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.ItemScatterer
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.Registries;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class ChestBlock
extends AbstractChestBlock<ChestBlockEntity>
implements Waterloggable {
    public static final MapCodec<ChestBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Registries.SOUND_EVENT.getCodec().fieldOf("open_sound").forGetter(ChestBlock::getOpenSound), (App)Registries.SOUND_EVENT.getCodec().fieldOf("close_sound").forGetter(ChestBlock::getCloseSound), (App)ChestBlock.createSettingsCodec()).apply((Applicative)instance, (openSound, closeSound, settings) -> new ChestBlock(() -> BlockEntityType.CHEST, openSound, closeSound, settings)));
    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
    public static final EnumProperty<ChestType> CHEST_TYPE = Properties.CHEST_TYPE;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final int field_31057 = 1;
    private static final VoxelShape SINGLE_SHAPE = Block.createColumnShape((double)14.0, (double)0.0, (double)14.0);
    private static final Map<Direction, VoxelShape> DOUBLE_SHAPES_BY_DIRECTION = VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)Block.createCuboidZShape((double)14.0, (double)0.0, (double)14.0, (double)0.0, (double)15.0));
    private final SoundEvent openSound;
    private final SoundEvent closeSound;
    private static final DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<Inventory>> INVENTORY_RETRIEVER = new /* Unavailable Anonymous Inner Class!! */;
    private static final DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<NamedScreenHandlerFactory>> NAME_RETRIEVER = new /* Unavailable Anonymous Inner Class!! */;

    public MapCodec<? extends ChestBlock> getCodec() {
        return CODEC;
    }

    public ChestBlock(Supplier<BlockEntityType<? extends ChestBlockEntity>> blockEntityTypeSupplier, SoundEvent openSound, SoundEvent closeSound, AbstractBlock.Settings settings) {
        super(settings, blockEntityTypeSupplier);
        this.openSound = openSound;
        this.closeSound = closeSound;
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH)).with((Property)CHEST_TYPE, (Comparable)ChestType.SINGLE)).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    public static DoubleBlockProperties.Type getDoubleBlockType(BlockState state) {
        ChestType chestType = (ChestType)state.get((Property)CHEST_TYPE);
        if (chestType == ChestType.SINGLE) {
            return DoubleBlockProperties.Type.SINGLE;
        }
        if (chestType == ChestType.RIGHT) {
            return DoubleBlockProperties.Type.FIRST;
        }
        return DoubleBlockProperties.Type.SECOND;
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (this.canMergeWith(neighborState) && direction.getAxis().isHorizontal()) {
            ChestType chestType = (ChestType)neighborState.get((Property)CHEST_TYPE);
            if (state.get((Property)CHEST_TYPE) == ChestType.SINGLE && chestType != ChestType.SINGLE && state.get((Property)FACING) == neighborState.get((Property)FACING) && ChestBlock.getFacing((BlockState)neighborState) == direction.getOpposite()) {
                return (BlockState)state.with((Property)CHEST_TYPE, (Comparable)chestType.getOpposite());
            }
        } else if (ChestBlock.getFacing((BlockState)state) == direction) {
            return (BlockState)state.with((Property)CHEST_TYPE, (Comparable)ChestType.SINGLE);
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    public boolean canMergeWith(BlockState state) {
        return state.isOf((Block)this);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (4.field_10775[((ChestType)state.get((Property)CHEST_TYPE)).ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> SINGLE_SHAPE;
            case 2, 3 -> (VoxelShape)DOUBLE_SHAPES_BY_DIRECTION.get(ChestBlock.getFacing((BlockState)state));
        };
    }

    public static Direction getFacing(BlockState state) {
        Direction direction = (Direction)state.get((Property)FACING);
        return state.get((Property)CHEST_TYPE) == ChestType.LEFT ? direction.rotateYClockwise() : direction.rotateYCounterclockwise();
    }

    public static BlockPos getPosInFrontOf(BlockPos pos, BlockState state) {
        Direction direction = ChestBlock.getFacing((BlockState)state);
        return pos.offset(direction);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction3;
        ChestType chestType = ChestType.SINGLE;
        Direction direction = ctx.getHorizontalPlayerFacing().getOpposite();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = ctx.shouldCancelInteraction();
        Direction direction2 = ctx.getSide();
        if (direction2.getAxis().isHorizontal() && bl && (direction3 = this.getNeighborChestDirection(ctx.getWorld(), ctx.getBlockPos(), direction2.getOpposite())) != null && direction3.getAxis() != direction2.getAxis()) {
            direction = direction3;
            ChestType chestType2 = chestType = direction.rotateYCounterclockwise() == direction2.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
        }
        if (chestType == ChestType.SINGLE && !bl) {
            chestType = this.getChestType(ctx.getWorld(), ctx.getBlockPos(), direction);
        }
        return (BlockState)((BlockState)((BlockState)this.getDefaultState().with((Property)FACING, (Comparable)direction)).with((Property)CHEST_TYPE, (Comparable)chestType)).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
    }

    protected ChestType getChestType(World world, BlockPos pos, Direction facing) {
        if (facing == this.getNeighborChestDirection(world, pos, facing.rotateYClockwise())) {
            return ChestType.LEFT;
        }
        if (facing == this.getNeighborChestDirection(world, pos, facing.rotateYCounterclockwise())) {
            return ChestType.RIGHT;
        }
        return ChestType.SINGLE;
    }

    protected FluidState getFluidState(BlockState state) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    private @Nullable Direction getNeighborChestDirection(World world, BlockPos pos, Direction neighborOffset) {
        BlockState blockState = world.getBlockState(pos.offset(neighborOffset));
        return this.canMergeWith(blockState) && blockState.get((Property)CHEST_TYPE) == ChestType.SINGLE ? (Direction)blockState.get((Property)FACING) : null;
    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        ItemScatterer.onStateReplaced((BlockState)state, (World)world, (BlockPos)pos);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            NamedScreenHandlerFactory namedScreenHandlerFactory = this.createScreenHandlerFactory(state, world, pos);
            if (namedScreenHandlerFactory != null) {
                player.openHandledScreen(namedScreenHandlerFactory);
                player.incrementStat(this.getOpenStat());
                PiglinBrain.onGuardedBlockInteracted((ServerWorld)serverWorld, (PlayerEntity)player, (boolean)true);
            }
        }
        return ActionResult.SUCCESS;
    }

    protected Stat<Identifier> getOpenStat() {
        return Stats.CUSTOM.getOrCreateStat((Object)Stats.OPEN_CHEST);
    }

    public BlockEntityType<? extends ChestBlockEntity> getExpectedEntityType() {
        return (BlockEntityType)this.entityTypeRetriever.get();
    }

    public static @Nullable Inventory getInventory(ChestBlock block, BlockState state, World world, BlockPos pos, boolean ignoreBlocked) {
        return ((Optional)block.getBlockEntitySource(state, world, pos, ignoreBlocked).apply(INVENTORY_RETRIEVER)).orElse(null);
    }

    public DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> getBlockEntitySource(BlockState state, World world, BlockPos pos, boolean ignoreBlocked) {
        BiPredicate<WorldAccess, BlockPos> biPredicate = ignoreBlocked ? (worldx, posx) -> false : ChestBlock::isChestBlocked;
        return DoubleBlockProperties.toPropertySource((BlockEntityType)((BlockEntityType)this.entityTypeRetriever.get()), ChestBlock::getDoubleBlockType, ChestBlock::getFacing, (Property)FACING, (BlockState)state, (WorldAccess)world, (BlockPos)pos, biPredicate);
    }

    protected @Nullable NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return ((Optional)this.getBlockEntitySource(state, world, pos, false).apply(NAME_RETRIEVER)).orElse(null);
    }

    public static DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Float2FloatFunction> getAnimationProgressRetriever(LidOpenable progress) {
        return new /* Unavailable Anonymous Inner Class!! */;
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChestBlockEntity(pos, state);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? ChestBlock.validateTicker(type, (BlockEntityType)this.getExpectedEntityType(), ChestBlockEntity::clientTick) : null;
    }

    public static boolean isChestBlocked(WorldAccess world, BlockPos pos) {
        return ChestBlock.hasBlockOnTop((BlockView)world, (BlockPos)pos) || ChestBlock.hasCatOnTop((WorldAccess)world, (BlockPos)pos);
    }

    private static boolean hasBlockOnTop(BlockView world, BlockPos pos) {
        BlockPos blockPos = pos.up();
        return world.getBlockState(blockPos).isSolidBlock(world, blockPos);
    }

    private static boolean hasCatOnTop(WorldAccess world, BlockPos pos) {
        List list = world.getNonSpectatingEntities(CatEntity.class, new Box((double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 2), (double)(pos.getZ() + 1)));
        if (!list.isEmpty()) {
            for (CatEntity catEntity : list) {
                if (!catEntity.isInSittingPose()) continue;
                return true;
            }
        }
        return false;
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        return ScreenHandler.calculateComparatorOutput((Inventory)ChestBlock.getInventory((ChestBlock)this, (BlockState)state, (World)world, (BlockPos)pos, (boolean)false));
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get((Property)FACING)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, CHEST_TYPE, WATERLOGGED});
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ChestBlockEntity) {
            ((ChestBlockEntity)blockEntity).onScheduledTick();
        }
    }

    public SoundEvent getOpenSound() {
        return this.openSound;
    }

    public SoundEvent getCloseSound() {
        return this.closeSound;
    }
}

