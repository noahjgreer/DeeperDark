package net.noahsarch.deeperdark.block;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

/**
 * A door that moves to the middle of its block when it sits in a line of glass panes, so the
 * panes run straight into it. Opened, it swings half a block into the space in front.
 */
public class GlassDoorBlock extends DoorBlock {
    public static final BooleanProperty CENTERED = BooleanProperty.create("centered");
    // Vanilla door shapes moved 6.5px along FACING; open ones reach past the block edge.
    private static final Map<Direction, VoxelShape> CENTERED_CLOSED =
            Shapes.rotateHorizontal(Block.box(0, 0, 6.5, 16, 16, 9.5));
    private static final Map<Direction, VoxelShape> CENTERED_OPEN_LEFT =
            Shapes.rotateHorizontal(Block.box(0, 0, -6.5, 3, 16, 9.5));
    private static final Map<Direction, VoxelShape> CENTERED_OPEN_RIGHT =
            Shapes.rotateHorizontal(Block.box(13, 0, -6.5, 16, 16, 9.5));

    public GlassDoorBlock(BlockSetType type, BlockBehaviour.Properties properties) {
        super(type, properties);
        registerDefaultState(defaultBlockState().setValue(CENTERED, false));
    }

    /** Whether a pane on the {@code fromPane} side of this door state should connect to it. */
    public static boolean hugsPane(BlockState door, Direction fromPane) {
        return door.getBlock() instanceof GlassDoorBlock
                && door.getValue(CENTERED)
                && fromPane.getAxis() == door.getValue(FACING).getClockWise().getAxis();
    }

    // A pane beside either half, or beside the other leaf of a double door, centers the door.
    private static boolean isCentered(LevelReader level, BlockPos lowerPos, Direction facing) {
        for (Direction side : new Direction[] {facing.getClockWise(), facing.getCounterClockWise()}) {
            for (BlockPos pos : new BlockPos[] {lowerPos, lowerPos.above()}) {
                BlockState neighbour = level.getBlockState(pos.relative(side));
                if (neighbour.getBlock() instanceof IronBarsBlock) return true;
                if (neighbour.getBlock() instanceof GlassDoorBlock
                        && neighbour.getValue(FACING).getAxis() == facing.getAxis()
                        && level.getBlockState(pos.relative(side, 2)).getBlock() instanceof IronBarsBlock) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state == null
                ? null
                : state.setValue(CENTERED, isCentered(context.getLevel(), context.getClickedPos(), state.getValue(FACING)));
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
            Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        BlockState updated = super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
        if (!updated.is(this) || directionToNeighbour.getAxis() != state.getValue(FACING).getClockWise().getAxis()) {
            return updated;
        }
        BlockPos lowerPos = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
        return updated.setValue(CENTERED, isCentered(level, lowerPos, state.getValue(FACING)));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (!state.getValue(CENTERED)) return super.getShape(state, level, pos, context);
        Direction facing = state.getValue(FACING);
        if (!state.getValue(OPEN)) return CENTERED_CLOSED.get(facing);
        return (state.getValue(HINGE) == DoorHingeSide.LEFT ? CENTERED_OPEN_LEFT : CENTERED_OPEN_RIGHT).get(facing);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CENTERED);
    }
}
