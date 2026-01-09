package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;

public class BreakDoorGoal extends DoorInteractGoal {
   private static final int MIN_MAX_PROGRESS = 240;
   private final Predicate difficultySufficientPredicate;
   protected int breakProgress;
   protected int lastBreakProgress;
   protected int maxProgress;

   public BreakDoorGoal(MobEntity mob, Predicate difficultySufficientPredicate) {
      super(mob);
      this.lastBreakProgress = -1;
      this.maxProgress = -1;
      this.difficultySufficientPredicate = difficultySufficientPredicate;
   }

   public BreakDoorGoal(MobEntity mob, int maxProgress, Predicate difficultySufficientPredicate) {
      this(mob, difficultySufficientPredicate);
      this.maxProgress = maxProgress;
   }

   protected int getMaxProgress() {
      return Math.max(240, this.maxProgress);
   }

   public boolean canStart() {
      if (!super.canStart()) {
         return false;
      } else if (!getServerWorld(this.mob).getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
         return false;
      } else {
         return this.isDifficultySufficient(this.mob.getWorld().getDifficulty()) && !this.isDoorOpen();
      }
   }

   public void start() {
      super.start();
      this.breakProgress = 0;
   }

   public boolean shouldContinue() {
      return this.breakProgress <= this.getMaxProgress() && !this.isDoorOpen() && this.doorPos.isWithinDistance(this.mob.getPos(), 2.0) && this.isDifficultySufficient(this.mob.getWorld().getDifficulty());
   }

   public void stop() {
      super.stop();
      this.mob.getWorld().setBlockBreakingInfo(this.mob.getId(), this.doorPos, -1);
   }

   public void tick() {
      super.tick();
      if (this.mob.getRandom().nextInt(20) == 0) {
         this.mob.getWorld().syncWorldEvent(1019, this.doorPos, 0);
         if (!this.mob.handSwinging) {
            this.mob.swingHand(this.mob.getActiveHand());
         }
      }

      ++this.breakProgress;
      int i = (int)((float)this.breakProgress / (float)this.getMaxProgress() * 10.0F);
      if (i != this.lastBreakProgress) {
         this.mob.getWorld().setBlockBreakingInfo(this.mob.getId(), this.doorPos, i);
         this.lastBreakProgress = i;
      }

      if (this.breakProgress == this.getMaxProgress() && this.isDifficultySufficient(this.mob.getWorld().getDifficulty())) {
         this.mob.getWorld().removeBlock(this.doorPos, false);
         this.mob.getWorld().syncWorldEvent(1021, this.doorPos, 0);
         this.mob.getWorld().syncWorldEvent(2001, this.doorPos, Block.getRawIdFromState(this.mob.getWorld().getBlockState(this.doorPos)));
      }

   }

   private boolean isDifficultySufficient(Difficulty difficulty) {
      return this.difficultySufficientPredicate.test(difficulty);
   }
}
