/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.block.Fertilizable$FertilizableType
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Type
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 */
package net.minecraft.block;

import java.util.List;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

/*
 * Exception performing whole class analysis ignored.
 */
public interface Fertilizable {
    public boolean isFertilizable(WorldView var1, BlockPos var2, BlockState var3);

    public boolean canGrow(World var1, Random var2, BlockPos var3, BlockState var4);

    public void grow(ServerWorld var1, Random var2, BlockPos var3, BlockState var4);

    public static boolean canSpread(WorldView world, BlockPos pos, BlockState state) {
        return Fertilizable.findPosToSpreadTo(Direction.Type.HORIZONTAL.stream().toList(), (WorldView)world, (BlockPos)pos, (BlockState)state).isPresent();
    }

    public static Optional<BlockPos> findPosToSpreadTo(World world, BlockPos pos, BlockState state) {
        return Fertilizable.findPosToSpreadTo((List)Direction.Type.HORIZONTAL.getShuffled(world.random), (WorldView)world, (BlockPos)pos, (BlockState)state);
    }

    private static Optional<BlockPos> findPosToSpreadTo(List<Direction> directions, WorldView world, BlockPos pos, BlockState state) {
        for (Direction direction : directions) {
            BlockPos blockPos = pos.offset(direction);
            if (!world.isAir(blockPos) || !state.canPlaceAt(world, blockPos)) continue;
            return Optional.of(blockPos);
        }
        return Optional.empty();
    }

    default public BlockPos getFertilizeParticlePos(BlockPos pos) {
        return switch (this.getFertilizableType().ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> pos.up();
            case 1 -> pos;
        };
    }

    default public FertilizableType getFertilizableType() {
        return FertilizableType.GROWER;
    }
}

