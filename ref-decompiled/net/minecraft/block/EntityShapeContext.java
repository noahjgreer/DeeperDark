package net.minecraft.block;

import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.CollisionView;
import org.jetbrains.annotations.Nullable;

public class EntityShapeContext implements ShapeContext {
   protected static final ShapeContext ABSENT;
   private final boolean descending;
   private final double minY;
   private final boolean placement;
   private final ItemStack heldItem;
   private final Predicate walkOnFluidPredicate;
   @Nullable
   private final Entity entity;

   protected EntityShapeContext(boolean descending, boolean placement, double minY, ItemStack heldItem, Predicate walkOnFluidPredicate, @Nullable Entity entity) {
      this.descending = descending;
      this.placement = placement;
      this.minY = minY;
      this.heldItem = heldItem;
      this.walkOnFluidPredicate = walkOnFluidPredicate;
      this.entity = entity;
   }

   /** @deprecated */
   @Deprecated
   protected EntityShapeContext(Entity entity, boolean collidesWithFluid, boolean placement) {
      boolean var10001 = entity.isDescending();
      double var10003 = entity.getY();
      ItemStack var10004;
      if (entity instanceof LivingEntity livingEntity) {
         var10004 = livingEntity.getMainHandStack();
      } else {
         var10004 = ItemStack.EMPTY;
      }

      Predicate var10005;
      if (collidesWithFluid) {
         var10005 = (state) -> {
            return true;
         };
      } else if (entity instanceof LivingEntity) {
         livingEntity = (LivingEntity)entity;
         var10005 = (state) -> {
            return livingEntity.canWalkOnFluid(state);
         };
      } else {
         var10005 = (state) -> {
            return false;
         };
      }

      this(var10001, placement, var10003, var10004, var10005, entity);
   }

   public boolean isHolding(Item item) {
      return this.heldItem.isOf(item);
   }

   public boolean canWalkOnFluid(FluidState stateAbove, FluidState state) {
      return this.walkOnFluidPredicate.test(state) && !stateAbove.getFluid().matchesType(state.getFluid());
   }

   public VoxelShape getCollisionShape(BlockState state, CollisionView world, BlockPos pos) {
      return state.getCollisionShape(world, pos, this);
   }

   public boolean isDescending() {
      return this.descending;
   }

   public boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue) {
      return this.minY > (double)pos.getY() + shape.getMax(Direction.Axis.Y) - 9.999999747378752E-6;
   }

   @Nullable
   public Entity getEntity() {
      return this.entity;
   }

   public boolean isPlacement() {
      return this.placement;
   }

   static {
      ABSENT = new EntityShapeContext(false, false, -1.7976931348623157E308, ItemStack.EMPTY, (fluidState) -> {
         return false;
      }, (Entity)null) {
         public boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue) {
            return defaultValue;
         }
      };
   }
}
