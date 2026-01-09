package net.minecraft.world;

import java.util.function.Predicate;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class RaycastContext {
   private final Vec3d start;
   private final Vec3d end;
   private final ShapeType shapeType;
   private final FluidHandling fluid;
   private final ShapeContext shapeContext;

   public RaycastContext(Vec3d start, Vec3d end, ShapeType shapeType, FluidHandling fluidHandling, Entity entity) {
      this(start, end, shapeType, fluidHandling, ShapeContext.of(entity));
   }

   public RaycastContext(Vec3d start, Vec3d end, ShapeType shapeType, FluidHandling fluidHandling, ShapeContext shapeContext) {
      this.start = start;
      this.end = end;
      this.shapeType = shapeType;
      this.fluid = fluidHandling;
      this.shapeContext = shapeContext;
   }

   public Vec3d getEnd() {
      return this.end;
   }

   public Vec3d getStart() {
      return this.start;
   }

   public VoxelShape getBlockShape(BlockState state, BlockView world, BlockPos pos) {
      return this.shapeType.get(state, world, pos, this.shapeContext);
   }

   public VoxelShape getFluidShape(FluidState state, BlockView world, BlockPos pos) {
      return this.fluid.handled(state) ? state.getShape(world, pos) : VoxelShapes.empty();
   }

   public static enum ShapeType implements ShapeProvider {
      COLLIDER(AbstractBlock.AbstractBlockState::getCollisionShape),
      OUTLINE(AbstractBlock.AbstractBlockState::getOutlineShape),
      VISUAL(AbstractBlock.AbstractBlockState::getCameraCollisionShape),
      FALLDAMAGE_RESETTING((state, world, pos, context) -> {
         return state.isIn(BlockTags.FALL_DAMAGE_RESETTING) ? VoxelShapes.fullCube() : VoxelShapes.empty();
      });

      private final ShapeProvider provider;

      private ShapeType(final ShapeProvider provider) {
         this.provider = provider;
      }

      public VoxelShape get(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext shapeContext) {
         return this.provider.get(blockState, blockView, blockPos, shapeContext);
      }

      // $FF: synthetic method
      private static ShapeType[] method_36690() {
         return new ShapeType[]{COLLIDER, OUTLINE, VISUAL, FALLDAMAGE_RESETTING};
      }
   }

   public static enum FluidHandling {
      NONE((state) -> {
         return false;
      }),
      SOURCE_ONLY(FluidState::isStill),
      ANY((state) -> {
         return !state.isEmpty();
      }),
      WATER((state) -> {
         return state.isIn(FluidTags.WATER);
      });

      private final Predicate predicate;

      private FluidHandling(final Predicate predicate) {
         this.predicate = predicate;
      }

      public boolean handled(FluidState state) {
         return this.predicate.test(state);
      }

      // $FF: synthetic method
      private static FluidHandling[] method_36691() {
         return new FluidHandling[]{NONE, SOURCE_ONLY, ANY, WATER};
      }
   }

   public interface ShapeProvider {
      VoxelShape get(BlockState state, BlockView world, BlockPos pos, ShapeContext context);
   }
}
