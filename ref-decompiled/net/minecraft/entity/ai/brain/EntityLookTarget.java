package net.minecraft.entity.ai.brain;

import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class EntityLookTarget implements LookTarget {
   private final Entity entity;
   private final boolean useEyeHeight;
   private final boolean blockPosAtEye;

   public EntityLookTarget(Entity entity, boolean useEyeHeight) {
      this(entity, useEyeHeight, false);
   }

   public EntityLookTarget(Entity entity, boolean useEyeHeight, boolean blockPosAtEye) {
      this.entity = entity;
      this.useEyeHeight = useEyeHeight;
      this.blockPosAtEye = blockPosAtEye;
   }

   public Vec3d getPos() {
      return this.useEyeHeight ? this.entity.getPos().add(0.0, (double)this.entity.getStandingEyeHeight(), 0.0) : this.entity.getPos();
   }

   public BlockPos getBlockPos() {
      return this.blockPosAtEye ? BlockPos.ofFloored(this.entity.getEyePos()) : this.entity.getBlockPos();
   }

   public boolean isSeenBy(LivingEntity entity) {
      Entity var3 = this.entity;
      if (var3 instanceof LivingEntity livingEntity) {
         if (!livingEntity.isAlive()) {
            return false;
         } else {
            Optional optional = entity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS);
            return optional.isPresent() && ((LivingTargetCache)optional.get()).contains(livingEntity);
         }
      } else {
         return true;
      }
   }

   public Entity getEntity() {
      return this.entity;
   }

   public String toString() {
      return "EntityTracker for " + String.valueOf(this.entity);
   }
}
