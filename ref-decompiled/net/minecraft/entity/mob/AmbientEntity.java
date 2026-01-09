package net.minecraft.entity.mob;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public abstract class AmbientEntity extends MobEntity {
   protected AmbientEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   public boolean canBeLeashed() {
      return false;
   }
}
