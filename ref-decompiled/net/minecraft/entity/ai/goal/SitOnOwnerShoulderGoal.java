package net.minecraft.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableShoulderEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class SitOnOwnerShoulderGoal extends Goal {
   private final TameableShoulderEntity tameable;
   private boolean mounted;

   public SitOnOwnerShoulderGoal(TameableShoulderEntity tameable) {
      this.tameable = tameable;
   }

   public boolean canStart() {
      LivingEntity var2 = this.tameable.getOwner();
      if (!(var2 instanceof ServerPlayerEntity serverPlayerEntity)) {
         return false;
      } else {
         boolean bl = !serverPlayerEntity.isSpectator() && !serverPlayerEntity.getAbilities().flying && !serverPlayerEntity.isTouchingWater() && !serverPlayerEntity.inPowderSnow;
         return !this.tameable.isSitting() && bl && this.tameable.isReadyToSitOnPlayer();
      }
   }

   public boolean canStop() {
      return !this.mounted;
   }

   public void start() {
      this.mounted = false;
   }

   public void tick() {
      if (!this.mounted && !this.tameable.isInSittingPose() && !this.tameable.isLeashed()) {
         LivingEntity var2 = this.tameable.getOwner();
         if (var2 instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var2;
            if (this.tameable.getBoundingBox().intersects(serverPlayerEntity.getBoundingBox())) {
               this.mounted = this.tameable.mountOnto(serverPlayerEntity);
            }
         }

      }
   }
}
