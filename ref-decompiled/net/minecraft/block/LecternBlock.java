/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.HorizontalFacingBlock
 *  net.minecraft.block.LecternBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.LecternBlockEntity
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.TypedEntityData
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.ItemTags
 *  net.minecraft.screen.NamedScreenHandlerFactory
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
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.block.OrientationHelper
 *  net.minecraft.world.block.WireOrientation
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.event.GameEvent$Emitter
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TypedEntityData;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.NamedScreenHandlerFactory;
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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.GameEvent;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class LecternBlock
extends BlockWithEntity {
    public static final MapCodec<LecternBlock> CODEC = LecternBlock.createCodec(LecternBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final BooleanProperty HAS_BOOK = Properties.HAS_BOOK;
    private static final VoxelShape BASE_SHAPE = VoxelShapes.union((VoxelShape)Block.createColumnShape((double)16.0, (double)0.0, (double)2.0), (VoxelShape)Block.createColumnShape((double)8.0, (double)2.0, (double)14.0));
    private static final Map<Direction, VoxelShape> OUTLINE_SHAPES_BY_DIRECTION = VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)VoxelShapes.union((VoxelShape)Block.createCuboidZShape((double)16.0, (double)10.0, (double)14.0, (double)1.0, (double)5.333333), (VoxelShape[])new VoxelShape[]{Block.createCuboidZShape((double)16.0, (double)12.0, (double)16.0, (double)5.333333, (double)9.666667), Block.createCuboidZShape((double)16.0, (double)14.0, (double)18.0, (double)9.666667, (double)14.0), BASE_SHAPE}));
    private static final int SCHEDULED_TICK_DELAY = 2;

    public MapCodec<LecternBlock> getCodec() {
        return CODEC;
    }

    public LecternBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH)).with((Property)POWERED, (Comparable)Boolean.valueOf(false))).with((Property)HAS_BOOK, (Comparable)Boolean.valueOf(false)));
    }

    protected VoxelShape getCullingShape(BlockState state) {
        return BASE_SHAPE;
    }

    protected boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        TypedEntityData typedEntityData;
        World world = ctx.getWorld();
        ItemStack itemStack = ctx.getStack();
        PlayerEntity playerEntity = ctx.getPlayer();
        boolean bl = false;
        if (!world.isClient() && playerEntity != null && playerEntity.isCreativeLevelTwoOp() && (typedEntityData = (TypedEntityData)itemStack.get(DataComponentTypes.BLOCK_ENTITY_DATA)) != null && typedEntityData.contains("Book")) {
            bl = true;
        }
        return (BlockState)((BlockState)this.getDefaultState().with((Property)FACING, (Comparable)ctx.getHorizontalPlayerFacing().getOpposite())).with((Property)HAS_BOOK, (Comparable)Boolean.valueOf(bl));
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return BASE_SHAPE;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)OUTLINE_SHAPES_BY_DIRECTION.get(state.get((Property)FACING));
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get((Property)FACING)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, POWERED, HAS_BOOK});
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LecternBlockEntity(pos, state);
    }

    public static boolean putBookIfAbsent(@Nullable LivingEntity user, World world, BlockPos pos, BlockState state, ItemStack stack) {
        if (!((Boolean)state.get((Property)HAS_BOOK)).booleanValue()) {
            if (!world.isClient()) {
                LecternBlock.putBook((LivingEntity)user, (World)world, (BlockPos)pos, (BlockState)state, (ItemStack)stack);
            }
            return true;
        }
        return false;
    }

    private static void putBook(@Nullable LivingEntity user, World world, BlockPos pos, BlockState state, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof LecternBlockEntity) {
            LecternBlockEntity lecternBlockEntity = (LecternBlockEntity)blockEntity;
            lecternBlockEntity.setBook(stack.splitUnlessCreative(1, user));
            LecternBlock.setHasBook((Entity)user, (World)world, (BlockPos)pos, (BlockState)state, (boolean)true);
            world.playSound(null, pos, SoundEvents.ITEM_BOOK_PUT, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    public static void setHasBook(@Nullable Entity user, World world, BlockPos pos, BlockState state, boolean hasBook) {
        BlockState blockState = (BlockState)((BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(false))).with((Property)HAS_BOOK, (Comparable)Boolean.valueOf(hasBook));
        world.setBlockState(pos, blockState, 3);
        world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of((Entity)user, (BlockState)blockState));
        LecternBlock.updateNeighborAlways((World)world, (BlockPos)pos, (BlockState)state);
    }

    public static void setPowered(World world, BlockPos pos, BlockState state) {
        LecternBlock.setPowered((World)world, (BlockPos)pos, (BlockState)state, (boolean)true);
        world.scheduleBlockTick(pos, state.getBlock(), 2);
        world.syncWorldEvent(1043, pos, 0);
    }

    private static void setPowered(World world, BlockPos pos, BlockState state, boolean powered) {
        world.setBlockState(pos, (BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(powered)), 3);
        LecternBlock.updateNeighborAlways((World)world, (BlockPos)pos, (BlockState)state);
    }

    private static void updateNeighborAlways(World world, BlockPos pos, BlockState state) {
        WireOrientation wireOrientation = OrientationHelper.getEmissionOrientation((World)world, (Direction)((Direction)state.get((Property)FACING)).getOpposite(), (Direction)Direction.UP);
        world.updateNeighborsAlways(pos.down(), state.getBlock(), wireOrientation);
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        LecternBlock.setPowered((World)world, (BlockPos)pos, (BlockState)state, (boolean)false);
    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if (((Boolean)state.get((Property)POWERED)).booleanValue()) {
            LecternBlock.updateNeighborAlways((World)world, (BlockPos)pos, (BlockState)state);
        }
    }

    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return (Boolean)state.get((Property)POWERED) != false ? 15 : 0;
    }

    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return direction == Direction.UP && (Boolean)state.get((Property)POWERED) != false ? 15 : 0;
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        BlockEntity blockEntity;
        if (((Boolean)state.get((Property)HAS_BOOK)).booleanValue() && (blockEntity = world.getBlockEntity(pos)) instanceof LecternBlockEntity) {
            return ((LecternBlockEntity)blockEntity).getComparatorOutput();
        }
        return 0;
    }

    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (((Boolean)state.get((Property)HAS_BOOK)).booleanValue()) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
        if (stack.isIn(ItemTags.LECTERN_BOOKS)) {
            return LecternBlock.putBookIfAbsent((LivingEntity)player, (World)world, (BlockPos)pos, (BlockState)state, (ItemStack)stack) ? ActionResult.SUCCESS : ActionResult.PASS;
        }
        if (stack.isEmpty() && hand == Hand.MAIN_HAND) {
            return ActionResult.PASS;
        }
        return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (((Boolean)state.get((Property)HAS_BOOK)).booleanValue()) {
            if (!world.isClient()) {
                this.openScreen(world, pos, player);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.CONSUME;
    }

    protected @Nullable NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        if (!((Boolean)state.get((Property)HAS_BOOK)).booleanValue()) {
            return null;
        }
        return super.createScreenHandlerFactory(state, world, pos);
    }

    private void openScreen(World world, BlockPos pos, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof LecternBlockEntity) {
            player.openHandledScreen((NamedScreenHandlerFactory)((LecternBlockEntity)blockEntity));
            player.incrementStat(Stats.INTERACT_WITH_LECTERN);
        }
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}

