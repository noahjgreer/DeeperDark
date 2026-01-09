package net.minecraft.entity.ai.pathing;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SwimNavigation extends EntityNavigation {
   private boolean canJumpOutOfWater;

   public SwimNavigation(MobEntity mobEntity, World world) {
      super(mobEntity, world);
   }

   protected PathNodeNavigator createPathNodeNavigator(int range) {
      this.canJumpOutOfWater = this.entity.getType() == EntityType.DOLPHIN;
      this.nodeMaker = new WaterPathNodeMaker(this.canJumpOutOfWater);
      this.nodeMaker.setCanEnterOpenDoors(false);
      return new PathNodeNavigator(this.nodeMaker, range);
   }

   protected boolean isAtValidPosition() {
      return this.canJumpOutOfWater || this.entity.isInFluid();
   }

   protected Vec3d getPos() {
      return new Vec3d(this.entity.getX(), this.entity.getBodyY(0.5), this.entity.getZ());
   }

   protected double adjustTargetY(Vec3d pos) {
      return pos.y;
   }

   protected boolean canPathDirectlyThrough(Vec3d origin, Vec3d target) {
      return doesNotCollide(this.entity, origin, target, false);
   }

   public boolean isValidPosition(BlockPos pos) {
      return !this.world.getBlockState(pos).isOpaqueFullCube();
   }

   public void setCanSwim(boolean canSwim) {
   }

   public boolean canControlOpeningDoors() {
      return false;
   }
}
