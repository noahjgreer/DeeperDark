/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractSignBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.HangingSignBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.SideShapeType
 *  net.minecraft.block.WallHangingSignBlock
 *  net.minecraft.block.WoodType
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.HangingSignBlockEntity
 *  net.minecraft.block.entity.SignBlockEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.HangingSignItem
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.RotationPropertyHelper
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.WallHangingSignBlock;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HangingSignBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.HangingSignItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class HangingSignBlock
extends AbstractSignBlock {
    public static final MapCodec<HangingSignBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)WoodType.CODEC.fieldOf("wood_type").forGetter(AbstractSignBlock::getWoodType), (App)HangingSignBlock.createSettingsCodec()).apply((Applicative)instance, HangingSignBlock::new));
    public static final IntProperty ROTATION = Properties.ROTATION;
    public static final BooleanProperty ATTACHED = Properties.ATTACHED;
    private static final VoxelShape DEFAULT_SHAPE = Block.createColumnShape((double)10.0, (double)0.0, (double)16.0);
    private static final Map<Integer, VoxelShape> SHAPES_BY_ROTATION = VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)Block.createColumnShape((double)14.0, (double)2.0, (double)0.0, (double)10.0)).entrySet().stream().collect(Collectors.toMap(entry -> RotationPropertyHelper.fromDirection((Direction)((Direction)entry.getKey())), Map.Entry::getValue));

    public MapCodec<HangingSignBlock> getCodec() {
        return CODEC;
    }

    public HangingSignBlock(WoodType woodType, AbstractBlock.Settings settings) {
        super(woodType, settings.sounds(woodType.hangingSignSoundType()));
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)ROTATION, (Comparable)Integer.valueOf(0))).with((Property)ATTACHED, (Comparable)Boolean.valueOf(false))).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        SignBlockEntity signBlockEntity;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SignBlockEntity && this.shouldTryAttaching(player, hit, signBlockEntity = (SignBlockEntity)blockEntity, stack)) {
            return ActionResult.PASS;
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    private boolean shouldTryAttaching(PlayerEntity player, BlockHitResult hitResult, SignBlockEntity sign, ItemStack stack) {
        return !sign.canRunCommandClickEvent(sign.isPlayerFacingFront(player), player) && stack.getItem() instanceof HangingSignItem && hitResult.getSide().equals((Object)Direction.DOWN);
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.up()).isSideSolid((BlockView)world, pos.up(), Direction.DOWN, SideShapeType.CENTER);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        boolean bl2;
        World world = ctx.getWorld();
        FluidState fluidState = world.getFluidState(ctx.getBlockPos());
        BlockPos blockPos = ctx.getBlockPos().up();
        BlockState blockState = world.getBlockState(blockPos);
        boolean bl = blockState.isIn(BlockTags.ALL_HANGING_SIGNS);
        Direction direction = Direction.fromHorizontalDegrees((double)ctx.getPlayerYaw());
        boolean bl3 = bl2 = !Block.isFaceFullSquare((VoxelShape)blockState.getCollisionShape((BlockView)world, blockPos), (Direction)Direction.DOWN) || ctx.shouldCancelInteraction();
        if (bl && !ctx.shouldCancelInteraction()) {
            Optional optional;
            if (blockState.contains((Property)WallHangingSignBlock.FACING)) {
                Direction direction2 = (Direction)blockState.get((Property)WallHangingSignBlock.FACING);
                if (direction2.getAxis().test(direction)) {
                    bl2 = false;
                }
            } else if (blockState.contains((Property)ROTATION) && (optional = RotationPropertyHelper.toDirection((int)((Integer)blockState.get((Property)ROTATION)))).isPresent() && ((Direction)optional.get()).getAxis().test(direction)) {
                bl2 = false;
            }
        }
        int i = !bl2 ? RotationPropertyHelper.fromDirection((Direction)direction.getOpposite()) : RotationPropertyHelper.fromYaw((float)(ctx.getPlayerYaw() + 180.0f));
        return (BlockState)((BlockState)((BlockState)this.getDefaultState().with((Property)ATTACHED, (Comparable)Boolean.valueOf(bl2))).with((Property)ROTATION, (Comparable)Integer.valueOf(i))).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES_BY_ROTATION.getOrDefault(state.get((Property)ROTATION), DEFAULT_SHAPE);
    }

    protected VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return this.getOutlineShape(state, world, pos, ShapeContext.absent());
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction == Direction.UP && !this.canPlaceAt(state, world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    public float getRotationDegrees(BlockState state) {
        return RotationPropertyHelper.toDegrees((int)((Integer)state.get((Property)ROTATION)));
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)ROTATION, (Comparable)Integer.valueOf(rotation.rotate(((Integer)state.get((Property)ROTATION)).intValue(), 16)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return (BlockState)state.with((Property)ROTATION, (Comparable)Integer.valueOf(mirror.mirror(((Integer)state.get((Property)ROTATION)).intValue(), 16)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{ROTATION, ATTACHED, WATERLOGGED});
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HangingSignBlockEntity(pos, state);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return HangingSignBlock.validateTicker(type, (BlockEntityType)BlockEntityType.HANGING_SIGN, SignBlockEntity::tick);
    }
}

