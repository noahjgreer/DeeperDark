package net.minecraft.block;

import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.CollisionView;
import org.jetbrains.annotations.Nullable;

public interface ShapeContext {
   static ShapeContext absent() {
      return EntityShapeContext.ABSENT;
   }

   static ShapeContext of(Entity entity) {
      Objects.requireNonNull(entity);
      byte var2 = 0;
      Object var10000;
      switch (entity.typeSwitch<invokedynamic>(entity, var2)) {
         case 0:
            AbstractMinecartEntity abstractMinecartEntity = (AbstractMinecartEntity)entity;
            var10000 = AbstractMinecartEntity.areMinecartImprovementsEnabled(abstractMinecartEntity.getWorld()) ? new ExperimentalMinecartShapeContext(abstractMinecartEntity, false) : new EntityShapeContext(entity, false, false);
            break;
         default:
            var10000 = new EntityShapeContext(entity, false, false);
      }

      return (ShapeContext)var10000;
   }

   static ShapeContext of(Entity entity, boolean collidesWithFluid) {
      return new EntityShapeContext(entity, collidesWithFluid, false);
   }

   static ShapeContext ofPlacement(@Nullable PlayerEntity player) {
      return new EntityShapeContext(player != null ? player.isDescending() : false, true, player != null ? player.getY() : -1.7976931348623157E308, player instanceof LivingEntity ? player.getMainHandStack() : ItemStack.EMPTY, player instanceof LivingEntity ? (state) -> {
         return player.canWalkOnFluid(state);
      } : (state) -> {
         return false;
      }, player);
   }

   static ShapeContext ofCollision(@Nullable Entity entity, double y) {
      EntityShapeContext var10000 = new EntityShapeContext;
      boolean var10002 = entity != null ? entity.isDescending() : false;
      double var10004 = entity != null ? y : -1.7976931348623157E308;
      ItemStack var10005;
      if (entity instanceof LivingEntity livingEntity) {
         var10005 = livingEntity.getMainHandStack();
      } else {
         var10005 = ItemStack.EMPTY;
      }

      Predicate var10006;
      if (entity instanceof LivingEntity livingEntity) {
         var10006 = (state) -> {
            return livingEntity.canWalkOnFluid(state);
         };
      } else {
         var10006 = (state) -> {
            return false;
         };
      }

      var10000.<init>(var10002, true, var10004, var10005, var10006, entity);
      return var10000;
   }

   boolean isDescending();

   boolean isAbove(VoxelShape shape, BlockPos pos, boolean defaultValue);

   boolean isHolding(Item item);

   boolean canWalkOnFluid(FluidState stateAbove, FluidState state);

   VoxelShape getCollisionShape(BlockState state, CollisionView world, BlockPos pos);

   default boolean isPlacement() {
      return false;
   }
}
