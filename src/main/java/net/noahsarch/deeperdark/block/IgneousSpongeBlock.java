package net.noahsarch.deeperdark.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.Nullable;

/**
 * A lava-absorbing counterpart to vanilla's sponge. Absorbs lava in the same
 * breadth-first fashion as {@link net.minecraft.world.level.block.SpongeBlock},
 * then turns into a magma block, which can be crafted back into an igneous
 * sponge with an empty bucket.
 */
public class IgneousSpongeBlock extends Block {
    public static final MapCodec<IgneousSpongeBlock> CODEC = simpleCodec(IgneousSpongeBlock::new);

    public static final int MAX_DEPTH = 6;
    public static final int MAX_COUNT = 64;

    private static final Direction[] ALL_DIRECTIONS = Direction.values();

    public IgneousSpongeBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<IgneousSpongeBlock> codec() {
        return CODEC;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(state.getBlock())) {
            this.tryAbsorbLava(level, pos);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block,
            @Nullable Orientation orientation, boolean movedByPiston) {
        this.tryAbsorbLava(level, pos);
        super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
    }

    protected void tryAbsorbLava(Level level, BlockPos pos) {
        if (this.removeLavaBreadthFirstSearch(level, pos)) {
            level.setBlock(pos, Blocks.MAGMA_BLOCK.defaultBlockState(), 2);
            level.playSound(null, pos, SoundEvents.SPONGE_ABSORB, SoundSource.BLOCKS, 1.0F, 0.8F);
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.0F);
        }
    }

    private boolean removeLavaBreadthFirstSearch(Level level, BlockPos startPos) {
        return BlockPos.breadthFirstTraversal(startPos, MAX_DEPTH, MAX_COUNT + 1, (pos, consumer) -> {
            for (Direction direction : ALL_DIRECTIONS) {
                consumer.accept(pos.relative(direction));
            }
        }, pos -> {
            if (pos.equals(startPos)) {
                return BlockPos.TraversalNodeStatus.ACCEPT;
            }

            BlockState state = level.getBlockState(pos);
            FluidState fluidState = level.getFluidState(pos);
            if (!fluidState.is(FluidTags.LAVA)) {
                return BlockPos.TraversalNodeStatus.SKIP;
            }

            if (state.getBlock() instanceof BucketPickup bucketPickup
                    && !bucketPickup.pickupBlock(null, level, pos, state).isEmpty()) {
                return BlockPos.TraversalNodeStatus.ACCEPT;
            }

            if (state.getBlock() instanceof LiquidBlock) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                return BlockPos.TraversalNodeStatus.ACCEPT;
            }

            return BlockPos.TraversalNodeStatus.SKIP;
        }) > 1;
    }
}
