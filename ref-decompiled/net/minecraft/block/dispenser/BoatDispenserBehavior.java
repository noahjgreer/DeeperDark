package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class BoatDispenserBehavior extends ItemDispenserBehavior {
   private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();
   private final EntityType boatType;

   public BoatDispenserBehavior(EntityType boatType) {
      this.boatType = boatType;
   }

   public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
      Direction direction = (Direction)pointer.state().get(DispenserBlock.FACING);
      ServerWorld serverWorld = pointer.world();
      Vec3d vec3d = pointer.centerPos();
      double d = 0.5625 + (double)this.boatType.getWidth() / 2.0;
      double e = vec3d.getX() + (double)direction.getOffsetX() * d;
      double f = vec3d.getY() + (double)((float)direction.getOffsetY() * 1.125F);
      double g = vec3d.getZ() + (double)direction.getOffsetZ() * d;
      BlockPos blockPos = pointer.pos().offset(direction);
      double h;
      if (serverWorld.getFluidState(blockPos).isIn(FluidTags.WATER)) {
         h = 1.0;
      } else {
         if (!serverWorld.getBlockState(blockPos).isAir() || !serverWorld.getFluidState(blockPos.down()).isIn(FluidTags.WATER)) {
            return this.fallbackBehavior.dispense(pointer, stack);
         }

         h = 0.0;
      }

      AbstractBoatEntity abstractBoatEntity = (AbstractBoatEntity)this.boatType.create(serverWorld, SpawnReason.DISPENSER);
      if (abstractBoatEntity != null) {
         abstractBoatEntity.initPosition(e, f + h, g);
         EntityType.copier(serverWorld, stack, (LivingEntity)null).accept(abstractBoatEntity);
         abstractBoatEntity.setYaw(direction.getPositiveHorizontalDegrees());
         serverWorld.spawnEntity(abstractBoatEntity);
         stack.decrement(1);
      }

      return stack;
   }

   protected void playSound(BlockPointer pointer) {
      pointer.world().syncWorldEvent(1000, pointer.pos(), 0);
   }
}
