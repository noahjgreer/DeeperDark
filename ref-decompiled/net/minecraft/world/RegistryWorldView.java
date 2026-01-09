package net.minecraft.world;

import java.util.List;
import java.util.Optional;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;

public interface RegistryWorldView extends EntityView, WorldView, ModifiableTestableWorld {
   default Optional getBlockEntity(BlockPos pos, BlockEntityType type) {
      return WorldView.super.getBlockEntity(pos, type);
   }

   default List getEntityCollisions(@Nullable Entity entity, Box box) {
      return EntityView.super.getEntityCollisions(entity, box);
   }

   default boolean doesNotIntersectEntities(@Nullable Entity except, VoxelShape shape) {
      return EntityView.super.doesNotIntersectEntities(except, shape);
   }

   default BlockPos getTopPosition(Heightmap.Type heightmap, BlockPos pos) {
      return WorldView.super.getTopPosition(heightmap, pos);
   }
}
