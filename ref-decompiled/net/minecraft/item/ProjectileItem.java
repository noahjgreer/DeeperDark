package net.minecraft.item;

import java.util.OptionalInt;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface ProjectileItem {
   ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction);

   default Settings getProjectileSettings() {
      return ProjectileItem.Settings.DEFAULT;
   }

   default void initializeProjectile(ProjectileEntity entity, double x, double y, double z, float power, float uncertainty) {
      entity.setVelocity(x, y, z, power, uncertainty);
   }

   public static record Settings(PositionFunction positionFunction, float uncertainty, float power, OptionalInt overrideDispenseEvent) {
      public static final Settings DEFAULT = builder().build();

      public Settings(PositionFunction positionFunction, float f, float g, OptionalInt optionalInt) {
         this.positionFunction = positionFunction;
         this.uncertainty = f;
         this.power = g;
         this.overrideDispenseEvent = optionalInt;
      }

      public static Builder builder() {
         return new Builder();
      }

      public PositionFunction positionFunction() {
         return this.positionFunction;
      }

      public float uncertainty() {
         return this.uncertainty;
      }

      public float power() {
         return this.power;
      }

      public OptionalInt overrideDispenseEvent() {
         return this.overrideDispenseEvent;
      }

      public static class Builder {
         private PositionFunction positionFunction = (pointer, direction) -> {
            return DispenserBlock.getOutputLocation(pointer, 0.7, new Vec3d(0.0, 0.1, 0.0));
         };
         private float uncertainty = 6.0F;
         private float power = 1.1F;
         private OptionalInt overrideDispenserEvent = OptionalInt.empty();

         public Builder positionFunction(PositionFunction positionFunction) {
            this.positionFunction = positionFunction;
            return this;
         }

         public Builder uncertainty(float uncertainty) {
            this.uncertainty = uncertainty;
            return this;
         }

         public Builder power(float power) {
            this.power = power;
            return this;
         }

         public Builder overrideDispenseEvent(int overrideDispenseEvent) {
            this.overrideDispenserEvent = OptionalInt.of(overrideDispenseEvent);
            return this;
         }

         public Settings build() {
            return new Settings(this.positionFunction, this.uncertainty, this.power, this.overrideDispenserEvent);
         }
      }
   }

   @FunctionalInterface
   public interface PositionFunction {
      Position getDispensePosition(BlockPointer pointer, Direction facing);
   }
}
