package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class GoToBedAndSleepGoal extends MoveToTargetPosGoal {
   private final CatEntity cat;

   public GoToBedAndSleepGoal(CatEntity cat, double speed, int range) {
      super(cat, speed, range, 6);
      this.cat = cat;
      this.lowestY = -2;
      this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
   }

   public boolean canStart() {
      return this.cat.isTamed() && !this.cat.isSitting() && !this.cat.isInSleepingPose() && super.canStart();
   }

   public void start() {
      super.start();
      this.cat.setInSittingPose(false);
   }

   protected int getInterval(PathAwareEntity mob) {
      return 40;
   }

   public void stop() {
      super.stop();
      this.cat.setInSleepingPose(false);
   }

   public void tick() {
      super.tick();
      this.cat.setInSittingPose(false);
      if (!this.hasReached()) {
         this.cat.setInSleepingPose(false);
      } else if (!this.cat.isInSleepingPose()) {
         this.cat.setInSleepingPose(true);
      }

   }

   protected boolean isTargetPos(WorldView world, BlockPos pos) {
      return world.isAir(pos.up()) && world.getBlockState(pos).isIn(BlockTags.BEDS);
   }
}
