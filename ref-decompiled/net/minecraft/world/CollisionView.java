package net.minecraft.world;

import com.google.common.collect.Iterables;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;

public interface CollisionView extends BlockView {
   WorldBorder getWorldBorder();

   @Nullable
   BlockView getChunkAsView(int chunkX, int chunkZ);

   default boolean doesNotIntersectEntities(@Nullable Entity except, VoxelShape shape) {
      return true;
   }

   default boolean canPlace(BlockState state, BlockPos pos, ShapeContext context) {
      VoxelShape voxelShape = state.getCollisionShape(this, pos, context);
      return voxelShape.isEmpty() || this.doesNotIntersectEntities((Entity)null, voxelShape.offset((Vec3i)pos));
   }

   default boolean doesNotIntersectEntities(Entity entity) {
      return this.doesNotIntersectEntities(entity, VoxelShapes.cuboid(entity.getBoundingBox()));
   }

   default boolean isSpaceEmpty(Box box) {
      return this.isSpaceEmpty((Entity)null, box);
   }

   default boolean isSpaceEmpty(Entity entity) {
      return this.isSpaceEmpty(entity, entity.getBoundingBox());
   }

   default boolean isSpaceEmpty(@Nullable Entity entity, Box box) {
      return this.isSpaceEmpty(entity, box, false);
   }

   default boolean isSpaceEmpty(@Nullable Entity entity, Box box, boolean checkFluid) {
      Iterable iterable = checkFluid ? this.getBlockOrFluidCollisions(entity, box) : this.getBlockCollisions(entity, box);
      Iterator var5 = iterable.iterator();

      while(var5.hasNext()) {
         VoxelShape voxelShape = (VoxelShape)var5.next();
         if (!voxelShape.isEmpty()) {
            return false;
         }
      }

      if (!this.getEntityCollisions(entity, box).isEmpty()) {
         return false;
      } else if (entity == null) {
         return true;
      } else {
         VoxelShape voxelShape2 = this.getWorldBorderCollisions(entity, box);
         return voxelShape2 == null || !VoxelShapes.matchesAnywhere(voxelShape2, VoxelShapes.cuboid(box), BooleanBiFunction.AND);
      }
   }

   default boolean isBlockSpaceEmpty(@Nullable Entity entity, Box box) {
      Iterator var3 = this.getBlockCollisions(entity, box).iterator();

      VoxelShape voxelShape;
      do {
         if (!var3.hasNext()) {
            return true;
         }

         voxelShape = (VoxelShape)var3.next();
      } while(voxelShape.isEmpty());

      return false;
   }

   List getEntityCollisions(@Nullable Entity entity, Box box);

   default Iterable getCollisions(@Nullable Entity entity, Box box) {
      List list = this.getEntityCollisions(entity, box);
      Iterable iterable = this.getBlockCollisions(entity, box);
      return list.isEmpty() ? iterable : Iterables.concat(list, iterable);
   }

   default Iterable getCollisions(@Nullable Entity entity, Box box, Vec3d pos) {
      List list = this.getEntityCollisions(entity, box);
      Iterable iterable = this.getBlockOrFluidCollisions(ShapeContext.ofCollision(entity, pos.y), box);
      return list.isEmpty() ? iterable : Iterables.concat(list, iterable);
   }

   default Iterable getBlockCollisions(@Nullable Entity entity, Box box) {
      return this.getBlockOrFluidCollisions(entity == null ? ShapeContext.absent() : ShapeContext.of(entity), box);
   }

   default Iterable getBlockOrFluidCollisions(@Nullable Entity entity, Box box) {
      return this.getBlockOrFluidCollisions(entity == null ? ShapeContext.absent() : ShapeContext.of(entity, true), box);
   }

   private Iterable getBlockOrFluidCollisions(ShapeContext shapeContext, Box box) {
      return () -> {
         return new BlockCollisionSpliterator(this, shapeContext, box, false, (pos, shape) -> {
            return shape;
         });
      };
   }

   @Nullable
   private VoxelShape getWorldBorderCollisions(Entity entity, Box box) {
      WorldBorder worldBorder = this.getWorldBorder();
      return worldBorder.canCollide(entity, box) ? worldBorder.asVoxelShape() : null;
   }

   default BlockHitResult getCollisionsIncludingWorldBorder(RaycastContext context) {
      BlockHitResult blockHitResult = this.raycast(context);
      WorldBorder worldBorder = this.getWorldBorder();
      if (worldBorder.contains(context.getStart()) && !worldBorder.contains(blockHitResult.getPos())) {
         Vec3d vec3d = blockHitResult.getPos().subtract(context.getStart());
         Direction direction = Direction.getFacing(vec3d.x, vec3d.y, vec3d.z);
         Vec3d vec3d2 = worldBorder.clamp(blockHitResult.getPos());
         return new BlockHitResult(vec3d2, direction, BlockPos.ofFloored(vec3d2), false, true);
      } else {
         return blockHitResult;
      }
   }

   default boolean canCollide(@Nullable Entity entity, Box box) {
      BlockCollisionSpliterator blockCollisionSpliterator = new BlockCollisionSpliterator(this, entity, box, true, (pos, voxelShape) -> {
         return voxelShape;
      });

      do {
         if (!blockCollisionSpliterator.hasNext()) {
            return false;
         }
      } while(((VoxelShape)blockCollisionSpliterator.next()).isEmpty());

      return true;
   }

   default Optional findSupportingBlockPos(Entity entity, Box box) {
      BlockPos blockPos = null;
      double d = Double.MAX_VALUE;
      BlockCollisionSpliterator blockCollisionSpliterator = new BlockCollisionSpliterator(this, entity, box, false, (pos, voxelShape) -> {
         return pos;
      });

      while(true) {
         BlockPos blockPos2;
         double e;
         do {
            if (!blockCollisionSpliterator.hasNext()) {
               return Optional.ofNullable(blockPos);
            }

            blockPos2 = (BlockPos)blockCollisionSpliterator.next();
            e = blockPos2.getSquaredDistance(entity.getPos());
         } while(!(e < d) && (e != d || blockPos != null && blockPos.compareTo(blockPos2) >= 0));

         blockPos = blockPos2.toImmutable();
         d = e;
      }
   }

   default Optional findClosestCollision(@Nullable Entity entity, VoxelShape shape, Vec3d target, double x, double y, double z) {
      if (shape.isEmpty()) {
         return Optional.empty();
      } else {
         Box box = shape.getBoundingBox().expand(x, y, z);
         VoxelShape voxelShape = (VoxelShape)StreamSupport.stream(this.getBlockCollisions(entity, box).spliterator(), false).filter((collision) -> {
            return this.getWorldBorder() == null || this.getWorldBorder().contains(collision.getBoundingBox());
         }).flatMap((collision) -> {
            return collision.getBoundingBoxes().stream();
         }).map((boxx) -> {
            return boxx.expand(x / 2.0, y / 2.0, z / 2.0);
         }).map(VoxelShapes::cuboid).reduce(VoxelShapes.empty(), VoxelShapes::union);
         VoxelShape voxelShape2 = VoxelShapes.combineAndSimplify(shape, voxelShape, BooleanBiFunction.ONLY_FIRST);
         return voxelShape2.getClosestPointTo(target);
      }
   }
}
