package net.minecraft.entity.ai.goal;

import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameRules;

public class UniversalAngerGoal extends Goal {
   private static final int BOX_VERTICAL_EXPANSION = 10;
   private final MobEntity mob;
   private final boolean triggerOthers;
   private int lastAttackedTime;

   public UniversalAngerGoal(MobEntity mob, boolean triggerOthers) {
      this.mob = mob;
      this.triggerOthers = triggerOthers;
   }

   public boolean canStart() {
      return getServerWorld(this.mob).getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER) && this.canStartUniversalAnger();
   }

   private boolean canStartUniversalAnger() {
      return this.mob.getAttacker() != null && this.mob.getAttacker().getType() == EntityType.PLAYER && this.mob.getLastAttackedTime() > this.lastAttackedTime;
   }

   public void start() {
      this.lastAttackedTime = this.mob.getLastAttackedTime();
      ((Angerable)this.mob).universallyAnger();
      if (this.triggerOthers) {
         this.getOthersInRange().stream().filter((entity) -> {
            return entity != this.mob;
         }).map((entity) -> {
            return (Angerable)entity;
         }).forEach(Angerable::universallyAnger);
      }

      super.start();
   }

   private List getOthersInRange() {
      double d = this.mob.getAttributeValue(EntityAttributes.FOLLOW_RANGE);
      Box box = Box.from(this.mob.getPos()).expand(d, 10.0, d);
      return this.mob.getWorld().getEntitiesByClass(this.mob.getClass(), box, EntityPredicates.EXCEPT_SPECTATOR);
   }
}
