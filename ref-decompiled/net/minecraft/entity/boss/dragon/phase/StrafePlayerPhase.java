package net.minecraft.entity.boss.dragon.phase;

import com.mojang.logging.LogUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class StrafePlayerPhase extends AbstractPhase {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MINIMUM_TARGET_SPOT_AMOUNT = 5;
   private int seenTargetTimes;
   @Nullable
   private Path path;
   @Nullable
   private Vec3d pathTarget;
   @Nullable
   private LivingEntity target;
   private boolean shouldFindNewPath;

   public StrafePlayerPhase(EnderDragonEntity enderDragonEntity) {
      super(enderDragonEntity);
   }

   public void serverTick(ServerWorld world) {
      if (this.target == null) {
         LOGGER.warn("Skipping player strafe phase because no player was found");
         this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
      } else {
         double d;
         double e;
         double h;
         if (this.path != null && this.path.isFinished()) {
            d = this.target.getX();
            e = this.target.getZ();
            double f = d - this.dragon.getX();
            double g = e - this.dragon.getZ();
            h = Math.sqrt(f * f + g * g);
            double i = Math.min(0.4000000059604645 + h / 80.0 - 1.0, 10.0);
            this.pathTarget = new Vec3d(d, this.target.getY() + i, e);
         }

         d = this.pathTarget == null ? 0.0 : this.pathTarget.squaredDistanceTo(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
         if (d < 100.0 || d > 22500.0) {
            this.updatePath();
         }

         e = 64.0;
         if (this.target.squaredDistanceTo(this.dragon) < 4096.0) {
            if (this.dragon.canSee(this.target)) {
               ++this.seenTargetTimes;
               Vec3d vec3d = (new Vec3d(this.target.getX() - this.dragon.getX(), 0.0, this.target.getZ() - this.dragon.getZ())).normalize();
               Vec3d vec3d2 = (new Vec3d((double)MathHelper.sin(this.dragon.getYaw() * 0.017453292F), 0.0, (double)(-MathHelper.cos(this.dragon.getYaw() * 0.017453292F)))).normalize();
               float j = (float)vec3d2.dotProduct(vec3d);
               float k = (float)(Math.acos((double)j) * 57.2957763671875);
               k += 0.5F;
               if (this.seenTargetTimes >= 5 && k >= 0.0F && k < 10.0F) {
                  h = 1.0;
                  Vec3d vec3d3 = this.dragon.getRotationVec(1.0F);
                  double l = this.dragon.head.getX() - vec3d3.x * 1.0;
                  double m = this.dragon.head.getBodyY(0.5) + 0.5;
                  double n = this.dragon.head.getZ() - vec3d3.z * 1.0;
                  double o = this.target.getX() - l;
                  double p = this.target.getBodyY(0.5) - m;
                  double q = this.target.getZ() - n;
                  Vec3d vec3d4 = new Vec3d(o, p, q);
                  if (!this.dragon.isSilent()) {
                     world.syncWorldEvent((Entity)null, 1017, this.dragon.getBlockPos(), 0);
                  }

                  DragonFireballEntity dragonFireballEntity = new DragonFireballEntity(world, this.dragon, vec3d4.normalize());
                  dragonFireballEntity.refreshPositionAndAngles(l, m, n, 0.0F, 0.0F);
                  world.spawnEntity(dragonFireballEntity);
                  this.seenTargetTimes = 0;
                  if (this.path != null) {
                     while(!this.path.isFinished()) {
                        this.path.next();
                     }
                  }

                  this.dragon.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
               }
            } else if (this.seenTargetTimes > 0) {
               --this.seenTargetTimes;
            }
         } else if (this.seenTargetTimes > 0) {
            --this.seenTargetTimes;
         }

      }
   }

   private void updatePath() {
      if (this.path == null || this.path.isFinished()) {
         int i = this.dragon.getNearestPathNodeIndex();
         int j = i;
         if (this.dragon.getRandom().nextInt(8) == 0) {
            this.shouldFindNewPath = !this.shouldFindNewPath;
            j = i + 6;
         }

         if (this.shouldFindNewPath) {
            ++j;
         } else {
            --j;
         }

         if (this.dragon.getFight() != null && this.dragon.getFight().getAliveEndCrystals() > 0) {
            j %= 12;
            if (j < 0) {
               j += 12;
            }
         } else {
            j -= 12;
            j &= 7;
            j += 12;
         }

         this.path = this.dragon.findPath(i, j, (PathNode)null);
         if (this.path != null) {
            this.path.next();
         }
      }

      this.followPath();
   }

   private void followPath() {
      if (this.path != null && !this.path.isFinished()) {
         Vec3i vec3i = this.path.getCurrentNodePos();
         this.path.next();
         double d = (double)vec3i.getX();
         double e = (double)vec3i.getZ();

         double f;
         do {
            f = (double)((float)vec3i.getY() + this.dragon.getRandom().nextFloat() * 20.0F);
         } while(f < (double)vec3i.getY());

         this.pathTarget = new Vec3d(d, f, e);
      }

   }

   public void beginPhase() {
      this.seenTargetTimes = 0;
      this.pathTarget = null;
      this.path = null;
      this.target = null;
   }

   public void setTargetEntity(LivingEntity targetEntity) {
      this.target = targetEntity;
      int i = this.dragon.getNearestPathNodeIndex();
      int j = this.dragon.getNearestPathNodeIndex(this.target.getX(), this.target.getY(), this.target.getZ());
      int k = this.target.getBlockX();
      int l = this.target.getBlockZ();
      double d = (double)k - this.dragon.getX();
      double e = (double)l - this.dragon.getZ();
      double f = Math.sqrt(d * d + e * e);
      double g = Math.min(0.4000000059604645 + f / 80.0 - 1.0, 10.0);
      int m = MathHelper.floor(this.target.getY() + g);
      PathNode pathNode = new PathNode(k, m, l);
      this.path = this.dragon.findPath(i, j, pathNode);
      if (this.path != null) {
         this.path.next();
         this.followPath();
      }

   }

   @Nullable
   public Vec3d getPathTarget() {
      return this.pathTarget;
   }

   public PhaseType getType() {
      return PhaseType.STRAFE_PLAYER;
   }
}
