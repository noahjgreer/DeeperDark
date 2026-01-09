package net.minecraft.world.explosion;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface Explosion {
   static DamageSource createDamageSource(World world, @Nullable Entity source) {
      return world.getDamageSources().explosion(source, getCausingEntity(source));
   }

   @Nullable
   static LivingEntity getCausingEntity(@Nullable Entity entity) {
      Entity var1 = entity;
      byte var2 = 0;

      while(true) {
         LivingEntity var10000;
         switch (var1.typeSwitch<invokedynamic>(var1, var2)) {
            case -1:
            default:
               var10000 = null;
               return var10000;
            case 0:
               TntEntity tntEntity = (TntEntity)var1;
               var10000 = tntEntity.getOwner();
               return var10000;
            case 1:
               LivingEntity livingEntity = (LivingEntity)var1;
               var10000 = livingEntity;
               return var10000;
            case 2:
               ProjectileEntity projectileEntity = (ProjectileEntity)var1;
               Entity var7 = projectileEntity.getOwner();
               if (var7 instanceof LivingEntity livingEntity2) {
                  var10000 = livingEntity2;
                  return var10000;
               }

               var2 = 3;
         }
      }
   }

   ServerWorld getWorld();

   DestructionType getDestructionType();

   @Nullable
   LivingEntity getCausingEntity();

   @Nullable
   Entity getEntity();

   float getPower();

   Vec3d getPosition();

   boolean canTriggerBlocks();

   boolean preservesDecorativeEntities();

   public static enum DestructionType {
      KEEP(false),
      DESTROY(true),
      DESTROY_WITH_DECAY(true),
      TRIGGER_BLOCK(false);

      private final boolean destroysBlocks;

      private DestructionType(final boolean destroysBlocks) {
         this.destroysBlocks = destroysBlocks;
      }

      public boolean destroysBlocks() {
         return this.destroysBlocks;
      }

      // $FF: synthetic method
      private static DestructionType[] method_36693() {
         return new DestructionType[]{KEEP, DESTROY, DESTROY_WITH_DECAY, TRIGGER_BLOCK};
      }
   }
}
