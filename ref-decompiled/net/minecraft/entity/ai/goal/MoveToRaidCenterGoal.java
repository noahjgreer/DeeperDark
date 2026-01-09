package net.minecraft.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.raid.Raid;
import net.minecraft.village.raid.RaidManager;

public class MoveToRaidCenterGoal extends Goal {
   private static final int FREE_RAIDER_CHECK_INTERVAL = 20;
   private static final float WALK_SPEED = 1.0F;
   private final RaiderEntity actor;
   private int nextFreeRaiderCheckAge;

   public MoveToRaidCenterGoal(RaiderEntity actor) {
      this.actor = actor;
      this.setControls(EnumSet.of(Goal.Control.MOVE));
   }

   public boolean canStart() {
      return this.actor.getTarget() == null && !this.actor.hasControllingPassenger() && this.actor.hasActiveRaid() && !this.actor.getRaid().isFinished() && !castToServerWorld(this.actor.getWorld()).isNearOccupiedPointOfInterest(this.actor.getBlockPos());
   }

   public boolean shouldContinue() {
      return this.actor.hasActiveRaid() && !this.actor.getRaid().isFinished() && !castToServerWorld(this.actor.getWorld()).isNearOccupiedPointOfInterest(this.actor.getBlockPos());
   }

   public void tick() {
      if (this.actor.hasActiveRaid()) {
         Raid raid = this.actor.getRaid();
         if (this.actor.age > this.nextFreeRaiderCheckAge) {
            this.nextFreeRaiderCheckAge = this.actor.age + 20;
            this.includeFreeRaiders(raid);
         }

         if (!this.actor.isNavigating()) {
            Vec3d vec3d = NoPenaltyTargeting.findTo(this.actor, 15, 4, Vec3d.ofBottomCenter(raid.getCenter()), 1.5707963705062866);
            if (vec3d != null) {
               this.actor.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
            }
         }
      }

   }

   private void includeFreeRaiders(Raid raid) {
      if (raid.isActive()) {
         ServerWorld serverWorld = castToServerWorld(this.actor.getWorld());
         Set set = Sets.newHashSet();
         List list = serverWorld.getEntitiesByClass(RaiderEntity.class, this.actor.getBoundingBox().expand(16.0), (raiderEntityx) -> {
            return !raiderEntityx.hasActiveRaid() && RaidManager.isValidRaiderFor(raiderEntityx);
         });
         set.addAll(list);
         Iterator var5 = set.iterator();

         while(var5.hasNext()) {
            RaiderEntity raiderEntity = (RaiderEntity)var5.next();
            raid.addRaider(serverWorld, raid.getGroupsSpawned(), raiderEntity, (BlockPos)null, true);
         }
      }

   }
}
