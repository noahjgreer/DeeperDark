package net.minecraft.world;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public interface TestableWorld {
   boolean testBlockState(BlockPos pos, Predicate state);

   boolean testFluidState(BlockPos pos, Predicate state);

   Optional getBlockEntity(BlockPos pos, BlockEntityType type);

   BlockPos getTopPosition(Heightmap.Type heightmap, BlockPos pos);
}
