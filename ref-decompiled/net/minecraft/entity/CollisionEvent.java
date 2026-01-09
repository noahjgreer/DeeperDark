package net.minecraft.entity;

import java.util.function.Consumer;
import net.minecraft.block.AbstractFireBlock;

public enum CollisionEvent {
   FREEZE((entity) -> {
      entity.setInPowderSnow(true);
      if (entity.canFreeze()) {
         entity.setFrozenTicks(Math.min(entity.getMinFreezeDamageTicks(), entity.getFrozenTicks() + 1));
      }

   }),
   FIRE_IGNITE(AbstractFireBlock::igniteEntity),
   LAVA_IGNITE(Entity::igniteByLava),
   EXTINGUISH(Entity::extinguish);

   private final Consumer action;

   private CollisionEvent(final Consumer action) {
      this.action = action;
   }

   public Consumer getAction() {
      return this.action;
   }

   // $FF: synthetic method
   private static CollisionEvent[] method_67648() {
      return new CollisionEvent[]{FREEZE, FIRE_IGNITE, LAVA_IGNITE, EXTINGUISH};
   }
}
